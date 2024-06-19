public class Restaurant {
    private int id;
    private String nom;
    private String adresse;
    private double lat;
    private double lon;
 
    public Restaurant(int id, String nom, String adresse, double lat, double lon) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId(){
        return this.id;
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