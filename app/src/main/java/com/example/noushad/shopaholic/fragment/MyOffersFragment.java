package com.example.noushad.shopaholic.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.noushad.shopaholic.adapter.OffersRecycleAdapter;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.utils.PaginationAdapterCallback;
import com.example.noushad.shopaholic.utils.SharedPrefManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noushad on 7/17/17.
 */

public class MyOffersFragment extends Fragment implements PaginationAdapterCallback {


    OnItemSelectedInterface mListener;
    RecyclerView mRecyclerView;
    private OffersRecycleAdapter mAdapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 20; // just a initial dummy value
    private int mCurrentPage = 1;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    private TextView txtError;
    private PaginationAdapterCallback mCallback;
    private DatabaseReference mDatabase;
    private List<String> mOfferKeys;

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    public interface OnItemSelectedInterface {
        void onListOfferSelected(Offer offer);
    }


    public MyOffersFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("News Feed");
        View view = initializeViews(inflater, container);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("offers").child(SharedPrefManager.getInstance(getContext()).getUserId());
        mOfferKeys = new ArrayList<>();
        return view;
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
        txtError = (TextView) view.findViewById(R.id.error_txt_cause);
        return view;
    }

    private void loadFirstPage() {
        hideErrorView();
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                Offer offer = dataSnapshot.getValue(Offer.class);
                Toast.makeText(getActivity(),offer.getCategories(),Toast.LENGTH_SHORT).show();
                mAdapter.add(offer);
                String key = dataSnapshot.getKey();
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

            txtError.setText(throwable.getMessage());
        }
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new OffersRecycleAdapter(getActivity().getApplicationContext(), (ListFragment.OnItemSelectedInterface) mListener, mCallback);
            mRecyclerView.setAdapter(mAdapter);
            loadFirstPage();
        } else {
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    private void loadNextPage() {
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

}
