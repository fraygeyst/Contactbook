package de.hskl.contacts;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.hskl.contacts.Database_Structure.Contact;
import de.hskl.contacts.Database_Structure.Numbers;
import de.hskl.contacts.Helper.DBHelper;

public class MainActivity extends AppCompatActivity {
    // Klassenvariablen für Verwendung in mehreren Methoden
    private DBHelper dbHelper;
    private ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Custom Toolbar initialisieren
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        // Custom Toolbar füllen
        final TextView tool_title = findViewById(R.id.main_toolbar_title);
        tool_title.setText("Kontakte");
        final ImageButton tool_search_close =  findViewById(R.id.main_toolbar_search_exit);
        final ImageButton tool_search = findViewById(R.id.main_toolbar_search);
        final EditText tool_searchtext = findViewById(R.id.main_toolbar_searchtext);
        tool_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tool_title.setVisibility(View.GONE);
                tool_searchtext.setVisibility(View.VISIBLE);
                tool_search.setVisibility(View.GONE);
                tool_search_close.setVisibility(View.VISIBLE);
                tool_searchtext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        setListViews(dbHelper.getContactHashListshort(tool_searchtext.getText().toString()));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
        tool_search_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tool_search.setVisibility(View.VISIBLE);
                tool_search_close.setVisibility(View.GONE);
                tool_title.setVisibility(View.VISIBLE);
                tool_searchtext.setVisibility(View.GONE);
                setListViews(dbHelper.getContactHashListshort());
            }
        });
        ImageButton tool_settings = findViewById(R.id.main_toolbar_settings);
        tool_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aufruf der Settings-Activity
                Intent settings_intent = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(settings_intent);
            }
        });


        // Vorbereitung Datenbankhilfsklasse
        dbHelper = new DBHelper(this);

        // Überprüfen ob Permission für Kameranutzung gegeben wurde
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        // Überprüfen ob App das erste Mal ausgeführt wird
        final SharedPreferences prefs = this.getSharedPreferences("contact_prefs.hskl", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        if(prefs.getBoolean("firsttime", true)){
            // Beispieldaten laden
            dbHelper.loadDefaults();
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_own_contact);
            Button save = dialog.findViewById(R.id.dialog_oc_save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText name = dialog.findViewById(R.id.dialog_oc_name);
                    EditText number = dialog.findViewById(R.id.dialog_oc_number);
                    editor.putString("own_name", name.getText().toString());
                    editor.putString("own_number", number.getText().toString());
                    editor.putBoolean("number_set", true);
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
            editor.putBoolean("firsttime",false);
            editor.apply();
        }
        if (prefs.getBoolean("number_set", false)){
            final TextView own_cont = findViewById(R.id.main_own_cont);
            own_cont.setVisibility(View.VISIBLE);
            own_cont.setText(prefs.getString("own_name", ""));
            own_cont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = prefs.getString("own_number", "");
                    String name = prefs.getString("own_name", "");
                    // Text für QR-Code holen
                    String text = generateQR(name, number);
                    // Erstellen des Codes vorbereiten
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        // Darstellung des QR-Codes in Dialogfenster
                        final Dialog dialog = new Dialog(MainActivity.this);
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
            own_cont.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_delete);
                    TextView title = dialog.findViewById(R.id.dialog_delete_text);
                    title.setText(R.string.own_contact_dialog);
                    Button delete = dialog.findViewById(R.id.dialog_delete_delete);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.remove("own_name");
                            editor.remove("own_number");
                            editor.remove("number_set");
                            Toast.makeText(MainActivity.this, R.string.own_cont_deleted, Toast.LENGTH_SHORT).show();
                            own_cont.setVisibility(View.GONE);
                            editor.apply();
                            dialog.dismiss();
                        }
                    });
                    Button cancel = dialog.findViewById(R.id.dialog_delete_cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
        }

        // Views initialisieren und Listener erstellen
        contactList = findViewById(R.id.main_listView);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aufruf der Erstellungs-Activity für Kontakte -> Manuelles Erstellen
                Intent settings_intent = new Intent(MainActivity.this, contact_create_activity.class);
                MainActivity.this.startActivity(settings_intent);
            }
        });
        FloatingActionButton fab_numpad = findViewById(R.id.fab_numberpad);
        fab_numpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent numpad_intent = new Intent(MainActivity.this, numpad_activity.class);
                MainActivity.this.startActivity(numpad_intent);
            }
        });

        // Füllen der ListView mit allen Kontakten
        setListViews(dbHelper.getContactHashListshort());
    }

    // Füllen und Updaten der ListViews
    private void setListViews(List<HashMap<String, String>> list){
        // Simple-Adapter mit überschriebener getView()-Methode um Profilbild anzuzeigen
        ListAdapter contact_adapter = new SimpleAdapter( MainActivity.this, list, R.layout.list_item_contact_short, new String[] { "id", "name"}, new int[] {R.id.listitem_contact_short_id, R.id.listitem_contact_short_name}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // View abgreifen
                View itemView = super.getView(position, convertView, parent);
                // Kontakt-Id rausfinden
                TextView id = itemView.findViewById(R.id.listitem_contact_short_id);
                // ImageView vorbereiten
                ImageView image = itemView.findViewById(R.id.listitem_contact_short_image);
                // Auslesen der Bitmap aus Datenbank
                Bitmap bits = dbHelper.getProfPicByContactId(Integer.parseInt(id.getText().toString()));
                // Wenn Bild vorhanden ist, dann dieses benutzen, sonst Standardbild einfügen
                if(bits == null){
                    image.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_contact));
                } else {
                    image.setImageBitmap(bits);
                }
                // Überprüfen ob Kontakt Favorit ist
                Contact cont = dbHelper.getContactById(Integer.parseInt(id.getText().toString()));
                if(cont.isIsfavourite()){
                    LinearLayout bg = itemView.findViewById(R.id.listitem_contact_short_bg);
                    bg.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                }
                return itemView;
            }
        };
        // Listener bei Click auf ListItem -> Aufruf des Items in Detailansicht
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView titemid = view.findViewById(R.id.listitem_contact_short_id);
                final int itemid = Integer.parseInt(titemid.getText().toString());
                Intent intent = new Intent(MainActivity.this, contact_view_activity.class);
                intent.putExtra("item_id", itemid);
                MainActivity.this.startActivity(intent);
            }
        });
        // Listener bei langem Click Kontakt löschen
        contactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_delete);
                final TextView titemid = view.findViewById(R.id.listitem_contact_short_id);
                Button delete = dialog.findViewById(R.id.dialog_delete_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int itemid = Integer.parseInt(titemid.getText().toString());
                        dbHelper.deleteContact(itemid);
                        setListViews(dbHelper.getContactHashListshort());
                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.dialog_delete_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
                return true;
            }
        });
        contactList.setAdapter(contact_adapter);
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