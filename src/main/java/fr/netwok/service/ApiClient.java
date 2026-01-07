package fr.netwok.service;

import com.google.gson.Gson;
import fr.netwok.model.Plat;
import java.net.URI;
import java.net.http.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:7001";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    // Paramètres MySQL pour le mode secours
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static List<Plat> fetchMenu() throws Exception {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/menu"))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(2))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
        } catch (Exception e) {
            System.out.println("API indisponible, chargement depuis la base de données...");
            return fetchMenuFromDatabase();
        }
    }

    private static List<Plat> fetchMenuFromDatabase() throws Exception {
        List<Plat> plats = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // On récupère l'ID en String et category_id en Int
            String query = "SELECT id, name, description, price, category_id, image_path FROM Dish";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

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
            conn.close();
        } catch (Exception e) { throw e; }
        return plats;
    }

    // --- CORRECTION : Accepte List<Plat> pour correspondre au PanierController ---
    public static void sendOrder(int tableNumber, List<Plat> plats) throws Exception {
        try {
            // Extraction des IDs (Le travail que le contrôleur ne fait pas)
            List<String> dishIds = new ArrayList<>();
            for (Plat p : plats) {
                dishIds.add(p.getId());
            }

            // Préparation de l'objet JSON
            var data = new Object() {
                int tableNumberValue = tableNumber;
                List<String> dishIdsList = dishIds;
            };

            String json = gson.toJson(data)
                    .replace("tableNumberValue", "tableNumber")
                    .replace("dishIdsList", "dishIds");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/orders"))
                    .header("Content-Type", "application/json")
                    .timeout(java.time.Duration.ofSeconds(2))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Commande envoyée au serveur");
        } catch (Exception e) {
            System.out.println("⚠️ Serveur indisponible - Commande non envoyée");
        }
    }
}