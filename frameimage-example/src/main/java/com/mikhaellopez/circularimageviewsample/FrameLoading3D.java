package com.mikhaellopez.circularimageviewsample;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mikhaellophes.frameImageView.T3D.SituationFragment;

/**
 * Created by hesk on 16年12月23日.
 */
public class FrameLoading3D extends SituationFragment {
    @Override
    protected void additionItems(LinearLayout ll) {
        ImageView image = new ImageView(getActivity());
        image.setImageResource(R.drawable.simple_input);
        ll.addView(image);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.situation_view;
    }

    @Override
    protected void glReady() {
      /*  getActivity().runOnUiThread(new Runnable() {
            public void run() {

            Glide.with(getActivity())
                        .load("file:///android_asset/simple_input.jpg")
                        .asBitmap()

                    .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        loadBackground(resource);
                    }
                });
            }
        });*/

      /*  try {
            File file = Glide.with(getActivity())
                    .load("file:///android_asset/simple_input.jpg")
                    .downloadOnly(500, 500).get();

            FileInputStream is = new FileInputStream(file);
            ls(is, null);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
*/
        // float r = (float) 800f / (float) 454f;
        // loadBackgroundRes(R.drawable.simple_input, r);


        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Glide.with(getActivity())
                        .load(R.drawable.alien32)
                        .asBitmap().override(512, 512)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                projectContextFrame(resource, 200f, 60f,
                                        ContextCompat.getColor(getActivity(), R.color.colorC2),
                                        ContextCompat.getColor(getActivity(), R.color.colorH2),
                                        ContextCompat.getColor(getActivity(), R.color.colorM2)
                                );
                            }
                        });
            }
        });
    }
}
