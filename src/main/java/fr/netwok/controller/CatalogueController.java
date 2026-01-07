package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    @FXML private Label lblMonPanierTitre;
    @FXML private Label languageDisplay;
    @FXML private HBox sousCategorieBar;
    @FXML private ScrollPane scrollPane;
    @FXML private Label sectionTitle;

    // Correspondance avec les ToggleButtons du FXML
    @FXML private ToggleButton tabEntrees;
    @FXML private ToggleButton tabPlats;
    @FXML private ToggleButton tabDesserts;
    @FXML private ToggleButton tabAllDesserts;

    // Bouton d'action
    @FXML private Button btnVoirPanier;

    private int categorieActuelle = 1;
    private static String langueActuelle = "FR";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            MockService.getInstance().rafraichirCatalogue();
        } catch (Exception e) {
            System.err.println("Erreur chargement catalogue: " + e.getMessage());
        }

        updatePanierDisplay();
        toggleSousCategorie(false);
        if (sectionTitle != null) {
            sectionTitle.setVisible(false);
            sectionTitle.setManaged(false);
        }
        afficherPlats(1);
    }

    // --- NAVIGATION ---

    @FXML void filtrerEntrees() {
        this.categorieActuelle = 1;
        toggleSousCategorie(false);
        afficherPlats(1);
        resetScroll();
    }

    @FXML void filtrerPlats() {
        this.categorieActuelle = 2;
        toggleSousCategorie(false);
        afficherPlats(2);
        resetScroll();
    }

    @FXML void filtrerDesserts() {
        this.categorieActuelle = 3;
        toggleSousCategorie(true);
        if (tabAllDesserts != null) tabAllDesserts.setSelected(true);
        afficherPlatsDessertsBoissons(0);
        resetScroll();
    }

    @FXML void filtrerDessertsOnly() { afficherPlatsDessertsBoissons(3); }
    @FXML void filtrerBoissonsOnly() { afficherPlatsDessertsBoissons(4); }
    @FXML void filtrerAllDessertsBoissons() { afficherPlatsDessertsBoissons(0); }

    // --- LOGIQUE D'AFFICHAGE ---

    private void afficherPlats(int categorie) {
        gridPlats.getChildren().clear();

        List<Plat> plats = MockService.getInstance().getPlatsParCategorie(categorie);
        for (Plat p : plats) {
            gridPlats.getChildren().add(creerCarteVBox(p));
        }
    }

    private void afficherPlatsDessertsBoissons(int mode) {
        gridPlats.getChildren().clear();
        updateSectionTitle(mode);
        if (mode == 3) {
            afficherSectionAvecTitre(ui("Desserts", "Desserts"), MockService.getInstance().getPlatsParCategorie(3));
        } else if (mode == 4) {
            afficherSectionAvecTitre(ui("Boissons", "Drinks"), MockService.getInstance().getPlatsParCategorie(4));
        } else {
            afficherSectionAvecTitre(ui("Desserts", "Desserts"), MockService.getInstance().getPlatsParCategorie(3));
            afficherSectionAvecTitre(ui("Boissons", "Drinks"), MockService.getInstance().getPlatsParCategorie(4));
        }
    }

    private void afficherSectionAvecTitre(String titre, List<Plat> plats) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        section.setStyle("-fx-padding: 20 0 20 0;");
        section.setMaxWidth(Double.MAX_VALUE);

        Label sectionLabel = new Label(titre.toUpperCase());
        sectionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        FlowPane carteContainer = new FlowPane(30, 30);
        carteContainer.setAlignment(Pos.TOP_CENTER);
        carteContainer.setPrefWrapLength(1200);
        plats.forEach(p -> carteContainer.getChildren().add(creerCarteVBox(p)));

        section.getChildren().addAll(sectionLabel, carteContainer);
        gridPlats.getChildren().add(section);
    }

    private VBox creerCarteVBox(Plat p) {
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(280);
        carte.setAlignment(Pos.CENTER);

        // TRADUCTION DYNAMIQUE DU CONTENU
        String nomAffiche = p.getNom();
        String descAffiche = p.getDescription();
        if (!"FR".equals(langueActuelle)) {
            String tNom = getTraductionProduit(p.getId(), "nom");
            String tDesc = getTraductionProduit(p.getId(), "desc");
            if (tNom != null) nomAffiche = tNom;
            if (tDesc != null) descAffiche = tDesc;
        }

        // Image
        ImageView imgView = new ImageView();
        imgView.setFitHeight(180); imgView.setFitWidth(240); imgView.setPreserveRatio(true);
        try {
            String fullPath = "/fr/netwok/images/" + p.getImagePath();
            URL res = getClass().getResource(fullPath);
            if (res != null) imgView.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) { e.printStackTrace(); }

        // Textes
        Label name = new Label(nomAffiche);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        name.setWrapText(true); name.setTextAlignment(TextAlignment.CENTER);

        Label desc = new Label(descAffiche);
        desc.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        desc.setWrapText(true); desc.setTextAlignment(TextAlignment.CENTER);

        Label price = new Label(String.format("%.2f €", p.getPrix()));
        price.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox quantityBox = creerSelecteurQuantite(p);

        carte.setOnMouseClicked(e -> ouvrirDetailPlat(p));
        carte.getChildren().addAll(imgView, name, desc, price, quantityBox);
        return carte;
    }

    private HBox creerSelecteurQuantite(Plat p) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        Label lblQty = new Label(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
        lblQty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Button btnMinus = new Button("-");
        btnMinus.setOnAction(e -> {
            if (MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
                lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
                updatePanierDisplay();
            }
        });

        Button btnPlus = new Button("+");
        btnPlus.setOnAction(e -> {
            MockService.getInstance().ajouterAuPanier(p);
            lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
            updatePanierDisplay();
        });

        box.getChildren().addAll(btnMinus, lblQty, btnPlus);
        return box;
    }

    // --- GESTION DES LANGUES ---

    @FXML
    void changeLanguage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        langueActuelle = btn.getText().toUpperCase();

        // 1. Traduction des onglets (Navigation)
        if (tabEntrees != null) tabEntrees.setText(ui("ENTRÉES", "STARTERS"));
        if (tabPlats != null) tabPlats.setText(ui("PLATS", "MAINS"));
        if (tabDesserts != null) tabDesserts.setText(ui("DESSERTS / BOISSONS", "DESSERTS / DRINKS"));

        // 2. Traduction de la barre de panier
        if (lblMonPanierTitre != null) lblMonPanierTitre.setText(ui("MON PANIER", "MY BASKET"));
        if (btnVoirPanier != null) btnVoirPanier.setText(ui("VOIR PANIER >", "VIEW BASKET >"));
        if (languageDisplay != null) languageDisplay.setText(ui("Langue :", "Language:"));

        updatePanierDisplay();

        // 3. Rafraîchir les cartes produits
        refreshView();
    }

    // Helper pour basculer facilement entre les deux langues
    private String ui(String fr, String en) {
        return "FR".equals(langueActuelle) ? fr : en;
    }

    private String getTraductionProduit(String id, String type) {
        if ("FR".equals(langueActuelle)) return null;
        return switch (id) {
            case "B1" -> type.equals("nom") ? "Iced Tea" : "Homemade, lime";
            case "B2" -> type.equals("nom") ? "Tsingtao Beer" : "Lager beer 33cl";
            case "B3" -> type.equals("nom") ? "Jap Lemonade" : "Ramune with marble";
            case "B4" -> type.equals("nom") ? "Coconut Juice" : "With chunks";
            case "B5" -> type.equals("nom") ? "Sake" : "Small pitcher";
            case "D1" -> type.equals("nom") ? "Coconut Pearls" : "2 pieces, warm";
            case "D2" -> type.equals("nom") ? "Iced Mochi" : "2 pieces, flavor of choice";
            case "D3" -> type.equals("nom") ? "Fresh Mango" : "Mango slices";
            case "D4" -> type.equals("nom") ? "Flambé Banana" : "With sake";
            case "D5" -> type.equals("nom") ? "Chinese Nougat" : "With sesame seeds";
            case "E1" -> type.equals("nom") ? "Chicken Nems" : "4 pieces, fish sauce";
            case "E10" -> type.equals("nom") ? "Dim Sum Mix" : "Steamed basket (6 pieces)";
            case "E2" -> type.equals("nom") ? "Spring Rolls" : "Shrimp, mint, rice";
            case "E3" -> type.equals("nom") ? "Chicken Gyozas" : "Grilled dumplings (5 pieces)";
            case "E4" -> type.equals("nom") ? "Beef Samoussas" : "Crispy with spices";
            case "E5" -> type.equals("nom") ? "Cabbage Salad" : "White cabbage, sesame marinade";
            case "E6" -> type.equals("nom") ? "Miso Soup" : "Tofu, wakame seaweed";
            case "E7" -> type.equals("nom") ? "Shrimp Tempura" : "Light fritters (4 pieces)";
            case "E8" -> type.equals("nom") ? "Beef Yakitori" : "Beef-cheese skewers";
            case "E9" -> type.equals("nom") ? "Edamame" : "Soybeans, sea salt";
            case "P1" -> type.equals("nom") ? "Pad Thai" : "Rice noodles, shrimp";
            case "P10" -> type.equals("nom") ? "Vege Wok" : "Noodles, tofu";
            case "P2" -> type.equals("nom") ? "Beef Bo Bun" : "Vermicelli, sautéed beef";
            case "P3" -> type.equals("nom") ? "Green Curry" : "Chicken, coconut milk";
            case "P4" -> type.equals("nom") ? "Cantonese Rice" : "Fried rice, ham";
            case "P5" -> type.equals("nom") ? "Caramel Pork" : "Candied ribs";
            case "P6" -> type.equals("nom") ? "Peking Duck" : "With pancakes";
            case "P7" -> type.equals("nom") ? "Bibimbap" : "Rice, beef, vegetables";
            case "P8" -> type.equals("nom") ? "Tonkotsu Ramen" : "Pork broth, noodles";
            case "P9" -> type.equals("nom") ? "Sushi Mix 12" : "Sushi assortment";
            default -> null;
        };
    }

    // --- MISE À JOUR PANIER ---

    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();

        // Traduction de l'unité article(s) / item(s)
        String unit = (nb > 1) ? ui("articles", "items") : ui("article", "item");

        lblNbArticles.setText(nb + " " + unit);
        lblTotal.setText(String.format("%.2f €", total));
    }
    private void refreshView() {
        if (categorieActuelle == 1) afficherPlats(1);
        else if (categorieActuelle == 2) afficherPlats(2);
        else afficherPlatsDessertsBoissons(0);
    }
    // --- UTILITAIRES ---

    private void toggleSousCategorie(boolean show) {
        if (sousCategorieBar != null) {
            sousCategorieBar.setVisible(show);
            sousCategorieBar.setManaged(show);
        }
    }

    private void resetScroll() { if (scrollPane != null) scrollPane.setVvalue(0); }

    private void updateSectionTitle(int mode) {
        if (sectionTitle == null) return;
        sectionTitle.setVisible(true); sectionTitle.setManaged(true);
        switch (mode) {
            case 3 -> sectionTitle.setText(ui("Desserts", "Desserts"));
            case 4 -> sectionTitle.setText(ui("Boissons", "Drinks"));
            default -> sectionTitle.setText(ui("Desserts & Boissons", "Desserts & Drinks"));
        }
    }

    private void ouvrirDetailPlat(Plat p) {
        try {
            DetailPlatController.setPlatAfficher(p);
            NetwokApp.setRoot("views/detailPlat");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML void voirPanier() {
        try { NetwokApp.setRoot("views/panier"); } catch (IOException e) { e.printStackTrace(); }
    }
}