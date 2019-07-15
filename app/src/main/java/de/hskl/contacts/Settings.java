package de.hskl.contacts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import de.hskl.contacts.Database_Structure.Numbers;

public class Settings extends AppCompatActivity {
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_settings);

        final Button show_qr = findViewById(R.id.settings_ownnumber_show);

        pref = getSharedPreferences("contact_prefs.hskl", Context.MODE_PRIVATE);
        if (pref.getBoolean("number_set", false)){
            show_qr.setVisibility(View.VISIBLE);
        }


        // Buttons initialisieren und Listener setzen
        Button emcats = findViewById(R.id.settings_email_category_but);
        emcats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aufruf der Category-Settings
                Intent email_settings_intent = new Intent(Settings.this, settings_email_category_activity.class);
                Settings.this.startActivity(email_settings_intent);
            }
        });
        Button nucats = findViewById(R.id.settings_number_category_but);
        nucats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aufruf der Category-Settings
                Intent settings_intent = new Intent(Settings.this, settings_category_activity.class);
                Settings.this.startActivity(settings_intent);
            }
        });
        Button perms = findViewById(R.id.settings_permissions_but);
        perms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aufruf der Permission-Settings
                Intent settings_intent = new Intent(Settings.this, settings_permission_activity.class);
                Settings.this.startActivity(settings_intent);
            }
        });
        Button ownnumber = findViewById(R.id.settings_ownnumber_edit);
        ownnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Settings.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_own_contact);
                final EditText name = dialog.findViewById(R.id.dialog_oc_name);
                final EditText number = dialog.findViewById(R.id.dialog_oc_number);
                if (pref.getBoolean("number_set", false)){
                    name.setText(pref.getString("own_name", ""));
                    number.setText(pref.getString("own_number", ""));
                }
                Button save = dialog.findViewById(R.id.dialog_oc_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("own_name", name.getText().toString());
                        editor.putString("own_number", number.getText().toString());
                        editor.putBoolean("number_set", true);
                        show_qr.setVisibility(View.VISIBLE);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.dialog_oc_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        show_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = pref.getString("own_number", "");
                String name = pref.getString("own_name", "");
                // Text für QR-Code holen
                String text = generateQR(name, number);
                // Erstellen des Codes vorbereiten
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    // Darstellung des QR-Codes in Dialogfenster
                    final Dialog dialog = new Dialog(Settings.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_qr_code);
                    ImageView qrview = dialog.findViewById(R.id.dialog_qr_image);
                    qrview.setImageBitmap(bitmap);
                    Button cancel = dialog.findViewById(R.id.dialog_qr_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // QR-Code String generieren
    private String generateQR(String name, String number){
        // Beginn mit "cont" für späteres Filtern
        String result = "cont#";
        // Verschiedene Kontaktteile werden mit "#" getrennt und Ende mit "end" für Filter
        result += name + "#" + getNumbersForQR(number) + "#" + "#" + false + "#end";
        return result;
    }
    // Nummern für QR-Code
    private String getNumbersForQR(String number){
        String result = "";
        Numbers number1 = new Numbers();
        number1.setNumber(number);
        List<Numbers> numlist = new ArrayList<>();
        numlist.add(number1);
        boolean first = true;
        // Nummerliste wird in Schleife durchlaufen
        for(int i = 0; i < numlist.size(); i++){
            // Hilfsnummer
            Numbers hnum = numlist.get(i);
            // Falls erster durchlauf wird Trennzeichen weggelassen
            result += hnum.getNumber() + "_" + "Privat";
        }
        return result;
    }
}