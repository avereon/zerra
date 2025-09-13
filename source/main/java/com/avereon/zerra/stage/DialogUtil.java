package com.avereon.zerra.stage;

import javafx.event.EventHandler;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.CustomLog;

import java.util.Optional;

@CustomLog
public class DialogUtil {

	public static <R> void show( Dialog<R> dialog ) {
		show( null, dialog );
	}

	public static <R> void show( Stage owner, Dialog<R> dialog ) {
		registerCenterOnStage( owner, dialog );
		dialog.show();
	}

	public static <R> Optional<R> showAndWait( Stage owner, Dialog<R> dialog ) {
		registerCenterOnStage( owner, dialog );
		return dialog.showAndWait();
	}

	private static <R> void registerCenterOnStage( Stage stage, Dialog<R> dialog ) {
		dialog.initOwner( stage );
		dialog.initModality( Modality.WINDOW_MODAL );
		// WORKAROUND To dialogs showing with zero size on Linux
		dialog.setResizable( true );

		final EventHandler<DialogEvent> shownHandler = e -> {
			double x;
			double y;
			if( stage == null ) {
				// Use the screen
				Screen screen = Screen.getPrimary();
				x = screen.getBounds().getMinX() + 0.5 * (screen.getBounds().getWidth() - dialog.getWidth());
				y = screen.getBounds().getMinY() + 0.5 * (screen.getBounds().getHeight() - dialog.getHeight());
			} else {
				// Use the stage
				x = stage.getX() + 0.5 * (stage.getWidth() - dialog.getWidth());
				y = stage.getY() + 0.5 * (stage.getHeight() - dialog.getHeight());
			}
			dialog.setX( x );
			dialog.setY( y );
			// TODO Need to remove the shown handler to prevent memory leaks
			dialog.setOnShown( null );
		};
		dialog.setOnShown( shownHandler );
	}

}
