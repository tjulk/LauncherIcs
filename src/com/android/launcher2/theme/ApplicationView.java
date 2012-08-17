/*
 * Copyright (C) 2008 The Android Open Source Project
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import com.android.launcher2.R;
import com.android.launcher2.Utilities;

public class ApplicationView extends TextView {
	public final static String TAG = "ApplicationView";
	public final static int sHighlightFocusColor = 0xffff8e00;
	public final static int sHighlightPressColor = 0xffffc300;

	public static final int HIGHLIGHT_CLASSIC = 0;
	public static final int HIGHLIGHT_GLOW = 1;

	private int mHighlightStyle = -1;
	private Drawable mSelector;

	private boolean mDrawSelectorSelf = true;

	private OnClickListener mClickListener;
	protected boolean mDragFlag = false;

	private static Paint sBlurPaint;

	protected ColorMatrixColorFilter mColorFilter;

	public ApplicationView(Context context) {
		super(context);
		init();
	}

	public ApplicationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ApplicationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		final Context context = getContext();
		if (sBlurPaint == null) {
			sBlurPaint = new Paint();
			sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * getResources()
					.getDisplayMetrics().density,
					android.graphics.BlurMaskFilter.Blur.NORMAL));
		}

		ColorMatrix cm = new ColorMatrix();
		Utilities.setContrastTranslateOnly(cm, 0.3f);
		mColorFilter = new ColorMatrixColorFilter(cm);

		mSelector = ThemeSettings.getStateListDrawable(context,
				R.drawable.shortcut_selector);
		setBackgroundDrawable(mSelector);
	}

	public void setHighlightStyle(int style) {
		mHighlightStyle = style;
	}

	public void setDrawSelectorSelf(boolean b) {
		mDrawSelectorSelf = b;
	}

	public boolean getDrawSelectorSelf() {
		return mDrawSelectorSelf;
	}

	public boolean needSelector() {
		return /* isPressed() || */isSelected() || isFocused();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mDrawSelectorSelf && needSelector()) {
			drawSelector(canvas);
		}
		super.onDraw(canvas);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		if (!isClickable()) {
			setClickable(true);
		}
		mClickListener = l;
	}

	@Override
	public boolean performClick() {
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);

		if (mClickListener != null) {
			postDelayed(new Runnable() {
				public void run() {
					if (isPressed()) {
						setPressed(false);
					}
					playSoundEffect(SoundEffectConstants.CLICK);
					mClickListener.onClick(ApplicationView.this);
				}
			}, ViewConfiguration.getPressedStateDuration());
			return true;
		}
		return false;
	}

	@Override
	public boolean performLongClick() {
		if (isPressed()) {
			setPressed(false);
		}
		return super.performLongClick();
	}

	final protected void drawSelector(Canvas canvas) {
		final int highlightStyle = HIGHLIGHT_CLASSIC ;//mHighlightStyle == -1 ? PreferenceHelper.getHighlightStyle(getContext()) : mHighlightStyle;
		
		drawSelector(canvas, highlightStyle);
	}

	final protected void drawSelector(Canvas canvas, int highlightStyle) {
		switch (highlightStyle) {
		case HIGHLIGHT_CLASSIC:
			drawClassicSelector(canvas);
			break;
		case HIGHLIGHT_GLOW:
			drawGlowSelector(canvas);
			break;
		}
	}

	private void drawClassicSelector(Canvas canvas) {
		final Drawable background = mSelector;
		if (background != null) {
			if (background.isStateful()) {
				background.setState(getDrawableState());
			}
			background.setBounds(0, 0, getWidth(), getHeight());
			final int scrollX = getScrollX();
			final int scrollY = getScrollY();

			if ((scrollX | scrollY) == 0) {
				background.draw(canvas);
			} else {
				canvas.translate(scrollX, scrollY);
				background.draw(canvas);
				canvas.translate(-scrollX, -scrollY);
			}
		}
	}

	private void drawGlowSelector(Canvas canvas) {
		final Drawable adrawable[] = getCompoundDrawables();
		if (adrawable[1] != null) {
			Drawable d = null;
			if (adrawable[1] instanceof TransitionDrawable) {
				final int level = ((TransitionDrawable) (adrawable[1]))
						.getLevel();
				d = ((TransitionDrawable) (adrawable[1])).getDrawable(level);
			} else {
				d = adrawable[1];
			}
			if (d == null) {
				return;
			}
			Bitmap bitmap = null;
			if (d instanceof BitmapDrawable) {
				bitmap = ((BitmapDrawable) d).getBitmap();
			} else if (d instanceof FastBitmapDrawable) {
				bitmap = ((FastBitmapDrawable) d).getBitmap();
			}
			if (bitmap != null) {
				Utilities.drawSelectedAllAppsBitmap(canvas, getScrollX(),
						getScrollY(), getWidth(), getHeight(),
						getPaddingLeft(), getPaddingTop(), isPressed(), bitmap);
			}
		}
	}

	public void getIconRect(Rect outRect) {
		Drawable[] drs = getCompoundDrawables();
		if (drs[1] != null) {
			final int iconWidth = drs[1].getIntrinsicWidth();
			final int iconHeight = drs[1].getIntrinsicHeight();
			outRect.set((getWidth() - iconWidth) / 2, getPaddingTop(),
					(getWidth() + iconWidth) / 2, getPaddingTop() + iconHeight);
		} else {
			outRect.set(0, 0, getWidth(), getHeight());
		}
	}

	public Rect getIconRect() {
		Rect rect = new Rect();
		getIconRect(rect);
		return rect;
	}

	public void getIndicatorRect(Rect mTempRect) {
		//final ApplicationInfo info = (ApplicationInfo) getTag();
		//boolean hasIndicator = (info != null && !info.isSystemApp);

		//		if (info != null && info instanceof AllAppsFolderInfo) {
		//			hasIndicator = true;
		//		}

		//		if (hasIndicator) {
		//			mTempRect.set(getWidth() * 1 / 2, 0, getWidth(), getHeight() / 3);
		//		} else {
		mTempRect.set(0, 0, 0, 0);
		//		}
	}

	public void setDragFlag(boolean flag) {
		if (mDragFlag != flag) {
			mDragFlag = flag;
			invalidate();
		}
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		mDragFlag = false;
	}
}
