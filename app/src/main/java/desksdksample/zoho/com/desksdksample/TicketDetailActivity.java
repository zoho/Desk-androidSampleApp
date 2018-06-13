package desksdksample.zoho.com.desksdksample;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.zoho.desksdkui.ticketdetail.ZDTicketDetailFragment;
import com.zoho.desksdkui.utils.ZDTicketDetailConfig;

public class TicketDetailActivity extends AppCompatActivity {
private Long mTicketId;
private Long mOrgId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        mTicketId = getIntent().getExtras().getLong("ticketId"); // No I18N
        mOrgId = getIntent().getExtras().getLong("orgId");// No I18N
        showDetailView();
    }

    private void showDetailView() {
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        ZDTicketDetailFragment zdTicketDetailFragment =new  ZDTicketDetailFragment();
        ZDTicketDetailConfig config = new ZDTicketDetailConfig.ZDBuilder().setConversationPageSize(40).build();
        zdTicketDetailFragment.initView(mOrgId, mTicketId, config);
        fm.add(R.id.wrapper, zdTicketDetailFragment, "ticketDetailView");// No I18N
        fm.commit();
    }
}
