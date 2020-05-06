package com.example.itprojects.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.itprojects.base.MainActivity;
import com.example.itprojects.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import es.dmoral.toasty.Toasty;

/**
 * This activity allows the user to log in or jump to register activity or
 * recover password activity.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-02
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Layout component variable
     */
    private ProgressBar mLogInPB;
    private EditText mEmailET;
    private EditText mPwdET;
    private TextInputLayout mPwdTIL;
    private Button mLoginBtn;
    private Button mRegisterBtn;
    private TextView mForgetPwdTV;

    /**
     * Firebase variable
     */
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsersRef;

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide the actionbar in this activity
        getSupportActionBar().hide();

        // Use Firebase get authentication and database
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get each component's ID
        mLogInPB = findViewById(R.id.pb_log);
        mEmailET = findViewById(R.id.login_email_2);
        mPwdET = findViewById(R.id.login_pwd_2);
        mPwdTIL = findViewById(R.id.login_pwd);
        mLoginBtn = findViewById(R.id.btn_login);
        mRegisterBtn = findViewById(R.id.btn_jump_to_reg);
        mForgetPwdTV = findViewById(R.id.forgetPwd);

        // Fix font apply issue under password mode
        mPwdTIL.setTypeface(ResourcesCompat.getFont(this, R.font.b));

        // Avoid toggle and error icon overlap
        mPwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // At default set it to "true"
                mPwdTIL.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Onclick Login
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        // Register button onclick change to register activity
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegisterIntent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(RegisterIntent);
            }
        });

        // Onclick change to forget password activity
        mForgetPwdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ForgetPwdIntent = new Intent(LoginActivity.this,
                        ForgetpwdActivity.class);
                ForgetPwdIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ForgetPwdIntent);
            }
        });
    }

    /**
     * Validate required info before do authentication, raise any warning if applicable.
     */

    private void checkLogin() {
        final String email = mEmailET.getText().toString().trim();
        String password = mPwdET.getText().toString().trim();

        // Check required field is filled in and match required format
        if (email.isEmpty()) {
            // Email filed cannot be empty
            mEmailET.setError("邮箱地址不能为空");
            mEmailET.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Email filed input format incorrect
            mEmailET.setError("请输入正确的邮箱地址");
            mEmailET.requestFocus();
            return;
        }

        // Password field cannot be empty
        if (password.isEmpty()) {
            /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */
            mPwdTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mPwdTIL.setPasswordVisibilityToggleEnabled(false);
            mPwdET.setError("密码不能为空");
            mPwdET.requestFocus();
            return;
        }

        if (password.length() < 6) {

            /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */
            mPwdTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mPwdTIL.setPasswordVisibilityToggleEnabled(false);
            mPwdET.setError("密码格式错误，6到16个字符之内");
            mPwdET.requestFocus();
            return;
        }

        // Pop up progress
        mLogInPB.setVisibility(View.VISIBLE);

        /*
         In Authentication state, check authentication status of user
         and give appropriate hint sign
        */
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mLogInPB.setVisibility(View.GONE);
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Change Toasty Text size --- Yat Yeung
                    Toasty.Config.getInstance().setTextSize(12).apply();

                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            // Update the token, unique for each Android phone logged in by the user
                            final String uid = FirebaseAuth.getInstance().getUid();
                            FirebaseInstanceId.getInstance().
                                    getInstanceId().addOnCompleteListener(
                                    new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            String deviceToken = task.getResult().getToken();
                                            mDatabaseUsersRef.child(uid).child("device_Token")
                                                    .setValue(deviceToken)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
//                                                Toasty.success(LoginActivity.this, "登陆成功！",
//                                                        Toast.LENGTH_SHORT).show();
                                                                Intent mainIntent = new Intent(LoginActivity.this,
                                                                        MainActivity.class);
                                                                mainIntent.putExtra("clicked_id",1);
                                                                startActivity(mainIntent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                        else {
                            user.sendEmailVerification();
                            Toasty.warning(LoginActivity.this, "请验证邮箱！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Query query = mDatabaseUsersRef.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Toasty.warning(LoginActivity.this,
                                            "您的账号或者密码有误!\n请重新输入", Toast.LENGTH_SHORT)
                                            .show();
                                }
                                else {
                                    Toasty.warning(LoginActivity.this,
                                            "您输入的账号不存在!\n请重新输入或注册新账户", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }


            });
        }
    }
}
