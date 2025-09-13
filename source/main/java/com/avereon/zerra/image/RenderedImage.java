package com.avereon.zerra.image;

import com.avereon.zerra.font.FontUtil;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.*;

import java.util.List;

public abstract class RenderedImage extends VectorImage {

	protected RenderedImage() {}

	protected RenderedImage( double gridX, double gridY ) {
		super( gridX, gridY );
	}

	protected abstract void render();

	protected void move( double x, double y ) {
		getGraphicsContext2D().translate( x, y );
	}

	protected void zoom( double x, double y ) {
		getGraphicsContext2D().scale( x, y );
	}

	protected void spin( double x, double y, double a ) {
		move( x, y );
		getGraphicsContext2D().rotate( a );
		move( -x, -y );
	}

	protected void clip() {
		getGraphicsContext2D().clip();
	}

	protected void save() {
		getGraphicsContext2D().save();
	}

	protected void restore() {
		getGraphicsContext2D().restore();
	}

	protected void startPath() {
		getGraphicsContext2D().beginPath();
	}

	protected void startPath( double x, double y ) {
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( x, y );
	}

	protected void moveTo( double x, double y ) {
		getGraphicsContext2D().moveTo( x, y );
	}

	protected void lineTo( double x, double y ) {
		getGraphicsContext2D().lineTo( x, y );
	}

	protected void addArc( double cx, double cy, double rx, double ry, double start, double extent ) {
		getGraphicsContext2D().arc( cx, cy, rx, ry, start, extent );
	}

	protected void addOval( double cx, double cy, double rx, double ry ) {
		getGraphicsContext2D().moveTo( cx + rx, cy );
		getGraphicsContext2D().arc( cx, cy, rx, ry, 0, 360 );
	}

	protected void addDot( double cx, double cy ) {
		addOval( cx, cy, g( 1 ), g( 1 ) );
	}

	protected void addLine( double x1, double y1, double x2, double y2 ) {
		moveTo( x1, y1 );
		lineTo( x2, y2 );
	}

