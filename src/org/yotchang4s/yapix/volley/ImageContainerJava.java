package org.yotchang4s.yapix.volley;

import android.graphics.Bitmap;

public class ImageContainerJava {
	private final com.android.volley.toolbox.ImageLoader.ImageContainer imageContainer;

	public ImageContainerJava(
			com.android.volley.toolbox.ImageLoader.ImageContainer imageContainer) {
		this.imageContainer = imageContainer;
	}

	public void cancelRequest() {
		this.imageContainer.cancelRequest();
	}

	public Bitmap getBitmap() {
		return imageContainer.getBitmap();
	}

	public String getRequestUrl() {
		return imageContainer.getRequestUrl();
	}
}