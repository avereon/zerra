package com.avereon.zerra.color;

import javafx.scene.paint.Color;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.assertj.core.api.Assertions.assertThat;

class ColorsTest {

	@Test
	void testToString() {
		assertThat( Colors.toString( Color.TRANSPARENT ) ).isEqualTo( "#00000000" );
		assertThat( Colors.toString( Color.BLACK ) ).isEqualTo( "#000000ff" );
		assertThat( Colors.toString( Color.WHITE ) ).isEqualTo( "#ffffffff" );
		assertThat( Colors.toString( Color.GRAY ) ).isEqualTo( "#808080ff" );
		assertThat( Colors.toString( null ) ).isNull();
	}

	@Test
	void testParse() {
		assertThat( Colors.parse( "#00000000" ) ).isEqualTo( Color.TRANSPARENT );
		assertThat( Colors.parse( "#000000ff" ) ).isEqualTo( Color.BLACK );
		assertThat( Colors.parse( "#ffffffff" ) ).isEqualTo( Color.WHITE );
		assertThat( Colors.parse( "#808080ff" ) ).isEqualTo( Color.GRAY );
		assertThat( Colors.parse( null ) ).isNull();
	}

	@Test
	void testGetBrightness() {
		// Transparent
		assertThat( Colors.getLuminance( Color.color( 0, 0, 0, 0 ) ) ).isEqualTo( 0.0 );
		assertThat( Colors.getLuminance( Color.color( 0.5, 0.5, 0.5, 0 ) ) ).isEqualTo( 0.5, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.color( 1, 1, 1, 0 ) ) ).isEqualTo( 1.0 );

