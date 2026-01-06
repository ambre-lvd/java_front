package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.ApiClient;
import fr.netwok.service.MockService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class PanierController implements Initializable {

    @FXML private VBox vboxArticles;
    @FXML private Label lblSousTotal;
    @FXML private Label lblTaxes;
    @FXML private Label lblTotalFinal;
    @FXML private TextField txtNumeroTable;
    @FXML private TextField txtNomClient;
    @FXML private Button btnConfirmer;

    private static final double TAUX_TAXE = 0.15; // 15%

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerPanier();
    }

    private void chargerPanier() {
        vboxArticles.getChildren().clear();

        List<Plat> toutLePanier = MockService.getInstance().getPanier();
        Set<String> idsTraites = new HashSet<>();

        if (toutLePanier.isEmpty()) {
            Label vide = new Label("Votre panier est vide.");
            vide.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px; -fx-padding: 40 0 40 0;");
            
            // Animation d'apparition
            FadeTransition fade = new FadeTransition(Duration.millis(500), vide);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            
            vboxArticles.getChildren().add(vide);
            btnConfirmer.setDisable(true);
        } else {
            btnConfirmer.setDisable(false);
            
            // Grouper les articles identiques avec animation
            int index = 0;
            for (Plat p : toutLePanier) {
                if (!idsTraites.contains(p.getId())) {
                    idsTraites.add(p.getId());
                    int qte = MockService.getInstance().getQuantiteDuPlat(p);
                    VBox ligneArticle = creerLigneArticle(p, qte);
                    
                    // Animation staggered (décalée)
                    FadeTransition fade = new FadeTransition(Duration.millis(400), ligneArticle);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.setDelay(Duration.millis(index * 100));
                    
                    TranslateTransition translate = new TranslateTransition(Duration.millis(400), ligneArticle);
                    translate.setFromY(30);
                    translate.setToY(0);
                    translate.setDelay(Duration.millis(index * 100));
                    
                    vboxArticles.getChildren().add(ligneArticle);
                    fade.play();
                    translate.play();
                    index++;
                }
            }
        }

        // Calcul des totaux avec animation
        animerTotaux();
    }

    private VBox creerLigneArticle(Plat p, int qte) {
        VBox ligneComplete = new VBox(8);
        ligneComplete.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4); " +
                              "-fx-background-radius: 12; " +
                              "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                              "-fx-border-radius: 12; " +
                              "-fx-border-width: 1; " +
                              "-fx-padding: 15;");
        
        // Ligne principale avec les informations
        HBox lignePrincipale = new HBox(20);
        lignePrincipale.setAlignment(Pos.CENTER_LEFT);

        // Colonne 0 : Image du plat
        ImageView imgPlat = new ImageView();
        imgPlat.setFitWidth(80);
        imgPlat.setFitHeight(80);
        imgPlat.setPreserveRatio(true);
        imgPlat.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 240, 255, 0.2), 5, 0.3, 0, 0);");
        
        try {
            String imagePath = p.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                String fullPath = "/fr/netwok/images/" + imagePath;
                URL res = getClass().getResource(fullPath);
                if (res != null) {
                    imgPlat.setImage(new Image(res.toExternalForm()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + e.getMessage());
        }

        // Colonne 1 : Nom et description/options
        VBox colInfo = new VBox(5);
        colInfo.setPrefWidth(350);
        colInfo.setMinWidth(350);
        
        Label lblNom = new Label(p.getNom());
        lblNom.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        lblNom.setWrapText(true);
        
        Label lblOptions = new Label(genererTextOptions());
        lblOptions.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        lblOptions.setWrapText(true);
        
        colInfo.getChildren().addAll(lblNom, lblOptions);

        // Colonne 2 : Contrôles de quantité
        HBox boxQuantite = new HBox(10);
        boxQuantite.setAlignment(Pos.CENTER);
        boxQuantite.setPrefWidth(130);
        boxQuantite.setMinWidth(130);
        
        Button btnMoins = new Button("-");
        btnMoins.setText("-");
        btnMoins.setStyle("-fx-padding: 10px; " +
                         "-fx-background-color: rgba(0, 240, 255, 0.2); " +
                         "-fx-text-fill: #00F0FF; " +
                         "-fx-font-size: 28px; " +
                         "-fx-font-weight: bold; " +
                         "-fx-cursor: hand; " +
                         "-fx-min-width: 50px; " +
                         "-fx-min-height: 50px; " +
                         "-fx-pref-width: 50px; " +
                         "-fx-pref-height: 50px; " +
                         "-fx-border-color: #00F0FF; " +
                         "-fx-border-width: 2; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10;");
        
        Label lblQte = new Label(String.valueOf(qte));
        lblQte.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold; -fx-min-width: 50px; -fx-alignment: center;");
        
        Button btnPlus = new Button("+");
        btnPlus.setText("+");
        btnPlus.setStyle("-fx-padding: 10px; " +
                        "-fx-background-color: rgba(0, 240, 255, 0.2); " +
                        "-fx-text-fill: #00F0FF; " +
                        "-fx-font-size: 28px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand; " +
                        "-fx-min-width: 50px; " +
                        "-fx-min-height: 50px; " +
                        "-fx-pref-width: 50px; " +
                        "-fx-pref-height: 50px; " +
                        "-fx-border-color: #00F0FF; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10;");
        
        btnMoins.setOnAction(e -> {
            MockService.getInstance().retirerDuPanier(p);
            animerModification(btnMoins);
            chargerPanier();
        });
        
        btnPlus.setOnAction(e -> {
            MockService.getInstance().ajouterAuPanier(p);
            animerModification(btnPlus);
            chargerPanier();
        });
        
        boxQuantite.getChildren().addAll(btnMoins, lblQte, btnPlus);

        // Colonne 3 : Prix unitaire
        Label lblPrixUnit = new Label(String.format("%.2f€", p.getPrix()));
        lblPrixUnit.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px;");
        lblPrixUnit.setPrefWidth(100);
        lblPrixUnit.setMinWidth(100);
        lblPrixUnit.setAlignment(Pos.CENTER_RIGHT);

        // Colonne 4 : Total
        double total = p.getPrix() * qte;
        Label lblTotal = new Label(String.format("%.2f€", total));
        lblTotal.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 18px; -fx-font-weight: bold;");
        lblTotal.setPrefWidth(120);
        lblTotal.setMinWidth(120);
        lblTotal.setAlignment(Pos.CENTER_RIGHT);

        // Bouton Supprimer
        Button btnSupprimer = new Button("✕");
        btnSupprimer.setStyle("-fx-background-color: transparent; " +
                             "-fx-text-fill: #FF007F; " +
                             "-fx-font-size: 20px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-cursor: hand;");
        btnSupprimer.setPrefWidth(50);
        btnSupprimer.setMinWidth(50);
        btnSupprimer.setOnAction(e -> {
            // Animation de suppression
            FadeTransition fade = new FadeTransition(Duration.millis(300), ligneComplete);
            fade.setFromValue(1);
            fade.setToValue(0);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), ligneComplete);
            scale.setFromX(1);
            scale.setFromY(1);
            scale.setToX(0.8);
            scale.setToY(0.8);
            
            fade.setOnFinished(event -> {
                // Supprimer toutes les occurrences de ce plat
                while(MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                    MockService.getInstance().retirerDuPanier(p);
                }
                chargerPanier();
            });
            
            fade.play();
            scale.play();
        });

        lignePrincipale.getChildren().addAll(imgPlat, colInfo, boxQuantite, lblPrixUnit, lblTotal, btnSupprimer);
        ligneComplete.getChildren().add(lignePrincipale);
        
        return ligneComplete;
    }

    private String genererTextOptions() {
        // Vous pouvez personnaliser ceci selon les options choisies
        // Pour l'instant, on affiche un exemple statique
        return "Épicé moyen, Riz blanc";
    }

    private void calculerTotaux() {
        double sousTotal = MockService.getInstance().getTotalPanier();
        double taxes = sousTotal * TAUX_TAXE;
        double totalFinal = sousTotal + taxes;

        lblSousTotal.setText(String.format("%.2f€", sousTotal));
        lblTaxes.setText(String.format("%.2f€", taxes));
        lblTotalFinal.setText(String.format("%.2f€", totalFinal));
    }

    private void animerTotaux() {
        double sousTotal = MockService.getInstance().getTotalPanier();
        double taxes = sousTotal * TAUX_TAXE;
        double totalFinal = sousTotal + taxes;

        // Animation de scintillement pour les totaux
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), lblTotalFinal);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(400), lblTotalFinal);
        fade.setFromValue(0.5);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(200));

        lblSousTotal.setText(String.format("%.2f€", sousTotal));
        lblTaxes.setText(String.format("%.2f€", taxes));
        lblTotalFinal.setText(String.format("%.2f€", totalFinal));
        
        scale.play();
        fade.play();
    }

    @FXML
    void confirmerCommande() {
        // 1. Récupération du panier (liste d'objets Plat)
        List<Plat> panier = MockService.getInstance().getPanier();
        if (panier.isEmpty()) {
            System.out.println("⚠️ Le panier est vide");
            return;
        }

        String numeroTable = txtNumeroTable.getText().trim();
        String nomClient = txtNomClient.getText().trim();

        // 2. Validation du numéro de table
        if (numeroTable.isEmpty()) {
            System.out.println("⚠️ Veuillez entrer un numéro de table");
            txtNumeroTable.setStyle("-fx-border-color: #FF007F; -fx-border-width: 2px; -fx-background-color: #1e293b; -fx-text-fill: white;");
            return;
        }

        try {
            // 3. Conversion du numéro de table
            int tableNumber;
            try {
                tableNumber = Integer.parseInt(numeroTable);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Numéro de table invalide");
                txtNumeroTable.setStyle("-fx-border-color: #FF007F; -fx-border-width: 2px;");
                return;
            }

            System.out.println("📝 Envoi de la commande - Table: " + tableNumber);

            // 4. ENVOI À L'API (On envoie la liste de Plats, l'ApiClient gèrera la conversion en JSON)
            ApiClient.sendOrder(tableNumber, panier);

            // 5. Succès : Feedback visuel
            System.out.println("✅ Commande enregistrée en BDD !");
            btnConfirmer.setText("✓ Commande confirmée");
            btnConfirmer.setDisable(true);
            btnConfirmer.setStyle("-fx-background-color: #10b981; -fx-text-fill: white;"); // Vert succès

            // 6. Navigation vers le reçu
            final String numCommande = "CMD-" + System.currentTimeMillis() % 100000;
            final int finalTableNumber = tableNumber;
            final String finalNomClient = nomClient;

            new Thread(() -> {
                try {
                    Thread.sleep(1200);
                    javafx.application.Platform.runLater(() -> {
                        RecuCommandeController.setCommandeInfo(numCommande, finalTableNumber, finalNomClient);
                        try {
                            NetwokApp.setRoot("views/recuCommande");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("❌ Erreur API : " + e.getMessage());
            btnConfirmer.setText("❌ Erreur Serveur");
            btnConfirmer.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
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

    // Animation pour les modifications (+ et -)
    private void animerModification(Button btn) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
        scale.setFromX(1.2);
        scale.setFromY(1.2);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }
}