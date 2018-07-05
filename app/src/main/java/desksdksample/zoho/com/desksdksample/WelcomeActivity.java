package desksdksample.zoho.com.desksdksample;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zoho.accounts.externalframework.ZohoErrorCodes;
import com.zoho.accounts.externalframework.ZohoSDK;
import com.zoho.accounts.externalframework.ZohoToken;
import com.zoho.accounts.externalframework.ZohoTokenCallback;

import java.util.HashMap;

/**
 * Created by mohan-zt105 on 28/05/18.
 */

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        HashMap<String,String> mParam = new HashMap<String, String>();
        mParam.put("logout","true");
        ZohoSDK sdk =  ZohoSDK.getInstance(getApplicationContext());
        if (!sdk.isUserSignedIn()) {
            sdk.presentLoginScreen(this, new ZohoTokenCallback() {
                @Override
                public void onTokenFetchInitiated() {
                    //On login Initiated -   onTokenFetch Initiated
                }

                @Override
                public void onTokenFetchComplete(ZohoToken zohoToken) {
                    //On login Success  -  onTokenFetch success
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onTokenFetchFailed(ZohoErrorCodes zohoErrorCodes) {
                    // On login failed - onTokenFetch failed
                }
            },mParam);
        }else {
            // this block will called on user logged in, will take to MainActivity
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }

}
