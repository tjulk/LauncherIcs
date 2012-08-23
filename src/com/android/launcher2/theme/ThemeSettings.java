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

import com.android.launcher2.preference.PreferencesProvider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;

public class ThemeSettings {
	
	public static String THEME_DEFAULT = "com.android.launcher2";
	
	private static final int RESOURCE_TYPE_DRAWABLE = 1;
	private static final int RESOURCE_TYPE_COLOR = 2;
	private static final int RESOURCE_TYPE_INTEGER = 3;
	private static final int RESOURCE_TYPE_DIMEN = 4;
	private static final int RESOURCE_TYPE_STRINGARRAY = 5;
	private static final int RESOURCE_TYPE_BOOLEAN = 6;

	private static Resources sResource = null;
	private static String sThemePackageName = THEME_DEFAULT;

	public static synchronized void init(Context context) {
		sThemePackageName = PreferencesProvider.Interface.General.getThemePackageName(context);
		sResource = getResources(context, sThemePackageName);
		if (sResource == null) {
			PreferencesProvider.Interface.General.putStringValue(context,
					"themePackageName",
					THEME_DEFAULT);
			sResource = context.getResources();
		}
	}

	public static boolean getBoolean(Context context, int resID) {
		return (Boolean) getThemeResource(context, resID, RESOURCE_TYPE_BOOLEAN);
	}

	public static boolean getBoolean(Context context, int resID, boolean defaultValue) {
		return (Boolean) getThemeResource(context, resID, defaultValue,
				RESOURCE_TYPE_BOOLEAN);
	}

	public static boolean getBoolean(Context context, String resName,
			boolean defaultValue) {
		return (Boolean) getThemeResource(context, resName, defaultValue,
				RESOURCE_TYPE_BOOLEAN);
	}

	public static String[] getStringArray(Context context, int resID) {
		return (String[]) getThemeResource(context, resID,
				RESOURCE_TYPE_STRINGARRAY);
	}

	public static String[] getStringArray(Context context, int resID,
			String[] defaultValue) {
		return (String[]) getThemeResource(context, resID, defaultValue,
				RESOURCE_TYPE_STRINGARRAY);
	}

	public static String[] getStringArray(Context context, String resName,
			String[] defaultValue) {
		return (String[]) getThemeResource(context, resName, defaultValue,
				RESOURCE_TYPE_STRINGARRAY);
	}

	public static float getDimen(Context context, int resID) {
		return (Float) getThemeResource(context, resID, RESOURCE_TYPE_DIMEN);
	}

	public static float getDimen(Context context, int resID, float defaultValue) {
		return (Float) getThemeResource(context, resID, defaultValue,
				RESOURCE_TYPE_DIMEN);
	}

	public static float getDimen(Context context, String resName, float defaultValue) {
		return (Float) getThemeResource(context, resName, defaultValue,
				RESOURCE_TYPE_DIMEN);
	}

	public static int getInteger(Context context, int resID) {
		return (Integer) getThemeResource(context, resID, RESOURCE_TYPE_INTEGER);
	}

	public static int getInteger(Context context, int resID, int defaultValue) {
		return (Integer) getThemeResource(context, resID, defaultValue,
				RESOURCE_TYPE_INTEGER);
	}

	public static int getInteger(Context context, String resName, int defaultValue) {
		return (Integer) getThemeResource(context, resName, defaultValue,
				RESOURCE_TYPE_INTEGER);
	}

	public static Drawable getDrawable(Context context, int resID) {
		return (Drawable) getThemeResource(context, resID,
				RESOURCE_TYPE_DRAWABLE);
	}

	public static Drawable getDrawable(Context context, int resID,
			Drawable defaultValue) {
		return (Drawable) getThemeResource(context, resID, defaultValue,
				RESOURCE_TYPE_DRAWABLE);
	}

	public static Drawable getDrawable(Context context, String resName,
			Drawable defaultValue) {
		return (Drawable) getThemeResource(context, resName, defaultValue,
				RESOURCE_TYPE_DRAWABLE);
	}

	public static StateListDrawable getStateListDrawable(Context context, int resID) {
		Object object = getThemeResource(context, resID, RESOURCE_TYPE_DRAWABLE);
		if (object != null && object instanceof StateListDrawable) {
			return (StateListDrawable) object;
		} else {
			return (StateListDrawable) getDefaultResourceValue(context, resID,
					RESOURCE_TYPE_DRAWABLE);
		}
	}

	public static NinePatchDrawable getNinePatchDrawable(Context context,
			int resID) {
		Object object = getThemeResource(context, resID, RESOURCE_TYPE_DRAWABLE);
		if (object != null && object instanceof NinePatchDrawable) {
			return (NinePatchDrawable) object;
		} else {
			return (NinePatchDrawable) getDefaultResourceValue(context, resID,
					RESOURCE_TYPE_DRAWABLE);
		}
	}

	public static int getColor(Context context, int resID) {
		return (Integer) getThemeResource(context, resID, RESOURCE_TYPE_COLOR);
	}

	public static int getColor(Context context, int resID, int defaultValue) {
		return (Integer) getThemeResource(context, resID, defaultValue,
				RESOURCE_TYPE_COLOR);
	}

	public static int getColor(Context context, String resName, int defaultValue) {
		return (Integer) getThemeResource(context, resName, defaultValue,
				RESOURCE_TYPE_COLOR);
	}

