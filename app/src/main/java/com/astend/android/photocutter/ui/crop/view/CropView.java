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

  /**
   * -1 не выбрана не одна точка<br>
   * в другом случае соотведствует индексу активной точки (cropPoint)
   */
  private int cropPointActivated = -1;

  private CropPoint[] cropPoints = new CropPoint[4];
  private Bitmap bitmap = null;

  public CropView(Context context) {
    super(context);
    init(context);

  }

  public CropView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);

  }

  public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);

  }

  public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);

  }

  private void init(Context context) {
    paint.setColor(Color.BLUE);
    paint.setStrokeWidth(1);
    paint.setStyle(Paint.Style.STROKE);


    setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY();
        Log.d("TAG", "Touch x: " + x + " y: " + y);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          for (int i = 0; i < cropPoints.length; ++i) {
            if (cropPoints[i].getLeft() < x && cropPoints[i].getRight() > x
                && cropPoints[i].getTop() < y && cropPoints[i].getBottom() > y) {
              Log.d("TAG", "sdfgvhbjkl");

              cropPointActivated = i;
              break;
            }
          }
          return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
          if (cropPointActivated != -1)
            cropPoints[cropPointActivated].setPosition(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          cropPointActivated = -1;
        }

        invalidate();
        return true;
      }
    });

    setOnDragListener(new OnDragListener() {
      @Override
      public boolean onDrag(View v, DragEvent event) {

        x = event.getX();
        y = event.getY();
        Log.d("TAG", "Drag x: " + x + " y: " + y);

//        cropPoints[0].setPosition(x,y);
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
//      canvas.drawBitmap(bitmap,0,0,bitmapPaint);
    canvas.drawRect(rect, paint);

    for (int i = 0; i < cropPoints.length; i++) {
      CropPoint point = cropPoints[i];
      canvas.drawCircle(point.getX(), point.getY(), point.getRadiusSize(), paint);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    Log.d("TAG", "onSizeChange ");
    Log.d("TAG", " w: " + w + " h: " + h);
    dstImgRect.right = h;
    dstImgRect.bottom = w;

    int spacingVertical = (int) ((h / 10f));
    int spacingHorizontal = (int) ((w / 10f));

    rect = new Rect(spacingVertical, spacingHorizontal, w - spacingVertical, h - spacingHorizontal);

    CropPoint topLeft = new CropPoint();
    CropPoint topRight = new CropPoint();
    CropPoint bottomRight = new CropPoint();
    CropPoint bottomLeft = new CropPoint();

    cropPoints[0] = topLeft;
    cropPoints[1] = topRight;
    cropPoints[2] = bottomRight;
    cropPoints[3] = bottomLeft;


    setPointPos(topLeft, spacingVertical, spacingHorizontal);
    setPointPos(topRight, w - spacingVertical, spacingHorizontal);
    setPointPos(bottomRight, w - spacingVertical, h - spacingHorizontal);
    setPointPos(bottomLeft, spacingVertical, h - spacingHorizontal);

  }

  private void setPointPos(CropPoint cropPoint, int x, int y) {
    cropPoint.setRadiusSize(15);
    cropPoint.setPosition(x, y);

  }


}
