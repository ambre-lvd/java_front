package fr.netwok.model;

public class Plat {
    private String id;
    private String name;
    private String description;
    private double price;
    private int category; // "Entrée", "Plat", "Dessert"
    private String imagePath; // Chemin vers l'image
    private int pimentChoisi = 0;
    private int accompagnementChoisi = 0;

    public Plat(String id, String nom, String description, double prix, int categorie, String imagePath) {
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
    public int getCategorie() { return category; }
    public String getImagePath() { return imagePath; }
    public void setPimentChoisi(int pimentChoisi) { this.pimentChoisi = pimentChoisi; }
    public int getPimentChoisi() { return pimentChoisi; }
    public void setAccompagnementChoisi(int accompagnementChoisi) { this.accompagnementChoisi = accompagnementChoisi; }
    public int getAccompagnementChoisi() { return accompagnementChoisi; }
    private int disponibilite;
    public int getDisponibilite() { return disponibilite; }
    public void setDisponibilite(int disponibilite) { this.disponibilite = disponibilite; }

    // Formattage du prix pour l'affichage (ex: "12.50 €")
    public String getPrixFormate() {
        return String.format("%.2f €", price);
    }
}