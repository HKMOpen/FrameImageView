package com.mikhaellophes.frameImageView.T2D;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
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
    private Paint labelPaint;
    private Paint measurementPaint;
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
    private boolean display_measurement = false;
    private boolean display_inch = false;

    // Properties
    private float borderWidth = 10f;
    private float defaultWidth = 10f;
    private float whiteSpace = 10f;
    private int canvasSize, canvas_sw, canvas_sh;
    private float shadowRadius;
    private int shadowColor = Color.BLACK;

    private String label_1 = "";
    private String label_2 = "";
    private String label_3 = "";
    private String label_4 = "";
    private PointF content_measurement = new PointF();
    private PointF content_measurement_factor = new PointF();
    /**
     * the actual border outer area
     */
    private float outterbx = 0f;

    /**
     * the actual border inner area
     */
    private float innerbx = 0f;

    private final static float inch_conversion = 0.393700787f;
    private final static float gap = 10f;
    private final static float dh = 50f;

    private PointF mPoint1 = new PointF();
    private PointF mPoint2 = new PointF();
    private PointF mPoint3 = new PointF();
    private PointF mPoint4 = new PointF();


    //endregion
    private float mPosX = 0f;
    private float mPosY = 0f;
    private float mLastTouchX = 0f;
    private float mLastTouchY = 0f;
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;


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

        labelPaint = new Paint();
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(25f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setAntiAlias(true);

        measurementPaint = new Paint();
        measurementPaint.setStrokeWidth(3);
        measurementPaint.setPathEffect(null);
        measurementPaint.setColor(Color.WHITE);
        measurementPaint.setStyle(Paint.Style.STROKE);
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
        updateText();
    }

    //endregion
    public void setScaleWhole(float mfactor) {
        this.scale_total = mfactor;
        callDraw();
    }

    public void setWhiteSpace(float sp_width) {
        this.whiteSpace = sp_width;
        updateWhiteSpaceMeasurement();
        updateText();
        callDraw();
    }

    //region Set Attr Method
    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        updateBorderMeasurement();
        updateText();
        callDraw();
    }

    public void setBorderColor(@ColorInt int borderColor) {
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


    public void setWhiteSpaceColor(@ColorInt int sp_width_color) {
        if (paintInner != null)
            paintInner.setColor(sp_width_color);
        callDraw();
    }

    public void setShadowColor(@ColorInt int shadowColor) {
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

    public final void setMeasurementColor(@ColorInt int color) {
        measurementPaint.setColor(color);
        callDraw();
    }

    public final void displayMeasurement(boolean b) {
        display_measurement = b;
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
        updateBorderMeasurement();
        updateWhiteSpaceMeasurement();
        updateText();
        disableTouch(false);
        callDraw();
    }

    public float[] retrieveContentConfig() {
        return new float[]{
                centerx, centery, scale_total
        };
    }

    private void updateBorderMeasurement() {
        if (content_measurement_factor.x > 0) {
            outterbx = borderWidth * content_measurement_factor.x;
        }
    }

    private void updateWhiteSpaceMeasurement() {
        if (content_measurement_factor.x > 0) {
            innerbx = whiteSpace * content_measurement_factor.x;
        }
    }

    public void configMeasureCal(float x_cm, float y_cm) {
        content_measurement.set(x_cm, y_cm);
        if (image != null) {
            content_measurement_factor.set(
                    x_cm / (float) image.getWidth(),
                    y_cm / (float) image.getHeight());
        }
        updateBorderMeasurement();
        updateWhiteSpaceMeasurement();
        updateText();
        callDraw();
    }

    private void updateText() {
        //the outer horizontal
        float w1 = outterbx * 2f + innerbx * 2f + content_measurement.x;
        label_1 = buildSb(w1).toString();
        //the inner horizontal
        float w2 = innerbx * 2f + content_measurement.x;
        label_2 = buildSb(w2).toString();
        //the outer vertical
        float w3 = outterbx * 2f + innerbx * 2f + content_measurement.y;
        label_3 = buildSb(w3).toString();
        //the inner vertical
        float w4 = innerbx * 2f + content_measurement.y;
        label_4 = buildSb(w4).toString();
    }

    private StringBuilder buildSb(float measurement) {
        StringBuilder sb4 = new StringBuilder();
        if (display_inch) {
            sb4.append(measurement * inch_conversion);
            sb4.append(" inch");
        } else {
            sb4.append(measurement);
            sb4.append(" cm");
        }
        return sb4;
    }

    private void measure_configuration(Canvas canvas) {
        if (!display_measurement) return;
        canvas.drawPath(buildPathHV1(), measurementPaint);
        canvas.drawPath(buildPathHV2(), measurementPaint);
        canvas.drawPath(buildPathV1(), measurementPaint);
        canvas.drawPath(buildPathV2(), measurementPaint);

        canvas.drawText(label_1, mPoint1.x, mPoint1.y, labelPaint);
        canvas.drawText(label_2, mPoint2.x, mPoint2.y, labelPaint);

        canvas.save();
        canvas.rotate(90f, mPoint3.x, mPoint3.y);
        canvas.drawText(label_3, mPoint3.x, mPoint3.y, labelPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(90f, mPoint4.x, mPoint4.y);
        canvas.drawText(label_4, mPoint4.x, mPoint4.y, labelPaint);
        canvas.restore();
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
        measure_configuration(canvas);
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

    private void drawShadow(float shadowRadius, @ColorInt int shadowColor) {
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
            mPosX = x;
            mPosY = y;
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


    private Path buildPathHV1() {
        float in = outter.top - gap - dh;
        float out = outter.top - gap;
        float mid = outter.top - gap - dh / 2f;
        Path path = new Path();
        path.moveTo(outter.left, in);
        path.lineTo(outter.left, out);

        path.moveTo(outter.right, in);
        path.lineTo(outter.right, out);

        path.moveTo(outter.left, mid);
        path.lineTo(outter.right, mid);
        path.close();

        float width = outter.right - outter.left;
        mPoint1.set(outter.left + width / 2f, outter.top - dh / 2f);
        return path;
    }

    private Path buildPathHV2() {
        float in = outter.top - gap - dh - dh;
        float out = outter.top - gap - dh;
        float mid = outter.top - gap - dh / 2f - dh;
        Path path = new Path();
        path.moveTo(innerRec.left, in);
        path.lineTo(innerRec.left, out);

        path.moveTo(innerRec.right, in);
        path.lineTo(innerRec.right, out);

        path.moveTo(innerRec.left, mid);
        path.lineTo(innerRec.right, mid);
        path.close();
        float width = innerRec.right - innerRec.left;
        mPoint2.set(innerRec.left + width / 2f, outter.top - dh - dh / 2f);
        return path;
    }

    private Path buildPathV1() {
        float in = outter.right + gap;
        float out = outter.right + gap + dh;
        float mid = outter.right + gap + dh / 2f;
        float mid_h = (outter.bottom - outter.top) / 2f + outter.top;
        Path path = new Path();
        path.moveTo(in, outter.top);
        path.lineTo(out, outter.top);

        path.moveTo(in, outter.bottom);
        path.lineTo(out, outter.bottom);

        path.moveTo(mid, outter.top);
        path.lineTo(mid, outter.bottom);
        path.close();


        mPoint3.set(mid, mid_h);
        return path;
    }

    private Path buildPathV2() {
        float in = outter.right + gap + dh;
        float out = outter.right + gap + dh + dh;
        float mid = outter.right + gap + dh / 2f + dh;
        float mid_h = (outter.bottom - outter.top) / 2f + outter.top;

        Path path = new Path();
        path.moveTo(in, innerRec.top);
        path.lineTo(out, innerRec.top);

        path.moveTo(in, innerRec.bottom);
        path.lineTo(out, innerRec.bottom);

        path.moveTo(mid, innerRec.top);
        path.lineTo(mid, innerRec.bottom);

        path.close();

        mPoint4.set(mid, mid_h);
        return path;
    }

}
