package desksdksample.zoho.com.desksdksample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.zoho.desk.provider.attachments.ZDAttachment;
import com.zoho.desk.provider.exceptions.ZDBaseException;
import com.zoho.desk.provider.threads.ZDThreadDetail;
import com.zoho.desk.provider.threads.ZDTicketConversationComment;
import com.zoho.desk.provider.tickets.ZDTicketDetail;
import com.zoho.desk.attachment.ZDAttachmentEditorFragment;
import com.zoho.desk.commenteditor.ZDCommentFragment;
import com.zoho.desk.attachment.utils.SIZE;
import com.zoho.desk.replyeditor.ZDReplyConfig;
import com.zoho.desk.replyeditor.ZDReplyFragment;
import com.zoho.desk.ticket.utils.TicketActions;
import com.zoho.desk.attachment.utils.ZDAttachmentConfig;


import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity implements ZDCommentFragment.Companion.ZDCommentInterface, ZDReplyFragment.Companion.ZDReplyInterface, ZDAttachmentEditorFragment.Companion.ZDAttachmentInterface {
    private String mTicketId;
    private String mOrgId;
    private String mDepartmentId;
    private String currentAgentName ;
    private ZDTicketConversationComment comment;
    private ZDTicketDetail ticketDetail;
    private ZDThreadDetail thread;
    private String actionType = "";
    private Gson gson = new Gson();
    ZDCommentFragment commentView;
    ZDReplyFragment replyView;
    ZDAttachmentEditorFragment attachmentView;
    private RelativeLayout wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        mOrgId = getIntent().getExtras().getString("orgId");// No I18N
        mTicketId = getIntent().getExtras().getString("ticketId"); // No I18N
        currentAgentName = getIntent().getExtras().getString("currentAgentName"); // No I18N
        mDepartmentId = getIntent().getExtras().getString("departmentId"); // No I18N
        String ticketString = getIntent().getExtras().getString("ticket");// No I18N
        actionType = getIntent().getExtras().getString("actionType");// No I18N
        comment = getIntent().getExtras().getParcelable("comment"); // No I18N
        ticketDetail = gson.fromJson(ticketString, ZDTicketDetail.class);
        thread = getIntent().getParcelableExtra("thread");// No I18N


        wrapper = findViewById(R.id.wrapper);
        if (actionType.equals(TicketActions.EDIT_COMMENT.getType())||actionType.equals(TicketActions.COMMENT.getType())) {
            showComment();
        } else {
            showReplyView();
        }
    }

    private void showReplyView() {
        wrapper.setVisibility(View.VISIBLE);
        if (getSupportFragmentManager().findFragmentByTag("replyView") == null) {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            ZDReplyConfig config;
            if (thread == null) {
                config = new ZDReplyConfig.ZDBuilder().setTicket(ticketDetail).build();
            } else {
                config = new ZDReplyConfig.ZDBuilder().setTicket(ticketDetail).setThread(thread).setThreadId(thread.getId()).build();
            }
            replyView = ZDReplyFragment.Companion.create(mOrgId, mDepartmentId, mTicketId, actionType,currentAgentName);
            replyView.setConfig(config);
            fm.add(R.id.wrapper, replyView, "replyView")// No I18N
                    .addToBackStack("replyView");// No I18N
            fm.commit();
        } else {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            replyView = (ZDReplyFragment) getSupportFragmentManager().findFragmentByTag("replyView");// No I18N
            fm.show(replyView);
            fm.commit();
        }
    }


    private void showComment() {
        wrapper.setVisibility(View.VISIBLE);
        if (getSupportFragmentManager().findFragmentByTag("commentView") == null) {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            commentView = ZDCommentFragment.Companion.create(mOrgId, mDepartmentId, mTicketId);
            if(comment!=null) {
                commentView.editComment(comment);
            }
            fm.add(R.id.wrapper, commentView, "commentView")// No I18N
                    .addToBackStack("commentView");// No I18N
            fm.commit();
        } else {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            commentView = (ZDCommentFragment) getSupportFragmentManager().findFragmentByTag("commentView");// No I18N
            fm.show(commentView);
            fm.commit();
        }
    }

    @Override
    public void openAttachment(@NonNull ArrayList<ZDAttachment> attachmentList) {
        if (getSupportFragmentManager().findFragmentByTag("attachmentView") == null) {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            ZDAttachmentConfig config = new ZDAttachmentConfig.ZDBuilder().setFileCount(25).setSizeIn(SIZE.MB).setSingleFileSize(10).setDoneVisibility(true).setTotalFileSize(200).build();
            attachmentView = ZDAttachmentEditorFragment.Companion.create(mOrgId);
            attachmentView.setAttachmentList(attachmentList);
            attachmentView.setConfig(config);
            fm.add(R.id.wrapper, attachmentView, "attachmentView")// No I18N
                    .addToBackStack("attachmentView")// No I18N
                    .commit();
        } else {
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            attachmentView = (ZDAttachmentEditorFragment) getSupportFragmentManager().findFragmentByTag("attachmentView");// No I18N
            fm.show(commentView);
            fm.commit();
        }
    }

    @Override
    public void setResultBack(@NonNull ZDTicketConversationComment comment) {
        Intent bundle = new Intent();
        bundle.putExtra("conversation", comment);// No I18N
        setResult(Activity.RESULT_OK, bundle);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (replyView!=null&&replyView.isContentChanged()) {
            replyView.saveAsDraft();
        } else {
            super.onBackPressed();
        }
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            finish();
        }
    }

    @Override
    public void onError(@NonNull ZDBaseException exception) {

    }

    @Override
    public void onDismiss() {
        onBackPressed();
    }

    @Override
    public void setAttachmentLists(@NonNull ArrayList<ZDAttachment> attachmentList) {
        getSupportFragmentManager().popBackStack("attachmentView", FragmentManager.POP_BACK_STACK_INCLUSIVE); //No I18N
        if (commentView != null) {
            commentView.setAttachmentList(attachmentList);
        } else if (replyView != null) {
            replyView.setAttachmentList(attachmentList);
        }
    }

    @Override
    public void setResultBack(@NonNull ZDThreadDetail thread) {
        Intent bundle = new Intent();
        bundle.putExtra("conversation", thread);// No I18N
        setResult(Activity.RESULT_OK, bundle);
        onBackPressed();
    }
}