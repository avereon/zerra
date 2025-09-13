package com.avereon.zerra.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.TimeoutException;

public final class FxStarter extends Application {

	private final static Object startLock = new Object();

	private static Throwable throwable;

	private static boolean started;

	public FxStarter() {}

	@Override
	public void start( Stage primaryStage ) {
		setStarted();
	}

	public static void startAndWait( long timeout ) throws RuntimeException {
		long limit = System.currentTimeMillis() + timeout;

		synchronized( startLock ) {
			if( started ) return;

			new Thread( () -> {
				try {
					FxStarter.launch();
				} catch( IllegalStateException exception ) {
					// Platform was already started by a different class
					setStarted();
				} catch( Throwable throwable ) {
					FxStarter.throwable = throwable;
				}
			} ).start();

			while( !started && throwable == null ) {
				try {
					startLock.wait( timeout );
					if( System.currentTimeMillis() >= limit && throwable == null && !started ) {
						throw new RuntimeException( new TimeoutException( "FX platform start timeout after " + timeout + " ms" ) );
					}
				} catch( Throwable throwable ) {
					FxStarter.throwable = throwable;
				}
			}

			if( throwable != null ) throw new RuntimeException( throwable );
		}
	}

	private static void setStarted( ) {
		synchronized( startLock ) {
			FxStarter.started = true;
			startLock.notifyAll();
		}
	}

}
