package com.astend.android.photocutter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.io.File;

public class ExtendedImageView extends AppCompatImageView {

  private int imgSrcHeight = 0;
  private int imgSrcWidth = 0;
  private int innerBitmapWidth = 0;
  private int innerBitmapHeight = 0;
  private float widthAspectRatio = 0;
  private float heightAspectRatio = 0;
  private String imagePath = null;
  private boolean isSizeSetup = false;
  private Bitmap bitmap;

  public ExtendedImageView(Context context) {
    super(context);
  }

  public ExtendedImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ExtendedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setImage(String photoPath) {
    if (photoPath == null || photoPath.isEmpty()) {
      Log.w("TAG", getClass().getSimpleName() + ".setImage photoPath is null or empty");
      return;
    }

    imagePath = photoPath;

    if (!isSizeSetup)
      return;

    loadImageBitmap();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    Log.d("TAG", "View: " + getWidth() + " " + getHeight());

    isSizeSetup = true;

    if (imagePath == null)
      return;

    loadImageBitmap();
  }

  //ToDo вынести в отдельный поток
  private void loadImageBitmap() {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;

    if (imagePath.equals("test")) {
      String testFileName = "img6000x4000.jpg"; //"img1800x1200.jpg"
      File file  = new File(getContext().getCacheDir(), testFileName);

      if (!file.exists())
        Utils.copyFileFromAssets(getContext(), "img6000x4000.jpg", file);

      BitmapFactory.decodeFile(file.getAbsolutePath(), options);
      loadSampleSize(options);

      bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }
    else {
      BitmapFactory.decodeFile(imagePath, options);
      loadSampleSize(options);
      bitmap = BitmapFactory.decodeFile(imagePath, options);
    }

    Log.d("TAG", "Simple bitmap size: " + bitmap.getWidth() + " " + bitmap.getHeight());
    setImageBitmap(bitmap);

    calculateInnerBitmapSize(bitmap.getWidth(), bitmap.getHeight());
  }

  private void loadSampleSize(BitmapFactory.Options options) {
    imgSrcHeight = options.outHeight;
    imgSrcWidth = options.outWidth;

    if (getWidth() == 0 || getHeight() == 0) {
      Log.w("TAG", getClass().getSimpleName() + ".loadSampleSize view size is zero!");
      return;
    }

    Log.d("TAG", "SourceImg: " + imgSrcWidth + " " + imgSrcHeight);

    int sampleSize = Utils.calculateInSampleSize(options, getWidth(), getHeight());
    Log.d("TAG", "sampleSize: " + sampleSize);
    options.inSampleSize = sampleSize;
    options.inJustDecodeBounds = false;
  }

  private void calculateInnerBitmapSize(int sampleBitmapWidth, int sampleBitmapHeight) {
    //тут нужно высчитать размер отрисованого битмапа
    if (getWidth() > getHeight()) {
      //если длина больше высоты

      // шото типа sampleBitmapWidth / 100 это 1 процент загруженого изображения
      // шото типа getWidth() / 100  это 1 процент размера вюшки
      // тут нужно просчитать на сколько процентов сжалось изображение по длине
      // и узнать на сколько сжалось по высоте
      // и перевести в пиксели и присвоить значения в переменные:
      innerBitmapWidth = 0;
      innerBitmapHeight = 0;
    }
    else {
      //в другом случае  высота равно длине или высота больше длины

      innerBitmapWidth = 0;
      innerBitmapHeight = 0;
    }

    Log.d("TAG", "Тут нужно сделать расчет соотношения исходного изображения и " +
        "внутреннего (отрисованого) bitmap");

    this.widthAspectRatio = (float) imgSrcWidth / innerBitmapWidth;
    this.heightAspectRatio = (float) imgSrcHeight / innerBitmapHeight;

    //зная соотношения, можно будет высчитывать обрезку
  }

  public int getImgSrcHeight() {
    return imgSrcHeight;
  }

  public int getImgSrcWidth() {
    return imgSrcWidth;
  }

  public float getWidthAspectRatio() {
    return widthAspectRatio;
  }

  public float getHeightAspectRatio() {
    return heightAspectRatio;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }
}