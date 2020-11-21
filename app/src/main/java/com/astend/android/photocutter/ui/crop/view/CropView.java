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
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CropView extends View {

  private final int cropPointActivatedSize = 25;
  private final int cropPointNormalSize = 15;
  private final int intersectionPadding = 75;

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

    setOnTouchListener(new OnTouchListener() {
      private boolean isBlocked;
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          findCropPoint(event.getX(), event.getY());
          return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
          if (isBlocked)
            return false;
          else
            isBlocked = onActionMove(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          isBlocked = false;
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
      if (cropPointActivated != -1 && i == cropPointActivated)
        canvas.drawCircle(point.getX(), point.getY(), cropPointActivatedSize, paint);
      else
        canvas.drawCircle(point.getX(), point.getY(), cropPointNormalSize, paint);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
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
    Log.d("TAG", "Image width: " + bitmap.getWidth() + " heigth: " + bitmap.getHeight());
    Log.d("TAG", "cropview width: " + getWidth() + " heigth: " + getHeight());
    Log.d("TAG", "rect: " + rect.toString());
    bitmap = Bitmap.createBitmap(
        bitmap, (int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
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

  private boolean onActionMove(float x, float y) {
    if (cropPointActivated == -1)
      return false;

    CropPoint cropPoint = cropPoints[cropPointActivated];
    float saveXPos = cropPoint.getX();
    float saveYPos = cropPoint.getY();

    cropPoint.setPosition(x, y);

    for (int i = 0; i < cropPoints.length; i++) {
      if (i == cropPointActivated) continue;

      CropPoint intersection = cropPoints[i];

      /*Log.d("TAG", "[" + cropPoint.getLeft() + "," + intersection.getTop() + ", " + cropPoint.getRight() + ", " + cropPoint.getBottom() + "]" +
          "   [" + intersection.getLeft() + "," + intersection.getTop() + ", " + intersection.getRight() + ", " + intersection.getBottom() + "]");*/

      if (isIntersectingRectangles(cropPoint, intersection)) {
        Log.d("TAG", "Пересекает " + cropPointActivated + " " + i);
        cropPoint.setPosition(saveXPos, saveYPos);
        return true;
      }
    }

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
    return false;
  }

  private void findCropPoint(float x, float y) {
    for (int i = 0; i < cropPoints.length; ++i) {
      if (cropPoints[i].getLeft() < x && cropPoints[i].getRight() > x
          && cropPoints[i].getTop() < y && cropPoints[i].getBottom() > y) {
        cropPointActivated = i;
        break;
      }
    }
  }

  private boolean isIntersectingRectangles(CropPoint a, CropPoint b) {
    return isIntersectionByX(a, b) && isIntersectionByY(a, b);
  }

  private boolean isIntersectionByX(CropPoint a, CropPoint b) {
    return intersectionPadding - a.getLeft() >= b.getLeft() && a.getLeft() <= b.getRight() - intersectionPadding
        || intersectionPadding + a.getRight() >= b.getLeft() && a.getRight() <= b.getRight() + intersectionPadding;
  }

  private boolean isIntersectionByY(CropPoint a, CropPoint b) {
    return intersectionPadding - a.getTop() >= b.getTop() && a.getTop() <= b.getBottom() - intersectionPadding
        || intersectionPadding + a.getBottom() >= b.getTop() && a.getBottom() <= b.getBottom() + intersectionPadding;
  }

}
