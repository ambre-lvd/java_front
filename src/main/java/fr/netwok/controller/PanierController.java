package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.ApiClient;
import fr.netwok.service.MockService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PanierController implements Initializable {

    @FXML private VBox vboxArticles;
    @FXML private Label lblSousTotal, lblTaxes, lblTotalFinal, lblTotalFinaltxt;
    @FXML private Label lblTitreRecap, colArticle, colQuantite, colPrix, colTotal;
    @FXML private Label lblTxtSousTotal, lblTxtTaxes, lblTxtTable, lblTxtNom, languageDisplay;
    @FXML private TextField txtNumeroTable, txtNomClient;
    @FXML private Button btnConfirmer, btnRetour, btnModifier;

    private String currentLanguage = "FR";
    private static final double TAUX_TAXE = 0.15;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Synchronisation avec la langue choisie dans le catalogue
        this.currentLanguage = CatalogueController.getLangueActuelle();
        traduire();
        chargerPanier();
    }

    // --- LOGIQUE DE TRADUCTION ---

    private String t(String fr, String en, String zh, String jp) {
        return switch (currentLanguage) {
            case "EN" -> en;
            case "ZH", "中文" -> zh;
            case "日本語" -> jp;
            default -> fr;
        };
    }

    @FXML
    void changeLanguage(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String nouvelleLangue = btn.getText().toUpperCase();
        CatalogueController.setLangueActuelle(nouvelleLangue);
        this.currentLanguage = nouvelleLangue;
        traduire();
        chargerPanier();
    }

    private void traduire() {
        lblTitreRecap.setText(t("Récapitulatif de commande", "Order Summary", "订单摘要", "ご注文内容の確認"));
        colArticle.setText(t("Article", "Item", "商品", "商品名"));
        colQuantite.setText(t("Quantité", "Quantity", "数量", "数量"));
        colPrix.setText(t("Prix Unit.", "Unit Price", "单价", "単価"));
        colTotal.setText(t("Total", "Total", "总计", "合計"));

        lblTxtSousTotal.setText(t("Sous-total :", "Subtotal:", "小计 :", "小計 :"));
        lblTxtTaxes.setText(t("Taxes (15%) :", "Taxes (15%):", "税费 (15%) :", "税金 (15%) :"));
        lblTotalFinaltxt.setText(t("TOTAL :", "TOTAL :", "總計 :", "合計 :"));

        lblTxtTable.setText(t("Numéro de table :", "Table Number:", "桌号 :", "テーブル番号 :"));
        lblTxtNom.setText(t("Nom du client (optionnel) :", "Client Name (optional):", "客户姓名 (可选) :", "お名前 (任意) :"));
        txtNumeroTable.setPromptText(t("Ex: 12", "e.g. 12", "例如: 12", "例: 12"));
        txtNomClient.setPromptText(t("Ex: Martin Dupont", "e.g. John Doe", "例如: 张三", "例: 山田太郎"));

        btnRetour.setText(t("← Retour", "← Back", "← 返回", "← 戻る"));
        btnModifier.setText(t("← Modifier", "← Edit", "← 修改", "← 修正"));
        btnConfirmer.setText(t("Confirmer la commande", "Confirm Order", "确认订单", "注文を確定する"));

        if (languageDisplay != null) {
            languageDisplay.setText(t("Langue : FR", "Language : EN", "语言 : 中文", "言語 : 日本語"));
        }
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

    // --- LOGIQUE PANIER ---

    private void chargerPanier() {
        vboxArticles.getChildren().clear();
        List<Plat> panier = MockService.getInstance().getPanier();

        // On utilise une Map pour compter les quantités par ID
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Plat> platMap = new HashMap<>();
        for(Plat p : panier) {
            counts.put(p.getId(), counts.getOrDefault(p.getId(), 0) + 1);
            platMap.put(p.getId(), p);
        }

        if (panier.isEmpty()) {
            Label vide = new Label(t("Votre panier est vide.", "Your cart is empty.", "您的购物车是空的。", "あなたのカートは空です。"));
            vide.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px; -fx-padding: 40 0 40 0;");
            vboxArticles.getChildren().add(vide);
            btnConfirmer.setDisable(true);
        } else {
            btnConfirmer.setDisable(false);
            int index = 0;
            for (String id : counts.keySet()) {
                Plat p = platMap.get(id);
                VBox ligne = creerLigneArticle(p, counts.get(id));

                ligne.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.millis(300), ligne);
                ft.setToValue(1);
                ft.setDelay(Duration.millis(index * 50));
                ft.play();

                vboxArticles.getChildren().add(ligne);
                index++;
            }
        }
        animerTotaux();
    }

    private VBox creerLigneArticle(Plat p, int qte) {
        VBox ligneComplete = new VBox(8);
        ligneComplete.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4); -fx-background-radius: 12; -fx-padding: 15;");

        HBox lp = new HBox(20);
        lp.setAlignment(Pos.CENTER_LEFT);

        // Traduction
        String[] trads = getTraductionProduit(p.getId());

        ImageView img = new ImageView();
        img.setFitWidth(80); img.setFitHeight(80); img.setPreserveRatio(true);
        try {
            URL res = getClass().getResource("/fr/netwok/images/" + p.getImagePath());
            if (res != null) img.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}

        VBox colInfo = new VBox(5);
        colInfo.setPrefWidth(350);
        Label lblNom = new Label(trads[0]);
        lblNom.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblDesc = new Label(trads[1]);
        lblDesc.setStyle("-fx-text-fill: #94a3b8;");
        colInfo.getChildren().addAll(lblNom, lblDesc);

        // Boutons +/-
        HBox boxQty = new HBox(10);
        boxQty.setAlignment(Pos.CENTER);
        boxQty.setPrefWidth(130);
        Button btnM = new Button("-");
        btnM.setOnAction(e -> { MockService.getInstance().retirerDuPanier(p); chargerPanier(); });
        Label lq = new Label(String.valueOf(qte));
        lq.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        Button btnP = new Button("+");
        btnP.setOnAction(e -> { MockService.getInstance().ajouterAuPanier(p); chargerPanier(); });
        boxQty.getChildren().addAll(btnM, lq, btnP);

        Label lprix = new Label(String.format("%.2f€", p.getPrix()));
        lprix.setStyle("-fx-text-fill: #94a3b8;");
        lprix.setPrefWidth(100); lprix.setAlignment(Pos.CENTER_RIGHT);

        Label lTot = new Label(String.format("%.2f€", p.getPrix() * qte));
        lTot.setStyle("-fx-text-fill: #00F0FF; -fx-font-weight: bold;");
        lTot.setPrefWidth(120); lTot.setAlignment(Pos.CENTER_RIGHT);

        lp.getChildren().addAll(img, colInfo, boxQty, lprix, lTot);
        ligneComplete.getChildren().add(lp);
        return ligneComplete;
    }

    private void animerTotaux() {
        double st = MockService.getInstance().getTotalPanier();
        double tx = st * TAUX_TAXE;
        lblSousTotal.setText(String.format("%.2f€", st));
        lblTaxes.setText(String.format("%.2f€", tx));
        lblTotalFinal.setText(String.format("%.2f€", st + tx));
    }

    @FXML
    void confirmerCommande() {
        if (txtNumeroTable.getText().isEmpty()) {
            txtNumeroTable.setStyle("-fx-border-color: #FF007F;");
            return;
        }

        // Logique d'envoi API...
        btnConfirmer.setText(t("✓ Envoyé", "✓ Sent", "✓ 已发送", "✓ 送信済み"));
        btnConfirmer.setDisable(true);

        // Redirection vers reçu après délai
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> {
                    try {
                        RecuCommandeController.setCommandeInfo("CMD-"+System.currentTimeMillis()%1000,
                                Integer.parseInt(txtNumeroTable.getText()), txtNomClient.getText(), currentLanguage);
                        NetwokApp.setRoot("views/recuCommande");
                    } catch (Exception e) { e.printStackTrace(); }
                });
            } catch (Exception e) {}
        }).start();
    }

    @FXML void retourCatalogue() throws IOException { NetwokApp.setRoot("views/catalogue"); }
}