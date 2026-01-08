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
    // URL de ton API Back-end
    private static final String BASE_URL = "http://localhost:7001";

    // Client HTTP et Gson pour le JSON
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    // Paramètres MySQL pour le mode secours
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Récupère le menu : priorité à la BDD (pour disposer des champs enrichis),
     * fallback sur l'API si la BDD est indisponible.
     */
    public static List<Plat> fetchMenu() throws Exception {
        try {
            return fetchMenuFromDatabase();
        } catch (Exception dbEx) {
            System.err.println("❌ Erreur BDD : " + dbEx.getMessage());
            dbEx.printStackTrace();
            System.out.println("⚠️ BDD indisponible, tentative via l'API...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/menu"))
                    .GET()
                    .timeout(Duration.ofSeconds(2))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
        }
    }

    private static List<Plat> fetchMenuFromDatabase() throws Exception {
        List<Plat> plats = new ArrayList<>();
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT id, name, description, price, category_id, image_path, disponibilite, allergens, calories, prep_time FROM Dish";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        Plat p = new Plat(
                                rs.getString("id"), rs.getString("name"),
                                rs.getString("description"), rs.getDouble("price"),
                                rs.getInt("category_id"), rs.getString("image_path"),rs.getInt("disponibilite"),
                                rs.getString("allergens"), rs.getInt("calories"), rs.getString("prep_time")
                        );
                        plats.add(p);
                    }
                }
            }

        return plats;
    }

    /**
     * CORRECTION IMPORTANTE ICI :
     * On envoie maintenant les objets complets (ID + OPTIONS) et pas juste les IDs.
     */
    // 1. Méthode pour préparer les données (ce que l'IDE a demandé d'extraire)
    private static Map<String, Object> createRequestBody(int tableNumber, List<Plat> plats) {
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (Plat p : plats) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("dishId", p.getId());
            itemData.put("piment", p.getPimentChoisi());
            itemData.put("accompagnement", p.getAccompagnementChoisi());
            itemsList.add(itemData);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tableNumber", tableNumber);
        requestBody.put("items", itemsList);
        return requestBody;
    }

    // 2. Méthode principale qui envoie (beaucoup plus courte)
    public static void sendOrder(int tableNumber, List<Plat> plats) {
        try {
            // On appelle la petite méthode qu'on a créée au-dessus
            Map<String, Object> requestBody = createRequestBody(tableNumber, plats);

            String json = gson.toJson(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/orders"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Envoi...
            client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Envoyé !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}