package desksdksample.zoho.com.desksdksample;

import android.app.Application;
import android.support.annotation.NonNull;

import com.zoho.accounts.externalframework.ZohoErrorCodes;
import com.zoho.accounts.externalframework.ZohoSDK;
import com.zoho.accounts.externalframework.ZohoToken;
import com.zoho.accounts.externalframework.ZohoTokenCallback;
import com.zoho.desk.core.ZDAuthenticationInterface;
import com.zoho.desk.core.ZDErrorOnLoginInterface;
import com.zoho.desk.core.ZDeskSdk;


import desksdksample.zoho.com.desksdksample.Utils.Constants;


/**
 * Created by mohan-zt105 on 28/05/18.
 */

public class MyApplication extends Application implements ZDAuthenticationInterface {
    @Override
    public void onCreate() {
        super.onCreate();
        ZohoSDK.getInstance(this).init(Constants.SCOPES, true);
        ZDeskSdk.Companion.getInstance().init(this, this);
        ZDeskSdk.Companion.getInstance().setZdEnableLogs(true);
        ZDeskSdk.Companion.getInstance().setErrorCallBack(new ZDErrorOnLoginInterface() {
            @Override
            public void onOAuthTokenInvalid(int i, @NonNull String s) {

            }
        });
    }



    @NonNull
    @Override
    public String getAuthToken() {
        return   ZohoSDK.getInstance(this).getToken().getToken();
    }

    @Override
    public void getAuthToken(final @NonNull com.zoho.desk.core.ZDSdkInterface callback) {
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
