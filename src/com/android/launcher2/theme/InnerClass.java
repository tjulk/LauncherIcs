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

import java.io.File;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.MaskFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ListAdapter;

public class InnerClass {
	final static String LOG_TAG = "InnerClass";

	public static void StatusBarManager_expand(Context context) {
		final Object statusBar = context.getSystemService("statusbar");
		if (statusBar != null) {
			try {
				Reflector.invokeMethod(statusBar, "expand", null, null);
			} catch (Exception e) {
				Log.e(LOG_TAG, "StatusBarManager_expand exception : "
						+ e.toString());
			}
		}
	}

	public static WindowManager WindowManagerImpl_getDefault() {
		try {
			Class<?> clazz = Class.forName("android.view.WindowManagerImpl");
			return (WindowManager) Reflector.invokeStaticMethod(clazz,
					"getDefault", null, null);
		} catch (Exception e) {
			Log.e(LOG_TAG, "WindowManagerImpl_getDefault exception : "
					+ e.toString());
		}
		return null;
	}

	public static boolean Bitmap_sameAs(Bitmap thisBmp, Bitmap otherBmp) {
		try {
			return (Boolean) Reflector.invokeMethod(thisBmp, "sameAs",
					new Class[] { Bitmap.class }, new Object[] { otherBmp });
		} catch (Exception e) {
			Log.e(LOG_TAG, "Bitmap_sameAs exception : " + e.toString());
		}
		return false;
	}

	public static ComponentName SearchManager_getGlobalSearchActivity(
			SearchManager searchmanager) {
		try {
			return (ComponentName) Reflector.invokeMethod(searchmanager,
					"getGlobalSearchActivity", null, null);
		} catch (Exception e) {
			Log.e(LOG_TAG, "SearchManager_getGlobalSearchActivity exception : "
					+ e.toString());
		}
		return null;
	}

	public static void XmlUtils_beginDocument(XmlPullParser parser,
			String firstElementName) {
		try {
			Class<?> clazz = Class.forName("com.android.internal.util.XmlUtils");
			Reflector.invokeStaticMethod(clazz, "beginDocument", new Class[] {
					XmlPullParser.class, String.class }, new Object[] { parser,
					firstElementName });
		} catch (Exception e) {
			Log
					.e(LOG_TAG, "XmlUtils_beginDocument exception : "
							+ e.toString());
		}
	}

	public static MaskFilter TableMaskFilter_CreateClipTable(int min, int max) {
		try {
			Class<?> clazz = Class.forName("android.graphics.TableMaskFilter");
			return (MaskFilter) Reflector.invokeStaticMethod(clazz,
					"CreateClipTable", new Class[] { int.class, int.class },
					new Object[] { min, max });
		} catch (Exception e) {
			Log.e(LOG_TAG, "TableMaskFilter_CreateClipTable exception : "
					+ e.toString());
		}
		return null;
	}

	public static Object PackageParser_parsePackage(File sourceFile,
			String destCodePath, DisplayMetrics metrics, int flags) {
		try {
			Object instance = Reflector
					.newInstance("android.content.pm.PackageParser");
			return Reflector.invokeMethod(instance, "parsePackage",
					new Class[] { File.class, String.class,
							DisplayMetrics.class, int.class }, new Object[] {
							sourceFile, destCodePath });
		} catch (Exception e) {
			Log.e(LOG_TAG, "PackageParser_parsePackage exception : "
					+ e.toString());
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public static List<String> Package_get_requestedPermissions(Object instance) {
		try {
			return (List<String>) Reflector.getValue(instance,
					"requestedPermissions");
		} catch (Exception e) {
			Log
					.e(LOG_TAG, "Package_get_requestedPermissions : "
							+ e.toString());
		}
		return null;
	}

	public static android.content.pm.ApplicationInfo Package_get_applicationInfo(
			Object instance) {
		try {
			return (android.content.pm.ApplicationInfo) Reflector.getValue(
					instance, "applicationInfo");
		} catch (Exception e) {
			Log.e(LOG_TAG, "Package_get_applicationInfo : " + e.toString());
		}
		return null;
	}

	public static int AssertManager_addAssetPath(AssetManager manager,
			String path) {
		try {
			return (Integer) Reflector.invokeMethod(manager, "addAssetPath",
					new Class[] { String.class }, new Object[] { path });
		} catch (Exception e) {
			Log.e(LOG_TAG, "SearchManager_getGlobalSearchActivity exception : "
					+ e.toString());
		}
		return 0;

	}

	public static boolean AlertController_onKeyUp(Object controller,
			int keyCode, KeyEvent event) {
		try {
			return (Boolean) Reflector.invokeMethod(controller, "onKeyUp",
					new Class[] { int.class, KeyEvent.class }, new Object[] {
							keyCode, event });
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertController_onKeyUp exception : "
					+ e.toString());
		}
		return false;
	}

	public static boolean AlertController_onKeyDown(Object controller,
			int keyCode, KeyEvent event) {
		try {
			return (Boolean) Reflector.invokeMethod(controller, "onKeyDown",
					new Class[] { int.class, KeyEvent.class }, new Object[] {
							keyCode, event });
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertController_onKeyDown exception : "
					+ e.toString());
		}
		return false;
	}

	public static void AlertController_installContent(Object controller) {
		try {
			Reflector.invokeMethod(controller, "installContent", null, null);
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertController_installContent exception : "
					+ e.toString());
		}
	}

	public static void AlertParams_apply(Object alertParams, Object controller) {
		try {
			Reflector.invokeMethod(alertParams, "apply",
					new Object[] { controller });
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertParams_apply exception : " + e.toString());
		}
	}

	public static void AlertParams_set_mOnClickListener(Object alertParams,
			DialogInterface.OnClickListener listener) {
		try {
			Reflector.setValue(alertParams, "mOnClickListener", listener);
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertParams_set_mOnClickListener exception : "
					+ e.toString());
		}
	}

	public static void AlertParams_set_mOnCancelListener(Object alertParams,
			DialogInterface.OnCancelListener listener) {
		try {
			Reflector.setValue(alertParams, "mOnCancelListener", listener);
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertParams_set_mOnCancelListener exception : "
					+ e.toString());
		}
	}

	public static void AlertParams_set_mTitle(Object alertParams, String title) {
		try {
			Reflector.setValue(alertParams, "mTitle", title);
		} catch (Exception e) {
			Log
					.e(LOG_TAG, "AlertParams_set_mTitle exception : "
							+ e.toString());
		}
	}

	public static void AlertParams_set_mAdapter(Object alertParams,
			ListAdapter adapter) {
		try {
			Reflector.setValue(alertParams, "mAdapter", adapter);
		} catch (Exception e) {
			Log.e(LOG_TAG, "AlertParams_set_mAdapter exception : "
					+ e.toString());
		}
	}

}
