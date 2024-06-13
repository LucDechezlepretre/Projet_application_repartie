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

    public String getLatitude(){
        return String.valueOf(this.lat).replace(',', '.');
    }
    
    public String getLongitude(){
        return String.valueOf(this.lon).replace(',', '.');
    }
}
