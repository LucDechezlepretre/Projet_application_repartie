import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class MainHttp{
    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .proxy(ProxySelector.of(new InetSocketAddress("www-cache.iutnc.univ-lorraine.fr", 3128)))
        .build();
         HttpRequest request = HttpRequest.newBuilder()
         .uri(URI.create("https://carto.g-ny.org/data/cifs/cifs_waze_v2.json"))
         .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, BodyHandlers.ofString());

            //System.out.println(response.statusCode());
            if(response.statusCode() >= 200 && response.statusCode() <= 399){
                System.out.println(response.body());  
            }            
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}