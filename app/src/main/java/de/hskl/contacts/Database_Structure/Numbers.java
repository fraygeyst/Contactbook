package de.hskl.contacts.Database_Structure;

// Modelklasse für die Tabelle "Nummern"
public class Numbers {
    private int id;
    private String number;
    private int cat_id_category;

    // Getter und Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public int getCat_id_category() {
        return cat_id_category;
    }
    public void setCat_id_category(int cat_id_category) {
        this.cat_id_category = cat_id_category;
    }
}