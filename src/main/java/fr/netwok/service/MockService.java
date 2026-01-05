package fr.netwok.service;

import fr.netwok.model.Plat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockService {

    private static MockService instance;
    private List<Plat> catalogue;
    private List<Plat> panier;

    private MockService() {
        panier = new ArrayList<>();
        catalogue = new ArrayList<>();
        
        // --- ENTRÉES (Noms EXACTS basés sur tes captures d'écran) ---
        // Attention aux majuscules et espaces, c'est ce qui bloquait !
        catalogue.add(new Plat("E1", "Nems Poulet", "4 pièces, sauce nuoc-mâm", 6.90, "Entrées", "Entrées/nems.png"));
        catalogue.add(new Plat("E2", "Rouleaux Printemps", "Crevettes, menthe, riz", 5.50, "Entrées", "Entrées/rouleaux printemps.png"));
        catalogue.add(new Plat("E3", "Gyozas Poulet", "Raviolis grillés (5 pièces)", 6.50, "Entrées", "Entrées/gyozas.png"));
        catalogue.add(new Plat("E4", "Samoussas Bœuf", "Croustillants aux épices", 6.00, "Entrées", "Entrées/samoussa.png"));
        catalogue.add(new Plat("E5", "Salade de Chou", "Chou blanc mariné sésame", 4.50, "Entrées", "Entrées/salade de chou.png"));
        catalogue.add(new Plat("E6", "Soupe Miso", "Tofu, algues wakame", 4.00, "Entrées", "Entrées/Soupe miso.png"));
        catalogue.add(new Plat("E7", "Tempura Crevettes", "Beignets légers (4 pièces)", 8.90, "Entrées", "Entrées/crevettes.png"));
        catalogue.add(new Plat("E8", "Yakitori Bœuf", "Brochettes bœuf-fromage", 5.90, "Entrées", "Entrées/yakitori boeuf.png"));
        catalogue.add(new Plat("E9", "Edamame", "Fèves de soja, fleur de sel", 4.50, "Entrées", "Entrées/Edamame.png"));
        catalogue.add(new Plat("E10", "Dim Sum Mix", "Panier vapeur (6 pièces)", 9.90, "Entrées", "Entrées/dim sum mix.png"));

        // --- PLATS (Je passe tout en .png par sécurité) ---
        // Assure-toi que tes fichiers dans le dossier "Plats" sont bien des .png aussi
        catalogue.add(new Plat("P1", "Pad Thaï", "Nouilles riz, crevettes", 14.90, "Plats", "Plats/pad thai.png"));
        catalogue.add(new Plat("P2", "Bo Bun Bœuf", "Vermicelles, bœuf sauté", 13.50, "Plats", "Plats/bobun.png"));
        catalogue.add(new Plat("P3", "Curry Vert", "Poulet, lait coco", 12.90, "Plats", "Plats/curry.png"));
        catalogue.add(new Plat("P4", "Riz Cantonais", "Riz sauté, jambon", 10.50, "Plats", "Plats/riz cantonais.png"));
        catalogue.add(new Plat("P5", "Porc Caramel", "Travers confits", 13.90, "Plats", "Plats/porc caramel.png"));
        catalogue.add(new Plat("P6", "Canard Laqué", "Avec crêpes", 16.90, "Plats", "Plats/canard.png"));
        catalogue.add(new Plat("P7", "Bibimbap", "Riz, bœuf, légumes", 14.50, "Plats", "Plats/bibimbap.png"));
        catalogue.add(new Plat("P8", "Ramen Tonkotsu", "Bouillon porc, nouilles", 13.90, "Plats", "Plats/ramen.png"));
        catalogue.add(new Plat("P9", "Sushi Mix 12", "Assortiment sushis", 15.90, "Plats", "Plats/sushi.png"));
        catalogue.add(new Plat("P10", "Wok Végé", "Nouilles, tofu", 11.90, "Plats", "Plats/wok vege.png"));

        // --- DESSERTS ET BOISSONS ---
        catalogue.add(new Plat("D1", "Perles de Coco", "2 pièces, chaud", 4.50, "Desserts", "Desserts et boissons/coco.png"));
        catalogue.add(new Plat("D2", "Mochi Glacé", "2 pièces, parfum au choix", 5.50, "Desserts", "Desserts et boissons/mochi.png"));
        catalogue.add(new Plat("D3", "Mangue Fraîche", "Tranches de mangue", 5.90, "Desserts", "Desserts et boissons/mangue.png"));
        catalogue.add(new Plat("D4", "Banane Flambée", "Au saké", 6.50, "Desserts", "Desserts et boissons/banane.png"));
        catalogue.add(new Plat("D5", "Nougat Chinois", "Aux graines de sésame", 3.50, "Desserts", "Desserts et boissons/nougat.png"));
        
        catalogue.add(new Plat("B1", "Thé Glacé", "Maison, citron vert", 3.50, "Boissons", "Desserts et boissons/the glace.png"));
        catalogue.add(new Plat("B2", "Bière Tsingtao", "Bière blonde 33cl", 4.50, "Boissons", "Desserts et boissons/tsingtao.png"));
        catalogue.add(new Plat("B3", "Limonade Jap", "Ramune à la bille", 4.00, "Boissons", "Desserts et boissons/ramune.png"));
        catalogue.add(new Plat("B4", "Jus de Coco", "Avec morceaux", 3.90, "Boissons", "Desserts et boissons/jus coco.png"));
        catalogue.add(new Plat("B5", "Saké", "Petit pichet", 6.00, "Boissons", "Desserts et boissons/sake.png"));
    }

    public static MockService getInstance() {
        if (instance == null) instance = new MockService();
        return instance;
    }

    public List<Plat> getPlats() { return catalogue; }

    public List<Plat> getPlatsParCategorie(String categorie) {
        return catalogue.stream()
                .filter(p -> p.getCategorie().equalsIgnoreCase(categorie))
                .collect(Collectors.toList());
    }

    public void ajouterAuPanier(Plat p) { panier.add(p); }
    public List<Plat> getPanier() { return panier; }
    public double getTotalPanier() { return panier.stream().mapToDouble(Plat::getPrix).sum(); }
    public int getNombreArticlesPanier() { return panier.size(); }
    public void viderPanier() { panier.clear(); }
}