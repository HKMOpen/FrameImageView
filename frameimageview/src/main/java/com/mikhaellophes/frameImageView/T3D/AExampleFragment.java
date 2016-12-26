package com.mikhaellophes.frameImageView.T3D;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mikhaellophes.circularimageview.R;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.IDisplay;
import org.rajawali3d.view.ISurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hesk on 16年12月23日.
 */

public abstract class AExampleFragment extends Fragment implements IDisplay, View.OnClickListener {
    public static final String BUNDLE_EXAMPLE_URL = "BUNDLE_EXAMPLE_URL";

    protected ProgressBar mProgressBarLoader;
    protected FrameLayout mLayout;
    protected ISurface mRenderSurface;
    protected ISurfaceRenderer mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the view
        mLayout = (FrameLayout) inflater.inflate(getLayoutId(), container, false);
        mLayout.findViewById(R.id.relative_layout_loader_container).bringToFront();

        // Find the TextureView
        mRenderSurface = (ISurface) mLayout.findViewById(R.id.rajwali_surface);

        // Create the loader
        mProgressBarLoader = (ProgressBar) mLayout.findViewById(R.id.progress_bar_loader);
        mProgressBarLoader.setVisibility(View.GONE);

        // Set the example link
        /*  mImageViewExampleLink = (GitHubLogoView) mLayout.findViewById(R.id.image_view_example_link);
        mImageViewExampleLink.setOnClickListener(this);*/

        // Create the renderer
        mRenderer = createRenderer();
        onBeforeApplyRenderer();
        applyRenderer();
        return mLayout;
    }

    protected void onBeforeApplyRenderer() {
    }

    @CallSuper
    protected void applyRenderer() {
        mRenderSurface.setSurfaceRenderer(mRenderer);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mLayout != null)
            mLayout.removeView((View) mRenderSurface);
    }

    @CallSuper
    protected void hideLoader() {
        mProgressBarLoader.post(new Runnable() {
            @Override
            public void run() {
                mProgressBarLoader.setVisibility(View.GONE);
            }
        });
    }

    @CallSuper
    protected void showLoader() {
        mProgressBarLoader.post(new Runnable() {
            @Override
            public void run() {
                mProgressBarLoader.setVisibility(View.VISIBLE);
            }
        });
    }

    @LayoutRes
    protected int getLayoutId() {
        return R.layout.basictv;
    }

    protected static abstract class AExampleRenderer extends Renderer {

        final AExampleFragment exampleFragment;

        public AExampleRenderer(Context context, @Nullable AExampleFragment fragment) {
            super(context);
            exampleFragment = fragment;
        }

        @Override
        public void onRenderSurfaceCreated(EGLConfig config, GL10 gl, int width, int height) {
            if (exampleFragment != null) exampleFragment.showLoader();
            super.onRenderSurfaceCreated(config, gl, width, height);
            if (exampleFragment != null) exampleFragment.hideLoader();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
        }

    }

}
