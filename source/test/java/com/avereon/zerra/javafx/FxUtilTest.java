package com.avereon.zerra.javafx;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FxUtilTest extends FxPlatformTestCase {

	private final TreeItem<String> root = new TreeItem<>( "root" );

	private final TreeItem<String> a = new TreeItem<>( "a" );

	private final TreeItem<String> a1 = new TreeItem<>( "a1" );

	private final TreeItem<String> a2 = new TreeItem<>( "a2" );

	private final TreeItem<String> b = new TreeItem<>( "b" );

	private final TreeItem<String> b3 = new TreeItem<>( "b3" );

	private final TreeItem<String> b4 = new TreeItem<>( "b4" );

	private final TreeItem<String> b5 = new TreeItem<>( "b5" );

	private final TreeItem<String> b6 = new TreeItem<>( "b6" );

	private final TreeItem<String> c = new TreeItem<>( "c" );

	private final TreeItem<String> c7 = new TreeItem<>( "c7" );

	@BeforeEach
	@SuppressWarnings( "unchecked" )
	public void setup() {
		a.getChildren().addAll( a1, a2 );
		b.getChildren().addAll( b3, b4, b5, b6 );
		c.getChildren().addAll( c7 );
		root.getChildren().addAll( a, b, c );
	}

	@Test
	void getContentBounds() {
		// given
		double size = 2;
		double stroke = 0.707106828689575;
		double sizeWithStroke = size + stroke;

		Pane pane = new Pane();
		pane.resize( 0, 0 );
		pane.getChildren().add( new Line( -size, -size, size, size ) );

		// when
		Bounds result = FxUtil.getContentBounds( pane );

		// then
		assertThat( result ).isEqualTo( new BoundingBox( -sizeWithStroke, -sizeWithStroke, 2 * sizeWithStroke, 2 * sizeWithStroke ) );
	}

	@Test
	void bounds() {
		// given
		Point3D origin = new Point3D( 0, 0, 0 );
		Point3D point = new Point3D( 1, 2, 3 );

		// when
		Bounds result = FxUtil.bounds( origin, point );

		// then
		assertThat( result ).isEqualTo( new BoundingBox( 0, 0, 0, 1, 2, 3 ) );
	}

	@Test
	void addPointToBounds() {
		// given
		Bounds bounds = new BoundingBox( 0, 0, 0, 0, 0, 0 );
		Point3D point = new Point3D( 1, 2, 3 );

		// when
		Bounds result = FxUtil.add( bounds, point );

		// then
		assertThat( result ).isEqualTo( new BoundingBox( 0, 0, 0, 1, 2, 3 ) );
	}

	@Test
	void merge() {
		// given
		Bounds a = new BoundingBox( 0, 0, 0, 1, 2, 3 );
		Bounds b = new BoundingBox( 1, 2, 3, 4, 5, 6 );

		// when
		Bounds result = FxUtil.merge( a, b );

		// then
		assertThat( result ).isEqualTo( new BoundingBox( 0, 0, 0, 5, 7, 9 ) );
	}

	@Test
	void testIsChildOfFalse() {
		Pane parent = new Pane();
		Node child = new Label();
		assertThat( FxUtil.isChildOf( child, parent ) ).isEqualTo( false );
	}

	@Test
	void testIsChildOfWithParent() {
		Pane parent = new Pane();
		Node child = new Label();

		assertThat( FxUtil.isChildOf( child, parent ) ).isEqualTo( false );

		parent.getChildren().add( child );

		assertThat( FxUtil.isChildOf( child, parent ) ).isEqualTo( true );
	}

	@Test
	void testIsChildOfWithGrandParent() {
		Pane grandParent = new Pane();
		Pane parent = new Pane();
		Node child = new Label();

		assertThat( FxUtil.isChildOf( child, grandParent ) ).isEqualTo( false );

		grandParent.getChildren().add( parent );
		parent.getChildren().add( child );

		assertThat( FxUtil.isChildOf( child, grandParent ) ).isEqualTo( true );
	}

	@Test
	void localToAncestor() {
		// given
		Pane ancestor = new Pane();
		Pane parent = new Pane();
		Pane child = new Pane();
		ancestor.getChildren().addAll( parent );
		parent.getChildren().add( child );

		ancestor.resize( 100, 100 );
		parent.resizeRelocate( 10, 10, 80, 80 );
		child.resizeRelocate( 50, 50, 10, 10 );

		// when
		Bounds result = FxUtil.localToAncestor( child, ancestor );

		// then
		assertThat( result ).isEqualTo( new BoundingBox( 60, 60, 0, 10, 10, 0 ) );
	}

	@Test
	void localToAncestorWithBounds() {
		// given
		Pane ancestor = new Pane();
		Pane parent = new Pane();
		Pane child = new Pane();
		ancestor.getChildren().addAll( parent );
		parent.getChildren().add( child );

		ancestor.resize( 100, 100 );
		parent.resizeRelocate( 10, 10, 80, 80 );
		child.resizeRelocate( 50, 50, 10, 10 );

		Bounds bounds = new BoundingBox( 20, -10, 0, 5, 5, 0 );

		// when
		Bounds result = FxUtil.localToAncestor( child, ancestor, bounds );

		// then
		assertThat( result ).isEqualTo( new BoundingBox( 80, 50, 0, 5, 5, 0 ) );
	}

	@Test
	void testFlatTree() {
		assertThat( FxUtil.flatTree( root ) ).contains( a, a1, a2, b, b3, b4, b5, b6, c, c7 );
	}

	@Test
	void testFlatTreeWithRoot() {
		assertThat( FxUtil.flatTree( root, true ) ).contains( root, a, a1, a2, b, b3, b4, b5, b6, c, c7 );
	}

}
