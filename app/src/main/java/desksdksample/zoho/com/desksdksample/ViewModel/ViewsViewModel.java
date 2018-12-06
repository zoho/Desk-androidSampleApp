package desksdksample.zoho.com.desksdksample.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.zoho.desksdk.ZDOrganizationAPIHandler;
import com.zoho.desksdk.ZDViewsAPIHandler;
import com.zoho.desksdk.callbacks.ZDCallback;
import com.zoho.desksdk.callbacks.ZDResult;
import com.zoho.desksdk.exceptions.ZDBaseException;
import com.zoho.desksdk.organizations.ZDOrganizationList;
import com.zoho.desksdk.profile.ZDIAMProfiles;
import com.zoho.desksdk.utils.ZDUtilsKt;
import com.zoho.desksdk.views.ZDViewsList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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


    public void getViewsList(long orgId, String department, String module) {
        ZDViewsAPIHandler.INSTANCE.listAllView(new ZDCallback<ZDViewsList>() {
            @Override
            public void onFailure(@Nullable Call<ZDViewsList> call, @NotNull ZDBaseException exception) {

            }

            @Override
            public void onSuccess(@NotNull ZDResult<? extends ZDViewsList> result) {
                ZDViewsList list = result.getData();
                if (list != null) {
                    mViewsList.postValue(list);
                }
            }
        }, orgId, department, module);
    }

    public void getProfileDetail() {
        ZDUtilsKt.getProfile(new ZDCallback<ZDIAMProfiles>() {
            @Override
            public void onFailure(@Nullable Call<ZDIAMProfiles> call, @NotNull ZDBaseException exception) {

            }

            @Override
            public void onSuccess(@NotNull ZDResult<? extends ZDIAMProfiles> result) {
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
            public void onFailure(@Nullable Call<ResponseBody> call, @NotNull ZDBaseException exception) {

            }

            @Override
            public void onSuccess(@NotNull ZDResult<? extends ResponseBody> result) {
                ResponseBody responseBody = result.getData();
                if (responseBody != null) {
                    mProfilePic.postValue(responseBody);
                }
            }
        }, emailId);
    }

    public void getOrganizationList() {
        ZDOrganizationAPIHandler.INSTANCE.getAllOrganizations(new ZDCallback<ZDOrganizationList>() {
            @Override
            public void onFailure(@Nullable Call<ZDOrganizationList> call, @NotNull ZDBaseException exception) {

            }

            @Override
            public void onSuccess(@NotNull ZDResult<? extends ZDOrganizationList> result) {
                ZDOrganizationList zdOrganizationList = result.getData();
                if (zdOrganizationList != null) {
                    mOrganizationList.postValue(zdOrganizationList);
                }
            }
        });
    }
}
