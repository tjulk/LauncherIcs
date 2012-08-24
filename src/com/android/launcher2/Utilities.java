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

package com.android.launcher2;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.graphics.TableMaskFilter;

import com.android.launcher2.theme.ThemeSettings;

/**
 * Various utilities shared amongst the Launcher's classes.
 */
public final class Utilities {
    private static final String TAG = "Launcher.Utilities";

    private static int sIconWidth = -1;
    private static int sIconHeight = -1;
    private static int sIconTextureWidth = -1;
    private static int sIconTextureHeight = -1;

    private static final Paint sBlurPaint = new Paint();
    private static final Paint sGlowColorPressedPaint = new Paint();
    private static final Paint sGlowColorFocusedPaint = new Paint();
    private static final Paint sDisabledPaint = new Paint();
    private static final Rect sOldBounds = new Rect();
    private static final Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG));
    }
    static int sColors[] = { 0xffff0000, 0xff00ff00, 0xff0000ff };
    static int sColorIndex = 0;

    /**
     * Returns a bitmap suitable for the all apps view. Used to convert pre-ICS
     * icon bitmaps that are stored in the database (which were 74x74 pixels at hdpi size)
     * to the proper size (48dp)
     */
    public static Bitmap createIconBitmap(Bitmap icon, Context context) {
    	
    	Log.d(TAG, "createIconBitmap bitmap");
        int textureWidth = sIconTextureWidth;
        int textureHeight = sIconTextureHeight;
        int sourceWidth = icon.getWidth();
        int sourceHeight = icon.getHeight();
        if (sourceWidth > textureWidth && sourceHeight > textureHeight) {
            // Icon is bigger than it should be; clip it (solves the GB->ICS migration case)
            return Bitmap.createBitmap(icon,
                    (sourceWidth - textureWidth) / 2,
                    (sourceHeight - textureHeight) / 2,
                    textureWidth, textureHeight);
        } else if (sourceWidth == textureWidth && sourceHeight == textureHeight) {
            // Icon is the right size, no need to change it
            return icon;
        } else {
            // Icon is too small, render to a larger bitmap
            return createIconBitmap(new BitmapDrawable(icon), context);
        }
    }

    
    //Pekall LK
    private static Drawable[] sBackgrounds;
    
    /**
     * Returns a bitmap suitable for the all apps view.
     */
    public static Bitmap createIconBitmap(Drawable icon, Context context) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }

            Log.d(TAG, "createIconBitmap drawable");
            
            int width = sIconWidth;
            int height = sIconHeight;

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            } else if (icon instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();
            
            Log.d(TAG, "sHasBackgraound:"+sHasBackgraound+" sBackgrounds:"+sBackgrounds+" width:"+width+" height:"+height+" sourceWidth:"+sourceWidth);

            //Pekall LK
			Drawable bg = null;
			if (sHasBackgraound && sBackgrounds != null
					&& sBackgrounds.length > 0) {
	            //Pekall LK if app icon size is bigger than background , reset it
	            if (sourceWidth>=width)
	            	sourceWidth = (int)(width*(4f/5));
	            if (sourceHeight>=height)
	            	sourceHeight =(int)(height*(4f/5));	
	            // && (width > sourceWidth || height > sourceHeight)) {
				bg = sBackgrounds[sRandom.nextInt(sBackgrounds.length)];
				Log.d(TAG, "icon bg is not null");
			} else {
				bg = null;
				Log.d(TAG, "icon bg is null");
			}
            
            if (sourceWidth > 0 && sourceHeight > 0) {
                // There are intrinsic sizes.
                if (width < sourceWidth || height < sourceHeight) {
                    // It's too big, scale it down.
                    final float ratio = (float) sourceWidth / sourceHeight;
                    if (sourceWidth > sourceHeight) {
                        height = (int) (width / ratio);
                    } else if (sourceHeight > sourceWidth) {
                        width = (int) (height * ratio);
                    }
                } else if (sourceWidth < width && sourceHeight < height) {
                    // Don't scale up the icon
                    width = sourceWidth;
                    height = sourceHeight;
                }
            }
            
            // no intrinsic size --> use default size
            int textureWidth = sIconTextureWidth;
            int textureHeight = sIconTextureHeight;

            final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            canvas.setBitmap(bitmap);

            
			if (bg != null) {
				bg.setBounds(0, 0, textureWidth, textureHeight);
				bg.draw(canvas);
				Log.d(TAG, "icon bg is not null ,set bg it to canvas");
			}  
            
            
            final int left = (textureWidth-width) / 2;
            final int top = (textureHeight-height) / 2;

            sOldBounds.set(icon.getBounds());
            icon.setBounds(left, top, left+width, top+height);
            icon.draw(canvas);
            icon.setBounds(sOldBounds);
            canvas.setBitmap(null);

            return bitmap;
        }
    }

    /**
     * Returns a Bitmap representing the thumbnail of the specified Bitmap.
     * The size of the thumbnail is defined by the dimension
     * android.R.dimen.launcher_application_icon_size.
     *
     * @param bitmap The bitmap to get a thumbnail of.
     * @param context The application's context.
     *
     * @return A thumbnail for the specified bitmap or the bitmap itself if the
     *         thumbnail could not be created.
     */
    static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }

            if (bitmap.getWidth() == sIconWidth && bitmap.getHeight() == sIconHeight) {
                return bitmap;
            } else {
                return createIconBitmap(new BitmapDrawable(bitmap), context);
            }
        }
    }

    static Bitmap drawDisabledBitmap(Bitmap bitmap, Context context) {
        synchronized (sCanvas) { // we share the statics :-(
            if (sIconWidth == -1) {
                initStatics(context);
            }
            final Bitmap disabled = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            canvas.setBitmap(disabled);
            
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, sDisabledPaint);

            canvas.setBitmap(null);

            return disabled;
        }
    }

    //Pekall LK
	private static boolean sHasBackgraound = true;
	private static Random sRandom;
    
    private static void initStatics(Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        final float density = metrics.density;

        //Pekall LK
		sHasBackgraound = ThemeSettings.getBoolean(context,
				R.bool.config_icon_has_background);
		sBackgrounds = loadIconBackground(context);
		sRandom = new Random();
		
        sIconWidth = sIconHeight = (int) (resources.getDimension(R.dimen.app_icon_size)*(sHasBackgraound?(5f / 4):1));
        
        sIconTextureWidth = sIconTextureHeight = sIconWidth;

        sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * density, BlurMaskFilter.Blur.NORMAL));
        sGlowColorPressedPaint.setColor(0xffffc300);
        sGlowColorPressedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
        sGlowColorFocusedPaint.setColor(0xffff8e00);
        sGlowColorFocusedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.2f);
        sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        sDisabledPaint.setAlpha(0x88);
    }
    
	public static void drawSelectedAllAppsBitmap(Canvas dest, int scrollX,
			int scrollY, int destWidth, int destHeight, int paddingLeft,
			int paddingTop, boolean pressed, Bitmap src) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				// We can't have gotten to here without src being initialized,
				// which
				// comes from this file already. So just assert.
				// initStatics(context);
				throw new RuntimeException(
						"Assertion failed: Utilities not initialized");
			}

			int[] xy = new int[2];
			Bitmap mask = src.extractAlpha(sBlurPaint, xy);

			float px = (destWidth - mask.getWidth()) / 2;
			float py = paddingTop - (mask.getHeight() - src.getHeight()) / 2;

			if ((scrollX | scrollY) == 0) {
				dest.drawBitmap(mask, px, py, pressed ? sGlowColorPressedPaint
						: sGlowColorFocusedPaint);
			} else {
				dest.translate(scrollX, scrollY);
				dest.drawBitmap(mask, px, py, pressed ? sGlowColorPressedPaint
						: sGlowColorFocusedPaint);
				dest.translate(-scrollX, -scrollY);
			}

			mask.recycle();
		}
	}
	
	public static void setContrastTranslateOnly(ColorMatrix cm, float contrast) {
		float scale = contrast + 1.f;
		float translate = (-.5f * scale + .5f) * 255.f;
		cm.set(new float[] { 1, 0, 0, 0, translate, 0, 1, 0, 0, translate, 0,
				0, 1, 0, translate, 0, 0, 0, 1, 0 });
	}
	
	public static Drawable tintThePicture(Context context, Drawable d,
			int reflectionHeight) {
		if (reflectionHeight == 0 || d == null) {
			return d;
		}
		BitmapDrawable mD = (BitmapDrawable) d;
		final int width = mD.getIntrinsicWidth();
		final int height = mD.getIntrinsicHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(mD.getBitmap(), 0, height
				- reflectionHeight, width, reflectionHeight, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + reflectionHeight), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(mD.getBitmap(), 0, 0, null);
		canvas.drawBitmap(reflectionImage, 0, height, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, width, 0,
				bitmapWithReflection.getHeight(), 0x70ffffff, 0x00ffffff,
				TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight(),
				paint);

		return new BitmapDrawable(context.getResources(), bitmapWithReflection);
	}
	
	//Pekall LK
	private static Drawable[] loadIconBackground(Context context) {
		String[] names = ThemeSettings.getStringArray(context,
				R.array.ic_shortcut_background, null);

		if (names == null
				|| ThemeSettings.THEME_DEFAULT.equals(ThemeSettings
						.getCurrentThemePackage())) {
			Resources res = context.getResources();
			names = res.getStringArray(R.array.ic_shortcut_background);
			Drawable[] ds = new Drawable[names.length];
			for (int i = 0; i < names.length; i++) {
				int id = res.getIdentifier(names[i], "drawable", context
						.getPackageName());
				if (id != 0) {
					ds[i] = res.getDrawable(id);
				}
			}
			return ds;
		} else {

			Drawable[] ds = new Drawable[names.length];

			int count = 0;
			for (int i = 0; i < names.length; i++) {
				ds[i] = ThemeSettings.getDrawable(context, names[i], null);
				if (ds[i] != null)
					count++;
			}
			// remove null items
			if (count != names.length && count > 0) {
				Drawable[] drawables = new Drawable[count];
				int j = 0;
				for (int i = 0; i < ds.length; i++) {
					if (ds[i] != null && j < count) {
						drawables[j++] = ds[i];
					}
				}
				ds = drawables;
			}
			return ds;
		}
	}
	
}
