package com.mikhaellopez.circularimageviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hesk on 16年12月23日.
 */

public class MainActual extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_framework);
        // mFrame_v = (FrameImageView) findViewById(R.id.circularImageView);
        getSupportFragmentManager().beginTransaction().add(
                R.id.emptyone,
                new FrameLoading3D()
        ).commit();

    }
}
