package com.astend.android.photocutter.ui.crop.view;

public class CropPoint {
  private float x;
  private float y;
  private int radiusSize;

  private int left;//r1.x
  private int top;//r1.y
  private int right;//r3.x
  private int bottom;//r3.y

  public void setPosition(float x,float y) {
    this.x = x;
    this.y = y;

    left = (int) (x - radiusSize);
    top = (int) (y - radiusSize);
    right = (int) (x + radiusSize);
    bottom = (int) (y + radiusSize);

  }

  public void setRadiusSize(int radiusSize) {
    this.radiusSize = radiusSize;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public int getRadiusSize() {
    return radiusSize;
  }

  public int getLeft() {
    return left;
  }

  public int getTop() {
    return top;
  }

  public int getRight() {
    return right;
  }

  public int getBottom() {
    return bottom;
  }
}
