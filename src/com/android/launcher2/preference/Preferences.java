/*
 * Copyright (C) 2011 The Pekall Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2.preference;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.util.Log;

import com.android.launcher2.Launcher;
import com.android.launcher2.LauncherApplication;
import com.android.launcher2.LauncherModel;
import com.android.launcher2.R;
import com.android.launcher2.theme.GalleryPreference;
import com.android.launcher2.theme.ThemePackageManager;
import com.android.launcher2.theme.ThemePackageManager.ThemeInfo;
import com.android.launcher2.theme.ThemePackageManager.ThemeManagerCallback;
import com.android.launcher2.theme.ThemeSettings;

public class Preferences extends PreferenceActivity implements ThemeManagerCallback {

    private static final String TAG = "Launcher.Preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs =
            getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
                editor.commit();

        // Remove some preferences on large screens
        if (LauncherApplication.isScreenLarge()) {
            PreferenceGroup homescreen = (PreferenceGroup) findPreference("ui_homescreen");
            homescreen.removePreference(findPreference("ui_homescreen_grid"));
            homescreen.removePreference(findPreference("ui_homescreen_screen_padding_vertical"));
            homescreen.removePreference(findPreference("ui_homescreen_screen_padding_horizontal"));
            homescreen.removePreference(findPreference("ui_homescreen_indicator"));

            PreferenceGroup drawer = (PreferenceGroup) findPreference("ui_drawer");
            drawer.removePreference(findPreference("ui_drawer_indicator"));
        }

        Preference version = findPreference("application_version");
        version.setTitle(getString(R.string.application_name));
        
        //Pekall LK init.
        initThemePreference();
    }
    
    //Pekall LK add static variable for theme
	private static final int DIALOG_WAITING = 1;
	private static final int MSG_APPLY_THEME = 0;
	private static final int MSG_SHOW_WAIT_DIALOG = MSG_APPLY_THEME + 1;
	private static final int MSG_DISMISS_WAIT_DIALOG = MSG_SHOW_WAIT_DIALOG + 1;
	private static final int MSG_UPDATE_THEME_INFOS = MSG_DISMISS_WAIT_DIALOG + 1;
	public static final int EXIT_LAUNCHER_FLAG = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_APPLY_THEME:
				saveTheme(true);
				dismissDialog(DIALOG_WAITING);
				break;
			case MSG_SHOW_WAIT_DIALOG:
				Log.d(TAG, "show dialog ====");
				showDialog(DIALOG_WAITING);
				mHandler.sendMessageDelayed(mHandler
						.obtainMessage(MSG_APPLY_THEME), 200);
				break;
			case MSG_DISMISS_WAIT_DIALOG:
				dismissDialog(DIALOG_WAITING);
				break;

			case MSG_UPDATE_THEME_INFOS:
				final GalleryPreference lp = (GalleryPreference) findPreference("themePackageName");
				lp.setThemeInfos((List<ThemePackageManager.ThemeInfo>) msg.obj);
				break;
			default:
				break;
			}
		}
	};
    
	public void applyTheme() {
		final GalleryPreference lp = (GalleryPreference) findPreference("themePackageName");
		final String packageName = lp.getValue();
		if (packageName.equals(ThemeSettings.getCurrentThemePackage())) {
			return;
		}
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_WAIT_DIALOG));
	}
	
	private void saveTheme(boolean setWallPaper) {
		final GalleryPreference lp = (GalleryPreference) findPreference("themePackageName");
		final String packageName = lp.getValue();

		PreferencesProvider.Interface.General.saveThemeSetting(this, packageName, setWallPaper);
		PreferencesProvider.Interface.General.setWallPaper(this, packageName);
		applyThemeDone();
	}
	
	private void applyThemeDone() {
		final Intent intent = new Intent();
		intent.setComponent(getComponentName());
		intent.setClass(this, Launcher.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void callbackForThemeManager(List<ThemeInfo> themeInfos) {
		Message msg = mHandler.obtainMessage(MSG_UPDATE_THEME_INFOS);
		msg.obj = themeInfos;
		mHandler.sendMessage(msg);
	}
	
	private void initThemePreference() {
		final GalleryPreference lp = (GalleryPreference) findPreference("themePackageName");
		lp.setActivity(this);
		LauncherApplication app = (LauncherApplication) getApplication();
		LauncherModel model = app.getModel();
		ThemePackageManager themePackageManager = model.getThemePackageManager(this);
		themePackageManager.setCallback(this);
		final Preference themePrefences = findPreference("themePackageName");
		themePrefences.setSummary(lp.getEntry());
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING: {
			return ProgressDialog.show(this, null,
					getText(R.string.pref_info_waiting), true, false);
		}
		}
		return super.onCreateDialog(id);
	}
}
