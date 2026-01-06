package fr.netwok;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import java.io.IOException;

public class NetwokApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("views/accueil"), 1920, 1080);
        stage.setTitle("NetWok - Borne de Commande");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);
        
        // Remettre le scroll en haut de la page
        if (root instanceof ScrollPane) {
            ((ScrollPane) root).setVvalue(0);
        }
        root.lookupAll(".scroll-pane").forEach(node -> {
            if (node instanceof ScrollPane) {
                ((ScrollPane) node).setVvalue(0);
            }
        });
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NetwokApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}