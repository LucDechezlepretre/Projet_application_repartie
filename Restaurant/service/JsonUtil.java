import java.util.ArrayList;

public class JsonUtil {
    
    public static String toJson(ArrayList<Restaurant> restaurants){

        StringBuilder json = new StringBuilder();

        json.append("{");
        json.append("\"data\": { \"restaurants\": [");
        for (int i = 0; i < restaurants.size(); i++) {
            json.append(JsonUtil.toJson(restaurants.get(i)));
            if (i < restaurants.size() - 1) {
                json.append(",");
            }
        }
        json.append("]}");
        json.append("}");
        return json.toString();
    }

    public static String toJson(Restaurant restaurant) {
        return String.format("{\"adresse\":\"%s\",\"latitude\":%s,\"longitude\":%s,\"nom\":\"%s\"}",
                restaurant.getAdresse(), restaurant.getLatitude(), restaurant.getLongitude(), restaurant.getNom());
    }
}
