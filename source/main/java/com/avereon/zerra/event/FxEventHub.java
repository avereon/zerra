package com.avereon.zerra.event;

import com.avereon.event.EventHub;
import javafx.event.EventDispatchChain;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FxEventHub extends EventHub {

	private final Map<javafx.event.EventType<? extends javafx.event.Event>, Collection<FxEventHandlerWrapper<?>>> fxHandlers;

	public FxEventHub() {
		this.fxHandlers = new ConcurrentHashMap<>();
	}

	public FxEventHub parent( EventHub parent ) {
		super.parent( parent );
		return this;
	}

	public FxEventHub dispatch( javafx.event.Event event ) {
		// While the type of the incoming event is known, the parent event types,
		// used later in the method are not well known. They could be of any event
		// type and therefore this variable needs to allow any event type.
		javafx.event.EventType<?> type = event.getEventType();

		// Go through all the handlers of the event type and all handlers of all
		// the parent event types, passing the event to each handler.
		while( type != null ) {
			fxHandlers.computeIfPresent( type, ( t, handlers ) -> {
				handlers.forEach( handler -> handler.dispatchEvent( event, null ) );
				return handlers;
			} );
			type = type.getSuperType();
		}

		// If there is a parent event hub, pass the event to it
		EventHub parent = super.getParent();
		if( parent instanceof FxEventHub ) ((FxEventHub)parent).dispatch( event );

		return this;
	}

	public <T extends javafx.event.Event> FxEventHub register( javafx.event.EventType<? super T> type, javafx.event.EventHandler<? super T> handler ) {
		fxHandlers.computeIfAbsent( type, k -> new HashSet<>() ).add( new FxEventHandlerWrapper<>( handler ) );
		return this;
	}

	public <T extends javafx.event.Event> FxEventHub unregister( javafx.event.EventType<? super T> type, javafx.event.EventHandler<? super T> handler ) {
		fxHandlers.computeIfPresent( type, ( t, c ) -> {
			c.removeIf( w -> w.getHandler() == handler );
			return c.isEmpty() ? null : c;
		} );
		return this;
	}

	@Getter
	private static class FxEventHandlerWrapper<T extends javafx.event.Event> implements javafx.event.EventDispatcher, javafx.event.EventHandler<T> {

		private final javafx.event.EventHandler<T> handler;

		public FxEventHandlerWrapper( javafx.event.EventHandler<T> handler ) {
			this.handler = handler;
		}

		@Override
		public void handle( T event ) {
			handler.handle( event );
		}

		@Override
		@SuppressWarnings( "unchecked" )
		public javafx.event.Event dispatchEvent( javafx.event.Event event, EventDispatchChain tail ) {
			handle( (T)event );
			return event;
		}

	}

}
