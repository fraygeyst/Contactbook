package de.hskl.contacts.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

// Utility Klasse für Profilbilder
public class DbBitmapUtility {

    // Umwandlung von Bitmap in Byte-Array zum Abspeichern in der Datenbank
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Umwandlung von Byte-Array in Bitmap zur Darstellung innerhalb der App
    public static Bitmap getImage(byte[] image) {
        if(image != null){
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } else {
            return null;
        }
    }
}