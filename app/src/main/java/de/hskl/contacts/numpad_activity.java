package de.hskl.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class numpad_activity extends AppCompatActivity implements View.OnClickListener {

    // Klassenvariablen
    private TextView number;
    private String numbertext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numpad_activity);
        setTitle(R.string.title_numpad);

        // Klassenvariablen initialisieren
        number = findViewById(R.id.number_textpad);
        numbertext = "";

        // Button initialisieren
        Button but0 = findViewById(R.id.button_0);
        Button but1 = findViewById(R.id.button_1);
        Button but2 = findViewById(R.id.button_2);
        Button but3 = findViewById(R.id.button_3);
        Button but4 = findViewById(R.id.button_4);
        Button but5 = findViewById(R.id.button_5);
        Button but6 = findViewById(R.id.button_6);
        Button but7 = findViewById(R.id.button_7);
        Button but8 = findViewById(R.id.button_8);
        Button but9 = findViewById(R.id.button_9);
        Button but_star = findViewById(R.id.button_star);
        Button but_hash = findViewById(R.id.button_hash);
        ImageButton delete = findViewById(R.id.button_del);
        ImageButton call = findViewById(R.id.button_call);

        // Listener setzen
        but0.setOnClickListener(this);
        but1.setOnClickListener(this);
        but2.setOnClickListener(this);
        but3.setOnClickListener(this);
        but4.setOnClickListener(this);
        but5.setOnClickListener(this);
        but6.setOnClickListener(this);
        but7.setOnClickListener(this);
        but8.setOnClickListener(this);
        but9.setOnClickListener(this);
        but_star.setOnClickListener(this);
        but_hash.setOnClickListener(this);
        delete.setOnClickListener(this);
        call.setOnClickListener(this);
    }
    // Methode um Nummertext upzudaten
    private void updateText(){
        number.setText(numbertext);
    }
    // OnClickListener
    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.button_0){
            // Text wird zur Nummer hinzugefügt
            numbertext += "0";
        }
        if(v.getId()== R.id.button_1){
            numbertext += "1";
        }
        if(v.getId()== R.id.button_2){
            numbertext += "2";
        }
        if(v.getId()== R.id.button_3){
            numbertext += "3";
        }
        if(v.getId()== R.id.button_4){
            numbertext += "4";
        }
        if(v.getId()== R.id.button_5){
            numbertext += "5";
        }
        if(v.getId()== R.id.button_6){
            numbertext += "6";
        }
        if(v.getId()== R.id.button_7){
            numbertext += "7";
        }
        if(v.getId()== R.id.button_8){
            numbertext += "8";
        }
        if(v.getId()== R.id.button_9){
            numbertext += "9";
        }
        if(v.getId()== R.id.button_star){
            numbertext += "*";
        }
        if(v.getId()== R.id.button_hash){
            numbertext += "#";
        }
        if(v.getId()== R.id.button_del){
            // Letzte Stelle aus String löschen
            if(!numbertext.equals("")){
                numbertext = numbertext.substring(0, numbertext.length()-1);
            }
        }
        if(v.getId()== R.id.button_call){
            // Anruf starten mit String aus aktueller Nummer
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse("tel:"+numbertext));
            // Überprüfen ob Berechtigungen vorhanden
            if (ContextCompat.checkSelfPermission(numpad_activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(numpad_activity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                startActivity(phoneIntent);
            }
        }
        // TextView aktualisieren
        updateText();
    }
}