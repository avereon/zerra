package com.avereon.zerra.image;

import com.avereon.zerra.color.Paints;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SvgIcon extends VectorIcon {

	public static final double RADIANS_PER_DEGREE = Math.PI / 180.0;

	/**
	 * A Paint object to denote the use of the primary color. The actual color is of no interest.
	 */
	protected static final Paint PRIMARY = Paint.valueOf( "#000000" );

	/**
	 * A Paint object to denote the use of the secondary color. The actual color is of no interest.
	 */
	protected static final Paint SECONDARY = Paint.valueOf( "#000000" );

	private final List<IconAction> actions;

	public SvgIcon() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	protected SvgIcon( double gridX, double gridY ) {
		this( gridX, gridY, null );
	}

	protected SvgIcon( double gridX, double gridY, String svgPath ) {
		super( gridX, gridY );
		actions = new CopyOnWriteArrayList<>();
		reset();
		fill( svgPath );
	}

	/**
	 * Create an SVG string for an arc. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of an arc based
	 * on the center start and extent.
	 */
	public static String arc( double cx, double cy, double r, double start, double extent ) {
		return arc( false, cx, cy, r, start, extent );
	}

	/**
	 * Create an SVG string for an arc. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of an arc based
	 * on the center start and extent.
	 */
	public static String arc( boolean moveToStart, double cx, double cy, double r, double start, double extent ) {
		int ccw = extent < 0 ? 0 : 1;
		double end = start + extent;
		double sx = cx + r * Math.cos( start * RADIANS_PER_DEGREE );
		double sy = cy + r * Math.sin( start * RADIANS_PER_DEGREE );
		double ex = cx + r * Math.cos( end * RADIANS_PER_DEGREE );
		double ey = cy + r * Math.sin( end * RADIANS_PER_DEGREE );

		String icon = "";
		if( moveToStart ) {
			icon += "M" + sx + "," + sy + " ";
		}
		icon += "A" + r + "," + r + " 0 0 " + ccw + " " + ex + "," + ey;

		return icon;
	}

	/**
	 * Create an SVG string for a circle. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of a full circle.
	 *
	 * @param cx The circle center X coordinate
	 * @param cy The circle center Y coordinate
	 * @param r The circle radius
	 * @return The SVG string for the circle
	 */
	public static String circle( double cx, double cy, double r ) {
		return "M" + (cx + r) + "," + cy + " A" + r + "," + r + " 0 0 0 " + (cx - r) + "," + cy + " A" + r + "," + r + " 0 0 0 " + (cx + r) + "," + cy;
	}

	/**
	 * Create an SVG string for an ellipse. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of a full ellipse.
	 *
	 * @param cx The ellipse center X coordinate
	 * @param cy The ellipse center Y coordinate
	 * @param rx The ellipse X radius
	 * @param ry The ellipse Y radius
	 * @return The SVG string for the ellipse
	 */
	public static String ellipse( double cx, double cy, double rx, double ry ) {
		return ellipse( cx, cy, rx, ry, 0 );
	}

	/**
	 * Create an SVG string for an ellipse. Because SVG path arcs are a bit
	 * cumbersome, this method simplifies the creating of a full ellipse.
	 *
	 * @param cx The ellipse center X coordinate
	 * @param cy The ellipse center Y coordinate
	 * @param rx The ellipse X radius
	 * @param ry The ellipse Y radius
	 * @return The SVG string for the ellipse
	 */
	public static String ellipse( double cx, double cy, double rx, double ry, double rotate ) {
		return "M" + (cx + rx) + "," + (cy + ry) + " A" + rx + "," + ry + " " + rotate + " 0 0 " + (cx - rx) + "," + (cy - ry) + " A" + rx + "," + ry + " " + rotate + " 0 0 " + (cx + rx) + "," + (cy + ry);
	}

	public static void main( String[] commands ) {
		Proof.proof( new SvgIcon( 24, 24, "M20.5 6c-2.61.7-5.67 1-8.5 1s-5.89-.3-8.5-1L3 8c1.86.5 4 .83 6 1v13h2v-6h2v6h2V9c2-.17 4.14-.5 6-1l-.5-2zM12 6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z" ) );
	}

	/**
	 * This method is used to define the rendering steps where it is problematic
	 * to define them in the constructor. This is particularly necessary when
	 * using values that come from the style class like colors and sizes.
	 */
	protected void define() {
	}

	public void restore() {
		actions.add( new Restore() );
	}

	public SvgIcon clip( String path ) {
		actions.add( new Clip( path, true ) );
		return this;
	}

	public SvgIcon transform( Transform transform ) {
		actions.add( new Xform( transform ) );
		return this;
	}

	/**
	 * Add a filled SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path ) {
		return fill( path, null, null );
	}

	public SvgIcon fill( String path, Paint paint ) {
		return fill( path, paint, null );
	}

	public SvgIcon fill( String path, FillRule rule ) {
		return fill( path, null, rule );
	}

	public SvgIcon fill( String path, Paint paint, FillRule rule ) {
		return fill( path, null, paint, rule );
	}

	public SvgIcon fill( String path, Font font, Paint paint, FillRule rule ) {
		if( path != null ) {
			actions.add( new Fill( path, true, font, paint, rule ) );
		}
		return this;
	}

	/**
	 * Add a filled text entry to the icon.
	 *
	 * @param path The text to fill
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon fillText( String path ) {
		return fillText( path, null, null );
	}

	@SuppressWarnings( "unused" )
	public SvgIcon fillText( String path, Paint paint ) {
		return fillText( path, paint, null );
	}

	@SuppressWarnings( "unused" )
	public SvgIcon fillText( String path, FillRule rule ) {
		return fillText( path, null, rule );
	}

	public SvgIcon fillText( String path, Paint paint, FillRule rule ) {
		return fillText( path, null, paint, rule );
	}

	public SvgIcon fillText( String path, Font font, Paint paint, FillRule rule ) {
		if( path != null ) {
			actions.add( new Fill( path, false, font, paint, rule ) );
		}
		return this;
	}

	/**
	 * Add a filled text entry to the icon.
	 *
	 * @param path The text to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon text( String path ) {
		return text( path, null, null );
	}

	public SvgIcon text( String path, Paint paint ) {
		return text( path, paint, null );
	}

	public SvgIcon text( String path, FillRule rule ) {
		return text( path, null, rule );
	}

	public SvgIcon text( String path, Paint paint, FillRule rule ) {
		return fillText( path, paint, rule );
	}

	/**
	 * Add a stroked SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path ) {
		return draw( path, null );
	}

	public SvgIcon draw( String path, Paint paint ) {
		return draw( path, paint, DEFAULT_STROKE_WIDTH );
	}

	public SvgIcon draw( String path, double width ) {
		return draw( path, null, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon draw( String path, Paint paint, double width ) {
		return draw( path, paint, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon draw( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join ) {
		return draw( path, paint, width, cap, join, 0 );
	}

	public SvgIcon draw(
		String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes
	) {
		return draw( path, null, paint, width, cap, join, dashOffset, dashes );
	}

	public SvgIcon draw(
		String path, Font font, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes
	) {
		if( path != null ) {
			actions.add( new Draw( path, true, font, paint, width, cap, join, dashOffset, dashes ) );
		}
		return this;
	}

	/**
	 * Add a stroked SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	@SuppressWarnings( "unused" )
	public SvgIcon drawText( String path ) {
		return drawText( path, null );
	}

	public SvgIcon drawText( String path, Paint paint ) {
		return drawText( path, paint, DEFAULT_STROKE_WIDTH );
	}

	@SuppressWarnings( "unused" )
	public SvgIcon drawText( String path, double width ) {
		return drawText( path, null, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon drawText( String path, Paint paint, double width ) {
		return drawText( path, paint, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	@SuppressWarnings( "unused" )
	public SvgIcon drawText( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join ) {
		return drawText( path, paint, width, cap, join, 0 );
	}

	public SvgIcon drawText(
		String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes
	) {
		return drawText( path, null, paint, width, cap, join, dashOffset, dashes );
	}

	public SvgIcon drawText(
		String path, Font font, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes
	) {
		if( path != null ) {
			actions.add( new Draw( path, false, font, paint, width, cap, join, dashOffset, dashes ) );
		}
		return this;
	}

	public SvgIcon draw( SvgIcon icon ) {
		if( icon != null ) {
			icon.doRender();
			actions.addAll( icon.actions );
			actions.add( new Restore() );
		}
		return this;
	}

	/**
	 * Create an SVG string that represents the given point rotated about cx,cy
	 * by the angle.
	 *
	 * @param x The X coordinate of the point
	 * @param y The Y coordinate of the point
	 * @param cx The X coordinate of the rotation center
	 * @param cy The Y coordinate of the rotation center
	 * @param angle The angle in degrees to rotate counter-clockwise
	 * @return The SVG string of the rotated point
	 */
	protected String rotate( double x, double y, double cx, double cy, double angle ) {
		return rx( cx, cy, x, y, angle ) + "," + ry( cx, cy, x, y, angle );
	}

	private double rx( double cx, double cy, double x, double y, double angle ) {
		return cx + ((x - cx) * Math.cos( Math.toRadians( angle ) )) + ((y - cy) * Math.sin( Math.toRadians( angle ) ));
	}

	private double ry( double cx, double cy, double x, double y, double angle ) {
		return cy + ((y - cy) * Math.cos( Math.toRadians( angle ) )) - ((x - cx) * Math.sin( Math.toRadians( angle ) ));
	}

	@Override
	public <T extends VectorImage> T copy() {
		T copy = super.copy();
		((SvgIcon)copy).actions.addAll( this.actions );
		return copy;
	}

	/**
	 * This method should not be overridden. Drawing definition should be done
	 * in the constructor.
	 */
	@Override
	protected final void doRender() {
		define();
		super.doRender();
		getGraphicsContext2D().save();
		actions.forEach( a -> a.render( this ) );
	}

	public String toSvg() {
		StringBuilder svg = new StringBuilder();
		svg.append( "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" ).append( getGridX() ).append( "\" height=\"" ).append( getGridY() ).append( "\">" );
		for( IconAction action : actions ) {
			// TODO Handle transforms
			//getTransforms();

			if( action instanceof Fill fill ) {
				svg.append( "<path fill=\"" ).append( Paints.toString( fill.calcPaint( this ) ) ).append( "\" d=\"" ).append( fill.getPath() ).append( "\"/>" );
			} else if( action instanceof Draw draw ) {
				svg
					.append( "<path stroke=\"" )
					.append( Paints.toString( draw.calcPaint( this ) ) )
					.append( "\" stroke-width=\"" )
					.append( draw.width )
					.append( "\" d=\"" )
					.append( draw.getPath() )
					.append( "\"/>" );
			}
		}
		svg.append( "</svg>" );

		return svg.toString();
	}

	private void doRestore() {
		getGraphicsContext2D().restore();
	}

	private interface IconAction {

		void render( SvgIcon icon );

	}

	private static abstract class RenderAction implements IconAction {

		private final String path;

		private final boolean isSvg;

		private final Paint paint;

		protected RenderAction( String path, boolean isSvg, Paint paint ) {
			this.path = path;
			this.isSvg = isSvg;
			this.paint = paint;
		}

		protected String getPath() {
			return path;
		}

		protected boolean isSvg() {
			return isSvg;
		}

		protected GraphicsContext setup( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.save();
			return context;
		}

		@SuppressWarnings( "UnusedReturnValue" )
		protected GraphicsContext teardown( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.restore();
			return context;
		}

		protected Paint calcPaint( SvgIcon icon ) {
			if( paint == null ) {
				return icon.getStrokePaint();
			} else if( paint == PRIMARY ) {
				return icon.getPrimaryPaint();
			} else if( paint == SECONDARY ) {
				return icon.getSecondaryPaint();
			} else {
				return paint;
			}
		}

	}

	private static class Fill extends RenderAction {

		private final Font font;

		private final FillRule rule;

		public Fill( String path, boolean isSvg, Font font, Paint paint, FillRule rule ) {
			super( path, isSvg, paint );
			this.font = font;
			this.rule = rule;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			context.setFont( font );
			context.setFill( calcPaint( icon ) );
			context.setFillRule( rule );

			if( isSvg() ) {
				context.beginPath();
				context.appendSVGPath( getPath() );
				context.fill();
			} else {
				context.fillText( getPath(), 0, 0 );
				context.restore();
			}

			teardown( icon );
		}

	}

	private static class Draw extends RenderAction {

		private final Font font;

		private final double width;

		private final StrokeLineCap cap;

		private final StrokeLineJoin join;

		private final double dashOffset;

		private final double[] dashes;

		public Draw(
			String path, boolean isSvg, Font font, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes
		) {
			super( path, isSvg, paint );
			this.font = font;
			this.width = width;
			this.cap = cap;
			this.join = join;
			this.dashOffset = dashOffset;
			this.dashes = dashes;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			context.setFont( font );
			context.setStroke( calcPaint( icon ) );
			context.setLineWidth( width );
			context.setLineCap( cap );
			context.setLineJoin( join );
			context.setLineDashOffset( dashOffset );
			context.setLineDashes( dashes );

			if( isSvg() ) {
				context.beginPath();
				context.appendSVGPath( getPath() );
				context.stroke();
			} else {
				context.strokeText( getPath(), 0, 0 );
				context.restore();
			}

			teardown( icon );
		}

	}

	private static class Clip extends RenderAction {

		public Clip( String path, boolean isSvg ) {
			super( path, isSvg, null );
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();

			// It is important to store the context state but not restore it
			context.save();

			context.beginPath();
			context.appendSVGPath( getPath() );
			context.clip();
		}

	}

	private static class Xform implements IconAction {

		private final Transform transform;

		public Xform( Transform transform ) {
			this.transform = transform;
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();

			// It is important to store the context state but not restore it
			context.save();

			context.transform( new Affine( transform ) );
		}

	}

	private static class Restore implements IconAction {

		@Override
		public void render( SvgIcon icon ) {
			icon.doRestore();
		}

	}

}
