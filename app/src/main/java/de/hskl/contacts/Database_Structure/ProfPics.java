package de.hskl.contacts.Database_Structure;

// Modelklasse für die Tabelle "Profilbilder"
public class ProfPics {
    private int id;
    private byte[] image;

    // Getter und Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
}