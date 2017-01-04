package com.mikhaellophes.frameImageView.T2D;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikhaellophes.circularimageview.R;

/**
 * Created by hesk on 16年12月26日.
 */

public class EditSituationDX extends Fragment {

    protected ImageView background;
    protected EditFrameImageView edFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setLayoutId(), null, false);
        return view;
    }

    @LayoutRes
    protected int setLayoutId() {
        return R.layout.t2d_workingsituation;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        background = (ImageView) view.findViewById(R.id.background);
        edFrame = (EditFrameImageView) view.findViewById(R.id.edcircularImageView);
    }


    protected void setOutBorderWidth(float n) {
        edFrame.setBorderWidth(n);
    }

    protected void setOutBorderColor(int n) {
        edFrame.setBorderColor(n);
    }

    protected void setSpaceWidth(float n) {
        edFrame.setWhiteSpace(n);
    }

    protected void setScale(float n) {
        edFrame.setScaleWhole(n);
    }

    protected void setSpaceColor(int n) {
        edFrame.setWhiteSpaceColor(n);
    }

    protected void setContent(Bitmap bitmap) {
        edFrame.setImageBitmap(bitmap);
    }

    protected void setBackground(Bitmap bitmap) {
        background.setImageBitmap(bitmap);
    }

    protected void setShadowRadius(float n) {
        edFrame.setShadowRadius(n);
    }

    protected void setShadowColor(int n) {
        edFrame.setShadowColor(n);
    }

    protected float[] getRawConfig() {
        return edFrame.retrieveContentConfig();
    }

    protected String getCurrentConfiguration() {
        float[] j = getRawConfig();
        StringBuilder sb = new StringBuilder();
        sb.append(j[0]);
        sb.append(" / ");
        sb.append(j[1]);
        sb.append(" / ");
        sb.append(j[2]);
        return sb.toString();
    }

    protected void setDefaultPosition() {
        edFrame.defaultPosition();
    }

    protected void setTouch(boolean h) {
        edFrame.disableTouch(h);
    }

    protected Bundle getMeta() {
       return edFrame.captureBundleConfig();
    }
}
