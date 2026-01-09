package fr.netwok.controller;

import fr.netwok.NetwokApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class AccueilController {

    @FXML private Label nomsEquipe;
    @FXML private Button btnCommencer;

    @FXML
    void commencerCommande() {
        try {
            animatePageTransition("views/catalogue");
        } catch (IOException e) {
            System.err.println("Erreur chargement catalogue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void animatePageTransition(String viewName) throws IOException {
        if (btnCommencer == null || btnCommencer.getScene() == null) {
            NetwokApp.setRoot(viewName);
            return;
        }
        
        // Créer un overlay noir immédiatement opaque sur la racine
        Pane root = (Pane) btnCommencer.getScene().getRoot();
        Rectangle blackOverlay = new Rectangle();
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setOpacity(1);
        
        root.getChildren().add(blackOverlay);
        blackOverlay.widthProperty().bind(root.widthProperty());
        blackOverlay.heightProperty().bind(root.heightProperty());
        
        // Charger la nouvelle page immédiatement après le noir
        NetwokApp.setRoot(viewName);
    }

    @FXML
    private void changeLanguage(ActionEvent event) {
        Button btnClique = (Button) event.getSource();
        String langue = btnClique.getText().toUpperCase();

        CatalogueController.setLangueActuelle(langue);
        switch (langue) {
            case "FR" -> {
                btnCommencer.setText("APPUYER POUR COMMENCER");
                nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD et Corentin DE ANGELIS");
            }
            case "EN" -> {
                btnCommencer.setText("TOUCH TO START");
                nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD and Corentin DE ANGELIS");
            }
            case "中文" -> {
                btnCommencer.setText("按下以開始");
                nomsEquipe.setText("馬特·卡佐拉、路易·杜朗、安布爾·拉沃和科倫汀·德·安傑利斯");
            }
            case "日本語" -> {
                btnCommencer.setText("開始するには押してください");
                nomsEquipe.setText("マット・カゾラ、ルイ・デュラン、アンブル・ラヴォー、コランタン・デ・アンジェリス");
            }
            case "ES" -> {
                btnCommencer.setText("PRESIONAR PARA COMENZAR");
                nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD y Corentin DE ANGELIS");
            }
            case "PY" -> {
                btnCommencer.setText("НАЖМИТЕ, ЧТОБЫ НАЧАТЬ");
                nomsEquipe.setText("Мэтт КАЗОРЛА, Луи ДЮРАН, Амбр ЛАВО и Корентин ДЕ АНДЖЕЛИС");
            }
            case "ไทย" -> {
                btnCommencer.setText("กดเพื่อเริ่ม");
                nomsEquipe.setText("แม็ตต์ คาซอร์ลา, หลุยส์ ดูรองด์, อัมเบร ลาวอูด และ โคเรนติน เดอ แองเจลิส");
            }
            case "한국말" -> {
                btnCommencer.setText("시작하려면 누르세요");
                nomsEquipe.setText("매트 카조를라, 루이 뒤랑, 앙브르 라보, 코렝탱 드 안젤리스");
            }
        }



    }
}