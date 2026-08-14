package demo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.HashMap;

import serpapi.SerpApi;
import serpapi.SerpApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class App { 
    public static void main(String[] args) { 
        String apiKey = "token here"; 
        String engine = "google"; 
        System.out.println("Using Offical Java Library"); 
        Map<String, String> auth = new HashMap<>(); 
        auth.put("engine", engine); 
        auth.put("api_key", apiKey); 
        
        SerpApi serpapi = new SerpApi(auth); 
        Map<String, String> parameter = new HashMap<>(); 
        parameter.put("q", "grace hopper birthday"); 
        
        try { 
            JsonObject data = serpapi.search(parameter); 
            String foundText = findDateValueInJson(data);
            
            if (foundText != null) {
                // Apply regex pattern to capture strictly: Month DD, YYYY
                Pattern datePattern = Pattern.compile("(January|February|March|April|May|June|July|August|September|October|November|December)\\s+\\d{1,2},\\s+\\d{4}");
                Matcher matcher = datePattern.matcher(foundText);
                
                if (matcher.find()) {
                    System.out.println("Grace Hopper's Birthday: " + matcher.group(0));
                } else {
                    System.out.println("Grace Hopper's Birthday: " + foundText);
                }
            } else {
                System.out.println("Could not extract any date from the response.");
            }
        } catch (SerpApiException e) { 
            System.out.println("SerpApi request failed."); 
            e.printStackTrace(); 
            System.exit(1); 
        }
    } 

    private static String findDateValueInJson(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("born")) return obj.get("born").getAsString();
            if (obj.has("birthday")) return obj.get("birthday").getAsString();
            if (obj.has("date_of_birth")) return obj.get("date_of_birth").getAsString();
            
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    String value = entry.getValue().getAsString();
                    if (value.contains("1906") || value.contains("December")) {
                        return value;
                    }
                }
                String deepResult = findDateValueInJson(entry.getValue());
                if (deepResult != null) return deepResult;
            }
        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            for (JsonElement item : arr) {
                String deepResult = findDateValueInJson(item);
                if (deepResult != null) return deepResult;
            }
        }
        return null;
    }
}