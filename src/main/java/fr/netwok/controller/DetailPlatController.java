package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.event.ActionEvent;
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

    @FXML private Label lblQuantite;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;
    @FXML private Button btnVoirPanier;

    @FXML private Label lblNbArticles;
    @FXML private Label lblTotal;

    private Plat platActuel;
    private int quantite = 1;

    // On utilise une variable statique pour garder la langue entre les écrans
    private static String langueActuelle = "FR";
    private static Plat platAfficher = null;

    public static void setPlatAfficher(Plat plat) {
        platAfficher = plat;
    }

    // Permet au CatalogueController de définir la langue avant de changer de vue
    public static void setLangue(String langue) {
        langueActuelle = langue;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (platAfficher != null) {
            platActuel = platAfficher;
            chargerDetailPlat();
            platAfficher = null;
        }
        updatePanierDisplay();
        traduireInterface();
    }

    // --- LOGIQUE DE TRADUCTION ---

    private String ui(String fr, String en) {
        return "FR".equals(langueActuelle) ? fr : en;
    }

    private void traduireInterface() {
        // On ajoute un "if != null" devant chaque élément pour éviter le crash
        if (btnAjouter != null) btnAjouter.setText(ui("Ajouter au panier", "Add to basket"));
        if (btnRetour != null) btnRetour.setText(ui("Retour", "Back"));
        if (btnVoirPanier != null) btnVoirPanier.setText(ui("Voir mon panier", "View my basket"));

        if (rbDoux != null) rbDoux.setText(ui("Doux", "Mild"));
        if (rbMoyen != null) rbMoyen.setText(ui("Moyen", "Medium"));
        if (rbFort != null) rbFort.setText(ui("Fort", "Spicy"));

        if (rbRiz != null) rbRiz.setText(ui("Riz", "Rice"));
        if (rbNouilles != null) rbNouilles.setText(ui("Nouilles", "Noodles"));
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

    private void chargerDetailPlat() {
        if (platActuel == null) return;

        String nomTrad = getTraductionProduit(platActuel.getId(), "nom");
        String descTrad = getTraductionProduit(platActuel.getId(), "desc");

        lblNom.setText(nomTrad != null ? nomTrad : platActuel.getNom());
        lblPrix.setText(String.format("%.2f €", platActuel.getPrix()));
        txtDescription.setText(descTrad != null ? descTrad : platActuel.getDescription());

        try {
            URL res = getClass().getResource("/fr/netwok/images/" + platActuel.getImagePath());
            if (res != null) imgPlat.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) { e.printStackTrace(); }

        txtInfos.setText(genererInformations());
    }

    private String genererInformations() {
        StringBuilder sb = new StringBuilder();
        sb.append(ui("Allergènes : peut contenir des traces de gluten, soja\n",
                "Allergens: may contain traces of gluten, soy\n"));
        sb.append(ui("Calories : environ 450 kcal\n", "Calories: approx. 450 kcal\n"));
        sb.append(ui("Temps de préparation : 15-20 min", "Preparation time: 15-20 min"));
        return sb.toString();
    }

    // --- ACTIONS ---

    @FXML
    void ajouterAuPanier() {
        if (platActuel == null) return;

        for (int i = 0; i < quantite; i++) {
            MockService.getInstance().ajouterAuPanier(platActuel);
        }

        updatePanierDisplay();
        btnAjouter.setText(ui("✓ Ajouté !", "✓ Added!"));
        btnAjouter.setDisable(true);

        new Thread(() -> {
            try {
                Thread.sleep(800);
                javafx.application.Platform.runLater(this::retourCatalogue);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }

    @FXML
    void changeLanguage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        langueActuelle = btn.getText().toUpperCase();
        traduireInterface();
        chargerDetailPlat();
        updatePanierDisplay();
    }

    @FXML void augmenterQuantite() { if (quantite < 99) { quantite++; lblQuantite.setText(String.valueOf(quantite)); } }
    @FXML void diminuerQuantite() { if (quantite > 1) { quantite--; lblQuantite.setText(String.valueOf(quantite)); } }

    @FXML void retourCatalogue() {
        try { NetwokApp.setRoot("views/catalogue"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML void voirPanier() {
        try { NetwokApp.setRoot("views/panier"); } catch (IOException e) { e.printStackTrace(); }
    }

    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();
        lblNbArticles.setText(nb + " " + ui("articles", "items"));
        lblTotal.setText(String.format("%.2f €", total));
    }
}