package com.example.merna.mallowner.mPicasso;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import com.example.merna.mallowner.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Merna on 5/14/2016.
 */
public class PicassoClient {

    public  static  void  downloadImg(Activity c, String url, ImageView img){
        if(url != null && url.length()>0){

            Picasso.with(c).load(url).placeholder(R.drawable.logo).into(img);
        }else {
            Picasso.with(c).load(R.drawable.logo).into(img);

        }
    }
}
