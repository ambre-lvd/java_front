package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.ApiClient;
import fr.netwok.service.MockService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class PanierController implements Initializable {

    @FXML private VBox vboxPanier;
    @FXML private Label lblTotalFinal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerPanier();
    }

    private void chargerPanier() {
        vboxPanier.getChildren().clear();

        List<Plat> toutLePanier = MockService.getInstance().getPanier();
        Set<String> idsTraites = new HashSet<>();

        if (toutLePanier.isEmpty()) {
            Label vide = new Label("Votre panier est vide.");
            vide.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px;");
            vboxPanier.getChildren().add(vide);
        }

        for (Plat p : toutLePanier) {
            if (!idsTraites.contains(p.getId())) {
                idsTraites.add(p.getId());
                int qte = MockService.getInstance().getQuantiteDuPlat(p);
                vboxPanier.getChildren().add(creerLigneProduit(p, qte));
            }
        }

        double total = MockService.getInstance().getTotalPanier();
        lblTotalFinal.setText(String.format("%.2f €", total));
    }

    private HBox creerLigneProduit(Plat p, int qte) {
        HBox ligne = new HBox(20);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: rgba(30, 41, 59, 0.6); -fx-background-radius: 15; -fx-padding: 15;");
        ligne.setPrefHeight(100);

        ImageView img = new ImageView();
        img.setFitHeight(80);
        img.setFitWidth(100);
        img.setPreserveRatio(true);
        try {
            String path = "/fr/netwok/images/" + p.getImagePath();
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) { /* Image par défaut si erreur */ }

        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        nom.setMinWidth(250);

        Label lblQte = new Label("x " + qte);
        lblQte.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        double sousTotal = p.getPrix() * qte;
        Label prix = new Label(String.format("%.2f €", sousTotal));
        prix.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnSupprimer = new Button("X");
        btnSupprimer.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF007F; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e -> {
            while(MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
            }
            chargerPanier();
        });

        ligne.getChildren().addAll(img, nom, lblQte, spacer, prix, btnSupprimer);
        return ligne;
    }

    /**
     * ACTION : Envoyer la commande au serveur Java (Back-end)
     */
    @FXML
    void validerCommande() {
        List<Plat> panier = MockService.getInstance().getPanier();
        if (panier.isEmpty()) return;

        try {
            // Extraction des IDs (E1, P2...) pour l'envoi API
            List<String> ids = panier.stream()
                    .map(Plat::getId)
                    .collect(Collectors.toList());

            // Appel de l'ApiClient (Table 99 par défaut)
            ApiClient.sendOrder(99, ids);

            // Succès : Vider et quitter
            MockService.getInstance().viderPanier();
            System.out.println("✅ Commande envoyée avec succès au serveur !");
            retourCatalogue();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi : " + e.getMessage());
            // Ici tu pourrais ajouter une alerte visuelle pour l'utilisateur
        }
    }

    @FXML
    void retourCatalogue() {
        try {
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}