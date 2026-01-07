package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
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
    @FXML private Label lblTotaltxt;
    @FXML private Label lblMonPanierTitre;
    @FXML private HBox sousCategorieBar;
    @FXML private ScrollPane scrollPane;
    @FXML private Label sectionTitle;

    @FXML private ToggleButton tabEntrees;
    @FXML private ToggleButton tabPlats;
    @FXML private ToggleButton tabDesserts;
    @FXML private ToggleButton tabAllDesserts;
    @FXML private ToggleButton tabDessertsOnly;
    @FXML private ToggleButton tabBoissonsOnly;

    @FXML private Button btnVoirPanier;

    private int categorieActuelle = 1;
    private int sousModeActuel = 0;
    private static String langueActuelle = "FR";

    public static String getLangueActuelle() { return langueActuelle; }
    public static void setLangueActuelle(String langue) { langueActuelle = langue; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            MockService.getInstance().rafraichirCatalogue();
        } catch (Exception e) {
            System.err.println("Erreur chargement catalogue: " + e.getMessage());
        }
        updateInterfaceText();
        refreshView();
    }

    private String t(String fr, String en, String zh, String jp) {
        return switch (langueActuelle) {
            case "EN" -> en;
            case "中文" -> zh;
            case "日本語" -> jp;
            default -> fr;
        };
    }

    private void updateInterfaceText() {
        if (tabEntrees != null) tabEntrees.setText(t("ENTRÉES", "STARTERS", "前菜", "前菜"));
        if (tabPlats != null) tabPlats.setText(t("PLATS", "MAINS", "主菜", "メイン"));
        if (tabDesserts != null) tabDesserts.setText(t("DESSERTS / BOISSONS", "DESSERTS / DRINKS", "甜点 / 饮料", "デザート・ドリンク"));
        if (tabAllDesserts != null) tabAllDesserts.setText(t("TOUT", "ALL", "全部", "すべて"));
        if (tabDessertsOnly != null) tabDessertsOnly.setText(t("DESSERTS", "DESSERTS", "甜点", "デザート"));
        if (tabBoissonsOnly != null) tabBoissonsOnly.setText(t("BOISSONS", "DRINKS", "饮料", "ドリンク"));
        if (lblTotaltxt != null) lblTotaltxt.setText(t("TOTAL :", "TOTAL :", "總計 :", "合計 :"));
        if (lblMonPanierTitre != null) lblMonPanierTitre.setText(t("MON PANIER", "MY BASKET", "我的购物车", "買い物かご"));
        if (btnVoirPanier != null) btnVoirPanier.setText(t("VOIR PANIER >", "VIEW BASKET >", "查看购物车 >", "かごを見る >"));
        updateSectionTitle(sousModeActuel);
        updatePanierDisplay();
    }

    private String[] getTraductionProduit(String id) {
        return switch (id) {
            case "B1" -> new String[]{t("Ice Tea", "Iced Tea", "冰茶", "アイスティー"), t("Maison, citron vert", "Homemade, lime", "自制青柠味", "自家製、ライム入り")};
            case "B2" -> new String[]{t("Bière Tsingtao", "Tsingtao Beer", "青岛啤酒", "青島ビール"), t("Bière blonde 33cl", "Lager beer 33cl", "33毫升", "ラガービール 33cl")};
            case "B3" -> new String[]{t("Limonade Jap", "Jap Lemonade", "弹珠汽水", "ラムネ"), t("Ramune à bille", "Ramune with marble", "日式传统汽水", "ビー玉入りラムネ")};
            case "B4" -> new String[]{t("Jus de Coco", "Coconut Juice", "椰子汁", "ココナッツジュース"), t("Avec morceaux", "With chunks", "果肉果汁", "果肉入り")};
            case "B5" -> new String[]{t("Sake", "Sake", "清酒", "日本酒"), t("Petit pichet", "Small pitcher", "小瓶装", "徳利（小）")};
            case "D1" -> new String[]{t("Perles de Coco", "Coconut Pearls", "椰丝球", "ココナッツ団子"), t("2 pièces, tiède", "2 pieces, warm", "2个, 温热", "2個、温かい")};
            case "D2" -> new String[]{t("Mochi Glacé", "Iced Mochi", "冰淇淋大福", "雪見だいふく"), t("2 pièces, Vanille et Matcha", "2 pieces, Vanilla/Matcha", "2个, 香草和抹茶", "2個、バニラと抹茶")};
            case "D3" -> new String[]{t("Mangue Fraîche", "Fresh Mango", "鲜芒果", "フレッシュマンゴー"), t("Tranches de mangue", "Mango slices", "新鲜切片", "マンゴースライス")};
            case "D4" -> new String[]{t("Banane Flambée", "Flambé Banana", "拔丝香蕉", "バナナのフランベ"), t("Au saké", "With sake", "清酒烹制", "日本酒風味")};
            case "D5" -> new String[]{t("Nougat Chinois", "Chinese Nougat", "芝麻糖", "中華風の中華菓子"), t("Aux graines de sésame", "With sesame seeds", "芝麻味", "ゴマ入り")};
            case "E1" -> new String[]{t("Nems Poulet", "Chicken Nems", "鸡肉春卷", "鶏肉の揚げ春巻き"), t("4 pièces, sauce nuoc-mâm", "4 pieces, fish sauce", "4个, 鱼露", "4個、ヌクマムソース")};
            case "E2" -> new String[]{t("Rouleaux Printemps", "Spring Rolls", "夏卷", "生春巻き"), t("Crevette, menthe, riz", "Shrimp, mint, rice", "鲜虾, 薄荷", "海老、ミント、米粉")};
            case "E3" -> new String[]{t("Gyozas Poulet", "Chicken Gyozas", "鸡肉饺子", "鶏肉餃子"), t("Raviolis grillés (5 pièces)", "Grilled dumplings (5 pcs)", "煎饺 (5个)", "焼き餃子（5個）")};
            case "E4" -> new String[]{t("Samoussas Boeuf", "Beef Samoussas", "牛肉咖喱角", "牛肉のサモサ"), t("Croustillant aux épices", "Crispy with spices", "香脆辣味", "スパイス香るカリカリ揚げ")};
            case "E5" -> new String[]{t("Salade de Chou", "Cabbage Salad", "凉拌卷心菜", "キャベツのサラダ"), t("Chou blanc, marinade sésame", "White cabbage, sesame", "白菜, 芝麻汁", "白キャベツの胡麻マリネ")};
            case "E6" -> new String[]{t("Soupe Miso", "Miso Soup", "味噌汤", "味噌汁"), t("Tofu, algues wakame", "Tofu, wakame seaweed", "豆腐, 海带", "豆腐、わかめ")};
            case "E7" -> new String[]{t("Tempura Crevettes", "Shrimp Tempura", "天妇罗虾", "海老の天ぷら"), t("Beignets légers (4 pièces)", "Light fritters (4 pcs)", "脆炸 (4个)", "衣揚げ（4個）")};
            case "E8" -> new String[]{t("Yakitori Boeuf", "Beef Yakitori", "牛肉串", "牛串焼き"), t("Brochettes boeuf-fromage", "Beef-cheese skewers", "芝士牛肉串", "牛チーズ串")};
            case "E9" -> new String[]{t("Edamame", "Edamame", "毛豆", "枝豆"), t("Fèves de soja, sel de mer", "Soybeans, sea salt", "盐水大豆", "塩ゆで枝豆")};
            case "E10" -> new String[]{t("Mix Dim Sum", "Dim Sum Mix", "点心拼盘", "点心セット"), t("Panier vapeur (6 pièces)", "Steamed basket (6 pcs)", "蒸笼 (6个)", "蒸し器（6個）")};
            case "P1" -> new String[]{t("Pad Thaï", "Pad Thai", "泰式炒河粉", "パッタイ"), t("Nouilles de riz, crevettes", "Rice noodles, shrimp", "大米粉, 鲜虾", "米粉の麺、海老")};
            case "P2" -> new String[]{t("Bo Bun Boeuf", "Beef Bo Bun", "牛肉米粉", "牛焼肉のブン"), t("Vermicelles, boeuf sauté", "Vermicelli, sautéed beef", "干拌粉, 炒牛肉", "米麺、牛肉炒め")};
            case "P3" -> new String[]{t("Curry Vert", "Green Curry", "绿咖喱", "グリーンカレー"), t("Poulet, lait de coco", "Chicken, coconut milk", "鸡肉, 椰奶", "鶏肉、ココナッツミルク")};
            case "P4" -> new String[]{t("Riz Cantonais", "Cantonese Rice", "扬州炒饭", "チャーハン"), t("Riz sauté, jambon", "Fried rice, ham", "火腿蛋炒饭", "ハム入り炒飯")};
            case "P5" -> new String[]{t("Porc au Caramel", "Caramel Pork", "红烧肉", "豚肉のキャラメル煮"), t("Travers de porc confits", "Candied ribs", "焦糖猪排", "豚バラ肉の甘辛煮")};
            case "P6" -> new String[]{t("Canard Laqué", "Peking Duck", "北京烤鸭", "北京ダック"), t("Avec crêpes", "With pancakes", "附荷叶饼", "薄餅添え")};
            case "P7" -> new String[]{t("Bibimbap", "Bibimbap", "石锅拌饭", "ビビンバ"), t("Riz, boeuf, légumes", "Rice, beef, vegetables", "米饭, 牛肉, 蔬菜", "ご飯、牛肉、野菜")};
            case "P8" -> new String[]{t("Tonkotsu Ramen", "Tonkotsu Ramen", "豚骨拉面", "豚骨ラーメン"), t("Bouillon porc, nouilles", "Pork broth, noodles", "浓汤面", "豚骨スープ、麺")};
            case "P9" -> new String[]{t("Mix Sushi 12", "Sushi Mix 12", "寿司拼盘", "寿司盛り合わせ 12貫"), t("Assortiment de sushi", "Sushi assortment", "12个寿司", "寿司のアソート")};
            case "P10" -> new String[]{t("Wok Végé", "Vege Wok", "素食炒面", "野菜の炒め物"), t("Nouilles, tofu", "Noodles, tofu", "面条, 豆腐", "麺、豆腐")};
            default -> new String[]{"?", "?", "?", "?"};
        };
    }

    // --- NAVIGATION ET FILTRES ---

    @FXML void changeLanguage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        langueActuelle = btn.getText().toUpperCase();
        updateInterfaceText();
        refreshView();
    }

    @FXML void filtrerEntrees() {
        this.categorieActuelle = 1;
        toggleSousCategorie(false);
        refreshView();
        resetScroll();
    }

    @FXML void filtrerPlats() {
        this.categorieActuelle = 2;
        toggleSousCategorie(false);
        refreshView();
        resetScroll();
    }

    @FXML void filtrerDesserts() {
        this.categorieActuelle = 3;
        this.sousModeActuel = 0;
        toggleSousCategorie(true);
        if (tabAllDesserts != null) tabAllDesserts.setSelected(true);
        refreshView();
        resetScroll();
    }

    @FXML void filtrerDessertsOnly() { this.sousModeActuel = 3; refreshView(); }
    @FXML void filtrerBoissonsOnly() { this.sousModeActuel = 4; refreshView(); }
    @FXML void filtrerAllDessertsBoissons() { this.sousModeActuel = 0; refreshView(); }

    // --- AFFICHAGE ---

    private void refreshView() {
        gridPlats.getChildren().clear();
        if (categorieActuelle == 1) {
            updateSectionTitle(1);
            List<Plat> plats = MockService.getInstance().getPlatsParCategorie(1);
            plats.forEach(p -> gridPlats.getChildren().add(creerCarteVBox(p)));
        } else if (categorieActuelle == 2) {
            updateSectionTitle(2);
            List<Plat> plats = MockService.getInstance().getPlatsParCategorie(2);
            plats.forEach(p -> gridPlats.getChildren().add(creerCarteVBox(p)));
        } else {
            updateSectionTitle(sousModeActuel);
            if (sousModeActuel == 3 || sousModeActuel == 0)
                afficherSectionAvecTitre(t("Desserts", "Desserts", "甜点", "デザート"), MockService.getInstance().getPlatsParCategorie(3));

            if (sousModeActuel == 4 || sousModeActuel == 0)
                afficherSectionAvecTitre(t("Boissons", "Drinks", "饮料", "ドリンク"), MockService.getInstance().getPlatsParCategorie(4));
        }
    }

    private void afficherSectionAvecTitre(String titre, List<Plat> plats) {
        VBox section = new VBox(15);
        section.setAlignment(Pos.TOP_CENTER);
        section.setStyle("-fx-padding: 20 0 20 0;");
        section.setMaxWidth(Double.MAX_VALUE);

        Label sectionLabel = new Label(titre.toUpperCase());
        sectionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        FlowPane carteContainer = new FlowPane(30, 30);
        carteContainer.setAlignment(Pos.TOP_CENTER);
        carteContainer.setPrefWrapLength(1200);
        plats.forEach(p -> carteContainer.getChildren().add(creerCarteVBox(p)));

        section.getChildren().addAll(sectionLabel, carteContainer);
        gridPlats.getChildren().add(section);
    }

    private VBox creerCarteVBox(Plat p) {
        VBox carte = new VBox(10);
        carte.getStyleClass().add("card-produit");
        carte.setPrefWidth(280);
        carte.setAlignment(Pos.CENTER);

        String[] trads = getTraductionProduit(p.getId());

        ImageView imgView = new ImageView();
        imgView.setFitHeight(180); imgView.setFitWidth(240); imgView.setPreserveRatio(true);
        try {
            URL res = getClass().getResource("/fr/netwok/images/" + p.getImagePath());
            if (res != null) imgView.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) { e.printStackTrace(); }

        Label name = new Label(trads[0]);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        name.setWrapText(true); name.setTextAlignment(TextAlignment.CENTER);

        Label desc = new Label(trads[1]);
        desc.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        desc.setWrapText(true); desc.setTextAlignment(TextAlignment.CENTER);

        Label price = new Label(String.format("%.2f €", p.getPrix()));
        price.setStyle("-fx-text-fill: #00F0FF; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox quantityBox = creerSelecteurQuantite(p);
        carte.setOnMouseClicked(e -> ouvrirDetailPlat(p));
        carte.getChildren().addAll(imgView, name, desc, price, quantityBox);
        return carte;
    }

    private HBox creerSelecteurQuantite(Plat p) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER);
        Label lblQty = new Label(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
        lblQty.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Button btnMinus = new Button("-");
        btnMinus.setOnAction(e -> {
            if (MockService.getInstance().getQuantiteDuPlat(p) > 0) {
                MockService.getInstance().retirerDuPanier(p);
                lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
                updatePanierDisplay();
            }
        });

        Button btnPlus = new Button("+");
        btnPlus.setOnAction(e -> {
            MockService.getInstance().ajouterAuPanier(p);
            lblQty.setText(String.valueOf(MockService.getInstance().getQuantiteDuPlat(p)));
            updatePanierDisplay();
        });

        box.getChildren().addAll(btnMinus, lblQty, btnPlus);
        return box;
    }

    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();
        String unit = (nb > 1) ? t("articles", "items", "件商品", "点") : t("article", "item", "件商品", "点");
        lblNbArticles.setText(nb + " " + unit);
        lblTotal.setText(String.format("%.2f €", total));
    }

    private void updateSectionTitle(int mode) {
        if (sectionTitle == null) return;
        sectionTitle.setVisible(true); sectionTitle.setManaged(true);
        if (categorieActuelle == 1) sectionTitle.setText(t("Entrées", "Starters", "前菜", "前菜"));
        else if (categorieActuelle == 2) sectionTitle.setText(t("Plats", "Mains", "主菜", "メイン"));
        else {
            switch (mode) {
                case 3 -> sectionTitle.setText(t("Desserts", "Desserts", "甜点", "デザート"));
                case 4 -> sectionTitle.setText(t("Boissons", "Drinks", "饮料", "ドリンク"));
                default -> sectionTitle.setText(t("Desserts & Boissons", "Desserts & Drinks", "甜点 & 饮料", "デザート＆ドリンク"));
            }
        }
    }

    private void toggleSousCategorie(boolean show) {
        if (sousCategorieBar != null) {
            sousCategorieBar.setVisible(show);
            sousCategorieBar.setManaged(show);
        }
    }

    private void resetScroll() { if (scrollPane != null) scrollPane.setVvalue(0); }

    private void ouvrirDetailPlat(Plat p) {
        try {
            DetailPlatController.setPlatAfficher(p);
            NetwokApp.setRoot("views/detailPlat");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML void voirPanier() {
        try { NetwokApp.setRoot("views/panier"); } catch (IOException e) { e.printStackTrace(); }
    }
}