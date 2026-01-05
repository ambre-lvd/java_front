package fr.netwok.service;

import com.google.gson.Gson;
import fr.netwok.model.Plat;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.Arrays;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:7001";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    // Récupérer le menu depuis le Back-end
    public static List<Plat> fetchMenu() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/menu"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Transforme le JSON reçu en liste d'objets Plat
        return Arrays.asList(gson.fromJson(response.body(), Plat[].class));
    }

    // Envoyer la commande finale au Back-end
    public static void sendOrder(int tableNumber, List<String> dishIds) throws Exception {
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
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}