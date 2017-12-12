package com.example.noushad.shopaholic.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.fragment.ListFragment;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.utils.PaginationAdapterCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by noushad on 7/16/17.
 */

public class OffersRecycleAdapter extends RecyclerView.Adapter {

    private final List<Offer> mOffers;
    private ListFragment.OnItemSelectedInterface mListener;
    private Context mContext;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private PaginationAdapterCallback mCallback;
    private Offer mOffer;
    private Context parentContext;
    private String errorMsg;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    public OffersRecycleAdapter(Context context, ListFragment.OnItemSelectedInterface listener,
                                PaginationAdapterCallback callback) {
        mContext = context;
        mOffers = new ArrayList<>();
        mListener = listener;
        this.mCallback = callback;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        parentContext = parent.getContext();
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.offer_item, parent, false);
                viewHolder = new OfferVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Offer dataItem = mOffers.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                OfferVH offer = (OfferVH) holder;
                try {
                    offer.bind(dataItem);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    "An unexpected error occurred!");

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mOffers.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public int getItemCount() {
        return mOffers == null ? 0 : mOffers.size();
    }

    public void reverseList() {

        Collections.reverse(mOffers);
    }


    private class OfferVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected Context context;
        private ImageView mCoverImage;
        private TextView mLastUpdatedTextView;
        private TextView mCurrentPrice;
        private TextView mOldPrice;
        private TextView mTitleTextView;
        private TextView mDiscountText;
//        private TextView mDescription;


        public OfferVH(View itemView) {
            super(itemView);
            mCoverImage = itemView.findViewById(R.id.offer_item_image);
            mLastUpdatedTextView = itemView.findViewById(R.id.offer_item_expiry_date);
            mTitleTextView = itemView.findViewById(R.id.offer_item_title);
            mDiscountText = itemView.findViewById(R.id.offer_item_discount_text);
            mCurrentPrice = itemView.findViewById(R.id.offer_current_price2);
            mOldPrice = itemView.findViewById(R.id.offer_old_price2);

            itemView.setOnClickListener(this);
        }

        private void bind(Offer dataItem) throws ParseException {

            Glide.with(mContext).load(dataItem.getLink()).into(mCoverImage);

            mTitleTextView.setText(dataItem.getTitle());
            mDiscountText.setText(dataItem.getDiscount() + " %OFF");
            mLastUpdatedTextView.setText(dataItem.getExpiry_date());
            mOldPrice.setText(dataItem.getPrice());
            int discount = Integer.parseInt(dataItem.getDiscount());
            double oldPrice = Double.parseDouble(dataItem.getPrice());
            double deducted = ((oldPrice * discount) / 100);
            double currentPrice = oldPrice - deducted;
            mCurrentPrice.setText(String.valueOf(currentPrice));
            String time = getlongtoago((long) Double.parseDouble(dataItem.getDate()));
//            mLastUpdatedTextView.setText(time);


        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Offer selectedOffer = mOffers.get(pos);
            mListener.onListOfferSelected(selectedOffer);
        }

    }


    private class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        private LoadingVH(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);
            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }


    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mOffers.size() - 1);
        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    public void add(Offer item) {
        mOffers.add(item);
        notifyItemInserted(mOffers.size() - 1);
    }

    public void addAll(List<Offer> items) {
        for (Offer item : items) {
            add(item);
        }
    }

    public void remove(int index) {

        if (index > -1) {
            mOffers.remove(index);
            notifyItemRemoved(index);
        }

    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(0);
        }
    }

    public void set(int position, Offer item) {
        mOffers.set(position, item);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Offer());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mOffers.size() - 1;
        Offer dataItem = mOffers.get(position);

        if (dataItem != null) {
            mOffers.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String getlongtoago(long createdAt) {
        //DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        crdate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(crdate1);
            current = dateFormat.parse(currenttime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time = null;
        if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago ";
            } else {
                time = diffDays + " days ago ";
            }
        } else {
            if (diffHours > 0) {
                if (diffHours == 1) {
                    time = diffHours + " hr ago";
                } else {
                    time = diffHours + " hrs ago";
                }
            } else {
                if (diffMinutes > 0) {
                    if (diffMinutes == 1) {
                        time = diffMinutes + " min ago";
                    } else {
                        time = diffMinutes + " mins ago";
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = diffSeconds + " secs ago";
                    }
                }

            }

        }
        return time;
    }

}
