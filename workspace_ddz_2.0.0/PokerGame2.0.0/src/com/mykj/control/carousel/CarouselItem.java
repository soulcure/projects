package com.mykj.control.carousel;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CarouselItem extends RelativeLayout
        implements Comparable<CarouselItem> {

    private static final String TAG = CarouselItem.class.getSimpleName();

    private static final int BASE_VIEW_ID = 1;
    private static final int IMAGE_VIEW_ID = BASE_VIEW_ID + 1;
    private ImageView mImage;

    private Context mContext;

    private int index;
    private float currentAngle;
    private float itemX;
    private float itemY;
    private float itemZ;
    private boolean drawn;

    // It's needed to find screen coordinates
    private Matrix mCIMatrix;

    @SuppressWarnings("deprecation")
    public CarouselItem(Context context) {

        super(context);
        mContext = context;

        RelativeLayout container = this;

        mImage = new ImageView(context);
        mImage.setId(IMAGE_VIEW_ID);


        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);


        container.setLayoutParams(params);
        container.addView(mImage);


        params.addRule(RelativeLayout.BELOW, IMAGE_VIEW_ID);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

    }


    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }


    public void setCurrentAngle(float currentAngle) {

        this.currentAngle = currentAngle;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    @Override
    public int compareTo(CarouselItem another) {
        return (int) (another.itemZ - this.itemZ);
    }

    public void setItemX(float x) {
        this.itemX = x;
    }

    public float getItemX() {
        return itemX;
    }

    public void setItemY(float y) {
        this.itemY = y;
    }

    public float getItemY() {
        return itemY;
    }

    public void setItemZ(float z) {
        this.itemZ = z;
    }

    public float getItemZ() {
        return itemZ;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImage.setImageBitmap(bitmap);

    }


    public String getName() {
        return mImage.toString();
    }


    public ImageView getImageView() {
        return this.mImage;
    }

    Matrix getCIMatrix() {
        return mCIMatrix;
    }

    void setCIMatrix(Matrix mMatrix) {
        this.mCIMatrix = mMatrix;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (currentAngle > 150 && currentAngle < 210) {
            mImage.getDrawable().setColorFilter(0xcccccccc, PorterDuff.Mode.MULTIPLY);   //最暗
        } else if (currentAngle > 45 && currentAngle < 315) {
            mImage.getDrawable().setColorFilter(0xeeeeeeee, PorterDuff.Mode.MULTIPLY);   //第四暗
        } else {
            mImage.getDrawable().setColorFilter(0xff000000, PorterDuff.Mode.DST);       //原色 最亮
        }
        super.dispatchDraw(canvas);
    }
}
