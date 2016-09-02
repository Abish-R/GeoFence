package helix.employeegeolocationdetector;

/**
 * Created by HelixTech-Admin on 3/17/2016.
 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AsyncSyncUpload extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;
    private Context context;
    String rep_sno;

    public AsyncSyncUpload(Context conx){
        context=conx;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        // do stuff before posting data
        progressDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("Syncing data. Please Wait...");
        progressDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... arg0) {
        String result=null;
        try{
            // url where the data will be posted
            String postReceiverUrl = "http://ridio.in/geofency/SyncUploadData";
            Log.v("Login Url:", "postURL: " + postReceiverUrl);
            HttpClient httpClient = new DefaultHttpClient();   // HttpClient
            HttpPost httpPost = new HttpPost(postReceiverUrl);	// post header

            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("rep_id", arg0[0]));
            nameValuePairs.add(new BasicNameValuePair("visited_date_time", arg0[1]));
            nameValuePairs.add(new BasicNameValuePair("doctor_id", arg0[2]));
            nameValuePairs.add(new BasicNameValuePair("latitude", arg0[3]));
            nameValuePairs.add(new BasicNameValuePair("longitude", arg0[4]));
            nameValuePairs.add(new BasicNameValuePair("visited_location", arg0[5]));
            rep_sno = arg0[6];
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
            httpPost.setEntity(entity);

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(context, "Server error while uploading data.!", Toast.LENGTH_LONG).show();
            //((AttendanceRegister)context).SingleButtonAlert("Attendance", "Server error while uploading data.", "Ok","");
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        // do stuff after posting data
        progressDialog.dismiss();
        int validation = 0;
        JSONObject root = null;
        String rep_id = null, message = null,doctor_id=null;
        try {
            Log.d("Inside Post", "message");
            root = new JSONObject(result);
            validation = root.getInt("response");
            message = root.getString("message");
            rep_id = root.getString("rep_id");
            doctor_id= root.getString("doctor_id");

            Log.d(result, rep_id);
            if (validation == 1) {
                ((EmployeeGeoLocationFinder) context).deleteDataLocally(rep_sno);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
