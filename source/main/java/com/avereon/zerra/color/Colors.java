package com.avereon.zerra.color;

import javafx.scene.paint.Color;

public class Colors {

	/*
	 * Per http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
	 * public static final double RED_BRIGHTNESS_FACTOR = 0.2126;
	 * public static final double GREEN_BRIGHTNESS_FACTOR = 0.7152;
	 * public static final double BLUE_BRIGHTNESS_FACTOR = 0.0722;
	 */

	// Per com.sun.javafx.util.Utils.calculateBrightness(Color)
	public static final double RED_BRIGHTNESS_FACTOR = 0.3;

	public static final double GREEN_BRIGHTNESS_FACTOR = 0.59;

	public static final double BLUE_BRIGHTNESS_FACTOR = 0.11;

	public static Color parse( String string ) {
		if( string == null ) return null;
		return Color.web( string );
	}

	public static String toString( Color color ) {
		if( color == null ) return null;
		int r = (int)Math.round( color.getRed() * 255.0 );
		int g = (int)Math.round( color.getGreen() * 255.0 );
		int b = (int)Math.round( color.getBlue() * 255.0 );
		int o = (int)Math.round( color.getOpacity() * 255.0 );
		return String.format( "#%02x%02x%02x%02x", r, g, b, o );
	}

	public static Color opaque( Color color ) {
		return new Color( color.getRed(), color.getGreen(), color.getBlue(), 1.0 );
	}

	public static Color translucent( Color color, double factor ) {
		return new Color( color.getRed(), color.getGreen(), color.getBlue(), factor );
	}

	public static Color mix( Color color, Color mixer, double factor ) {
		if( color == null || mixer == null ) return null;

		if( Color.TRANSPARENT.equals( color ) ) return translucent( mixer, factor );
		if( Color.TRANSPARENT.equals( mixer ) ) return translucent( color, factor );

		factor = clamp( factor );
		double mixFactor = 1 - factor;

		double colorR = color.getRed() * mixFactor;
		double colorG = color.getGreen() * mixFactor;
		double colorB = color.getBlue() * mixFactor;
		double colorA = color.getOpacity() * mixFactor;

		double mixerR = mixer.getRed() * factor;
		double mixerG = mixer.getGreen() * factor;
		double mixerB = mixer.getBlue() * factor;
		double mixerA = mixer.getOpacity() * factor;

		double r = colorR + mixerR;
		double g = colorG + mixerG;
		double b = colorB + mixerB;
		double a = colorA + mixerA;

		return new Color( r, g, b, a );
	}

	/**
	 * Determine if the specified color is transparent.
	 *
	 * @param color The color to check for transparency.
	 * @return True if the color is transparent, false otherwise.
	 */
	public static boolean isTransparent( Color color ) {
		return color != null && color.getOpacity() == 0;
	}

	/**
	 * Generate a toned color by mixing the specified color with white (factor
	 * 1.0) or black (factor -1.0).
	 */
	public static Color getTone( Color color, double factor ) {
		//factor = clamp( factor );
		if( factor > 0 ) return getTint( color, factor );
		if( factor < 0 ) return getShade( color, -factor );
		return color;
	}

	/**
	 * Generate a tinted color by mixing the specified color with white.
	 *
	 * @param color The color to tint
	 * @param factor The tint factor
	 * @return The tinted color
	 */
	public static Color getTint( Color color, double factor ) {
		factor = clamp( factor );
		return mix( color, new Color( 1, 1, 1, color.getOpacity() ), factor );
	}

	/**
	 * Generate a shaded color by mixing the specified color with black.
	 *
	 * @param color The color to shade
	 * @param factor The shade factor
	 * @return The shaded color
	 */
	public static Color getShade( Color color, double factor ) {
		factor = clamp( factor );
		return mix( color, new Color( 0, 0, 0, color.getOpacity() ), factor );
	}

	/**
	 * Get the intensity of the specified color.
	 *
	 * @param color The color for which to find the luminance.
	 * @return A number between 0.0 and 1.0 representing the luminance.
	 */
	public static double getLuminance( Color color ) {
		if( color.getRed() + color.getGreen() + color.getBlue() == 3.0 ) return 1.0;
		return RED_BRIGHTNESS_FACTOR * color.getRed() + GREEN_BRIGHTNESS_FACTOR * color.getGreen() + BLUE_BRIGHTNESS_FACTOR * color.getBlue();
	}

