package com.parse.instagramclone;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ViewImageActivity extends AppCompatActivity {

    TextView likeCounterText;
    ImageView heartImageView;

    String currentPhotoId = "";
    int currentPhotoLikeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        heartImageView = (ImageView) findViewById(R.id.heartImageView);

        heartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikePhoto(v);
            }
        });

        likeCounterText = (TextView) findViewById(R.id.likeCounterText);

        Intent intent = getIntent();

        String objectId = intent.getStringExtra("objectId");

        currentPhotoId = objectId;

        byte[] byteArray = intent.getByteArrayExtra("imageByteArray");

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView.setImageBitmap(bitmap);

        ParseQuery<ParseObject> query = new ParseQuery<>("Image");

        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                currentPhotoLikeCount = object.getInt("likes");
                likeCounterText.setText(currentPhotoLikeCount + " likes.");
            }
        });
    }

    public void LikePhoto(View view)
    {
        currentPhotoLikeCount++;

        ParseQuery<ParseObject> query = new ParseQuery<>("Image");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {

            ObjectAnimator anim = ObjectAnimator.ofFloat(heartImageView, "ScaleY", 0.4f, 1f);
            anim.setInterpolator(new BounceInterpolator());
            anim.setDuration(500);
            anim.start();
        }

        heartImageView.setClickable(false);

        query.getInBackground(currentPhotoId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.put("likes", currentPhotoLikeCount);
                likeCounterText.setText(currentPhotoLikeCount + " likes.");
                try {
                    object.save();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
