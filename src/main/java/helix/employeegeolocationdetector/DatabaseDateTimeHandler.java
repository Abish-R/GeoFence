package helix.employeegeolocationdetector;

/**
 * Created by HelixTech-Admin on 3/12/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDateTimeHandler extends SQLiteOpenHelper {
    /** All Static variables*/
    private static final int DATABASE_VERSION = 1; 				  // Database Version
    private static final String DATABASE_NAME = "geolocationdetector";  	   	  // Database Name
    private static final String TABLE_DOCTOR = "dr_table";  // Contacts table name
    private static final String TABLE_REP = "rep_table";  // Contacts table name

    /** Contacts Table Columns names*/
    private static final String DOCTOR_SNO = "dr_sno";
    private static final String DOCTOR_ID = "dr_id";
    private static final String DOCTOR_NAME = "dr_name";
    private static final String DOCTOR_PLACE = "dr_place";
    private static final String DOCTOR_LAT = "dr_lat";
    private static final String DOCTOR_LON = "dr_lon";
    private static final String DOCTOR_RADIUS = "dr_radius";
    private static final String DOCTOR_CREATED_DATE = "dr_crtd_dt";

    private static final String REP_SNO = "rep_sno";
    private static final String REP_ID = "rep_id";
    private static final String REP_TIME_OF_VISIT = "rep_time_of_visit";//changed
    private static final String REP_VISITED_DR_ID = "rep_visited_dr_id";//changed
    private static final String REP_LAT = "rep_lat";
    private static final String REP_LON = "rep_lon";//changed
    private static final String REP_LOCATION = "rep_location";
    private static final String REP_UPSTATE = "rep_up_status";

    public DatabaseDateTimeHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Creating Tables*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_DOCTOR + "("+ DOCTOR_SNO + " INTEGER PRIMARY KEY autoincrement not null,"
                + DOCTOR_ID + " TEXT,"+ DOCTOR_NAME + " TEXT,"+ DOCTOR_PLACE + " TEXT,"+ DOCTOR_LAT + " TEXT,"+
                DOCTOR_LON+ " TEXT," +DOCTOR_RADIUS+ " TEXT,"+DOCTOR_CREATED_DATE+ " DATETIME)";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLE1 = "CREATE TABLE " + TABLE_REP + "("+ REP_SNO + " INTEGER PRIMARY KEY autoincrement not null,"
                + REP_ID + " TEXT,"+ REP_TIME_OF_VISIT + " DATETIME,"+ REP_VISITED_DR_ID + " TEXT,"+ REP_LAT + " TEXT,"+
                REP_LON + " TEXT,"+ REP_LOCATION + " TEXT,"+ REP_UPSTATE + " TEXT)";
        db.execSQL(CREATE_TABLE1);
    }

    /** Upgrading database*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REP);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    /** Adding new data*/
    void addDoctorData(GetSetData atten) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(DOCTOR_SNO, atten.getDrId());
        values.put(DOCTOR_ID, atten.getDrId());                    // Get Rate
        values.put(DOCTOR_NAME, atten.getDrName());
        values.put(DOCTOR_PLACE, atten.getDrPlace());
        values.put(DOCTOR_LAT, atten.getDrLat());                    // Get Rate
        values.put(DOCTOR_LON, atten.getDrLon());
        values.put(DOCTOR_RADIUS,atten.getDrRadius());				// Get Created date
        //values.put(DOCTOR_CREATED_DATE,atten.getDrCreatedDate());

        db.insert(TABLE_DOCTOR, null, values);
        /** Inserting Row*/
//        long temp=0;
//        if(checkInTimePunchedOrNot(atten.getAttandenceDate()) == 0)
//            temp = db.insert(TABLE_DOCTOR, null, values);
//        else
//            temp = 0;
//        Log.d(values.getAsString(DOCTOR_ID), "Done");
        db.close(); // Closing database connection
//        return temp;
    }

    /** Adding new rate*/
    void addRepData(GetSetData atten) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(REP_SNO, atten.getRepId());
        values.put(REP_ID, atten.getRepId());                    // Get Rate
        values.put(REP_TIME_OF_VISIT, atten.getRepVisitedTime());
        values.put(REP_VISITED_DR_ID, atten.getRepVisitedDrId());
        values.put(REP_LAT, atten.getRepLat());                    // Get Rate
        values.put(REP_LON, atten.getRepLon());
        values.put(REP_LOCATION, atten.getRepLocation());                // Get Created date
        values.put(REP_UPSTATE, atten.getRepUpState());

        db.insert(TABLE_REP, null, values);
        db.close();
    }

    /** Update rate of a date */
    public String[] getDoctorLatLongRadius(String dr_id){
        String[] latlongrad=new String[3];
        String Query = "SELECT "+DOCTOR_LAT+","+DOCTOR_LON+","+DOCTOR_RADIUS+" FROM "+TABLE_DOCTOR+" WHERE "+DOCTOR_ID+"='"+dr_id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            latlongrad[0]=cursor.getString(0);
            latlongrad[1]=cursor.getString(1);
            latlongrad[2]=cursor.getString(2);
        }
        db.close();
        return latlongrad;
    }

    public List<GetSetData> getAllDoctorList(){
        List<GetSetData> list = new ArrayList<GetSetData>();
        String Query = "SELECT * FROM "+TABLE_DOCTOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                GetSetData values = new GetSetData();
                values.setDrId(cursor.getString(1));
                values.setDrName(cursor.getString(2));
                values.setDrPlace(cursor.getString(3));
                list.add(values);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return list;
    }

    public List<GetSetData> getDataNotUploaded(){
        List<GetSetData> data_list = new ArrayList<GetSetData>();
        String Query = "SELECT * FROM " + TABLE_REP +" WHERE "+REP_UPSTATE +"='N'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                GetSetData values = new GetSetData();
                values.setRepSNo(cursor.getString(0));
                values.setRepId(cursor.getString(1));
                values.setRepVisitedTime(cursor.getString(2));
                values.setRepVisitedDrId(cursor.getString(3));
                values.setRepLat(cursor.getString(4));
                values.setRepLon(cursor.getString(5));
                values.setRepLocation(cursor.getString(6));
                values.setRepUpState(cursor.getString(7));
//                values.setOutPicLocation(cursor.getString(8));
                data_list.add(values);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return data_list;
    }

    void deleteAfterUpload(String s_no){
        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(REP_UPSTATE, "U");
//        int temp = db.update(TABLE_REP, values, REP_ID + " = ? AND "+REP_VISITED_DR_ID+" =?", new String[]{rep_id,dr_id});

//        String Query1 = "SELECT * FROM " + TABLE_REP;
//        Cursor cursor1 = db.rawQuery(Query1, null);
//        int temp = cursor1.getCount();
        long dd=db.delete(TABLE_REP, REP_SNO + " = ? AND "+REP_UPSTATE+" =?", new String[]{s_no,"U"});
        ContentValues values = new ContentValues();
        values.put(REP_UPSTATE, "U");
        int temp = db.update(TABLE_REP, values, REP_SNO + " = ?", new String[]{s_no});
        db.close();

    }

    int getNotUploadCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TABLE_REP+" WHERE "+REP_UPSTATE +"='N'";
        Cursor cursor = db.rawQuery(Query, null);
        int temp1 = cursor.getCount();
        db.close();
        return temp1;
    }


    int getCountOfRow(){
        String Query = "SELECT * FROM " + TABLE_DOCTOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        int temp=cursor.getCount();
        return temp;
    }
}
