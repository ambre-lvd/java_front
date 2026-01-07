package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
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

    @FXML private Label lblTitreRecu, lblNumCommande, lblNumTable, lblNomClient, lblHeure;
    @FXML private Label lblTxtTable, lblTxtNom, lblTxtHeure, lblTxtSousTotal, lblTxtTaxes, lblTxtTotal;
    @FXML private Label lblTxtNumCommande;
    @FXML private Label lblSousTotal, lblTaxes, lblTotal, lblMerci, lblAuRevoir;
    @FXML private Button btnTermine;
    @FXML private VBox vboxArticlesTicket, vboxMessage;

    private static String numeroCommande = "";
    private static int numeroTable = 0;
    private static String nomClient = "";
    private static String currentLanguage = "FR";

    public static void setCommandeInfo(String numCmd, int table, String client, String lang) {
        numeroCommande = numCmd;
        numeroTable = table;
        nomClient = client;
        currentLanguage = lang;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        traduireInterface();
        afficherTicket();
    }
    private String t(String fr, String en, String zh, String jp) {
        return switch (currentLanguage) {
            case "EN" -> en;
            case "中文" -> zh;
            case "日本語" -> jp;
            default -> fr;
        };
    }
    private void traduireInterface() {
        lblTitreRecu.setText(t("TICKET DE CAISSE", "RECEIPT", "收据", "領収書"));
        lblTxtNumCommande.setText(t("N° Commande :", "Order N°:", "订单号 :", "注文番号 :"));
        lblTxtTable.setText(t("Table :", "Table:", "桌号 :", "テーブル :"));
        lblTxtNom.setText(t("Client :", "Customer:", "客户 :", "様 :")); // Au Japon, le nom est suivi de "様"
        lblTxtHeure.setText(t("Heure :", "Time:", "时间 :", "日時 :"));

        lblTxtSousTotal.setText(t("Sous-total :", "Subtotal:", "小计 :", "小計 :"));
        lblTxtTaxes.setText(t("Taxes (15%) :", "Taxes (15%):", "税费 (15%) :", "税 (15%) :"));
        lblTxtTotal.setText(t("TOTAL :", "TOTAL:", "总计 :", "合計 :"));

        btnTermine.setText(t("✓ Terminé", "✓ Done", "✓ 完成", "✓ 完了"));

        lblMerci.setText(t("Merci de votre commande !", "Thank you for your order!", "感谢您的订购！", "ご注文ありがとうございました！"));
        lblAuRevoir.setText(t("À bientôt chez NETWOK", "See you soon at NETWOK", "NETWOK 期待您的再次光临", "またのご来店をお待ちしております"));
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

    private void afficherTicket() {
        lblNumCommande.setText(numeroCommande);
        lblNumTable.setText(String.valueOf(numeroTable));
        String clientVide = t("Non spécifié", "Not specified", "未指定", "指定なし");
        lblNomClient.setText(nomClient.isEmpty() ? clientVide : nomClient);
        lblHeure.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        vboxArticlesTicket.getChildren().clear(); // Toujours vider avant de remplir
        List<Plat> panier = MockService.getInstance().getPanier();
        Set<String> idsTraites = new HashSet<>();

        for (Plat p : panier) {
            if (!idsTraites.contains(p.getId())) {
                idsTraites.add(p.getId());
                int qte = MockService.getInstance().getQuantiteDuPlat(p);
                String[] trads = getTraductionProduit(p.getId());
                vboxArticlesTicket.getChildren().add(creerLigneArticleTicket(trads[0], qte, p.getPrix()));
            }
        }
        calculerTotaux();
    }

    private HBox creerLigneArticleTicket(String nom, int qte, double prix) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(nom + " x" + qte);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        HBox.setHgrow(lbl, Priority.ALWAYS);
        lbl.setMaxWidth(Double.MAX_VALUE);

        Label lblP = new Label(String.format("%.2f€", prix * qte));
        lblP.setStyle("-fx-text-fill: #00F0FF; -fx-font-weight: bold;");
        ligne.getChildren().addAll(lbl, lblP);
        return ligne;
    }

    private void calculerTotaux() {
        double st = MockService.getInstance().getTotalPanier();
        double tx = st * 0.15;
        lblSousTotal.setText(String.format("%.2f€", st));
        lblTaxes.setText(String.format("%.2f€", tx));
        lblTotal.setText(String.format("%.2f€", st + tx));
    }

    @FXML void terminerCommande() {
        VBox ticketParent = (VBox) vboxArticlesTicket.getParent().getParent();
        FadeTransition fo = new FadeTransition(Duration.millis(500), ticketParent);
        fo.setToValue(0);
        fo.setOnFinished(e -> {
            vboxMessage.setOpacity(0);
            FadeTransition fi = new FadeTransition(Duration.millis(800), vboxMessage);
            fi.setToValue(1);
            fi.play();
            PauseTransition p = new PauseTransition(Duration.seconds(4));
            p.setOnFinished(ev -> retournerCatalogue());
            p.play();
        });
        fo.play();
    }

    private void retournerCatalogue() {
        try {
            MockService.getInstance().viderPanier();
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) { e.printStackTrace(); }
    }
}