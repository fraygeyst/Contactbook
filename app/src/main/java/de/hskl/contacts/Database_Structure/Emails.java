package de.hskl.contacts.Database_Structure;

// Modelklasse für die Tabelle "Email"
public class Emails {
    private int id;
    private String email_adress;
    private int cat_id_category;

    // Getter und Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmail_adress() {
        return email_adress;
    }
    public void setEmail_adress(String email_adress) {
        this.email_adress = email_adress;
    }
    public int getCat_id_category() {
        return cat_id_category;
    }
    public void setCat_id_category(int cat_id_category) {
        this.cat_id_category = cat_id_category;
    }
}