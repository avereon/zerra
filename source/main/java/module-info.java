module com.avereon.zerra {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.xenon;
	requires com.avereon.xenon.junit5;
	requires java.logging;
	requires javafx.graphics;
	requires org.assertj.core;
	requires org.junit.jupiter.api;
	requires org.testfx;
	requires org.testfx.junit5;

	exports com.avereon.zerra;
}
