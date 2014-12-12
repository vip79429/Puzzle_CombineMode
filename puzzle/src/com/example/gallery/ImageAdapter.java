package com.example.gallery;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import appGame.Puzzle.CombineMode.PuzzleActivity;

public class ImageAdapter extends BaseAdapter {// private ArrayList<byte[]>
												// dishImages = new
												// ArrayList<byte[]>();
	// private ImageView[] mImages;

	Context context;
	ArrayList<Uri> imgResId;
	DisplayMetrics metrics;
	
	public ImageAdapter(PuzzleActivity gallery1Activity, ArrayList<Uri> imgresId, DisplayMetrics metrics) {
		// TODO Auto-generated constructor stub
		super();
		this.context = gallery1Activity;
		this.imgResId = imgresId;
		this.metrics = metrics;
	}

	// public boolean createReflectedImages() {
	// final int reflectionGap = 4;
	// int index = 0;
	// System.out
	// .println("dishImages size "
	// + dishImages
	// .size());
	// for (int i = 0; i < dishImages
	// .size(); ++i) {
	// System.out
	// .println("dishImage --- "
	// + dishImages
	// .get(i));
	// Bitmap originalImage = BitmapFactory
	// .decodeByteArray(
	// dishImages
	// .get(i),
	// 0,
	// dishImages
	// .get(i).length);
	// int width = originalImage
	// .getWidth();
	// int height = originalImage
	// .getHeight();
	// Matrix matrix = new Matrix();
	// matrix.preScale(1, -1);
	// Bitmap reflectionImage = Bitmap
	// .createBitmap(
	// originalImage,
	// 0,
	// height / 2,
	// width,
	// height / 2,
	// matrix,
	// false);
	//
	// Bitmap bitmapWithReflection = Bitmap
	// .createBitmap(
	// width,
	// (height + height / 2),
	// Config.ARGB_8888);
	//
	// Canvas canvas = new Canvas(
	// bitmapWithReflection);
	// canvas.drawBitmap(
	// originalImage,
	// 0, 0, null);
	// Paint deafaultPaint = new Paint();
	// canvas.drawRect(
	// 0,
	// height,
	// width,
	// height
	// + reflectionGap,
	// deafaultPaint);
	// canvas.drawBitmap(
	// reflectionImage,
	// 0,
	// height
	// + reflectionGap,
	// null);
	// Paint paint = new Paint();
	// LinearGradient shader = new LinearGradient(
	// 0,
	// originalImage
	// .getHeight(),
	// 0,
	// bitmapWithReflection
	// .getHeight()
	// + reflectionGap,
	// 0x70ffffff,
	// 0x00ffffff,
	// TileMode.CLAMP);
	// paint.setShader(shader);
	// paint.setXfermode(new PorterDuffXfermode(
	// Mode.DST_IN));
	// canvas.drawRect(
	// 0,
	// height,
	// width,
	// bitmapWithReflection
	// .getHeight()
	// + reflectionGap,
	// paint);
	// ImageView imageView = new ImageView(
	// context);
	// imageView
	// .setImageBitmap(bitmapWithReflection);
	// // imageView.setLayoutParams(new GalleryFlow.LayoutParams(180,
	// // 240));
	// imageView
	// .setLayoutParams(new DetialGallery.LayoutParams(
	// 170,
	// 200));
	// imageView.setScaleType(ScaleType.FIT_XY);
	// mImages[index++] = imageView;
	// }
	// return true;
	// }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return Integer.MAX_VALUE;
		return Integer.MAX_VALUE;

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return position;
		//
		// return position;
		// return position;

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		ImageView imageView = new ImageView(context);
//		imageView.setImageBitmap(imgResId.get(arg0 % imgResId.size()));
		imageView.setImageURI(imgResId.get(arg0 % imgResId.size()));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		com.example.gallery.DetialGallery.LayoutParams p1 = new com.example.gallery.DetialGallery.LayoutParams(
				(int) (metrics.widthPixels * 0.7),
				(int) (metrics.heightPixels * 0.36));
//		p1.gravity = Gravity.CENTER;
//		p1.bottomMargin = (int) (metrics.heightPixels * 0.02);
		imageView.setLayoutParams(p1);

		return imageView;

	}

}
