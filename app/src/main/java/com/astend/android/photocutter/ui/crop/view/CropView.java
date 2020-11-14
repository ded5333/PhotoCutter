package com.astend.android.photocutter.ui.crop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CropView extends View {

  private RectF rect = new RectF(100, 100, 200, 200);
  private Rect srcImgRect = new Rect(0, 0, 200, 200);
  private Rect cropRect = new Rect(100, 100, 200, 200);
  private Rect dstImgRect = new Rect(0, 0, 200, 200);
  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


  /**
   * -1 не выбрана не одна точка<br>
   * в другом случае соотведствует индексу активной точки (cropPoint)
   */
  private int cropPointActivated = -1;
  private int cropPointActivatedSize = 25;
  private int cropPointNormalSize = 15;


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
    paint.setStrokeWidth(4);
    paint.setStyle(Paint.Style.STROKE);


    setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          findCropPoint(event.getX(), event.getY());
          return true;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
          onActionMove(event.getX(), event.getY());
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
          cropPoints[cropPointActivated].setRadiusSize(cropPointNormalSize);
          cropPointActivated = -1;
        }

        invalidate();
        return true;
      }
    });

    setOnDragListener(new OnDragListener() {
      @Override
      public boolean onDrag(View v, DragEvent event) {

        //  invalidate();
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

    rect = new RectF(spacingVertical, spacingHorizontal, w - spacingVertical, h - spacingHorizontal);

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
    cropPoint.setRadiusSize(cropPointNormalSize);
    cropPoint.setPosition(x, y);

  }

  private void calculatePoints() {
    for (int i = 0; i < cropPoints.length; i++) {
      if (i == cropPointActivated) continue;

      CropPoint cropPoint = cropPoints[i];

      switch (i) {
        case 0:
          cropPoint.setPosition(rect.left, rect.top);
          break;
        case 1:
          cropPoint.setPosition(rect.right, rect.top);
          break;
        case 2:
          cropPoint.setPosition(rect.right, rect.bottom);
          break;
        case 3:
          cropPoint.setPosition(rect.left, rect.bottom);
          break;
      }
    }
  }

  private void onActionMove(float x, float y) {
    if (cropPointActivated == -1)
      return;

    CropPoint cropPoint = cropPoints[cropPointActivated];
    cropPoint.setPosition(x, y);
    switch (cropPointActivated) {
      case 0:
        rect.left = cropPoint.getX();
        rect.top = cropPoint.getY();
        break;
      case 1:
        rect.top = cropPoint.getY();
        rect.right = cropPoint.getX();
        break;
      case 2:
        rect.right = cropPoint.getX();
        rect.bottom = cropPoint.getY();
        break;
      case 3:
        rect.bottom = cropPoint.getY();
        rect.left = cropPoint.getX();
        break;
    }
    calculatePoints();
  }

  private void findCropPoint(float x, float y) {
    for (int i = 0; i < cropPoints.length; ++i) {
      CropPoint cropPoint = cropPoints[i];
      if (cropPoints[i].getLeft() < x && cropPoints[i].getRight() > x
          && cropPoints[i].getTop() < y && cropPoints[i].getBottom() > y) {
        cropPoint.setRadiusSize(cropPointActivatedSize);

        cropPointActivated = i;
        break;
      }
    }

  }

}
