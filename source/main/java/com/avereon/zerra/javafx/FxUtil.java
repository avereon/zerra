package com.avereon.zerra.javafx;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class FxUtil {

	public static Pos parseAlign( String align ) {
		return switch( align ) {
			case "northwest" -> Pos.TOP_LEFT;
			case "north" -> Pos.TOP_CENTER;
			case "northeast" -> Pos.TOP_RIGHT;
			case "west" -> Pos.CENTER_LEFT;
			case "east" -> Pos.CENTER_RIGHT;
			case "southwest" -> Pos.BOTTOM_LEFT;
			case "south" -> Pos.BOTTOM_CENTER;
			case "southeast" -> Pos.BOTTOM_RIGHT;
			default -> Pos.CENTER;
		};
	}

	public static BackgroundPosition parseBackgroundPosition( String align ) {
		return switch( align ) {
			case "northwest" -> new BackgroundPosition( Side.LEFT, 0, true, Side.TOP, 0, true );
			case "north" -> new BackgroundPosition( Side.LEFT, 0.5, true, Side.TOP, 0, true );
			case "northeast" -> new BackgroundPosition( Side.LEFT, 1, true, Side.TOP, 0, true );
			case "west" -> new BackgroundPosition( Side.LEFT, 0, true, Side.TOP, 0.5, true );
			case "center" -> new BackgroundPosition( Side.LEFT, 0.5, true, Side.TOP, 0.5, true );
			case "east" -> new BackgroundPosition( Side.LEFT, 1, true, Side.TOP, 0.5, true );
			case "southwest" -> new BackgroundPosition( Side.LEFT, 0, true, Side.TOP, 1, true );
			case "south" -> new BackgroundPosition( Side.LEFT, 0.5, true, Side.TOP, 1, true );
			case "southeast" -> new BackgroundPosition( Side.LEFT, 1, true, Side.TOP, 1, true );
			default -> BackgroundPosition.CENTER;
		};
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Parent> T findParentByClass( Node node, Class<T> clazz ) {
		while( (node = node.getParent()) != null ) {
			if( clazz.isAssignableFrom( node.getClass() ) ) return (T)node;
		}
		return null;
	}

	public static boolean isChildOf( Node node, Node container ) {
		while( (node = node.getParent()) != null ) {
			if( node == container ) return true;
		}
		return false;
	}

	public static boolean isParentOf( TreeItem<?> item, TreeItem<?> child ) {
		TreeItem<?> parent = child;

		while( parent != null ) {
			if( item.equals( parent ) ) return true;
			parent = parent.getParent();
		}

		return false;
	}

	/**
	 * Get the bounds of a node in the coordinate space of the ancestor node. The
	 * ancestor does not have to be the immediate parent of the local node.
	 *
	 * @param local The node to get the bounds of
	 * @param ancestor The ancestor node to get the bounds in
	 * @return The bounds of the local node in the ancestor node coordinate space
	 */
	public static Bounds localToAncestor( Node local, Node ancestor ) {
		return localToAncestor( local, ancestor, local.getLayoutBounds() );
	}

	/**
	 * Convert a bounds in the local node coordinate space to a bounds in the
	 * ancestor node coordinate space. The ancestor does not have to be the
	 * immediate parent of the local node.
	 *
	 * @param local The node to get the bounds of
	 * @param ancestor The ancestor node to get the bounds in
	 * @param bounds The bounds to convert from local space to ancestor space
	 * @return The bounds of the local node in the ancestor node coordinate space
	 */
	public static Bounds localToAncestor( Node local, Node ancestor, Bounds bounds ) {
		Bounds result = bounds;

		Node node = local;
		while( node != null && node != ancestor ) {
			result = node.localToParent( result );
			node = node.getParent();
		}

		return result;
	}

	public static Rectangle toRectangle( Bounds bounds ) {
		return new Rectangle( bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight() );
	}

	public static Bounds getContentBounds( Parent parent ) {
		Bounds bounds = null;
		for( Node child : parent.getChildrenUnmodifiable() ) {
			bounds = bounds == null ? child.getLayoutBounds() : merge( bounds, child.getLayoutBounds() );
		}
		return bounds;
	}


	public static Bounds bounds( Point3D a, Point3D b ) {
		double minX = Math.min( a.getX(), b.getX() );
		double minY = Math.min( a.getY(), b.getY() );
		double minZ = Math.min( a.getZ(), b.getZ() );
		double maxX = Math.max( a.getX(), b.getX() );
		double maxY = Math.max( a.getY(), b.getY() );
		double maxZ = Math.max( a.getZ(), b.getZ() );
		return new BoundingBox( minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ );
	}

	public static Bounds add( Bounds bounds, Point3D point ) {
		double minX = Math.min( bounds.getMinX(), point.getX() );
		double minY = Math.min( bounds.getMinY(), point.getY() );
		double minZ = Math.min( bounds.getMinZ(), point.getZ() );
		double maxX = Math.max( bounds.getMaxX(), point.getX() );
		double maxY = Math.max( bounds.getMaxY(), point.getY() );
		double maxZ = Math.max( bounds.getMaxZ(), point.getZ() );
		return new BoundingBox( minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ );
	}

	public static Bounds merge( Bounds a, Bounds b ) {
		if( a == null ) return b;
		if( b == null ) return a;
		double minX = Math.min( a.getMinX(), b.getMinX() );
		double minY = Math.min( a.getMinY(), b.getMinY() );
		double minZ = Math.min( a.getMinZ(), b.getMinZ() );
		double maxX = Math.max( a.getMaxX(), b.getMaxX() );
		double maxY = Math.max( a.getMaxY(), b.getMaxY() );
		double maxZ = Math.max( a.getMaxZ(), b.getMaxZ() );
		return new BoundingBox( minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ );
	}

	public static Bounds add( Bounds a, Insets b ) {
		return new BoundingBox( a.getMinX(), a.getMinY(), a.getWidth() + b.getLeft() + b.getRight(), a.getHeight() + b.getTop() + b.getBottom() );
	}

	public static Insets add( Insets a, Insets b ) {
		return new Insets( a.getTop() + b.getTop(), a.getRight() + b.getRight(), a.getBottom() + b.getBottom(), a.getLeft() + b.getLeft() );
	}

	public static <T> List<TreeItem<T>> flatTree( TreeItem<T> item ) {
		return flatTree( item, false );
	}

	public static <T> List<TreeItem<T>> flatTree( TreeItem<T> item, boolean includeItem ) {
		List<TreeItem<T>> list = new ArrayList<>();

		if( includeItem ) list.add( item );
		item.getChildren().forEach( ( child ) -> list.addAll( flatTree( child, true ) ) );

		return list;
	}

	/**
	 * Pick the child node in a parent node that contains the scene point. Found
	 * at: <a href="http://fxexperience.com/2016/01/node-picking-in-javafx/">fxexperience.com</a>.
	 *
	 * @param parent The parent node
	 * @param sceneX The x coordinate in the scene
	 * @param sceneY The y coordinate in the scene
	 * @return The node at the location
	 */
	public static Node pick( Node parent, double sceneX, double sceneY ) {
		Point2D point = parent.sceneToLocal( sceneX, sceneY, true );

		// Check if the given node has the point inside it
		if( !parent.contains( point ) ) return null;

		if( parent instanceof Parent ) {
			Node closest = null;
			List<Node> children = ((Parent)parent).getChildrenUnmodifiable();
			for( int i = children.size() - 1; i >= 0; i-- ) {
				Node child = children.get( i );
				point = child.sceneToLocal( sceneX, sceneY, true );
				if( child.isVisible() && !child.isMouseTransparent() && child.contains( point ) ) {
					closest = child;
					break;
				}
			}

			if( closest != null ) return pick( closest, sceneX, sceneY );
		}

		return parent;
	}

	public static <T extends Node> T findById( Collection<? extends T> nodes, String id ) {
		return nodes.stream().filter( n -> Objects.equals( n.getId(), id ) ).findFirst().orElse( null );
	}

	public static <T extends MenuItem> T findMenuItemById( Collection<? extends T> nodes, String id ) {
		return nodes.stream().filter( n -> Objects.equals( n.getId(), id ) ).findFirst().orElse( null );
	}

	public static void setTransferMode( DragEvent e ) {
		setTransferMode( e, TransferMode.MOVE );
	}

	public static void setTransferMode( DragEvent e, TransferMode... defaultModes ) {
		// This allows either copy or move, but copy appears to be default on Cinnamon
		e.acceptTransferModes( TransferMode.ANY );

		// This forces the mode to move
		//e.acceptTransferModes( TransferMode.MOVE );
	}

}
