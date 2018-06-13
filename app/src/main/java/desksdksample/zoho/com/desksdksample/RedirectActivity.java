package desksdksample.zoho.com.desksdksample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zoho.accounts.externalframework.ZohoSDK;

/**
 * Created by mohan-zt105 on 28/05/18.
 */

public class RedirectActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZohoSDK.getInstance(this).handleRedirection(this);
    }
}
