package com.mikhaellopez.circularimageviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mikhaellophes.frameImageView.T2D.EditFrameImageView;

/**
 * Created by hesk on 16年11月8日.
 */

public class BestSetupUp extends AppCompatActivity {
    private EditFrameImageView mFrame_v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_framework);
        getSupportFragmentManager().beginTransaction().add(
                R.id.emptyone,
                new FrameLoading2D()
        ).commit();

    }
}
