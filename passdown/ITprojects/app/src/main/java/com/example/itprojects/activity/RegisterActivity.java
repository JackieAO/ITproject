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

import com.example.itprojects.bean.User;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.SecureRandom;

import es.dmoral.toasty.Toasty;

/**
 * This activity is the Registration page.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-08-31
 */

public class RegisterActivity extends AppCompatActivity {
    // Generate random string
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();

    /**
     * Layout component variable
     */

    // User typed in email
    private EditText mEmailET;

    // User typed in password
    private EditText mPasswordET;

    // User typed in second password
    private EditText mRepwdET;

    // User typed in surname
    private EditText mSurnameET;

    // User typed in firstname
    private EditText mFirstnameET;

    // Layout which wraps the password edit text
    private TextInputLayout mPasswordTIL;
    private TextInputLayout mRepwdTIL;

    // Click to send register information into Google Firebase
    private Button mRegisterBtn;
    private TextView mBackToLoginTV;

    // Progress bar shown on processing register data
    private ProgressBar mRegPB;

    /**
     * Firebase variable
     */

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout
        setContentView(R.layout.activity_register);

        // Hide the actionbar in this activity
        getSupportActionBar().hide();

        // Use Firebase get authentication and database
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get each component ID
        mEmailET = findViewById(R.id.reg_email_2);
        mPasswordET = findViewById(R.id.reg_pwd_2);
        mRepwdET = findViewById(R.id.reg_confirm_pwd_2);
        mPasswordTIL = findViewById(R.id.reg_pwd);
        mRepwdTIL = findViewById(R.id.reg_confirm_pwd);
        mSurnameET = findViewById(R.id.reg_sname_2);
        mFirstnameET = findViewById(R.id.reg_gname_2);
        mRegisterBtn = findViewById(R.id.btn_reg);
        mBackToLoginTV = findViewById(R.id.btn_back_to_log);
        mRegPB = findViewById(R.id.pb_reg);

        // Fix font apply issue under password mode
        mPasswordTIL.setTypeface(ResourcesCompat.getFont(this, R.font.b));
        mRepwdTIL.setTypeface(ResourcesCompat.getFont(this, R.font.b));

