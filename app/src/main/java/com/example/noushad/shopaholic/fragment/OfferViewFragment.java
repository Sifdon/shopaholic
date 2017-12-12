package com.example.noushad.shopaholic.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.activity.MainActivity;
import com.example.noushad.shopaholic.event.RemovedEvent;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.utils.FirebaseService;
import com.example.noushad.shopaholic.utils.SharedPrefManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by noushad on 7/17/17.
 */


public class OfferViewFragment extends Fragment {

    private static final String ARG_PASSED_OFFER = "com.example.noushad.shopaholic.fragment.selected offer";
    private static final int UPDATE_ALL = 1;
    private static final int UPDATE_COMMENTS = 2;

    private ImageView mCoverImageView;
    private TextView mNameTextView;
    private TextView mLastUpdateTextView;
    private TextView mTitleTextView;
    private TextView mOfferDescription;
    private TextView mDiscountTextView;
    private TextView mOldPriceTextView;
    private TextView mCategoryTextView;
    private TextView mExpiryDateTextView;
    private TextView mCurrentPriceTextView;
    private Button mFeedbackButton;
    private Offer mOffer;
    private TextView mLocationTextView;
    private Button mCallButton;

    public static OfferViewFragment newInstance(Offer offer) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PASSED_OFFER, offer);
        OfferViewFragment OfferViewFragment = new OfferViewFragment();
        OfferViewFragment.setArguments(bundle);
        return OfferViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.offer_view, container, false);
        initializeViews(view);
        mOffer = (Offer) getArguments().getSerializable(ARG_PASSED_OFFER);
        updateUI(mOffer);
        return view;
    }

    private void initializeViews(final View view) {

        getActivity().setTitle("Offer View");
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Offer View");
        mCoverImageView = (ImageView) view.findViewById(R.id.offer_cover_image);
        mNameTextView = (TextView) view.findViewById(R.id.offer_maker_name);
        mLocationTextView = (TextView) view.findViewById(R.id.offer_location_text);
        mDiscountTextView = view.findViewById(R.id.offer_discount_text);
        mCurrentPriceTextView = view.findViewById(R.id.offer_current_price);
        mLastUpdateTextView = (TextView) view.findViewById(R.id.offer_last_updated_text);
        mTitleTextView = (TextView) view.findViewById(R.id.offer_title);
        mOfferDescription = (TextView) view.findViewById(R.id.offer_description_text);
        mOldPriceTextView = view.findViewById(R.id.offer_old_price);
        mCategoryTextView = view.findViewById(R.id.offer_category);
        mFeedbackButton = view.findViewById(R.id.feedback_button);
        mCallButton = view.findViewById(R.id.call_now_button);
        mExpiryDateTextView = view.findViewById(R.id.offer_expiry_date);

        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });


    }

    private void makeCall() {
        if(mOffer.getEmail().equals(SharedPrefManager.getInstance(getActivity()).getUser().getEmail())){
            Toast.makeText(getActivity(), "Post Again with Same Title", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).startFragment(new OfferPostingFragment(), 2);
        }else {
            String phone = mOffer.getPhone_no();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }
    }


    private void removeOffer() {
        new FirebaseService().removeOffer(mOffer);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RemovedEvent event) {
        Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void sendEmail() {
        if(mOffer.getEmail().equals(SharedPrefManager.getInstance(getActivity()).getUser().getEmail())){
            removeOffer();
        }else {
            String[] TO = {mOffer.getEmail(),"ankanp0@gmail.com"};
            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack On Your Offer : " + mOffer.getTitle());
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,");
            try {
                startActivity(emailIntent);

            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void updateUI(Offer offer) {
        mOffer = offer;
        if (offer.getEmail().equals(SharedPrefManager.getInstance(getActivity()).getUser().getEmail())) {
            mCallButton.setText("EDIT");
            mFeedbackButton.setText("DELETE");
        }
        Glide.with(getActivity()).load(offer.getLink()).into(mCoverImageView);
        mNameTextView.setText(offer.getName());
        String text = offer.getDiscount();
        mDiscountTextView.setText(text);
        mOldPriceTextView.setText(offer.getPrice());
        mCategoryTextView.setText(offer.getCategories());
        mExpiryDateTextView.setText(offer.getExpiry_date());
        int discount = Integer.parseInt(offer.getDiscount());
        double oldPrice = Double.parseDouble(offer.getPrice());
        double deducted = ((oldPrice * discount) / 100);
        double currentPrice = oldPrice - deducted;
        mCurrentPriceTextView.setText(String.valueOf(currentPrice));
        String time = getlongtoago((long) Double.parseDouble(offer.getDate()));
        mLastUpdateTextView.setText(time);
        mTitleTextView.setText(offer.getTitle());
        mOfferDescription.setText(offer.getDescription());
        mLocationTextView.setText(offer.getLocation());


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

    public String getlongtoago(long createdAt) {
        //DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        crdate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

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
