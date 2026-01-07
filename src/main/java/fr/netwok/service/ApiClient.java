package fr.netwok.service;

import com.google.gson.Gson;
import fr.netwok.model.Plat;
import java.net.URI;
import java.net.http.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors; // Indispensable

public class ApiClient {
    private static final String BASE_URL = "http://localhost:7001";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

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
            String query = "SELECT id, name, description, price, category_id, image_path FROM Dish";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                plats.add(new Plat(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("category_id"),
                        rs.getString("image_path")
                ));
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Erreur BDD : " + e.getMessage());
            throw e;
        }
        return plats;
    }

    // --- MÉTHODE CORRIGÉE POUR ACCEPTER List<Plat> ---
    public static void sendOrder(int tableNumber, List<Plat> panier) throws Exception {
        // On utilise la structure de classe interne pour un JSON propre
        OrderRequest data = new OrderRequest(tableNumber, panier);
        String json = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders"))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Erreur serveur : " + response.statusCode());
        }
    }

    // --- STRUCTURES POUR GSON (Mapping JSON) ---
    private static class OrderRequest {
        int tableNumber;
        List<ItemData> items;

        OrderRequest(int tableNumber, List<Plat> panier) {
            this.tableNumber = tableNumber;
            this.items = panier.stream()
                    .map(p -> new ItemData(
                            p.getId(),
                            1,
                            p.getPimentChoisi(),        // On récupère le choix réel
                            p.getAccompagnementChoisi() // On récupère le choix réel
                    ))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    private static class ItemData {
        String dishId;
        int quantity;
        int piment;
        int accompagnement;

        ItemData(String id, int q, int p, int a) {
            this.dishId = id;
            this.quantity = q;
            this.piment = p;
            this.accompagnement = a;
        }
    }
}