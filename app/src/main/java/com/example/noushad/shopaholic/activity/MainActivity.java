package com.example.noushad.shopaholic.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.event.FilterOptionEvent;
import com.example.noushad.shopaholic.fragment.FilterOptionsFragment;
import com.example.noushad.shopaholic.fragment.ListFragment;
import com.example.noushad.shopaholic.fragment.MyOffersFragment;
import com.example.noushad.shopaholic.fragment.OfferPostingFragment;
import com.example.noushad.shopaholic.fragment.OfferViewFragment;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.utils.ImageUtils;
import com.example.noushad.shopaholic.utils.PaginationAdapterCallback;
import com.example.noushad.shopaholic.utils.SharedPrefManager;
import com.example.noushad.shopaholic.utils.TagManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ListFragment.OnItemSelectedInterface, MyOffersFragment.OnItemSelectedInterface, PaginationAdapterCallback, FilterOptionsFragment.OnFragmentInteractionListener {

    private static final int PICK_IMAGE = 1;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static final int CREATE_NEW = 1;
    public static final int REPLACE = 2;
    public static final String OFFER_POSTING_FRAGMENT = "blog_creation_fragment";
    public static final String OFFER_VIEW_FRAGMENT = "blog_view_fragment";
    public static final String LIST_FRAGMENT = "list_fragment";
    public static final String RETRIEVE_FRAGMENT = "retrieve fragment";
    public static final String MY_OWN_POST_FRAGMENT = "own post fragment";
    public String CURRENT_FRAGMENT_TAG = LIST_FRAGMENT;
    private ImageView mNavUserProfileImage;
    private File mFile;
    private TextView mDiscountText;
    private TextView mPlaceText;
    private TextView mCategoryText;
    private AlertDialog filterDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        setNavigationViewListner();
        initializeViews();
        if (!SharedPrefManager.getInstance(this).isLoggedIn())
            hideMenuOptions();
        Fragment savedFragment = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT_TAG);
        if (savedFragment == null)
            startFragment(new ListFragment(), CREATE_NEW);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    private void hideMenuOptions() {
        NavigationView navigationView = findViewById(R.id.blog_navigation);
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.my_account).setVisible(false);
        navMenu.findItem(R.id.post_offer).setVisible(false);
        navMenu.findItem(R.id.my_offers).setVisible(false);
        navMenu.findItem(R.id.action_logout).setVisible(false);
    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            mFile = ImageUtils.getImageFile(this, data.getData(), mNavUserProfileImage);
//            WebOperations.updateUserPhoto(MainActivity.this, "PROFILE PICTURE", mFile);
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.blog_navigation);
        navigationView.setNavigationItemSelectedListener(this);
        final View view = navigationView.getHeaderView(0);
    }

    public void startFragment(Fragment fragment, int command) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (command == CREATE_NEW)
            fragmentTransaction.add(R.id.placeHolder, fragment, CURRENT_FRAGMENT_TAG);
        else {
            fragmentTransaction.replace(R.id.placeHolder, fragment, CURRENT_FRAGMENT_TAG).addToBackStack(null);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter:
                showFilterDialog();
                break;
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.filter_dialog, null);
        dialogBuilder.setView(dialogView);
        mPlaceText = dialogView.findViewById(R.id.filter_location_text);
        mCategoryText = dialogView.findViewById(R.id.filter_categories_text);
        mDiscountText = dialogView.findViewById(R.id.filter_discount_text);

        mPlaceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions(TagManager.LOCATION_FRAGMENT_TAG);
            }
        });
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions(TagManager.CATEGORY_FRAGMENT_TAG);
            }
        });
        mDiscountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions(TagManager.DISCOUNT_FRAGMENT_TAG);
            }
        });
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        filterDialog = dialogBuilder.create();
        filterDialog.show();
        filterDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        filterDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));


    }

    private void showOptions(String tag) {
        filterDialog.dismiss();
        startFragment(FilterOptionsFragment.newInstance(tag), REPLACE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                CURRENT_FRAGMENT_TAG = LIST_FRAGMENT;
                startFragment(new ListFragment(), REPLACE);
                break;
            case R.id.my_account:
                if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                    startActivity(new Intent(this, UserProfileActivity.class));
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "You Need To Login", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.post_offer:
                if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                    CURRENT_FRAGMENT_TAG = OFFER_POSTING_FRAGMENT;
                    startFragment(new OfferPostingFragment(), REPLACE);
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "You Need To Login", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.my_offers:
                if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                    CURRENT_FRAGMENT_TAG = MY_OWN_POST_FRAGMENT;
                    startFragment(new MyOffersFragment(), REPLACE);
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(this, "You Need To Login", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_sign_in:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                    Toast.makeText(getApplicationContext(), "Logging Out...", Toast.LENGTH_SHORT).show();
                    logout();
                }
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPrefManager.getInstance(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onListOfferSelected(Offer offer) {
        OfferViewFragment offerViewFragment = OfferViewFragment.newInstance(offer);
        startFragment(offerViewFragment, REPLACE);
    }

    @Override
    public void retryPageLoad() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onFragmentInteraction(String text, String tag) {
//        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();


        this.onBackPressed();

        switch (tag) {
            case TagManager.CATEGORY_FRAGMENT_TAG:

                EventBus.getDefault().post(new FilterOptionEvent(text, "categories"));
                break;
            case TagManager.DISCOUNT_FRAGMENT_TAG:

                EventBus.getDefault().post(new FilterOptionEvent(text, "discount"));
                break;
            case TagManager.LOCATION_FRAGMENT_TAG:
                EventBus.getDefault().post(new FilterOptionEvent(text, "location"));
                break;
        }
    }


}
