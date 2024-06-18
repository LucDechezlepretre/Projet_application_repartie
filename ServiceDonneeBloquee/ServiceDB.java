import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;

public class ServiceDB implements ServiceDonneesBloquees{
    @Override
    public String recupererDonneesBloquees() throws RemoteException {
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
        String json = null;
        try {
            response = client.send(request, BodyHandlers.ofString());

            //System.out.println(response.statusCode());
            if(response.statusCode() >= 200 && response.statusCode() <= 399){
                System.out.println(response.body()); 
                json = response.body();


            }            
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

    public static void main(String[] args){
        //On crée les objets du service
        ServiceDB serviceDB = new ServiceDB();
            

        try{
            ServiceDonneesBloquees service = (ServiceDonneesBloquees)UnicastRemoteObject.exportObject(serviceDB, 0);
            try{
                Registry reg = LocateRegistry.getRegistry(1099); /* Création de l'annuaire */
                
            //Un_port); 
            /* Un_port = un entier particulier ou 0 pour auto-assigné */

                try{
                    reg.rebind("DonneeBloquee", service);                  /* Enregistrement de la référence sous le nom "CompteurDistant" */
                }
                catch(RemoteException e){
                    System.out.println("Impossible de communiquer avec l'annuaire");
                }
            //System.out.p
            }
            catch(RemoteException e){
            System.out.println("Erreur lors de l'enregistrement de l'annuaire");
            }
        }
        catch(RemoteException e){
            System.out.println("Erreur lors de l'enregistrement d'un service");
        }
    }
    
}
