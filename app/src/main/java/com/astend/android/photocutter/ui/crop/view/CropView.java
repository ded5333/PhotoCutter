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
  private Paint paint = new Paint();
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
    paint.setStrokeWidth(10);
    paint.setStyle(Paint.Style.STROKE);

    for (int i = 0; i < cropPoints.length; i++) {
      CropPoint cropPoint = new CropPoint();
      cropPoints[i] = cropPoint;
      cropPoint.x = i * 100 + 50;
      cropPoint.y = i * 100 + 50;
      cropPoint.radiusSize = 15;

    }

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
      canvas.drawCircle(point.x,point.y,point.radiusSize,paint);
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

    rect = new Rect(spacingHorizontal,spacingVertical,h - spacingVertical ,w -  spacingHorizontal);


  }

}
