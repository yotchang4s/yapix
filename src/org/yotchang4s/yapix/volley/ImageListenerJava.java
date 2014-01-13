package org.yotchang4s.yapix.volley;

import com.android.volley.VolleyError;

public abstract class ImageListenerJava implements
		com.android.volley.toolbox.ImageLoader.ImageListener {

	@Override
	public final void onResponse(
			com.android.volley.toolbox.ImageLoader.ImageContainer response,
			boolean isImmediate) {
		onResponse(new ImageContainerJava(response), isImmediate);
	}

	public abstract void onErrorResponse(VolleyError error);

	public abstract void onResponse(ImageContainerJava imageContainer,
			boolean isImmediate);
}
