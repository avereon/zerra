package com.avereon.zerra.theme;

import lombok.Getter;

@Getter
public enum Theme {

	DARK( "-fx-text-background-color: #E0E0E0FF;" ),
	LIGHT( "-fx-text-background-color: #202020FF;" );

	private final String fxTextBackgroundColor;

	Theme( String text ) {
		this.fxTextBackgroundColor = text;
	}

}
