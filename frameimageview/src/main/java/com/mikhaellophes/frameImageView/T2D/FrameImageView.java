package com.mikhaellophes.frameImageView.T2D;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.mikhaellophes.circularimageview.R;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * Created by Mikhael LOPEZ on 09/10/2015, hesk 9/4/2016
 */
public class FrameImageView extends View {
    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;

    // Default Values
    private static final float DEFAULT_BORDER_WIDTH = 4;
    private static final float DEFAULT_SHADOW_RADIUS = 8.0f;

    // Properties
    private float borderWidth;
    private int canvasSize, canvas_sw, canvas_sh;
    private float shadowRadius;
    private int shadowColor = Color.BLACK;

    // Object used to draw
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;
    private final Matrix matrix;
    private final RectF outter;
    private final RectF innerRec;

    //the constant M for the rate of width and height
    //   private float m;
    private float scale_total;
    private ScaleGestureDetector mScaleDetector;

    //region Constructor & Init Method
    public FrameImageView(final Context context) {
        this(context, null);
    }

    public FrameImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        matrix = new Matrix();
        outter = new RectF();
        innerRec = new RectF();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        init(context, attrs, defStyleAttr);
    }

    float pivotPointX = 0f;
    float pivotPointY = 0f;
    private float mScaleFactor = 1.f;

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            pivotPointX = detector.getFocusX();
            pivotPointY = detector.getFocusY();

            Log.d(LOG_TAG, "mScaleFactor " + mScaleFactor);
            Log.d(LOG_TAG, "pivotPointY " + pivotPointY + ", pivotPointX= "
                    + pivotPointX);
            mScaleFactor = Math.max(0.05f, mScaleFactor);
            updateScale(mScaleFactor);
            invalidate();
            return true;
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // Init paint
        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);

        // Load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.FrameImageView, defStyleAttr, 0);

        // Init Border
        if (attributes.getBoolean(R.styleable.FrameImageView_fic_border, true)) {
            float defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().getResources().getDisplayMetrics().density;
            setBorderWidth(attributes.getDimension(R.styleable.FrameImageView_fic_border_width, defaultBorderSize));
            setBorderColor(attributes.getColor(R.styleable.FrameImageView_fic_border_color, Color.WHITE));
        }

        // Init Shadow
        if (attributes.getBoolean(R.styleable.FrameImageView_fic_shadow, false)) {
            shadowRadius = DEFAULT_SHADOW_RADIUS;
            drawShadow(attributes.getFloat(R.styleable.FrameImageView_fic_shadow_radius, shadowRadius), attributes.getColor(R.styleable.FrameImageView_fic_shadow_color, shadowColor));
        }
    }
    //endregion

    //region Set Attr Method
    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        requestLayout();
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);
        invalidate();
    }

    public void addShadow() {
        if (shadowRadius == 0)
            shadowRadius = DEFAULT_SHADOW_RADIUS;
        drawShadow(shadowRadius, shadowColor);
        invalidate();
    }

    public void setShadowRadius(float shadowRadius) {
        drawShadow(shadowRadius, shadowColor);
        invalidate();
    }

    public void setShadowColor(int shadowColor) {
        drawShadow(shadowRadius, shadowColor);
        invalidate();
    }


    void updateScale(float mfactor) {
       // this.setScaleX(mfactor);
       // this.setScaleY(mfactor);
    }

    void updateMousePoint2(float x, float y) {
       // this.setTranslationX(x);
       // this.setTranslationY(y);
    }


    //region Draw Method
    @Override
    public void onDraw(Canvas canvas) {
        // -Load the bitmap
        loadBitmap();
        // -Check if image isn't null
        if (image == null)
            return;
        if (!isInEditMode()) {
            canvasSize = Math.min(canvas.getWidth(), canvas.getHeight());
            canvas_sw = canvas.getWidth();
            canvas_sh = canvas.getHeight();
        }
        canvas.drawRect(outter, paintBorder);
        canvas.drawRect(innerRec, paint);
    }

    public void setImageBitmap(Bitmap map) {
        this.image = map;
        loadBitmap();
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
        if (image != null)
            updateShader();
    }

    private void drawShadow(float shadowRadius, int shadowColor) {
        this.shadowRadius = shadowRadius;
        this.shadowColor = shadowColor;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        }
        paintBorder.setShadowLayer(shadowRadius, 0.0f, shadowRadius / 2, shadowColor);
    }

    private float centerx;
    private float centery;
    private float _tx;
    private float _ty;
    private boolean useTouchPoint = false;


    private void updateMousePoint(float x, float y) {
        useTouchPoint = true;
        _tx = x;
        _ty = y;
        updateShader();
        invalidate();
    }

    private void determinCenter() {
        if (useTouchPoint) {
            centerx = _tx;
            centery = _ty;
        } else {
            centerx = canvas_sw / 2f;
            centery = canvas_sh / 2f;
        }
    }

    private void updateShader() {
        if (image == null)
            return;
        float available_w = canvas_sw - borderWidth * 2f - shadowRadius * 2f;
        float available_h = canvas_sh - borderWidth * 2f - shadowRadius * 2f;
        int size = Math.min(image.getWidth(), image.getHeight());
        int width = (image.getWidth() - size) / 2;
        int height = (image.getHeight() - size) / 2;
        float dx = available_w / (float) image.getWidth();
        float dy = available_h / (float) image.getHeight();
        scale_total = Math.min(dx, dy);
        determinCenter();
        final float w_float = scale_total * image.getWidth();
        final float h_float = scale_total * image.getHeight();
        innerRec.set(
                centerx - w_float / 2f,
                centery - h_float / 2f,
                centerx + w_float / 2f,
                centery + h_float / 2f
        );
        outter.set(
                centerx - w_float / 2f - borderWidth,
                centery - h_float / 2f - borderWidth,
                centerx + w_float / 2f + borderWidth,
                centery + h_float / 2f + borderWidth
        );
        BitmapShader shader = new BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        // Center Image in Shader
        if (width != 0 || height != 0) {
            // source isn't square, move viewport to center
            matrix.reset();
            matrix.postScale(scale_total, scale_total);
            matrix.postTranslate(
                    (available_w - w_float) / 2f + shadowRadius + borderWidth,
                    (available_h - h_float) / 2f + shadowRadius + borderWidth
            );
            shader.setLocalMatrix(matrix);
        }
        // Set Shader in Paint
        paint.setShader(shader);
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
                   // invalidate();
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
    }

}
