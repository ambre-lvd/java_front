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
    
    // Paramètres MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Récupérer le menu depuis le Back-end ou la BDD locale
    public static List<Plat> fetchMenu() throws Exception {
        try {
            // Essayer d'abord l'API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/menu"))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(2))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Transforme le JSON reçu en liste d'objets Plat
            return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
        } catch (Exception e) {
            System.out.println("API indisponible, chargement depuis la base de données...");
            // Fallback : charger depuis la BDD
            return fetchMenuFromDatabase();
        }
    }

    // Récupérer les plats depuis la base de données MySQL
    private static List<Plat> fetchMenuFromDatabase() throws Exception {
        List<Plat> plats = new ArrayList<>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
            System.out.println("✅ " + plats.size() + " plats chargés depuis la BDD");
        } catch (Exception e) {
            System.err.println("❌ Erreur connexion BDD : " + e.getMessage());
            throw e;
        }
        
        return plats;
    }

    // Envoyer la commande finale au Back-end
    public static void sendOrder(int tableNumber, List<String> dishIds) throws Exception {
        try {
            // Objet temporaire pour correspondre au format attendu par le Back
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
            System.out.println("⚠️ Serveur indisponible - Commande traitée localement");
            // Ne pas lever l'exception, continuer normalement
        }
    }
}