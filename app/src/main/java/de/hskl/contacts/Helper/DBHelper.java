package de.hskl.contacts.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import de.hskl.contacts.Database_Structure.Contact;
import de.hskl.contacts.Database_Structure.Emails;
import de.hskl.contacts.Database_Structure.Numbers;
import de.hskl.contacts.Database_Structure.ProfPics;

// Datenbankhilfsklasse
public class DBHelper extends SQLiteOpenHelper {

    // Allgemeine Datenbankinfos
    // Datenbankversion
    private static final int DATABASE_VERSION = 2;

    // Datenbankname
    private static final String DATABASE_NAME = "Kontakte";

    // Tabellennamen
    private static final String TABLE_CONCTACT = "Contact";
    private static final String TABLE_PROFPICS = "Profilepics";
    private static final String TABLE_NUMBERS = "Nummern";
    private static final String TABLE_EMAILS = "Emails";
    private static final String TABLE_CATEGORY = "Kategorie";
    private static final String TABLE_EMAIL_CATEGORY = "EmailKategorie";

    // Spaltennamen
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name_vorname";
    private static final String KEY_CAT_ID_PB = "cat_id_profilepic";
    private static final String KEY_CAT_ID_NUMBERS = "cat_id_numbers";
    private static final String KEY_CAT_ID_EMAILS = "cat_id_emails";
    private static final String KEY_FAVOURITE = "favourite";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NUMBER = "nummer";
    private static final String KEY_CAT_ID_CATEGORY = "cat_id_category";
    private static final String KEY_EMAIL_ADRESS = "emailAdresse";

