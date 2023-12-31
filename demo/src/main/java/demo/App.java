/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package demo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.HashMap;

import serpapi.SerpApi;
import serpapi.SerpApiException;

public class App {
    public static void main(String[] args) throws SerpApiException {
        if(args.length != 1) {
            System.out.println("Usage: app <secret api key>");
            System.exit(1);
        }

        String location = "Austin,Texas";
        System.out.println("find the first Coffee in " + location);

        // set api_key provided by the command line
        Map<String, String> auth = new HashMap<>();
        auth.put("api_key", args[0]);

        // Create search
        SerpApi serpapi= new SerpApi(auth);

        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", "Coffee");
        parameter.put("location", location);

        try {
           // Perform search
           JsonObject data = serpapi.search(parameter);
           
           // Decode response
           JsonArray results = data.get("local_results").getAsJsonObject().get("places").getAsJsonArray();
           JsonObject first_result = results.get(0).getAsJsonObject();
           System.out.println("first coffee shop: " + first_result.get("title").getAsString() + " found on Google in " + location);
        } catch (SerpApiException e) {
            System.out.println("oops exception detected!");
            e.printStackTrace();
        }
    }
}
