package com.example.merna.mallowner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merna.mallowner.Model.Shop;
import com.example.merna.mallowner.mPicasso.PicassoClient;
import com.example.merna.mallowner.ui.MainActivity;
import com.example.merna.mallowner.ui.addShop;
import com.example.merna.mallowner.utils.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Merna on 5/1/2016.
 */
public class shopAdapter extends FirebaseListAdapter<Shop> {
    Activity activity;

    public shopAdapter(MainActivity activity, Class<Shop> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
    }


    @Override
    protected void populateView(View v, final Shop model, final int position) {
        super.populateView(v, model);

        TextView name = (TextView) v.findViewById(R.id.name);
        ImageView logo = (ImageView) v.findViewById(R.id.ShopLogo);
        LinearLayout deleteBtn = (LinearLayout) v.findViewById(R.id.deleteLayout);
        LinearLayout editBtn = (LinearLayout) v.findViewById(R.id.editLayout);
        ImageView fb = (ImageView) v.findViewById(R.id.fbItem);
        ImageView call = (ImageView) v.findViewById(R.id.callItem);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to delete this shop ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Firebase gRef = new Firebase(Constants.FIREBASE_URL);
                                gRef.removeUser(model.getShopEmail(), model.getPassword(), new Firebase.ResultHandler() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(activity, "deleted", Toast.LENGTH_SHORT).show();
                                        Firebase shopRef = new Firebase(Constants.FIREBASE_URL).child("Shops")
                                                .child(getRef(position).getKey()).child("status");
                                        shopRef.setValue("true");
                                        //Firebase ref = getRef(position);
                                        // ref.removeValue();
                                        Firebase userRef = new Firebase(Constants.FIREBASE_URL).child("users").child(getRef(position).getKey());
                                        userRef.removeValue();
                                    }

                                    @Override
                                    public void onError(FirebaseError firebaseError) {

                                    }
                                });
                                // activity.finish();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(activity, "edit btn clicked", Toast.LENGTH_SHORT).show();
                Log.d("kk", "populateView " + getRef(position));
                Intent in = new Intent(activity, addShop.class);
                in.putExtra("editobj", model);
                in.putExtra("uuid", getRef(position).getKey());
                activity.startActivity(in);

            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                Uri uri = Uri.parse("http://" + model.getFbContact());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + model.getPhone()));
                //not understand it , back..
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                activity.startActivity(intent);
            }
        });


        name.setText(model.getShopName());
        String img = model.getLogo();

        if (model.getLogo() != null) {
            PicassoClient.downloadImg(activity, img, logo);
//                byte[] decodedString = Base64.decode(model.getLogo(), Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                logo.setImageBitmap(decodedByte);
        }

    }
}
