package com.zenpets.users.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TypefaceSpan extends MetricAffectingSpan {
	/** An <code>LruCache</code> for previously loaded typefaces. */
	private static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(12);
	private Typeface mTypeface;
	
	public TypefaceSpan(Context context) {
		mTypeface = sTypefaceCache.get("HelveticaNeueLTW1G-Cn.otf");
		
		if (mTypeface == null)	{
			mTypeface = Typeface.createFromAsset(context.getApplicationContext() .getAssets(), String.format("fonts/%s", "HelveticaNeueLTW1G-Cn.otf"));
			
			// Cache the loaded Typeface
			sTypefaceCache.put("HelveticaNeueLTW1G-Cn.otf", mTypeface);
		}
	}
	
	@Override
	public void updateMeasureState(TextPaint p)	{
		p.setTypeface(mTypeface);
		
		// Note: This flag is required for proper typeface rendering
		p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
	
	@Override
	public void updateDrawState(TextPaint tp)	{
		tp.setTypeface(mTypeface);
		
		// Note: This flag is required for proper typeface rendering
		tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
}