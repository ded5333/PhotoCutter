package com.astend.android.photocutter.ui.crop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CropView extends View {

  private Rect rect = new Rect(100, 100, 200, 200);
  private Rect srcImgRect = new Rect(0, 0, 200, 200);
  private Rect cropRect = new Rect(100, 100, 200, 200);
  private Rect dstImgRect = new Rect(0, 0, 200, 200);
  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private float x = 0;
  private float y = 0;

  CropPoint[] cropPoints = new CropPoint[4];


  Bitmap bitmap = null;

  public CropView(Context context) {
    super(context);
    init();
  }

  public CropView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();

  }

  public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();

  }

  public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();

  }

  private void init() {
    paint.setColor(Color.YELLOW);
    paint.setStrokeWidth(1);
    paint.setStyle(Paint.Style.STROKE);

    setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY();
        Log.d("TAG", " x: " + x + " y: " + y);
        cropPoints[0].x = x;
        cropPoints[0].y = y;
        invalidate();
        return true;
      }
    });

    setOnDragListener(new OnDragListener() {
      @Override
      public boolean onDrag(View v, DragEvent event) {

        x = event.getX();
        y = event.getY();
        Log.d("TAG", " x: " + x + " y: " + y);

        cropPoints[0].x = x;
        cropPoints[0].y = y;
        invalidate();
        return true;
      }
    });
  }

  public void setImageBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
    Log.d("TAG", "SetImageBitmap");
    srcImgRect.right = bitmap.getWidth();
    srcImgRect.bottom = bitmap.getHeight();

    invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (bitmap != null)
      canvas.drawBitmap(bitmap, srcImgRect, dstImgRect, bitmapPaint);
    //  canvas.drawBitmap(bitmap,0,0,bitmapPaint);
    canvas.drawRect(rect, paint);

    for (int i = 0; i <cropPoints.length ; i++) {
      CropPoint point = cropPoints[i];
    //  canvas.drawCircle(point.x - point.radiusSize/2 ,point.y + point.radiusSize/2 ,point.radiusSize,paint);
      canvas.drawCircle(point.x ,point.y,point.radiusSize,paint);
    }



  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    Log.d("TAG", "onSizeChange ");
    Log.d("TAG", " w: " + w + " h: " + h);
    dstImgRect.right = h;
    dstImgRect.bottom = w;

    int spacingVertical = (int) ((h / 100f) * 10);
    int spacingHorizontal = (int) ((w / 100f) * 10);

    //todo перепроверить расчеты квадрата и точек

    rect = new Rect(spacingHorizontal,spacingVertical,w - spacingVertical ,h -  spacingHorizontal);

    CropPoint topLeft = new CropPoint();
    CropPoint topRight = new CropPoint();
    CropPoint bottomRight = new CropPoint();
    CropPoint bottomLeft = new CropPoint();

    cropPoints[0] = topLeft;
    cropPoints[1] = topRight;
    cropPoints[2] = bottomRight;
    cropPoints[3] = bottomLeft;

    setPointPos(topLeft, spacingVertical,spacingHorizontal);
    setPointPos(topRight, w - spacingVertical,spacingHorizontal);
    setPointPos(bottomRight, w - spacingVertical,h -  spacingHorizontal);
    setPointPos(bottomLeft,  spacingVertical,h -  spacingHorizontal);

  }

  private void setPointPos(CropPoint cropPoint,int x , int y){
    cropPoint.radiusSize = 15;
    cropPoint.x = x;
    cropPoint.y = y;

  }

  private void initPoints(int w, int h){



  }

}
