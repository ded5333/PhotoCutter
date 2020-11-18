package com.astend.android.photocutter.ui.crop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
  private Rect dstImgRect = new Rect(0, 0, 200, 200);
  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  boolean isSize;


  /**
   * -1 не выбрана не одна точка<br>
   * в другом случае соотведствует индексу активной точки (cropPoint)
   */
  private int cropPointActivated = -1;
  private final int cropPointActivatedSize = 25;
  private final int cropPointNormalSize = 15;


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
//          if (rect.right - rect.left > 20 && rect.bottom - rect.top > 20) {
//
//
//          }
          onActionMove(event.getX(), event.getY());


        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
          if (cropPointActivated != -1)
            cropPoints[cropPointActivated].setRadiusSize(cropPointNormalSize);
          cropPointActivated = -1;
        }

        invalidate();
        return true;
      }
    });

    setOnDragListener((v, event) -> true);
  }

  public void setImageBitmap(Bitmap bitmap) {
    Matrix matrix = new Matrix();
    matrix.setRotate(90);
    //todo необходимо изменить маштабирование изображения(вписать в квадрат)
    this.bitmap = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    srcImgRect.right = bitmap.getWidth();
    srcImgRect.bottom = bitmap.getHeight();

    invalidate();
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (bitmap != null)
      canvas.drawBitmap(bitmap, srcImgRect, dstImgRect, bitmapPaint);
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
    dstImgRect.right = w;
    dstImgRect.bottom = h;

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

  public void cropBitmap() {
    Log.d("TAG", "Image width    " + bitmap.getWidth() + " heigth " + bitmap.getHeight());
    Log.d("TAG", "cropview width  " + getWidth() + " heigth " + getHeight());
    Log.d("TAG", "rect " + rect.toString());
    bitmap = Bitmap.createBitmap(bitmap, (int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
    invalidate();
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
//     if (rect.right - rect.left > 50 && rect.bottom - rect.top > 50) {
//      return;
//    }


    CropPoint cropPoint = cropPoints[cropPointActivated];

    for (int i = 0; i < cropPoints.length; i++) {
      if (i == cropPointActivated) continue;
//      if (i == 3 && cropPointActivated == 0){
//        continue;
//      } else if (cropPointActivated == 3 && i == 1){
//        continue;
     // }
//     if (cropPointActivated == 0 && i == 1){
//        continue;
//      }

      CropPoint intersection = cropPoints[i];
      //проверка пересичения двух квадратов
      if ((cropPoint.getLeft() >= intersection.getLeft() && cropPoint.getLeft() <= intersection.getRight()
          || cropPoint.getRight() >= intersection.getLeft() && cropPoint.getRight() <= intersection.getRight())
          && (cropPoint.getTop() >= intersection.getTop() && cropPoint.getTop() <= intersection.getBottom()
          || cropPoint.getBottom() >= intersection.getTop() && cropPoint.getBottom() <= intersection.getBottom() )) {

        Log.d("TAG", "Пересикает " + cropPointActivated + " " + i);
        return;


      }
    }


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
