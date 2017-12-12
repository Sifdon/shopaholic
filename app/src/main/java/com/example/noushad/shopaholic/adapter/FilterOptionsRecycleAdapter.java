package com.example.noushad.shopaholic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.fragment.FilterOptionsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noushad on 11/25/17.
 */

public class FilterOptionsRecycleAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final FilterOptionsFragment.OnFragmentInteractionListener mListener;
    private final String mTag;
    List<String> options;
    private Context mParentContext;

    public FilterOptionsRecycleAdapter(Context applicationContext, FilterOptionsFragment.OnFragmentInteractionListener listener, String tag) {
        options = new ArrayList<>();
        mContext = applicationContext;
        mListener = listener;
        mTag = tag;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mParentContext = parent.getContext();
        View viewItem = inflater.inflate(R.layout.options_item, parent, false);
        viewHolder = new OptionVH(viewItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String text = options.get(position);
        OptionVH optionVH = (OptionVH) holder;
        optionVH.bind(text);
    }

    @Override
    public int getItemCount() {
        return options == null ? 0 : options.size();
    }

    private static final String TAG = "FilterOptionsRecycleAda";
    public void addAll(List<String> options) {
        Log.d(TAG, "addAll: ");
        for (String item : options) {
            add(item);
        }
    }

    private void add(String item) {
        options.add(item);
    }

    public void remove(String item) {
        int position = options.indexOf(item);
        if (position > -1) {
            options.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(options.get(0));
        }
    }

    private class OptionVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView OptionNameText;

        public OptionVH(View viewItem) {
            super(viewItem);
            OptionNameText = viewItem.findViewById(R.id.option_item_name);
            viewItem.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            String text = options.get(pos);
            mListener.onFragmentInteraction(text,mTag);
        }

        public void bind(String text) {
            OptionNameText.setText(text);
        }
    }
}
