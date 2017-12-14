package com.example.noushad.shopaholic.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.event.ErrorEvent;
import com.example.noushad.shopaholic.event.RegisterEvent;
import com.example.noushad.shopaholic.fragment.FilterOptionsFragment;
import com.example.noushad.shopaholic.utils.FirebaseService;
import com.example.noushad.shopaholic.utils.TagManager;
import com.vstechlab.easyfonts.EasyFonts;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class RegisterActivity extends AppCompatActivity implements FilterOptionsFragment.OnFragmentInteractionListener {

    private EditText mNameEditText;
    private TextView mLocationText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private File mFile;
    private ConstraintLayout mLayout;

    private static final String TAG = "RegisterActivity";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        initializeViews();


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformation();

            }
        });

    }

    private void initializeViews() {

        mNameEditText = (EditText) findViewById(R.id.account_name);
        mLocationText = findViewById(R.id.account_location);
        mEmailEditText = (EditText) findViewById(R.id.account_email);
        mPhoneEditText = (EditText) findViewById(R.id.account_phone);
        mPasswordEditText = (EditText) findViewById(R.id.account_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.account_confirm_password);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setTypeface(EasyFonts.caviarDreams(this));
        mLayout = findViewById(R.id.location_fragment_place_holder);

        mLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.location_fragment_place_holder,
                        FilterOptionsFragment.newInstance(TagManager.LOCATION_FRAGMENT_TAG), null);
                fragmentTransaction.commit();
            }
        });
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();

        } else {
            super.onBackPressed();
        }
    }


    private void getInformation() {

        String name = "";
        String email = "";
        String phone = "";
        String location = "";
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();
        name = mNameEditText.getText().toString();
        email = mEmailEditText.getText().toString();
        phone = mPhoneEditText.getText().toString();
        location = mLocationText.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                location.isEmpty() || location.equals("Location")) {

            Toast.makeText(this, "No Fields can be empty!! ", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword) && (!password.isEmpty() || !confirmPassword.isEmpty())) {

            Toast.makeText(this, "Passwords Do not match", Toast.LENGTH_SHORT).show();

        } else if (password.length() < 8) {
            Toast.makeText(this, "Passwords Must Be 8 characters long", Toast.LENGTH_SHORT).show();
        } else {

            registerUser(name, email, password, phone, location);
        }

    }

    private void registerUser(String name, String email, String password, String phone, String location) {
        mProgressDialog.setMessage("Signing Up...");
        mProgressDialog.show();
        FirebaseService service = new FirebaseService();
        service.createUser(email, password, name, phone, location);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterEvent(RegisterEvent event) {
        mProgressDialog.dismiss();
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        mProgressDialog.dismiss();
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();

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

    @Override
    public void onFragmentInteraction(String text, String tag) {
        mLayout.setVisibility(View.GONE);

        mLocationText.setText(text);
    }
}
