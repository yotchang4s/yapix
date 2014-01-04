package org.yotchang4s.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ObservableGridView extends GridViewV16 {

	private int mHeight = 0;
	private int[] mItemOffsetY = new int[0];

	public ObservableGridView(Context context) {
		super(context);
	}

	public ObservableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObservableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void computeScrollY() {
		int itemCount = getAdapter().getCount();

		this.mHeight = 0;
		this.mItemOffsetY = new int[itemCount];

		int beforeHeight = 0;

		for (int i = 0; i < itemCount; i++) {
			if (i % getNumColumns() == 0) {
				View view = getAdapter().getView(i, null, this);
				android.view.ViewGroup.LayoutParams layoutParams = view
						.getLayoutParams();
				int height;
				if (layoutParams == null) {
					view.measure(MeasureSpec.makeMeasureSpec(0,
							MeasureSpec.UNSPECIFIED), MeasureSpec
							.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
					height = view.getMeasuredHeight()
							+ getVerticalSpacingComcat();
				} else {
					height = layoutParams.height + getVerticalSpacingComcat();
				}
				beforeHeight = this.mHeight;
				this.mHeight += height;
			}
			this.mItemOffsetY[i] = beforeHeight;
		}
	}

	public int getComputedScrollY() {
		if (this.mItemOffsetY.length == 0) {
			return 0;
		} else {
			int pos = getFirstVisiblePosition();
			View view = getChildAt(0);
			return this.mItemOffsetY[pos] - view.getTop() + getPaddingTop();
		}
	}

	public int getGridHeight() {
		return this.mHeight;
	}
}