package com.example.itprojects.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itprojects.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

/**
 * This activity allows the user to type in his email and send a link to remake new password.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-08-31
 */

public class ForgetpwdActivity extends AppCompatActivity {

    /**
     * Layout component variable
     */

    // User types in his email here
    private EditText mForgetEmailET;

    // Send link
    private Button mSendEmailBtn;

    // Show progress bar when sending retrieve email
    private ProgressBar mSendPB;

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);

        // Hide the actionbar in this activity
        getSupportActionBar().hide();

        // Use Firebase to get authentication
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Get each component ID
        mSendPB = findViewById(R.id.pb_forget);
        mForgetEmailET = findViewById(R.id.forget_email);

        // Click to send the retrieve email
        mSendEmailBtn = findViewById(R.id.btn_send_mail);

        // Onclick to send retrieve password email to user if user exist
        mSendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Activate the progress bar
                mSendPB.setVisibility(View.VISIBLE);

                // Send by Firebase authentication system
                mAuth.sendPasswordResetEmail(mForgetEmailET.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // Deactivate the progress bar
                                mSendPB.setVisibility(View.GONE);

                                // Retrieve message successfully send
                                if (task.isSuccessful()) {

                                    // Successful message is shown
                                    Toasty.success(ForgetpwdActivity.this,
                                            "验证邮件已发送", Toast.LENGTH_SHORT).show();

                                    // The page redirects to the Login page
                                    Intent LoginIntent = new Intent(
                                            ForgetpwdActivity.this,
                                            LoginActivity.class);
                                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(LoginIntent);
                                }
                                else {
                                    // The typed email does not exist in our Firebase server
                                    Toasty.warning(ForgetpwdActivity.this,
                                            "此用户不存在", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