        // Back to login activity
        mBackToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent (RegisterActivity.this,
                        LoginActivity.class);
                LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(LoginIntent);
            }
        });

        // Avoid toggle and error icon overlap
        mPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPasswordTIL.setPasswordVisibilityToggleEnabled(true);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Detect Text change
        mRepwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRepwdTIL.setPasswordVisibilityToggleEnabled(true);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Registration onclick event
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    /**
     * This function is for registration event.
     */

    private void startRegister(){
        // Get input text from each field
        final String surname = mSurnameET.getText().toString().trim();
        final String firstname = mFirstnameET.getText().toString().trim();
        final String email = mEmailET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();
        String repwd = mRepwdET.getText().toString().trim();

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

            mPasswordTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mPasswordTIL.setPasswordVisibilityToggleEnabled(false);
            mPasswordET.setError("密码不能为空");
            mPasswordET.requestFocus();
            return;
        }
        // Password have to be recheck
        if (repwd.isEmpty()) {
            /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */

            mRepwdTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mRepwdTIL.setPasswordVisibilityToggleEnabled(false);
            mRepwdET.setError("请进行核对密码");
            mRepwdET.requestFocus();
            return;
        }
        // Incorrect password format
        if (password.length() < 6) {
            /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */

            mPasswordTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mPasswordTIL.setPasswordVisibilityToggleEnabled(false);
            mPasswordET.setError("密码格式错误，6到16个字符之内");
            mPasswordET.requestFocus();
            return;
        }
        // Incorrect recheck format
        if (repwd.length() < 6) {

             /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */

            mRepwdTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mRepwdTIL.setPasswordVisibilityToggleEnabled(false);
            mRepwdET.setError("密码格式错误，6到16个字符之内");
            mRepwdET.requestFocus();
            return;
        }

        // Incorrect password format
        if (password.length() > 16) {

            /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */

            mPasswordTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mPasswordTIL.setPasswordVisibilityToggleEnabled(false);
            mPasswordET.setError("密码格式错误，6到16个字符之内");
            mPasswordET.requestFocus();
            return;
        }

        // Incorrect recheck format
        if (repwd.length() > 16) {
            /*
             In order to solve toggle overlap problem, double disabled is needed
             1st disable, to ensure toggle is disable in password visibility
            */

            mRepwdTIL.setPasswordVisibilityToggleEnabled(false);

            // 2nd disable, disable in showing icon
            mRepwdTIL.setPasswordVisibilityToggleEnabled(false);
            mRepwdET.setError("密码格式错误，6到16个字符之内");
            mRepwdET.requestFocus();
            return;
        }

        // Last name field cannot be empty
        if (surname.isEmpty()) {
            mSurnameET.setError("姓不能为空");
            mSurnameET.requestFocus();
            return;
        }

        // First name field cannot be empty
        if (firstname.isEmpty()) {
            mFirstnameET.setError("名不能为空");
            mFirstnameET.requestFocus();
            return;
        }

        // Inconsistent password and the password check
        if (!password.equals(repwd)) {
            Toasty.warning(this, "两次密码输入不一致，请重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Let progress bar visible
        mRegPB.setVisibility(View.VISIBLE);

        // Passed all conditions, ready for upload register information
        if(!TextUtils.isEmpty(surname) && !TextUtils.isEmpty(firstname) && !TextUtils.isEmpty(email)
                 && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(repwd)){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // Progress bar set to finish status
                    mRegPB.setVisibility(View.GONE);

                    if(task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();

                        // Create user in database
                        DatabaseReference current_user_db = mDatabaseRef.child(user_id);
                        // Gengerate invitation code
                        String code;
                        while (true) {
                            code = generateRandomInviteCode();
                            if (checkCodeNotRepeat(code)) {
                                break;
                            }
                        }
                        // Initialize the user
                        // Default the workspace name
                        User users = new User(email,"default",surname, firstname,
                                firstname+"的传家库",code);

                        // Set user in database
                        current_user_db.setValue(users);

                        // Get firebase authentication
                        FirebaseUser userAuth = mAuth.getCurrentUser();

                        // Send authentication email
                        userAuth.sendEmailVerification();
                        Toasty.success(RegisterActivity.this, "注册成功!\n" +
                                "已发送邮箱验证", Toast.LENGTH_SHORT).show();

                        // Update the token, unique for each Android phone logged in by the user
                        final String uid = FirebaseAuth.getInstance().getUid();

                        FirebaseInstanceId.getInstance().
                                getInstanceId().addOnCompleteListener(
                                new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        String deviceToken = task.getResult().getToken();
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("device_Token")
                                                .setValue(deviceToken);
                                    }
                                }
                        );


                        // Redirect to login page
                        Intent mainIntent = new Intent(RegisterActivity.this,
                                LoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                    else {

                        // Registration terminate, the email already registered the app
                        Toasty.warning(RegisterActivity.this, "该邮箱已注册",
                                Toast.LENGTH_SHORT).show();
                        Intent LoginIntent = new Intent(RegisterActivity.this,
                                LoginActivity.class);
                        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(LoginIntent);
                    }
                }
            });
        }
    }

    // Source: https://www.mkyong.com/java/java-how-to-generate-a-random-string/
    public static String generateRandomInviteCode() {
        // Invite code length
        int length = 6;

        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            //System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);
        }

        return sb.toString();
    }

    // Return false if two codes are identical
    public static boolean checkCodeNotRepeat(final String code) {
        final boolean[] status = {true};

        FirebaseDatabase.getInstance().getReference("Users/").
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    String otherUserCode = postSnapShot.child("invited_Code").getValue().toString();
                    if (code.equals(otherUserCode)) {
                        status[0] =  false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return status[0];
    }
}