	/**
	 * Invert the luminance of a color while attempting to keep the hue.
	 * Sometimes, due to color channel overflow when inverting luminance, the hue
	 * cannot be well maintained. Therefore, inverting the luminance of a
	 * luminance inverted color does not always result in the original color. But
	 * in most cases luminance inverts and reverts well.
	 *
	 * @param color The color to invert luminance
	 * @return A color of the same, or similar, hue with inverted luminance
	 */
	public static Color invertLuminance( Color color ) {
		double sourceLuminance = Colors.getLuminance( color );
		double targetLuminance = Colors.getLuminance( color.invert() );

		//System.out.println( "sourceLuminance=" + sourceLuminance + " targetLuminance=" + targetLuminance );

		if( sourceLuminance == 0 ) return Color.color( 1, 1, 1, color.getOpacity() );
		if( sourceLuminance == 1 ) return Color.color( 0, 0, 0, color.getOpacity() );

		double inverseFactor = targetLuminance / sourceLuminance;
		double r = color.getRed() * inverseFactor;
		double g = color.getGreen() * inverseFactor;
		double b = color.getBlue() * inverseFactor;

		boolean redPegged = r > 1.0;
		boolean greenPegged = g > 1.0;
		boolean bluePegged = b > 1.0;
		boolean rollover = redPegged || greenPegged || bluePegged;

		int peggedCount = 0;
		if( redPegged ) peggedCount++;
		if( greenPegged ) peggedCount++;
		if( bluePegged ) peggedCount++;

		if( rollover ) {
			// Remove luminance already accounted for by existing colors
			double remainingTargetLuminance = targetLuminance;
			remainingTargetLuminance -= clamp( r ) * RED_BRIGHTNESS_FACTOR;
			remainingTargetLuminance -= clamp( g ) * GREEN_BRIGHTNESS_FACTOR;
			remainingTargetLuminance -= clamp( b ) * BLUE_BRIGHTNESS_FACTOR;

			double combinedAvailableFactors = 0;
			if( !redPegged ) combinedAvailableFactors += RED_BRIGHTNESS_FACTOR;
			if( !greenPegged ) combinedAvailableFactors += GREEN_BRIGHTNESS_FACTOR;
			if( !bluePegged ) combinedAvailableFactors += BLUE_BRIGHTNESS_FACTOR;

			double splitRatio = 1.0;
			if( redPegged && b > 0 ) splitRatio = g / b;
			if( greenPegged && r > 0 ) splitRatio = b / r;
			if( bluePegged && g > 0 ) splitRatio = r / g;

			if( peggedCount == 2 || splitRatio == 1.0 ) {
				double n = remainingTargetLuminance / combinedAvailableFactors;
				r = clamp( redPegged ? 1.0 : n );
				g = clamp( greenPegged ? 1.0 : n );
				b = clamp( bluePegged ? 1.0 : n );
			} else {
				if( redPegged ) {
					double termA = remainingTargetLuminance / (GREEN_BRIGHTNESS_FACTOR + BLUE_BRIGHTNESS_FACTOR / splitRatio);
					double termB = termA / splitRatio;
					r = 1;
					g = clamp( g + termA );
					b = clamp( b + termB );
				} else if( greenPegged ) {
					double termA = remainingTargetLuminance / (BLUE_BRIGHTNESS_FACTOR + RED_BRIGHTNESS_FACTOR / splitRatio);
					double termB = termA / splitRatio;
					r = clamp( r + termB );
					g = 1;
					b = clamp( b + termA );
				} else {
					double termA = remainingTargetLuminance / (RED_BRIGHTNESS_FACTOR + GREEN_BRIGHTNESS_FACTOR / splitRatio);
					double termB = termA / splitRatio;
					r = clamp( r + termA );
					g = clamp( g + termB );
					b = 1;
				}
			}
		}

		//System.out.println( "color=" + color + " r=" + r + " g=" + g + " b=" + b );
		return Color.color( r, g, b, color.getOpacity() );
	}

	public static java.awt.Color asAwtColor( javafx.scene.paint.Color color ) {
		return new java.awt.Color( (float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getOpacity() );
	}

	public static javafx.scene.paint.Color asFxColor( java.awt.Color color ) {
		return javafx.scene.paint.Color.rgb( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255.0 );
	}

	private static double clamp( double value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 1 ) {
			value = 1;
		}
		return value;
	}

	@SuppressWarnings( "unused" )
	private static float clamp( float value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 1 ) {
			value = 1;
		}
		return value;
	}

	@SuppressWarnings( "unused" )
	private static int clamp( int value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 255 ) {
			value = 255;
		}
		return value;
	}

}

