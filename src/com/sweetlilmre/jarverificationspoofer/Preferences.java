package com.sweetlilmre.jarverificationspoofer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences   extends PreferenceActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
    addPreferencesFromResource(R.xml.prefs);
  }
}
