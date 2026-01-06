package fr.netwok.service;

import com.google.gson.Gson;
import fr.netwok.model.Plat;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors; // AJOUT : Import indispensable pour .collect()

public class ApiClient {
    private static final String BASE_URL = "http://localhost:7001";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static List<Plat> fetchMenu() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/menu"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
    }

    public static void sendOrder(int tableNumber, List<Plat> panier) throws Exception {
        // On crée une structure simple (Map ou classe dédiée)
        // Utiliser une classe interne static est plus propre que l'objet anonyme "var orderData"
        OrderRequest data = new OrderRequest(tableNumber, panier);

        String json = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Erreur serveur : " + response.statusCode());
        }
    }

    // --- CLASSES DE STRUCTURE POUR GSON ---

    // Structure globale de la commande
    private static class OrderRequest {
        int tableNumber;
        List<ItemData> items;

        OrderRequest(int tableNumber, List<Plat> panier) {
            this.tableNumber = tableNumber;
            this.items = panier.stream()
                    .map(p -> new ItemData(p.getId(), 1, 0, 0))
                    .collect(Collectors.toList());
        }
    }

    // Structure d'un plat dans la commande (doit être STATIC)
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