package fr.netwok.model;

public class Plat {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category; // "Entrée", "Plat", "Dessert"
    private String imagePath; // Chemin vers l'image

    public Plat(String id, String nom, String description, double prix, String categorie, String imagePath) {
        this.id = id;
        this.name = nom;
        this.description = description;
        this.price = prix;
        this.category = categorie;
        this.imagePath = imagePath;
    }

    // Getters
    public String getId() { return id; }
    public String getNom() { return name; }
    public String getDescription() { return description; }
    public double getPrix() { return price; }
    public String getCategorie() { return category; }
    public String getImagePath() { return imagePath; }
    
    // Formattage du prix pour l'affichage (ex: "12.50 €")
    public String getPrixFormate() {
        return String.format("%.2f €", price);
    }
}