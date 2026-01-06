package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {

    @FXML private FlowPane gridPlats;
    @FXML private Label lblNbArticles; 
    @FXML private Label lblTotal;
    @FXML private HBox sousCategorieBar;
    @FXML private ToggleButton tabAllDesserts;
    @FXML private ToggleButton tabDessertsOnly;
    @FXML private ToggleButton tabBoissonsOnly;
    @FXML private Label sectionTitle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // On entoure par un try-catch pour éviter que l'appli ne se bloque
        try {
            MockService.getInstance().rafraichirCatalogue();
        } catch (Exception e) {
            System.err.println("Le catalogue n'a pas pu être chargé : " + e.getMessage());
        }

        updatePanierDisplay();
        if (sousCategorieBar != null) {
            sousCategorieBar.setVisible(false);
            sousCategorieBar.setManaged(false);
        }
        if (sectionTitle != null) {
            sectionTitle.setVisible(false);
            sectionTitle.setManaged(false);
        }
        afficherPlats(1);
    }

    @FXML void filtrerEntrees() {
        toggleSousCategorie(false);
        afficherPlats(1);
    }

    @FXML void filtrerPlats() {
        toggleSousCategorie(false);
        afficherPlats(2);
    }
    
    @FXML
    void filtrerDesserts() {
        toggleSousCategorie(true);
        if (tabAllDesserts != null && !tabAllDesserts.isSelected()) {
            tabAllDesserts.setSelected(true);
        }
        afficherPlatsDessertsBoissons(0);
    }

    @FXML
    void filtrerDessertsOnly() {
        afficherPlatsDessertsBoissons(3);
    }

    @FXML
    void filtrerBoissonsOnly() {
        afficherPlatsDessertsBoissons(4);
    }

    @FXML
    void filtrerAllDessertsBoissons() {
        afficherPlatsDessertsBoissons(0);
    }
    
    @FXML
    void voirPanier() {
        try {
            NetwokApp.setRoot("views/panier");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Ouvre l'écran de détail du plat
    private void ouvrirDetailPlat(Plat p) {
        try {
            DetailPlatController.setPlatAfficher(p);
            NetwokApp.setRoot("views/detailPlat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Met à jour la barre du bas (Total et Nombre d'articles)
    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();
        
        lblNbArticles.setText(nb + " articles");
        lblTotal.setText(String.format("%.2f €", total));
    }

    private void afficherPlats(int categorie) {
        gridPlats.getChildren().clear();
        List<Plat> plats = MockService.getInstance().getPlatsParCategorie(categorie);

        // DEBUG VITAL : Est-ce que la liste contient quelque chose ?
        System.out.println("🔍 Tentative d'affichage pour : " + categorie);
        System.out.println("📊 Nombre de plats trouvés dans cette catégorie : " + plats.size());

        if (plats.isEmpty()) {
            System.out.println("⚠️ Attention : Aucun plat trouvé. Vérifiez l'orthographe dans MySQL !");
        }

        for (Plat p : plats) {
            creerCartePlat(p);
        }
    }

    private void afficherPlatsDessertsBoissons(int mode) {
        gridPlats.getChildren().clear();
        updateSectionTitle(mode);

        // MODE DESSERT (ID 3)
        if (mode == 3) {
            afficherSectionAvecTitre("Desserts", MockService.getInstance().getPlatsParCategorie(3));
            return;
        }

        // MODE BOISSON (ID 4)
        if (mode == 4) {
            afficherSectionAvecTitre("Boissons", MockService.getInstance().getPlatsParCategorie(4));
            return;
        }

        // MODE TOUT (Si mode n'est ni 3 ni 4, on affiche les deux sections)
        afficherSectionAvecTitre("Desserts", MockService.getInstance().getPlatsParCategorie(3));
        afficherSectionAvecTitre("Boissons", MockService.getInstance().getPlatsParCategorie(4));
    }

    private void afficherSectionAvecTitre(String titre, List<Plat> plats) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        section.setStyle("-fx-padding: 20 0 20 0;");
        section.setMaxWidth(Double.MAX_VALUE);

        // Titre au-dessus - grand et blanc
        Label sectionLabel = new Label(titre.toUpperCase());
        sectionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        // FlowPane avec les cartes en dessous
        FlowPane carteContainer = new FlowPane(30, 30);
        carteContainer.setAlignment(Pos.TOP_CENTER);
        carteContainer.setPrefWrapLength(1200);
        plats.forEach(p -> carteContainer.getChildren().add(creerCarteVBox(p)));

        VBox.setVgrow(carteContainer, Priority.ALWAYS);
        section.getChildren().addAll(sectionLabel, carteContainer);
        
        gridPlats.getChildren().add(section);
    }

    private void updateSectionTitle(int mode) {
        if (sectionTitle == null) return;
        sectionTitle.setVisible(true);
        sectionTitle.setManaged(true);

        switch (mode) {
            case 3 -> sectionTitle.setText("Desserts");
            case 4 -> sectionTitle.setText("Boissons");
            default -> sectionTitle.setText("Desserts & Boissons");
        }
    }

    // Petit séparateur visuel façon apps de commande
    private HBox creerSectionHeader(String titre) {
        Label lbl = new Label(titre.toUpperCase());
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: #00f0ff; -fx-min-height: 2px; -fx-max-height: 2px;");
        HBox.setHgrow(line, Priority.ALWAYS);

        HBox box = new HBox(12, lbl, line);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 10 0 5 0;");
        box.setMaxWidth(900);
        return box;
    }

    private void toggleSousCategorie(boolean show) {
        if (sousCategorieBar != null) {
            sousCategorieBar.setVisible(show);
            sousCategorieBar.setManaged(show);
        }
    }

    private void creerCartePlat(Plat p) {
        gridPlats.getChildren().add(creerCarteVBox(p));
    }

    private VBox creerCarteVBox(Plat p) {
        // 1. Le conteneur principal (VBox)
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(280);
        carte.setAlignment(Pos.CENTER);
        
        // Rendre la carte cliquable pour voir le détail
        carte.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) { // Simple clic
                // Animation de clic
                ScaleTransition scale = new ScaleTransition(Duration.millis(150), carte);
                scale.setFromX(1.0);
                scale.setFromY(1.0);
                scale.setToX(0.95);
                scale.setToY(0.95);
                
                scale.setOnFinished(event -> ouvrirDetailPlat(p));
                scale.play();
            }
        });
        carte.setStyle(carte.getStyle() + "-fx-cursor: hand;");

        // 2. L'Image avec sécurité
        ImageView imgView = new ImageView();
        imgView.setFitHeight(180);
        imgView.setFitWidth(240);
        imgView.setPreserveRatio(true);

        try {
            String imagePath = p.getImagePath();
            if (imagePath == null || imagePath.isEmpty()) {
                System.err.println("❌ L'ID " + p.getId() + " n'a pas de chemin d'image en BDD.");
            } else {
                String fullPath = "/fr/netwok/images/" + imagePath;
                URL res = getClass().getResource(fullPath);

                if (res != null) {
                    imgView.setImage(new Image(res.toExternalForm()));
                } else {
                    // C'EST CETTE LIGNE QUI VA TE DONNER LA SOLUTION
                    System.err.println("⚠️ Fichier introuvable : " + fullPath);
                    System.err.println("   -> Vérifiez que le fichier existe bien dans src/main/resources" + fullPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Les Textes (Utilisation des nouveaux getters)
        Label name = new Label(p.getNom()); // ou p.getName() selon ton Plat.java
        name.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setTextAlignment(TextAlignment.CENTER);

        Label desc = new Label(p.getDescription());
        desc.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        desc.setWrapText(true);
        desc.setTextAlignment(TextAlignment.CENTER);

        Label price = new Label(String.format("%.2f €", p.getPrix())); // ou p.getPrice()
        price.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 24px; -fx-font-weight: bold;");

        // 4. Sélecteur de quantité
        int qteActuelle = MockService.getInstance().getQuantiteDuPlat(p);

        HBox quantityBox = new HBox(15);
        quantityBox.setAlignment(Pos.CENTER);

        Button btnMinus = new Button("-");
        btnMinus.setStyle("-fx-cursor: hand;");

        Label lblQty = new Label(String.valueOf(qteActuelle));
        lblQty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-cursor: hand;");

        btnPlus.setOnAction(e -> {
            MockService.getInstance().ajouterAuPanier(p);
            lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
            updatePanierDisplay();
            
            // Animation du bouton +
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btnPlus);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.3);
            scale.setToY(1.3);
            scale.play();
            
            // Animation du label quantité
            ScaleTransition scaleQty = new ScaleTransition(Duration.millis(200), lblQty);
            scaleQty.setFromX(0.8);
            scaleQty.setFromY(0.8);
            scaleQty.setToX(1.1);
            scaleQty.setToY(1.1);
            scaleQty.play();
        });

        btnMinus.setOnAction(e -> {
            if (MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
                lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
                updatePanierDisplay();
                
                // Animation du bouton -
                ScaleTransition scale = new ScaleTransition(Duration.millis(150), btnMinus);
                scale.setFromX(1.0);
                scale.setFromY(1.0);
                scale.setToX(1.3);
                scale.setToY(1.3);
                scale.play();
            }
        });

        quantityBox.getChildren().addAll(btnMinus, lblQty, btnPlus);

        // 5. Assemblage
        carte.getChildren().addAll(imgView, name, desc, price, quantityBox);
        return carte;
    }
}