package com.mikhaellopez.circularimageviewsample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mikhaellophes.frameImageView.T2D.EditSituationDX;

import static com.mikhaellophes.frameImageView.T2D.EditFrameImageView.TAG_DIMENSION_DISCRIPTION;

/**
 * Created by hesk on 16年12月24日.
 */

public class FrameLoading2D extends EditSituationDX {
    @Override
    protected int setLayoutId() {
        return R.layout.workingsituation;
    }

    private TextView mStringSpan;
    private TextView mTShowUp;
    private Button mStringSpanButton;
    private Button mXtouch;
    private Button mMetaInfo;
    private SeekBar s1;
    private SeekBar s2;
    private SeekBar s3;
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
                        edFrame.configMeasureCal(45f, 45f);
                        edFrame.displayMeasurement(true);
                        edFrame.setHangingPosition(350f, 350f);
                    }
                });

        mStringSpan = (TextView) view.findViewById(R.id.txt_space);
        mTShowUp = (TextView) view.findViewById(R.id.text_show_up);
        mStringSpanButton = (Button) view.findViewById(R.id.button_out_d);
        mXtouch = (Button) view.findViewById(R.id.button_out_toggle);
        mMetaInfo = (Button) view.findViewById(R.id.button_metainfo);
        mStringSpanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStringSpan.setText(getCurrentConfiguration());
            }
        });
        s1 = (SeekBar) view.findViewById(R.id.seekbar1);
        s2 = (SeekBar) view.findViewById(R.id.seekbar2);
        s3 = (SeekBar) view.findViewById(R.id.seekbar3);
        mXtouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTouch(touchx = !touchx);
            }
        });
        mMetaInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mb = getMeta();
                mTShowUp.setText(mb.getString(TAG_DIMENSION_DISCRIPTION));
            }
        });
        s1.setOnSeekBarChangeListener(new SeekbarOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setOutBorderWidth((float) i * getResources().getDisplayMetrics().density);
            }
        });
        s2.setOnSeekBarChangeListener(new SeekbarOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setScale((float) i / (float) 20);
            }
        });
        s3.setOnSeekBarChangeListener(new SeekbarOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setSpaceWidth((float) i * getResources().getDisplayMetrics().density);
            }
        });


    }

    class SeekbarOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


}
