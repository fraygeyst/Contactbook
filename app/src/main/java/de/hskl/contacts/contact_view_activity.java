package de.hskl.contacts;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hskl.contacts.Database_Structure.Contact;
import de.hskl.contacts.Database_Structure.Emails;
import de.hskl.contacts.Database_Structure.Numbers;
import de.hskl.contacts.Helper.DBHelper;
import de.hskl.contacts.Helper.DbBitmapUtility;

public class contact_view_activity extends AppCompatActivity {

    // Klassenvariablen für Nutzung in mehreren Methoden
    private DBHelper dbHelper;
    private TextView cont_name;
    private List<Numbers> numbers;
    private List<Emails> emails;
    private ListView numberlist;
    private ListView emaillist;
    private Contact fillCont;
    private Bitmap image = null;
    private TextView tool_title;
    private ImageButton tool_isfav;
    private ImageButton tool_isntfav;
    private Boolean favourite;
    private de.hdodenhof.circleimageview.CircleImageView imgBut;
    private static final int REQUEST_CAPTURE_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view_activity);

        // Custom Actionbar initialsieren
        final View actionBarLayout = getLayoutInflater().inflate(R.layout.contact_view_actionbar,null);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout, new androidx.appcompat.app.ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Actionbar initialisieren und Listener setzen
        tool_isfav = findViewById(R.id.contview_toolbar_isfav);
        tool_isntfav = findViewById(R.id.contview_toolbar_isntfav);
        tool_title = findViewById(R.id.contview_toolbar_title);
        ImageButton tool_edit = findViewById(R.id.contview_toolbar_edit);
        tool_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contact_view_activity.this, contact_create_activity.class);
                intent.putExtra("item_id", fillCont.getId());
                contact_view_activity.this.startActivity(intent);
            }
        });
        ImageButton tool_qr = findViewById(R.id.contview_toolbar_qr);
        tool_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Text für QR-Code holen
                String text = generateQR();
                // Erstellen des Codes vorbereiten
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    // Darstellung des QR-Codes in Dialogfenster
                    final Dialog dialog = new Dialog(contact_view_activity.this);
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
        ImageButton tool_delete = findViewById(R.id.contview_toolbar_delete);
        tool_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(contact_view_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_delete);
                Button delete = dialog.findViewById(R.id.dialog_delete_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.deleteContact(fillCont.getId());
                        Intent intent = new Intent(contact_view_activity.this, MainActivity.class);
                        contact_view_activity.this.startActivity(intent);
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
            }
        });
        tool_isfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.updateFavourite(fillCont.getId(),false);
                favourite = false;
                fillCont = dbHelper.getContactById(fillCont.getId());
                setfavourite();
            }
        });
        tool_isntfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.updateFavourite(fillCont.getId(),true);
                favourite = true;
                fillCont = dbHelper.getContactById(fillCont.getId());
                setfavourite();
            }
        });

        // Initalisieren von Klassenvariablen
        numbers = new ArrayList<>();
        emails = new ArrayList<>();

        // Vorbereiten der Datenbankhilfsklasse
        dbHelper = new DBHelper(this);

        // Views initialisieren
        cont_name = findViewById(R.id.cont_view_name);
        numberlist = findViewById(R.id.cont_view_number_list);
        emaillist = findViewById(R.id.cont_view_add_email_list);

        // Überprüfen ob Neuer Kontakt erstellt wird oder vorhandener Bearbeitet
        imgBut = findViewById(R.id.cont_view_avatar);
        imgBut.setImageDrawable(ContextCompat.getDrawable(contact_view_activity.this, R.drawable.ic_contact));

        // Daten aus Intent lesen
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("item_id");
        fillData(id);

        // Setzen der ListViews
        setListViews();
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
        ListAdapter number_adapter = new SimpleAdapter( contact_view_activity.this, getNumberHashList(), R.layout.list_item_number, new String[] { "id", "number", "category"}, new int[] {R.id.listitem_number_id, R.id.listitem_number_number, R.id.listitem_number_category}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);
                final TextView numberview = itemView.findViewById(R.id.listitem_number_number);
                ImageButton callbut = itemView.findViewById(R.id.listitem_number_call);
                // Aufruf des Telefons bei Klick
                callbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:"+numberview.getText().toString()));
                        // Überprüfen ob Berechtigungen vorhanden
                        if (ContextCompat.checkSelfPermission(contact_view_activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(contact_view_activity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                        } else {
                            startActivity(phoneIntent);
                        }
                    }
                });
                return itemView;
            }
        };
        // Gleiche für Emails wie bei Nummern
        ListAdapter email_adapter = new SimpleAdapter( contact_view_activity.this, getEmailHashList(), R.layout.list_item_email, new String[] { "id", "email", "category"}, new int[] {R.id.listitem_email_id, R.id.listitem_email_email, R.id.listitem_email_category}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);
                final TextView emailview = itemView.findViewById(R.id.listitem_email_email);
                ImageButton callbut = itemView.findViewById(R.id.listitem_email_call);
                callbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:"+emailview.getText().toString()));
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.cont_email_send)));
                    }
                });
                return itemView;
            }
        };
        numberlist.setAdapter(number_adapter);
        emaillist.setAdapter(email_adapter);
        setListViewHeightBasedOnChildren(numberlist);
        setListViewHeightBasedOnChildren(emaillist);
    }
    // Falls vorhandener Kontakt aufgerufen wurde, dann werden Daten von diesem gefüllt
    private void fillData(int id){
        fillCont = dbHelper.getContactById(id);
        cont_name.setText(fillCont.getName());
        tool_title.setText(fillCont.getName());
        numbers = dbHelper.getNumberListByIdList(fillCont.getCat_id_numbers());
        emails = dbHelper.getEmailListByIdList(fillCont.getCat_id_emails());
        image = DbBitmapUtility.getImage(dbHelper.getProfPicById(fillCont.getCat_id_pb()));
        if(image != null){
            imgBut.setImageBitmap(image);
        } else {
            imgBut.setImageDrawable(ContextCompat.getDrawable(contact_view_activity.this, R.drawable.ic_contact));
        }
        favourite = fillCont.isIsfavourite();
        setfavourite();
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
    // Toolbar Favorit aktualisieren
    private void setfavourite(){
        if(favourite){
            tool_isfav.setVisibility(View.VISIBLE);
            tool_isntfav.setVisibility(View.GONE);
        } else {
            tool_isfav.setVisibility(View.GONE);
            tool_isntfav.setVisibility(View.VISIBLE);
        }
    }
}