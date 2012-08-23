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
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.launcher2.R;
import com.android.launcher2.ApplicationInfo;
import com.android.launcher2.Utilities;

public class InstallThemePkgActivity extends Activity {
	public static final String TAG = "InstallThemePkgActivity";
	private String mSDCardPath;
	private static final String FIND_ROOT_DIRECT = "/launchertheme/";
	public static final String PERMISSTION_THEME = "com.android.launcher.permission.THEME";

	private static final int MENU_REFRESH = 1;

	private static final int DIALOG_WAITING = 1;

	private static final int MSG_START_FINDING = 0;
	private static final int MSG_END_FINDING = MSG_START_FINDING + 1;
	private static final int MSG_FIND_THEME_APKS = MSG_END_FINDING + 1;

	private boolean mIsFindingApks = false;
	private InstallThemePkgAdapter mAdapter = null;

	private List<ApplicationInfo> mApkInfos = new ArrayList<ApplicationInfo>();

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.install_theme_pkg);

		mHandler = new Handler(getMainLooper()) {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_START_FINDING:
					showDialog(DIALOG_WAITING);
					break;
				case MSG_END_FINDING:
					dismissDialog(DIALOG_WAITING);
					doAfterFind();
					break;
				case MSG_FIND_THEME_APKS:
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}

			}
		};

		final GridView apksGrid = (GridView) findViewById(R.id.install_pkg_grid);

		final boolean isPort = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		mAdapter = new InstallThemePkgAdapter(this, mApkInfos);

		apksGrid.setGravity(Gravity.CENTER);
		apksGrid.setAdapter(mAdapter);
		apksGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ApplicationInfo info = (ApplicationInfo) view.getTag();
				startActivity(info.intent);
			}
		});

		if (isPort) {
			apksGrid.setNumColumns(4);
		} else {
			apksGrid.setNumColumns(6);
		}
		findThemePkgs();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!checkSDCard()) {
			return;
		}
		IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mIsFindingApks) {
			forceStopFinding();
		}
	}

	@Override
	public void onBackPressed() {
		if (mIsFindingApks) {
			forceStopFinding();
		}
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_REFRESH, 0, R.string.refresh).setIcon(
				R.drawable.ic_menu_refresh);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH: {
			forceStopFinding();
			findThemePkgs();
			return true;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	public void findThemePkgs() {
		if (!checkSDCard()) {
			return;
		}
		mIsFindingApks = true;
		mApkInfos.clear();
		mAdapter.notifyDataSetChanged();
		File root = new File(mSDCardPath, FIND_ROOT_DIRECT);
		if (!root.exists()) {
			root.mkdirs();
		}
		mHandler.sendEmptyMessage(MSG_START_FINDING);
		mHandler.post(new FindAPKThread(root, mApkInfos, getResources()
				.getDisplayMetrics(), getResources().getConfiguration()));
	}

	private boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			mSDCardPath = android.os.Environment.getExternalStorageDirectory()
					.toString();
			return true;
		} else {
			doAfterSDRemoved();
			return false;
		}
	}

	private void doAfterSDRemoved() {
		finish();
	}

	private void doAfterFind() {
		mIsFindingApks = false;
		final GridView apksGrid = (GridView) findViewById(R.id.install_pkg_grid);
		final View view = findViewById(R.id.msg_no_theme_file);

		if (mAdapter.getCount() > 0) {
			apksGrid.setVisibility(View.VISIBLE);
			view.setVisibility(View.GONE);
		} else {
			apksGrid.setVisibility(View.GONE);
			view.setVisibility(View.VISIBLE);
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WAITING: {
			Dialog dialog = ProgressDialog.show(this, null,
					getText(R.string.pref_info_waiting), true, true);
			dialog.setOnDismissListener(new OnDismissListener() {

				public void onDismiss(DialogInterface dialog) {
					mIsFindingApks = false;
				}
			});

			return dialog;
		}
		}

		return super.onCreateDialog(id);
	}

	private class FindAPKThread extends Thread {
		File file;
		List<ApplicationInfo> list;
		DisplayMetrics metrics;
		Configuration config;

		public FindAPKThread(File file, List<ApplicationInfo> list,
				DisplayMetrics metrics, Configuration config) {
			this.file = file;
			this.list = list;
			this.metrics = metrics;
			this.config = config;

		}

		public void run() {
			getAPKs(file, list, metrics, config);
			mHandler.sendEmptyMessage(MSG_END_FINDING);
		}

	}

	private void forceStopFinding() {
		if (mIsFindingApks) {
			mIsFindingApks = false;
			dismissDialog(DIALOG_WAITING);
		}
	}

	private void getAPKs(File file, List<ApplicationInfo> list,
			DisplayMetrics metrics, Configuration config) {
		if (!mIsFindingApks) {
			return;
		}
		if (!file.isDirectory() && file.getName().endsWith(".apk")) {
			final String filePath = file.getPath();
			Object pkg = InnerClass.PackageParser_parsePackage(file, filePath,
					metrics, 0);
			if (pkg != null) {
				final List<String> permissions = InnerClass
						.Package_get_requestedPermissions(pkg);
				if (permissions == null || permissions.size() == 0) {
					return;
				}
				boolean isThemeAPK = false;
				for (int i = 0; i < permissions.size(); i++) {
					if (PERMISSTION_THEME.equals(permissions.get(i))) {
						isThemeAPK = true;
						break;
					}
				}

				if (!isThemeAPK) {
					return;
				}

				android.content.pm.ApplicationInfo pkgInfo = InnerClass
						.Package_get_applicationInfo(pkg);
				ApplicationInfo info = new ApplicationInfo();
				info.title = file.getName();
				if (pkgInfo.icon == 0) {
					info.iconBitmap = Utilities.createIconBitmap(
							getPackageManager().getDefaultActivityIcon(), this);
				} else {
					AssetManager assmgr;
					try {
						assmgr = (AssetManager) Reflector
								.newInstance("AssetManager");
					} catch (Exception e) {
						Log.e("Launcher", "Create AssetManager fail!");
						return;
					}
					InnerClass.AssertManager_addAssetPath(assmgr, filePath);
					Resources res = new Resources(assmgr, metrics, config);
					info.iconBitmap = Utilities.createIconBitmap(res
							.getDrawable(pkgInfo.icon), this);
				}

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				info.intent = intent;
				list.add(info);
				Message msg = mHandler.obtainMessage(MSG_FIND_THEME_APKS);
				mHandler.sendMessage(msg);
			}
			return;
		} else if (file.isDirectory()) {
			final String[] fileList = file.list();
			if (fileList == null || fileList.length == 0) {
				return;
			}
			for (int i = 0; i < fileList.length; i++) {
				getAPKs(new File(file, fileList[i]), list, metrics, config);
			}
		}
	}

	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				checkSDCard();
			} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)
					|| Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
				doAfterSDRemoved();
			}
		}
	};

	public static class InstallThemePkgAdapter extends
			ArrayAdapter<ApplicationInfo> {
		private final LayoutInflater mInflater;
		private List<ApplicationInfo> mApps;

		public InstallThemePkgAdapter(Context context,
				List<ApplicationInfo> apps) {
			super(context, 0, apps);
			mInflater = LayoutInflater.from(context);
			mApps = apps;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = new TextView(getContext());
			}

			final ApplicationView appView = (ApplicationView) convertView;
			final ApplicationInfo info = getItem(position);
			info.iconBitmap.setDensity(Bitmap.DENSITY_NONE);
			appView.setCompoundDrawablesWithIntrinsicBounds(null,
					new BitmapDrawable(getContext().getResources(),
							info.iconBitmap), null, null);
			appView.setText(info.title);
			appView.setTag(mApps.get(position));
			return convertView;
		}
	}

}
