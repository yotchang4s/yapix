package org.yotchang4s.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridViewV16 extends GridView {

	private int verticalSpacing;

	public GridViewV16(Context context) {
		super(context);
	}

	public GridViewV16(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewV16(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public int getVerticalSpacingComcat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return getVerticalSpacingComcat16();
		}
		return this.verticalSpacing;
	}

	@Override
	public void setVerticalSpacing(int verticalSpacing) {
		super.setVerticalSpacing(verticalSpacing);
		this.verticalSpacing = verticalSpacing;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public int getVerticalSpacingComcat16() {
		return getVerticalSpacing();
	}
}
