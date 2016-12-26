package com.mikhaellophes.frameImageView.T3D;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mikhaellophes.frameImageView.T3D.AExampleFragment;
import com.mikhaellophes.frameImageView.exception.ExceptionDialog;

import org.rajawali3d.Object3D;
import org.rajawali3d.curves.ICurve3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.util.Capabilities;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;
import org.rajawali3d.view.SurfaceView;

import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hesk on 16年12月23日.
 */

public abstract class SituationFragment extends AExampleFragment implements View.OnTouchListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((View) mRenderSurface).setOnTouchListener(this);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.TOP);

        TextView label = new TextView(getActivity());
        label.setText("this is okay and we are good to go now.");
        label.setTextSize(14);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(0xFFFFFFFF);
        label.setHeight(100);
        ll.addView(label);
        mLayout.addView(ll);
        additionItems(ll);
        return mLayout;
    }

    protected void additionItems(LinearLayout ll) {

    }


    @Override
    protected void onBeforeApplyRenderer() {
        ((SurfaceView) mRenderSurface).setTransparent(true);
        super.onBeforeApplyRenderer();
    }

    protected abstract void glReady();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((FrameMerchandiseRenderer) mRenderer).getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                ((FrameMerchandiseRenderer) mRenderer).moveSelectedObject(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ((FrameMerchandiseRenderer) mRenderer).stopMovingSelectedObject();
                break;
        }
        return true;
    }

    @Override
    public FrameMerchandiseRenderer createRenderer() {
        return new FrameMerchandiseRenderer(getActivity(), this);
    }

    @Override
    public void onClick(View view) {

    }

    private Canvas nFrameCanvas;
    private Paint paint;
    private Paint paintBorder;
    private Paint paintcontent;
    private Matrix matrix;
    private RectF outter;
    private RectF innerRec;
    private Texture mItemTextureAlpha;

    private int mFrameCount;
    private int innerPaintColor;
    private int outterPaintColor;
    private int middlePaintColor;
    private boolean mShouldUpdateTexture;
    private float borderWidth, whiteSpace;

    protected void projectContextFrame(final Bitmap bitmap, final float whitespace, final float borderWidth, final @ColorInt int inner, final @ColorInt int outer, final @ColorInt int middle) {
        new Thread(new Runnable() {
            public void run() {
                matrix = new Matrix();
                outter = new RectF();
                innerRec = new RectF();
                paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setAntiAlias(true);
                paintBorder = new Paint();
                paintBorder.setAntiAlias(true);
                paintBorder.setStyle(Paint.Style.FILL);
                paintcontent = new Paint();
                paintcontent.setAntiAlias(true);
                paintcontent.setColor(Color.WHITE);
                paintcontent.setFilterBitmap(true);
                paintcontent.setDither(true);
                paintcontent.setStyle(Paint.Style.FILL);
                setFrameConfig(whitespace, borderWidth, inner, outer, middle);

                Bitmap mBtnBitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888);
                nFrameCanvas = new Canvas(mBtnBitmap);
                float ratio = updateShader(bitmap, mBtnBitmap);
                // -- Clear the canvas, transparent
                //
                // -- adding content
                //  paint.setColor(innerPaintColor);
                //  paintBorder.setColor(outterPaintColor);

                //nFrameCanvas.drawRect(innerRec, paint);
                ((FrameMerchandiseRenderer) mRenderer).loadContextFrame(mBtnBitmap, ratio);
                // mShouldUpdateTexture = true;
            }
        }).start();
    }

    void setFrameConfig(float space, float border, @ColorInt int innerPaintColor, @ColorInt int outterPaintColor, @ColorInt int middlePaintColor) {
        this.borderWidth = border;
        this.whiteSpace = space;
        this.outterPaintColor = outterPaintColor;
        this.middlePaintColor = middlePaintColor;
        this.innerPaintColor = innerPaintColor;
    }

    float updateShader(Bitmap content, Bitmap basecanvas) {

        float centerx = (float) basecanvas.getWidth() / 2f;
        float centery = (float) basecanvas.getHeight() / 2f;
        final float w_float = basecanvas.getWidth();
        final float h_float = basecanvas.getHeight();


        // float available_w = canvas_sw - borderWidth * 2f - shadowRadius * 2f;
        // float available_h = canvas_sh - borderWidth * 2f - shadowRadius * 2f;
        // int size = Math.min(generatedBitmap.getWidth(), generatedBitmap.getHeight());
        // int width = (generatedBitmap.getWidth() - size) / 2;
        // int height = (generatedBitmap.getHeight() - size) / 2;
        // float dx = available_w / (float) generatedBitmap.getWidth();
        // float dy = available_h / (float) generatedBitmap.getHeight();
        // scale_total = Math.min(dx, dy);
        // determinCenter();

        outter.set(
                centerx - w_float / 2f + borderWidth,
                centery - h_float / 2f + borderWidth,
                centerx + w_float / 2f - borderWidth,
                centery + h_float / 2f - borderWidth
        );
        float inner_left = centerx - ((w_float - whiteSpace * 2f) / 2f);
        float inner_top = centery - ((h_float - whiteSpace * 2f) / 2f);
        float inner_right = centerx + ((w_float - whiteSpace * 2f) / 2f);
        float inner_bottom = centery + ((h_float - whiteSpace * 2f) / 2f);
        float sc_w = (inner_right - inner_left) / content.getWidth();
        float sc_h = (inner_bottom - inner_top) / content.getHeight();

        innerRec.set(inner_left, inner_top, inner_right, inner_bottom);
        Matrix transformation = new Matrix();
        transformation.postTranslate(inner_left, inner_top);
        transformation.preScale(sc_w, sc_h);

        paintBorder.setColor(outterPaintColor);
        //ColorFilter ne = new ColorFilter();
        //paintBorder.setColorFilter(ne);
        nFrameCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        nFrameCanvas.drawRect(outter, paintBorder);
        nFrameCanvas.drawRect(innerRec, paint);
        // nFrameCanvas.drawBitmap(content, inner_left, inner_top, paintcontent);
        nFrameCanvas.drawBitmap(content, transformation, paintcontent);
        // BitmapShader shader = new BitmapShader(basecanvas, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        // Set Shader in Paint
        // paint.setShader(shader);

        float ratio = (float) content.getWidth() / (float) content.getHeight();
        return ratio;
    }


    protected void loadBackgroundRes(int resID, float ratio_width_over_height) {
        ((FrameMerchandiseRenderer) mRenderer).loadBackgroundRes(resID, ratio_width_over_height);
    }

    private void showExceptionDialog(String title, String message) {
        ExceptionDialog exceptionDialog = ExceptionDialog.newInstance(title, message);
        exceptionDialog.show(getFragmentManager(), ExceptionDialog.TAG);
    }

    private final class FrameMerchandiseRenderer extends AExampleRenderer implements OnObjectPickedListener {

        private ObjectColorPicker mPicker;
        private Object3D mSelectedObject;
        private int[] mViewport;
        private double[] mNearPos4;
        private double[] mFarPos4;
        private Vector3 mNearPos;
        private Vector3 mFarPos;
        private Vector3 mNewObjPos;
        private Matrix4 mViewMatrix;
        private Matrix4 mProjectionMatrix;

        private Object3D mETC1Plane;
        private Object3D frameCanvas;
        private final int NUM_POINTS = 100;


        FrameMerchandiseRenderer(Context context, @Nullable AExampleFragment fragment) {
            super(context, fragment);

        }

        void loadContextFrame(Bitmap bitmap, float ratio) {
            frameCanvas = new Plane(2.5f, 2.5f, 1, 1);
            //  frameCanvas.setMaterial(new SimpleMaterial(AMaterial.ALPHA_MASKING));
            /* frameCanvas.addTexture(mTextureManager.addTexture(
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.newcastle))); */
            mItemTextureAlpha = new Texture("png", bitmap);
            Material _mat = new Material();
            // _mat.enableTime(true);
            // _mat.addPlugin(new CustomMaterialPlugin());
            _mat.enableLighting(true);
            _mat.setColorInfluence(0.0f);

            frameCanvas.setMaterial(_mat);
            frameCanvas.setRotation(Vector3.Axis.Y, 15f);
            //   frameCanvas.setDoubleSided(true);
            frameCanvas.setColor(0xffffff);
            //frameCanvas.setColor((int) (Math.random() * 0xffffff));
            frameCanvas.setPosition(0, 0, 0);

            try {
                _mat.addTexture(mItemTextureAlpha);
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }

            mPicker.registerObject(frameCanvas);
            getCurrentScene().addChild(frameCanvas);
        }

        void loadBackgroundRes(int res, float wh_ratio) {
            Texture _tx = new Texture("jpg", res);
            fromTexture(_tx, wh_ratio);
        }

        /**
         * the loaded texture with compression
         *
         * @param tx    the obj
         * @param ratio the cal ratio
         */
        private void fromTexture(Texture tx, float ratio) {
            try {
                Material _mat = new Material();
                _mat.addTexture(tx);
                _mat.setColorInfluence(0);
                // float w = mViewport[2] / (float) 1000f;
                // int h = mViewport[3];
                float c = 5.5f;
                mETC1Plane = new Plane(ratio * c, c, 1, 1);
                mETC1Plane.setMaterial(_mat);
                mETC1Plane.setPosition(0, 0, 0);
                // mPicker.registerObject(mETC1Plane);
                getCurrentScene().addChild(mETC1Plane);
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void initScene() {
            if (Capabilities.getGLESMajorVersion() < 3) {
                showExceptionDialog("ETC2 Not Supported", "This device does not support OpenGL ES 3.0 and cannot use ETC2 textures.");
                return;
            }
            mViewport = new int[]{0, 0, getViewportWidth(), getViewportHeight()};
            mNearPos4 = new double[4];
            mFarPos4 = new double[4];
            mNearPos = new Vector3();
            mFarPos = new Vector3();
            mNewObjPos = new Vector3();
            mViewMatrix = getCurrentCamera().getViewMatrix();
            mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
            mPicker = new ObjectColorPicker(this);
            mPicker.setOnObjectPickedListener(this);

            DirectionalLight light = new DirectionalLight(-1, 0, -1);
            light.setPower(1.5f);
            getCurrentScene().addLight(light);
            getCurrentCamera().setZ(10);
            getCurrentCamera().setPosition(0, 0, 7);



         /*   EllipticalOrbitAnimation3D camAnim = new EllipticalOrbitAnimation3D(
                    new Vector3(3, 2, 10),
                    new Vector3(1, 0, 8),
                    0, 359);

            camAnim.setDurationMilliseconds(20000);
            camAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
            camAnim.setTransformable3D(getCurrentCamera());
            getCurrentScene().registerAnimation(camAnim);
            camAnim.play();

            getCurrentCamera().enableLookAt();
            getCurrentCamera().setLookAt(0, 0, 0);*/

            glReady();
        }


        @Override
        public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
            super.onRenderSurfaceSizeChanged(gl, width, height);
            mViewport[2] = getViewportWidth();
            mViewport[3] = getViewportHeight();
            mViewMatrix = getCurrentCamera().getViewMatrix();
            mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
        }


        void getObjectAt(float x, float y) {
            mPicker.getObjectAt(x, y);
        }

        public void onObjectPicked(@NonNull Object3D object) {
            mSelectedObject = object;
        }

        @Override
        public void onNoObjectPicked() {
            RajLog.w("No object picked!");
        }

        void moveSelectedObject(float x, float y) {
            if (mSelectedObject == null)
                return;
            //
            // -- unproject the screen coordinate (2D) to the camera's near plane
            //
            GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
                    mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

            //
            // -- unproject the screen coordinate (2D) to the camera's far plane
            //
            GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
                    mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

            //
            // -- transform 4D coordinates (x, y, z, w) to 3D (x, y, z) by dividing
            // each coordinate (x, y, z) by w.
            //
            mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
                    / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
            mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
                    mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

            //
            // -- now get the coordinates for the selected object
            //
            double factor = (Math.abs(mSelectedObject.getZ()) + mNearPos.z)
                    / (getCurrentCamera().getFarPlane() - getCurrentCamera()
                    .getNearPlane());

            mNewObjPos.setAll(mFarPos);
            mNewObjPos.subtract(mNearPos);
            mNewObjPos.multiply(factor);
            mNewObjPos.add(mNearPos);

            mSelectedObject.setX(mNewObjPos.x);
            mSelectedObject.setY(mNewObjPos.y);
        }

        void stopMovingSelectedObject() {
            mSelectedObject = null;
        }

        private void drawCurve(ICurve3D curve, int color, Vector3 position) {
            Material lineMaterial = new Material();

            Stack<Vector3> points = new Stack<Vector3>();
            for (int i = 0; i <= NUM_POINTS; i++) {
                Vector3 point = new Vector3();
                curve.calculatePoint(point, (float) i / (float) NUM_POINTS);
                points.add(point);
            }

            Line3D line = new Line3D(points, 1, color);
            line.setMaterial(lineMaterial);
            line.setPosition(position);
            getCurrentScene().addChild(line);
        }

    }
}
/**
 * //
 * // -- Quadratic Bezier Curve
 * //
 * <p>
 * ICurve3D curve = new CubicBezierCurve3D(new Vector3(-1, 0, 0),
 * new Vector3(-1, 1.3f, 0), new Vector3(1, -1.9f, 0),
 * new Vector3(1, 0, 0));
 * <p>
 * drawCurve(curve, 0xffffff, new Vector3(0, 2, 0));
 * <p>
 * //
 * // -- Linear Bezier Curve
 * //
 * <p>
 * curve = new LinearBezierCurve3D(new Vector3(-1, 0, 0), new Vector3(1, 0, 0));
 * <p>
 * drawCurve(curve, 0xffff00, new Vector3(0, 1f, 0));
 * <p>
 * //
 * // -- Quadratic Bezier Curve
 * //
 * curve = new QuadraticBezierCurve3D(new Vector3(-1, 0, 0),
 * new Vector3(.3f, 1, 0), new Vector3(1, 0, 0));
 * <p>
 * drawCurve(curve, 0x00ff00, new Vector3(0, 0, 0));
 * <p>
 * //
 * // -- Catmull Rom Curve
 * //
 * CatmullRomCurve3D catmull = new CatmullRomCurve3D();
 * catmull.addPoint(new Vector3(-1.5f, 0, 0)); // control point 1
 * catmull.addPoint(new Vector3(-1, 0, 0)); // start point
 * catmull.addPoint(new Vector3(-.5f, .3f, 0));
 * catmull.addPoint(new Vector3(-.2f, -.2f, 0));
 * catmull.addPoint(new Vector3(.1f, .5f, 0));
 * catmull.addPoint(new Vector3(.5f, -.3f, 0));
 * catmull.addPoint(new Vector3(1, 0, 0)); // end point
 * catmull.addPoint(new Vector3(1.5f, -1, 0)); // control point 2
 * <p>
 * drawCurve(catmull, 0xff0000, new Vector3(0, -1, 0));
 * <p>
 * //
 * // -- Compound path
 * //
 * <p>
 * CompoundCurve3D compound = new CompoundCurve3D();
 * compound.addCurve(new CubicBezierCurve3D(new Vector3(-1, 0, 0),
 * new Vector3(-1, 1.3f, 0), new Vector3(-.5f, -1.9f, 0),
 * new Vector3(-.5f, 0, 0)));
 * compound.addCurve(new LinearBezierCurve3D(new Vector3(-.5f, 0, 0),
 * new Vector3(0, 0, 0)));
 * compound.addCurve(new QuadraticBezierCurve3D(new Vector3(0, 0, 0),
 * new Vector3(.3f, 1, 0), new Vector3(.5f, 0, 0)));
 * <p>
 * catmull = new CatmullRomCurve3D();
 * catmull.addPoint(new Vector3(0, 1, 0)); // control point 1
 * catmull.addPoint(new Vector3(.5f, 0, 0)); // start point
 * catmull.addPoint(new Vector3(.7f, .3f, 0));
 * catmull.addPoint(new Vector3(.75f, -.2f, 0));
 * catmull.addPoint(new Vector3(.9f, .5f, 0));
 * catmull.addPoint(new Vector3(1, 0, 0)); // end point
 * catmull.addPoint(new Vector3(1.5f, -1, 0)); // control point 2
 * <p>
 * compound.addCurve(catmull);
 * <p>
 * drawCurve(compound, 0xff3333, new Vector3(0, -2, 0));
 **/