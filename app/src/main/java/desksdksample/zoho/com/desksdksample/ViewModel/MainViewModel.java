package desksdksample.zoho.com.desksdksample.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.zoho.desk.provider.ZDDepartmentAPIHandler;
import com.zoho.desk.provider.ZDOrganizationAPIHandler;
import com.zoho.desk.provider.callbacks.ZDCallback;
import com.zoho.desk.provider.callbacks.ZDResult;
import com.zoho.desk.provider.departments.ZDDepartmentList;
import com.zoho.desk.provider.exceptions.ZDBaseException;
import com.zoho.desk.provider.organizations.ZDOrganizationList;

import java.util.HashMap;

import retrofit2.Call;

/**
 * created by suresh-zt259 on 25/04/19.
 */
public class MainViewModel extends ViewModel {
    public MutableLiveData<ZDOrganizationList> mOrganizationList = new MutableLiveData<>();
    public MutableLiveData<ZDDepartmentList> mDepartmentList = new MutableLiveData<>();

    public void getOrganizationList() {
        ZDOrganizationAPIHandler.INSTANCE.getAllOrganizations(new ZDCallback<ZDOrganizationList>() {
            @Override
            public void onFailure(@Nullable Call<ZDOrganizationList> call, @NonNull ZDBaseException exception) {

            }

            @Override
            public void onSuccess(@NonNull ZDResult<? extends ZDOrganizationList> result) {
                ZDOrganizationList zdOrganizationList = result.getData();
                if (zdOrganizationList != null) {
                    mOrganizationList.postValue(zdOrganizationList);
                }
            }
        });
    }

    public void getDepartmentList(String orgId) {
        HashMap<String,Object> param = new HashMap<>();
        param.put("from",0);
        param.put("limit",200);
        ZDDepartmentAPIHandler.INSTANCE.listAllDepartments(new ZDCallback<ZDDepartmentList>() {
            @Override
            public void onFailure(@Nullable Call<ZDDepartmentList> call, @NonNull ZDBaseException exception) {

            }

            @Override
            public void onSuccess(@NonNull ZDResult<? extends ZDDepartmentList> result) {
                mDepartmentList.postValue(result.getData());
            }
        }, orgId, param);
    }
}