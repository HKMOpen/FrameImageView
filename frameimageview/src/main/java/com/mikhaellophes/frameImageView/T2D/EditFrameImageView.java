package com.mikhaellophes.frameImageView.T2D;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.mikhaellophes.circularimageview.R;

/**
 * Created by hesk on 16年12月26日.
 */
public class EditFrameImageView extends View {
    public static final String TAG_FRAME_COLOR = "image_meta.frame_color";
    public static final String TAG_FRAME_BACKDROP = "image_meta.backdrop_color";
    public static final String TAG_FRAME_SHADOW = "image_meta.frame_shadow";
    public static final String TAG_FRAME_WIDTH = "image_meta.frame_width";
    public static final String TAG_FRAME_ORIGINAL_IMAGE = "original_image";
    public static final String TAG_FRAME_ORIGINAL_SM = "original_small_image";
    public static final String TAG_FRAME_SPACE_WIDTH = "image_meta.frame_spc";
    public static final String TAG_FRAME_SPACE_COLOR = "image_meta.frame_spc_color";
    public static final String TAG_FRAME_HX = "image_meta.x";
    public static final String TAG_FRAME_HY = "image_meta.y";
    public static final String TAG_FRAME_SCALE = "image_meta.scale";
    public static final String TAG_FRAME_TRANSFORM = "image_meta.transform";
    public static final String TAG_FRAME_ID = "id_image_basemap";

    // Default Values
    private static final float DEFAULT_BORDER_WIDTH = 4f;
    private static final float DEFAULT_SHADOW_RADIUS = 8.0f;

    // Object used to draw
    private Bitmap image;
    private Paint paintInner;
    private Paint paintBorder;
    private Paint paintcontent;
    private Paint paintCenter;
    private final Matrix matrix;
    private final RectF outter;
    private final RectF innerRec;
    private final RectF centerRec;

    //the constant M for the rate of width and height
    //   private float m;

    private ScaleGestureDetector mScaleDetector;
    private float pivotPointX = 0f;
    private float pivotPointY = 0f;
    private float mScaleFactor = 1.f;
    private float scale_total = 1.f;
    private float centerx;
    private float centery;
    private boolean useTouchPoint = false;

    // Properties
    private float borderWidth;
    private float defaultWidth;
    private float whiteSpace = 10f;
    private int canvasSize, canvas_sw, canvas_sh;
    private float shadowRadius;
    private int shadowColor = Color.BLACK;


    //region Constructor & Init Method
    public EditFrameImageView(final Context context) {
        this(context, null);
    }

    public EditFrameImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditFrameImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        matrix = new Matrix();
        outter = new RectF();
        innerRec = new RectF();
        centerRec = new RectF();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        init(context, attrs, defStyleAttr);
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            pivotPointX = detector.getFocusX();
            pivotPointY = detector.getFocusY();

