package fr.netwok.controller;

import fr.netwok.NetwokApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AccueilController {

    @FXML
    void commencerCommande(ActionEvent event) {
        System.out.println("--- CLIC DÉTECTÉ : Tentative d'ouverture du catalogue ---");
        
        try {
            // Essaie de charger la vue
            NetwokApp.setRoot("views/catalogue");
            System.out.println("--- SUCCÈS : Catalogue chargé ---");
        } catch (IOException e) {
            // AFFICHE L'ERREUR EN ROUGE DANS LA CONSOLE
            System.err.println("--- ERREUR CRITIQUE ---");
            System.err.println("Impossible d'ouvrir 'views/catalogue.fxml'.");
            System.err.println("Cause probable : Erreur dans le code du CatalogueController (images ?) ou fichier FXML introuvable.");
            e.printStackTrace(); 
        } catch (Exception e) {
            System.err.println("Autre erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private Label languageDisplay;
    @FXML
    private Label nomsEquipe;
    @FXML
    private Button btnCommencer;

    @FXML
    private void changeLanguage(ActionEvent event) {
        Button btnClique = (Button) event.getSource();
        String langue = btnClique.getText();

        // On met à jour le petit label du bas
        //languageDisplay.setText("Langue sélectionnée : " + langue);

        // On change tous les autres textes selon la langue
        switch (langue) {
            case "FR":
                btnCommencer.setText("APPUYER POUR COMMENCER");
                nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD et Corentin DE ANGELIS");
                break;
            case "EN":
                btnCommencer.setText("TOUCH TO START");
                nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD and Corentin DE ANGELIS");
                break;
        }
    }
}