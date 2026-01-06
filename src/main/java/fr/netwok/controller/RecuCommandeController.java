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

    @FXML private Label lblNumCommande;
    @FXML private Label lblNumTable;
    @FXML private Label lblNomClient;
    @FXML private Label lblHeure;
    @FXML private VBox vboxArticlesTicket;
    @FXML private Label lblSousTotal;
    @FXML private Label lblTaxes;
    @FXML private Label lblTotal;
    @FXML private VBox vboxMessage;

    // Données passées de PanierController
    private static String numeroCommande = "";
    private static int numeroTable = 0;
    private static String nomClient = "";

    public static void setCommandeInfo(String numCmd, int table, String client) {
        numeroCommande = numCmd;
        numeroTable = table;
        nomClient = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherTicket();
    }

    private void afficherTicket() {
        // Informations de commande
        lblNumCommande.setText(numeroCommande);
        lblNumTable.setText(String.valueOf(numeroTable));
        lblNomClient.setText(nomClient.isEmpty() ? "Non spécifié" : nomClient);
        
        // Heure actuelle
        LocalTime heure = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        lblHeure.setText(heure.format(formatter));

        // Articles du panier
        List<Plat> panier = MockService.getInstance().getPanier();
        Set<String> idsTraites = new HashSet<>();

        for (Plat p : panier) {
            if (!idsTraites.contains(p.getId())) {
                idsTraites.add(p.getId());
                int qte = MockService.getInstance().getQuantiteDuPlat(p);
                vboxArticlesTicket.getChildren().add(creerLigneArticleTicket(p, qte));
            }
        }

        // Calcul des totaux
        calculerTotaux();
    }

    private HBox creerLigneArticleTicket(Plat p, int qte) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-padding: 5 0 5 0;");

        // Nom et quantité
        Label lblNom = new Label(p.getNom() + " x" + qte);
        lblNom.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        lblNom.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblNom, javafx.scene.layout.Priority.ALWAYS);

        // Prix total
        double total = p.getPrix() * qte;
        Label lblPrix = new Label(String.format("%.2f€", total));
        lblPrix.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblPrix.setMinWidth(80);
        lblPrix.setAlignment(Pos.CENTER_RIGHT);

        ligne.getChildren().addAll(lblNom, lblPrix);
        return ligne;
    }

    private void calculerTotaux() {
        double sousTotal = MockService.getInstance().getTotalPanier();
        double taxes = sousTotal * 0.15;
        double totalFinal = sousTotal + taxes;

        lblSousTotal.setText(String.format("%.2f€", sousTotal));
        lblTaxes.setText(String.format("%.2f€", taxes));
        lblTotal.setText(String.format("%.2f€", totalFinal));
    }

    private void afficherMessageMerci() {
        // Afficher le message de merci pendant 5 secondes, puis retourner au catalogue
        PauseTransition pauseMessage = new PauseTransition(Duration.seconds(5));
        pauseMessage.setOnFinished(e -> retournerCatalogue());
        pauseMessage.play();
    }

    @FXML
    void terminerCommande() {
        // Créer le parent du ticket pour l'animation
        VBox ticketParent = (VBox) vboxArticlesTicket.getParent();
        
        // Fade out du ticket
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), ticketParent);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.play();

        // Fade in du message
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), vboxMessage);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.millis(500));
        fadeIn.play();

        // Démarrer le compte à rebours du message (5 secondes)
        fadeIn.setOnFinished(event -> afficherMessageMerci());
    }

    private void retournerCatalogue() {
        try {
            // Vider le panier
            MockService.getInstance().viderPanier();
            NetwokApp.setRoot("views/accueil");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
