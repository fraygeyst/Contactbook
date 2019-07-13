package de.hskl.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Einstellungsmoeglichkeiten der Zugriffsberechtigungen der App.
 * Dient jediglich der Uebersicht bzw. um eine Berechtigung im nachhinein zu erlauben.
 * Android sieht es nicht vor?, erlaubte Berechtigungen ueber die App zu entziehen.
 * Dies muss über das Android System erfolgen.
 * Z. B. über > APP-Logo gedrueckt halten > PERMISSIONS
 */

public class settings_permission_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_permission_activity);

        // Views initialsieren
        final Switch callphone = findViewById(R.id.settings_perm_sw_callphone);
        final Switch camera = findViewById(R.id.settings_perm_sw_camera);

        // Permission Abfrage und setzen der Switchs
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            callphone.setChecked(true);
        }else {
            callphone.setChecked(false);
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            camera.setChecked(true);
        }else {
            camera.setChecked(false);
        }

        // Listener für Swtiches
        callphone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ //wenn es auf TRUE wechselt, dann anfragen
                    ActivityCompat.requestPermissions(settings_permission_activity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                }else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(),"Already granted permission can't be revoked. Please use your Phone Settings to revoke Permissions.", Toast.LENGTH_LONG).show();
                        callphone.setChecked(true);
                    }
                }
            }
        });
        camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ //wenn es auf TRUE wechselt, dann anfragen
                    ActivityCompat.requestPermissions(settings_permission_activity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }else {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(),"Already granted permission can't be revoked. Please use your Phone Settings to revoke Permissions.", Toast.LENGTH_LONG).show();
                        camera.setChecked(true);
                    }
                }
            }
        });
    }
}
