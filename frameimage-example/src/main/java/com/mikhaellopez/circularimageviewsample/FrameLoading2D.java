package com.mikhaellopez.circularimageviewsample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mikhaellophes.frameImageView.T2D.EditSituationDX;

/**
 * Created by hesk on 16年12月24日.
 */

public class FrameLoading2D extends EditSituationDX {
    @Override
    protected int setLayoutId() {
        return R.layout.workingsituation;
    }

    private TextView mStringSpan;
    private Button mStringSpanButton;
    private Button mXtouch;
    private boolean touchx = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(this).load(R.drawable.simple_input)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        setBackground(resource);

                    }
                });

        Glide.with(this).load(R.drawable.pp6)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        setContent(resource);
                        setSpaceColor(ContextCompat.getColor(getContext(), R.color.colorH2));
                    }
                });

        mStringSpan = (TextView) view.findViewById(R.id.txt_space);
        mStringSpanButton = (Button) view.findViewById(R.id.button_out_d);
        mXtouch = (Button) view.findViewById(R.id.button_out_toggle);
        mStringSpanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStringSpan.setText(getCurrentConfiguration());
            }
        });
        mXtouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTouch(touchx = !touchx);
            }
        });
    }

}
