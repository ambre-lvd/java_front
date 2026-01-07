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
        // 1. Récupérer la taille réelle de ton écran (sans la barre des tâches)
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        // 2. Charger la vue
        Parent root = loadFXML("views/accueil");

        // 3. Créer la scène avec EXACTEMENT la taille disponible
        scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        stage.setTitle("NetWok - Borne de Commande");
        stage.setScene(scene);

        // 4. Maximiser pour être sûr que ça colle aux bords
        stage.setMaximized(true);
        // stage.setFullScreen(true); // Décommente cette ligne si tu veux un vrai mode "Borne" (sans croix pour fermer)

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