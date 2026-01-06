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
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
    @FXML private Label sectionTitle;

    // N'oubliez pas de mettre ces fx:id sur vos boutons de navigation dans Scene Builder
    @FXML private Button btnNavEntrees;
    @FXML private Button btnNavPlats;
    @FXML private Button btnNavDesserts;
    @FXML private Button btnVoirPanier;

    private String langueActuelle = "FR";
    private int categorieActuelle = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            MockService.getInstance().rafraichirCatalogue();
        } catch (Exception e) {
            System.err.println("Erreur chargement : " + e.getMessage());
        }
        updatePanierDisplay();
        filtrerEntrees(); // Affiche les entrées par défaut
    }

    // --- LOGIQUE DE TRADUCTION ---

    private String ui(String fr, String en) {
        return "FR".equals(langueActuelle) ? fr : en;
    }

    private String getTraductionProduit(String id, String type) {
        if ("FR".equals(langueActuelle)) return null;

        return switch (id) {
            // Boissons (B)
            case "B1" -> type.equals("nom") ? "Iced Tea" : "Homemade, lime";
            case "B2" -> type.equals("nom") ? "Tsingtao Beer" : "Lager beer 33cl";
            case "B3" -> type.equals("nom") ? "Jap Lemonade" : "Ramune with marble";
            case "B4" -> type.equals("nom") ? "Coconut Juice" : "With chunks";
            case "B5" -> type.equals("nom") ? "Sake" : "Small pitcher";
            // Desserts (D)
            case "D1" -> type.equals("nom") ? "Coconut Pearls" : "2 pieces, warm";
            case "D2" -> type.equals("nom") ? "Iced Mochi" : "2 pieces, flavor of choice";
            case "D3" -> type.equals("nom") ? "Fresh Mango" : "Mango slices";
            case "D4" -> type.equals("nom") ? "Flambé Banana" : "With sake";
            case "D5" -> type.equals("nom") ? "Chinese Nougat" : "With sesame seeds";
            // Entrées (E)
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
            // Plats (P)
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

    @FXML
    void changeLanguage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        this.langueActuelle = btn.getText().toUpperCase();

        // 1. Traduire les boutons de navigation
        if (btnNavEntrees != null) btnNavEntrees.setText(ui("ENTRÉES", "STARTERS"));
        if (btnNavPlats != null) btnNavPlats.setText(ui("PLATS", "MAIN COURSES"));
        if (btnNavDesserts != null) btnNavDesserts.setText(ui("DESSERTS", "DESSERTS"));
        if (btnVoirPanier != null) btnVoirPanier.setText(ui("VOIR MON PANIER", "VIEW MY BASKET"));

        // 2. Traduire le récapitulatif du bas
        updatePanierDisplay();

        // 3. Rafraîchir la vue de contenu
        if (categorieActuelle == 1) filtrerEntrees();
        else if (categorieActuelle == 2) filtrerPlats();
        else filtrerDesserts();
    }

    // --- NAVIGATION ---

    @FXML void filtrerEntrees() {
        categorieActuelle = 1;
        toggleSousCategorie(false);
        updateSectionTitle(1);
        afficherPlatsSimple(1);
    }

    @FXML void filtrerPlats() {
        categorieActuelle = 2;
        toggleSousCategorie(false);
        updateSectionTitle(2);
        afficherPlatsSimple(2);
    }

    @FXML void filtrerDesserts() {
        categorieActuelle = 3;
        toggleSousCategorie(true);
        if (tabAllDesserts != null) tabAllDesserts.setSelected(true);
        afficherPlatsDessertsBoissons(0);
    }

    private void updateSectionTitle(int code) {
        if (sectionTitle == null) return;
        sectionTitle.setVisible(true);
        sectionTitle.setManaged(true);
        String titre = switch (code) {
            case 1 -> ui("NOS ENTRÉES", "OUR STARTERS");
            case 2 -> ui("NOS PLATS", "OUR MAIN COURSES");
            case 3 -> ui("NOS DESSERTS", "OUR DESSERTS");
            case 4 -> ui("NOS BOISSONS", "OUR DRINKS");
            default -> ui("DESSERTS & BOISSONS", "DESSERTS & DRINKS");
        };
        sectionTitle.setText(titre);
    }

    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();
        lblNbArticles.setText(nb + " " + ui("articles", "items"));
        lblTotal.setText(String.format(ui("Total : %.2f €", "Total: %.2f €"), total));
    }

    // --- LOGIQUE D'AFFICHAGE ---

    private void afficherPlatsSimple(int cat) {
        gridPlats.getChildren().clear();
        MockService.getInstance().getPlatsParCategorie(cat).forEach(p -> gridPlats.getChildren().add(creerCarteVBox(p)));
    }

    private void afficherPlatsDessertsBoissons(int mode) {
        gridPlats.getChildren().clear();
        updateSectionTitle(mode);
        if (mode == 3) {
            afficherZoneInterne(ui("DESSERTS", "DESSERTS"), 3);
        } else if (mode == 4) {
            afficherZoneInterne(ui("BOISSONS", "DRINKS"), 4);
        } else {
            afficherZoneInterne(ui("DESSERTS", "DESSERTS"), 3);
            afficherZoneInterne(ui("BOISSONS", "DRINKS"), 4);
        }
    }

    private void afficherZoneInterne(String titre, int cat) {
        VBox zone = new VBox(10);
        zone.setAlignment(Pos.TOP_CENTER);
        Label l = new Label(titre);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        FlowPane fp = new FlowPane(25, 25);
        fp.setAlignment(Pos.TOP_CENTER);
        fp.setPrefWrapLength(1100);
        MockService.getInstance().getPlatsParCategorie(cat).forEach(p -> fp.getChildren().add(creerCarteVBox(p)));
        zone.getChildren().addAll(l, fp);
        gridPlats.getChildren().add(zone);
    }

    private VBox creerCarteVBox(Plat p) {
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(260);
        carte.setAlignment(Pos.CENTER);

        ImageView iv = new ImageView();
        iv.setFitHeight(160); iv.setFitWidth(220); iv.setPreserveRatio(true);
        try {
            URL res = getClass().getResource("/fr/netwok/images/" + p.getImagePath());
            if (res != null) iv.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}

        // Traduction Nom/Desc
        String n = getTraductionProduit(p.getId(), "nom");
        String d = getTraductionProduit(p.getId(), "desc");

        Label name = new Label(n != null ? n : p.getNom());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        name.setWrapText(true); name.setTextAlignment(TextAlignment.CENTER);

        Label desc = new Label(d != null ? d : p.getDescription());
        desc.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        desc.setWrapText(true); desc.setTextAlignment(TextAlignment.CENTER);

        Label price = new Label(String.format("%.2f €", p.getPrix()));
        price.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox qBox = new HBox(15);
        qBox.setAlignment(Pos.CENTER);
        Button btnM = new Button("-");
        Label lblQ = new Label(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
        lblQ.setStyle("-fx-text-fill: white;");
        Button btnP = new Button("+");

        btnP.setOnAction(e -> {
            MockService.getInstance().ajouterAuPanier(p);
            lblQ.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
            updatePanierDisplay();
        });
        btnM.setOnAction(e -> {
            if(MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
                lblQ.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
                updatePanierDisplay();
            }
        });

        qBox.getChildren().addAll(btnM, lblQ, btnP);
        carte.getChildren().addAll(iv, name, desc, price, qBox);

        carte.setOnMouseClicked(e -> { if(e.getClickCount() == 1) ouvrirDetail(p); });
        return carte;
    }

    private void ouvrirDetail(Plat p) {
        try {
            // DetailPlatController.setPlatAfficher(p);
            NetwokApp.setRoot("views/detailPlat");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void toggleSousCategorie(boolean show) {
        if (sousCategorieBar != null) {
            sousCategorieBar.setVisible(show);
            sousCategorieBar.setManaged(show);
        }
    }

    @FXML void filtrerDessertsOnly() { afficherPlatsDessertsBoissons(3); }
    @FXML void filtrerBoissonsOnly() { afficherPlatsDessertsBoissons(4); }
    @FXML void filtrerAllDessertsBoissons() { afficherPlatsDessertsBoissons(0); }
    @FXML void voirPanier() { try { NetwokApp.setRoot("views/panier"); } catch (IOException e) { e.printStackTrace(); } }
}