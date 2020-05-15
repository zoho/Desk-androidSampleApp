package desksdksample.zoho.com.desksdksample;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.zoho.accounts.externalframework.ZohoSDK;
import com.zoho.desk.provider.exceptions.ZDBaseException;
import com.zoho.desk.provider.profile.ZDIAMProfiles;
import com.zoho.desk.provider.tickets.ZDTicket;
import com.zoho.desk.provider.views.ZDViews;
import com.zoho.desk.provider.views.ZDViewsList;
import com.zoho.desk.ticket.ticketlist.ZDTicketListFragment;
import com.zoho.desk.ticket.utils.ZDTicketListConfig;
import com.zoho.desksdkui.ZohoDeskUIKitKt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import desksdksample.zoho.com.desksdksample.Utils.Utils;
import desksdksample.zoho.com.desksdksample.ViewModel.ViewsViewModel;
import okhttp3.ResponseBody;

import static com.zoho.desk.image.ImageUtilsKt.setGlideImage;
import static com.zoho.desk.ticket.utils.ZDUIUtilsKt.getAttachmentFileName;
import static com.zoho.desk.ticket.utils.ZDUIUtilsKt.saveDownloadedFile;


public class TicketListActivity extends AppCompatActivity implements ZDTicketListFragment.Companion.ZDTicketListInterface {
    // Organization Id
    private String mOrgId = "";
    // Department Id
    private String departmentId = "allDepartment";// No I18N
    // Module name
    private String mModule = "tickets";// No I18N
    private ViewsViewModel mViewModel;
    private ProgressBar mLoader;
    private ZDTicketListFragment ticketList;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ImageView mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        mLoader = findViewById(R.id.loader);
//        mList = findViewById(R.id.ticketList);
        mLogout = findViewById(R.id.logout);
        // Android ViewModel is LifeCycle aware Object,
        // Scope is maintained based on the LifeCycle owner.
        mViewModel = ViewModelProviders.of(this).get(ViewsViewModel.class);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        departmentId = getIntent().getExtras().getString("departmentId"); // No I18N
        mOrgId = getIntent().getExtras().getString("orgId");// No I18N
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mViewModel.getViewsList(mOrgId, mModule, departmentId);
        mViewModel.getProfileDetail();
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
                        Intent intent = new Intent(TicketListActivity.this, WelcomeActivity.class);
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
                setPageSize(99).sortBy("-recentThread").//No I18N
                build();
//        loadTicketList(config);
//        showDetailViews();
    }

    private void _setObservers() {
        final LinearLayout mLayout = findViewById(R.id.drawer_wrapper);

        // Observes the List of Views
        mViewModel.mViewsList.observe(this, new Observer<ZDViewsList>() {
            @Override
            public void onChanged(@android.support.annotation.Nullable ZDViewsList zdViewsList) {
                if (zdViewsList != null) {
                    showDetailViews(zdViewsList.getData().get(7).getId());
                    for (ZDViews views : zdViewsList.getData()) {
                        final LinearLayout propertyField = (LinearLayout) getLayoutInflater().inflate(R.layout.views_menu_item, mLayout, false);
                        TextView viewsText = propertyField.findViewById(R.id.view_name);
                        viewsText.setText(views.getName());
                        viewsText.setTextColor(Color.parseColor("#151515"));//No I18N
                        viewsText.setTextSize(17f);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewsText.getLayoutParams();
                        lp.setMargins(Utils.getPixel(viewsText.getContext(), 20), Utils.getPixel(viewsText.getContext(), 13), Utils.getPixel(viewsText.getContext(), 15), Utils.getPixel(viewsText.getContext(), 13));
                        viewsText.setLayoutParams(lp);
                        viewsText.setTag(views);
                        viewsText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ZDViews zdViews = (ZDViews) v.getTag();
                                setTitle(zdViews.getName());

                                ArrayList<String> deptList = new ArrayList<>();
                                deptList.add(departmentId);
                                ZDTicketListConfig config = new ZDTicketListConfig.ZDBuilder().setViewId(zdViews.getId()).sortBy("-recentThread").setDepartmentIds(deptList).setPageSize(99).build();// No I18N
                                ticketList.setConfig(config);
//                                ticketList.resetList();
                                mLoader.setVisibility(View.VISIBLE);
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
        mViewModel.mProfile.observe(this, new Observer<ZDIAMProfiles>() {
            @Override
            public void onChanged(final @Nullable ZDIAMProfiles zdProfiles) {
                if (zdProfiles != null) {
                    final ImageView profilePicture = findViewById(R.id.profile_pic);
                    mViewModel.downloadProfilePicture(zdProfiles.getProfile().getPrimary_email());
                    mViewModel.mProfilePic.observe(TicketListActivity.this, new Observer<ResponseBody>() {
                        @Override
                        public void onChanged(@Nullable ResponseBody responseBody) {
                            String url = saveDownloadedFile(profilePicture.getContext(), getAttachmentFileName("", zdProfiles.getProfile().getLast_name() + ".png"), responseBody);// No I18N
                            setGlideImage(profilePicture,url,true,false);

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
        mViewModel.errorMessage.observe(this, new Observer<ZDBaseException>() {
            @Override
            public void onChanged(@Nullable ZDBaseException e) {
                Toast.makeText(TicketListActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                onAction(e.getMessage());
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
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() == 0) {
                finish();
            }
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

    private void showDetailViews(String viewId) {


        if (getSupportFragmentManager().findFragmentByTag("ticketListView") == null) {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            ticketList = ZDTicketListFragment.Companion.create(mOrgId);
            ArrayList<String> deptList = new ArrayList<>();
            deptList.add(departmentId);
            ZDTicketListConfig config = new ZDTicketListConfig.ZDBuilder().setViewId(viewId).sortBy("-recentThread").setDepartmentIds(deptList).setPageSize(99).build();// No I18N
            ticketList.setConfig(config);
            fm.add(R.id.wrapper, ticketList, "ticketListView");// No I18N
            fm.addToBackStack("ticketListView");// No I18N
            fm.commit();
        } else {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            ticketList = (ZDTicketListFragment) getSupportFragmentManager().findFragmentByTag("ticketListView");// No I18N
            fm.show(ticketList);
            fm.commit();
        }
    }

    @Override
    public void onAction(@NotNull String status) {
        mLoader.setVisibility(View.GONE);
    }

    @Override
    public void onClick(@NotNull ZDTicket ticket) {
        Intent intent = new Intent(TicketListActivity.this, TicketDetailActivity.class);
        intent.putExtra("ticketId", ticket.getId()); // No I18N
        intent.putExtra("departmentId", departmentId); // No I18N
        intent.putExtra("orgId", mOrgId);// No I18N
        intent.putExtra("ticket", ticket);// No I18N
        startActivityForResult(intent, 1);
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1) {
                ZDTicket ticket =  data.getParcelableExtra("ticket");// No I18N
                if(ticket!=null) {
                    ticketList.updateTicketList( ticket);
                }
            }
        }
    }

    @Override
    public void onListItemUpdated(@NotNull ZDTicket ticket) {
        ticketList.updateTicketList(ticket);
    }

    @Override
    public void onListItemDeleted(@NotNull ZDTicket ticket) {
        ticketList.deleteTicketList(ticket);

    }
}