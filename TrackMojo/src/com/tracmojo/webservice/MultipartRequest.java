package com.tracmojo.webservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

public class MultipartRequest extends Request<String> {

	// private MultipartEntity entity = new MultipartEntity();

	MultipartEntityBuilder entity = MultipartEntityBuilder.create();
	HttpEntity httpentity;
	String FILE_PART_NAME = "file";

	private final Response.Listener<String> mListener;
	private Response.ErrorListener mErrorListener;
	// private final File mFilePart;
	private final Map<String, String> mStringPart;
	private Map<String, File> mByteparams;

	public MultipartRequest(String url, Response.ErrorListener errorListener,
			Response.Listener<String> listener, Map<String, File> byteparams,
			Map<String, String> mStringPart) {
		super(Method.POST, url, errorListener);

		mErrorListener = errorListener;
		mByteparams = byteparams;
		mListener = listener;
		/*
		 * mFilePart = file; FILE_PART_NAME = file_key;
		 */
		this.mStringPart = mStringPart;
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		buildMultipartEntity();
	}

	public void addStringBody(String param, String value) {
		mStringPart.put(param, value);
	}

	private void buildMultipartEntity() {
		// entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
		for (String key : mByteparams.keySet()) {

			Matrix matrix = null;
			ExifInterface exif1;
			try {
				exif1 = new ExifInterface(mByteparams.get(key).getAbsolutePath());
				
				int orientation = exif1.getAttributeInt(
						ExifInterface.TAG_ORIENTATION, 1);
				matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
				} else if (orientation == 3) {
					matrix.postRotate(180);
				} else if (orientation == 8) {
					matrix.postRotate(270);
				}

								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = 2;
			Bitmap bmp = BitmapFactory.decodeFile(mByteparams.get(key)
					.getAbsolutePath(), option);
			
			bmp = Bitmap.createBitmap(bmp, 0, 0,
					bmp.getWidth(), bmp.getHeight(), matrix,
					true); // rotating bitmap
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG, 60, bos);
			InputStream in = new ByteArrayInputStream(bos.toByteArray());
			ContentBody foto = new InputStreamBody(in, "filename.jpg");
			//ContentBody foto = new InputStreamBody(in, filename)

			entity.addPart(key, foto);

		}

		for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
			entity.addTextBody(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public String getBodyContentType() {
		return httpentity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			httpentity = entity.build();

			httpentity.writeTo(bos);

		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		return Response.success(new String(response.data), getCacheEntry());
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}
	
	@Override
	public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
		// TODO Auto-generated method stub
		retryPolicy = new DefaultRetryPolicy(30000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return super.setRetryPolicy(retryPolicy);
	}
	
	@Override
	public void deliverError(VolleyError error) {
		// TODO Auto-generated method stub

		Log.e("Deliver Error", "True");

		mErrorListener.onErrorResponse(error);
		super.parseNetworkError(error);
	}
	
	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		if (volleyError.networkResponse != null
				&& volleyError.networkResponse.data != null) {
			Log.e("status Code", " code "
					+ volleyError.networkResponse.statusCode);

			try {
				JSONObject jsonobject = new JSONObject(new String(
						volleyError.networkResponse.data));

				VolleyError error = new VolleyError(
						jsonobject.optString("message"));
				volleyError = error;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			
			VolleyError error = new VolleyError(VolleySetup.ErrorMessage);
			volleyError = error;
		}

		return volleyError;
	}
	
	
}