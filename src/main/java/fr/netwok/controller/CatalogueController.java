package fr.netwok.controller;

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
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogueController implements Initializable {

    @FXML private FlowPane gridPlats;
    
    // Connexion aux éléments du FXML pour le panier
    @FXML private Label lblNbArticles; 
    @FXML private Label lblTotal;

    // État local du panier (simple compteur pour l'instant)
    private int compteurArticles = 0;
    private double montantTotal = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser l'affichage à 0
        updatePanierDisplay();
        
        // Charger la première catégorie
        afficherPlats("Entrées");
    }

    @FXML
    void filtrerEntrees() {
        afficherPlats("Entrées");
    }

    @FXML
    void filtrerPlats() {
        afficherPlats("Plats");
    }

    @FXML
    void filtrerDesserts() {
        // Affiche les desserts ET les boissons dans le même onglet
        gridPlats.getChildren().clear();
        List<Plat> desserts = MockService.getInstance().getPlatsParCategorie("Desserts");
        List<Plat> boissons = MockService.getInstance().getPlatsParCategorie("Boissons");
        
        desserts.forEach(this::creerCartePlat);
        boissons.forEach(this::creerCartePlat);
    }
    
    @FXML
    void voirPanier() {
        // Pour l'instant on affiche juste un message console
        // Plus tard : NetwokApp.setRoot("views/panier");
        System.out.println("Clic sur Voir Panier -> Total actuel : " + montantTotal + "€");
    }

    // Met à jour les textes en bas de l'écran
    private void updatePanierDisplay() {
        lblNbArticles.setText(compteurArticles + " articles");
        lblTotal.setText(String.format("%.2f €", montantTotal));
    }

    // Ajoute un plat au total et met à jour l'affichage
    private void ajouterAuPanier(Plat p) {
        compteurArticles++;
        montantTotal += p.getPrix();
        updatePanierDisplay();
        System.out.println("Ajouté au panier : " + p.getNom());
    }

    private void afficherPlats(String categorie) {
        gridPlats.getChildren().clear();
        List<Plat> plats = MockService.getInstance().getPlatsParCategorie(categorie);
        for (Plat p : plats) {
            creerCartePlat(p);
        }
    }

    private void creerCartePlat(Plat p) {
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(280);
        carte.setAlignment(Pos.CENTER);

        ImageView imgView = new ImageView();
        imgView.setFitHeight(180);
        imgView.setFitWidth(240);
        imgView.setPreserveRatio(true);
        
        try {
            // Tente de charger l'image, sinon logo par défaut
            String imagePath = "/fr/netwok/images/" + p.getImagePath();
            // Petite astuce : si l'image n'existe pas, Java renverra null ou une erreur
            // Ici on suppose que si vous n'avez pas toutes les images (nems.png, etc), 
            // ça ira dans le catch et affichera le logo.
            URL urlImg = getClass().getResource(imagePath);
            if(urlImg != null) {
                imgView.setImage(new Image(urlImg.toExternalForm()));
            } else {
                throw new Exception("Image introuvable");
            }
        } catch (Exception e) {
            try {
                imgView.setImage(new Image(getClass().getResourceAsStream("/fr/netwok/images/logo_main.png")));
            } catch (Exception ex) { /* Rien */ }
        }

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

        Button btnAjouter = new Button("AJOUTER +");
        btnAjouter.getStyleClass().add("btn-ajouter");
        
        // ACTION : Quand on clique, on appelle ajouterAuPanier()
        btnAjouter.setOnAction(e -> ajouterAuPanier(p));

        carte.getChildren().addAll(imgView, nom, desc, prix, btnAjouter);
        gridPlats.getChildren().add(carte);
    }
}