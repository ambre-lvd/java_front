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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {

    @FXML private FlowPane gridPlats;
    @FXML private Label lblNbArticles; 
    @FXML private Label lblTotal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // On entoure par un try-catch pour éviter que l'appli ne se bloque
        try {
            MockService.getInstance().rafraichirCatalogue();
        } catch (Exception e) {
            System.err.println("Le catalogue n'a pas pu être chargé : " + e.getMessage());
        }

        updatePanierDisplay();
        afficherPlats("Entrée");
    }

    @FXML void filtrerEntrees() { afficherPlats("Entrée"); }
    @FXML void filtrerPlats() { afficherPlats("Plat"); }
    
    @FXML
    void filtrerDesserts() {
        gridPlats.getChildren().clear();
        List<Plat> desserts = MockService.getInstance().getPlatsParCategorie("Desserts");
        List<Plat> boissons = MockService.getInstance().getPlatsParCategorie("Boissons");
        desserts.forEach(this::creerCartePlat);
        boissons.forEach(this::creerCartePlat);
    }
    
    @FXML
    void voirPanier() {
        try {
            NetwokApp.setRoot("views/panier");
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

    private void afficherPlats(String categorie) {
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

    private void creerCartePlat(Plat p) {
        // 1. Le conteneur principal (VBox)
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(280);
        carte.setAlignment(Pos.CENTER);

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
        });

        btnMinus.setOnAction(e -> {
            if (MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
                lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
                updatePanierDisplay();
            }
        });

        quantityBox.getChildren().addAll(btnMinus, lblQty, btnPlus);

        // 5. Assemblage
        carte.getChildren().addAll(imgView, name, desc, price, quantityBox);
        gridPlats.getChildren().add(carte);
    }
}