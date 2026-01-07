package fr.netwok.controller;

import fr.netwok.NetwokApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AccueilController {

    @FXML private Label nomsEquipe;
    @FXML private Button btnCommencer;

    @FXML
    void commencerCommande(ActionEvent event) {
        try {
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) {
            System.err.println("Erreur chargement catalogue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void changeLanguage(ActionEvent event) {
        Button btnClique = (Button) event.getSource();
        String langue = btnClique.getText().toUpperCase();

        CatalogueController.setLangueActuelle(langue);
        if (langue.equals("FR")) {
            btnCommencer.setText("APPUYER POUR COMMENCER");
            nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD et Corentin DE ANGELIS");
        } else {
            btnCommencer.setText("TOUCH TO START");
            nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD and Corentin DE ANGELIS");
        }
    }
}