	private static Object getThemeResource(Context context, int resID, int type) {
		if (sResource == null) {
			init(context);
		}
		if (THEME_DEFAULT.equals(sThemePackageName)) {
			return getDefaultResourceValue(context, resID, type);
		}
		try {
			final String resName = context.getResources().getResourceEntryName(
					resID);
			Object o = getResourceValueByName(context, sResource,
					sThemePackageName, resName, type);
			return o == null ? getDefaultResourceValue(context, resID, type)
					: o;
		} catch (NotFoundException e) {

		}
		return getDefaultResourceValue(context, resID, type);
	}

	private static Object getThemeResource(Context context, int resID,
			Object defaultValue, int type) {
		if (sResource == null) {
			init(context);
		}
		if (THEME_DEFAULT.equals(sThemePackageName)) {
			Object o = getDefaultResourceValue(context, resID, type);
			return o == null ? defaultValue : o;
		}
		try {
			final String resName = context.getResources().getResourceEntryName(
					resID);
			Object o = getResourceValueByName(context, sResource,
					sThemePackageName, resName, type);
			return o == null ? defaultValue : o;
		} catch (NotFoundException e) {

		}
		return defaultValue;
	}

	private static Object getThemeResource(Context context, String resName,
			Object defaultValue, int type) {
		if (sResource == null) {
			init(context);
		}

		if (THEME_DEFAULT.equals(sThemePackageName)) {
			return defaultValue;
		}

		Object o = getResourceValueByName(context, sResource,
				sThemePackageName, resName, type);
		return o == null ? defaultValue : o;
	}

	private static Object getResourceValueByName(Context context,
			Resources res, String packageName, String resName, int type) {
		try {
			if (THEME_DEFAULT.equals(sThemePackageName)) {
				packageName = context.getPackageName();
			}
			resName = resName.toLowerCase().trim().replace(".", "_").replace(
					" ", "_");
			
			int id = 0;
			switch (type) {
			case RESOURCE_TYPE_DRAWABLE:
				id = res.getIdentifier(resName, "drawable", packageName);
				if (id != 0) {
					return res.getDrawable(id);
				}
				break;
			case RESOURCE_TYPE_COLOR:
				id = res.getIdentifier(resName, "color", packageName);
				if (id != 0) {
					return res.getColor(id);
				}
				break;
			case RESOURCE_TYPE_INTEGER:
				id = res.getIdentifier(resName, "integer", packageName);
				if (id != 0) {
					return res.getInteger(id);
				}
				break;
			case RESOURCE_TYPE_DIMEN:
				id = res.getIdentifier(resName, "dimen", packageName);
				if (id != 0) {
					return res.getDimension(id);
				}
				break;
			case RESOURCE_TYPE_STRINGARRAY:
				id = res.getIdentifier(resName, "array", packageName);
				if (id != 0) {
					return res.getStringArray(id);
				}
				break;
			case RESOURCE_TYPE_BOOLEAN:
				id = res.getIdentifier(resName, "bool", packageName);
				if (id != 0) {
					return res.getBoolean(id);
				}
				break;
			default:
				return null;
			}

		} catch (NotFoundException e) {
		}
		return null;
	}

	private static Object getDefaultResourceValue(Context context, int id,
			int type) {
		Resources res = context.getResources();
		switch (type) {
		case RESOURCE_TYPE_DRAWABLE:
			return res.getDrawable(id);
		case RESOURCE_TYPE_COLOR:
			return res.getColor(id);
		case RESOURCE_TYPE_INTEGER:
			return res.getInteger(id);
		case RESOURCE_TYPE_DIMEN:
			return res.getDimension(id);
		case RESOURCE_TYPE_STRINGARRAY:
			return res.getStringArray(id);
		case RESOURCE_TYPE_BOOLEAN:
			return res.getBoolean(id);
		default:
			return null;
		}
	}

	public static Resources getResources(Context context, String themePackage) {
		if (THEME_DEFAULT.equals(themePackage)) {
			return context.getResources();
		}

		PackageManager packageManager = context.getPackageManager();
		Resources themeResources = null;

		try {
			themeResources = packageManager
					.getResourcesForApplication(themePackage);
		} catch (NameNotFoundException e) {
			themeResources = null;
		}

		return themeResources;
	}

	public static String getCurrentThemePackage() {
		return sThemePackageName;
	}
	/**
	 * setting name of theme's drawable,color resouces
	 */

	public static final String THEME_WALLPAPER = "theme_wallpaper";
	public static final String THEME_ICON_BACKGROUND = "ic_shortcut_background";
	public static final String THEME_PREVIEW = "theme_preview";


	/**
	 * setting name of theme's integer resouces
	 */
	public static final String ALL_APPS_BG_ALPHA = "app_apps_bg_alpha";

	/**
	 * setting name of theme's dimen resouces
	 */
	public static final String WORKSPACE_LONGAXIS_START_PADDING = "workspace_longaxis_start_padding";
	public static final String WORKSPACE_LONGAXIS_END_PADDING = "workspace_longaxis_end_padding";
	public static final String WORKSPACE_SHORTAXIS_START_PADDING = "workspace_shortaxis_start_padding";
	public static final String WORKSPACE_SHORTAXIS_END_PADDING = "workspace_shortaxis_end_padding";

	/**
	 * setting name of theme's boolean resouces
	 */
	public static final String HOTSEAT_ICON_DRAW_REFLECTION = "hotseat_icon_draw_reflection";
	/**
	 * setting name of theme's array resouces
	 */
	public static final String ALL_APPS_CUSTOM_POSITION_NAMES = "all_apps_custom_postion_names";
	public static final String THEME_EXTRA_WALLPAPERS = "extra_wallpapers";
}
