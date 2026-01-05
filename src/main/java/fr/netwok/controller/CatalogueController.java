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
        updatePanierDisplay(); // Met à jour le bandeau du bas au lancement
        afficherPlats("Entrées");
    }

    @FXML void filtrerEntrees() { afficherPlats("Entrées"); }
    @FXML void filtrerPlats() { afficherPlats("Plats"); }
    
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
        for (Plat p : plats) {
            creerCartePlat(p);
        }
    }

    private void creerCartePlat(Plat p) {
        // 1. La Carte Globale
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(280);
        carte.setAlignment(Pos.CENTER);

        // 2. L'Image
        ImageView imgView = new ImageView();
        imgView.setFitHeight(180);
        imgView.setFitWidth(240);
        imgView.setPreserveRatio(true);
        
        try {
            String imagePath = "/fr/netwok/images/" + p.getImagePath();
            URL urlImg = getClass().getResource(imagePath);
            if(urlImg != null) {
                imgView.setImage(new Image(urlImg.toExternalForm()));
            } else {
                throw new Exception("Image introuvable");
            }
        } catch (Exception e) {
            try {
                imgView.setImage(new Image(getClass().getResource("/fr/netwok/images/logo_main.png").toExternalForm()));
            } catch (Exception ex) { }
        }

        // 3. Les Textes
        Label nom = new Label(p.getNom());
        nom.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        nom.setWrapText(true);
        nom.setTextAlignment(TextAlignment.CENTER);

        Label desc = new Label(p.getDescription());
        desc.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        desc.setWrapText(true);
        desc.setTextAlignment(TextAlignment.CENTER);

        Label prix = new Label(p.getPrixFormate());
        prix.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Consolas';");

        // 4. LE SÉLECTEUR DE QUANTITÉ (Nouveau !)
        
        // On vérifie combien il y en a déjà dans le panier pour afficher le bon chiffre
        int qteActuelle = MockService.getInstance().getQuantiteDuPlat(p);
        
        HBox quantityBox = new HBox();
        quantityBox.getStyleClass().add("box-quantite");
        quantityBox.setAlignment(Pos.CENTER);
        
        Button btnMinus = new Button("-");
        btnMinus.getStyleClass().add("btn-minus");
        
        Label lblQty = new Label(String.valueOf(qteActuelle));
        lblQty.getStyleClass().add("lbl-quantite");
        
        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("btn-plus");

        // ACTION : BOUTON PLUS (+)
        btnPlus.setOnAction(e -> {
            MockService.getInstance().ajouterAuPanier(p);
            
            // Mise à jour visuelle locale
            int newQte = Integer.parseInt(lblQty.getText()) + 1;
            lblQty.setText(String.valueOf(newQte));
            
            // Mise à jour de la barre du bas
            updatePanierDisplay();
        });

        // ACTION : BOUTON MOINS (-)
        btnMinus.setOnAction(e -> {
            int currentQte = Integer.parseInt(lblQty.getText());
            if (currentQte > 0) {
                MockService.getInstance().retirerDuPanier(p);
                
                // Mise à jour visuelle locale
                lblQty.setText(String.valueOf(currentQte - 1));
                
                // Mise à jour de la barre du bas
                updatePanierDisplay();
            }
        });

        quantityBox.getChildren().addAll(btnMinus, lblQty, btnPlus);

        // 5. Assemblage final
        carte.getChildren().addAll(imgView, nom, desc, prix, quantityBox);
        gridPlats.getChildren().add(carte);
    }
}