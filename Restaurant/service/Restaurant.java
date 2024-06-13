public class Restaurant {
    private String nom;
    private String adresse;
    private double lat;
    private double lon;
 
    public Restaurant(String nom, String adresse, double lat, double lon) {
        this.nom = nom;
        this.adresse = adresse;
        this.lat = lat;
        this.lon = lon;
    }

    public String getNom(){
        return this.nom;
    }

    public String getAdresse(){
        return this.adresse;
    }

    public double getLatitude(){
        return this.lat;
    }
    
    public double getLongitude(){
        return this.lon;
    }
}
