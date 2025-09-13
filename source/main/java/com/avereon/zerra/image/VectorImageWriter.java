package com.avereon.zerra.image;

import com.avereon.util.FileUtil;
import com.avereon.util.TextUtil;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.javafx.Fx;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import net.sf.image4j.codec.ico.ICOEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VectorImageWriter {

	private BufferedImage image;

	/**
	 * @see #save(VectorImage, Path, double, double, Color)
	 */
	public void save( VectorImage renderer, Path path ) throws Exception {
		save( List.of( renderer ), path );
	}

	/**
	 * @see #save(VectorImage, Path, double, double, Color)
	 */
	public void save( VectorImage renderer, Path path, double width, double height ) throws Exception {
		save( renderer, path, width, height, null );
	}

	/**
	 * Save a rendered image using the specified width and height. This is useful
	 * for creating an image that does not use the renderer's width and height.
	 * The renderer can also be repositioned using the relocate() method.
	 *
	 * @param renderer The image renderer
	 * @param path The path of the saved image
	 * @param width The image width
	 * @param height The image height
	 * @param fill The background fill color
	 * @throws Exception If an error occurs
	 */
	public void save( VectorImage renderer, Path path, double width, double height, Color fill ) throws Exception {
		BufferedImage image = createAwtImage( renderer, width, height, fill );
		saveAwtImages( image == null ? List.of() : List.of( image ), path );
	}

	/**
	 * @see #save(List, Path, Color)
	 */
	public void save( List<VectorImage> renderers, Path path ) throws Exception {
		save( renderers, path, null );
	}

	/**
	 * Save a list of images as an icon. If the icon format supports multiple
	 * images, then all images are used. If the icon format does not support
	 * multiple images, then only the first image is used.
	 *
	 * @param renderers The list of image renderers
	 * @param path The path of the saved image
	 * @param fill The background fill color
	 * @throws Exception If an error occurs
	 */
	public void save( List<VectorImage> renderers, Path path, Color fill ) throws Exception {
		List<BufferedImage> images = new ArrayList<>();
		for( VectorImage renderer : renderers ) {
			images.add( createAwtImage( renderer, renderer.getWidth(), renderer.getHeight(), fill ) );
		}
		saveAwtImages( images, path );
	}

	private void saveAwtImages( List<BufferedImage> images, Path path ) throws Exception {
		Path parent = path.getParent();
		if( !Files.exists( parent ) ) Files.createDirectories( parent );
		File absoluteFile = path.toFile().getAbsoluteFile();
		String type = FileUtil.getExtension( path );
		if( TextUtil.isEmpty( type ) ) type = "png";
		type = type.toLowerCase();

		if( "ico".equals( type ) ) {
			ICOEncoder.write( images, absoluteFile );
		} else {
			boolean result = ImageIO.write( images.getFirst(), type, absoluteFile );
			if( !result ) throw new IllegalArgumentException( "Image writer not available for " + type );
		}
	}

	private BufferedImage createAwtImage( VectorImage renderer, double width, double height, Color fill ) throws Exception {
		String style = "";
		if( fill != null ) style += "-fx-background-color: " + Colors.toString( fill ) + ";";
		renderer.getProperties().put( "container-style", style );

		Runnable createImage = () -> doCreateAwtImageFx( renderer, width, height, fill );
		try {
			Fx.run( createImage );
		} catch( IllegalStateException exception ) {
			Platform.startup( createImage );
		}
		Fx.waitForWithExceptions( 5000 );
		if( this.image == null ) throw new NullPointerException( "Image not created" );
		return this.image;
	}

	private void doCreateAwtImageFx( VectorImage renderer, double width, double height, Color fill ) {
		int type = BufferedImage.TYPE_INT_ARGB;
		if( fill != null && fill.isOpaque() ) type = BufferedImage.TYPE_INT_RGB;

		BufferedImage buffer = new BufferedImage( (int)width, (int)height, type );
		this.image = SwingFXUtils.fromFXImage( renderer.getImage( width, height ), buffer );
	}

}
