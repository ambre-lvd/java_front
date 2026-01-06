package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DetailPlatController implements Initializable {

    @FXML private ImageView imgPlat;
    @FXML private Label lblNom;
    @FXML private Label lblPrix;
    @FXML private Text txtDescription;
    @FXML private Text txtInfos;
    
    @FXML private RadioButton rbDoux;
    @FXML private RadioButton rbMoyen;
    @FXML private RadioButton rbFort;
    
    @FXML private RadioButton rbRiz;
    @FXML private RadioButton rbNouilles;
    
    @FXML private Button btnMinus;
    @FXML private Label lblQuantite;
    @FXML private Button btnPlus;
    @FXML private Button btnAjouter;
    
    @FXML private Label lblNbArticles;
    @FXML private Label lblTotal;
    
    private Plat platActuel;
    private int quantite = 1;
    
    // Cette méthode statique permet de passer le plat depuis le catalogue
    private static Plat platAfficher = null;
    
    public static void setPlatAfficher(Plat plat) {
        platAfficher = plat;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (platAfficher != null) {
            platActuel = platAfficher;
            chargerDetailPlat();
            platAfficher = null; // Reset pour éviter les problèmes
        }
        updatePanierDisplay();
    }

    private void chargerDetailPlat() {
        if (platActuel == null) return;
        
        // Nom et prix
        lblNom.setText(platActuel.getNom());
        lblPrix.setText(String.format("%.2f €", platActuel.getPrix()));
        
        // Description
        txtDescription.setText(platActuel.getDescription());
        
        // Image
        try {
            String imagePath = platActuel.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                String fullPath = "/fr/netwok/images/" + imagePath;
                URL res = getClass().getResource(fullPath);
                
                if (res != null) {
                    imgPlat.setImage(new Image(res.toExternalForm()));
                } else {
                    System.err.println("⚠️ Image non trouvée : " + fullPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
        
        // Informations additionnelles (à adapter selon vos données)
        String infos = genererInformations();
        txtInfos.setText(infos);
    }

    private String genererInformations() {
        // Vous pouvez adapter ceci selon les informations disponibles dans votre base de données
        StringBuilder sb = new StringBuilder();
        
        // Exemple d'informations (à remplacer par de vraies données si disponibles)
        sb.append("Allergènes : peut contenir des traces de fruits à coque, gluten, soja\n");
        sb.append("Calories : environ 450 kcal\n");
        sb.append("Temps de préparation : 15-20 minutes\n");
        
        return sb.toString();
    }

    @FXML
    void augmenterQuantite() {
        if (quantite < 99) {
            quantite++;
            lblQuantite.setText(String.valueOf(quantite));
        }
    }

    @FXML
    void diminuerQuantite() {
        if (quantite > 1) {
            quantite--;
            lblQuantite.setText(String.valueOf(quantite));
        }
    }

    @FXML
    void ajouterAuPanier() {
        if (platActuel == null) return;
        
        // Récupération des options sélectionnées
        String epice = getEpiceSelectionnee();
        String accompagnement = getAccompagnementSelectionne();
        
        // Debug : afficher les options choisies
        System.out.println("Ajout au panier :");
        System.out.println("  - Plat : " + platActuel.getNom());
        System.out.println("  - Quantité : " + quantite);
        System.out.println("  - Épice : " + epice);
        System.out.println("  - Accompagnement : " + accompagnement);
        
        // Ajouter la quantité demandée au panier
        for (int i = 0; i < quantite; i++) {
            MockService.getInstance().ajouterAuPanier(platActuel);
        }
        
        // Mise à jour de l'affichage du panier
        updatePanierDisplay();
        
        // Message de confirmation (optionnel)
        btnAjouter.setText("✓ Ajouté au panier");
        btnAjouter.setDisable(true);
        
        // Réactiver le bouton après 1 seconde et retourner au catalogue
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> {
                    btnAjouter.setText("Ajouter au panier");
                    btnAjouter.setDisable(false);
                    // Retour automatique au catalogue
                    retourCatalogue();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getEpiceSelectionnee() {
        if (rbDoux.isSelected()) return "Doux";
        if (rbMoyen.isSelected()) return "Moyen";
        if (rbFort.isSelected()) return "Fort";
        return "Doux"; // Par défaut
    }

    private String getAccompagnementSelectionne() {
        if (rbRiz.isSelected()) return "Riz";
        if (rbNouilles.isSelected()) return "Nouilles";
        return "Riz"; // Par défaut
    }

    @FXML
    void retourCatalogue() {
        try {
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void voirPanier() {
        try {
            NetwokApp.setRoot("views/panier");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();
        
        lblNbArticles.setText(nb + " articles");
        lblTotal.setText(String.format("%.2f €", total));
    }
}
