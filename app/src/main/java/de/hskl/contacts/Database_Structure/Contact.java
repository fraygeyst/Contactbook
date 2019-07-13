package de.hskl.contacts.Database_Structure;

import java.util.List;

// Modelklasse für die Tabelle "Kontakt"
public class Contact {
    private int id;
    private String name;
    private int cat_id_pb;
    private List<Integer> cat_id_numbers;
    private List<Integer> cat_id_emails;
    private boolean isfavourite;

    // Getter und Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCat_id_pb() {
        return cat_id_pb;
    }
    public void setCat_id_pb(int cat_id_pb) {
        this.cat_id_pb = cat_id_pb;
    }
    public List<Integer> getCat_id_numbers() {
        return cat_id_numbers;
    }
    public void setCat_id_numbers(List<Integer> cat_id_numbers) {
        this.cat_id_numbers = cat_id_numbers;
    }
    public List<Integer> getCat_id_emails() {
        return cat_id_emails;
    }
    public void setCat_id_emails(List<Integer> cat_id_emails) {
        this.cat_id_emails = cat_id_emails;
    }
    public boolean isIsfavourite() {
        return isfavourite;
    }
    public void setIsfavourite(boolean isfavourite) {
        this.isfavourite = isfavourite;
    }

    // Umwandlung der Listen in einen String zum Schreiben in Datenbank
    public String getCat_id_numbers_toString(){
        String result = "";
        for(int i = 0; i < this.cat_id_numbers.size(); i++){
            result = result + String.valueOf(this.cat_id_numbers.get(i)) + "#";
        }
        return result;
    }
    public String getCat_id_emails_toString(){
        String result = "";
        for(int i = 0; i < this.cat_id_emails.size(); i++){
            result = result + String.valueOf(this.cat_id_emails.get(i)) + "#";
        }
        return result;
    }
}