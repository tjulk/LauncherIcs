/*
 * Copyright (C) 20012 The Pekall Project
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
package com.android.launcher2.theme;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.android.launcher2.R;
import com.android.launcher2.preference.PreferencesProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.Log;

public class ThemePackageManager {
	private final static String TAG = "ThemePackageManager";
	private List<ThemeInfo> mThemes;
	private boolean mLoaded = false;
	private WeakReference<ThemeManagerCallback> mCallback;

	public ThemePackageManager() {
		mThemes = new ArrayList<ThemeInfo>();
	}

	public void setCallback(ThemeManagerCallback callback) {
		mCallback = new WeakReference<ThemeManagerCallback>(callback);
		callback.callbackForThemeManager(mThemes);
	}

	public boolean isLoadedThemePackage() {
		return mLoaded;
	}

	public void refreshThemePackage(Context context) {
		if (context == null) {
			return;
		}
		mLoaded = false;
		doRefreshThemePackage(context);
	}

	private void initThemes(Context context) {
		ThemeInfo defaultThemeInfo = new ThemeInfo(ThemeSettings.THEME_DEFAULT,
				context.getResources().getText(R.string.default_theme)
						.toString(), R.drawable.theme_preview);
		mThemes.add(defaultThemeInfo);
	}

	private void doRefreshThemePackage(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> themes = findThemePackages(context);
		int size = themes.size();

		mThemes.clear();
		initThemes(context);

		for (int i = 0; i < size; i++) {
			ThemeInfo info = getThemePreviewInfo(themes.get(i), pm);
			if (info != null) {
				mThemes.add(info);
			}
		}
		mLoaded = true;
		if (mCallback != null) {
			ThemeManagerCallback callback = mCallback.get();
			if (callback != null) {
				callback.callbackForThemeManager(mThemes);
			}
		}

	}

	public void updateTheme(Context context, Intent intent) {
		final String action = intent.getAction();
		final String packageName = intent.getData().getSchemeSpecificPart();
		if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			remove(context, packageName);
		} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			add(context, packageName);
		}
	}

	private void remove(Context context, String themePackage) {
		synchronized (mThemes) {
			final int N = mThemes.size();
			ThemeInfo info = null;
			for (int i = 0; i < N; i++) {
				if (mThemes.get(i).packageName.equals(themePackage)) {
					info = mThemes.get(i);
					break;
				}
			}
			if (info != null) {
				mThemes.remove(info);
				if (mCallback != null) {
					final ThemeManagerCallback callback = mCallback.get();
					if (callback != null) {
						callback.callbackForThemeManager(mThemes);
					}
				}
				final String currentThemePackage = ThemeSettings
						.getCurrentThemePackage();
				if (currentThemePackage.equals(themePackage)) {
					PreferencesProvider.Interface.General.saveThemeSetting(context,
							ThemeSettings.THEME_DEFAULT, false);
				}
			}
		}
	}

	private void add(Context context, String themePackage) {
		synchronized (mThemes) {
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> themes = findThemePackages(context);
			final int N = themes.size();
			for (int i = 0; i < N; i++) {
				if (themePackage.equals(themes.get(i).activityInfo.packageName)) {
					ThemeInfo info = getThemePreviewInfo(themes.get(i), pm);
					if (info != null) {
						mThemes.add(info);
						if (mCallback != null) {
							ThemeManagerCallback callback = mCallback.get();
							if (callback != null) {
								callback.callbackForThemeManager(mThemes);
							}
						}
					}
				}
			}

		}
	}

	private ThemeInfo getThemePreviewInfo(ResolveInfo info, PackageManager pm) {
		try {
			String appPackageName = info.activityInfo.packageName.toString();
			String themeName = info.loadLabel(pm).toString();
			Resources res = pm.getResourcesForApplication(appPackageName);
			int id = res.getIdentifier(ThemeSettings.THEME_PREVIEW, "drawable",
					appPackageName);

			return new ThemeInfo(appPackageName, themeName, id);
		} catch (NameNotFoundException e) {
			Log.e(TAG, info.activityInfo.packageName.toString()
					+ " can not find");
		}
		return null;
	}

	private List<ResolveInfo> findThemePackages(Context context) {
		final PackageManager pm = context.getPackageManager();
		Intent intent = new Intent("com.android.launcher.THEMES");
		intent.addCategory("android.intent.category.DEFAULT");
		List<ResolveInfo> themes = pm.queryIntentActivities(intent, 0);

		if (themes == null) {
			themes = new ArrayList<ResolveInfo>(0);
			return themes;
		} else {
			final int N = themes.size();
			final List<ResolveInfo> validList = new ArrayList<ResolveInfo>(N);
			for (int i = 0; i < N; i++) {
				final ResolveInfo themeInfo = themes.get(i);
				try {
					PackageInfo packageInfo = pm.getPackageInfo(
							themeInfo.activityInfo.packageName,
							PackageManager.GET_PERMISSIONS);
					for (int j = 0; j < packageInfo.requestedPermissions.length; j++) {
						if (InstallThemePkgActivity.PERMISSTION_THEME
								.equals(packageInfo.requestedPermissions[j])) {
							validList.add(themeInfo);
							break;
						}
					}

				} catch (NameNotFoundException e) {
				}
			}
			Collections.sort(validList, new MyDisplayNameComparator(pm));
			return validList;
		}
	}

	public interface ThemeManagerCallback {
		public void callbackForThemeManager(List<ThemeInfo> themeInfos);
	}

	public static class ThemeInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		public int previewIcon;
		public String apkId;
		public String apkUrl;
		public String packageName;
		public String name;
		public String size;
		public String downloadCount;
		public String pubDate;
		public String author;
		public String overview;
		public String[] previews;

		public boolean isInstall;
		public boolean isDownload;
		public boolean isDownloading;

		@Override
		public String toString() {
			return "ThemeInfo [apkId=" + apkId + ", apkUrl=" + apkUrl
					+ ", author=" + author + ", downloadCount=" + downloadCount
					+ ", isDownload=" + isDownload + ", isDownloading="
					+ isDownloading + ", isInstall=" + isInstall + ", name="
					+ name + ", overview=" + overview + ", packageName="
					+ packageName + ", previewIcon=" + previewIcon
					+ ", previews=" + Arrays.toString(previews) + ", pubDate="
					+ pubDate + ", size=" + size + "]";
		}

		public void setApkId(String apkId) {
			this.apkId = apkId;
			//apkUrl = Constant.THEME_DOWNLOAD_URL + apkId;
			apkUrl = "";
		}

		public void setPreviews(String previews) {
			if (previews != null) {
				this.previews = previews.split(";");
			}
		}

		public ThemeInfo(String p, String t, int d) {
			previewIcon = d;
			packageName = p;
			name = t;
			isInstall = true;
		}

		public ThemeInfo() {
		}

	}

	private static class MyDisplayNameComparator implements
			Comparator<ResolveInfo> {
		public MyDisplayNameComparator(PackageManager pm) {
			mPM = pm;
		}

		public final int compare(ResolveInfo a, ResolveInfo b) {
			CharSequence sa = a.loadLabel(mPM);
			if (sa == null)
				sa = a.activityInfo.name;
			CharSequence sb = b.loadLabel(mPM);
			if (sb == null)
				sb = b.activityInfo.name;

			final String aTitle = sa.toString();
			final String bTitle = sb.toString();

			return sCollator.compare(aTitle, bTitle);
		}

		private final Collator sCollator = Collator.getInstance();

		private PackageManager mPM;
	}
}
