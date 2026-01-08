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
            return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
        } catch (Exception e) {
            System.out.println("⚠️ API indisponible, chargement depuis la base de données de secours...");
            return fetchMenuFromDatabase();
        }
    }

    private static List<Plat> fetchMenuFromDatabase() throws Exception {
        List<Plat> plats = new ArrayList<>();
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT id, name, description, price, category_id, image_path FROM Dish";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        Plat p = new Plat(
                                rs.getString("id"), rs.getString("name"),
                                rs.getString("description"), rs.getDouble("price"),
                                rs.getInt("category_id"), rs.getString("image_path")
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
    public static void sendOrder(int tableNumber, List<Plat> plats) throws Exception {
        try {
            // 1. On prépare la liste des "items" telle que le Back-end l'attend
            // (cf. la classe DishItemRequest du Back-end.)
            List<Map<String, Object>> itemsList = new ArrayList<>();

            for (Plat p : plats) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("dishId", p.getId());

                // C'est ici qu'on envoie enfin tes choix au serveur !
                itemData.put("piment", p.getPimentChoisi());
                itemData.put("accompagnement", p.getAccompagnementChoisi());

                itemsList.add(itemData);
            }

            // 2. Création du corps JSON global
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("tableNumber", tableNumber);
            // Attention : la clé doit être "items" pour correspondre à ton Back-end
            requestBody.put("items", itemsList);

            String json = gson.toJson(requestBody);
            System.out.println("📤 Envoi JSON COMPLET : " + json);

            // 3. Envoi de la requête POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/orders"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(5))
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
            e.printStackTrace();
        }
    }
}