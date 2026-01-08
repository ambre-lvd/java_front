package fr.netwok.model;

public class Plat {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private final int category; // "Entrée", "Plat", "Dessert"
    private final String imagePath; // Chemin vers l'image
    private int pimentChoisi = 0;
    private int accompagnementChoisi = 0;
    private int disponibilite;

    public Plat(String id, String nom, String description, double prix, int categorie, String imagePath, int disponibilite) {
        this.id = id;
        this.name = nom;
        this.description = description;
        this.price = prix;
        this.category = categorie;
        this.imagePath = imagePath;
        this.disponibilite = disponibilite;
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
    public int getDisponibilite() { return disponibilite; }
    public void setDisponibilite(int disponibilite) { this.disponibilite = disponibilite; }

}