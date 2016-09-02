package helix.employeegeolocationdetector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AttendanceRegister extends AppCompatActivity{
        //,CompoundButton.OnCheckedChangeListener {//},LocationListener {

    DatabaseDateTimeHandler dbdth = new DatabaseDateTimeHandler(this);
    CheckInternet ci = new CheckInternet(this);
    //GetDeviceID gdi = new GetDeviceID(this);
    Button cam_image, save, sync,gps_loc;
    //CheckBox in_checkBox, out_checkBox;
    ImageView cam_img;
    EditText in_time;//, out_time;
    TextView gps_position;
    GPSTracker gps;
    double latitude, longitude;
    String checkedstate = "",attend_date;
    SimpleDateFormat sdf1;
    Calendar c,cc;
    int CAMERA_REQUEST = 0;
    boolean photo_taken = false;
    final String upLoadServerUri = "http://ridio.in/employeeattendance/PictureUpload", charset = "UTF-8";
    ProgressDialog progressDialog;
    Context context;

//    public AttendanceRegister(Context conx){
//        context=conx;
//        dbdth = new DatabaseDateTimeHandler(this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();
        Bundle intent = getIntent().getExtras();
        checkedstate = intent.getString("punch");
        if(checkedstate.equals("sync")) {
            syncPerform();
        }
        else if(checkedstate.equals("outtime")) {
            attend_date = intent.getString("punching_date");
            againAskingGPS();
            setTimeInEditText();
        }
        else {
            againAskingGPS();
            setTimeInEditText();
        }
//        Location locn1 = new Location("Loc");
//        locn1.setLatitude(10.000111);
//        locn1.setLongitude(50.222333);
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeButtonColor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //changeButtonColor();
    }

    @Override
    protected void onStop() {
            super.onStop();
        if(checkedstate.equals("sync"))
            Log.d("No GPS","Ok");
        else
            gps.stopUsingGPS();
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
//        if(checkedstate.equals("sync"))
//            Log.d("Nothing to Save","Ok");
//        else
        if(photo_taken)
            SingleButtonAlert("Attendance", "Save data before going back.", "Ok", "save");
        else
            super.onBackPressed();
    }


    void setTimeInEditText(){
        int auto_time_check= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
        int auto_time_zone_check= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
        if(auto_time_check==1 && auto_time_zone_check==1) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cc = Calendar.getInstance();
            in_time.setText(sdf.format(cc.getTime()));
        }
        else {
            in_time.setText("");
            SingleButtonAlert("Attendance", "You Disabled the auto time & auto time zone checkbox in Settings.", "Change", "auto_time");
        }
    }

    public void syncPerform(){
        File filePath1 = null; int count=0;
        //List<GetSetData> image_list = dbdth.getImageName();
        if(!ci.isOnline()) {
            if (checkedstate.equals("sync"))
                SingleButtonAlert("Internet", "Check internet connection before syncing.", "Ok", "finish");
            else
                SingleButtonAlert("Internet", "Check internet connection before syncing.", "Ok", "");
        }
//        else if (image_list.size() < 1 && notUploadCount() < 1) {
//            if (checkedstate.equals("sync"))
//                SingleButtonAlert("Attendance", "All data Synced.\nNo more data to Sync.", "Ok", "finish");
//            else
//                SingleButtonAlert("Attendance", "All data Synced.\nNo more data to Sync.", "Ok", "");
//        }
//        else
//            for (GetSetData img : image_list) {
//                count++;
//                int i=0;
//                if(image_list.size()== count)
//                    i=image_list.size();
//                else
//                    i=0;
//                if (img.getInPicLocation()!=null) {
//                    filePath1 = new File(Environment.getExternalStorageDirectory() + File.separator + "EAttendance", img.getInPicLocation() + ".png");
//                    if (checkImageAvailable(filePath1))
//                        new AsyncImageUpload(this, filePath1).execute(upLoadServerUri, charset, img.getAttandenceDate(), "In","0");
//                }
//                if (img.getOutPicLocation() != null){
//                    filePath1 = new File(Environment.getExternalStorageDirectory() + File.separator + "EAttendance", img.getOutPicLocation() + ".png");
//                    if (checkImageAvailable(filePath1))
//                        new AsyncImageUpload(this, filePath1).execute(upLoadServerUri, charset, img.getAttandenceDate(), "Out",i+"");
//                }
//            }
    }


    public boolean checkImageAvailable(File file) {
        if (file.exists())
            return true;
        else {
            //Toast.makeText(AttendanceRegister.this, "You have been deleted the image from Location.!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void againAskingGPS(){
        gps = new GPSTracker(AttendanceRegister.this);
        if(gps.canGetLocation()) {
            progressDisplayToLoadLatLong();
        }
        else
            gps.showSettingsAlert();
    }
    private void progressDisplayToLoadLatLong(){
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("Searching Location. Please Wait...");
        progressDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        progressDialog.setCancelable(false);
        progressDialog.show();
        gps.getLocation();
        Handler handler = new Handler();
        int temp = getLatLong();

        if(temp==0) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.d("Location", "Not Detected.!!");
                    int temp = getLatLong();
                    progressDialog.dismiss();
                    if (gps.getLocationMode(AttendanceRegister.this) != 3)
                        SingleButtonAlert("Attendance", "Change GPS mode settings to 'High Accuracy'", "Ok", "gps_mode");
                    else if (temp == 0) {
                        Toast.makeText(AttendanceRegister.this, "Location Not Received.", Toast.LENGTH_LONG).show();
                    }
                }
            }, 7000);
        }

    }

    private int getLatLong(){
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        if(latitude==0 && longitude==0) {
            gps_position.setText("Searching Location");
            return 0;
        }
        else {
            gps_position.setText(latitude+"  "+longitude);
            return 1;
        }
    }
    /**Camera activity set image*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            cam_img.setImageBitmap(photo);
            photo_taken=true;
        }
    }

    private void getAndSaveDataLocally(){
        //getBeforeLocation();
        String intime="",pic_name="",gps_address="";
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        //attend_date=sdf1.format(c.getTime());
        if(in_time.getText().toString().length()< 1)
            SingleButtonAlert("Attendance", "You Disabled the auto time & auto time zone checkbox in Settings.", "Change", "auto_time");
        else if(!photo_taken)
            SingleButtonAlert("Attendance", "You should capture your picture before Saving", "Ok", "");
        else if(checkedstate.equals("intime")  && in_time.getText().toString().length()>1){
            if (getLatLong()==1) {
                attend_date=sdf1.format(c.getTime());
                intime=in_time.getText().toString();
                gps_address= gps.getAddressLine(ci.isOnline());
                pic_name = "In_" + sdf2.format(cc.getTime());
//                if(dbdth.addAttendance(new GetSetData("abcd123","abish.r01@gmail.com","9688910689","",//gdi.getDeviceId(),
//                        intime, pic_name, latitude + "",longitude + "", gps_address, "P", attend_date, "N"))==0) {
//                    SingleButtonAlert("Attendance", "You already put attendance Today.", "Ok", "");
//                }else{
//                    //sciis.saveImage("EAttendance", pic_name, cam_img);
//                    SingleButtonAlert("Attendance", "In Time successfully completed.", "Ok", "finish");
//                    }
                photo_taken = false;
                in_time.setText("");
                resetPage();
            }
            else
                SingleButtonAlert("Attendance", "Location not detected.", "Ok","gps");
        }
        else if(checkedstate.equals("outtime") && in_time.getText().toString().length()>1) {
            if (getLatLong()==1) {
                intime=in_time.getText().toString();
                gps_address= gps.getAddressLine(ci.isOnline());
                pic_name = "Out_" + sdf2.format(cc.getTime());
//                if(dbdth.updateAttendance(new GetSetData(intime, pic_name, latitude + "", longitude + "",
//                        gps_address, "N", attend_date,"N"))==0) {
//                    SingleButtonAlert("Attendance", "You can't put out time before in time", "Ok", "");
//                }
//                else {
//                    //sciis.saveImage("EAttendance", pic_name, cam_img);
//                    SingleButtonAlert("Attendance", "Attendance process completed.\nNow you can proceed to Sync", "Ok", "sync_alert");
//                }
                photo_taken = false;
                in_time.setText("");
                resetPage();
            }
            else
                SingleButtonAlert("Attendance", "Location not detected.", "Ok","gps");
        }
        else
            SingleButtonAlert("Attendance", "You already put attendance Today.", "Ok", "");

    }

    public void deleteImageAndUploadData(File imf_loc,String attn_dt, String in_for,int count){
        if(imf_loc.exists() && attn_dt.length()>1) {
            imf_loc.delete();
            //dbdth.changeImageUploadState(attn_dt, in_for);
        }
        if(count!=0) {
            bringTableDataToSync();
            //sync.setBackgroundResource(R.drawable.press_effect_rect);
        }
    }

    public int notUploadCount(){
        List<GetSetData> attend_data=dbdth.getDataNotUploaded();
        return attend_data.size();
    }

    private int bringTableDataToSync(){
        List<GetSetData> attend_data=dbdth.getDataNotUploaded();
        String user_id,email,mobile,device_id,in_time,out_time,in_pic_name,out_pic_name,in_gps_lat,
                out_gps_lat,in_gps_lon,out_gps_lon,in_gps_address,out_gps_address,upload_state,attend_dt;
        for (GetSetData gs : attend_data) {
            //GetSetAttandance gsa=new GetSetAttandance();
//            user_id = gs.getUserId();
//            email = gs.getEmail();
//            mobile = gs.getMobile();
//            device_id = gs.getDeviceId();
//            in_time = gs.getInTime();
//            if(gs.getInAddress()==null || gs.getOutAddress()==null) {
//                in_gps_address = "null";
//                out_gps_address = "null";
//            }
//            else {
//                in_gps_address = gs.getInAddress();
//                out_gps_address = gs.getOutAddress();
//            }
//            if(gs.getOutTime()==null && gs.getOutPicLocation()==null && gs.getOutGpsLat()==null &&
//                    gs.getOutGpsLon()==null && gs.getOutAddress()==null) {
//                out_time = "null";
//                out_pic_name="null";
//                out_gps_lat="null";
//                out_gps_lon="null";
//                out_gps_address="null";
//            }
//            else {
//                out_time = gs.getOutTime();
//                out_pic_name = gs.getOutPicLocation();
//                out_gps_lat = gs.getOutGpsLat();
//                out_gps_lon = gs.getOutGpsLon();
//                out_gps_address= gs.getOutAddress();
//            }
//
//            in_pic_name= gs.getInPicLocation();
//            in_gps_lat= gs.getInGpsLat();
//            in_gps_lon= gs.getInGpsLon();
//            upload_state= gs.getUpState();
//            attend_dt= gs.getAttandenceDate();

//                new AsyncSyncUpload(AttendanceRegister.this).execute(user_id, email, mobile, device_id, in_time, out_time, in_pic_name,
//                        out_pic_name, in_gps_lat, in_gps_lon, out_gps_lat, out_gps_lon, in_gps_address, out_gps_address,
//                        upload_state, attend_dt);
        }
            return attend_data.size();
    }

    public void recordDeleteAfterUploadSuccess(String atndce_dt){
//        dbdth.checkImgUpstateBeforeDelete(atndce_dt);
        //if(checkedstate.equals("sync"))
        finish();
    }

    private void openDateTimeSettings(){
        Intent callGPSSettingIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }

    public void getBeforeLocation(){
        progressDialog.dismiss();
    }

    private void resetPage(){
//        in_checkBox.setChecked(false);
//        out_checkBox.setChecked(false);
        in_time.setText("");
        gps_loc.setVisibility(View.INVISIBLE);
        gps_position.setText("");
        //out_time.setText("");
        //cam_img.setImageResource(R.drawable.cam_img);
        //sync.setBackgroundColor(getResources().getColor(R.drawable.press_effect_rect));
    }

    public void changeButtonColor(){
        //if(dbdth.getImageName().size()>0)
            //sync.setBackgroundResource(R.drawable.press_effect_rect1);
        //else
            //sync.setBackgroundResource(R.drawable.press_effect_rect);
    }

    /**Alert with single button*/
    public void SingleButtonAlert(String title,String message,String but_txt,final String _state){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttendanceRegister.this);
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
                else if (_state.equals("sync_alert")) {
                    changeButtonColor();
                    save.setEnabled(false);
                    cam_image.setEnabled(false);
                }
                else if (_state.equals("save"))
                    getAndSaveDataLocally();
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
    }