            Log.d(LOG_TAG, "mScaleFactor " + mScaleFactor);
            Log.d(LOG_TAG, "pivotPointY " + pivotPointY + ", pivotPointX= " + pivotPointX);
            mScaleFactor = Math.max(0.05f, mScaleFactor);
            updateScale(mScaleFactor);
            invalidate();
            return true;
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // Init paint
        paintInner = new Paint();
        paintInner.setAntiAlias(true);
        paintInner.setStyle(Paint.Style.FILL);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);

        paintCenter = new Paint();
        paintCenter.setAntiAlias(true);
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setColor(Color.TRANSPARENT);

        paintcontent = new Paint();
        paintcontent.setAntiAlias(true);
        paintcontent.setColor(Color.WHITE);
        paintcontent.setFilterBitmap(true);
        paintcontent.setDither(true);
        paintcontent.setStyle(Paint.Style.FILL);

        // Load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.EditFrameImageView, defStyleAttr, 0);

        // Init Border
        if (attributes.getBoolean(R.styleable.EditFrameImageView_fic_border_d, true)) {
            float defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().getResources().getDisplayMetrics().density;
            setBorderWidth(attributes.getDimension(R.styleable.EditFrameImageView_fic_border_width_d, defaultBorderSize));
            defaultWidth = attributes.getDimension(R.styleable.EditFrameImageView_fic_default_width_d, defaultBorderSize);
            setBorderColor(attributes.getColor(R.styleable.EditFrameImageView_fic_border_color_d, Color.WHITE));
        }

        // Init Shadow
        if (attributes.getBoolean(R.styleable.EditFrameImageView_fic_shadow_d, false)) {
            shadowRadius = DEFAULT_SHADOW_RADIUS;
            drawShadow(attributes.getFloat(R.styleable.EditFrameImageView_fic_shadow_radius_d, shadowRadius), attributes.getColor(R.styleable.EditFrameImageView_fic_shadow_color_d, shadowColor));
        }
    }

    //endregion
    public void setScaleWhole(float mfactor) {
        this.scale_total = mfactor;
        callDraw();
    }

    //region Set Attr Method
    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        requestLayout();
        callDraw();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);
        callDraw();
    }

    public void addShadow() {
        if (shadowRadius == 0)
            shadowRadius = DEFAULT_SHADOW_RADIUS;
        drawShadow(shadowRadius, shadowColor);
        callDraw();
    }

    public void setShadowRadius(float shadowRadius) {
        drawShadow(shadowRadius, shadowColor);
        callDraw();
    }

    public void setWhiteSpace(float sp_width) {
        whiteSpace = sp_width;
        callDraw();
    }

    public void setWhiteSpaceColor(int sp_width_color) {
        if (paintInner != null)
            paintInner.setColor(sp_width_color);
        callDraw();
    }

    public void setShadowColor(int shadowColor) {
        drawShadow(shadowRadius, shadowColor);
        callDraw();
    }

    public final void setHangingPosition(float x, float y) {
        centerx = x;
        centery = y;
        callDraw();
    }

    public final void defaultPosition() {
        centerx = canvas_sw / 2f;
        centery = canvas_sh / 2f;
        callDraw();
    }

    private OnFrameConfigChange changeListener;

    public void setOnChangeListener(OnFrameConfigChange listener) {
        changeListener = listener;
    }

    private void callDraw() {
        invalidate();
        if (changeListener != null) {
            changeListener.transform(centerx, centery, scale_total);
        }
    }

    /**
     * get data from the current configuration and it will disable touch event
     *
     * @return bundle item
     */
    public final Bundle captureBundleConfig() {
        Bundle mb = new Bundle();
        mb.putFloat(TAG_FRAME_HX, centerx);
        mb.putFloat(TAG_FRAME_HY, centery);
        mb.putFloat(TAG_FRAME_SCALE, scale_total);
        mb.putFloat(TAG_FRAME_WIDTH, borderWidth);
        mb.putFloat(TAG_FRAME_SPACE_WIDTH, whiteSpace);
        mb.putFloat(TAG_FRAME_SHADOW, shadowRadius);
        mb.putInt(TAG_FRAME_SPACE_COLOR, paintInner.getColor());
        mb.putInt(TAG_FRAME_BACKDROP, shadowColor);
        mb.putInt(TAG_FRAME_COLOR, paintBorder.getColor());
        disableTouch(false);
        return mb;
    }

    /**
     * restore the configuration and it will disable touch event
     *
     * @param mb bundle
     */
    public final void restoreCofig(Bundle mb) {
        centerx = mb.getFloat(TAG_FRAME_HX, centerx);
        centery = mb.getFloat(TAG_FRAME_HY, centery);
        scale_total = mb.getFloat(TAG_FRAME_SCALE, scale_total);
        borderWidth = mb.getFloat(TAG_FRAME_WIDTH, borderWidth);
        whiteSpace = mb.getFloat(TAG_FRAME_SPACE_WIDTH, whiteSpace);
        shadowRadius = mb.getFloat(TAG_FRAME_SHADOW, shadowRadius);
        setWhiteSpaceColor(mb.getInt(TAG_FRAME_SPACE_COLOR, paintInner.getColor()));
        setShadowColor(mb.getInt(TAG_FRAME_BACKDROP, shadowColor));
        setBorderColor(mb.getInt(TAG_FRAME_COLOR, paintBorder.getColor()));
        disableTouch(false);
        callDraw();
    }

    public float[] retrieveContentConfig() {
        return new float[]{
                centerx, centery, scale_total
        };
    }

    @Override
    public void onDraw(Canvas canvas) {
        // -Check if image isn't null
        if (image == null)
            return;
        // -Load the bitmap
        loadBitmap();
        if (!isInEditMode()) {
            canvasSize = Math.min(canvas.getWidth(), canvas.getHeight());
            if (defaultWidth < 100) {
                canvas_sw = canvas.getWidth();
                canvas_sh = canvas.getHeight();
            }
        }
        canvas.drawRect(outter, paintBorder);
        canvas.drawRect(innerRec, paintInner);
        canvas.drawBitmap(image, matrix, paintcontent);
    }

    public void setImageBitmap(Bitmap map) {
        this.image = map;
        loadBitmap();
    }

    public void disableTouch(boolean b) {
        useTouchPoint = b;
    }


    private void updateShader() {
        if (image == null)
            return;
        int size = Math.min(image.getWidth(), image.getHeight());
        int width = (image.getWidth() - size) / 2;
        int height = (image.getHeight() - size) / 2;
        float w_float = scale_total * image.getWidth();
        float h_float = scale_total * image.getHeight();
        float applied_border_width = borderWidth * scale_total;
        float applied_whitespace = whiteSpace * scale_total;
        float rw = w_float / 2f;
        float rh = h_float / 2f;
        float combo = applied_border_width + applied_whitespace;
        float w_outter = rw + combo;
        float h_outter = rh + combo;
        float w_inner = rw + applied_whitespace;
        float h_inner = rh + applied_whitespace;
        final float scale_2 = scale_total;
        matrix.reset();
        matrix.postScale(scale_2, scale_2);
        matrix.postTranslate(centerx - rw, centery - rh);
        innerRec.set(centerx - w_inner, centery - h_inner,
                centerx + w_inner, centery + h_inner);
        outter.set(centerx - w_outter, centery - h_outter,
                centerx + w_outter, centery + h_outter);
    }

    private void loadBitmap() {
        if (this.image != null) {
            updateShader();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasSize = w;
        canvas_sw = w;
        canvas_sh = h;
        if (h < canvasSize)
            canvasSize = h;
        if (image != null) {
            updateShader();
        }
    }

    private void drawShadow(float shadowRadius, int shadowColor) {
        this.shadowRadius = shadowRadius;
        this.shadowColor = shadowColor;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        }
        paintBorder.setShadowLayer(shadowRadius, 0.0f, shadowRadius / 2, shadowColor);
    }


    void updateScale(float mfactor) {
        if (useTouchPoint) {
            scale_total = mfactor;
        }
    }

    void updateMousePoint2(float x, float y) {
        if (useTouchPoint) {
            centerx = x;
            centery = y;
        }
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        Bitmap bmp;
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            bmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                    0,
                    bitmap.getHeight(), bitmap.getHeight());
        } else {
            bmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                    bitmap.getWidth(), bitmap.getWidth());
        }
        return bmp;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        if (!(intrinsicWidth > 0 && intrinsicHeight > 0))
            return null;

        try {
            // Create Bitmap object out of the drawable
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Simply return null of failed bitmap creations
            Log.e(getClass().toString(), "Encountered OutOfMemoryError while generating bitmap!");
            return null;
        }
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        /*int imageSize = (width < height) ? width : height;
        setMeasuredDimension(imageSize, imageSize);*/
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize;
        }

        return (result + 5);
    }

    //endregion
    private float mPosX = 0f;
    private float mPosY = 0f;
    private float mLastTouchX;
    private float mLastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (useTouchPoint) {
            // Let the ScaleGestureDetector inspect all events.
            mScaleDetector.onTouchEvent(ev);
            final int action = ev.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    final float x = ev.getX();
                    final float y = ev.getY();

                    mLastTouchX = x;
                    mLastTouchY = y;

                    mActivePointerId = ev.getPointerId(0);
                    break;


                case MotionEvent.ACTION_MOVE:
                    final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                    final float x1 = ev.getX(pointerIndex);
                    final float y1 = ev.getY(pointerIndex);

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    if (!mScaleDetector.isInProgress()) {
                        final float dx = x1 - mLastTouchX;
                        final float dy = y1 - mLastTouchY;
                        mPosX += dx;
                        mPosY += dy;
                        updateMousePoint2(mPosX, mPosY);
                        invalidate();
                    }

                    mLastTouchX = x1;
                    mLastTouchY = y1;
                    break;


                case MotionEvent.ACTION_UP:
                    mActivePointerId = INVALID_POINTER_ID;
                    break;


                case MotionEvent.ACTION_CANCEL:
                    mActivePointerId = INVALID_POINTER_ID;
                    break;


                case MotionEvent.ACTION_POINTER_UP:
                    final int pointerIndex_ = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = ev.getPointerId(pointerIndex_);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex_ == 0 ? 1 : 0;
                        mLastTouchX = ev.getX(newPointerIndex);
                        mLastTouchY = ev.getY(newPointerIndex);
                        mActivePointerId = ev.getPointerId(newPointerIndex);
                    }
                    break;

            }
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

}
