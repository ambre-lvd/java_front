package fr.netwok.controller;

import fr.netwok.NetwokApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;

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
}