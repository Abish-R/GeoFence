package helix.employeegeolocationdetector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 	Purpose	   : Check Internet
 *  Created by : Abish
 *  Created Dt : 30-03-2016
 *  Modified on:
 *  Verified by:
 *  Verified Dt:
 */
public class CheckInternet {
    private Context context;

    public CheckInternet(Context contex){
        this.context = contex;
    }

    /** Check Internet connection**/
    public boolean isOnline() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
        }
        return false;
    }

}
