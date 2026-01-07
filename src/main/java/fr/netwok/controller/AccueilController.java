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
        else if (langue.equals("ES")) {
            btnCommencer.setText("PRESIONAR PARA COMENZAR");
            nomsEquipe.setText("Matt CAZORLA, Louis DURAND, Ambre LAVAUD y Corentin DE ANGELIS");
        }
        else if (langue.equals("PY")) {
            btnCommencer.setText("НАЖМИТЕ, ЧТОБЫ НАЧАТЬ");
            nomsEquipe.setText("Мэтт КАЗОРЛА, Луи ДЮРАН, Амбр ЛАВО и Корентин ДЕ АНДЖЕЛИС");
        }
        else if (langue.equals("ไทย")) {
            btnCommencer.setText("กดเพื่อเริ่ม");
            nomsEquipe.setText("แม็ตต์ คาซอร์ลา, หลุยส์ ดูรองด์, อัมเบร ลาวอูด และ โคเรนติน เดอ แองเจลิส");
        }
        else if (langue.equals("한국말")) {
            btnCommencer.setText("시작하려면 누르세요");
            nomsEquipe.setText("매트 카조를라, 루이 뒤랑, 앙브르 라보, 코렝탱 드 안젤리스");
        }



    }
}