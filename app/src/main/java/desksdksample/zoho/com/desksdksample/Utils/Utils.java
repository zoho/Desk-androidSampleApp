package desksdksample.zoho.com.desksdksample.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by mohan-zt105 on 30/05/18.
 */

public final class Utils {


    public static int getPixel(Context context,int dp){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)((metrics.density*dp)+0.5f);
    }

}
