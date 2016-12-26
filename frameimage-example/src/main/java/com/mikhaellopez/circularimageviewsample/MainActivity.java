package com.mikhaellopez.circularimageviewsample;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.mikhaellophes.frameImageView.T2D.FrameImageView;


/**
 * Created by Mikhael LOPEZ on 09/10/15.
 */
public class MainActivity extends AppCompatActivity {

    private FrameImageView mFrameIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameIV = (FrameImageView) findViewById(R.id.circularImageView);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
             /*   Glide.with(getApplicationContext()).load(R.drawable.pp5).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mFrameIV.setImageBitmap(resource);
                    }
                });*/

                Glide.with(getBaseContext()).load(R.drawable.pp6).asBitmap()
                        .override(2000, 2000)
                        //.fitCenter()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mFrameIV.setImageBitmap(resource);
                                /*frameImageView.setImageBitmap(resource);
                                init(mode);*/
                            }
                        });

            }
        }, 1000);
        // BORDER
        ((SeekBar) findViewById(R.id.seekBarBorderWidth)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFrameIV.setBorderWidth(progress * (int) getResources().getDisplayMetrics().density);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // AMPLITUDE
        ((SeekBar) findViewById(R.id.seekBarShadowRadius)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFrameIV.setShadowRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // LobsterShadeSlider lobster = (LobsterShadeSlider) findViewById(R.id.shade_slider);
        //  LobsterShadeSlider lobster_grey = (LobsterShadeSlider) findViewById(R.id.shader_grey);
        //COLOR
        /* lobster.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {
                mFrameIV.setBorderColor(color);
                //   mFrameIV.setShadowColor(color);
            }

            @Override
            public void onColorSelected(@ColorInt int color) {
            }
        });*/
        //LobsterPicker picker = (LobsterPicker) findViewById(R.id.picker);
        Button picker = (Button) findViewById(R.id.frame1);
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colordialog();
            }
        });
        //lobster.addDecorator(lobster_grey);
        //picker.addDecorator(lobster);
    }

    private void colordialog() {
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Choose color")
                .initialColor(ContextCompat.getColor(getApplication(), R.color.colorAccent))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        // toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        mFrameIV.setBorderColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lopspower/CircularImageView")));
                return true;
            case R.id.beer:
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.pay_me_a_beer))
                        .setMessage(getResources().getString(R.string.offer_me_a_beer))
                        .setPositiveButton(getResources().getString(android.R.string.ok).toUpperCase(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/LopezMikhael")));
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.cancel).toUpperCase(), null)
                        .show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
