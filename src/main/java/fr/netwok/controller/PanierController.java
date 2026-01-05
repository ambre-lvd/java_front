package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
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

public class PanierController implements Initializable {

    @FXML private VBox vboxPanier;
    @FXML private Label lblTotalFinal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerPanier();
    }

    private void chargerPanier() {
        // 1. Nettoyer l'affichage précédent
        vboxPanier.getChildren().clear();
        
        // 2. Récupérer tous les articles
        List<Plat> toutLePanier = MockService.getInstance().getPanier();
        
        // 3. Astuce pour ne pas afficher de doublons : on garde une liste des ID déjà traités
        Set<String> idsTraites = new HashSet<>();

        // Si le panier est vide
        if (toutLePanier.isEmpty()) {
            Label vide = new Label("Votre panier est vide pour le moment.");
            vide.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px;");
            vboxPanier.getChildren().add(vide);
        }

        // 4. Parcourir le panier et créer une ligne pour chaque plat unique
        for (Plat p : toutLePanier) {
            if (!idsTraites.contains(p.getId())) {
                idsTraites.add(p.getId());
                
                // On récupère la quantité réelle
                int qte = MockService.getInstance().getQuantiteDuPlat(p);
                
                // Créer la ligne visuelle
                vboxPanier.getChildren().add(creerLigneProduit(p, qte));
            }
        }

        // 5. Afficher le total global
        double total = MockService.getInstance().getTotalPanier();
        lblTotalFinal.setText(String.format("%.2f €", total));
    }

    private HBox creerLigneProduit(Plat p, int qte) {
        // Conteneur de la ligne (Style "Carte en verre")
        HBox ligne = new HBox(20);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: rgba(30, 41, 59, 0.6); -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 15;");
        ligne.setPrefHeight(100);

        // Image (Petite vignette)
        ImageView img = new ImageView();
        img.setFitHeight(80);
        img.setFitWidth(100);
        img.setPreserveRatio(true);
        try {
            String path = "/fr/netwok/images/" + p.getImagePath();
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) { /* Pas d'image, tant pis */ }

        // Nom du produit
        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        nom.setMinWidth(300);

        // Quantité (ex: "x 2")
        Label lblQte = new Label("x " + qte);
        lblQte.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 20px;");
        
        // Espace flexible
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Prix total pour cette ligne (Prix unitaire * Quantité)
        double sousTotal = p.getPrix() * qte;
        Label prix = new Label(String.format("%.2f €", sousTotal));
        prix.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");

        // Bouton Supprimer (Croix rouge)
        Button btnSupprimer = new Button("X");
        btnSupprimer.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF007F; -fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand; -fx-border-color: #FF007F; -fx-border-radius: 50%;");
        btnSupprimer.setOnAction(e -> {
            // Retire TOUS les exemplaires de ce produit
            while(MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
            }
            // Recharge la page pour mettre à jour
            chargerPanier(); 
        });

        // Assemblage
        ligne.getChildren().addAll(img, nom, lblQte, spacer, prix, btnSupprimer);
        return ligne;
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