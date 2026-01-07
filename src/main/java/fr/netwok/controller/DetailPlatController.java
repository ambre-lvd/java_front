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

    @FXML private Label epices;
    @FXML private Label acc;
    @FXML private Label qt;

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
    public static void setLangueActuelle(String langue) {
        langueActuelle = langue;
    }
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
        this.langueActuelle = CatalogueController.getLangueActuelle();
        if (platAfficher != null) {
            platActuel = platAfficher;
            chargerDetailPlat();
            platAfficher = null;
        }
        updatePanierDisplay();
        traduireInterface();
    }

    // --- LOGIQUE DE TRADUCTION ---

    private String t(String fr, String en, String zh, String jp) {
        return switch (langueActuelle) {
            case "EN" -> en;
            case "中文" -> zh;
            case "日本語" -> jp;
            default -> fr;
        };
    }

    private void traduireInterface() {
        if (btnAjouter != null) btnAjouter.setText(t("Ajouter au panier", "Add to basket", "加入购物车", "かごに追加"));
        if (btnRetour != null) btnRetour.setText(t("Retour", "Back", "返回", "戻る"));
        if (btnVoirPanier != null) btnVoirPanier.setText(t("Voir mon panier", "View my basket", "查看购物车", "かごを見る"));
        if (epices != null) epices.setText(t("Épice", "Spice", "辣度", "辛さ"));
        if (acc != null) acc.setText(t("Accompagnement", "Side", "配菜", "付け合わせ"));
        if (rbDoux != null) rbDoux.setText(t("Doux", "Mild", "不辣", "甘口"));
        if (rbMoyen != null) rbMoyen.setText(t("Moyen", "Medium", "微辣", "中辛"));
        if (rbFort != null) rbFort.setText(t("Fort", "Spicy", "大辣", "大辛"));
        if (rbRiz != null) rbRiz.setText(t("Riz", "Rice", "米饭", "ライス"));
        if (rbNouilles != null) rbNouilles.setText(t("Nouilles", "Noodles", "面条", "麺"));
        if (qt != null) qt.setText(t("Quantité", "Quantity", "数量", "数量"));
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

    private void chargerDetailPlat() {
        if (platActuel == null) return;

        String[] trads = getTraductionProduit(platActuel.getId());
        String nomTrad = (trads != null) ? trads[0] : platActuel.getNom();
        String descTrad = (trads != null) ? trads[1] : platActuel.getDescription();

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
        sb.append(t("Allergènes : peut contenir des traces de gluten, soja\n", "Allergens: may contain traces of gluten, soy\n", "過敏原：可能含有麩質、大豆的痕跡\n", "アレルゲン：小麦、大豆の成分が含まれている可能性があります\n"));
        sb.append(t("Calories : environ 450 kcal\n", "Calories: approx. 450 kcal\n", "熱量：約 450 大卡\n", "エネルギー：約 450 kcal\n"));
        sb.append(t("Temps de préparation : 15-20 min", "Preparation time: 15-20 min", "準備時間：15-20 分鐘", "調理時間：15-20 分"));
        return sb.toString();
    }

    // --- ACTIONS ---

    @FXML
    void ajouterAuPanier() {
        if (platActuel == null) return;

        // --- 1. RÉCUPÉRATION DU NIVEAU DE PIMENT ---
        // On définit un entier : 0 pour Doux, 1 pour Moyen, 2 pour Fort
        int niveauPiment = 0;
        if (rbMoyen.isSelected()) {
            niveauPiment = 1;
        } else if (rbFort.isSelected()) {
            niveauPiment = 2;
        }
        platActuel.setPimentChoisi(niveauPiment);

        // --- 2. RÉCUPÉRATION DE L'ACCOMPAGNEMENT ---
        // On définit un entier : 0 pour Riz, 1 pour Nouilles
        int niveauAccompagnement = 0;
        if (rbNouilles.isSelected()) {
            niveauAccompagnement = 1;
        }
        platActuel.setAccompagnementChoisi(niveauAccompagnement);

        // --- 3. AJOUT AU PANIER SELON LA QUANTITÉ ---
        for (int i = 0; i < quantite; i++) {
            // On utilise l'instance du service pour stocker le plat avec ses options
            MockService.getInstance().ajouterAuPanier(platActuel);
        }

        // --- 4. MISE À JOUR DE L'INTERFACE ET FEEDBACK ---
        updatePanierDisplay();

        btnAjouter.setText(t("✓ Ajouté !", "✓ Added!", "✓ 已添加！", "✓ 追加しました！"));
        btnAjouter.setDisable(true);

        // Petit thread pour retourner au catalogue automatiquement après l'ajout
        new Thread(() -> {
            try {
                Thread.sleep(800);
                javafx.application.Platform.runLater(this::retourCatalogue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void changeLanguage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String langue = btn.getText().toUpperCase();
        CatalogueController.setLangueActuelle(langue);
        this.langueActuelle = langue;
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
        lblNbArticles.setText(nb + " " + t("articles", "items", "文章", "記事"));
        lblTotal.setText(String.format("%.2f €", total));
    }
}