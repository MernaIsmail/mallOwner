package com.example.merna.mallowner.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.merna.mallowner.Model.Shop;
import com.example.merna.mallowner.R;
import com.example.merna.mallowner.mPicasso.PicassoClient;
import com.example.merna.mallowner.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class shopDetailsFragment extends Fragment {

    ImageView Img;
    TextView mTxtShopName,mTxtPhone,mTxtFb,mTxtTwitter;

    public shopDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_shop_details, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((shopDetails)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.leftarrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getContext(), MainActivity.class);
                startActivity(in);
            }
        });
        ((shopDetails)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        Img=(ImageView)rootView.findViewById(R.id.ShopLogoDetail);
        mTxtShopName=(TextView)rootView.findViewById(R.id.ShopNameDetails);
        mTxtPhone=(TextView)rootView.findViewById(R.id.callDetails);
        mTxtFb=(TextView)rootView.findViewById(R.id.fbDetails);
        mTxtTwitter=(TextView)rootView.findViewById(R.id.twitterDetails);

        Intent intent = getActivity().getIntent();
        String shopUUID = intent.getStringExtra("shop");
        Log.e("shopdetails",shopUUID);
        Firebase shop = new Firebase(Constants.FIREBASE_URL).child("Shops").child(shopUUID);
        shop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("snap", String.valueOf(dataSnapshot.getValue()));
                String logo=(String)dataSnapshot.child("logo").getValue();
                String name= (String) dataSnapshot.child("shopName").getValue();
                String phone= (String)dataSnapshot.child("phone").getValue();
                String fbContact= (String)dataSnapshot.child("fbContact").getValue();
                String twitterContact= (String)dataSnapshot.child("twitterContact").getValue();

                mTxtShopName.setText(name);
                mTxtPhone.setText(phone);
                mTxtFb.setText(fbContact);
                mTxtTwitter.setText(twitterContact);

                PicassoClient.downloadImg(getActivity(),logo,Img);
//                assert logo != null;
//                          if(logo != null) {
//
//                    byte[] decodedString = Base64.decode(logo, Base64.DEFAULT);
//                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    Img.setImageBitmap(decodedByte);
//                       }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return  rootView;
    }
}
