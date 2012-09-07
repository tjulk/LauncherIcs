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

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import com.android.launcher2.AppsCustomizePagedView;
import com.android.launcher2.LauncherApplication;
import com.android.launcher2.R;
import com.android.launcher2.Workspace;
import com.android.launcher2.theme.ThemeSettings;

public final class PreferencesProvider {
	
	private static String TAG = "PreferencesProvider";
	
    public static final String PREFERENCES_KEY = "com.android.launcher2_preferences";

    public static final String PREFERENCES_CHANGED = "preferences_changed";

    public static class Interface {
        public static class Homescreen {
            public static int getNumberHomescreens(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getInt("ui_homescreen_screens", 5);
            }
            public static int getDefaultHomescreen(Context context, int def) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getInt("ui_homescreen_default_screen", def + 1) - 1;
            }
            public static int getCellCountX(Context context, int def) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                String[] values = preferences.getString("ui_homescreen_grid", "0|" + def).split("\\|");
                try {
                    return Integer.parseInt(values[1]);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            public static int getCellCountY(Context context, int def) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                String[] values = preferences.getString("ui_homescreen_grid", def + "|0").split("\\|");;
                try {
                    return Integer.parseInt(values[0]);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            public static int getScreenPaddingVertical(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return (int)((float) preferences.getInt("ui_homescreen_screen_padding_vertical", 0) * 3.0f *
                        LauncherApplication.getScreenDensity());
            }
            public static int getScreenPaddingHorizontal(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return (int)((float) preferences.getInt("ui_homescreen_screen_padding_horizontal", 0) * 3.0f *
                        LauncherApplication.getScreenDensity());
            }
            public static boolean getShowSearchBar(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("ui_homescreen_general_search", true);
            }
            public static boolean getResizeAnyWidget(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("ui_homescreen_general_resize_any_widget", false);
            }
            public static boolean getHideIconLabels(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("ui_homescreen_general_hide_icon_labels", false);
            }
            public static class Scrolling {
                public static boolean getScrollWallpaper(Context context) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_homescreen_scrolling_scroll_wallpaper", true);
                }
                public static Workspace.TransitionEffect getTransitionEffect(Context context, String def) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return Workspace.TransitionEffect.valueOf(
                            preferences.getString("ui_homescreen_scrolling_transition_effect", def));
                }
                public static boolean getFadeInAdjacentScreens(Context context, boolean def) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_homescreen_scrolling_fade_adjacent_screens", def);
                }
            }
            public static class Indicator {
                public static boolean getShowScrollingIndicator(Context context) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_homescreen_indicator_enable", true);
                }
                public static boolean getFadeScrollingIndicator(Context context) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_homescreen_indicator_fade", true);
                }
                public static boolean getShowDockDivider(Context context) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_homescreen_indicator_background", true);
                }
            }
        }

        public static class Drawer {
            public static boolean getJoinWidgetsApps(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("ui_drawer_widgets_join_apps", true);
            }
            public static class Scrolling {
                public static AppsCustomizePagedView.TransitionEffect getTransitionEffect(Context context, String def) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return AppsCustomizePagedView.TransitionEffect.valueOf(
                            preferences.getString("ui_drawer_scrolling_transition_effect", def));
                }
                public static boolean getFadeInAdjacentScreens(Context context) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_drawer_scrolling_fade_adjacent_screens", false);
                }
            }
            public static class Indicator {
                public static boolean getShowScrollingIndicator(Context context) {
                   final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_drawer_indicator_enable", true);
                }
                public static boolean getFadeScrollingIndicator(Context context) {
                    final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                    return preferences.getBoolean("ui_drawer_indicator_fade", true);
                }
            }
        }

        public static class Dock {

        }

        public static class Icons {

        }

        public static class General {
            public static boolean getAutoRotate(Context context, boolean def) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("ui_general_orientation", def);
            }
            
            public static boolean isFistLauncherFlag(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("is_first_launcher", true);
            }
            
            public static boolean isSetAppsBackgroundAsWallPaper(Context context) {
                final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
                return preferences.getBoolean("is_set_apps_background_as_wallpaper", false);
            }
            
        	public static void clearFirstLaunchFlag(Context context) {
        		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        		preferences.edit().putBoolean("is_first_launcher", false).commit();
        	}
        	
        	public static void saveThemeSetting(Context context,
        			String themePackageName, boolean isSetWallPaper) {
        		SharedPreferences sp = context.getSharedPreferences(
        				PREFERENCES_KEY, Context.MODE_PRIVATE);
        		SharedPreferences.Editor editor = sp.edit();

        		if (com.android.launcher2.theme.ThemeSettings.THEME_DEFAULT.equals(themePackageName)) {
        			editor.putString("themePackageName",com.android.launcher2.theme.ThemeSettings.THEME_DEFAULT);
        		} else {
        			Resources themeResources = ThemeSettings.getResources(context,
        					themePackageName);
        			if (themeResources == null) {
        				themeResources = context.getResources();
        			}
        			editor.putString("themePackageName",
        					themePackageName);

        			int id = themeResources.getIdentifier(
        					ThemeSettings.ALL_APPS_BG_ALPHA, "integer",
        					themePackageName);
        			if (id != 0) {
        				editor.putInt("allAppsBgAlpha",
        						themeResources.getInteger(id));
        				id = 0;
        			}
        		}
        		if (!editor.commit()) {
        			Log.e("Launcher", "not_enough_space");
        			return;
        		}

        		final LauncherApplication app = ((LauncherApplication) (context
        				.getApplicationContext()));
        		app.mIconCache.flush();
        		app.mModel.reset(app);

        		ThemeSettings.init(context);
        	}
        	public static String getThemePackageName(Context context) {
        		SharedPreferences sp = context.getSharedPreferences(
        				PREFERENCES_KEY, Context.MODE_PRIVATE);
        		return sp.getString("themePackageName",
        				ThemeSettings.THEME_DEFAULT);
        	}
        	
        	public static void putStringValue(Context context, String key, String value) {
        		SharedPreferences sp = context.getSharedPreferences(
        				PREFERENCES_KEY, Context.MODE_PRIVATE);
        		SharedPreferences.Editor editor = sp.edit();
        		editor.putString(key, value);
        		if (!editor.commit()) {
        			Log.e("Launcher", "not_enough_space");
        		}
        	}
        	
        	public static void setWallPaper(Context context, String themePackageName) {
        		String[] names = ThemeSettings.getStringArray(context,
        				R.array.wallpapers, null);
        		if (names == null) { // if no wall paper list in the theme, instead of the Launcher's
        			Resources res = context.getResources();
        			names = res.getStringArray(R.array.wallpapers);
        		}
        		if (names == null || names.length == 0) {
        			return;
        		}
        		Resources themeResources = ThemeSettings.getResources(context,
        				themePackageName);
        		int wallpaperId = 0;
        		for (int i = 0; i < names.length; i++) {
        			wallpaperId = themeResources.getIdentifier(names[i], "drawable",
        					themePackageName);
        			if (wallpaperId != 0) {
        				break;
        			}
        		}
        		if (wallpaperId == 0) { // if can not find any wall paper in theme, find
        								// one in the Launcher
        			themeResources = context.getResources();
        			for (int i = 0; i < names.length; i++) {
        				wallpaperId = themeResources.getIdentifier(names[0],
        						"drawable", context.getPackageName());
        				if (wallpaperId != 0) {
        					break;
        				}
        			}
        			if (wallpaperId == 0) {
        				return;
        			}
        		}
        		Options mOptions = new BitmapFactory.Options();
        		mOptions.inDither = false;
        		mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        		Bitmap wallpaper = null;

        		try {
        			wallpaper = BitmapFactory.decodeResource(themeResources,
        					wallpaperId, mOptions);
        		} catch (OutOfMemoryError e) {
        			Log.e(TAG, "decodeResource error wallpaperId=" + wallpaperId);
        		}
        		if (wallpaper != null) {
        			try {
        				WallpaperManager wpm = (WallpaperManager) context
        						.getSystemService("wallpaper");
        				wpm.setBitmap(wallpaper);
        				wallpaper.recycle();
        			} catch (Exception e) {
        				Log.e(TAG, e.getMessage());
        			}
        		}

        		Log.e(TAG, "setWallpaper 2" + System.currentTimeMillis());
        	}
        }
        
        
    }

    public static class Application {

    }
}
