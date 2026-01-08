package fr.netwok.service;

import fr.netwok.model.Plat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockService {

    private static MockService instance;
    private final List<Plat> catalogue;
    private final List<Plat> panier;

    // CONSTRUCTEUR : On initialise les listes vides
    private MockService() {
        panier = new ArrayList<>();
        catalogue = new ArrayList<>();
        // On ne met plus rien en dur ici, on utilisera rafraichirCatalogue()
    }

    public static MockService getInstance() {
        if (instance == null) {
            instance = new MockService();
        }
        return instance;
    }

    // =================================================================================
    // NOUVELLE MÉTHODE : CONNEXION API
    // =================================================================================

    public void rafraichirCatalogue() {
        try {
            List<Plat> nouveauxPlats = ApiClient.fetchMenu();
            this.catalogue.clear(); // On vide l'ancien catalogue
            if (nouveauxPlats != null && !nouveauxPlats.isEmpty()) {
                this.catalogue.addAll(nouveauxPlats);
                System.out.println("✅ " + catalogue.size() + " plats chargés !");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur API : " + e.getMessage());
            // Optionnel : ajoute un plat de secours pour tester si la page s'ouvre
            this.catalogue.add(new Plat("ERR", "Erreur Serveur", "Vérifiez la console", 0, 1, ""));
        }
    }

    // =================================================================================
    // MÉTHODES DE CATALOGUE (Inchangées)
    // =================================================================================

    public List<Plat> getPlats() {
        return catalogue;
    }

    public List<Plat> getPlatsParCategorie(int categorie) {
        return catalogue.stream()
                .filter(p -> p.getCategorie() == categorie)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // MÉTHODES DE GESTION DU PANIER (Indispensables pour PanierController)
    // =================================================================================

    public void ajouterAuPanier(Plat p) {
        panier.add(p);
    }

    public void retirerDuPanier(Plat p) {
        Plat aRetirer = null;
        for (Plat item : panier) {
            if (item.getId().equals(p.getId())) {
                aRetirer = item;
                break;
            }
        }
        if (aRetirer != null) panier.remove(aRetirer);
    }

    public int getQuantiteDuPlat(Plat p) {
        int count = 0;
        for (Plat item : panier) {
            if (item.getId().equals(p.getId())) count++;
        }
        return count;
    }

    public List<Plat> getPanier() {
        return panier;
    }

    public double getTotalPanier() {
        return panier.stream().mapToDouble(Plat::getPrix).sum();
    }

    public int getNombreArticlesPanier() {
        return panier.size();
    }

    public void viderPanier() {
        panier.clear();
    }
}