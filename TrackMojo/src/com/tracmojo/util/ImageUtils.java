package com.tracmojo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tracmojo.R;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImageUtils {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;
	Bitmap bitmap;
	public final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public  final int MEDIA_TYPE_IMAGE = 10;
	public final int SELECT_PICTURE = 10;
	public final int GALLERY_KITKAT_INTENT_CALLED=20;

	//	private  PopupWindow pwindoImage;
	//	private Uri fileUri;
	protected  String IMAGE_DIRECTORY_NAME = "";
	//	String photo_from = null;
	boolean isRoundedCorner = false, isRoundImage = false;	//photo = false, 
	Uri fileUri; // file url to store image/video
	String filepath;
	int ImageHeight;
	boolean isMultiplePhotoSelectionOn;

	Context mContext;
	
	private int DEFAULT_IMAGE = R.mipmap.ic_launcher;

	public ImageUtils(Context context) {
		mContext = context;
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		bitmap = null;
		IMAGE_DIRECTORY_NAME = mContext.getResources().getString(R.string.app_name);

		options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.build();
	}

	/**
	 * Image From Camera/Gallery Functions Start
	 * 
	 */

	public void getImage(Context context, boolean photo){
		mContext = context;
		isRoundImage = false;
		showPhotoSelectionDialog(mContext, photo);    	
	}

	public void getRoundedImage(Context context, boolean photo, boolean isRoundedCorner, int height){
		mContext = context;
		this.isRoundedCorner = isRoundedCorner;
		ImageHeight = height;
		isRoundImage = true;
		showPhotoSelectionDialog(mContext, photo);
	}

	public void showPhotoSelectionDialog(final Context mContext, boolean photo){
		try {
			final CharSequence[] items =mContext.getResources().getStringArray(R.array.PhotoSelectionOptions);

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getString(R.string.photoselection_dialog_header));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					// Do something with the selection
					//mDoneButton.setText(items[item]);
					if(item == 0){
						if (!isDeviceSupportCamera(mContext)) {
							Toast.makeText(mContext,"Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();		                
						}else{
							dialog.dismiss();
							// capture picture
							captureImage(mContext);
						}	
					}else if(item == 1){
						galleryImage(mContext);
					}else if(item == 2){
						dialog.dismiss();
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*@SuppressLint("InflateParams")
	public  void initiatePopupWindowImage(final Context context, boolean photo) {
		try {

			IMAGE_DIRECTORY_NAME = context.getResources().getString(R.string.app_name);

			final Dialog dialog = new Dialog(mContext); 
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); 
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			dialog.setContentView(R.layout.image_upload_popup); 
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(); 
			lp.copyFrom(dialog.getWindow().getAttributes()); 
			lp.width = LayoutParams.MATCH_PARENT; 
			lp.height = LayoutParams.MATCH_PARENT; 
			dialog.getWindow().setAttributes(lp); 
			dialog.show();

			Button btn_cancel = (Button) dialog.findViewById(R.id.btn_postadd_cancel);
			Button btn_take_photo = (Button) dialog.findViewById(R.id.btn_postadd_take_or_remove_photo);
			Button btn_choose_photo = (Button) dialog.findViewById(R.id.btn_postadd_choose_or_replace_photo);

			if(photo){
				btn_take_photo.setText(context.getResources().getString(R.string.replace_photo));
			}		

			btn_cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			btn_take_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Checking camera availability
					if (!isDeviceSupportCamera(context)) {
						Toast.makeText(context,"Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();		                
					}else{
						dialog.dismiss();
						// capture picture
						captureImage(context);
					}					
				}
			});

			btn_choose_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					galleryImage(context);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


	/**
	 * Checking device has camera hardware or not
	 * */
	private  boolean isDeviceSupportCamera(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * Capturing Camera Image will lauch camera app requrest image capture
	 * @param context 
	 */
	public  void captureImage(Context context) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); 
		Log.e("In Capture Image", "==>"+fileUri);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// start the image capture Intent
		((Activity) context).startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);          
	}

	/**
	 * Open Gallery to choose exiting photos
	 */
	public void galleryImage(Context context) {
		if (Build.VERSION.SDK_INT <19){
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			((Activity) context).startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
		} else {
			//Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			((Activity) context).startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
		}    	
	}

	public  String getPath(Uri uri, Context context) {
		// just some safety built in 
		if( uri == null ) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		if( cursor != null ){
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fallback here
		return uri.getPath();
	}    


	/**
	 * Creating file uri to store image/video
	 */
	public  Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	private  File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		} 
		return mediaFile;
	}


	public  void previewCapturedImage(ImageView imageview ,Context context, String selectedImagePath, String photo_from) {
		try {   			

			Log.e("photo_from :", ""+photo_from);
			File file = null;
			if(photo_from!=null && photo_from.equals(context.getResources().getString(R.string.photo_from_camera))){

				file = new File(selectedImagePath);

				Log.e("Captured Path :", ""+file.getAbsolutePath());

			}else if(photo_from!=null && photo_from.equals(context.getResources().getString(R.string.photo_from_gallery))){

				file = new File(selectedImagePath.toString());          	  
			}

			if(file!=null && file.exists()){  
				new ResizeImage(context,imageview, photo_from).execute(selectedImagePath);
			}else{
				Toast.makeText(context, "Error in Load Image", Toast.LENGTH_LONG).show();
			}           

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public  Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	/*class ResizeImage extends AsyncTask<String, Void, String> {

		String errorMessage="";

		ProgressDialog progressDialog;
		ByteArrayOutputStream baos;

		Context mContext;
		ImageView imageView;
		String photo_from;
		public ResizeImage(Context context, ImageView imageview, String photoFrom) {
			// TODO Auto-generated constructor stub
			mContext = context;
			this.imageView = imageview;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "", "Please wait...");
			progressDialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			try{

				File f =new File(params[0]);		       
				Matrix matrix = null;

				if(photo_from!=null && !photo_from.equals("") && photo_from.equals(mContext.getResources().getString(R.string.photo_from_camera))){
					ExifInterface exif1 = new ExifInterface(f.getAbsolutePath());
					int orientation = exif1.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
					Log.d("EXIF", "Exif: " + orientation);
					matrix = new Matrix();
					if (orientation == 6) {
						matrix.postRotate(90);
					}
					else if (orientation == 3) {
						matrix.postRotate(180);
					}
					else if (orientation == 8) {
						matrix.postRotate(270);
					}	              
				}   

				try
				{
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),option);

					//bitmap = BitmapFactory.decodeFile(fileUri.getPath(), option);
					ExifInterface ei = new ExifInterface(f.getAbsolutePath());
					int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					if (bitmap != null) {
						switch (orientation) {
								case 0:
							bitmap = RotateBitmap(bitmap, 90);
							break;
						case ExifInterface.ORIENTATION_ROTATE_90:
							bitmap = RotateBitmap(bitmap, 90);
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							bitmap = RotateBitmap(bitmap, 180);
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							bitmap = RotateBitmap(bitmap, 270);
							break;
							// etc.
						}
					}

					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

					bitmap.compress(CompressFormat.PNG,50, baos);

					if(isRoundImage){
						if(isRoundedCorner){
							//								If You want only Rounded Corner Just give last argument as true
							bitmap = scaleCenterCrop(bitmap, ImageHeight, true);							
						}else{
							//						 		It will Round Image
							bitmap = scaleCenterCrop(bitmap, ImageHeight, false);
						}
					}						

				}catch(OutOfMemoryError e){
					e.printStackTrace();
				}
				catch (Exception e)
				{			       
					Log.e("Bitmap Load Error", "Failed to load");
				}

			} catch(Throwable e){
				e.printStackTrace();
			}
			return errorMessage;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
				progressDialog=null;
			}
			if(bitmap!=null && imageView!=null){
				imageView.setImageBitmap(bitmap);
			}			
		}
	}*/
	
	class ResizeImage extends AsyncTask<String, Void, String> {

		String errorMessage = "";

		ProgressDialog progressDialog;
		ByteArrayOutputStream baos;
		Context mContext;
		ImageView imageView;
		String photo_from;
		
		public ResizeImage(Context context, ImageView imageview, String photoFrom) {
			// TODO Auto-generated constructor stub
			mContext = context;
			this.imageView = imageview;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "", "Please wait...");
			progressDialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			try {

				File f = new File(params[0]);

				ExifInterface exif1 = new ExifInterface(f.getAbsolutePath());
				int orientation = exif1.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
				Log.d("EXIF", "Exif: " + orientation);
				Matrix matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
				} else if (orientation == 3) {
					matrix.postRotate(180);
				} else if (orientation == 8) {
					matrix.postRotate(270);
				}

				try {
					/*BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), option);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

					bitmap.compress(CompressFormat.PNG, 50, baos);*/
					
					BitmapFactory.Options option = new BitmapFactory.Options();
			    	
			    	 int file_size = Integer.parseInt(String.valueOf(f.length() / 1024));
	    		     Log.e("file_size", " " + file_size);
	    		
	    		     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    		 bitmap = null;
		    		    
	    		     if (file_size < 1000) {
	    		    	 option.inSampleSize = 2;			    		    	 
	    		     }else{
	    		    	 option.inSampleSize = 4;			    		    	 
	    		     }					    	
						
	    		     bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),option);
	    		     
			    	Log.e("Width is:", " " + bitmap.getWidth());
			    	Log.e("Height is:", " " + bitmap.getHeight());
			    	
			    	if(bitmap.getWidth()>1280 || bitmap.getHeight()>1000){
			    		
			    		bitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
			    		bitmap.compress(CompressFormat.PNG,50, baos);
			    		
			    	}else{
			    		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
					    
				    	bitmap.compress(CompressFormat.PNG,50, baos);
			    	}	
			    	
			    	if(isRoundImage){
						if(isRoundedCorner){
							//								If You want only Rounded Corner Just give last argument as true
							bitmap = scaleCenterCrop(bitmap, ImageHeight, true);							
						}else{
							//						 		It will Round Image
							bitmap = scaleCenterCrop(bitmap, ImageHeight, false);
						}
					}	

				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (Exception e) {

					Log.e("Bitmap Load Error", "Failed to load");
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
			return errorMessage;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			/*if (bitmap != null) {
				ivPhoto.setImageBitmap(bitmap);
			}*/
			
			if(bitmap!=null){
								
				int file_size = sizeOf(bitmap);
				file_size = file_size/1024;
				if(file_size <= 5120){
					imageView.setImageBitmap(bitmap);	
				}else{
					//showAlertDialog(getResources().getString(R.string.error_large_image), 0);
					Toast.makeText(mContext, "Image Loading Error", Toast.LENGTH_SHORT).show();
					bitmap = null;
				}					
			}else{
				imageView.setImageResource(DEFAULT_IMAGE);
				Toast.makeText(mContext, "Image Loading Error", Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected int sizeOf(Bitmap data) {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return data.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, boolean corner) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width, respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newHeight / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newHeight - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our new,
		// scaled bitmap onto it.
		Bitmap dest = Bitmap.createBitmap(newHeight, newHeight, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);
		dest = getRoundedBitmap(dest, corner);
		return dest;
	}

	public static Bitmap getRoundedBitmap(Bitmap bitmap, boolean corner) {
		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final int color = Color.RED;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 6;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		if (corner)
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		else
			canvas.drawOval(rectF, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		bitmap.recycle();

		return output;
	}



	public String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public Uri getFileURI(){
		return fileUri;
	}
}
