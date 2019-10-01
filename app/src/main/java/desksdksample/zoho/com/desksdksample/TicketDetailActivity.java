package desksdksample.zoho.com.desksdksample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zoho.desk.provider.agents.ZDAgentDetail;
import com.zoho.desk.provider.threads.ZDConversation;
import com.zoho.desk.provider.threads.ZDThreadDetail;
import com.zoho.desk.provider.threads.ZDTicketConversationComment;
import com.zoho.desk.provider.tickets.ZDTicket;
import com.zoho.desk.provider.tickets.ZDTicketDetail;
import com.zoho.desk.ticket.ticketdetail.ZDTicketDetailFragment;
import com.zoho.desk.ticket.utils.TicketActions;
import com.zoho.desk.ticket.utils.ZDTicketDetailConfig;


public class TicketDetailActivity extends AppCompatActivity implements ZDTicketDetailFragment.Companion.ZDTicketDetailInterface {
    private String mTicketId;
    private String mDepartmentId;
    private String mOrgId;
    private ZDTicket ticket;
    ZDTicketDetailFragment zdTicketDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        mTicketId = getIntent().getExtras().getString("ticketId"); // No I18N
        mDepartmentId = getIntent().getExtras().getString("departmentId"); // No I18N
        mOrgId = getIntent().getExtras().getString("orgId");// No I18N
        ticket = getIntent().getParcelableExtra("ticket");// No I18N
        showDetailViews();
    }

    private void showDetailViews() {
        if (getSupportFragmentManager().findFragmentByTag("ticketDetailView") == null) {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            zdTicketDetailFragment = ZDTicketDetailFragment.Companion.create(mOrgId, mDepartmentId, mTicketId);
            ZDTicketDetailConfig config = new ZDTicketDetailConfig.ZDBuilder().setConversationPageSize(40).setTicket(ticket).build();
            zdTicketDetailFragment.setConfig(config);
            fm.add(R.id.wrapper, zdTicketDetailFragment, "ticketDetailView");// No I18N
            fm.addToBackStack("ticketDetailView");// No I18N
            fm.commit();
        } else {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            zdTicketDetailFragment = (ZDTicketDetailFragment) getSupportFragmentManager().findFragmentByTag("ticketDetailView");// No I18N
            fm.show(zdTicketDetailFragment);
            fm.commit();
        }
    }

    private void setResultBack() {
        ZDTicketDetail ticket = zdTicketDetailFragment.getTicket();
        Intent bundle = new Intent();
        bundle.putExtra("ticket", ticket);// No I18N
        setResult(Activity.RESULT_OK, bundle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultBack();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            finish();
        }
    }

    @Override
    public void onAction(@NonNull String action, @NonNull ZDTicketDetail ticket, @Nullable ZDConversation conversation, @Nullable ZDAgentDetail currentAgentDetail) {
        if (!action.equals(TicketActions.DISCARD_DRAFT.getType()) && !action.equals(TicketActions.SEND_DRAFT.getType()) && !action.equals(TicketActions.DELETE_COMMENT.getType()) && !action.equals(TicketActions.TICKET_SPAMMED.getType()) && !action.equals(TicketActions.TICKET_CLOSED.getType()) && !action.equals(TicketActions.TICKET_DELETED.getType())) {
            Gson gson = new Gson();
            Intent intent = new Intent(TicketDetailActivity.this, ReplyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ticketId", mTicketId);// No I18N
            bundle.putString("departmentId", mDepartmentId);// No I18N
            bundle.putString("orgId", mOrgId);// No I18N
            bundle.putString("ticket", gson.toJson(ticket));// No I18N
            bundle.putString("actionType", action);// No I18N

            String name = "";
            if (currentAgentDetail != null) {
                if (currentAgentDetail.getFirstName() != null){
                    name += currentAgentDetail.getFirstName()+" ";
                }
                if (currentAgentDetail.getLastName() != null){
                    name += currentAgentDetail.getLastName();
                }
            }
            bundle.putString("currentAgentName", name);// No I18N
            if (conversation != null) {
                if (conversation instanceof ZDThreadDetail) {
                    bundle.putParcelable("thread", conversation);// No I18N
                } else if (conversation instanceof ZDTicketConversationComment) {
                    bundle.putParcelable("comment", conversation);// No I18N
                }
            }
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
            overridePendingTransition(0, 0);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1) {
                zdTicketDetailFragment.updateConversationList((ZDConversation) data.getParcelableExtra("conversation"));// No I18N
            }
        }
    }


}