    //Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Tabellen Create-Strings
    // Contact Create
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONCTACT + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_CAT_ID_PB + " INTEGER," + KEY_CAT_ID_NUMBERS
            + " TEXT," + KEY_CAT_ID_EMAILS + " TEXT," + KEY_FAVOURITE + " INTEGER" + ")";
    // ProfPic Create
    private static final String CREATE_TABLE_PROFPIC = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PROFPICS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_IMAGE
            + " BLOB" + ")";
    // Numbers Create
    private static final String CREATE_TABLE_NUMBERS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NUMBERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER
            + " TEXT," + KEY_CAT_ID_CATEGORY + " TEXT" + ")";
    // Emails Create
    private static final String CREATE_TABLE_EMAILS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EMAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EMAIL_ADRESS
            + " TEXT," + KEY_CAT_ID_CATEGORY + " TEXT" + ")";
    // Category Create
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CATEGORY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT" + ")";
    // Category Create
    private static final String CREATE_TABLE_EMAIL_CATEGORY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EMAIL_CATEGORY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT" + ")";

    // Datenbank-Create
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_PROFPIC);
        db.execSQL(CREATE_TABLE_NUMBERS);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_EMAIL_CATEGORY);
        db.execSQL(CREATE_TABLE_EMAILS);
    }

    // Datenbankupdate bei Erhöhung der Datenbankversion
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONCTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFPICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL_CATEGORY);
        onCreate(db);
    }

    // Helper

    // Ausgabe eines Strings mit "#"-Trennung als String-Liste
    private List<Integer> StringToList(String iString){
        List<String> iList = new ArrayList<>(Arrays.asList(iString.split("#")));
        List<Integer> oList = new ArrayList<>();
        for(int i = 0; i < iList.size(); i++){
            oList.set(i, oList.get(i));
        }
        return oList;
    }

    // Inserts
    public long insertContact(Contact icont){
        // Vorbereitung der Datenbank
        SQLiteDatabase db = getWritableDatabase();
        // Erstellung einer Hilfsvariablen um Daten in Datenbank zu schieben
        ContentValues cv = new ContentValues();
        // Füllen der Hilfsvariable
        cv.put(DBHelper.KEY_NAME, icont.getName());
        cv.put(DBHelper.KEY_CAT_ID_PB, icont.getCat_id_pb());
        cv.put(DBHelper.KEY_CAT_ID_NUMBERS, icont.getCat_id_numbers_toString());
        cv.put(DBHelper.KEY_CAT_ID_EMAILS, icont.getCat_id_emails_toString());
        cv.put(DBHelper.KEY_CAT_ID_PB, icont.getCat_id_pb());
        cv.put(DBHelper.KEY_FAVOURITE, icont.isIsfavourite());
        // Schreiben der Hilfsvariable in die Datenbank
        long check = db.insert(DBHelper.TABLE_CONCTACT, null, cv);
        db.close();
        return check;
    }
    public long insertProfPic(ProfPics ipic){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_IMAGE, ipic.getImage());
        long check = db.insert(TABLE_PROFPICS, null, cv);
        db.close();
        return check;
    }
    public long insertNumber(Numbers inum){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NUMBER, inum.getNumber());
        cv.put(DBHelper.KEY_CAT_ID_CATEGORY, inum.getCat_id_category());
        long check = db.insert(TABLE_NUMBERS, null, cv);
        db.close();
        return check;
    }
    public long insertCategory(String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NAME, name);
        long check = db.insert(TABLE_CATEGORY, null, cv);
        db.close();
        return check;
    }
    public long insertEmailCategory(String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NAME, name);
        long check = db.insert(TABLE_EMAIL_CATEGORY, null, cv);
        db.close();
        return check;
    }
    public long updateCategory(int id, String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NAME, name);
        long check = db.update(DBHelper.TABLE_CATEGORY, cv, KEY_ID + "= ?", new String[]{String.valueOf(id)});
        db.close();
        return check;
    }
    public long updateEmailCategory(int id, String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NAME, name);
        long check = db.update(DBHelper.TABLE_EMAIL_CATEGORY, cv, KEY_ID + "= ?", new String[]{String.valueOf(id)});        db.close();
        return check;
    }
    public long insertEmail(Emails iemail){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_EMAIL_ADRESS, iemail.getEmail_adress());
        cv.put(DBHelper.KEY_CAT_ID_CATEGORY, iemail.getCat_id_category());
        long check = db.insert(TABLE_EMAILS, null, cv);
        db.close();
        return check;
    }

    // Getter
    private List<Contact> getContactList(){
        // Vorbereiten der Datenbank
        SQLiteDatabase db = getReadableDatabase();
        // SQL-Select für Ausführung in Datenbank
        String query = "SELECT * FROM " + TABLE_CONCTACT;
        // Erstellen der Ausgabeliste
        List<Contact> oCont = new ArrayList<>();
        // Cursor um durch die Datenbank zu springen
        Cursor cursor = db.rawQuery(query, null);
        // Falls Datensätze in der Datenbank vorhanden sind
        if(cursor.moveToFirst()){
            do {
                Contact hcont = new Contact();
                hcont.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                hcont.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                hcont.setCat_id_pb(cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID_PB)));
                hcont.setCat_id_numbers(StringToList(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID_NUMBERS))));
                hcont.setCat_id_pb(cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID_PB)));
                hcont.setCat_id_emails(StringToList(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID_EMAILS))));
                if(cursor.getInt(cursor.getColumnIndex(KEY_FAVOURITE)) == 1){
                    hcont.setIsfavourite(true);
                } else {
                    hcont.setIsfavourite(false);
                }
                oCont.add(hcont);
            } while(cursor.moveToNext());   // Solange, bis alle ausgelesenen Datensätze durchgegangen wurden
        }
        // Datenbank und Cursor für nächste Verwendung schließen
        cursor.close();
        db.close();
        return oCont;
    }
    // ArrayListen zum Schreiben von Daten in ListViews
    public ArrayList<HashMap<String, String>> getContactArrayList(){
        List<Contact> iCont = getContactList();
        ArrayList<HashMap<String, String>> oCont = new ArrayList<>();
        for(int i = 0; i < iCont.size(); i++){
            Contact hcont = iCont.get(i);
            HashMap<String, String> hhash = new HashMap<>();
            hhash.put(DBHelper.KEY_ID, String.valueOf(hcont.getId()));
            hhash.put(DBHelper.KEY_NAME, String.valueOf(hcont.getName()));
            hhash.put(DBHelper.KEY_CAT_ID_PB, String.valueOf(hcont.getCat_id_pb()));
            hhash.put(DBHelper.KEY_CAT_ID_NUMBERS, hcont.getCat_id_numbers_toString());
            hhash.put(DBHelper.KEY_CAT_ID_EMAILS, hcont.getCat_id_emails_toString());
            hhash.put(DBHelper.KEY_FAVOURITE, String.valueOf(hcont.isIsfavourite()));
            oCont.add(hhash);
        }
        return oCont;
    }
    public ArrayList<String> getCategoryList(){
        ArrayList<String> iGroup = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CATEGORY;
        Cursor cursor = db.rawQuery(select, null);
        ArrayList<String> oCat = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                iGroup.add(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            } while(cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return iGroup;
    }
    public ArrayList<String> getEmailCategoryList(){
        ArrayList<String> iGroup = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_EMAIL_CATEGORY;
        Cursor cursor = db.rawQuery(select, null);
        ArrayList<String> oCat = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                iGroup.add(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            } while(cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return iGroup;
    }
    public int getCategoryIdByName(String name){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME +" LIKE \"%" + name + "%\"";
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            int result = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            db.close();
            cursor.close();
            return result;
        } else {
            db.close();
            cursor.close();
            return 0;
        }
    }
    public int getEmailCategoryIdByName(String name){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_EMAIL_CATEGORY + " WHERE " + KEY_NAME +" LIKE \"%" + name + "%\"";
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            int result = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            db.close();
            cursor.close();
            return result;
        } else {
            db.close();
            cursor.close();
            return 0;
        }
    }
    public int getCategoryIdByNameDb(SQLiteDatabase db, Cursor cursor, String name){
        String select = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME +" LIKE \"%" + name + "%\"";
        cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            int result = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            return result;
        } else {
            return 0;
        }
    }
    public int getEmailCategoryIdByNameDb(SQLiteDatabase db, Cursor cursor, String name){
        String select = "SELECT * FROM " + TABLE_EMAIL_CATEGORY + " WHERE " + KEY_NAME +" LIKE \"%" + name + "%\"";
        cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            int result = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            return result;
        } else {
            return 0;
        }
    }
    public String getCategoryNameById(int id){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            String result = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            db.close();
            cursor.close();
            return result;
        } else {
            db.close();
            cursor.close();
            return "";
        }
    }
    public String getEmailCategoryNameById(int id){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_EMAIL_CATEGORY + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            String result = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            db.close();
            cursor.close();
            return result;
        } else {
            db.close();
            cursor.close();
            return "";
        }
    }
    public List<HashMap<String, String>> getCategoryHashList(){
        List<HashMap<String, String>> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CATEGORY;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do {
                HashMap<String, String> hhash = new HashMap<>();
                hhash.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                hhash.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hhash);
            } while(cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return result;
    }
    public List<HashMap<String, String>> getEmailCategoryHashList(){
        List<HashMap<String, String>> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_EMAIL_CATEGORY;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do {
                HashMap<String, String> hhash = new HashMap<>();
                hhash.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                hhash.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hhash);
            } while(cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return result;
    }
    public List<HashMap<String, String>> getContactHashListshort(){
        List<HashMap<String,String>> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CONCTACT + " WHERE " + KEY_FAVOURITE + " = " + "1" + " ORDER BY " + KEY_NAME + " ASC";
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do {
                HashMap<String, String> hhash = new HashMap<>();
                hhash.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                hhash.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hhash);
            } while(cursor.moveToNext());
        }
        select = "SELECT * FROM " + TABLE_CONCTACT + " WHERE " + KEY_FAVOURITE + " = " + "0" + " ORDER BY " + KEY_NAME + " ASC";
        cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do {
                HashMap<String, String> hhash = new HashMap<>();
                hhash.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                hhash.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hhash);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }
    public List<HashMap<String, String>> getContactHashListshort(String contname){
        List<HashMap<String,String>> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CONCTACT + " WHERE " + KEY_NAME + " LIKE '%" + contname + "%' ORDER BY " + KEY_NAME + " ASC";
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do {
                HashMap<String, String> hhash = new HashMap<>();
                hhash.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                hhash.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hhash);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }
    public Contact getContactById(int id){
        Contact cont = new Contact();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONCTACT + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            cont.setId(id);
            cont.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cont.setCat_id_numbers(getStringAsInt(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID_NUMBERS))));
            cont.setCat_id_emails(getStringAsInt(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID_EMAILS))));
            cont.setCat_id_pb(cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID_PB)));
            if(cursor.getInt(cursor.getColumnIndex(KEY_FAVOURITE)) == 1){
                cont.setIsfavourite(true);
            } else {
                cont.setIsfavourite(false);
            }
        }
        cursor.close();
        db.close();
        return cont;
    }
    public List<Numbers> getNumberListByIdList(List<Integer> num_ids){
        List<Numbers> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "";
        Cursor cursor = db.rawQuery(select, null);
        for(int i = 0; i < num_ids.size(); i++){
            select = "SELECT * FROM " + TABLE_NUMBERS + " WHERE " + KEY_ID + " = " + num_ids.get(i);
            cursor = db.rawQuery(select, null);
            if(cursor.moveToFirst()){
                Numbers hnumber = new Numbers();
                hnumber.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                hnumber.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
                hnumber.setCat_id_category(cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID_CATEGORY)));
                result.add(hnumber);
            }
        }
        cursor.close();
        db.close();
        return result;
    }
    public List<Emails> getEmailListByIdList(List<Integer> email_ids){
        List<Emails> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "";
        Cursor cursor = db.rawQuery(select, null);
        for(int i = 0; i < email_ids.size(); i++){
            select = "SELECT * FROM " + TABLE_EMAILS + " WHERE " + KEY_ID + " = " + email_ids.get(i);
            cursor = db.rawQuery(select, null);
            if(cursor.moveToFirst()){
                Emails hemail = new Emails();
                hemail.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                hemail.setEmail_adress(cursor.getString(cursor.getColumnIndex(KEY_EMAIL_ADRESS)));
                hemail.setCat_id_category(cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID_CATEGORY)));
                result.add(hemail);
            }
        }
        cursor.close();
        db.close();
        return result;
    }
    public byte[] getProfPicById(int id){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_PROFPICS + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            byte[] result = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
            cursor.close();
            db.close();
            return result;
        } else {
            return null;
        }
    }
    public Bitmap getProfPicByContactId(int contid){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CONCTACT + " WHERE " + KEY_ID + " = " + contid;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            int pbid = cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID_PB));
            return DbBitmapUtility.getImage(getProfPicById(pbid));
        } else {
            return null;
        }
    }

    //Deletes
    public boolean deleteEmailCategory(int id){
        // Vorbereiten der Datenbank
        SQLiteDatabase db = getReadableDatabase();
        // Schauen, ob Datensatz in Datenbank vorhanden ist
        String query = "SELECT * FROM " + TABLE_EMAILS + " WHERE " + KEY_CAT_ID_CATEGORY + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){ // Falls Datensatz nicht vorhanden ist -> Abbruch
            cursor.close();
            db.close();
            return false;
        } else {
            db = getWritableDatabase();
            // Änderung des Datenbankbefehls für Löschen
            query = "DELETE FROM " + TABLE_EMAIL_CATEGORY + " WHERE " + KEY_ID + " = " + id;
            // Ausführen innerhalb Datenbank
            db.execSQL(query);
            cursor.close();
            db.close();
            return true;
        }
    }
    public boolean deleteCategory(int id){
        // Vorbereiten der Datenbank
        SQLiteDatabase db = getReadableDatabase();
        // Schauen, ob Datensatz in Datenbank vorhanden ist
        String query = "SELECT * FROM " + TABLE_NUMBERS + " WHERE " + KEY_CAT_ID_CATEGORY + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){   // Falls Datensatz nicht vorhanden ist -> Abbruch
            cursor.close();
            db.close();
            return false;
        } else {
            db = getWritableDatabase();
            // Änderung des Datenbankbefehls für Löschen
            query = "DELETE FROM " + TABLE_CATEGORY + " WHERE " + KEY_ID + " = " + id;
            // Ausführen innerhalb Datenbank
            db.execSQL(query);
            cursor.close();
            db.close();
            return true;
        }
    }
    public void deleteContact(int id){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CONCTACT + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(select, null);
        Contact cont = new Contact();
        if(cursor.moveToFirst()){
            cont.setCat_id_emails(getStringAsInt(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID_EMAILS))));
            cont.setCat_id_numbers(getStringAsInt(cursor.getString(cursor.getColumnIndex(KEY_CAT_ID_NUMBERS))));
        }
        db = getWritableDatabase();
        deleteProfPic(db, cont.getCat_id_pb());
        for(int i = 0; i < cont.getCat_id_emails().size(); i++){
            deleteEmail(db, cont.getCat_id_emails().get(i));
        }
        for (int i = 0; i < cont.getCat_id_numbers().size(); i++){
            deleteNumber(db, cont.getCat_id_numbers().get(i));
        }
        String query = "DELETE FROM " + TABLE_CONCTACT + " WHERE " + KEY_ID + " = " + id;
        db.execSQL(query);
        cursor.close();
        db.close();
    }
    public void deleteNumber(SQLiteDatabase db, int id){
        String query = "DELETE FROM " + TABLE_NUMBERS + " WHERE " + KEY_ID + " = " + id;
        db.execSQL(query);
    }
    public void deleteEmail(SQLiteDatabase db, int id){
        String query = "DELETE FROM " + TABLE_EMAILS + " WHERE " + KEY_ID + " = " + id;
        db.execSQL(query);
    }
    public void deleteProfPic(SQLiteDatabase db, int id){
        String query = "DELETE FROM " + TABLE_PROFPICS + " WHERE " + KEY_ID + " = " + id;
        db.execSQL(query);
    }
    public void deleteNumber(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NUMBERS + " WHERE " + KEY_ID + " = " + id;
        db.execSQL(query);
        db.close();
    }
    public void deleteEmail(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_EMAILS + " WHERE " + KEY_ID + " = " + id;
        db.execSQL(query);
        db.close();
    }

    // Updates
    public long updateContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_NAME, contact.getName());
        cv.put(DBHelper.KEY_CAT_ID_PB, contact.getCat_id_pb());
        cv.put(DBHelper.KEY_CAT_ID_NUMBERS, contact.getCat_id_numbers_toString());
        cv.put(DBHelper.KEY_CAT_ID_EMAILS, contact.getCat_id_emails_toString());
        cv.put(DBHelper.KEY_CAT_ID_PB, contact.getCat_id_pb());
        cv.put(DBHelper.KEY_FAVOURITE, contact.isIsfavourite());
        long id = db.update(DBHelper.TABLE_CONCTACT, cv, KEY_ID + "= ?", new String[]{String.valueOf(contact.getId())});
        db.close();
        return id;
    }

    // Utility
    // String mit "#"-Trennung als Liste aus Integer Werten
    public List<Integer> getStringAsInt(String input){
        List<String> splitlist = Arrays.asList(input.split("\\s*#\\s*"));
        List<Integer> result = new ArrayList<>();
        if(!input.equals("")){
            for(int i = 0; i < splitlist.size(); i++){
                result.add(Integer.parseInt(splitlist.get(i)));
            }
        }
        return result;
    }
    // Überprüfen ob eine Kategorie schon in der Datenbank vorhanden ist
    public long checkInsCategory(String catname){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME + " = '" + catname + "'";
        Cursor cursor = db.rawQuery(select , null);
        if(cursor.moveToFirst()){
            long id = getCategoryIdByNameDb(db, cursor, catname);
            cursor.close();
            db.close();
            return id;
        } else {
            cursor.close();
            db.close();
            return insertCategory(catname);
        }
    }
    // Überprüfen ob eine Kategorie schon in der Datenbank vorhanden ist
    public long checkInsEmailCategory(String catname){
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_EMAIL_CATEGORY + " WHERE " + KEY_NAME + " = '" + catname + "'";
        Cursor cursor = db.rawQuery(select , null);
        if(cursor.moveToFirst()){
            long id = getEmailCategoryIdByNameDb(db, cursor, catname);
            cursor.close();
            db.close();
            return id;
        } else {
            cursor.close();
            db.close();
            return insertEmailCategory(catname);
        }
    }
    // Favourite Update um Favorit bei View zu setzen
    public void updateFavourite(int id, boolean fav){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEY_FAVOURITE, fav);
        db.update(DBHelper.TABLE_CONCTACT, cv, KEY_ID + "= ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void loadDefaults(){
        List<String> defaultcats = Arrays.asList("Mobil 1", "Mobil 2", "Arbeit 1", "Arbeit 2", "Fax");
        for(int i = 0; i < defaultcats.size(); i++){
            insertCategory(defaultcats.get(i));
        }
        List<String> defaultemailcats = Arrays.asList("Privat", "Arbeit", "Sonstige");
        for(int i = 0; i < defaultemailcats.size(); i++){
            insertEmailCategory(defaultemailcats.get(i));
        }
    }
}