/* Author: Luigi Vincent
*
* Calling class / entry point for the Code Generator
*
* Companion app to my PennApps XIV submission.
*/

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CodeCreator extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Parent root = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("assets/main.fxml"));
			root = loader.load();
			((CreatorController) loader.getController()).setStage(stage);
		} catch (IOException ioe) {
			System.err.println("Could not load FXML");
			System.exit(1);
		}

		stage.setScene(new Scene(root));
		stage.setTitle("PennApps XIV");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("assets/penn_apps.png")));
		stage.setOnCloseRequest(e -> Platform.exit());
		stage.show();
	}
}