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
import java.util.ArrayList;
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

    // --- VARIABLES DE SAUVEGARDE ---
    private static String numeroCommande = "";
    private static int numeroTable = 0;
    private static String nomClient = "";
    private static String currentLanguage = "FR";

    // Sauvegarde des données car le Panier est vidé
    private static double montantTotalSauvegarde = 0.0;
    private static List<Plat> panierSauvegarde = new ArrayList<>();

    /**
     * Méthode appelée par PanierController pour passer les infos avant de changer de page.
     */
    public static void setCommandeInfo(String numCmd, int table, String client, String lang, double total, List<Plat> items) {
        numeroCommande = numCmd;
        numeroTable = table;
        nomClient = client;
        currentLanguage = lang;
        montantTotalSauvegarde = total;
        // Important : On crée une COPIE de la liste
        panierSauvegarde = new ArrayList<>(items);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        traduireInterface();
        afficherTicket();
    }

    private String t(String fr, String en, String zh, String jp, String es, String ru, String th, String co) {
        return switch (currentLanguage) {
            case "EN" -> en;
            case "中文" -> zh;
            case "日本語" -> jp;
            case "ES" -> es;
            case "PY" -> ru;
            case "ไทย" -> th;
            case "한국말" -> co;
            default -> fr;
        };
    }

    private void traduireInterface() {
        lblTitreRecu.setText(t("TICKET DE CAISSE", "RECEIPT", "收据", "領収書", "RECIBO", "ЧЕК", "ใบเสร็จรับเงิน", "영수증"));
        lblTxtNumCommande.setText(t("N° Commande :", "Order N°:", "订单号 :", "注文番号 :", "N° Pedido:", "№ Заказа:", "หมายเลขคำสั่งซื้อ:", "주문 번호:"));
        lblTxtTable.setText(t("Table :", "Table:", "桌号 :", "テーブル :", "Mesa:", "Стол:", "โต๊ะ:", "테이블:"));
        lblTxtNom.setText(t("Client :", "Customer:", "客户 :", "様 :", "Cliente:", "Клиент:", "ลูกค้า:", "고객명:"));
        lblTxtHeure.setText(t("Heure :", "Time:", "时间 :", "日時 :", "Hora:", "Время:", "เวลา:", "시간:"));
        lblTxtSousTotal.setText(t("Sous-total :", "Subtotal:", "小计 :", "小計 :", "Subtotal:", "Промежуточный итог:", "ราคารวมก่อนภาษี:", "소계:"));
        lblTxtTaxes.setText(t("Taxes (15%) :", "Taxes (15%):", "税费 (15%) :", "税 (15%) :", "Impuestos (15%):", "Налоги (15%):", "ภาษี (15%):", "세금 (15%):"));
        lblTxtTotal.setText(t("TOTAL :", "TOTAL:", "总计 :", "合計 :", "TOTAL:", "ИТОГО:", "ยอดรวมสุทธิ:", "총합계:"));
        btnTermine.setText(t("✓ Terminé", "✓ Done", "✓ 完成", "✓ 完了", "✓ Finalizar", "✓ Готово", "✓ เสร็จสิ้น", "✓ 완료"));
        lblMerci.setText(t("Merci de votre commande !", "Thank you for your order!", "感谢您的订购！", "ご注文ありがとうございました！", "¡Gracias por su pedido!", "Спасибо за заказ!", "ขอบคุณสำหรับคำสั่งซื้อของคุณ!", "주문해 주셔서 감사합니다!"));
        lblAuRevoir.setText(t("À bientôt chez NETWOK", "See you soon at NETWOK", "NETWOK 期待您的再次光临", "またのご来店をお待ちしております", "¡Hasta pronto en NETWOK!", "До встречи в NETWOK", "แล้วพบกันใหม่ที่ NETWOK", "NETWOK에서 곧 다시 뵙겠습니다"));
    }

    private String[] getTraductionProduit(String id) {
        return switch (id) {
            // BOISSONS
            case "B1" -> new String[]{t("Ice Tea", "Iced Tea", "冰茶", "アイスティー", "Té Helado", "Холодный чай", "ชาดำเย็น", "아이스티"), t("Maison, citron vert", "Homemade, lime", "自制青柠味", "自家製、ライム入り", "Casero, lima", "Домашний, с лаймом", "โฮมเมด มะนาว", "수제, 라임")};
            case "B2" -> new String[]{t("Bière Tsingtao", "Tsingtao Beer", "青岛啤酒", "青島ビール", "Cerveza Tsingtao", "Пиво Циндао", "เบียร์ชิงเต่า", "칭따오 맥주"), t("Bière blonde 33cl", "Lager beer 33cl", "33毫升", "ラガービール 33cl", "Cerveza rubia 33cl", "Светлое пиво 33cl", "เบียร์ลาเกอร์ 33cl", "라거 맥주 33cl")};
            case "B3" -> new String[]{t("Limonade Jap", "Jap Lemonade", "弹珠汽水", "ラムネ", "Limonada Jap", "Японский лимонад", "ราเมเนะ", "라무네"), t("Ramune à bille", "Ramune with marble", "日式传统汽水", "ビー玉入りラムネ", "Ramune con canica", "Рамунэ с шариком", "น้ำโซดาญี่ปุ่น", "구슬 사이다")};
            case "B4" -> new String[]{t("Jus de Coco", "Coconut Juice", "椰子汁", "ココナッツジュース", "Jugo de Coco", "Кокосовый сок", "น้ำมะพร้าว", "코코넛 주스"), t("Avec morceaux", "With chunks", "果肉果汁", "果肉入り", "Con trozos", "С кусочками", "มีเนื้อมะพร้าว", "과육 포함")};
            case "B5" -> new String[]{t("Sake", "Sake", "清酒", "日本酒", "Sake", "Саке", "สาเก", "사케"), t("Petit pichet", "Small pitcher", "小瓶装", "徳利（小）", "Jarra pequeña", "Маленький кувшин", "กาเล็ก", "도구리 (소)")};

            // DESSERTS
            case "D1" -> new String[]{t("Perles de Coco", "Coconut Pearls", "椰丝球", "ココナッツ団子", "Perlas de Coco", "Кокосовые шарики", "ขนมต้มมะพร้าว", "코코넛 경단"), t("2 pièces, tiède", "2 pieces, warm", "2个, 温热", "2個、温かい", "2 piezas, tibio", "2 штуки, теплые", "2 ชิ้น อุ่นๆ", "2개, 따뜻함")};
            case "D2" -> new String[]{t("Mochi Glacé", "Iced Mochi", "冰淇淋大福", "雪見だいふく", "Mochi Helado", "Моти-мороженое", "โมจิไอศกรีม", "모찌 아이스크림"), t("2 pièces, Vanille et Matcha", "2 pieces, Vanilla/Matcha", "2个, 香草和抹茶", "2個、バニラと抹茶", "2 piezas, Vainilla/Matcha", "2 штуки, ваниль/матча", "2 ชิ้น วานิลลา/มัทฉะ", "2개, 바닐라/말차")};
            case "D3" -> new String[]{t("Mangue Fraîche", "Fresh Mango", "鲜芒果", "フレッシュマンゴー", "Mango Fresco", "Свежий манго", "มะม่วงสด", "생망고"), t("Tranches de mangue", "Mango slices", "新鲜切片", "マンゴースライス", "Rodajas de mango", "Ломтики манго", "มะม่วงหั่นชิ้น", "망고 슬라이스")};
            case "D4" -> new String[]{t("Banane Flambée", "Flambé Banana", "拔丝香蕉", "バナナのフランベ", "Plátano Flambeado", "Фламбированный банан", "กล้วยทอดฟลมเบ้", "바나나 플람베"), t("Au saké", "With sake", "清酒烹制", "日本酒風味", "Con sake", "С саке", "ผสมสาเก", "사케 풍미")};
            case "D5" -> new String[]{t("Nougat Chinois", "Chinese Nougat", "芝麻糖", "中華風の中華菓子", "Turrón Chino", "Китайская нуга", "ตุ๊บตั๊บจีน", "중국식 누가"), t("Aux graines de sésame", "With sesame seeds", "芝麻味", "ゴマ入り", "Con semillas de sésamo", "С кунжутом", "ผสมงา", "참깨 포함")};

            // ENTREES
            case "E1" -> new String[]{t("Nems Poulet", "Chicken Nems", "鸡肉春卷", "鶏肉の揚げ春巻き", "Rollitos de Pollo", "Немы с курицей", "ปอเปี๊ยะทอดไก่", "치킨 넴"), t("4 pièces, sauce nuoc-mâm", "4 pieces, fish sauce", "4个, 鱼露", "4個、ヌクマムソース", "4 piezas, salsa de pescado", "4 штуки, рыбный соус", "4 ชิ้น พร้อมน้ำจิ้มปลา", "4개, 피쉬 소스")};
            case "E2" -> new String[]{t("Rouleaux Printemps", "Spring Rolls", "夏卷", "生春巻き", "Rollitos de Primavera", "Спринг-роллы", "ปอเปี๊ยะสด", "월남쌈"), t("Crevette, menthe, riz", "Shrimp, mint, rice", "鲜虾, 薄荷", "海老、ミント、米粉", "Camarón, menta, arroz", "Креветки, мята, рис", "กุ้ง มิ้นต์ เส้นหมี่", "새우, 민트, 쌀면")};
            case "E3" -> new String[]{t("Gyozas Poulet", "Chicken Gyozas", "鸡肉饺子", "鶏肉餃子", "Gyozas de Pollo", "Гёдза с курицей", "เกี๊ยวซ่าไก่", "치킨 교자"), t("Raviolis grillés (5 pièces)", "Grilled dumplings (5 pcs)", "煎饺 (5个)", "焼き餃子（5個）", "Dumplings a la plancha (5 pzas)", "Жареные пельмени (5 шт)", "เกี๊ยวซ่าย่าง (5 ชิ้น)", "군만두 (5개)")};
            case "E4" -> new String[]{t("Samoussas Boeuf", "Beef Samoussas", "牛肉咖喱角", "牛肉のサモサ", "Samosas de Ternera", "Самоса с говядиной", "ซามูซ่าเนื้อ", "소고기 사모사"), t("Croustillant aux épices", "Crispy with spices", "香脆辣味", "スパイス香るカリカリ揚げ", "Crujiente con especias", "Хрустящие со специями", "แป้งกรอบสอดไส้เครื่องเทศ", "매콤하고 바삭함")};
            case "E5" -> new String[]{t("Salade de Chou", "Cabbage Salad", "凉拌卷心菜", "キャベツのサラダ", "Ensalada de Col", "Салат из капусты", "สลัดกะหล่ำปลี", "양배추 샐러드"), t("Chou blanc, marinade sésame", "White cabbage, sesame", "白菜, 芝麻汁", "白キャベツの胡麻マリネ", "Col blanca, sésamo", "Белокочанная капуста, кунжут", "กะหล่ำปลีหมักงา", "코울슬로, 참깨 드레싱")};
            case "E6" -> new String[]{t("Soupe Miso", "Miso Soup", "味噌汤", "味噌汁", "Sopa Miso", "Мисо суп", "ซุปมิโซะ", "미소된장국"), t("Tofu, algues wakame", "Tofu, wakame seaweed", "豆腐, 海带", "豆腐、わかめ", "Tofu, algas wakame", "Тофу, водоросли вакаме", "เต้าหู้ สาหร่ายวากาเมะ", "두부, 미역")};
            case "E7" -> new String[]{t("Tempura Crevettes", "Shrimp Tempura", "天妇罗虾", "海老の天ぷら", "Tempura de Camarones", "Темпура с креветками", "เทมปุระกุ้ง", "새우 튀김"), t("Beignets légers (4 pièces)", "Light fritters (4 pcs)", "脆炸 (4个)", "衣揚げ（4個）", "Fritura ligera (4 piezas)", "Легкий кляр (4 штуки)", "กุ้งชุบแป้งทอด (4 ชิ้น)", "가벼운 튀김 (4개)")};
            case "E8" -> new String[]{t("Yakitori Boeuf", "Beef Yakitori", "牛肉串", "牛串焼き", "Yakitori de Ternera", "Якитори из говядины", "ยากิโทริเนื้อ", "소고기 야키토리"), t("Brochettes boeuf-fromage", "Beef-cheese skewers", "芝士牛肉串", "牛チーズ串", "Brochetas ternera-queso", "Шашлычки говядина-сыр", "เนื้อพันชีสเสียบไม้", "소고기 치즈 꼬치")};
            case "E9" -> new String[]{t("Edamame", "Edamame", "毛豆", "枝豆", "Edamame", "Эдамаме", "ถั่วแระญี่ปุ่น", "에다마메"), t("Fèves de soja, sel de mer", "Soybeans, sea salt", "盐水大豆", "塩ゆで枝豆", "Soja, sal de mar", "Соевые бобы, морская соль", "ถั่วเหลืองฝักอ่อนโรยเกลือ", "자숙면, 바다 소금")};
            case "E10" -> new String[]{t("Mix Dim Sum", "Dim Sum Mix", "点心拼盘", "点心セット", "Mix de Dim Sum", "Димсам микс", "ติ่มซำรวมมิตร", "딤섬 모듬"), t("Panier vapeur (6 pièces)", "Steamed basket (6 pcs)", "蒸笼 (6个)", "蒸し器（6個）", "Cesta al vapor (6 piezas)", "Паровая корзина (6 шт)", "เข่งนึ่ง (6 ชิ้น)", "찜통 (6개)")};

            // PLATS
            case "P1" -> new String[]{t("Pad Thaï", "Pad Thai", "泰式炒河粉", "パッタイ", "Pad Thai", "Пад-тай", "ผัดไทย", "팥타이"), t("Nouilles de riz, crevettes", "Rice noodles, shrimp", "大米粉, 鲜虾", "米粉の麺、海老", "Fideos de arroz, camarones", "Рисовая лапша, креветки", "เส้นเล็กผัดกุ้ง", "쌀국수, 새우")};
            case "P2" -> new String[]{t("Bo Bun Boeuf", "Beef Bo Bun", "牛肉米粉", "牛焼肉のブン", "Bo Bun de Ternera", "Бо Бун с говядиной", "โบブンเนื้อ", "소고기 보분"), t("Vermicelles, boeuf sauté", "Vermicelli, sautéed beef", "干拌粉, 炒牛肉", "米麺、牛肉炒め", "Fideos, ternera salteada", "Вермишель, жареная говядина", "เส้นหมี่ เนื้อผัด", "버미셀리, 소고기 볶음")};
            case "P3" -> new String[]{t("Curry Vert", "Green Curry", "绿咖喱", "グリーンカレー", "Curry Verde", "Зеленый карри", "แกงเขียวหวาน", "그린 커리"), t("Poulet, lait de coco", "Chicken, coconut milk", "鸡肉,椰奶", "鶏肉、ココナッツミルク", "Pollo, leche de coco", "Курица, кокосовое молоко", "ไก่ กะทิ", "치킨, 코코넛 밀크")};
            case "P4" -> new String[]{t("Riz Cantonais", "Cantonese Rice", "扬州炒饭", "チャーハン", "Arroz Cantonés", "Рис по-кантонски", "ข้าวผัดหยางโจว", "볶음밥"), t("Riz sauté, jambon", "Fried rice, ham", "火腿蛋炒饭", "ハム入り炒飯", "Arroz salteado, jamón", "Жареный рис, ветчина", "ข้าวผัดใส่แฮม", "햄 볶음밥")};
            case "P5" -> new String[]{t("Porc au Caramel", "Caramel Pork", "红烧肉", "豚肉のキャラメル煮", "Cerdo al Caramelo", "Свинина в карамели", "หมูหวาน", "돼지갈비찜"), t("Travers de porc confits", "Candied ribs", "焦糖猪排", "豚バラ肉の甘辛煮", "Costillas de cerdo confitadas", "Засахаренные ребрышки", "ซี่โครงหมูตุ๋นหวาน", "졸인 돼지갈비")};
            case "P6" -> new String[]{t("Canard Laqué", "Peking Duck", "北京烤鸭", "北京ダック", "Pato Laqueado", "Утка по-пекински", "เป็ดปักกิ่ง", "베이징 덕"), t("Avec crêpes", "With pancakes", "附荷叶饼", "薄餅添え", "Con crepas", "С блинчиками", "เสิร์ฟพร้อมแป้งห่อ", "전병 포함")};
            case "P7" -> new String[]{t("Bibimbap", "Bibimbap", "石锅拌饭", "ビビンバ", "Bibimbap", "Пибимпап", "บิบิมบับ", "비빔밥"), t("Riz, boeuf, légumes", "Rice, beef, vegetables", "米饭, 牛肉, 蔬菜", "ご飯、牛肉、野菜", "Arroz, ternera, verduras", "Рис, говядина, овощи", "ข้าว เนื้อ ผัก", "밥, 소고기, 야채")};
            case "P8" -> new String[]{t("Tonkotsu Ramen", "Tonkotsu Ramen", "豚骨拉面", "豚骨ラーメン", "Tonkotsu Ramen", "Тонкоцу рамэн", "ทงคัตสึราเมน", "돈코츠 라멘"), t("Bouillon porc, nouilles", "Pork broth, noodles", "浓汤面", "豚骨スープ、麺", "Caldo de cerdo, fideos", "Свиной бульон, лапша", "ซุปกระดูกหมู บะหมี่", "돼지 사골 육수, 면")};
            case "P9" -> new String[]{t("Mix Sushi 12", "Sushi Mix 12", "寿司拼盘", "寿司盛り合わせ 12貫", "Mix de Sushi 12", "Ассорти суши 12 шт", "ซูชิรวม 12 ชิ้น", "모듬 초밥 12피스"), t("Assortiment de sushi", "Sushi assortment", "12个寿司", "寿司のアソート", "Surtido de sushi", "Набор суши", "ซูชิหลากหลายหน้า", "다양한 초밥")};
            case "P10" -> new String[]{t("Wok Végé", "Vege Wok", "素食炒面", "野菜の炒め物", "Wok Vegetariano", "Вок вегетарианский", "ว็อกมังสวิรัติ", "야채 워크"), t("Nouilles, tofu", "Noodles, tofu", "面条, 豆腐", "麺、豆腐", "Fideos, tofu", "Лапша, тофу", "บะหมี่ เต้าหู้", "면, 두부")};
            default -> new String[]{"?", "?", "?", "?", "?", "?", "?", "?"};
        };
    }

    private void afficherTicket() {
        lblNumCommande.setText(numeroCommande);
        lblNumTable.setText(String.valueOf(numeroTable));
        String clientVide = t("Non spécifié", "Not specified", "未指定", "指定なし", "No especificado", "Не указано", "ไม่ได้ระบุ", "미지정");
        lblNomClient.setText(nomClient.isEmpty() ? clientVide : nomClient);
        lblHeure.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
<<<<<<< HEAD
        vboxArticlesTicket.getChildren().clear();
        List<Plat> panier = MockService.getInstance().getPanier();
=======

        vboxArticlesTicket.getChildren().clear();

        // On utilise la liste SAUVEGARDÉE (panierSauvegarde) et non MockService qui est vide
        List<Plat> panier = panierSauvegarde;
>>>>>>> ab9c4c8f5bc8a3993343f0a0d8ea4ea7059dd33d
        Set<String> idsTraites = new HashSet<>();

        for (Plat p : panier) {
            if (!idsTraites.contains(p.getId())) {
                idsTraites.add(p.getId());

                // --- CORRECTION QUANTITÉ ---
                // On calcule la quantité manuellement depuis la liste sauvegardée
                // car le MockService a été vidé
                int qte = 0;
                for(Plat temp : panier) {
                    if(temp.getId().equals(p.getId())) {
                        qte++;
                    }
                }

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
        // On utilise le total SAUVEGARDÉ et pas celui du service vide
        double st = montantTotalSauvegarde;
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
            // Pas besoin de vider ici, le panier est déjà vide depuis l'écran précédent
            NetwokApp.setRoot("views/catalogue");
        } catch (IOException e) { e.printStackTrace(); }
    }
}