package de.hskl.contacts;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hskl.contacts.Database_Structure.Contact;
import de.hskl.contacts.Database_Structure.Emails;
import de.hskl.contacts.Database_Structure.Numbers;
import de.hskl.contacts.Database_Structure.ProfPics;
import de.hskl.contacts.Helper.CaptureActivityPortrait;
import de.hskl.contacts.Helper.DBHelper;
import de.hskl.contacts.Helper.DbBitmapUtility;

public class contact_create_activity extends AppCompatActivity {

    // Klassenvariablen für Nutzung in mehreren Methoden
    private DBHelper dbHelper;
    private EditText cont_name;
    private List<Numbers> numbers;
    private List<Emails> emails;
    private ListView numberlist;
    private ListView emaillist;
    private Contact fillCont;
    private Boolean filled = false;
    private Bitmap image = null;
    private TextView title;
    private boolean favourite = false;
    private ImageButton isfav;
    private ImageButton isntfav;
    private de.hdodenhof.circleimageview.CircleImageView imgBut;
    private static final int REQUEST_CAPTURE_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_create_activity);

        // Custom Actionbar initialsieren
        final View actionBarLayout = getLayoutInflater().inflate(R.layout.contact_detail_actionbar,null);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout, new androidx.appcompat.app.ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Actionbar initialisieren und Listener setzen
        title = findViewById(R.id.contdet_toolbar_title);
        isfav = findViewById(R.id.contdet_toolbar_isfav);
        isntfav = findViewById(R.id.contdet_toolbar_isntfav);
        isfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favourite = false;
                switchfavourites();
            }
        });
        isntfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favourite = true;
                switchfavourites();
            }
        });
        ImageButton qr_f = findViewById(R.id.contdet_toolbar_qr_f);
        qr_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filled){
                    // Text für QR-Code holen
                    String text = generateQR();
                    // Erstellen des Codes vorbereiten
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        // Darstellung des QR-Codes in Dialogfenster
                        final Dialog dialog = new Dialog(contact_create_activity.this);
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
                } else {
                    // Aufruf des Barcode Scanners
                    IntentIntegrator integrator = new IntentIntegrator(contact_create_activity.this);
                    integrator.setPrompt("Barcode scannen");
                    integrator.setOrientationLocked(true);
                    integrator.setCaptureActivity(CaptureActivityPortrait.class);
                    integrator.initiateScan();
                }
            }
        });
        ImageButton save = findViewById(R.id.contdet_toolbar_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Speichern eines neuen Kontakts bzw Update eines vorhandenen
                // Kontakt-Objekt erstellen
                Contact contact = new Contact();
                // Überprüfen ob Name eingegeben wurde
                if(!cont_name.getText().toString().equals("")){
                    // Name setzen
                    contact.setName(cont_name.getText().toString());
                    // Nummern setzen
                    contact.setCat_id_numbers(getNumberList());
                    // Emails setzen
                    contact.setCat_id_emails(getEmailList());
                    // Falls Bild vorhanden ist, Bild setzen
                    if(image != null){
                        ProfPics ipic = new ProfPics();
                        ipic.setImage(DbBitmapUtility.getBytes(image));
                        contact.setCat_id_pb((int)dbHelper.insertProfPic(ipic));
                    }
                    // Wenn Favorit -> Setzen
                    if(favourite){
                        contact.setIsfavourite(true);
                    } else {
                        contact.setIsfavourite(false);
                    }
                    // Überprüfen ob Kontakt erstellen oder updaten
                    if(filled){
                        contact.setId(fillCont.getId());
                        dbHelper.updateContact(contact);
                        // Kontakt anschauen
                        Intent intent = new Intent(contact_create_activity.this, contact_view_activity.class);
                        intent.putExtra("item_id", fillCont.getId());
                        contact_create_activity.this.startActivity(intent);
                    } else {
                        long contid = dbHelper.insertContact(contact);
                        // Kontakt anschauen
                        Intent intent = new Intent(contact_create_activity.this, contact_view_activity.class);
                        intent.putExtra("item_id", (int)contid);
                        contact_create_activity.this.startActivity(intent);
                    }
                } else {
                    Toast.makeText(contact_create_activity.this, R.string.cont_name_missing, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initalisieren von Klassenvariablen
        numbers = new ArrayList<>();
        emails = new ArrayList<>();

        // Vorbereiten der Datenbankhilfsklasse
        dbHelper = new DBHelper(this);

        // Views initialisieren
        cont_name = findViewById(R.id.cont_detail_name);
        numberlist = findViewById(R.id.cont_detail_number_list);
        emaillist = findViewById(R.id.cont_detail_add_email_list);

        // Überprüfen ob Neuer Kontakt erstellt wird oder vorhandener Bearbeitet
        imgBut = findViewById(R.id.cont_detail_avatar);
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras != null){
                int id = extras.getInt("item_id");
                filled = true;
                fillData(id);
            } else {
                imgBut.setImageDrawable(ContextCompat.getDrawable(contact_create_activity.this, R.drawable.ic_contact));
            }
        }

        // Views initialisieren und Listener setzen
        ImageButton add_number_but = findViewById(R.id.cont_detail_add_number);
        add_number_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog zum Einfügen einer neuen Telefonnummer
                final Dialog dialog = new Dialog(contact_create_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_add_number);
                Button save = dialog.findViewById(R.id.dialog_en_save);
                final Spinner category_spinner = dialog.findViewById(R.id.dialog_en_spinner);
                ArrayAdapter spinner_adapter = new ArrayAdapter(contact_create_activity.this, android.R.layout.simple_spinner_item, dbHelper.getCategoryList());
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category_spinner.setAdapter(spinner_adapter);
                final EditText number_text = dialog.findViewById(R.id.dialog_en_text);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Neues Nummer-Objekt erstellen
                        Numbers number = new Numbers();
                        // Nummer einfügen
                        number.setNumber(number_text.getText().toString());
                        // Überprüfen ob Kategorie gewählt wurde
                        if(!category_spinner.getSelectedItem().toString().equals("")){
                            // Kategorie aus Spinner ziehen
                            number.setCat_id_category(dbHelper.getCategoryIdByName(category_spinner.getSelectedItem().toString()));
                            numbers.add(number);
                            // ListView aktualisieren
                            setListViews();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(contact_create_activity.this, R.string.cont_category_choose, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Button cancel = dialog.findViewById(R.id.dialog_en_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        ImageButton add_email_but = findViewById(R.id.cont_detail_add_email);
        add_email_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vorgehen für Einfügen neuer Email wie bei Nummer
                final Dialog dialog = new Dialog(contact_create_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_add_email);
                Button save = dialog.findViewById(R.id.dialog_em_save);
                final Spinner category_spinner = dialog.findViewById(R.id.dialog_em_spinner);
                ArrayAdapter spinner_adapter = new ArrayAdapter(contact_create_activity.this, android.R.layout.simple_spinner_item, dbHelper.getEmailCategoryList());
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category_spinner.setAdapter(spinner_adapter);
                final EditText email_text = dialog.findViewById(R.id.dialog_em_text);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Emails email = new Emails();
                        email.setEmail_adress(email_text.getText().toString());
                        if(!category_spinner.getSelectedItem().toString().equals("")){
                            email.setCat_id_category(dbHelper.getEmailCategoryIdByName(category_spinner.getSelectedItem().toString()));
                            emails.add(email);
                            setListViews();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(contact_create_activity.this, R.string.cont_category_choose, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Button cancel = dialog.findViewById(R.id.dialog_em_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kamera öffnen, um Profilbild aufzunehmen
                openCameraIntent();
            }
        });

        // Setzen der ListViews
        setListViews();
    }
    // Nummern aus Liste in Datenbank schreiben
    public List<Integer> getNumberList(){
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < numbers.size(); i++){
            int id = (int) dbHelper.insertNumber(numbers.get(i));
            result.add(id);
        }
        return result;
    }
    // Emails aus Liste in Datenbank schreiben
    public List<Integer> getEmailList(){
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < emails.size(); i++){
            int id = (int) dbHelper.insertEmail(emails.get(i));
            result.add(id);
        }
        return result;
    }
    // Höhe der ListView-Items richtig setzen
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    // Nummern als Hashliste für ListView
    private List<HashMap<String, String>> getNumberHashList(){
        List<HashMap<String, String>> result = new ArrayList<>();
        for(int i = 0; i < numbers.size(); i++){
            HashMap<String, String> hhash = new HashMap<>();
            Numbers number = numbers.get(i);
            hhash.put("number", String.valueOf(number.getNumber()));
            hhash.put("category", dbHelper.getCategoryNameById(number.getCat_id_category()));
            result.add(hhash);
        }
        return result;
    }
    // Emails als Hashliste für ListView
    private List<HashMap<String, String>> getEmailHashList(){
        List<HashMap<String, String>> result = new ArrayList<>();
        for(int i = 0; i < emails.size(); i++){
            HashMap<String, String> hhash = new HashMap<>();
            Emails email = emails.get(i);
            hhash.put("email", String.valueOf(email.getEmail_adress()));
            hhash.put("category", dbHelper.getEmailCategoryNameById(email.getCat_id_category()));
            result.add(hhash);
        }
        return result;
    }
    // Aktuslisierung und setzen der ListViews
    private void setListViews(){
        // SimpleAdapter mit überschriebener getView()-Methode für Anruf Button
        ListAdapter number_adapter = new SimpleAdapter( contact_create_activity.this, getNumberHashList(), R.layout.list_item_number_det, new String[] { "id", "number", "category"}, new int[] {R.id.listitem_number_id, R.id.listitem_number_number, R.id.listitem_number_category});
        // LonClickListener für ListItem zum Löschen
        numberlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(contact_create_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_delete);
                Button delete = dialog.findViewById(R.id.dialog_delete_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(filled){
                            Numbers number = numbers.get(position);
                            dbHelper.deleteNumber(number.getId());
                        }
                        numbers.remove(position);
                        setListViews();
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
                return false;
            }
        });
        // Gleiche für Emails wie bei Nummern
        ListAdapter email_adapter = new SimpleAdapter( contact_create_activity.this, getEmailHashList(), R.layout.list_item_email_det, new String[] { "id", "email", "category"}, new int[] {R.id.listitem_email_id, R.id.listitem_email_email, R.id.listitem_email_category});
        emaillist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(contact_create_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_delete);
                Button delete = dialog.findViewById(R.id.dialog_delete_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(filled){
                            Emails email = emails.get(position);
                            dbHelper.deleteEmail(email.getId());
                        }
                        emails.remove(position);
                        dialog.dismiss();
                        setListViews();
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
                return false;
            }
        });
        numberlist.setAdapter(number_adapter);
        emaillist.setAdapter(email_adapter);
        setListViewHeightBasedOnChildren(numberlist);
        setListViewHeightBasedOnChildren(emaillist);
    }
    // Falls vorhandener Kontakt aufgerufen wurde, dann werden Daten von diesem gefüllt
    private void fillData(int id){
        fillCont = dbHelper.getContactById(id);
        cont_name.setText(fillCont.getName());
        numbers = dbHelper.getNumberListByIdList(fillCont.getCat_id_numbers());
        emails = dbHelper.getEmailListByIdList(fillCont.getCat_id_emails());
        image = DbBitmapUtility.getImage(dbHelper.getProfPicById(fillCont.getCat_id_pb()));
        if(image != null){
            imgBut.setImageBitmap(image);
        } else {
            imgBut.setImageDrawable(ContextCompat.getDrawable(contact_create_activity.this, R.drawable.ic_contact));
        }
        if(fillCont.isIsfavourite()){
            favourite = true;
            switchfavourites();
        } else{
            favourite = false;
            switchfavourites();
        }
    }
    // falls Kontakt mit QR eingegeben
    private void fillData(Contact fillCont){
        cont_name.setText(fillCont.getName());
        numbers = dbHelper.getNumberListByIdList(fillCont.getCat_id_numbers());
        emails = dbHelper.getEmailListByIdList(fillCont.getCat_id_emails());
        image = DbBitmapUtility.getImage(dbHelper.getProfPicById(fillCont.getCat_id_pb()));
        if(image != null){
            imgBut.setImageBitmap(image);
        } else {
            imgBut.setImageDrawable(ContextCompat.getDrawable(contact_create_activity.this, R.drawable.ic_contact));
        }
        if(fillCont.isIsfavourite()){
            favourite = true;
            switchfavourites();
        } else{
            favourite = false;
            switchfavourites();
        }
    }
    // QR-Code String generieren
    private String generateQR(){
        // Beginn mit "cont" für späteres Filtern
        String result = "cont#";
        // Verschiedene Kontaktteile werden mit "#" getrennt und Ende mit "end" für Filter
        result += cont_name.getText().toString() + "#" + getNumbersForQR(numbers) + "#" + getEmailsForQR(emails) + "#" + fillCont.isIsfavourite() + "#end";
        return result;
    }
    // Nummern für QR-Code
    private String getNumbersForQR(List<Numbers> numlist){
        String result = "";
        boolean first = true;
        // Nummerliste wird in Schleife durchlaufen
        for(int i = 0; i < numlist.size(); i++){
            // Hilfsnummer
            Numbers hnum = numlist.get(i);
            // Falls erster durchlauf wird Trennzeichen weggelassen
            if(first){
                first = false;
                result += hnum.getNumber() + "_" + dbHelper.getCategoryNameById(hnum.getCat_id_category());
            } else {
                result += "&" + hnum.getNumber() + "_" + dbHelper.getCategoryNameById(hnum.getCat_id_category());
            }
        }
        return result;
    }
    // Emails für QR-Code
    private String getEmailsForQR(List<Emails> emaList){
        // Vorgehen wie bei Nummern
        String result = "";
        boolean first = true;
        for(int i = 0; i < emaList.size(); i++){
            Emails hmail = emaList.get(i);
            if(first){
                first = false;
                result += hmail.getEmail_adress() + "_" + dbHelper.getEmailCategoryNameById(hmail.getCat_id_category());
            } else {
                result += "&" + hmail.getEmail_adress() + "_" + dbHelper.getEmailCategoryNameById(hmail.getCat_id_category());
            }
        }
        return result;
    }
    // Öffnen der Kamera für Profilbild
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
        }
    }
    // Auslesen des Kamera Ergebnisses um Profilbild zu bekommen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Falls Ergebnis vorliegt und ein Bild ist
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            // Bild != null
            if (data != null && data.getExtras() != null) {
                // Bitmap speichern und in ImageView einfügen
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imgBut.setImageBitmap(imageBitmap);
                image = imageBitmap;
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // Überprüfen ob Scanner ein Ergebnis liefert
            if(result != null) {
                // Testen ob Ergenis == null
                if(result.getContents() == null) {
                    Toast.makeText(this, R.string.main_scan_cancelled, Toast.LENGTH_LONG).show();
                } else {
                    Contact cont = getContactByString(result.getContents());
                    if(cont != null){
                        fillData(cont);
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    // Verarbeitung des Scannerergebnisses
    private Contact getContactByString(String qr){
        Contact cont = new Contact();
        // QR-Code liefert String zurück. Bei Erstellung des QR-Codes wurde dieser mit "#" getrennt
        // String wird in String-Array getrennt
        String[] split = qr.split("#");
        // Testen ob es sich um einen Kontakt handelt (Formatierung mit "cont" am Anfang und "end" am Ende
        if(split[0].equals("cont") && split[5].equals("end")){
            // Kontaktname wird aus Array gelesen
            cont.setName(split[1]);
            // Nummern aus Array lesen
            String nums = split[2];
            // Da Nummern mit Kategorie in QR-Code müssen diese auch nochmals getrennt werden
            List<Integer> numcats = new ArrayList<>();
            // Abfrage ob eine oder mehrere Nummern, da mehrere Nummern durch "&" getrennt
            if(nums.contains("&")){
                // Weitere Trennung des Strings aus Array
                String[] numsplit = nums.split("&");
                for (int i = 0; i < numsplit.length; i++){
                    // Auslesen einzelner Nummer als Array
                    String[] numhelp = numsplit[i].split("_");
                    // Hilfsnummer-Objekt anlegen und füllen mit Nummer und passender Kategorie
                    Numbers hnum = new Numbers();
                    hnum.setNumber(numhelp[0]);
                    // Überprüfen ob Kategorie schon in Datenbank vorhanden und optionales Erstellen der Kategorie
                    hnum.setCat_id_category((int) dbHelper.checkInsCategory(numhelp[1]));
                    // Einfügen der Nummer in Datenbank und Liste für späteren Kontakt
                    numcats.add((int)dbHelper.insertNumber(hnum));
                }
            } else {
                if(nums.contains("_")){
                    // Gleiches vorgehen wie oben nur bei einzelner Nummer
                    String[] numhelp = nums.split("_");
                    Numbers hnum = new Numbers();
                    hnum.setNumber(numhelp[0]);
                    hnum.setCat_id_category((int) dbHelper.checkInsCategory(numhelp[1]));
                    numcats.add((int)dbHelper.insertNumber(hnum));
                }
            }
            // Nummerliste in späteren Kontakt einfügen
            cont.setCat_id_numbers(numcats);
            //Trennung Mails
            // Vorgehen wie bei Nummern
            String mails = split[3];
            List<Integer> mailcats = new ArrayList<>();
            if(mails.contains("&")){
                String[] mailsplit = mails.split("&");
                for (int i = 0; i < mailsplit.length; i++){
                    String[] mailhelp = mailsplit[i].split("_");
                    Emails hmail = new Emails();
                    hmail.setEmail_adress(mailhelp[0]);
                    hmail.setCat_id_category((int) dbHelper.checkInsEmailCategory(mailhelp[1]));
                    mailcats.add((int)dbHelper.insertEmail(hmail));
                }
            } else {
                if(mails.contains("_")){
                    String[] mailhelp = mails.split("_");
                    Emails hmail = new Emails();
                    hmail.setEmail_adress(mailhelp[0]);
                    hmail.setCat_id_category((int) dbHelper.checkInsEmailCategory(mailhelp[1]));
                    mailcats.add((int)dbHelper.insertEmail(hmail));
                }
            }
            cont.setCat_id_emails(mailcats);
            // Trennung Favourite
            // Abfrage ob Kontakt als Favorit gesetzt wurde -> Auch setzen
            if(split[4].equals("true")){
                cont.setIsfavourite(true);
            } else {
                cont.setIsfavourite(false);
            }
            // Neuer Kontakt wird zurück gegeben
            return cont;
        } else {
            return null;
        }
    }

    // OnBackPressed Override für Editing
    @Override
    public void onBackPressed() {
        if (filled){
            Intent intent = new Intent(contact_create_activity.this, contact_view_activity.class);
            intent.putExtra("item_id", fillCont.getId());
            contact_create_activity.this.startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
    // Override Actionbar Back Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
    private void switchfavourites(){
        if(favourite){
            isfav.setVisibility(View.VISIBLE);
            isntfav.setVisibility(View.GONE);
        } else {
            isfav.setVisibility(View.GONE);
            isntfav.setVisibility(View.VISIBLE);
        }
    }
}