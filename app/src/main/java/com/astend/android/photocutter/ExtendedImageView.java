package com.astend.android.photocutter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ExtendedImageView extends AppCompatImageView {

  public ExtendedImageView(Context context) {
    super(context);

  }

  public ExtendedImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

  }

  public ExtendedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

  }

//  @Override
//  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
//    int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
//
//    Log.d("TAG", "Measure ViewWidth  " + viewWidth + " viewHeight " + viewHeight );
//  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
//    Log.d("TAG", "Size viewWidth  " + w + " viewHeight " + h );
//    setDrawingCacheEnabled(true);
//    buildDrawingCache();
//    Bitmap bitmap = getDrawingCache();
//    setDrawingCacheEnabled(false);
//    Log.d("TAG", "BITMAP viewWidth  " + bitmap.getWidth() + " viewHeight " + bitmap.getHeight() );
//      Rect rect = getDrawable().getBounds();
//      Log.d("TAG", "Rect: " + rect.right + " " + rect.bottom);
//
     // float

  }
}
