package com.astend.android.photocutter.ui.crop.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.astend.android.photocutter.R;
import com.astend.android.photocutter.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropView extends View {

  private final int cropPointActivatedRadiusSize = 25;
  private final int cropPointNormalRadiusSize = 15;
  private final int intersectionPadding = 100;
  private final int cropPointPadding = 50;

  private RectF rect = new RectF(100, 100, 200, 200);
  private Rect dstImgRect = new Rect(0, 0, 200, 200);
  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public File croppedFile = null;

  /**
   * -1 не выбрана не одна точка<br>
   * в другом случае соотведствует индексу активной точки (cropPoint)
   */
  private int cropPointActivated = -1;
  private CropPoint[] cropPoints = new CropPoint[4];

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
          if (findCropPoint(event.getX(), event.getY()))
            invalidate();
          return true;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
          if (isBlocked)
            return false;
          else
            isBlocked = onActionMove(event.getX(), event.getY());
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
          isBlocked = false;
          cropPointActivated = -1;
        }

        invalidate();
        return true;
      }
    });

    setOnDragListener((v, event) -> true);
  }

  /*public void setImageBitmap(Bitmap bitmap) {
    Matrix matrix = new Matrix();
    matrix.setRotate(90);
    //todo необходимо изменить маштабирование изображения(вписать в квадрат)
    this.bitmap = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    srcImgRect.right = bitmap.getWidth();
    srcImgRect.bottom = bitmap.getHeight();

    invalidate();
  }*/

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    /*if (bitmap != null)
      canvas.drawBitmap(bitmap, srcImgRect, dstImgRect, bitmapPaint);*/
    canvas.drawRect(rect, paint);


    for (int i = 0; i < cropPoints.length; i++) {
      CropPoint point = cropPoints[i];
      if (cropPointActivated != -1 && i == cropPointActivated)
        canvas.drawCircle(point.getX(), point.getY(), cropPointActivatedRadiusSize, paint);

      else
        canvas.drawCircle(point.getX(), point.getY(), cropPointNormalRadiusSize, paint);
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

  public Bitmap cropBitmap(Bitmap bitmap,
                           String imgPath,
                           int imgSrcWidth,
                           int imgSrcHeight,
                           int imgInnerWidth,
                           int imgInnerHeight) {

    // освобождаем память bitmap
    bitmap.recycle();

    Log.d("TAG", "rect: " + rect.toString());
    float percentInnerWidth = imgInnerWidth / 100f;
    float xPercentLeft = (cropPoints[0].getX() - ((getWidth() - imgInnerWidth) / 2f)) / percentInnerWidth;
    float percentSrcWidth = imgSrcWidth / 100f;
    float xLeftPx = percentSrcWidth * xPercentLeft;

    float percentInnerHeight = imgInnerHeight / 100f;
    float yPercentTop = (cropPoints[0].getY() - ((getHeight() - imgInnerHeight) / 2f)) / percentInnerHeight;
    float percentSrcHeight = imgSrcHeight / 100f;
    float yTopPx = percentSrcHeight * yPercentTop;

    float xPercentRight = (cropPoints[2].getX() - ((getWidth() - imgInnerWidth) / 2f)) / percentInnerWidth;
    float xRightPx = percentSrcWidth * xPercentRight;
    float yPercentBottom = (cropPoints[2].getY() - ((getHeight() - imgInnerHeight) / 2f)) / percentInnerHeight;
    float yBottomPx = percentSrcHeight * yPercentBottom;


    Log.d("TAG", "xLeftPx: " + xLeftPx + " x " + yTopPx);
    Log.d("TAG", " " + xRightPx + " x " + yBottomPx);

    if (yTopPx < 0)
      yTopPx = 0;
    if (imgSrcHeight < yBottomPx)
      yBottomPx = imgSrcHeight;
    if (xLeftPx < 0)
      xLeftPx = 0;
    if (imgSrcWidth < xRightPx)
      xRightPx = imgSrcWidth;

    try {
      bitmap = BitmapFactory.decodeFile(imgPath);

      bitmap = Bitmap.createBitmap(
          bitmap,
          (int) xLeftPx,
          (int) yTopPx,
          (int) (xRightPx - xLeftPx),
          (int) (yBottomPx - yTopPx)
      );

      croppedFile = saveBitmap(bitmap);
    } catch (Exception e) {
      e.printStackTrace();
      croppedFile = null;

      AlertDialog.Builder dialogOutOfMemory = new AlertDialog.Builder(getContext());
      dialogOutOfMemory.setTitle("Важное Сообщение!")
          .setMessage("Недостатоно памяти!")
          .setPositiveButton("Ок", (dialog, which) -> dialog.cancel());
    }
    return bitmap;
  }


  private File saveBitmap(Bitmap bitmap) {
    File file = getContext().getCacheDir();

    String state = Environment.getExternalStorageState();

    if (state.equals(Environment.MEDIA_MOUNTED))
      //file = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
      file = new File(Environment.getExternalStorageDirectory(), getContext().getString(R.string.dir_name));

      if (!file.exists())
        file.mkdir();
      file = new File(file, getContext().getString(R.string.prefix_file_name) + "_" + System.currentTimeMillis() + ".jpg");

    try {
      boolean isCreated = file.createNewFile();
      if (!isCreated) {
        file = getContext().getCacheDir();
        file.createNewFile();
      }
    } catch (IOException e) {
      e.printStackTrace();
      file = getContext().getCacheDir();
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

    try {
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fileOutputStream);
      fileOutputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      //MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), file.getName(), "");
      Utils.addImageToGallery(getContext(), file);
      /*MediaScannerConnection.scanFile(
          getContext(),
          new String[]{file.toString()},
          null,
          (path, uri) -> Log.d("TAG", "onScanCompleted: " + path));*/
    } catch (Exception e) {
      e.printStackTrace();
    }
    return file;

  }


  private void setPointPos(CropPoint cropPoint, int x, int y) {
    cropPoint.setRadiusSize(cropPointNormalRadiusSize);
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

    if (isOutOfBoundsView(cropPoint)) {
      cropPoint.setPosition(
          Math.max(saveXPos, cropPointNormalRadiusSize),
          Math.max(saveYPos, cropPointNormalRadiusSize)
      );
      return false;
    }

    for (int i = 0; i < cropPoints.length; i++) {
      if (i == cropPointActivated) continue;

      CropPoint intersection = cropPoints[i];
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

  private boolean findCropPoint(float x, float y) {
    for (int i = 0; i < cropPoints.length; ++i) {
      if (cropPoints[i].getLeft() - cropPointPadding < x && cropPoints[i].getRight() + cropPointPadding > x
          && cropPoints[i].getTop() - cropPointPadding < y && cropPoints[i].getBottom() + cropPointPadding > y) {
        cropPointActivated = i;
        return true;
      }
    }
    return false;
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

  public boolean isOutOfBoundsView(CropPoint cropPoint) {
    if (cropPointNormalRadiusSize > cropPoint.getX()
        || cropPoint.getX() > getWidth() - cropPointNormalRadiusSize
        || cropPointNormalRadiusSize > cropPoint.getY()
        || cropPoint.getY() > getHeight() - cropPointNormalRadiusSize) {
      return true;
    }
    return false;
  }

  public File getCroppedFile() {
    return croppedFile;
  }
}
