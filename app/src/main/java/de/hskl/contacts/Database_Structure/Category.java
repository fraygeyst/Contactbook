package de.hskl.contacts.Database_Structure;

// Modelklasse für die Tabelle "Kategorie"
public class Category {
    private int id;
    private String name;

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
}