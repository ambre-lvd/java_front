package fr.netwok.controller;

import fr.netwok.NetwokApp;
import fr.netwok.model.Plat;
import fr.netwok.service.MockService;
import javafx.application.Platform;
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
    @FXML private Label detail;
    @FXML private Label desc;
    @FXML private Label info;
    @FXML private Label opt;

    @FXML private RadioButton rbDoux;
    @FXML private RadioButton rbMoyen;
    @FXML private RadioButton rbFort;
    @FXML private RadioButton rbRiz;
    @FXML private RadioButton rbNouilles;

    @FXML private Label lblQuantite;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;
    @FXML private Button btnVoirPanier;
    @FXML private Button panierlogo;


    @FXML private javafx.scene.layout.Pane vboxOptionsPiment;
    @FXML private javafx.scene.layout.Pane vboxOptionsAccompagnement;

    private static Plat platAfficher;
    private Plat platActuel;
    private int quantite = 1;
    private static String langueActuelle = "FR";

    public static void setPlatAfficher(Plat p) {
        platAfficher = p;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        langueActuelle = CatalogueController.getLangueActuelle();
        if (platAfficher != null) {
            platActuel = platAfficher;
            chargerDetailPlat();
            // On ne met pas platAfficher à null ici si on veut pouvoir changer de langue
            // sur la même page, ou alors on s'assure que platActuel est bien conservé.
        }
        updatePanierDisplay();
        traduireInterface();
    }

    private void chargerDetailPlat() {
        if (platActuel == null) return;

        // --- 1. RÉCUPÉRATION DES TRADUCTIONS DYNAMIQUES ---
        String[] traductions = getTraductionProduit(platActuel.getId());
        String nomTraduit = traductions[0];
        String descTraduite = traductions[1];

        // --- 2. MISE À JOUR DES TEXTES ---
        lblNom.setText(nomTraduit.equals("?") ? platActuel.getNom() : nomTraduit);
        lblPrix.setText(String.format("%.2f€", platActuel.getPrix()));

        // Description
        if (!descTraduite.equals("?")) {
            txtDescription.setText(descTraduite);
        } else {
            txtDescription.setText(platActuel.getDescription() != null ? platActuel.getDescription() : "");
        }
        txtDescription.setStyle("-fx-fill: white;");

        // Informations fixes (Allergènes, etc.)
        txtInfos.setText(genererInformations());
        txtInfos.setStyle("-fx-fill: white;");

        // --- 3. GESTION DE L'IMAGE ---
        try {
            String rawPath = platActuel.getImagePath();
            if (rawPath != null && !rawPath.isEmpty()) {
                if (rawPath.startsWith("/")) rawPath = rawPath.substring(1);
                String fullPath = rawPath.startsWith("images") ? "/fr/netwok/" + rawPath : "/fr/netwok/images/" + rawPath;
                URL imageUrl = getClass().getResource(fullPath);
                if (imageUrl != null) {
                    imgPlat.setImage(new Image(imageUrl.toExternalForm()));
                } else {
                    imgPlat.setImage(new Image(getClass().getResource("/fr/netwok/images/logo_main.png").toExternalForm()));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur image : " + e.getMessage());
        }

        // --- 4. MASQUAGE DES OPTIONS (Desserts ID=3 & Boissons ID=4) ---
        int categoryId = platActuel.getCategorie();
        boolean isOptionHidden = (categoryId == 3 || categoryId == 4);

        if (opt != null) { opt.setVisible(!isOptionHidden); opt.setManaged(!isOptionHidden); }
        if (vboxOptionsPiment != null) { vboxOptionsPiment.setVisible(!isOptionHidden); vboxOptionsPiment.setManaged(!isOptionHidden); }
        if (vboxOptionsAccompagnement != null) { vboxOptionsAccompagnement.setVisible(!isOptionHidden); vboxOptionsAccompagnement.setManaged(!isOptionHidden); }

        if (isOptionHidden) {
            platActuel.setPimentChoisi(0);
            platActuel.setAccompagnementChoisi(0);
        }
    }

    @FXML
    void ajouterAuPanier() {
        if (rbMoyen.isSelected()) platActuel.setPimentChoisi(1);
        else if (rbFort.isSelected()) platActuel.setPimentChoisi(2);
        else platActuel.setPimentChoisi(0);

        if (rbNouilles.isSelected()) platActuel.setAccompagnementChoisi(1);
        else platActuel.setAccompagnementChoisi(0);

        for (int i = 0; i < quantite; i++) {
            MockService.getInstance().ajouterAuPanier(platActuel);
        }

        btnAjouter.setText(t("✓ Ajouté", "✓ Added", "✓ 已添加", "✓ 追加済み", "✓ ¡Añadido!", "✓ Добавлено", "✓ เพิ่มแล้ว", "✓ 추가됨"));
        btnAjouter.setDisable(true);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(this::retourCatalogue);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }

    private String genererInformations() {
        String allergens = platActuel.getAllergens() != null && !platActuel.getAllergens().isEmpty() 
            ? platActuel.getAllergens() 
            : t("Non spécifié", "Not specified", "未指定", "指定なし", "No especificado", "Не указано", "ไม่ระบุ", "미지정");
        
        int calories = platActuel.getCalories();
        String prepTime = platActuel.getPrepTime() != null && !platActuel.getPrepTime().isEmpty() 
            ? platActuel.getPrepTime() 
            : "---";
        
        return t("Allergènes : ", "Allergens: ", "過敏原：", "アレルゲン：", "Alérgenos: ", "Аллергены: ", "ข้อมูลสำหรับผู้แพ้อาหาร: ", "알레르기: ") + allergens + "\n" +
                t("Calories : ", "Calories: ", "熱量：", "エネルギー：", "Calorías: ", "Калории: ", "แคลอรี่: ", "칼로리: ") + calories + " kcal\n" +
                t("Temps : ", "Time: ", "準備時間：", "調理時間：", "Tiempo: ", "Время: ", "เวลา: ", "시간: ") + prepTime;
    }

    @FXML
    void changeLanguage(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String langue = btn.getText().toUpperCase();
        CatalogueController.setLangueActuelle(langue);
        langueActuelle = langue;
        traduireInterface();
        chargerDetailPlat();
        updatePanierDisplay();
    }

    private void traduireInterface() {
        detail.setText(t("DÉTAIL DU PLAT", "DISH DETAILS", "菜品詳情", "料理の詳細", "DETALLES", "ДЕТАЛИ", "รายละเอียด", "상세 정보"));
        desc.setText(t("Description", "Description", "描述", "説明", "Descripción", "Описание", "คำอธิบาย", "설명"));
        info.setText(t("Informations", "Information", "資訊", "情報", "Información", "Информация", "ข้อมูล", "정보"));
        opt.setText(t("OPTIONS", "OPTIONS", "選項", "オプション", "OPCIONES", "ОПЦИИ", "ตัวเลือก", "옵션"));
        epices.setText(t("Niveau de piment", "Spiciness", "辣度", "辛さ", "Nivel de picante", "Острота", "ระดับความเผ็ด", "매운 정도"));
        acc.setText(t("Accompagnement", "Side dish", "配菜", "付け合わせ", "Acompañamiento", "Garniture", "เครื่องเคียง", "사이드"));
        qt.setText(t("Quantité", "Quantity", "數量", "数量", "Cantidad", "Количество", "จำนวน", "수량"));
        btnAjouter.setText(t("Ajouter au panier", "Add to cart", "加入購物車", "カートに入れる", "Añadir", "В корзину", "ใส่ตะกร้า", "담기"));
        btnRetour.setText(t("Retour", "Back", "返回", "戻る", "Volver", "Назад", "กลับ", "뒤로"));
        rbDoux.setText(t("Doux", "Mild", "不辣", "甘口", "Suave", "Мягкий", "เผ็ดน้อย", "순한맛"));
        rbMoyen.setText(t("Moyen", "Medium", "中辣", "中辛", "Medio", "Средний", "เผ็ดกลาง", "보통맛"));
        rbFort.setText(t("Fort", "Hot", "大辣", "辛口", "Picante", "Острый", "เผ็ดมาก", "매운맛"));
        rbRiz.setText(t("Riz", "Rice", "米飯", "ご飯", "Arroz", "Рис", "ข้าว", "밥"));
        rbNouilles.setText(t("Nouilles", "Noodles", "麵條", "麺", "Fideos", "Лапша", "เส้นหมี่", "면"));
        panierlogo.setText(t("🛒 Panier", "🛒 Cart", "🛒 购物车", "🛒 買い物カゴ", "🛒 Carrito", "🛒 Корзина", "🛒 ตะกร้าสินค้า", "🛒 장바구니"));
    }

    private String t(String fr, String en, String zh, String ja, String es, String ru, String th, String ko) {
        switch (langueActuelle) {
            case "EN": return en;
            case "中文": return zh;
            case "日本語": return ja;
            case "ES": return es;
            case "PY": return ru;
            case "ไทย": return th;
            case "한국말": return ko;
            default: return fr;
        }
    }

    private String[] getTraductionProduit(String id) {
        return switch (id != null ? id : "") {
            case "B1" -> new String[]{t("Ice Tea", "Iced Tea", "冰茶", "アイスティー", "Té Helado", "Холодный чай", "ชาดำเย็น", "아이스티"), t("Maison, citron vert", "Homemade, lime", "自制青柠味", "自家製、ライム入り", "Casero, lima", "Домашний, с лаймом", "โฮมเมด มะนาว", "수제, 라임")};
            case "B2" -> new String[]{t("Bière Tsingtao", "Tsingtao Beer", "青岛啤酒", "青島ビール", "Cerveza Tsingtao", "Пиво Циндао", "เบียร์ชิงเต่า", "칭따오 맥주"), t("Bière blonde 33cl", "Lager beer 33cl", "33毫升", "ラガービール 33cl", "Cerveza rubia 33cl", "Светлое пиво 33cl", "เบียร์ลาเกอร์ 33cl", "라거 맥주 33cl")};
            case "B3" -> new String[]{t("Limonade Jap", "Jap Lemonade", "弹珠汽水", "ラムネ", "Limonada Jap", "Японский лимонад", "ราเมเนะ", "라무네"), t("Ramune à bille", "Ramune with marble", "日式传统汽水", "ビー玉入りラムネ", "Ramune con canica", "Рамунэ с шариком", "น้ำโซดาญี่ปุ่น", "구슬 사이다")};
            case "B4" -> new String[]{t("Jus de Coco", "Coconut Juice", "椰子汁", "ココナッツジュース", "Jugo de Coco", "Кокосовый сок", "น้ำมะพร้าว", "코코넛 주스"), t("Avec morceaux", "With chunks", "果肉果汁", "果肉入り", "Con trozos", "С кусочками", "มีเนื้อมะพร้าว", "과육 포함")};
            case "B5" -> new String[]{t("Sake", "Sake", "清酒", "日本酒", "Sake", "Саке", "สาเก", "사케"), t("Petit pichet", "Small pitcher", "小瓶装", "徳利（小）", "Jarra pequeña", "Маленький кувшин", "กาเล็ก", "도구리 (소)")};
            case "D1" -> new String[]{t("Perles de Coco", "Coconut Pearls", "椰丝球", "ココナッツ団子", "Perlas de Coco", "Кокосовые шарики", "ขนมต้มมะพร้าว", "코코넛 경단"), t("2 pièces, tiède", "2 pieces, warm", "2个, 温热", "2個、温かい", "2 piezas, tibio", "2 штуки, теплые", "2 ชิ้น อุ่นๆ", "2개, 따뜻함")};
            case "D2" -> new String[]{t("Mochi Glacé", "Iced Mochi", "冰淇淋大福", "雪見だいふく", "Mochi Helado", "Моти-мороженое", "โมจิไอศกรีม", "모찌 아이스크림"), t("2 pièces, Vanille et Matcha", "2 pieces, Vanilla/Matcha", "2个, 香草和抹茶", "2個、バニラと抹茶", "2 piezas, Vainilla/Matcha", "2 штуки, ваниль/матча", "2 ชิ้น วานิลลา/มัทฉะ", "2개, 바닐라/말차")};
            case "D3" -> new String[]{t("Mangue Fraîche", "Fresh Mango", "鲜芒果", "フレッシュマンゴー", "Mango Fresco", "Свежий манго", "มะมะม่วงสด", "생망고"), t("Tranches de mangue", "Mango slices", "新鲜切片", "マンゴースライス", "Rodajas de mango", "Ломтики манго", "มะม่วงหั่นชิ้น", "망고 슬라이스")};
            case "D4" -> new String[]{t("Banane Flambée", "Flambé Banana", "拔丝香蕉", "バナナのフランベ", "Plátano Flambeado", "Фламбированный банан", "กล้วยทอดฟลมเบ้", "바나나 플람베"), t("Au saké", "With sake", "清酒烹制", "日本酒風味", "Con sake", "С саке", "ผสมสาเก", "사케 풍미")};
            case "D5" -> new String[]{t("Nougat Chinois", "Chinese Nougat", "芝麻糖", "中華風の中華菓子", "Turrón Chino", "Китайская нуга", "ตุ๊บตั๊บจีน", "중국식 누가"), t("Aux graines de sésame", "With sesame seeds", "芝麻味", "ゴマ入り", "Con semillas de sésamo", "С кунжутом", "ผสมงา", "참깨 포함")};
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
            case "P1" -> new String[]{t("Pad Thaï", "Pad Thai", "泰式炒河粉", "パッタイ", "Pad Thai", "Пад-тай", "ผัดไทย", "팥타이"), t("Nouilles de riz, crevettes", "Rice noodles, shrimp", "大米粉, 鲜虾", "米粉の麺、海老", "Fideos de arroz, camarones", "Рисовая лапша, креветки", "เส้นเล็กผัดกุ้ง", "쌀국수, 새우")};
            case "P2" -> new String[]{t("Bo Bun Boeuf", "Beef Bo Bun", "牛肉米粉", "牛焼肉 de Bun", "Bo Bun de Ternera", "Бо Бун с говядиной", "โบブンเนื้อ", "소고기 보분"), t("Vermicelles, boeuf sauté", "Vermicelli, sautéed beef", "干拌粉, 炒牛肉", "米麺、牛肉炒め", "Fideos, ternera salteada", "Вермишель, жареная говядина", "เส้นหมี่ เนื้อผัด", "버미셀리, 소고기 볶음")};
            case "P3" -> new String[]{t("Curry Vert", "Green Curry", "绿咖喱", "グリーンカレー", "Curry Verde", "Зеленый карри", "แกงเขียวหวาน", "그린 커리"), t("Poulet, lait de coco", "Chicken, coconut milk", "鸡肉, 椰奶", "鶏肉、ココナッツミルク", "Pollo, leche de coco", "Курица, кокосовое молоко", "ไก่ กะทิ", "치킨, 코코넛 밀크")};
            case "P4" -> new String[]{t("Riz Cantonais", "Cantonese Rice", "扬州炒饭", "チャーハン", "Arroz Cantonés", "Рис по-кантонски", "ข้าวผัดหยางโจว", "볶음밥"), t("Riz sauté, jambon", "Fried rice, ham", "火腿蛋炒饭", "ハム入り炒飯", "Arroz salteado, jamón", "Жареный рис, ветчина", "ข้าวผัดใส่แฮม", "햄 볶음밥")};
            case "P5" -> new String[]{t("Porc au Caramel", "Caramel Pork", "红烧肉", "豚肉のキャラメル煮", "Cerdo al Caramelo", "Свинина в карамели", "หมูหวาน", "돼지갈비찜"), t("Travers de porc confits", "Candied ribs", "焦糖猪排", "豚バラ肉の甘辛煮", "Costillas de cerdo confitadas", "Засахаренные ребрышки", "ซี่โครงหมูตุ๋นหวาน", "졸인 돼지갈비")};
            case "P6" -> new String[]{t("Canard Laqué", "Peking Duck", "北京烤鸭", "北京ダック", "Pato Laqueado", "Утка по-пекински", "เป็ดปักกิ่ง", "베이징 덕"), t("Avec crêpes", "With pancakes", "附荷叶饼", "薄餅添え", "Con crepas", "С блинчиками", "เสิร์ฟพร้อมแป้งห่อ", "전병 포함")};
            case "P7" -> new String[]{t("Bibimbap", "Bibimbap", "石锅拌饭", "ビビンバ", "Bibimbap", "Пибимпап", "บิบิมบับ", "비빔밥"), t("Riz, boeuf, légumes", "Rice, beef, vegetables", "米饭, 牛肉, 蔬菜", "ご飯、牛肉、野菜", "Arroz, ternera, verduras", "Рис, говядина, овощи", "ข้าว เนื้อ ผัก", "밥, 소고기, 야채")};
            case "P8" -> new String[]{t("Tonkotsu Ramen", "Tonkotsu Ramen", "豚骨拉面", "豚骨ラーメン", "Tonkotsu Ramen", "Тонкоцу рамэн", "ทงคัตสึราเมน", "돈코츠 라멘"), t("Bouillon porc, nouilles", "Pork broth, noodles", "浓汤面", "豚骨スープ、麺", "Caldo de cerdo, fideos", "Свиной бульон, лапша", "ซุปกระดูกหมู บะหมี่", "돼지 사골 육수, 면")};
            case "P9" -> new String[]{t("Mix Sushi 12", "Sushi Mix 12", "寿司拼盘", "寿司盛り合わせ 12貫", "Mix de Sushi 12", "Ассорти суши 12 шт", "ซูชิรวม 12 ชิ้น", "모듬 초밥 12피스"), t("Assortiment de sushi", "Sushi assortment", "12个寿司", "寿司のアソート", "Surtido de sushi", "Набор суши", "ซูชิหลากหลายหน้า", "다양한 초밥")};
            case "P10" -> new String[]{t("Wok Végé", "Vege Wok", "素食炒面", "野菜の炒め物", "Wok Vegetariano", "Вок вегетарианский", "ว็อกมังสวิรัติ", "야채 워크"), t("Nouilles, tofu", "Noodles, tofu", "面条, 豆腐", "麺、豆腐", "Fideos, tofu", "Лапша, тофу", "บะหมี่ เต้าหู้", "면, 두부")};
            default -> new String[]{"?", "?"};
        };
    }

    @FXML void augmenterQuantite() { if (quantite < 99) { quantite++; lblQuantite.setText(String.valueOf(quantite)); } }
    @FXML void diminuerQuantite() { if (quantite > 1) { quantite--; lblQuantite.setText(String.valueOf(quantite)); } }
    @FXML void retourCatalogue() { try { NetwokApp.setRoot("views/catalogue"); } catch (IOException e) { e.printStackTrace(); } }
    @FXML void voirPanier() { try { NetwokApp.setRoot("views/panier"); } catch (IOException e) { e.printStackTrace(); } }

    private void updatePanierDisplay() {
        int nb = MockService.getInstance().getNombreArticlesPanier();
        double total = MockService.getInstance().getTotalPanier();
        String label = (nb <= 1) ? t("article", "item", "件商品", "点", "artículo", "товар", "รายการ", "개") : t("articles", "items", "件商品", "点", "artículos", "товаров", "รายการ", "개");
        btnVoirPanier.setText(String.format("%d %s (%.2f€)", nb, label, total));
    }
}