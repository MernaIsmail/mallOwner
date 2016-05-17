package com.example.merna.mallowner.ui;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.cloudinary.Cloudinary;
import com.example.merna.mallowner.Model.Shop;
import com.example.merna.mallowner.R;
import com.example.merna.mallowner.mPicasso.PicassoClient;
import com.example.merna.mallowner.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class addShopFragment extends Fragment {

    Firebase mFirebaseRef;
    private ProgressDialog mAuthProgressDialog;
    EditText mEditTextEmailInput, mEditTextPasswordInput, mEditTextShopName, mEditTextcallInput, mEditTextFbContact, mEditTextTwitterContact;
    ImageButton addPic;
    private static final int PICK_IMAGE = 100;
    String mEmail, mPassword, mShopName, mCallContact, mFbContact, mTwitterContact;
    String imageFile = null;
    Shop newShop;
    Shop toEdit = null;
    String uuidRef;
    Uri imageUri;
    Cloudinary cloudinary;
    String Generated_Id;
    InputStream in;
    //String[] spinnerValues = { "Category1", "Category2", "Category3","Category3"};
    //Spinner categorySpinner;

    public addShopFragment() {
    }


    public String generatePIN() {
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomPIN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                mEmail = mEditTextEmailInput.getText().toString();
                mPassword = mEditTextPasswordInput.getText().toString();
                mShopName = mEditTextShopName.getText().toString();
                mCallContact = mEditTextcallInput.getText().toString();
                mFbContact = mEditTextFbContact.getText().toString();
                mTwitterContact = mEditTextTwitterContact.getText().toString();

                if (isConnected(getContext())) {

                    boolean validEmail = isEmailValid(mEmail);
                    boolean validUserName = isUserNameValid(mShopName);
                    boolean validPassword = isPasswordValid(mPassword);
                    if (!validEmail || !validUserName || !validPassword) return false;
                    if (TextUtils.isEmpty(mCallContact)) {
                        mEditTextcallInput.setError("Please Enter The Number!");
                        return false;
                    } else if (mCallContact.length() < 11) {
                        mEditTextcallInput.setError("Please Enter The Correct Number!");
                        return false;
                    }

/**
 * If everything was valid show the progress dialog to indicate that
 * account creation has started
 *
 */Log.d("immm", String.valueOf(imageUri));

                    if (imageUri != null) {

                        Generated_Id = generatePIN();
                        final Map<String, String> options = new HashMap<>();
                        options.put("public_id", Generated_Id);

                        try {
                            in = ((addShop) getActivity()).getContentResolver().openInputStream(imageUri);
                            imageFile = cloudinary.url().generate(Generated_Id + ".jpg");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    cloudinary.uploader().upload(in, options);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("errior", "imgtest");
                                }

                            }
                        };
                        new Thread(runnable).start();


                    }

                    if (toEdit != null) { //update
                        Firebase eRef = new Firebase(Constants.FIREBASE_URL).child("Shops").child(uuidRef);
                        Firebase oRef = new Firebase(Constants.FIREBASE_URL);
                        oRef.changeEmail(toEdit.getShopEmail(), toEdit.getPassword(), mEmail, new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {
                                Log.d("change email", "done");
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {

                            }
                        });
                        oRef.changePassword(mEmail, toEdit.getPassword(), mPassword, new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {

                            }
                        });
                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("shopEmail", mEmail);
                        updates.put("shopName", mShopName);
                        updates.put("password", mPassword);
                        if (imageFile != null) {
                            updates.put("logo", imageFile);
                        }
                        updates.put("phone", mCallContact);
                        updates.put("fbContact", mFbContact);
                        updates.put("twitterContact", mTwitterContact);
                        eRef.updateChildren(updates);
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        mAuthProgressDialog.show();
                        /**
                         * Create new user with specified email and password
                         */
                        mFirebaseRef.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {

                            @Override
                            public void onSuccess(Map<String, Object> result) {
                                /* Dismiss the progress dialog */
                                mAuthProgressDialog.dismiss();
                                Log.i("LOG_TAG", getString(R.string.log_message_auth_successful));
                                String uid = (String) result.get("uid");
                                newShop = new Shop(mShopName, mEmail, mPassword, imageFile, mCallContact, mFbContact, mTwitterContact);
                                createUserInFirebaseHelper(uid, newShop);
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                /* Error occurred, log the error and dismiss the progress dialog */
                                Log.d("LOG_TAG\"", getString(R.string.log_error_occurred) +
                                        firebaseError);
                                mAuthProgressDialog.dismiss();
                /* Display the appropriate error message */
                                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                                    mEditTextEmailInput.setError(getString(R.string.error_email_taken));
                                } else {
                                    showErrorToast(firebaseError.getMessage());
                                }

                            }

                        });
                    }
                }else {

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("No Internet connection.");
                    alertDialog.setMessage("You have no internet connection");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            imageUri = data.getData();
            addPic.setImageURI(imageUri);
//            try {
//                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(String.valueOf(imageUri)));
//                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 50, bYtE);
//                bmp.recycle();
//                byte[] byteArray = bYtE.toByteArray();
//                imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }

    private void createUserInFirebaseHelper(final String uid, final Shop shop) {

        //create User with type shop in userListDB
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(uid);
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* If there is no user, make one */
                if (dataSnapshot.getValue() == null) {
                 /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    Shop newUser = new Shop(mEmail);
                    userLocation.setValue(newUser);
                    Log.e("create", newUser.getShopEmail() + newUser.getType());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("LOG_TAG", getString(R.string.log_error_occurred) + firebaseError.getMessage());
            }
        });
        //create shop info in ShopListDB
        final Firebase shopList = new Firebase(Constants.FIREBASE_URL).child("Shops").child(uid);
        shopList.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {

                    shopList.setValue(shop);


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_shop, container, false);
//        ((addShop)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        ((addShop)getActivity()).getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((addShop) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to return to home and cancel this data ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent in = new Intent(getContext(), MainActivity.class);
                                startActivity(in);
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
        ((addShop) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        Map config = new HashMap();
        config.put("cloud_name", "gp");
        config.put("api_key", "667862958976234");
        config.put("api_secret", "zAQ9orjld73mDil8fFsdDNXUQrg");
        cloudinary = new Cloudinary(config);

        Intent intent = getActivity().getIntent();
        // String shopUUID = intent.getStringExtra("editobj");
        toEdit = (Shop) intent.getSerializableExtra("editobj");
        uuidRef = intent.getStringExtra("uuid");

        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
        mEditTextShopName = (EditText) rootView.findViewById(R.id.shopName);
        mEditTextEmailInput = (EditText) rootView.findViewById(R.id.email);
        mEditTextPasswordInput = (EditText) rootView.findViewById(R.id.password);
        mEditTextcallInput = (EditText) rootView.findViewById(R.id.callContacts);
        mEditTextFbContact = (EditText) rootView.findViewById(R.id.fbContacts);
        mEditTextTwitterContact = (EditText) rootView.findViewById(R.id.twitterContacts);
        addPic = (ImageButton) rootView.findViewById(R.id.addPic);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        if (toEdit != null) {
            mEditTextShopName.setText(toEdit.getShopName());
            mEditTextEmailInput.setText(toEdit.getShopEmail());
            mEditTextPasswordInput.setText(toEdit.getPassword());
            mEditTextcallInput.setText(toEdit.getPhone());
            mEditTextFbContact.setText(toEdit.getFbContact());
            mEditTextTwitterContact.setText(toEdit.getTwitterContact());
            PicassoClient.downloadImg(getActivity(),toEdit.getLogo(),addPic);
//            if (toEdit.getLogo() != null) {
//                byte[] decodedString = Base64.decode(toEdit.getLogo(), Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                addPic.setImageBitmap(decodedByte);
//            }

        }


        mAuthProgressDialog = new ProgressDialog(getContext());
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_creating_user_with_firebase));
        mAuthProgressDialog.setCancelable(false);


        return rootView;
    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailInput.setError(String.format(getString(R.string.error_invalid_email_not_valid),
                    email));
            return false;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextShopName.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            mEditTextPasswordInput.setError(getResources().getString(R.string.error_invalid_password_not_valid));
            return false;
        }
        return true;
    }

    private void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void setupActionBar() {

    }


}
