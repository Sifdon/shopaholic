package com.example.noushad.shopaholic.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.event.SignInEvent;
import com.example.noushad.shopaholic.utils.FirebaseService;
import com.example.noushad.shopaholic.utils.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vstechlab.easyfonts.EasyFonts;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mCreateAccountTextView;
    private TextView mForgetPasswordTextView;
    private String mEmail;
    private String mPassword;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initializeViews();
        mAuth = FirebaseAuth.getInstance();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmail = mEmailEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                if (mEmail.isEmpty() || mPassword.isEmpty()) {
                    if (mEmail.isEmpty()) {
                        emailInputLayout.setErrorEnabled(true);
                        emailInputLayout.setError("Email cannot be empty");
                    } else {
                        emailInputLayout.setErrorEnabled(false);
                    }
                    if (mPassword.isEmpty()) {
                        passwordInputLayout.setErrorEnabled(true);
                        passwordInputLayout.setError("Password cannot be empty");
                    } else {
                        passwordInputLayout.setErrorEnabled(false);
                    }
                } else {
                    passwordInputLayout.setErrorEnabled(false);
                    emailInputLayout.setErrorEnabled(false);
                    userLogin(mEmail, mPassword);
                }
            }
        });
        mPasswordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mEmail = mEmailEditText.getText().toString();
                    mPassword = mPasswordEditText.getText().toString();
                    if (mEmail.isEmpty() || mPassword.isEmpty()) {
                        if (mEmail.isEmpty()) {
                            emailInputLayout.setErrorEnabled(true);
                            emailInputLayout.setError("Email cannot be empty");
                        } else {
                            emailInputLayout.setErrorEnabled(false);
                        }
                        if (mPassword.isEmpty()) {
                            passwordInputLayout.setErrorEnabled(true);
                            passwordInputLayout.setError("Password cannot be empty");
                        } else {
                            passwordInputLayout.setErrorEnabled(false);
                        }
                    } else {
                        passwordInputLayout.setErrorEnabled(false);
                        emailInputLayout.setErrorEnabled(false);
                        userLogin(mEmail, mPassword);
                    }
                    return true;
                }
                return false;
            }
        });

        mCreateAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mForgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initializeViews() {
        progressDialog = new ProgressDialog(this);
        emailInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        mEmailEditText = (EditText) findViewById(R.id.email_input);

        mPasswordEditText = (EditText) findViewById(R.id.password_input);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setTypeface(EasyFonts.caviarDreams(this));
        mCreateAccountTextView = (TextView) findViewById(R.id.create_account);
        mCreateAccountTextView.setTypeface(EasyFonts.caviarDreams(this));
        mForgetPasswordTextView = (TextView) findViewById(R.id.tv_forgetPass);
        mForgetPasswordTextView.setTypeface(EasyFonts.caviarDreams(this));

        TextView textView = (TextView) findViewById(R.id.tv_signIn);
    }

    private void userLogin(String email, String password) {

        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        String token = SharedPrefManager.getInstance(this).getDeviceToken();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                SharedPrefManager.getInstance(LoginActivity.this).setUserId(user.getUid());
                                Toast.makeText(LoginActivity.this, "Logged In.",
                                        Toast.LENGTH_SHORT).show();
                                new FirebaseService().getUserData(user.getUid());

                            }else{
                                Toast.makeText(LoginActivity.this, "Please Verify Your Email",
                                        Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }

                        } else {

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private static final String TAG = "LoginActivity";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(SignInEvent event) {
        SharedPrefManager.getInstance(this).userOwnDataUpdate(event.getUserInfo());
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}
