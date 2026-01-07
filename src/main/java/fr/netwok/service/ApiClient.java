package fr.netwok.service;

import com.google.gson.Gson;
import fr.netwok.model.Plat;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:7001";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Récupère le menu via l'API.
     * Si le serveur est éteint, une exception est levée.
     */
    public static List<Plat> fetchMenu() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/menu"))
                .GET()
                .timeout(java.time.Duration.ofSeconds(3))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur serveur : " + response.statusCode());
        }

        return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
    }

    /**
     * Envoie la commande au Back-end.
     * Inclut maintenant le niveau de piment et l'accompagnement choisis.
     */
    public static void sendOrder(int tableNumber, List<Plat> panier) throws Exception {
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
            throw new RuntimeException("Erreur lors de l'envoi : " + response.statusCode());
        }
        System.out.println("✅ Commande transmise au serveur avec succès.");
    }

    // --- CLASSES INTERNES POUR LE MAPPING JSON (GSON) ---

    private static class OrderRequest {
        int tableNumber;
        List<ItemData> items;

        OrderRequest(int tableNumber, List<Plat> panier) {
            this.tableNumber = tableNumber;
            // Transformation du panier en format attendu par le Back-end
            this.items = panier.stream()
                    .map(p -> new ItemData(
                            p.getId(),
                            1,
                            p.getPimentChoisi(),
                            p.getAccompagnementChoisi()
                    ))
                    .collect(Collectors.toList());
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
