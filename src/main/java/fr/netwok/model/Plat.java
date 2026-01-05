package fr.netwok.model;

public class Plat {
    private String id;
    private String nom;
    private String description;
    private double prix;
    private String categorie; // "Entrées", "Plats", "Desserts"
    private String imagePath; // Chemin vers l'image

    public Plat(String id, String nom, String description, double prix, String categorie, String imagePath) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.categorie = categorie;
        this.imagePath = imagePath;
    }

    // Getters
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public double getPrix() { return prix; }
    public String getCategorie() { return categorie; }
    public String getImagePath() { return imagePath; }
    
    // Formattage du prix pour l'affichage (ex: "12.50 €")
    public String getPrixFormate() {
        return String.format("%.2f €", prix);
    }
}