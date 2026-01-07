package fr.netwok.service;

import com.google.gson.Gson;
import fr.netwok.model.Plat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;

public class ApiClient {
    // URL de ton API Back-end (Modelio / Javalin)
    private static final String BASE_URL = "http://localhost:7001";

    // Client HTTP et Gson pour le JSON
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    // Paramètres MySQL pour le mode secours (si l'API est éteinte)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Récupère le menu depuis l'API, ou depuis la BDD si l'API échoue.
     */
    public static List<Plat> fetchMenu() throws Exception {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/menu"))
                    .GET()
                    .timeout(Duration.ofSeconds(2))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Conversion du JSON reçu en tableau de Plats
            return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
        } catch (Exception e) {
            System.out.println("⚠️ API indisponible, chargement depuis la base de données de secours...");
            return fetchMenuFromDatabase();
        }
    }

    /**
     * Mode secours : Lecture directe de la BDD MySQL
     */
    private static List<Plat> fetchMenuFromDatabase() throws Exception {
        List<Plat> plats = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT id, name, description, price, category_id, image_path FROM Dish";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    while (rs.next()) {
                        String id = rs.getString("id");
                        String nom = rs.getString("name");
                        String description = rs.getString("description");
                        double prix = rs.getDouble("price");
                        int categorie = rs.getInt("category_id");
                        String image = rs.getString("image_path");

                        Plat p = new Plat(id, nom, description, prix, categorie, image);
                        plats.add(p);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return plats;
    }

    /**
     * Envoie la commande au serveur (Correction Map + JSON propre)
     */
    public static void sendOrder(int tableNumber, List<Plat> plats) throws Exception {
        try {
            // 1. Extraction des IDs des plats
            List<String> dishIds = new ArrayList<>();
            for (Plat p : plats) {
                dishIds.add(p.getId());
            }

            // 2. Création d'une Map pour générer un JSON propre
            // C'est ce qui corrige l'erreur "readValue... must not be null"
            Map<String, Object> data = new HashMap<>();
            data.put("tableNumber", tableNumber);
            data.put("dishIds", dishIds);

            String json = gson.toJson(data);
            System.out.println("📤 Envoi JSON : " + json);

            // 3. Envoi de la requête POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/orders"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(2))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("✅ Commande envoyée avec succès !");
            } else {
                System.err.println("❌ Erreur serveur (" + response.statusCode() + ") : " + response.body());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible d'envoyer la commande : " + e.getMessage());
            // Tu peux relancer l'exception si tu veux afficher l'erreur à l'utilisateur
            // throw e;
        }
    }
}