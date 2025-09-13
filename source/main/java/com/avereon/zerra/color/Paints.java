package com.avereon.zerra.color;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

public class Paints {

	private Paints() {}

	public static String toString( Paint paint ) {
		return switch( paint ) {
			case null -> null;
			case Color color -> toString( color );
			case LinearGradient gradient -> toString( gradient );
			case RadialGradient gradient -> toString( gradient );
			default -> throw new IllegalArgumentException( "Unknown paint type: " + paint.getClass().getName() );
		};
	}

	public static String toString( Color color ) {
		if( color == null ) return null;
		return Colors.toString( color );
	}

	public static String toString( LinearGradient gradient ) {
		if( gradient == null ) return null;
		return gradient.toString();
	}

	public static String toString( RadialGradient gradient ) {
		if( gradient == null ) return null;
		return gradient.toString();
	}

	public static Paint parse( String string ) {
		if( string == null ) return null;

		if( string.startsWith( "#" ) ) return Colors.parse( string );
		if( string.startsWith( "linear-gradient" ) ) return LinearGradient.valueOf( string );
		if( string.startsWith( "radial-gradient" ) ) return RadialGradient.valueOf( string );

		return Colors.parse( string );
	}

	public static Paint parseWithNullOnException( String string ) {
		try {
			return parse( string );
		} catch( IllegalArgumentException exception ) {
			return null;
		}
	}

	@SuppressWarnings( "unused" )
	public static boolean isTransparent( Paint paint ) {
		if( paint == null ) return true;
		if( paint instanceof Color color ) return Colors.isTransparent( color );
		return false;
	}

}
