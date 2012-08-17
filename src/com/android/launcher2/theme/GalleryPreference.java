package com.android.launcher2.theme;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.android.launcher2.R;
import com.android.launcher2.Utilities;
import com.android.launcher2.preference.Preferences;
import com.android.launcher2.theme.ThemePackageManager.ThemeInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;


public class GalleryPreference extends Preference implements
		DialogInterface.OnDismissListener {

	private static final int MENU_THEME_PREFERENCE_DELETE = 0;
	private static final int MENU_THEME_PREFERENCE_INSTALL = 1;

	private int mCurrentPosition = -1;
	private List<ThemeInfo> mThemeInfos;
	private LayoutInflater mLayoutInflater;
	private PackageManager mPm;
	private ThemePreviewAdapter mAdapter;

	private Gallery mGallery;
	private TextView mTitle;
	private TextView mAdapterPostion;
	private Button mApply;

	private Dialog mDialog;

	private Preferences mActivity;

	public GalleryPreference(Context context) {
		super(context);
		init();
	}

	public GalleryPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GalleryPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		Context context = getContext();
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPm = context.getPackageManager();
		mThemeInfos = new ArrayList<ThemeInfo>();
		mAdapter = new ThemePreviewAdapter();
		setPersistent(false);
	}

	public void setActivity(Preferences activity) {
		mActivity = activity;
	}

	protected void onClick() {
		showDialog(null);
	}

	private void showDialog(Bundle state) {
		final Context context = getContext();

		Dialog dialog = mDialog = new ThemePreferencePreview(context);
		dialog.setOnDismissListener(this);
		if (state != null) {
			dialog.onRestoreInstanceState(state);
		}

		dialog.show();
	}

	public void onDismiss(DialogInterface dialog) {
		mDialog = null;
	}

	public Dialog getDialog() {
		return mDialog;
	}

	public void setThemeInfos(List<ThemeInfo> themeInfos) {
		Collections.sort(themeInfos, new MyDisplayNameComparator());
		refreshData(themeInfos);
	}

	public void refreshData(List<ThemeInfo> themeInfos) {
		mThemeInfos = themeInfos;
		mAdapter.notifyDataSetChanged();
		updateTitle();

	}

	public void refreshData() {
		mAdapter.notifyDataSetChanged();
		updateTitle();
	}

	public String getValue() {
		if (mCurrentPosition < 0 || mThemeInfos == null
				|| mThemeInfos.size() <= 0) {
			return "";
		}

		return mThemeInfos.get(mCurrentPosition).packageName;
	}

	public String getEntry() {
		if (mCurrentPosition < 0 || mThemeInfos == null
				|| mThemeInfos.size() <= 0) {
			return "";
		}
		return mThemeInfos.get(mCurrentPosition).name;
	}

	public void updateTitle() {
		if (mCurrentPosition < 0 || mCurrentPosition >= mThemeInfos.size()) {
			return;
		}
		updateTitle(mCurrentPosition);
	}

	private void updateTitle(int position) {
		if (ThemeSettings.getCurrentThemePackage().equals(
				mThemeInfos.get(position).packageName)) {
			mApply.setEnabled(false);
		} else {
			mApply.setEnabled(true);
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(position + 1).append("/").append(mThemeInfos.size());
		mAdapterPostion.setText(sb.toString());
		mTitle.setText(mThemeInfos.get(position).name);
		mCurrentPosition = position;
	}

	private class ThemePreferencePreview extends Dialog {

		public ThemePreferencePreview(Context context) {
			super(context, android.R.style.Theme_NoTitleBar);
			setContentView(R.layout.theme_preview_preference);

			mGallery = (Gallery) findViewById(R.id.theme_preview_preference_gallery);
			mTitle = (TextView) findViewById(R.id.theme_preview_preference_pkg);
			mAdapterPostion = (TextView) findViewById(R.id.theme_preview_preference_adapter_position);
			mApply = (Button) findViewById(R.id.theme_preview_preference_apply);
			mApply.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mActivity.applyTheme();
				}
			});

			mGallery.setAdapter(mAdapter);
			mGallery.setCallbackDuringFling(false);
			mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					updateTitle(position);
				}

				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
			if (mCurrentPosition >= 0) {
				mGallery.setSelection(mCurrentPosition);
			}
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			super.onCreateOptionsMenu(menu);
			//if(LauncherApplication.IS_DRAGGABLE){
			// menu.add(MENU_GROUP_THEME_PREFERENCE,
			// MENU_THEME_PREFERENCE_DELETE,
			// 0, R.string.pref_themes_uninstall_theme);
			// menu.add(MENU_GROUP_THEME_PREFERENCE,
			// MENU_THEME_PREFERENCE_INSTALL, 0,
			// R.string.pref_themes_install_theme);
			//}
			return true;
		}

		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
			switch (item.getItemId()) {
			case MENU_THEME_PREFERENCE_DELETE:
				//mActivity.uninstallTheme();
				return true;
			case MENU_THEME_PREFERENCE_INSTALL:
				//mActivity.installTheme();
				return true;

			}
			return super.onMenuItemSelected(featureId, item);
		}

	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		final Dialog dialog = mDialog;
		if (dialog == null || !dialog.isShowing()) {
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.isDialogShowing = true;
		myState.dialogBundle = dialog.onSaveInstanceState();
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		if (myState.isDialogShowing) {
			showDialog(myState.dialogBundle);
		}
	}

	private static class SavedState extends BaseSavedState {
		boolean isDialogShowing;
		Bundle dialogBundle;

		public SavedState(Parcel source) {
			super(source);
			isDialogShowing = source.readInt() == 1;
			dialogBundle = source.readBundle();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(isDialogShowing ? 1 : 0);
			dest.writeBundle(dialogBundle);
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}
 
	}

	private class ThemePreviewAdapter extends BaseAdapter {

		public int getCount() {
			return mThemeInfos.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;

			final Context context = getContext();
			if (convertView == null) {
				image = (ImageView) mLayoutInflater.inflate(
						R.layout.theme_preview_item, parent, false);
			} else {
				image = (ImageView) convertView;
			}

			final ThemeInfo info = mThemeInfos.get(position);
			Drawable d = null;
			if (ThemeSettings.THEME_DEFAULT.equals(info.packageName)) {
				d = context.getResources()
						.getDrawable(R.drawable.theme_preview);
			} else if (info != null) {
				if (info.previewIcon == 0) {
					d = mPm.getDefaultActivityIcon();
				} else {
					try {
						final Resources res = mPm
								.getResourcesForApplication(info.packageName);
						d = res.getDrawable(info.previewIcon);
					} catch (NameNotFoundException e) {
						d = mPm.getDefaultActivityIcon();
					}
				}
			}
			if (d != null) {
				d = Utilities.tintThePicture(context, d,
						d.getIntrinsicHeight() / 6);
			}
			image.setImageDrawable(d);
			return image;
		}
	}

	private static class MyDisplayNameComparator implements
			Comparator<ThemeInfo> {
		public final int compare(ThemeInfo a, ThemeInfo b) {

			final String aTitle = a.name;
			final String bTitle = b.name;

			return sCollator.compare(aTitle, bTitle);
		}

		private final Collator sCollator = Collator.getInstance();
	}
}