	protected void addRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().rect( x, y, w, h );
	}

	@SuppressWarnings( "unused" )
	protected void curveTo( double xc, double yc, double x1, double y1 ) {
		getGraphicsContext2D().quadraticCurveTo( xc, yc, x1, y1 );
	}

	protected void curveTo( double xc1, double yc1, double xc2, double yc2, double x1, double y1 ) {
		getGraphicsContext2D().bezierCurveTo( xc1, yc1, xc2, yc2, x1, y1 );
	}

	protected void closePath() {
		getGraphicsContext2D().closePath();
	}

	@SuppressWarnings( "unused" )
	protected void fillText( String text, double x, double y, double textSize ) {
		fillText( text, x, y, textSize, -1 );
	}

	protected void fillText( String text, double x, double y, double textSize, double maxWidth ) {
		renderText( text, x, y, textSize, maxWidth, false );
	}

	@SuppressWarnings( "unused" )
	protected void drawText( String text, double x, double y, double textSize ) {
		drawText( text, x, y, textSize, -1 );
	}

	@SuppressWarnings( "SameParameterValue" )
	protected void drawText( String text, double x, double y, double textSize, double maxWidth ) {
		renderText( text, x, y, textSize, maxWidth, true );
	}

	@SuppressWarnings( "unused" )
	protected Paint linearPaint( double x1, double y1, double x2, double y2, Stop... stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	@SuppressWarnings( "unused" )
	protected Paint linearPaint( double x1, double y1, double x2, double y2, List<Stop> stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	@SuppressWarnings( "unused" )
	protected Paint radialPaint( double x, double y, double r, Stop... stops ) {
		return new RadialGradient( 0, 0, x, y, r, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint radialPaint( double x, double y, double r, List<Stop> stops ) {
		return new RadialGradient( 0, 0, x, y, r, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Font deriveFont( Font font, double size ) {
		if( font == null ) font = getGraphicsContext2D().getFont();
		FontWeight fontWeight = FontUtil.getFontWeight( font.getStyle() );
		FontPosture fontPosture = FontUtil.getFontPosture( font.getStyle() );
		return Font.font( font.getFamily(), fontWeight, fontPosture, size );
	}

	protected void setDrawCap( StrokeLineCap cap ) {
		getGraphicsContext2D().setLineCap( cap );
	}

	@SuppressWarnings( "unused" )
	protected void setDrawJoin( StrokeLineJoin join ) {
		getGraphicsContext2D().setLineJoin( join );
	}

	protected void setDrawWidth( double width ) {
		getGraphicsContext2D().setLineWidth( width );
	}

	protected void setDrawPaint( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
	}

	protected void setFillPaint( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
	}

	@SuppressWarnings( "SameParameterValue" )
	protected void setTextAlign( TextAlignment alignment ) {
		getGraphicsContext2D().setTextAlign( alignment );
	}

	@SuppressWarnings( "unused" )
	protected void setTextBaseLine( VPos baseline ) {
		getGraphicsContext2D().setTextBaseline( baseline );
	}

	protected void drawImage( Image image ) {
		drawImage( image, 0, 0 );
	}

	/**
	 * Draw an image.
	 * <p>
	 * Note that x, y are relative to the grid. The width and height are set to
	 * the grid width and height.
	 * </p>
	 *
	 * @param image The image to render
	 * @param x The position x
	 * @param y The position x
	 */
	@SuppressWarnings( "SameParameterValue" )
	protected void drawImage( Image image, double x, double y ) {
		drawImage( image, x, y, getGridX(), getGridY() );
	}

	/**
	 * Draw an image.
	 * <p>
	 * Note that x, y, w and h are relative to the grid.
	 * </p>
	 *
	 * @param image The image to render
	 * @param x The position x
	 * @param y The position x
	 * @param w The width
	 * @param h The height
	 */
	protected void drawImage( Image image, double x, double y, double w, double h ) {
		getGraphicsContext2D().drawImage( image, x, y, w, h );
	}

	@SuppressWarnings( "unused" )
	protected void clearRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().clearRect( x, y, w, h );
	}

	protected void draw() {
		getGraphicsContext2D().stroke();
	}

	protected void draw( Paint paint ) {
		getGraphicsContext2D().save();
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().stroke();
		getGraphicsContext2D().restore();
	}

	protected void fill() {
		getGraphicsContext2D().fill();
	}

	protected void fill( Paint paint ) {
		getGraphicsContext2D().save();
		getGraphicsContext2D().setFill( paint );
		getGraphicsContext2D().fill();
		getGraphicsContext2D().restore();
	}

	protected void render( VectorImage image ) {
		image.setStrokePaint( getStrokePaint() );
		image.setPrimaryPaint( getPrimaryPaint() );
		image.setSecondaryPaint( getSecondaryPaint() );
		image.setGraphicsContext2D( getGraphicsContext2D() );
		image.doRender();
	}

	protected void doRender() {
		super.doRender();
		render();
	}

	protected double g( double value ) {
		return value;
	}

	private void renderText( String text, double x, double y, double textSize, double maxWidth, boolean draw ) {
		// Font sizes smaller than one don't scale as expected,
		// so the workaround is to scale according to the text
		// size and divide the coordinates by the text size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale the transform
		getGraphicsContext2D().save();
		getGraphicsContext2D().scale( scale, scale );

		getGraphicsContext2D().setFont( deriveFont( getFont(), fontSize ) );
		getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.GRAY );

		// Stroke or fill the text
		if( draw ) {
			if( maxWidth < 0 ) {
				getGraphicsContext2D().strokeText( text, x / scale, y / scale );
			} else {
				getGraphicsContext2D().strokeText( text, x / scale, y / scale, maxWidth / scale );
			}
		} else {
			if( maxWidth < 0 ) {
				getGraphicsContext2D().fillText( text, x / scale, y / scale );
			} else {
				getGraphicsContext2D().fillText( text, x / scale, y / scale, maxWidth / scale );
			}
		}

		getGraphicsContext2D().restore();
	}

}
