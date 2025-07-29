package com.avereon.zerra.javafx;

import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.CustomLog;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@CustomLog
public class Fx {

	public static final Bounds EMPTY_BOUNDS = new BoundingBox( 0, 0, 0, 0, 0, 0 );

	public static final Transform IDENTITY_TRANSFORM = new Affine();

	private Fx() {}

	public static void startup() {
		try {
			log.atDebug().log( "Starting FX Platform..." );
			Platform.startup( () -> log.atConfig().log( "FX Platform started!" ) );
		} catch( IllegalStateException exception ) {
			log.atConfig().log( "FX Platform already started!" );
		}
	}

	/**
	 * Convenience method to run a Runnable on the FX thread.
	 *
	 * @param runnable The runnable to execute
	 */
	public static void run( Runnable runnable ) {
		Platform.runLater( runnable );
	}

	/**
	 * Convenience method to ensure a runnable is executed on the FX thread.
	 * If the calling thread is already the FX thread, then this method simply
	 * executes the runnable. Otherwise, it will delegate execution of the
	 * runnable to the FX thread.
	 *
	 * @param runnable The runnable to execute
	 */
	public static void onFxOrCurrent( Runnable runnable ) {
		if( isFxThread() ) {
			runnable.run();
		} else if( Fx.isRunning() ) {
			Fx.run( runnable );
		} else {
			runnable.run();
		}
	}

	/**
	 * Convenience method to run a Runnable on the FX thread if FX is available.
	 * If not, run the Runnable on the current thread with the assumption that it
	 * is not the FX thread.
	 *
	 * @param runnable The runnable to execute
	 */
	public static void runOrOnCurrentThread( Runnable runnable ) {
		if( Fx.isRunning() ) {
			Fx.run( runnable );
		} else {
			runnable.run();
		}
	}

	/**
	 * Convenience method to run Callable on the FX thread and return the result.
	 *
	 * @param callable The callable to execute
	 * @param <T> The type of the result
	 * @return The result of the callable
	 */
	public static <T> T call( Callable<T> callable ) throws Exception {
		AtomicReference<T> reference = new AtomicReference<>();
		AtomicReference<Exception> exceptionReference = new AtomicReference<>();
		run( () -> {
			try {
				reference.set( callable.call() );
			} catch( Exception exception ) {
				exceptionReference.set( exception );
			}
		} );
		Fx.waitFor( 1, TimeUnit.SECONDS );
		if( exceptionReference.get() != null ) throw exceptionReference.get();
		return reference.get();
	}

	public static boolean isRunning() {
		try {
			Platform.runLater( () -> {} );
			return true;
		} catch( IllegalStateException throwable ) {
			return false;
		}
	}

	public static boolean isFxThread() {
		return Platform.isFxApplicationThread();
	}

	public static void affirmOnFxThread() {
		if( !isFxThread() ) throw new IllegalStateException( "Not on FX thread; thread=" + Thread.currentThread().getName() );
	}

	public static Stage getStage( MouseEvent event ) {
		return (Stage)getWindow( event );
	}

	public static Window getWindow( MouseEvent event ) {
		return ((Node)event.getSource()).getScene().getWindow();
	}

	public static void waitFor( long timeout ) {
		waitFor( timeout, TimeUnit.MILLISECONDS );
	}

	public static void waitFor( long count, TimeUnit unit ) {
		try {
			doWaitForWithExceptions( count, unit );
		} catch( InterruptedException exception ) {
			Thread.currentThread().interrupt();
		} catch( TimeoutException ignore ) {
			// Intentionally ignore exception
		}
	}

	public static void waitForWithExceptions( long timeout ) throws TimeoutException, InterruptedException {
		waitForWithExceptions( timeout, TimeUnit.MILLISECONDS );
	}

	public static void waitForWithExceptions( long count, TimeUnit unit ) throws TimeoutException, InterruptedException {
		for( int index = 0; index < count; index++ ) {
			doWaitForWithExceptions( count, unit );
		}
	}

	private static void doWaitForWithExceptions( long count, TimeUnit unit ) throws TimeoutException, InterruptedException {
		if( Fx.isFxThread() ) throw new IllegalStateException( "Attempt to wait on FX thread from FX thread" );

		Semaphore semaphore = new Semaphore( 0 );
		Platform.runLater( semaphore::release );

		// NOTE Thread.yield() is helpful but not consistent
		//Thread.yield();

		if( !semaphore.tryAcquire( count, unit ) ) throw new TimeoutException( "Timeout waiting for FX" );
	}

}
