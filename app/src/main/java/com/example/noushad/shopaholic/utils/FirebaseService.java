package com.example.noushad.shopaholic.utils;

import android.support.annotation.NonNull;

import com.example.noushad.shopaholic.event.ErrorEvent;
import com.example.noushad.shopaholic.event.RegisterEvent;
import com.example.noushad.shopaholic.event.RemovedEvent;
import com.example.noushad.shopaholic.event.SignInEvent;
import com.example.noushad.shopaholic.model.Offer;
import com.example.noushad.shopaholic.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by noushad on 11/28/17.
 */

public class FirebaseService {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void postOffer(Offer offer) {
        mDatabase.child("offers").child("all_offers").child(offer.getTitle() + "_" + offer.getId()).setValue(offer);
        mDatabase.child("offers").child(offer.getId()).child(offer.getTitle() + "_" + offer.getId()).setValue(offer);
    }


    public void createUser(final String email, String password, final String name, final String phone, final String location) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification();
                    }
                    String id = user.getUid();
                    UserInfo userInfo = new UserInfo(name, email, phone, id, location);
                    postUserInfo(userInfo);
                    EventBus.getDefault().post(new RegisterEvent("Please Check Your Email to Verify Account"));
                } else {
                    EventBus.getDefault().post(new ErrorEvent("SignUp Failed"));
                }
            }
        });
    }

    private void postUserInfo(UserInfo userInfo) {
        mDatabase.child("users").child(userInfo.getId()).setValue(userInfo);
    }

    public void getUserData(String uid) {

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                EventBus.getDefault().post(new SignInEvent(userInfo));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeOffer(Offer offer) {
        String offer_Id = offer.getTitle() + "_" + offer.getId();
        mDatabase.child("offers").child("all_offers").child(offer_Id).removeValue();
        mDatabase.child("offers").child(offer.getId()).child(offer_Id).removeValue();

        EventBus.getDefault().post(new RemovedEvent());


    }

}
