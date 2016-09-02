package helix.employeegeolocationdetector;

/**
 * Created by HelixTech-Admin on 3/30/2016.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DoctorListAdapter extends ArrayAdapter<String>{

    private Context activity;
    private ArrayList data;
    public Resources res;
    GetSetData tempValues=null;
    LayoutInflater inflater;

    /***  CustomAdapter Constructor, initializer for customer entry **/
    public DoctorListAdapter(Context vehicleMasterClass,int textViewResourceId,
                                     ArrayList objects,Resources resLocal){
        super(vehicleMasterClass, textViewResourceId, objects);

        /*** Take passed values ***/
        activity = vehicleMasterClass;
        data     = objects;
        res      = resLocal;

        /***  Layout inflator to call external xml layout () **/
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /** This funtion called for each row ( Called data.size() times ) */
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /*** Inflate bike_spinner.xml file for each row ( Defined below ) **/
        View row = inflater.inflate(R.layout.bike_spinner, parent, false);

        /*** Get each Model object from Arraylist **/
        tempValues = null;
        tempValues = (GetSetData) data.get(position);

        TextView label        = (TextView)row.findViewById(R.id.location);
        TextView sub          = (TextView)row.findViewById(R.id.doctor);
//        if(position==0){
//            label.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spnr_drp_dwn_blck_30, 0);
////            Drawable bg_img = getContext().getResources().getDrawable( R.drawable.spnr_drp_dwn_blck_30 );
////            label.setCompoundDrawables(null,null, bg_img,null);
//        }

//        if(position==0){  // Set values for spinner each row
            sub.setText(tempValues.getDrName());
            label.setText(tempValues.getDrPlace());
//            label.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spnr_drp_dwn_blck_30, 0);
//            Drawable bg_img = getContext().getResources().getDrawable( R.drawable.spnr_drp_dwn_blck_30 );
//            label.setCompoundDrawables(null,null, bg_img,null);
//        }
//        else{
//            sub.setText(tempValues.getDrName());
//            label.setText(tempValues.getDrPlace());
//            label.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//            label.setCompoundDrawables(null,null, null,null);
//        }

        return row;
    }
}
