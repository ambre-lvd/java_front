package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class RecuCommandeController implements Initializable {

    @FXML private Label lblTitreRecu, lblNumCommande, lblNumTable, lblNomClient, lblHeure;
    @FXML private Label lblTxtTable, lblTxtNom, lblTxtHeure, lblTxtSousTotal, lblTxtTaxes, lblTxtTotal;
    @FXML private Label lblTxtNumCommande;
    @FXML private Label lblSousTotal, lblTaxes, lblTotal, lblMerci, lblAuRevoir;
    @FXML private VBox vboxArticlesTicket, vboxMessage;

    private static String numeroCommande = "";
    private static int numeroTable = 0;
    private static String nomClient = "";
    private static String currentLanguage = "FR";

    public static void setCommandeInfo(String numCmd, int table, String client, String lang) {
        numeroCommande = numCmd;
        numeroTable = table;
        nomClient = client;
        currentLanguage = lang;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        traduireInterface();
        afficherTicket();
    }

    private void traduireInterface() {
        boolean isFR = currentLanguage.equals("FR");
        lblTitreRecu.setText(isFR ? "TICKET DE CAISSE" : "RECEIPT");
        lblTxtNumCommande.setText(isFR ? "N° Commande :" : "Order N°");
        lblTxtTable.setText(isFR ? "Table :" : "Table:");
        lblTxtNom.setText(isFR ? "Client :" : "Customer:");
        lblTxtHeure.setText(isFR ? "Heure :" : "Time:");
        lblTxtSousTotal.setText(isFR ? "Sous-total :" : "Subtotal:");
        lblTxtTaxes.setText(isFR ? "Taxes (15%) :" : "Taxes (15%):");
        lblTxtTotal.setText(isFR ? "TOTAL :" : "TOTAL:");
        lblMerci.setText(isFR ? "Merci de votre commande !" : "Thank you for your order!");
        lblAuRevoir.setText(isFR ? "À bientôt chez NETWOK" : "See you soon at NETWOK");
    }
    private String getTraductionNomPlat(String id, String type) {
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

    private void afficherTicket() {
        lblNumCommande.setText(numeroCommande);
        lblNumTable.setText(String.valueOf(numeroTable));
        lblNomClient.setText(nomClient.isEmpty() ? (currentLanguage.equals("FR") ? "Non spécifié" : "Not specified") : nomClient);
        lblHeure.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        List<Plat> panier = MockService.getInstance().getPanier();
        Set<String> idsTraites = new HashSet<>();

        for (Plat p : panier) {
            if (!idsTraites.contains(p.getId())) {
                idsTraites.add(p.getId());
                int qte = MockService.getInstance().getQuantiteDuPlat(p);
                String nomAffiche = getTraductionNomPlat(p.getId(), p.getNom());
                vboxArticlesTicket.getChildren().add(creerLigneArticleTicket(nomAffiche, qte, p.getPrix()));
            }
        }
        calculerTotaux();
    }

    private HBox creerLigneArticleTicket(String nom, int qte, double prix) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(nom + " x" + qte);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        HBox.setHgrow(lbl, Priority.ALWAYS);
        lbl.setMaxWidth(Double.MAX_VALUE);

        Label lblP = new Label(String.format("%.2f€", prix * qte));
        lblP.setStyle("-fx-text-fill: #00F0FF; -fx-font-weight: bold;");
        ligne.getChildren().addAll(lbl, lblP);
        return ligne;
    }

    private void calculerTotaux() {
        double st = MockService.getInstance().getTotalPanier();
        double tx = st * 0.15;
        lblSousTotal.setText(String.format("%.2f€", st));
        lblTaxes.setText(String.format("%.2f€", tx));
        lblTotal.setText(String.format("%.2f€", st + tx));
    }

    @FXML void terminerCommande() {
        VBox ticketParent = (VBox) vboxArticlesTicket.getParent().getParent();
        FadeTransition fo = new FadeTransition(Duration.millis(500), ticketParent);
        fo.setToValue(0);
        fo.setOnFinished(e -> {
            vboxMessage.setOpacity(0);
            FadeTransition fi = new FadeTransition(Duration.millis(800), vboxMessage);
            fi.setToValue(1);
            fi.play();
            PauseTransition p = new PauseTransition(Duration.seconds(4));
            p.setOnFinished(ev -> retournerCatalogue());
            p.play();
        });
        fo.play();
    }

    private void retournerCatalogue() {
        try {
            MockService.getInstance().viderPanier();
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) { e.printStackTrace(); }
    }
}