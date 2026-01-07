package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.ApiClient;
import fr.netwok.service.MockService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PanierController implements Initializable {

    @FXML private VBox vboxArticles;
    @FXML private Label lblSousTotal, lblTaxes, lblTotalFinal;
    @FXML private Label lblTitreRecap, colArticle, colQuantite, colPrix, colTotal;
    @FXML private Label lblTxtSousTotal, lblTxtTaxes, lblTxtTable, lblTxtNom, languageDisplay;
    @FXML private TextField txtNumeroTable, txtNomClient;
    @FXML private Button btnConfirmer, btnRetour, btnModifier;

    private String currentLanguage = "FR";
    private static final double TAUX_TAXE = 0.15;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        traduire();
        chargerPanier();
    }

    @FXML
    void changeLanguage(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        currentLanguage = btn.getText().toUpperCase(); // Récupère "FR" ou "EN"
        traduire();
        chargerPanier(); // Indispensable pour rafraîchir les noms des produits
    }

    private void traduire() {
        boolean isFR = currentLanguage.equals("FR");

        // Interface statique
        lblTitreRecap.setText(isFR ? "Récapitulatif de commande" : "Order Summary");
        colArticle.setText(isFR ? "Article" : "Item");
        colQuantite.setText(isFR ? "Quantité" : "Quantity");
        colPrix.setText(isFR ? "Prix Unit." : "Unit Price");
        colTotal.setText(isFR ? "Total" : "Total");

        lblTxtSousTotal.setText(isFR ? "Sous-total :" : "Subtotal:");
        lblTxtTaxes.setText(isFR ? "Taxes (15%) :" : "Taxes (15%):");

        lblTxtTable.setText(isFR ? "Numéro de table :" : "Table Number:");
        lblTxtNom.setText(isFR ? "Nom du client (optionnel) :" : "Client Name (optional):");
        txtNumeroTable.setPromptText(isFR ? "Ex: 12" : "e.g. 12");
        txtNomClient.setPromptText(isFR ? "Ex: Martin Dupont" : "e.g. John Doe");

        btnRetour.setText(isFR ? "← Retour" : "← Back");
        btnModifier.setText(isFR ? "← Modifier" : "← Edit");
        btnConfirmer.setText(isFR ? "Confirmer la commande" : "Confirm Order");

        languageDisplay.setText(isFR ? "Langue : FR" : "Language : EN");
    }

    private String getTraductionProduit(String id, String type) {
        if ("FR".equals(currentLanguage)) return null; // Le modèle Plat contient déjà les noms en FR

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

    private void chargerPanier() {
        vboxArticles.getChildren().clear();
        List<Plat> toutLePanier = MockService.getInstance().getPanier();
        Set<String> idsTraites = new HashSet<>();

        if (toutLePanier.isEmpty()) {
            String msg = currentLanguage.equals("FR") ? "Votre panier est vide." : "Your cart is empty.";
            Label vide = new Label(msg);
            vide.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px; -fx-padding: 40 0 40 0;");
            vboxArticles.getChildren().add(vide);
            btnConfirmer.setDisable(true);
        } else {
            btnConfirmer.setDisable(false);
            int index = 0;
            for (Plat p : toutLePanier) {
                if (!idsTraites.contains(p.getId())) {
                    idsTraites.add(p.getId());
                    int qte = MockService.getInstance().getQuantiteDuPlat(p);
                    VBox ligneArticle = creerLigneArticle(p, qte);

                    ligneArticle.setOpacity(0);
                    FadeTransition fade = new FadeTransition(Duration.millis(400), ligneArticle);
                    fade.setToValue(1);
                    fade.setDelay(Duration.millis(index * 100));
                    fade.play();

                    vboxArticles.getChildren().add(ligneArticle);
                    index++;
                }
            }
        }
        animerTotaux();
    }

    private VBox creerLigneArticle(Plat p, int qte) {
        VBox ligneComplete = new VBox(8);
        ligneComplete.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4); -fx-background-radius: 12; -fx-padding: 15;");

        HBox lignePrincipale = new HBox(20);
        lignePrincipale.setAlignment(Pos.CENTER_LEFT);

        // Image
        ImageView imgPlat = new ImageView();
        imgPlat.setFitWidth(80); imgPlat.setFitHeight(80); imgPlat.setPreserveRatio(true);
        try {
            String fullPath = "/fr/netwok/images/" + p.getImagePath();
            URL res = getClass().getResource(fullPath);
            if (res != null) imgPlat.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}

        // --- TRADUCTION DYNAMIQUE DU PRODUIT ---
        String nomAffiche = getTraductionProduit(p.getId(), "nom");
        if (nomAffiche == null) nomAffiche = p.getNom(); // Fallback vers le nom d'origine (FR)

        String descAffiche = getTraductionProduit(p.getId(), "desc");
        if (descAffiche == null) descAffiche = currentLanguage.equals("FR") ? "Accompagnement au choix" : "Choice of side dish";

        // Info
        VBox colInfo = new VBox(5);
        colInfo.setPrefWidth(350);
        Label lblNom = new Label(nomAffiche);
        lblNom.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblOptions = new Label(descAffiche);
        lblOptions.setStyle("-fx-text-fill: #94a3b8;");
        colInfo.getChildren().addAll(lblNom, lblOptions);

        // Quantité
        HBox boxQuantite = new HBox(10);
        boxQuantite.setAlignment(Pos.CENTER);
        boxQuantite.setPrefWidth(130);
        Button btnMoins = new Button("-");
        btnMoins.setStyle("-fx-text-fill: #00F0FF; -fx-background-color: transparent; -fx-border-color: #00F0FF; -fx-border-radius: 5; -fx-cursor: hand;");
        Label lblQte = new Label(String.valueOf(qte));
        lblQte.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-text-fill: #00F0FF; -fx-background-color: transparent; -fx-border-color: #00F0FF; -fx-border-radius: 5; -fx-cursor: hand;");

        btnMoins.setOnAction(e -> { MockService.getInstance().retirerDuPanier(p); chargerPanier(); });
        btnPlus.setOnAction(e -> { MockService.getInstance().ajouterAuPanier(p); chargerPanier(); });

        boxQuantite.getChildren().addAll(btnMoins, lblQte, btnPlus);

        // Prix
        Label lblPrixUnit = new Label(String.format("%.2f€", p.getPrix()));
        lblPrixUnit.setStyle("-fx-text-fill: #94a3b8;");
        lblPrixUnit.setPrefWidth(100); lblPrixUnit.setAlignment(Pos.CENTER_RIGHT);

        Label lblTotalRow = new Label(String.format("%.2f€", p.getPrix() * qte));
        lblTotalRow.setStyle("-fx-text-fill: #00F0FF; -fx-font-weight: bold;");
        lblTotalRow.setPrefWidth(120); lblTotalRow.setAlignment(Pos.CENTER_RIGHT);

        // Supprimer
        Button btnSuppr = new Button("✕");
        btnSuppr.setStyle("-fx-text-fill: #FF007F; -fx-background-color: transparent; -fx-cursor: hand;");
        btnSuppr.setOnAction(e -> {
            while(MockService.getInstance().getQuantiteDuPlat(p) > 0) MockService.getInstance().retirerDuPanier(p);
            chargerPanier();
        });

        lignePrincipale.getChildren().addAll(imgPlat, colInfo, boxQuantite, lblPrixUnit, lblTotalRow, btnSuppr);
        ligneComplete.getChildren().add(lignePrincipale);
        return ligneComplete;
    }

    private void animerTotaux() {
        double sousTotal = MockService.getInstance().getTotalPanier();
        double taxes = sousTotal * TAUX_TAXE;
        lblSousTotal.setText(String.format("%.2f€", sousTotal));
        lblTaxes.setText(String.format("%.2f€", taxes));
        lblTotalFinal.setText(String.format("%.2f€", sousTotal + taxes));
    }

    @FXML
    void confirmerCommande() {
        if (txtNumeroTable.getText().isEmpty()) {
            txtNumeroTable.setStyle("-fx-border-color: #FF007F;");
            return;
        }
        // Simulation d'envoi
        btnConfirmer.setText(currentLanguage.equals("FR") ? "✓ Commande envoyée" : "✓ Order sent");
        btnConfirmer.setDisable(true);
    }

    @FXML
    void retourCatalogue() throws IOException {
        NetwokApp.setRoot("views/catalogue");
    }
}