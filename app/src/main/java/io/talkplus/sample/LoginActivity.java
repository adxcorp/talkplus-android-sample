package io.talkplus.sample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import io.talkplus.TalkPlus;
import io.talkplus.entity.user.TPUser;
import io.talkplus.sample.base.BaseActivity;
import io.talkplus.util.CommonUtil;

public class LoginActivity extends BaseActivity {
    private EditText mEditTextUserId;
    private EditText mEditTextNickname;

    private Button mBtnLogin;

    private static final String KEY_USER_ID = "KeyUserId";
    private static final String KEY_USER_NAME = "KeyUserName";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mEditTextUserId = findViewById(R.id.editTextId);
        mEditTextNickname = findViewById(R.id.editTextNickname);

        String userId = CommonUtil.getProperty(this, KEY_USER_ID, "");
        String userName = CommonUtil.getProperty(this, KEY_USER_NAME, "");

        mEditTextUserId.setText(userId);
        mEditTextNickname.setText(userName);

        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTalkPlus();
            }
        });

        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userName)) {
            loginTalkPlus();
        }
    }

    private void loginTalkPlus() {
        String userId = mEditTextUserId.getText().toString();
        String userName = mEditTextNickname.getText().toString();

        CommonUtil.setProperty(this, KEY_USER_ID, userId);
        CommonUtil.setProperty(this, KEY_USER_NAME, userName);

        showProgressDialog();

        TalkPlus.loginWithAnonymous(userId, userName, null, new TalkPlus.CallbackListener<TPUser>() {
            @Override
            public void onSuccess(TPUser user) {
                dismissProgressDialog();

                showToast("로그인에 성공하였습니다.");

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                dismissProgressDialog();

                showToast("로그인에 실패하였습니다.");
            }
        });
    }

}
