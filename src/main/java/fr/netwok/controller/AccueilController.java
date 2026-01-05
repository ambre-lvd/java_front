package fr.netwok.controller;

import fr.netwok.NetwokApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;

public class AccueilController {

    @FXML
    void commencerCommande(ActionEvent event) {
        System.out.println("Lancement du catalogue...");
        try {
            // Charge la nouvelle vue catalogue.fxml
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur : Impossible de trouver 'views/catalogue.fxml'");
        }
    }
}