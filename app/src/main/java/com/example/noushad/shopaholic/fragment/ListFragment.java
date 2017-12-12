package com.example.noushad.shopaholic.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.activity.LoginActivity;
import com.example.noushad.shopaholic.activity.MainActivity;
import com.example.noushad.shopaholic.adapter.OffersRecycleAdapter;
import com.example.noushad.shopaholic.event.FilterOptionEvent;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.utils.PaginationAdapterCallback;
import com.example.noushad.shopaholic.utils.SharedPrefManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noushad on 7/17/17.
 */

public class ListFragment extends Fragment implements PaginationAdapterCallback, FilterOptionsFragment.OnFragmentInteractionListener {


    OnItemSelectedInterface mListener;
    RecyclerView mRecyclerView;
    private OffersRecycleAdapter mAdapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20;
    private int mCurrentPage = 1;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    Button discount_button;
    private TextView txtError;
    private PaginationAdapterCallback mCallback;
    private DatabaseReference mDatabase;
    private List<String> mOfferKeys;

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    @Override
    public void onFragmentInteraction(String text, String tag) {
        Toast.makeText(getActivity(), tag + "  //  " + text, Toast.LENGTH_SHORT).show();
    }

    public interface OnItemSelectedInterface {
        void onListOfferSelected(Offer offer);
    }


    public ListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("News Feed");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mOfferKeys = new ArrayList<>();
        View view = initializeViews(inflater, container);
        isStoragePermissionGranted();
        return view;
    }

    private boolean isStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if ((getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) && (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) && (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }

    }

    @NonNull
    private View initializeViews(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mListener = (OnItemSelectedInterface) getActivity();
        mCallback = (PaginationAdapterCallback) getActivity();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar = (ProgressBar) view.findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        btnRetry = (Button) view.findViewById(R.id.error_btn_retry);
        discount_button = view.findViewById(R.id.post_discount_button);
        discount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.startFragment(new OfferPostingFragment(), MainActivity.REPLACE);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getContext(), "You Need To Login", Toast.LENGTH_SHORT).show();

                }
            }
        });
        txtError = (TextView) view.findViewById(R.id.error_txt_cause);
        return view;
    }

    private void loadFirstPage() {
        hideErrorView();
        Query query = mDatabase.child("offers").child("all_offers").orderByChild("date");
        makeQuery(query);
    }

    private void makeQuery(Query query) {

        mAdapter.clear();

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                Offer offer = dataSnapshot.getValue(Offer.class);
                mAdapter.add(offer);
                String key = dataSnapshot.getKey();
//                mAdapter.reverseList();
                mOfferKeys.add(key);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Offer offer = dataSnapshot.getValue(Offer.class);
                String key = dataSnapshot.getKey();
                int index = mOfferKeys.indexOf(key);
                mAdapter.set(index, offer);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Offer offer = dataSnapshot.getValue(Offer.class);
                String key = dataSnapshot.getKey();
                int index = mOfferKeys.indexOf(key);
                mAdapter.remove(index);
                mAdapter.notifyItemChanged(index);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText("Error in Connection!");
        }
    }

    private void updateUI() {

        if (mAdapter == null) {
            mAdapter = new OffersRecycleAdapter(getActivity().getApplicationContext(), mListener, mCallback);
            mRecyclerView.setAdapter(mAdapter);
            loadFirstPage();
        } else {
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

    }

    private void loadNextPage() {

//        callResponse().enqueue(new Callback<AllpostsResponse>() {
//            @Override
//            public void onResponse(Call<AllpostsResponse> call, Response<AllpostsResponse> response) {
//                mAdapter.removeLoadingFooter();
//                if (response.isSuccessful()) {
//                    isLoading = false;
//                    List<DataItem> results = response.body().getData();
//                    mAdapter.addAll(results);
//                    if (mCurrentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
//                    else isLastPage = true;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AllpostsResponse> call, Throwable t) {
//                mAdapter.showRetry(true, t.getMessage());
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterOptionEvent(FilterOptionEvent event) {
        Toast.makeText(getActivity(), event.getTag() + "//======//" + event.getText(), Toast.LENGTH_SHORT).show();
        String value = event.getText();
        Query query = null;
        if(!value.equals("All")) {

            query = mDatabase.child("offers").child("all_offers").orderByChild(event.getTag()).equalTo(value);

        }else{
            query = mDatabase.child("offers").child("all_offers").orderByChild("date");
        }
        makeQuery(query);
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
