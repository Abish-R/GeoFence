package helix.employeegeolocationdetector;

/** Ridio v1.0.1
 * 	Purpose	   : Download or Sync the Bookings for Cloud DB
 *  Created by : Abish
 *  Created Dt : 23-02-2016
 *  Modified on:
 *  Verified by:
 *  Verified Dt:
 * **/

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AsyncSyncDownload extends AsyncTask<String, Void, String> {
    /**Class Variables*/
    Context context;
    String TAG="AsyncSignUpClass",data_format="";
    int validation=0;
    JSONObject root;
    ProgressDialog progressDialog;
    public AsyncSyncDownload(Context contx) {/** Edit the URL **/
        //this.activity = activity;
        context = contx;
    }
    /**Before Sending Data*/
    protected void onPreExecute() {
        super.onPreExecute();
        // do stuff before posting data
       // if(data_format.equals("Old")) {
            progressDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage("Loading. Please Wait...");
            progressDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            progressDialog.show();
      //  }
    }
    /**While sending datas to DB*/
    @Override
    protected String doInBackground(String... arg0) {
        String result=null;
        try{
            // url where the data will be posted
            String postReceiverUrl = arg0[0];/** Edit the url **/
            Log.v(TAG, "postURL: " + postReceiverUrl);
            HttpClient httpClient = new DefaultHttpClient();   // HttpClient
            HttpPost httpPost = new HttpPost(postReceiverUrl);	// post header

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
           // sendErrorReport(); /** add the function definition **/
            e.printStackTrace();
        }
        return result;
    }
    /**After Data p  assed, returned datas are below*/
    @Override
    protected void onPostExecute(String result) {
        // do stuff after posting data
        //if(data_format.equals("Old"))
            progressDialog.dismiss();
        String message=null;
        JSONArray data;
        try {
            Log.d("Inside Post", "message");
            root = new JSONObject(result);
            validation = root.getInt("response");
            message = root.getString("message");

            Log.d(result, "");
            if(validation==1) {
                data=root.getJSONArray("data");
                /**Doing Rate insertion process, from which the datas are downloaded from cloud*/
                for(int i=0;i<data.length();i++) {
                    String dr_id = data.getJSONObject(i).getString("doctor_id");
                    String dr_name = data.getJSONObject(i).getString("doctor_name");
                    String dr_place = data.getJSONObject(i).getString("doctor_location");
                    String dr_lat = data.getJSONObject(i).getString("latitude");
                    String dr_lon = data.getJSONObject(i).getString("longitude");
                    String dr_radius = data.getJSONObject(i).getString("radious");
                    String dr_crtd_dt = data.getJSONObject(i).getString("created_date");
                    ((EmployeeGeoLocationFinder) context).addDoctorsDetailLocally(dr_id,dr_name,
                            dr_place,dr_lat,dr_lon,dr_radius,dr_crtd_dt);
                }
                ((EmployeeGeoLocationFinder) context).successMethod();
            }
        }
        catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(context, "Server error while uploading data.!", Toast.LENGTH_LONG).show();
        }
    }
//    /**Alert Messages*/
//    private void alertWrongSignUp(String val){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setTitle("SignUp Error");
//        alertDialogBuilder.setMessage(val);
//        // set positive button: Yes message
//        alertDialogBuilder.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int id) {
//            }
//        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
}
