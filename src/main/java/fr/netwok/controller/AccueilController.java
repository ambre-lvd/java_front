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
        } else if (langue.equals("EN")) {
            btnCommencer.setText("TOUCH TO START");
            nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD and Corentin DE ANGELIS");
        }
        else if (langue.equals("中文")) {
            btnCommencer.setText("按下以開始");
            nomsEquipe.setText("馬特·卡佐拉、路易·杜朗、安布爾·拉沃和科倫汀·德·安傑利斯");
        }
        else if (langue.equals("日本語")) {
            btnCommencer.setText("開始するには押してください");
            nomsEquipe.setText("マット・カゾラ、ルイ・デュラン、アンブル・ラヴォー、コランタン・デ・アンジェリス");
        }



    }
}