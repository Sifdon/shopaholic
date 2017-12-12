package com.example.noushad.shopaholic.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noushad.shopaholic.R;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.utils.FirebaseService;
import com.example.noushad.shopaholic.utils.ImageUtils;
import com.example.noushad.shopaholic.utils.SharedPrefManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vstechlab.easyfonts.EasyFonts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * Created by noushad on 7/17/17.
 */

public class OfferPostingFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    public static final String BLOG_CREATION_FRAGMENT = "blog_creation_fragment";
    private ImageView coverImage;
    private EditText titleInput;
    private EditText oldPriceInput;
    private EditText descriptionInput;
    private ImageButton coverUploadButton;
    private Button postButton;
    private TextView mSelectedCategoriesText;
    private ImageButton mCategoriesButton;
    private TextView mExpiryDate;
    private String[] categoriesList = {"Electronics", "Fashion", "Cloth", "Shoe", "Toy", "Jewellery", "Bag", "Health Care", "Furniture", "Others"};
    private boolean[] checkedItems;
    private ArrayList<Integer> selectedCategories;
    private File mFile;
    private EditText mDiscountPercentageText;
    private FirebaseService mService;
    private StorageReference mStorageRef;
    private Uri mFilePath;
    private Calendar myCalendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.post_offer, container, false);
        mService = new FirebaseService();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        initializeViews(view);
        setButtonListeners();
        return view;
    }

    private void setButtonListeners() {
        coverUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        mCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!faultsFound()) {
                    uploadImage();
                }
            }
        });

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        mExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateDate() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(myCalendar.getTimeInMillis()<System.currentTimeMillis()){
            Toast.makeText(getActivity(),"You Chose an INVALID Date",Toast.LENGTH_SHORT).show();
            return;
        }
        mExpiryDate.setText(sdf.format(myCalendar.getTime()));

    }

    private void uploadOffer(String imgDownloadUrl) {
        String selectedCategory = (String) mSelectedCategoriesText.getText();
        String title = titleInput.getText().toString().toLowerCase();
        String price = oldPriceInput.getText().toString().toLowerCase();
        String discountPercent = mDiscountPercentageText.getText().toString();
        String description = descriptionInput.getText().toString();
        String expiryDate = mExpiryDate.getText().toString();
        Offer offer = new Offer(imgDownloadUrl, selectedCategory, title, price, discountPercent, description, expiryDate,
                SharedPrefManager.getInstance(getActivity()).getUser());
        mService.postOffer(offer);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void uploadImage() {
        if (mFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference imageStorageRef = mStorageRef.child("images/" + titleInput.getText().toString().toLowerCase() + SharedPrefManager
                    .getInstance(getActivity()).getUserId());
            imageStorageRef.putFile(mFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        uploadOffer(downloadUrl.toString());
                    }

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


    private void showCategoryDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Categories");
        mBuilder.setMultiChoiceItems(categoriesList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemIndex, boolean isChecked) {
                cleanCategories();
                if (isChecked) {
                    selectedCategories.add(itemIndex);
                } else {
                    selectedCategories.remove(selectedCategories.indexOf(itemIndex));
                }
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String items = "";
                for (int i = 0; i < selectedCategories.size(); i++) {
                    items += categoriesList[selectedCategories.get(i)];
                    if (i != selectedCategories.size() - 1) {
                        items += ", ";
                    }
                }
                mSelectedCategoriesText.setText(items);
            }
        });
        mBuilder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cleanCategories();
            }
        });

        AlertDialog categoryDialog = mBuilder.create();
        categoryDialog.show();
        categoryDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        categoryDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void cleanCategories() {
        for (int i = 0; i < checkedItems.length; i++)
            checkedItems[i] = false;
        selectedCategories.clear();
        mSelectedCategoriesText.setText("");
    }

    private void initializeViews(View view) {
        coverImage = (ImageView) view.findViewById(R.id.offer_cover_input);
        coverUploadButton = (ImageButton) view.findViewById(R.id.imageUploadButton);
        mCategoriesButton = (ImageButton) view.findViewById(R.id.ib_categories_input);
        mExpiryDate = view.findViewById(R.id.offer_expiry_date_input);
        mSelectedCategoriesText = (TextView) view.findViewById(R.id.tv_selected_items);
        mSelectedCategoriesText.setTypeface(EasyFonts.caviarDreams(getContext()));
        titleInput = (EditText) view.findViewById(R.id.titleEditText);
        titleInput.setTypeface(EasyFonts.caviarDreams(getContext()));
        oldPriceInput = (EditText) view.findViewById(R.id.oldPriceInput);
        oldPriceInput.setTypeface(EasyFonts.caviarDreams(getContext()));
        descriptionInput = (EditText) view.findViewById(R.id.descriptionEditText);
        descriptionInput.setTypeface(EasyFonts.caviarDreams(getContext()));
        postButton = (Button) view.findViewById(R.id.postButton);
        checkedItems = new boolean[categoriesList.length];
        selectedCategories = new ArrayList<>();
        mDiscountPercentageText = view.findViewById(R.id.discount_percentage_text);
    }

    private boolean faultsFound() {
        int postLength = descriptionInput.getText().toString().trim().length();
        if (TextUtils.isEmpty(titleInput.getText().toString()) || TextUtils.isEmpty(oldPriceInput.getText().toString())
                || TextUtils.isEmpty(descriptionInput.getText().toString()) || TextUtils.isEmpty(mSelectedCategoriesText.getText().toString())
                || TextUtils.isEmpty(mExpiryDate.getText().toString())) {

            Toast.makeText(getActivity(), "No Fields can be empty", Toast.LENGTH_SHORT).show();
            return true;
        } else if (postLength < 20) {
            Toast.makeText(getActivity(), "Description must contain 60 characters or more.", Toast.LENGTH_SHORT).show();
            return true;

        } else if ((coverImage.getDrawable() == null)) {
            Toast.makeText(getActivity(), "Cover Image cannot be empty", Toast.LENGTH_SHORT).show();
            return true;
        } else if (mSelectedCategoriesText.getText().toString().equals("Category")) {
            Toast.makeText(getActivity(), "You Must Select A Category", Toast.LENGTH_SHORT).show();
            return true;
        } else if (mExpiryDate.getText().equals("Offer Expiry Date")) {
            Toast.makeText(getActivity(), "Please Select a Valid Expiry Date", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            Pattern pattern = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(titleInput.getText().toString().trim());
            boolean found = matcher.find();
            if (found) {
                Toast.makeText(getActivity(), "Title Must not Contain Special Character", Toast.LENGTH_SHORT).show();
                return true;
            }

        }
        return false;
    }

    private void pickImage() {
        Intent chooserIntent = ImageUtils.getChooserIntent();
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            mFile = ImageUtils.getImageFile(getContext(), data.getData(), coverImage);
            mFilePath = data.getData();
        }

    }


}

