package com.example.mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile.R;
import com.example.mobile.config.SessionManager;
import com.example.mobile.model.WalletCreate;
import com.example.mobile.service.adapter.EditMemberAdapter;

public class EditMembersActivity extends BaseActivity {

    Button goToSendInvitationBtn;
    RecyclerView membersRv;
    TextView walletNameTv;
    EditMemberAdapter editMemberAdapter;
    String accessToken;
    Boolean owner;
    WalletCreate walletCreate;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_members);

        session = new SessionManager(this);

        goToSendInvitationBtn = findViewById(R.id.send_invitations_btn);
        membersRv = findViewById(R.id.browse_members_add_rv);
        walletNameTv = findViewById(R.id.name_wallet_tv);

        walletNameTv.setText(getIntent().getStringExtra("name"));

        accessToken = getIntent().getStringExtra("accessToken");
        walletCreate = getIntent().getParcelableExtra("wallet");
        walletNameTv.setText(walletCreate.getName());

        owner = walletCreate.getOwner().equals(session.getUserDetails().get(SessionManager.KEY_LOGIN));

        editMemberAdapter = new EditMemberAdapter(this, walletCreate.getUserList(), accessToken, walletCreate.getId(), owner);
        membersRv.setLayoutManager(new LinearLayoutManager(EditMembersActivity.this));
        membersRv.setAdapter(editMemberAdapter);

        goToSendInvitationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditMembersActivity.this, AddMemberActivity.class);
            startActivity(intent);
            finish();
        });
    }
}