package org.yotchang4s.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

	private int horizontalSpacing;
	private int verticalSpacing;

	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, new int[] {
				android.R.attr.horizontalSpacing,
				android.R.attr.verticalSpacing });

		this.horizontalSpacing = a.getDimensionPixelOffset(0, 0);
		this.verticalSpacing = a.getDimensionPixelOffset(1, 0);

		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childLeft = getPaddingLeft();
		int childTop = getPaddingTop();
		int lineHeight = 0;
		// 100 is a dummy number
		int myWidth = resolveSize(100, widthMeasureSpec);
		int wantedHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			// let the child measure itself
			child.measure(
					getChildMeasureSpec(widthMeasureSpec, 0,
							child.getLayoutParams().width),
					getChildMeasureSpec(heightMeasureSpec, 0,
							child.getLayoutParams().height));
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			// lineheight is the height of current line, should be the height of
			// the heightest view
			lineHeight = Math.max(childHeight, lineHeight);
			if (childWidth + childLeft + getPaddingRight() > myWidth) {
				// wrap this line
				childLeft = getPaddingLeft();
				childTop += this.verticalSpacing + lineHeight;
				if (i != getChildCount() - 1) {
					lineHeight = 0;
				}
			}
			childLeft += childWidth + this.horizontalSpacing;
		}
		wantedHeight += childTop + lineHeight + getPaddingBottom();
		setMeasuredDimension(myWidth,
				resolveSize(wantedHeight, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childLeft = getPaddingLeft();
		int childTop = getPaddingTop();
		int lineHeight = 0;
		int myWidth = right - left;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			lineHeight = Math.max(childHeight, lineHeight);
			if (childWidth + childLeft + getPaddingRight() > myWidth) {
				childLeft = getPaddingLeft();
				childTop += this.verticalSpacing + lineHeight;
				lineHeight = 0;
			}
			child.layout(childLeft, childTop, childLeft + childWidth, childTop
					+ childHeight);
			childLeft += childWidth + this.horizontalSpacing;
		}
	}
}