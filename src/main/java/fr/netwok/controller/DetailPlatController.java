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
import javafx.scene.layout.VBox; // Import ajouté
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.layout.HBox;

public class DetailPlatController implements Initializable {

    @FXML private ImageView imgPlat;
    @FXML private Label lblNom;
    @FXML private Label lblPrix;
    @FXML private Label detail;
    @FXML private Text txtDescription;
    @FXML private Text txtInfos;

    @FXML private Label epices;
    @FXML private Label acc;
    @FXML private Label qt;

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
    @FXML private Label languageDisplay;

    // Nouveaux conteneurs pour masquer les options
    // Dans DetailPlatController.java
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
            platAfficher = null;
        }
        updatePanierDisplay();
        traduireInterface();
    }

    private void chargerDetailPlat() {
        if (platActuel == null) return;

        // --- 1. TEXTES & VISIBILITÉ ---
        lblNom.setText(platActuel.getNom());
        lblPrix.setText(String.format("%.2f€", platActuel.getPrix()));

        // Description (avec sécurité anti-null)
        if (platActuel.getDescription() != null && !platActuel.getDescription().isEmpty()) {
            txtDescription.setText(platActuel.getDescription());
        } else {
            txtDescription.setText("Aucune description disponible.");
        }
        txtDescription.setStyle("-fx-fill: white;"); // Force la couleur blanche

        // Informations
        txtInfos.setText(genererInformations());
        txtInfos.setStyle("-fx-fill: white;"); // Force la couleur blanche

        // --- 2. IMAGE (Gestion du chemin) ---
        try {
            String rawPath = platActuel.getImagePath();

            if (rawPath != null && !rawPath.isEmpty()) {
                if (rawPath.startsWith("/")) rawPath = rawPath.substring(1);

                String fullPath;
                if (rawPath.startsWith("images")) {
                    fullPath = "/fr/netwok/" + rawPath;
                } else {
                    fullPath = "/fr/netwok/images/" + rawPath;
                }

                URL imageUrl = getClass().getResource(fullPath);
                if (imageUrl != null) {
                    imgPlat.setImage(new Image(imageUrl.toExternalForm()));
                } else {
                    // Image par défaut si introuvable
                    URL defaultImg = getClass().getResource("/fr/netwok/images/logo_main.png");
                    if (defaultImg != null) imgPlat.setImage(new Image(defaultImg.toExternalForm()));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur image : " + e.getMessage());
        }

        // --- 3. MASQUAGE DES OPTIONS (Desserts & Boissons) ---
        // ID 3 = Dessert, ID 4 = Boisson
        int categoryId = platActuel.getCategorie();
        boolean isOptionHidden = (categoryId == 3 || categoryId == 4);

        // Masquage du titre "OPTIONS"
        if (opt != null) {
            opt.setVisible(!isOptionHidden);
            opt.setManaged(!isOptionHidden);
        }

        // Masquage des blocs d'options
        if (vboxOptionsPiment != null) {
            vboxOptionsPiment.setVisible(!isOptionHidden);
            vboxOptionsPiment.setManaged(!isOptionHidden);
        }
        if (vboxOptionsAccompagnement != null) {
            vboxOptionsAccompagnement.setVisible(!isOptionHidden);
            vboxOptionsAccompagnement.setManaged(!isOptionHidden);
        }

        // Réinitialisation des choix techniques
        if (isOptionHidden) {
            platActuel.setPimentChoisi(0);
            platActuel.setAccompagnementChoisi(0);
        }
    }

    @FXML
    void ajouterAuPanier() {
        // Enregistrement des choix de l'utilisateur avant ajout
        if (rbMoyen.isSelected()) platActuel.setPimentChoisi(1);
        else if (rbFort.isSelected()) platActuel.setPimentChoisi(2);
        else platActuel.setPimentChoisi(0);

        if (rbNouilles.isSelected()) platActuel.setAccompagnementChoisi(1);
        else platActuel.setAccompagnementChoisi(0);

        // Ajout multiple selon la quantité
        for (int i = 0; i < quantite; i++) {
            MockService.getInstance().ajouterAuPanier(platActuel);
        }

        btnAjouter.setText(t("✓ Ajouté", "✓ Added", "✓ 已添加", "✓ 追加済み", "✓ ¡Añadido!", "✓ Добавлено", "✓ เพิ่มแล้ว", "✓ 추가됨"));
        btnAjouter.setDisable(true);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(this::retourCatalogue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String genererInformations() {
        return t("Allergènes : traces de gluten, soja\n", "Allergens: traces of gluten, soy\n", "過敏原：可能含有麩質、大豆\n", "アレルゲン：小麦、大豆\n", "Alérgenos: trazas de gluten, soja\n", "Аллергены: следы глютена, сои\n", "ข้อมูลสำหรับผู้แพ้อาหาร: อาจมีกลูเตนและถั่วเหลือง\n", "알레르기: 글루텐, 대두 포함 가능\n") +
                t("Calories : env. 450 kcal\n", "Calories: approx. 450 kcal\n", "熱量：約 450 大卡\n", "エネルギー：約 450 kcal\n", "Calorías: aprox. 450 kcal\n", "Калории: около 450 ккал\n", "แคลอรี่: ประมาณ 450 kcal\n", "칼로리: 약 450 kcal\n") +
                t("Temps : 15-20 min", "Time: 15-20 min", "準備時間：15-20 分鐘", "調理時間：15-20 分", "Tiempo: 15-20 min", "Время: 15-20 мин", "เวลา: 15-20 นาที", "시간: 15-20분");
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
        String label = (nb <= 1) ? t("article", "item", "件商品", "点", "artículo", "товар", "รายการ", "개") : t("articles", "items", "件商品", "点", "artículos", "товаров", "รายการ", "개");
        btnVoirPanier.setText(String.format("%d %s (%.2f€)", nb, label, total));
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
    }

    private String t(String fr, String en, String zh, String ja, String es, String ru, String th, String ko) {
        switch (langueActuelle) {
            case "EN": return en;
            case "ZH": return zh;
            case "JA": return ja;
            case "ES": return es;
            case "RU": return ru;
            case "TH": return th;
            case "KO": return ko;
            default: return fr;
        }
    }
}