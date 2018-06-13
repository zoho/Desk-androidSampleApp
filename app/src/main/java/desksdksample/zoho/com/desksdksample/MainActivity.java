package desksdksample.zoho.com.desksdksample;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zoho.accounts.externalframework.ZohoSDK;
import com.zoho.desksdk.organizations.ZDOrganizationList;
import com.zoho.desksdk.profile.ZDProfiles;
import com.zoho.desksdk.tickets.ZDTicket;
import com.zoho.desksdk.utils.ZDClickListener;
import com.zoho.desksdk.utils.ZDErrorData;
import com.zoho.desksdk.utils.ZDTypeCallBack;
import com.zoho.desksdk.views.ZDViews;
import com.zoho.desksdk.views.ZDViewsList;
import com.zoho.desksdkui.ZohoDeskUIKitKt;
import com.zoho.desksdkui.paging.ZDNetworkState;
import com.zoho.desksdkui.ticketlist.ZDTicketListView;
import com.zoho.desksdkui.utils.ZDTicketListConfig;

import org.jetbrains.annotations.NotNull;

import desksdksample.zoho.com.desksdksample.Utils.Utils;
import desksdksample.zoho.com.desksdksample.ViewModel.ViewsViewModel;
import okhttp3.ResponseBody;

import static com.zoho.desksdkui.utils.ZDUIUtilsKt.ZD_INITIAL_LOADING;
import static com.zoho.desksdkui.utils.ZDUIUtilsKt.getAttachmentFileName;
import static com.zoho.desksdkui.utils.ZDUIUtilsKt.saveDownloadedFile;
import static com.zoho.desksdkui.utils.ZDUIUtilsKt.setGlideImage;

public class MainActivity extends AppCompatActivity {
    // Organization Id
    private Long mOrgId = 0L;
    // Department Id
    private String mDepartment = "allDepartment";// No I18N
    // Module name
    private String mModule = "tickets";// No I18N
    private ViewsViewModel mViewModel;
    private ProgressBar mLoader;
    private ZDTicketListView mList;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ImageView mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoader = findViewById(R.id.loader);
        mList = findViewById(R.id.ticketList);
        mLogout = findViewById(R.id.logout);
        // Android ViewModel is LifeCycle aware Object,
        // Scope is maintained based on the LifeCycle owner.
        mViewModel = ViewModelProviders.of(this).get(ViewsViewModel.class);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mViewModel.getOrganizationList();
        initView();
        _setObservers();
    }

    private void initView() {
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mLoader.setVisibility(View.VISIBLE);
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

    private void initList() {
        ZDTicketListConfig config = new ZDTicketListConfig.ZDBuilder().
                setPageSize(99).
                build();
        loadTicketList(config);
    }

    private void _setObservers() {
        final LinearLayout mLayout = findViewById(R.id.drawer_wrapper);
        mViewModel.mOrganizationList.observe(this, new Observer<ZDOrganizationList>() {
            @Override
            public void onChanged(@Nullable ZDOrganizationList zdOrganizationList) {
                if (zdOrganizationList != null && zdOrganizationList.getData().size() > 1) {
                    mOrgId = zdOrganizationList.getData().get(3).getId();
                    initList();
                    mViewModel.getViewsList(mOrgId, mDepartment, mModule);
                    mViewModel.getProfileDetail();
                }
            }
        });
        // Observes the List of Views
        mViewModel.mViewsList.observe(this, new Observer<ZDViewsList>() {
            @Override
            public void onChanged(@android.support.annotation.Nullable ZDViewsList zdViewsList) {
                if (zdViewsList != null) {
                    for (ZDViews views : zdViewsList.getData()) {
                        final LinearLayout propertyField = (LinearLayout) getLayoutInflater().inflate(R.layout.views_menu_item, mLayout, false);
                        TextView viewsText = propertyField.findViewById(R.id.view_name);
                        viewsText.setText(views.getName());
                        viewsText.setTextColor(Color.parseColor("#151515"));//No I18N
                        viewsText.setTextSize(17f);
                        LinearLayout.LayoutParams lp=( LinearLayout.LayoutParams) viewsText.getLayoutParams();
                        lp.setMargins(Utils.getPixel(viewsText.getContext(),20),Utils.getPixel(viewsText.getContext(),13),Utils.getPixel(viewsText.getContext(),15),Utils.getPixel(viewsText.getContext(),13));
                        viewsText.setLayoutParams(lp);
                        viewsText.setTag(views);
                        viewsText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ZDViews zdViews = (ZDViews) v.getTag();
                                setTitle(zdViews.getName());
                                mLoader.setVisibility(View.VISIBLE);
                                ZDTicketListConfig config = mList.getConfiguration();
                                config.setMViewId(zdViews.getId());
                                mList.updateConfig(config, mOrgId);
                                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        });
                        mLayout.addView(propertyField);
                    }
                }
            }
        });
        // Observes the Profile Details
        mViewModel.mProfile.observe(this, new Observer<ZDProfiles>() {
            @Override
            public void onChanged(final @Nullable ZDProfiles zdProfiles) {
                if (zdProfiles != null) {
                    final ImageView profilePicture = findViewById(R.id.profile_pic);
                    mViewModel.downloadProfilePicture(zdProfiles.getProfile().getPrimary_email());
                    mViewModel.mProfilePic.observe(MainActivity.this, new Observer<ResponseBody>() {
                        @Override
                        public void onChanged(@Nullable ResponseBody responseBody) {
                            String url = saveDownloadedFile(profilePicture.getContext(), getAttachmentFileName(0L, zdProfiles.getProfile().getLast_name() + ".png"), responseBody);// No I18N
                            setGlideImage(profilePicture, url,0L, true, false,false);
                        }

                    });


                    final TextView userName = findViewById(R.id.user_name);
                    userName.setText(zdProfiles.getProfile().getDisplay_name());

                    final TextView emailId = findViewById(R.id.mail_id);
                    emailId.setText(zdProfiles.getProfile().

                            getPrimary_email());


                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void loadTicketList(ZDTicketListConfig config) {
        // Init method of ZDTicketList, which receives
        //life cycle owner ,
        // Organization Id ,
        // Department Id,
        // Optional Configuration Object
        // Network CallBack
        // Click Callback
        mList.initTicketList(this, mOrgId, config, new ZDTypeCallBack<ZDNetworkState>() {
            @Override
            public void onFailed(ZDErrorData code) {
            }

            @Override
            public void onSuccess(ZDNetworkState data) {
                if (data.getMsg().equals(ZD_INITIAL_LOADING)) {
                    mLoader.setVisibility(View.GONE);
                }
            }
        }, new ZDClickListener<ZDTicket>() {
            @Override
            public void onClick(ZDTicket data, @NotNull View view) {
                Intent intent = new Intent(MainActivity.this, TicketDetailActivity.class);
                intent.putExtra("ticketId", data.getId()); // No I18N
                intent.putExtra("orgId", mOrgId);// No I18N
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

    }
}