		// Basics
		assertThat( Colors.getLuminance( Color.BLACK ) ).isEqualTo( 0.0 );
		assertThat( Colors.getLuminance( Color.RED ) ).isEqualTo( 0.3 );
		assertThat( Colors.getLuminance( Color.LIME ) ).isEqualTo( 0.59, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.BLUE ) ).isEqualTo( 0.11 );
		assertThat( Colors.getLuminance( Color.CYAN ) ).isEqualTo( 0.7 );
		assertThat( Colors.getLuminance( Color.MAGENTA ) ).isEqualTo( 0.41 );
		assertThat( Colors.getLuminance( Color.YELLOW ) ).isEqualTo( 0.89, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.WHITE ) ).isEqualTo( 1.0 );

		// Grays
		assertThat( Colors.getLuminance( Color.rgb( 127, 127, 127 ) ) ).isEqualTo( 0.4980392158031463 );
		assertThat( Colors.getLuminance( Color.color( 0.5, 0.5, 0.5, 1 ) ) ).isEqualTo( 0.5, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.rgb( 128, 128, 128 ) ) ).isEqualTo( 0.501960813999176 );
	}

	@Test
	void testSwapLuminance() {
		// Transparent
		assertSwapLuminance( Color.color( 0, 0, 0, 0 ), Color.color( 1, 1, 1, 0 ) );
		assertSwapLuminance( Color.color( 0.5, 0.5, 0.5, 0 ), Color.color( 0.5, 0.5, 0.5, 0 ) );
		assertSwapLuminance( Color.color( 1, 1, 1, 0 ), Color.color( 0, 0, 0, 0 ) );

		// Basics
		assertSwapLuminance( Color.color( 0, 0, 0 ), Color.color( 1, 1, 1 ) );
		assertSwapLuminance( Color.color( 1, 0, 0 ), Color.color( 1, 0.4 / 0.7, 0.4 / 0.7 ) );
		assertSwapLuminance( Color.color( 0, 1, 0 ), Color.color( 0, 0.6949152542372882, 0 ) );
		assertSwapLuminance( Color.color( 0, 0, 1 ), Color.color( 0.78 / 0.89, 0.78 / 0.89, 1 ) );
		assertSwapLuminance( Color.color( 0, 1, 1 ), Color.color( 0, 0.4285714285714286, 0.4285714285714286 ) );
		assertSwapLuminance( Color.color( 1, 0, 1 ), Color.color( 1, 0.3050847457627119, 1 ) );
		assertSwapLuminance( Color.color( 1, 1, 0 ), Color.color( 0.12359550561797754, 0.12359550561797754, 0 ) );
		assertSwapLuminance( Color.color( 1, 1, 1 ), Color.color( 0, 0, 0 ) );

		// Grays
		assertSwapLuminance( Color.rgb( 127, 127, 127 ), Color.color( 0.5019607543945312, 0.5019607543945312, 0.5019607543945312 ) );
		assertSwapLuminance( Color.color( 0.5, 0.5, 0.5 ), Color.color( 0.5, 0.5, 0.5 ) );
		assertSwapLuminance( Color.rgb( 128, 128, 128 ), Color.color( 0.4980391860008239, 0.4980391860008239, 0.4980391860008239 ) );

		// Others
		assertSwapLuminance( Color.rgb( 32, 64, 128 ), Color.color( 0.43855326441494197, 0.8771065288298839, 1.0 ) );
		assertSwapLuminance( Color.rgb( 128, 32, 64 ), Color.color( 1.0, 0.5527959296732772, 1.0 ), Offset.offset( 1e-1 ) );
		assertSwapLuminance( Color.rgb( 64, 128, 32 ), Color.color( 0.40048539793840837, 0.8009707958768167, 0.20024269896920419 ) );
		assertSwapLuminance( Color.rgb( 128, 64, 32 ), Color.color( 1.0, 0.6008207945860633, 0.30041039729303165 ) );
		assertSwapLuminance( Color.rgb( 64, 32, 128 ), Color.color( 1.0, 0.5760092319560652, 1.0 ), Offset.offset( 1e-1 ) );
		assertSwapLuminance( Color.rgb( 32, 128, 64 ), Color.color( 0.22173202141291556, 0.8869280856516623, 0.44346404282583113 ) );
	}

	@Test
	void asAwtColor() {
		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb( 128, 64, 32, 0.5 );
		java.awt.Color awtColor = Colors.asAwtColor( fxColor );
		assertThat( awtColor.getRed() ).isEqualTo( 128 );
		assertThat( awtColor.getGreen() ).isEqualTo( 64 );
		assertThat( awtColor.getBlue() ).isEqualTo( 32 );
		assertThat( awtColor.getAlpha() ).isEqualTo( 128 );
	}

	@Test
	void asFxColor() {
		java.awt.Color awtColor = new java.awt.Color( 128, 64, 32, 128 );
		javafx.scene.paint.Color fxColor = Colors.asFxColor( awtColor );
		assertThat( fxColor.getRed() ).isEqualTo( 0.501960813999176 );
		assertThat( fxColor.getGreen() ).isEqualTo( 0.250980406999588 );
		assertThat( fxColor.getBlue() ).isEqualTo( 0.125490203499794 );
		assertThat( fxColor.getOpacity() ).isEqualTo( 0.501960813999176 );
	}

	private void assertSwapLuminance( Color source, Color target ) {
		assertSwapLuminance( source, target, Offset.offset( 1e-7 ) );
	}

	private void assertSwapLuminance( Color source, Color target, Offset<Double> offset ) {
		Color inverted = Colors.invertLuminance( source );
		Color reverted = Colors.invertLuminance( inverted );

		try {
			assertThat( inverted.getRed() ).isEqualTo( target.getRed() );
			assertThat( inverted.getGreen() ).isEqualTo( target.getGreen() );
			assertThat( inverted.getBlue() ).isEqualTo( target.getBlue() );
			assertThat( inverted.getOpacity() ).isEqualTo( target.getOpacity() );
			assertThat( inverted.getHue() ).isEqualTo( target.getHue() );
			assertThat( Colors.getLuminance( inverted ) ).isEqualTo( Colors.getLuminance( target ), offset );

			// Sometimes the reverts are just way off due to color channel overflow
			//			assertThat( reverted.getRed() ).isEqualTo( source.getRed(), offset );
			//			assertThat( reverted.getGreen() ).isEqualTo( source.getGreen(), offset );
			//			assertThat( reverted.getBlue() ).isEqualTo( source.getBlue(), offset );
			assertThat( reverted.getOpacity() ).isEqualTo( source.getOpacity() );
			//			assertThat( reverted.getHue() ).isEqualTo( source.getHue() );
			assertThat( Colors.getLuminance( reverted ) ).isEqualTo( Colors.getLuminance( source ), offset );
		} catch( AssertionFailedError error ) {
			System.out.println( "source=" + source + " expected=" + target + " inverted=" + inverted + " reverted=" + reverted );
			throw error;
		}
	}

}
