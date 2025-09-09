package com.avereon.zerra.event;

import com.avereon.zerra.javafx.Fx;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

@CustomLog
public class FxEventWatcher <T extends Event> implements EventHandler<T> {

	private static final long DEFAULT_WAIT_TIMEOUT = 2500;

	private final Queue<T> events = new ConcurrentLinkedQueue<>();

	@Getter
	private final long timeout;

	@Getter
	@Setter
	private boolean printEventCapture;

	public FxEventWatcher() {
		this( DEFAULT_WAIT_TIMEOUT );
	}

	public FxEventWatcher( long timeout ) {
		this.timeout = timeout;
	}

	@Override
	public synchronized void handle( T event ) {
		if( printEventCapture ) System.out.println( "Captured FX event: type=" + event.getEventType() );
		events.offer( event );
		notifyAll();
	}

	public synchronized List<T> getEvents() {
		return new ArrayList<>( events );
	}

	public void waitForEvent( EventType<? extends T> type ) throws InterruptedException, TimeoutException {
		waitForEvent( type, timeout );
		Fx.waitForWithExceptions( timeout );
	}

	@SuppressWarnings( "unused" )
	public synchronized void waitForNextEvent( EventType<T> type ) throws InterruptedException, TimeoutException {
		waitForNextEvent( type, timeout );
	}

	/**
	 * Wait for an event of a specific type to occur. If the event has already
	 * occurred, this method will return immediately. If the event has not already
	 * occurred, then this method waits until the next event occurs, or the
	 * specified timeout, whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	public synchronized void waitForEvent( EventType<? extends T> type, long timeout ) throws InterruptedException, TimeoutException {
		if( timeout <= 0 ) return;

		String eventTypeName = type.getSuperType() + "." + type.getName();
		long expiration = System.currentTimeMillis() + timeout;
		boolean expired = expiration < System.currentTimeMillis();

		T event = null;
		while( !expired && (event = findNext( type )) == null ) {
			wait( Math.max( 0, expiration - System.currentTimeMillis() ) );

			// Wait if the expiration is still greater than the current time
			expired = expiration < System.currentTimeMillis();
		}

		if( expired ) throw new TimeoutException( "Timeout waiting for event " + eventTypeName );

		if( event.getClass().getSimpleName().equals( "ToolEvent")) System.out.println( "Received event=" + event );
	}

	private synchronized T findNext( EventType<? extends T> type ) {
		T event;
		while( (event = events.poll()) != null ) {
			if( event.getEventType() == type ) return event;
		}
		return null;
	}

	/**
	 * Wait for the next event of a specific type to occur. This method always waits until the next event occurs, or the specified timeout, whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	@SuppressWarnings( "SameParameterValue" )
	private synchronized void waitForNextEvent( EventType<T> type, long timeout ) throws InterruptedException, TimeoutException {
		events.clear();
		waitForEvent( type, timeout );
	}

}
