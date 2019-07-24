package de.hskl.contacts;

import android.app.Dialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import de.hskl.contacts.Helper.DBHelper;

public class settings_email_category_activity extends AppCompatActivity {
    // Klassenvariablen zur Verwendung in mehreren Methoden
    private DBHelper dbHelper;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_email_category_activity);

        // Vorbereitung Datenbankhilfsklasse
        dbHelper = new DBHelper(this);

        // Views initialisieren
        lv = findViewById(R.id.settings_email_category_list);
        FloatingActionButton addEmailCategory = findViewById(R.id.settings_email_category_but);
        addEmailCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(settings_email_category_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_add_category);
                Button save = dialog.findViewById(R.id.dialog_cat_save);
                final EditText category_text = dialog.findViewById(R.id.dialog_cat_text);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.insertEmailCategory(category_text.getText().toString());
                        setListViews();
                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.dialog_cat_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        // ListViews füllen
        setListViews();
    }

    // Methode zur Aktualisierung der ListViews bei neuen Datensätzen
    private void setListViews(){
        // Adapter: Verbindung Liste mit Datensätzen und ListView
        ListAdapter adapter = new SimpleAdapter( settings_email_category_activity.this, dbHelper.getEmailCategoryHashList(), R.layout.list_item_category, new String[] { "id", "name"}, new int[] {R.id.listitem_category_id, R.id.listitem_category_name});
        // LongClickListener für Listeneinträge
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView titemid = view.findViewById(R.id.listitem_category_id);
                final int itemid = Integer.parseInt(titemid.getText().toString());
                // Dialog zum Löschen eines Eintrags
                final Dialog dialog = new Dialog(settings_email_category_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_delete);
                Button delete = dialog.findViewById(R.id.dialog_delete_delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean check = dbHelper.deleteEmailCategory(itemid);
                        if(check){
                            setListViews();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(settings_email_category_activity.this, "Kategorie konnte nicht gelöscht werden! Sie wird noch verwendet.", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(settings_email_category_activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_add_category);
                Button save = dialog.findViewById(R.id.dialog_cat_save);
                final EditText category_text = dialog.findViewById(R.id.dialog_cat_text);
                TextView titemid = view.findViewById(R.id.listitem_category_id);
                final int itemid = Integer.parseInt(titemid.getText().toString());
                category_text.setText(dbHelper.getEmailCategoryNameById(itemid));
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.updateEmailCategory(itemid, category_text.getText().toString());
                        setListViews();
                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.dialog_cat_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        lv.setAdapter(adapter);
        setListViewHeightBasedOnChildren(lv);
    }

    // Utility Klasse zur richtigen Darstellung von ListViews
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
}
