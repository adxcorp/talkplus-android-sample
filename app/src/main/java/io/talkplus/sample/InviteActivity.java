package io.talkplus.sample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.talkplus.sample.base.BaseActivity;

public class InviteActivity extends BaseActivity {
    private EditText mEditTextUserId1;
    private EditText mEditTextUserId2;
    private EditText mEditTextUserId3;
    private EditText mEditTextUserId4;
    private EditText mEditTextUserId5;

    private Button mBtnInvite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mEditTextUserId1 = findViewById(R.id.editTextUserId1);
        mEditTextUserId2 = findViewById(R.id.editTextUserId2);
        mEditTextUserId3 = findViewById(R.id.editTextUserId3);
        mEditTextUserId4 = findViewById(R.id.editTextUserId4);
        mEditTextUserId5 = findViewById(R.id.editTextUserId5);

        mBtnInvite = findViewById(R.id.btnInvite);

        mBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> array = new ArrayList<>();

                if (!TextUtils.isEmpty(mEditTextUserId1.getText().toString())) {
                    array.add(mEditTextUserId1.getText().toString());
                }

                if (!TextUtils.isEmpty(mEditTextUserId2.getText().toString())) {
                    array.add(mEditTextUserId2.getText().toString());
                }

                if (!TextUtils.isEmpty(mEditTextUserId3.getText().toString())) {
                    array.add(mEditTextUserId3.getText().toString());
                }

                if (!TextUtils.isEmpty(mEditTextUserId4.getText().toString())) {
                    array.add(mEditTextUserId4.getText().toString());
                }

                if (!TextUtils.isEmpty(mEditTextUserId5.getText().toString())) {
                    array.add(mEditTextUserId5.getText().toString());
                }

                Intent intent = new Intent();
                intent.putStringArrayListExtra("userIds", array);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }
}
