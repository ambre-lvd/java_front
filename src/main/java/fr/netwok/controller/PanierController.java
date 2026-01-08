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

    @FXML private VBox vboxArticles, vboxSuggestions;
    @FXML private Label lblSousTotal, lblTaxes, lblTotalFinal, lblTotalFinaltxt;
    @FXML private Label lblTitreRecap, colArticle, colQuantite, colPrix, colTotal;
    @FXML private Label lblTxtSousTotal, lblTxtTaxes, lblTxtTable, lblTxtNom, languageDisplay;
    @FXML private TextField txtNumeroTable, txtNomClient;
    @FXML private Button btnConfirmer, btnRetour, btnModifier;

    private String currentLanguage = "FR";
    private Plat suggestionFixe = null;
    private static final double TAUX_TAXE = 0.15;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentLanguage = CatalogueController.getLangueActuelle();
        traduire();
        chargerPanier();
    }

    private String t(String fr, String en, String zh, String jp, String es, String ru, String th, String co) {
        return switch (currentLanguage) {
            case "EN" -> en;
            case "ZH", "中文" -> zh;
            case "日本語" -> jp;
            case "ES" -> es;
            case "PY" -> ru;
            case "ไทย" -> th;
            case "한국말" -> co;
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
        lblTitreRecap.setText(t("Récapitulatif de commande", "Order Summary", "订单摘要", "ご注文内容の確認", "Resumen del pedido", "Детали заказа", "สรุปรายการสั่งซื้อ", "주문 내역"));
        colArticle.setText(t("Article", "Item", "商品", "商品名", "Artículo", "Товар", "รายการ", "품목"));
        colQuantite.setText(t("Quantité", "Quantity", "数量", "数量", "Cantidad", "Кол-во", "จำนวน", "수량"));
        colPrix.setText(t("Prix Unit.", "Unit Price", "单价", "単価", "Precio Unit.", "Цена за ед.", "ราคาต่อหน่วย", "단가"));
        colTotal.setText(t("Total", "Total", "总计", "合計", "Total", "Итого", "รวม", "합계"));
        lblTxtSousTotal.setText(t("Sous-total :", "Subtotal:", "小计 :", "小計 :", "Subtotal:", "Промежуточный итог:", "ราคารวมก่อนภาษี:", "소계:"));
        lblTxtTaxes.setText(t("Taxes (15%) :", "Taxes (15%):", "税费 (15%) :", "税金 (15%) :", "Impuestos (15%):", "Налоги (15%):", "ภาษี (15%):", "세금 (15%):"));
        lblTotalFinaltxt.setText(t("TOTAL :", "TOTAL :", "總計 :", "合計 :", "TOTAL:", "ИТОГО:", "ยอดรวมสุทธิ:", "총합계:"));
        lblTxtTable.setText(t("Numéro de table :", "Table Number:", "桌号 :", "テーブル番号 :", "Número de mesa:", "Номер стола:", "หมายเลขโต๊ะ:", "테이블 번호:"));
        lblTxtNom.setText(t("Nom du client (optionnel) :", "Client Name (optional):", "客户姓名 (可选) :", "お名前 (任意) :", "Nombre del cliente (opcional):", "Имя клиента (опционально):", "ชื่อลูกค้า (ถ้ามี):", "고객명 (선택 사항):"));
        txtNumeroTable.setPromptText(t("Ex: 12", "e.g. 12", "例如: 12", "例: 12", "Ej: 12", "Напр: 12", "เช่น: 12", "예: 12"));
        txtNomClient.setPromptText(t("Ex: Martin Dupont", "e.g. John Doe", "例如: 张三", "例: 山田太郎", "Ej: Juan Pérez", "Напр: Иван Иванов", "เช่น: สมชาย ดีใจ", "예: 홍길동"));
        btnRetour.setText(t("← Retour", "← Back", "← 返回", "← 戻る", "← Volver", "← Назад", "← กลับ", "← 뒤로가기"));
        btnModifier.setText(t("← Modifier", "← Edit", "← 修改", "← 修正", "← Modificar", "← Изменить", "← แก้ไข", "← 수정하기"));
        btnConfirmer.setText(t("Confirmer la commande", "Confirm Order", "确认订单", "注文を確定する", "Confirmar pedido", "Подтвердить заказ", "ยืนยันการสั่งซื้อ", "주문 확정하기"));
        if (languageDisplay != null) {
            languageDisplay.setText(t("Langue : FR", "Language : EN", "语言 : 中文", "言語 : 日本語", "Idioma: ES", "Язык: PY", "ภาษา: ไทย", "언어: 한국말"));
        }
    }

    private String[] getTraductionProduit(String id) {
        return switch (id) {
            case "B1" -> new String[]{t("Ice Tea", "Iced Tea", "冰茶", "アイスティー", "Té Helado", "Холодный чай", "ชาดำเย็น", "아이스티"), t("Maison, citron vert", "Homemade, lime", "自制青柠味", "自家製、ライム入り", "Casero, lima", "Домашний, с лаймом", "โฮมเมด มะนาว", "수제, 라임")};
            case "B2" -> new String[]{t("Bière Tsingtao", "Tsingtao Beer", "青岛啤酒", "青島ビール", "Cerveza Tsingtao", "Пиво Циндао", "เบียร์ชิงเต่า", "칭따오 맥주"), t("Bière blonde 33cl", "Lager beer 33cl", "33毫升", "ラガービール 33cl", "Cerveza rubia 33cl", "Светлое пиво 33cl", "เบียร์ลาเกอร์ 33cl", "라거 맥주 33cl")};
            case "B3" -> new String[]{t("Limonade Jap", "Jap Lemonade", "弹珠汽水", "ラムネ", "Limonada Jap", "Японский лимонад", "ราเมเนะ", "라무네"), t("Ramune à bille", "Ramune with marble", "日式传统汽水", "ビー玉入りラムネ", "Ramune con canica", "Рамунэ с шариком", "น้ำโซดาญี่ปุ่น", "구슬 사이다")};
            case "B4" -> new String[]{t("Jus de Coco", "Coconut Juice", "椰子汁", "ココナッツジュース", "Jugo de Coco", "Кокосовый сок", "น้ำมะพร้าว", "코코넛 주스"), t("Avec morceaux", "With chunks", "果肉果汁", "果肉入り", "Con trozos", "С кусочками", "มีเนื้อมะพร้าว", "과육 포함")};
            case "B5" -> new String[]{t("Sake", "Sake", "清酒", "日本酒", "Sake", "Са케", "สาเก", "사케"), t("Petit pichet", "Small pitcher", "小瓶装", "徳利（小）", "Jarra pequeña", "Маленький кувшин", "กาเล็ก", "도구리 (소)")};
            case "D1" -> new String[]{t("Perles de Coco", "Coconut Pearls", "椰丝球", "ココナッツ団子", "Perlas de Coco", "Кокосовые шарики", "ขนมต้มมะพร้าว", "코코넛 경단"), t("2 pièces, tiède", "2 pieces, warm", "2个, 温热", "2個、温かい", "2 piezas, tibio", "2 штуки, теплые", "2 ชิ้น อุ่นๆ", "2개, 따뜻함")};
            case "D2" -> new String[]{t("Mochi Glacé", "Iced Mochi", "冰淇淋大福", "雪見だいふく", "Mochi Helado", "Моти-мороженое", "โมจิไอศกรีม", "모찌 아이스크림"), t("2 pièces, Vanille et Matcha", "2 pieces, Vanilla/Matcha", "2个, 香草和抹茶", "2個、バニラと抹茶", "2 piezas, Vainilla/Matcha", "2 штуки, ваниль/матча", "2 ชิ้น วานิลลา/มัทฉะ", "2개, 바닐라/말차")};
            case "D3" -> new String[]{t("Mangue Fraîche", "Fresh Mango", "鲜芒果", "フレッシュマンゴー", "Mango Fresco", "Свежий манго", "มะม่วงสด", "생망고"), t("Tranches de mangue", "Mango slices", "新鲜切片", "マンゴースライス", "Rodajas de mango", "Ломтики манго", "มะม่วงหั่นชิ้น", "망고 슬라이스")};
            case "D4" -> new String[]{t("Banane Flambée", "Flambé Banana", "拔丝香蕉", "バナナのフランベ", "Plátano Flambeado", "Фламбированный банан", "กล้วยทอดฟลมเบ้", "바나나 플람베"), t("Au saké", "With sake", "清酒烹制", "日本酒風味", "Con sake", "С саке", "ผสมสาเก", "사케 풍미")};
            case "D5" -> new String[]{t("Nougat Chinois", "Chinese Nougat", "芝麻糖", "中華風の中華菓子", "Turrón Chino", "Китайская нуга", "ตุ๊บตั๊บจีน", "중국식 누가"), t("Aux graines de sésame", "With sesame seeds", "芝麻味", "ゴマ入り", "Con semillas de sésamo", "С куншутом", "ผสมงา", "참깨 포함")};
            case "E1" -> new String[]{t("Nems Poulet", "Chicken Nems", "鸡肉春卷", "鶏肉の揚げ春巻き", "Rollitos de Pollo", "Немы с курицей", "ปอเปี๊ยะทอดไก่", "치킨 넴"), t("4 pièces, sauce nuoc-mâm", "4 pieces, fish sauce", "4个, 鱼露", "4個、ヌクマムソース", "4 piezas, salsa de pescado", "4 штуки, рыбный соус", "4 ชิ้น พร้อมน้ำจิ้มปลา", "4개, 피ッシュ 소스")};
            case "E2" -> new String[]{t("Rouleaux Printemps", "Spring Rolls", "夏卷", "生春巻き", "Rollitos de Primavera", "Спринг-роллы", "ปอเปี๊ยะสด", "월남쌈"), t("Crevette, menthe, riz", "Shrimp, mint, rice", "鲜虾, 薄荷", "海老、ミント、米粉", "Camarón, menta, arroz", "Креветки, мята, рис", "กุ้ง มิ้นต์ เส้นหมี่", "새우, 민트, 쌀면")};
            case "E3" -> new String[]{t("Gyozas Poulet", "Chicken Gyozas", "鸡肉饺子", "鶏肉餃子", "Gyozas de Pollo", "Гёдза с курицей", "เกี๊ยวซ่าไก่", "치킨 교자"), t("Raviolis grillés (5 pièces)", "Grilled dumplings (5 pcs)", "煎饺 (5个)", "焼き餃子（5個）", "Dumplings a la plancha (5 pzas)", "Жареные пельмени (5 шт)", "เกี๊ยวซ่าย่าง (5 ชิ้น)", "군만두 (5개)")};
            case "E4" -> new String[]{t("Samoussas Boeuf", "Beef Samoussas", "牛肉咖喱角", "牛肉のサモサ", "Samosas de Ternera", "Самоса с говядиной", "ซามูซ่าเนื้อ", "소고기 사모사"), t("Croustillant aux épices", "Crispy with spices", "香脆辣味", "スパイス香るカリカリ揚げ", "Crujiente con especias", "Хрустящие со специями", "แป้งกรอบสอดไส้เครื่องเทศ", "매콤하고 바삭함")};
            case "E5" -> new String[]{t("Salade de Chou", "Cabbage Salad", "凉拌卷心菜", "キャベツのサラダ", "Ensalada de Col", "Салат из капусты", "สลัดกะหล่ำปลี", "양배추 샐러드"), t("Chou blanc, marinade sésame", "White cabbage, sesame", "白菜, 芝麻汁", "白キャベツ de胡麻マリネ", "Col blanca, sésamo", "Белокочанная капуста, кунжут", "กะหล่ำปลีหมักงา", "코울슬로, 참깨 드레싱")};
            case "E6" -> new String[]{t("Soupe Miso", "Miso Soup", "味噌汤", "味噌汁", "Sopa Miso", "Мисо суп", "ซุปมิโซะ", "미소된장국"), t("Tofu, algues wakame", "Tofu, wakame seaweed", "豆腐, 海带", "豆腐、わかめ", "Tofu, algas wakame", "Тофу, водоросли вакаме", "เต้าหู้ สาหร่ายวากาเมะ", "두부, 미역")};
            case "E7" -> new String[]{t("Tempura Crevettes", "Shrimp Tempura", "天妇罗虾", "海老の天ぷら", "Tempura de Camarones", "Темпура с креветками", "เทมปุระกุ้ง", "새우 튀김"), t("Beignets légers (4 pièces)", "Light fritters (4 pcs)", "脆炸 (4个)", "衣揚げ（4個）", "Fritura ligera (4 piezas)", "Легкий кляр (4 штуки)", "กุ้งชุบแป้งทอด (4 ชิ้น)", "가벼운 튀김 (4개)")};
            case "E8" -> new String[]{t("Yakitori Boeuf", "Beef Yakitori", "牛肉串", "牛串焼き", "Yakitori de Ternera", "Якитори из говядины", "ยากิโทริเนื้อ", "소고기 야키토리"), t("Brochettes boeuf-fromage", "Beef-cheese skewers", "芝士牛肉串", "牛チーズ串", "Brochetas ternera-queso", "Шашлычки говядина-сыр", "เนื้อพันชี스เสียบไม้", "소고기 치즈 꼬치")};
            case "E9" -> new String[]{t("Edamame", "Edamame", "毛豆", "枝豆", "Edamame", "Эдамаме", "ถั่วแระญี่ปุ่น", "에다마메"), t("Fèves de soja, sel de mer", "Soybeans, sea salt", "盐水大豆", "塩ゆで枝豆", "Soja, sal de mar", "Соевые бобы, морская соль", "ถั่วเหลืองฝักอ่อนโรยเกลือ", "자숙면, 바다 소금")};
            case "E10" -> new String[]{t("Mix Dim Sum", "Dim Sum Mix", "点心拼盘", "点心セット", "Mix de Dim Sum", "Димсам микс", "ติ่มซำรวมมิตร", "딤섬 모듬"), t("Panier vapeur (6 pièces)", "Steamed basket (6 pcs)", "蒸笼 (6个)", "蒸し器（6個）", "Cesta al vapor (6 piezas)", "Паровая корзина (6 шт)", "เข่งนึ่ง (6 ชิ้น)", "찜통 (6개)")};
            case "P1" -> new String[]{t("Pad Thaï", "Pad Thai", "泰式炒河粉", "パッタイ", "Pad Thai", "Пад-тай", "ผัดไทย", "팥타이"), t("Nouilles de riz, crevettes", "Rice noodles, shrimp", "大米粉, 鲜虾", "米粉の麺、海老", "Fideos de arroz, camarones", "Рисовая лапша, креветки", "เส้นเล็กผัดกุ้ง", "쌀국수, 새우")};
            case "P2" -> new String[]{t("Bo Bun Boeuf", "Beef Bo Bun", "牛肉米粉", "牛焼肉のブン", "Bo Bun de Ternera", "Бо Бун с говядиной", "โบブンเนื้อ", "소고기 보분"), t("Vermicelles, boeuf sauté", "Vermicelli, sautéed beef", "干拌粉, 炒牛肉", "米麺、牛肉炒め", "Fideos, ternera salteada", "Вермишель, жареная говядина", "เส้นหมี่ เนื้อผัด", "버미셀리, 소고기 볶음")};
            case "P3" -> new String[]{t("Curry Vert", "Green Curry", "绿咖喱", "グリーンカレー", "Curry Verde", "Зеленый карри", "แกงเขียวหวาน", "그린 커리"), t("Poulet, lait de coco", "Chicken, coconut milk", "鸡肉, 椰奶", "鶏肉、ココナッツミルク", "Pollo, leche de coco", "Курица, кокосовое молоко", "ไก่ กะทิ", "치킨, 코코넛 밀크")};
            case "P4" -> new String[]{t("Riz Cantonais", "Cantonese Rice", "扬州炒饭", "チャーハン", "Arroz Cantonés", "Рис по-кантонски", "ข้าวผัดหยางโจว", "볶음밥"), t("Riz sauté, jambon", "Fried rice, ham", "火腿蛋炒饭", "ハム入り炒飯", "Arroz salteado, jamón", "Жареный рис, ветчина", "ข้าวผัดใส่แฮม", "햄 볶음밥")};
            case "P5" -> new String[]{t("Porc au Caramel", "Caramel Pork", "红烧肉", "豚肉のキャラメル煮", "Cerdo al Caramelo", "Свинина в карамели", "หมูหวาน", "돼지갈비찜"), t("Travers de porc confits", "Candied ribs", "焦糖猪排", "豚バラ肉の甘辛煮", "Costillas de cerdo confitadas", "Засахаренные ребрышки", "ซี่โครงหมูตุ๋นหวาน", "졸인 돼지갈비")};
            case "P6" -> new String[]{t("Canard Laqué", "Peking Duck", "北京烤鸭", "北京ダック", "Pato Laqueado", "Утка по-пекински", "เป็ดปักกิ่ง", "베이징 덕"), t("Avec crêpes", "With pancakes", "附荷叶饼", "薄餅添え", "Con crepas", "С блинчиками", "เสิร์ฟพร้อมแป้งห่อ", "전병 포함")};
            case "P7" -> new String[]{t("Bibimbap", "Bibimbap", "石锅拌饭", "ビビンバ", "Bibimbap", "Пибимпап", "บิบิมบับ", "비빔밥"), t("Riz, boeuf, légumes", "Rice, beef, vegetables", "米饭, 牛肉, 蔬菜", "ご飯、牛肉、野菜", "Arroz, ternera, verduras", "Рис, говядина, овощи", "ข้าว เนื้อ ผัก", "밥, 소고기, 야채")};
            case "P8" -> new String[]{t("Tonkotsu Ramen", "Tonkotsu Ramen", "豚骨拉面", "豚骨ラーメン", "Tonkotsu Ramen", "Тонкоцу рамэн", "ทงคัตสึราเมน", "돈코츠 라멘"), t("Bouillon porc, nouilles", "Pork broth, noodles", "浓汤面", "豚骨スープ、麺", "Caldo de cerdo, fideos", "Свиной бульон, laпша", "ซุปกระดูกหมู บะหมี่", "돼지 사골 육수, 면")};
            case "P9" -> new String[]{t("Mix Sushi 12", "Sushi Mix 12", "寿司拼盘", "寿司盛り合わせ 12貫", "Mix de Sushi 12", "Ассорти суши 12 шт", "ซูชิรวม 12 ชิ้น", "모듬 초밥 12피스"), t("Assortiment de sushi", "Sushi assortment", "12个寿司", "寿司のアソート", "Surtido de sushi", "Набор суши", "ซูชิหลากหลายหน้า", "다양한 초밥")};
            case "P10" -> new String[]{t("Wok Végé", "Vege Wok", "素食炒面", "野菜の炒め物", "Wok Vegetariano", "Вок вегетарианский", "ว็อกมังสวิรัติ", "야채 워크"), t("Nouilles, tofu", "Noodles, tofu", "面条, 豆腐", "麺、豆腐", "Fideos, tofu", "Лапша, тофу", "บะหมี่ เต้าหู้", "면, 두부")};
            default -> new String[]{"?", "?", "?", "?", "?", "?", "?", "?"};
        };
    }

    private void chargerPanier() {
        vboxArticles.getChildren().clear();
        vboxSuggestions.getChildren().clear();
        List<Plat> panier = MockService.getInstance().getPanier();

        Map<String, Integer> counts = new HashMap<>();
        Map<String, Plat> platMap = new HashMap<>();
        Set<String> categoriesPresentes = new HashSet<>();

        for(Plat p : panier) {
            counts.put(p.getId(), counts.getOrDefault(p.getId(), 0) + 1);
            platMap.put(p.getId(), p);
            categoriesPresentes.add(p.getId().substring(0, 1));
        }

        if (panier.isEmpty()) {
            suggestionFixe = null;
            Label vide = new Label(t("Votre panier est vide.", "Your cart is empty.", "您的购物车是空的。", "あなたのカートは空です。", "Tu carrito está vacío.", "Ваша корзина пуста.", "ตะกร้าของคุณว่างเปล่า", "장바구니가 비어 있습니다."));
            vide.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 24px; -fx-padding: 40 0 40 0;");
            vboxArticles.getChildren().add(vide);
            btnConfirmer.setDisable(true);
        } else {
            btnConfirmer.setDisable(false);
            for (String id : counts.keySet()) {
                vboxArticles.getChildren().add(creerLigneArticle(platMap.get(id), counts.get(id)));
            }

            if (suggestionFixe == null) {
                suggestionFixe = trouverSuggestion(categoriesPresentes);
            }

            else if (categoriesPresentes.contains(suggestionFixe.getId().substring(0, 1))) {
                suggestionFixe = trouverSuggestion(categoriesPresentes);
            }

            if (suggestionFixe != null) {
                vboxSuggestions.getChildren().add(creerLigneSuggestion(suggestionFixe));
            }
        }
        animerTotaux();
    }


    private Plat trouverSuggestion(Set<String> categoriesPresentes) {
        List<String> toutesCategories = Arrays.asList("E", "P", "D", "B");
        List<String> manquantes = new ArrayList<>();
        for(String c : toutesCategories) if(!categoriesPresentes.contains(c)) manquantes.add(c);

        if(manquantes.isEmpty()) return null;

        List<Plat> catalogue = MockService.getInstance().getPlats();
        if(catalogue == null) return null;

        List<Plat> copie = new ArrayList<>(catalogue);
        Collections.shuffle(copie);

        for(Plat p : copie) {
            if(manquantes.contains(p.getId().substring(0, 1))) return p;
        }
        return null;
    }

    private VBox creerLigneSuggestion(Plat p) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: rgba(0, 240, 255, 0.05); -fx-border-color: #00F0FF; -fx-border-radius: 12; -fx-padding: 15;");

        String[] trads = getTraductionProduit(p.getId());
        Label hint = new Label(t("Petit creux ? Essayez ceci :", "Hungry? Try this:", "还饿吗？试试这个：", "こちらもいかがですか？", "¿Tienes hambre? Prueba esto:", "Проголодались? Попробуйте это:", "หิวไหม? ลองนี่สิ:", "출출하신가요? 이걸 추천해요:"));
        hint.setStyle("-fx-text-fill: #00F0FF; -fx-font-weight: bold;");

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView img = new ImageView();
        img.setFitWidth(150);
        img.setFitHeight(150);
        img.setPreserveRatio(true);
        try {
            URL res = getClass().getResource("/fr/netwok/images/" + p.getImagePath());
            if (res != null) img.setImage(new Image(res.toExternalForm()));
            else System.err.println("Image not found for suggestion: " + p.getImagePath()); // Debug
        } catch (Exception e) {
            System.err.println("Error loading image for suggestion: " + p.getImagePath() + " - " + e.getMessage()); // Debug
        }
        content.getChildren().add(img);

        Label nom = new Label(trads[0] + " (" + String.format("%.2f€", p.getPrix()) + ")");
        nom.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        Button btn = new Button(t("+ Ajouter", "+ Add", "+ 添加", "+ 追加", "+ Añadir", "+ Добавить", "+ เพิ่ม", "+ 추가"));
        btn.setStyle("-fx-background-color: #00F0FF; -fx-text-fill: #0f172a; -fx-font-weight: bold;");
        btn.setOnAction(e -> { MockService.getInstance().ajouterAuPanier(p); chargerPanier(); });

        content.getChildren().addAll(nom, s, btn);
        box.getChildren().addAll(hint, content);
        return box;
    }

    private VBox creerLigneArticle(Plat p, int qte) {
        VBox ligne = new VBox(8);
        ligne.setStyle("-fx-background-color: rgba(30, 41, 59, 0.4); -fx-background-radius: 12; -fx-padding: 15;");
        HBox lp = new HBox(20); lp.setAlignment(Pos.CENTER_LEFT);
        String[] trads = getTraductionProduit(p.getId());
        ImageView img = new ImageView(); img.setFitWidth(150); img.setFitHeight(150);
        try {
            URL res = getClass().getResource("/fr/netwok/images/" + p.getImagePath());
            if (res != null) img.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}
        VBox colInfo = new VBox(5); colInfo.setPrefWidth(350);
        Label lblNom = new Label(trads[0]); lblNom.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblDesc = new Label(trads[1]); lblDesc.setStyle("-fx-text-fill: #94a3b8;");
        colInfo.getChildren().addAll(lblNom, lblDesc);
        HBox boxQty = new HBox(10); boxQty.setAlignment(Pos.CENTER); boxQty.setPrefWidth(130);
        Button btnM = new Button("-"); btnM.setOnAction(e -> { MockService.getInstance().retirerDuPanier(p); chargerPanier(); });
        Label lq = new Label(String.valueOf(qte)); lq.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        Button btnP = new Button("+"); btnP.setOnAction(e -> { MockService.getInstance().ajouterAuPanier(p); chargerPanier(); });
        boxQty.getChildren().addAll(btnM, lq, btnP);
        Label lprix = new Label(String.format("%.2f€", p.getPrix())); lprix.setStyle("-fx-text-fill: #94a3b8;"); lprix.setPrefWidth(100); lprix.setAlignment(Pos.CENTER_RIGHT);
        Label lTot = new Label(String.format("%.2f€", p.getPrix() * qte)); lTot.setStyle("-fx-text-fill: #00F0FF; -fx-font-weight: bold;"); lTot.setPrefWidth(120); lTot.setAlignment(Pos.CENTER_RIGHT);
        lp.getChildren().addAll(img, colInfo, boxQty, lprix, lTot);
        ligne.getChildren().add(lp);
        return ligne;
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
        if (txtNumeroTable.getText().isEmpty()) { txtNumeroTable.setStyle("-fx-border-color: #FF007F;"); return; }
        try {
            int table = Integer.parseInt(txtNumeroTable.getText());
            List<Plat> panier = MockService.getInstance().getPanier();
            if (panier.isEmpty()) return;
            ApiClient.sendOrder(table, panier);
            btnConfirmer.setText(t("✓ Envoyé", "✓ Sent", "✓ 已发送", "✓ 送信済み", "✓ ¡Enviado!", "✓ Отправлено!", "✓ ส่งแล้ว!", "✓ 전송됨!"));
            btnConfirmer.setDisable(true);
            MockService.getInstance().viderPanier();
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            RecuCommandeController.setCommandeInfo("CMD-" + System.currentTimeMillis() % 10000, table, txtNomClient.getText(), currentLanguage);
                            NetwokApp.setRoot("views/recuCommande");
                        } catch (Exception e) {}
                    });
                } catch (Exception e) {}
            }).start();
        } catch (Exception e) { txtNumeroTable.setStyle("-fx-border-color: red;"); }
    }

    @FXML void retourCatalogue() throws IOException { NetwokApp.setRoot("views/catalogue"); }
}