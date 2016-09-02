package helix.employeegeolocationdetector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmployeeGeoLocationFinder extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    DatabaseDateTimeHandler dbdth = new DatabaseDateTimeHandler(this);
    CheckInternet ci = new CheckInternet(this);
    DoctorListAdapter dla;
    //GetDeviceID gdi = new GetDeviceID(this);
    Button cnfm,sync;
    Spinner dr_spinner;
    GPSTracker gps;
    double latitude, longitude;
    SimpleDateFormat sdf1;
    Calendar c,cc;
    ProgressDialog progressDialog;
    String selected_dr_id,gps_address;
    public ArrayList<GetSetData> dr_names = new ArrayList<GetSetData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geolocationdetector);
        initializeViews();
        c = Calendar.getInstance();
        if(dbdth.getCountOfRow() < 1)
            if(ci.isOnline())
                new AsyncSyncDownload(EmployeeGeoLocationFinder.this).execute("http://ridio.in/geofency/SyncDownloadData");
            else
                SingleButtonAlert("Internet","Check Internet Connection","Ok","");
        else
            successMethod();
    }

    @Override
    protected void onStart(){
        super.onStart();
        againAskingGPS();
    }

    @Override
    protected void onStop(){
        super.onStop();
        gps.stopUsingGPS();
    }

    private void initializeViews() {
        cnfm = (Button) findViewById(R.id.cnfm);
        sync = (Button) findViewById(R.id.sync);
        dr_spinner = (Spinner)findViewById(R.id.dr_spinner);

        cnfm.setOnClickListener(this);
        sync.setOnClickListener(this);
        dr_spinner.setOnItemSelectedListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cnfm:
                getAllDataAndSave();
                break;
            case R.id.sync:
                syncingProcess();
                break;
        }
    }

    private void againAskingGPS(){
        gps = new GPSTracker(EmployeeGeoLocationFinder.this);
        if(gps.canGetLocation()) {
            progressDisplayToLoadLatLong();
        }
        else
            gps.showSettingsAlert();
    }

    void progressBar(){
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("Searching Location. Please Wait...");
        progressDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    private void progressDisplayToLoadLatLong(){
        progressBar();
        gps.getLocation();
        Handler handler = new Handler();
        int temp = getLatLong();

        if(temp==0) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.d("Location", "Not Detected.!!");
                    int temp = getLatLong();
                    progressDialog.dismiss();
                    if (gps.getLocationMode(EmployeeGeoLocationFinder.this) != 3)
                        SingleButtonAlert("Attendance", "Change GPS mode settings to 'High Accuracy'", "Ok", "gps_mode");
                    else if (temp == 0) {
                        Toast.makeText(EmployeeGeoLocationFinder.this, "Location Not Received.", Toast.LENGTH_LONG).show();
                    }
                }
            }, 7000);
        }else
            progressDialog.dismiss();

    }

    private int getLatLong(){
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        if(latitude==0 && longitude==0) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public void successMethod(){
        setListData();
        changeButtonColor();
    }

    private void getAllDataAndSave(){
        String time_of_visit=getTimeFromMobile();
        if(time_of_visit.equals(""))
            SingleButtonAlert("Attendance", "You Disabled the auto time & auto time zone checkbox in Settings.", "Change", "auto_time");
        else if(getLatLong()==0)
            SingleButtonAlert("Attendance", "Location not detected.", "Ok","gps");
        else if(selected_dr_id.equals("0"))
            SingleButtonAlert("Attendance", "First Select Doctor from the List", "Ok","");
        else if(checkDoctorRepLocation()==0)
            SingleButtonAlert("Attendance", "You are far from the selected area.", "Ok","");
        else{
            gps_address = gps.getAddressLine(true);
            dbdth.addRepData(new GetSetData("","abcd12345",time_of_visit,selected_dr_id,latitude+"",longitude+"",gps_address,"N"));
            sync.setBackgroundResource(R.drawable.sync_btn_bg);
            sync.setEnabled(true);
            SingleButtonAlert("Attendance", "You are inside the selected Doctor's area", "Ok", "done");
            //new AsyncGetAddress().execute(time_of_visit);
            //gps_address= "Chennai";//gps.getAddressLine(ci.isOnline());
//            SingleButtonAlert("Attendance", "abcd12345\n" + time_of_visit + "\n" + selected_dr_id + "\n" + latitude + "\n" + longitude + "\n" + gps_address, "Ok", "done");
        }
    }

    private int checkDoctorRepLocation(){
        String[] latlonrad;
        latlonrad = dbdth.getDoctorLatLongRadius(selected_dr_id);

        Location rep_loc = new Location("Loc");
        Location dr_loc = new Location("Loc");

        rep_loc.setLatitude((latitude));
        rep_loc.setLongitude((longitude));
        dr_loc.setLatitude(Double.parseDouble(latlonrad[0]));
        dr_loc.setLongitude(Double.parseDouble(latlonrad[1]));

        float dr_rad = Float.parseFloat(latlonrad[2]);
        float distanceInMeters = rep_loc.distanceTo(dr_loc);

        if(dr_rad>=distanceInMeters)
            return 1;
        else
            return 0;
    }


    private String getTimeFromMobile(){
        int auto_time_check= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
        int auto_time_zone_check= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
        if(auto_time_check==1 && auto_time_zone_check==1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cc = Calendar.getInstance();
            return sdf.format(cc.getTime());
        }
        else
            return "";
    }

    public void setListData(){
        /** Now i have taken static values by loop.**/
        List<GetSetData> contacts = dbdth.getAllDoctorList();
        dr_names.clear();
        final GetSetData dr_nm = new GetSetData();
        dr_nm.setDrId("0");
        dr_nm.setDrName("Select Doctor");
        dr_nm.setDrPlace("");
        dr_names.add(dr_nm);
        for (GetSetData cn : contacts) {
            final GetSetData dr_nm1 = new GetSetData();
            dr_nm1.setDrId(cn.getDrId());
            dr_nm1.setDrName(cn.getDrName());
            dr_nm1.setDrPlace(cn.getDrPlace());
            dr_names.add(dr_nm1);
        }
        Resources res = getResources();
        dla = new DoctorListAdapter(this, R.layout.bike_spinner, dr_names,res);
        dr_spinner.setAdapter(dla);
    }

    public void addDoctorsDetailLocally(String dr_id,String dr_name,String dr_place,String dr_lat,
                                        String dr_lon,String dr_radius,String dr_crtd_dt){
        dbdth.addDoctorData(new GetSetData(dr_id, dr_name, dr_place, dr_lat, dr_lon, dr_radius));//dr_crtd_dt
    }

    private void syncingProcess(){
        List<GetSetData> rep_data=dbdth.getDataNotUploaded();
        String rep_sno,rep_id,rep_time_of_visit,rep_visited_dr_id,rep_lat,rep_lon,rep_location,rep_up_status;
        if(rep_data.size()<1)
            SingleButtonAlert("Attendance","No data to Sync.!","Ok","");
        else
            for (GetSetData gs : rep_data) {
                rep_sno = gs.getRepSNo();
                rep_id = gs.getRepId();
                rep_time_of_visit = gs.getRepVisitedTime();
                rep_visited_dr_id = gs.getRepVisitedDrId();
                rep_lat = gs.getRepLat();
                rep_lon = gs.getRepLon();
                if(gs.getRepLocation()==null)
                    rep_location="null";
                else
                    rep_location = gs.getRepLocation();
                rep_up_status = gs.getRepUpState();
                if(ci.isOnline())
                    new AsyncSyncUpload(EmployeeGeoLocationFinder.this).execute(rep_id,rep_time_of_visit,
                        rep_visited_dr_id,rep_lat,rep_lon,rep_location,rep_sno,rep_up_status);
                else
                    SingleButtonAlert("Internet","Check Internet Connection","Ok","");
            }
    }

    public void deleteDataLocally(String sno){
        dbdth.deleteAfterUpload(sno);
        changeButtonColor();
    }
    private void changeButtonColor(){
        if(dbdth.getNotUploadCount() == 0) {
            sync.setBackgroundResource(R.color.red_blacky);
            sync.setEnabled(false);
        }
        else {
            sync.setBackgroundResource(R.drawable.sync_btn_bg);
            sync.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
        // Get selected row data to show on screen
        String selected_dr_name = dr_names.get(position).getDrName();
        String selected_dr_place = dr_names.get(position).getDrPlace();//((TextView) v.findViewById(R.id.location)).getText().toString();
        selected_dr_id = dr_names.get(position).getDrId();
        //Toast.makeText(getApplicationContext(),selected_dr_id+" "+selected_dr_name+ " "+selected_dr_place, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        //Toast.makeText(getApplicationContext(), "You Must Select Vehicle", Toast.LENGTH_LONG).show();
    }

    private void openDateTimeSettings(){
        Intent callGPSSettingIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }


    /**Alert with single button*/
    public void SingleButtonAlert(String title,String message,String but_txt,final String _state){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeGeoLocationFinder.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton(but_txt, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (_state.equals("gps"))
                            againAskingGPS();
                        else if (_state.equals("gps_mode")) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
//                        else if (_state.equals("done"))
//                            progressDialog.dismiss();
                        else if (_state.equals("auto_time"))
                            openDateTimeSettings();
                        else if (_state.equals("finish"))
                            finish();
                    }
                }

        );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    class AsyncGetAddress extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar();
        }
        @Override
        protected String doInBackground(String... arg0) {
            if(ci.isOnline()) {
                gps_address = gps.getAddressLine(true);
                SingleButtonAlert("Attendance", "Internet Available, Still no Location detected", "Ok", "");
            }
            else
                SingleButtonAlert("Attendance", "abcd12345\n" + arg0[0] + "\n" + selected_dr_id + "\n" + latitude + "\n" + longitude + "\n" + gps_address, "Ok", "done");
            //dbdth.addRepData(new GetSetData("","abcd12345",time_of_visit,"dr_id",latitude+"",longitude+"",gps_address,"N"));
            progressDialog.dismiss();
            return null;
        }
    }

}
