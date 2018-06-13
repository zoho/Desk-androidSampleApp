package desksdksample.zoho.com.desksdksample;

import android.app.Application;

import com.zoho.accounts.externalframework.ZohoErrorCodes;
import com.zoho.accounts.externalframework.ZohoSDK;
import com.zoho.accounts.externalframework.ZohoToken;
import com.zoho.accounts.externalframework.ZohoTokenCallback;
import com.zoho.desksdk.ZDAuthenticationInterface;
import com.zoho.desksdk.ZDDOMAIN;
import com.zoho.desksdk.ZDSdkInterface;
import com.zoho.desksdk.ZDeskSdk;

import org.jetbrains.annotations.NotNull;

import desksdksample.zoho.com.desksdksample.Utils.Constants;


/**
 * Created by mohan-zt105 on 28/05/18.
 */

public class MyApplication extends Application implements ZDAuthenticationInterface {
    @Override
    public void onCreate() {
        super.onCreate();
        ZohoSDK.getInstance(this).init(Constants.SCOPES, true);
        ZDeskSdk.Companion.getInstance().init(this, this, ZDDOMAIN.US);
    }

    @Override
    public void getAuthToken(final @NotNull ZDSdkInterface callback) {
        ZohoSDK.getInstance(this).getToken(new ZohoTokenCallback() {
            @Override
            public void onTokenFetchInitiated() {

            }

            @Override
            public void onTokenFetchComplete(ZohoToken zohoToken) {
                callback.setOAuthToken(zohoToken.getToken());
            }

            @Override
            public void onTokenFetchFailed(ZohoErrorCodes zohoErrorCodes) {

            }
        });
    }
}
