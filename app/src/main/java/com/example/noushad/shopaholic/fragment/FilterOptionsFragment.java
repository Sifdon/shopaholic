package com.example.noushad.shopaholic.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.adapter.FilterOptionsRecycleAdapter;
import com.example.noushad.shopaholic.utils.TagManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterOptionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TAG = "filter options";

    // TODO: Rename and change types of parameters
    private String mOptionsTag;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FilterOptionsRecycleAdapter mAdapter;

    public FilterOptionsFragment() {
    }


    // TODO: Rename and change types and number of parameters
    public static FilterOptionsFragment newInstance(String tag) {
        FilterOptionsFragment fragment = new FilterOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOptionsTag = getArguments().getString(ARG_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = initializeViews(inflater, container);
        return view;
    }

    @NonNull
    private View initializeViews(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_filter_options, container, false);
        mRecyclerView = view.findViewById(R.id.filter_recycler_list);
        mListener = (OnFragmentInteractionListener) getActivity();
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new FilterOptionsRecycleAdapter(getActivity().getApplicationContext(), mListener,mOptionsTag);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setAdapter(mAdapter);
        }
        loadFilterOptions();
    }

    private void loadFilterOptions() {
        List<String> options = new ArrayList<>();
        switch (mOptionsTag) {
            case TagManager.CATEGORY_FRAGMENT_TAG:
                options = getCategoriesList();
                break;
            case TagManager.LOCATION_FRAGMENT_TAG:
                options = getLocationsList();
                break;
            case TagManager.DISCOUNT_FRAGMENT_TAG:
                options = getDiscountList();
                break;
        }
        mAdapter.addAll(options);
    }

    private List<String> getDiscountList() {
        List<String> discounts = new ArrayList<>();
        discounts.add("All");
        discounts.add("5");
        discounts.add("10");
        discounts.add("15");
        discounts.add("20");
        discounts.add("25");
        discounts.add("30");
        discounts.add("35");
        discounts.add("40");
        discounts.add("45");
        discounts.add("50");
        return discounts;

    }

    private List<String> getLocationsList() {
        List<String> locations = new ArrayList<>();
        locations.add("All");
        locations.add("Dhanmondi");
        locations.add("Mirpur");
        locations.add("Uttora");
        locations.add("Gulshan");
        locations.add("Muhammadpur");
        locations.add("Khilgao");
        locations.add("Banani");
        locations.add("Savar");
        locations.add("Zatrabari");
        return locations;

    }

    private List<String> getCategoriesList() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Electronics");
        categories.add("Fashion");
        categories.add("Cloth");
        categories.add("Shoe");
        categories.add("Toy");
        categories.add("Jewellery");
        categories.add("Bag");
        categories.add("Health Care");
        categories.add("Furniture");
        categories.add("Others");

        return categories;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String text, String tag);
    }
}
