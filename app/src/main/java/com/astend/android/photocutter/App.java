package com.astend.android.photocutter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {

  //todo переделать SharedPreferences в WrapperSharedPreferences
  public static SharedPreferences preferences = null;

  @Override
  public void onCreate() {
    super.onCreate();
    preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

  }
}
