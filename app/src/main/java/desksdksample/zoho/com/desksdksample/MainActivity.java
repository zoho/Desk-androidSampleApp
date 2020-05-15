package desksdksample.zoho.com.desksdksample;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.zoho.accounts.externalframework.ZohoSDK;
import com.zoho.desk.provider.departments.ZDDepartment;
import com.zoho.desk.provider.departments.ZDDepartmentList;
import com.zoho.desk.provider.organizations.ZDOrganization;
import com.zoho.desk.provider.organizations.ZDOrganizationList;
import com.zoho.desksdkui.ZohoDeskUIKitKt;

import java.util.ArrayList;

import desksdksample.zoho.com.desksdksample.ViewModel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private String mDepartmentId;
    private String mOrgId;
    private MainViewModel mViewModel;
    private Spinner spOrganization;
    private Spinner spDepartment;
    private TextView tvLogout;
    private RelativeLayout rlProgress;
    private Button bTickets;
    private Switch  allDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        spOrganization = findViewById(R.id.spOrganization);
        spDepartment = findViewById(R.id.spDepartment);
        tvLogout = findViewById(R.id.tvLogout);
        rlProgress = findViewById(R.id.rlProgress);
        bTickets = findViewById(R.id.bTickets);

        _setObservers();
        _setClickListener();

        mViewModel.getOrganizationList();

    }

    private void _setClickListener() {
        bTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrgId != null && mDepartmentId != null) {
                    Intent intent = new Intent(MainActivity.this, TicketListActivity.class);
                    intent.putExtra("departmentId", mDepartmentId); // No I18N
                    intent.putExtra("orgId", mOrgId);// No I18N
                    startActivity(intent);
                }
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                rlProgress.setVisibility(View.VISIBLE);
                ZohoSDK sdk = ZohoSDK.getInstance(getApplicationContext());
                sdk.revoke(new ZohoSDK.OnLogoutListener() {
                    @Override
                    public void onLogoutSuccess() {
                        ZohoDeskUIKitKt.flushUserData(v.getContext());
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    }

                    @Override
                    public void onLogoutFailed() {

                    }
                });
            }
        });
    }

    private void _setObservers() {
        mViewModel.mOrganizationList.observe(this, new Observer<ZDOrganizationList>() {
            @Override
            public void onChanged(@Nullable ZDOrganizationList zdOrganizationList) {
                if (zdOrganizationList != null) {
                    initializeSpinner(zdOrganizationList);
                }
            }
        });

        mViewModel.mDepartmentList.observe(this, new Observer<ZDDepartmentList>() {
            @Override
            public void onChanged(@Nullable ZDDepartmentList zdDepartmentList) {
                rlProgress.setVisibility(View.GONE);
                if (zdDepartmentList != null) {
                    initializeSpinner(zdDepartmentList);
                }
            }
        });
    }

    private void initializeSpinner(final ZDOrganizationList zdOrganizationList) {
        ArrayList<String> orgNameList = new ArrayList<>();
        for (ZDOrganization organization : zdOrganizationList.getData()) {
            orgNameList.add(organization.getCompanyName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, orgNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrganization.setAdapter(dataAdapter);
        spOrganization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOrgId = zdOrganizationList.getData().get(position).getId();
                rlProgress.setVisibility(View.VISIBLE);
                mViewModel.getDepartmentList(mOrgId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initializeSpinner(final ZDDepartmentList zdDepartmentList) {
        ZDDepartment dd =new ZDDepartment();
                dd.setId("allDepartment"); // No I18N
                dd.setName("All Department");// No I18N
        zdDepartmentList.getData().add(0,dd);
        ArrayList<String> deptNameList = new ArrayList<>();
        for (ZDDepartment dept : zdDepartmentList.getData()) {
            deptNameList.add(dept.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, deptNameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(dataAdapter);
        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mDepartmentId = zdDepartmentList.getData().get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }


}
