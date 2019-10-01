package desksdksample.zoho.com.desksdksample.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zoho.desk.provider.ZDViewsAPIHandler;
import com.zoho.desk.provider.callbacks.ZDCallback;
import com.zoho.desk.provider.callbacks.ZDResult;
import com.zoho.desk.provider.exceptions.ZDBaseException;
import com.zoho.desk.provider.organizations.ZDOrganizationList;
import com.zoho.desk.provider.profile.ZDIAMProfiles;
import com.zoho.desk.provider.utils.ZDUtilsKt;
import com.zoho.desk.provider.views.ZDViewsList;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by mohan-zt105 on 26/05/18.
 */

public class ViewsViewModel extends ViewModel {
    public MutableLiveData<ZDViewsList> mViewsList = new MutableLiveData<>();
    public MutableLiveData<ZDIAMProfiles> mProfile = new MutableLiveData<>();
    public MutableLiveData<ResponseBody> mProfilePic = new MutableLiveData<>();
    public MutableLiveData<ZDOrganizationList> mOrganizationList = new MutableLiveData<>();
    public MutableLiveData<ZDBaseException> errorMessage = new MutableLiveData<>();



    public void getViewsList(String orgId, String module, String departmentId) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(!departmentId.equals("allDepartment")) {
            map.put("departmentId", departmentId);
        }
        ZDViewsAPIHandler.INSTANCE.listAllView(new ZDCallback<ZDViewsList>() {
            @Override
            public void onFailure(@Nullable Call<ZDViewsList> call, @NonNull ZDBaseException exception) {
                errorMessage.postValue(exception);
            }

            @Override
            public void onSuccess(@NonNull ZDResult<? extends ZDViewsList> result) {
                ZDViewsList list = result.getData();
                if (list != null) {
                    mViewsList.postValue(list);
                }
            }
        }, orgId, module, map);
    }

    public void getProfileDetail() {
        ZDUtilsKt.getProfile(new ZDCallback<ZDIAMProfiles>() {
            @Override
            public void onFailure(@Nullable Call<ZDIAMProfiles> call, @NonNull ZDBaseException exception) {
                errorMessage.postValue(exception);
            }

            @Override
            public void onSuccess(@NonNull ZDResult<? extends ZDIAMProfiles> result) {
                ZDIAMProfiles profile = result.getData();
                if (profile != null) {
                    mProfile.postValue(profile);
                }
            }
        });
    }

    public void downloadProfilePicture(String emailId) {
        ZDUtilsKt.downloadProfilePicture(new ZDCallback<ResponseBody>() {
            @Override
            public void onFailure(@Nullable Call<ResponseBody> call, @NonNull ZDBaseException exception) {
                errorMessage.postValue(exception);
            }

            @Override
            public void onSuccess(@NonNull ZDResult<? extends ResponseBody> result) {
                ResponseBody responseBody = result.getData();
                if (responseBody != null) {
                    mProfilePic.postValue(responseBody);
                }
            }
        }, emailId);
    }


}
