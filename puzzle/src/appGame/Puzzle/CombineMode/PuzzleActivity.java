package appGame.Puzzle.CombineMode;

import imac.gc.RecycleAllView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.DetialGallery;
import com.example.gallery.ImageAdapter;
import com.polites.android.GestureImageView;

public class PuzzleActivity extends Activity {
	/** Called when the activity is first created. */

	private int x_count = 4; // 拼圖鈕最大列數
	private int y_count = 4; // 拼圖鈕最大欄數
	private int sence; // 記錄所在的XML

	private int goal_image_id = R.drawable.a; // 初始圖片參數
	private int i1 = 1;
	private String i2 = "";
	private int file_length = 0;
	private Bitmap bmpBuffer = null;
	private Bitmap bmpBuffer_red = null;
	
	// 呼叫相機
	private static final int CAMERA_WITH_DATA = 3023;
	// 呼叫相簿
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	// 照片目錄
	private static final String local_file = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/Puzzle_Photo/";
	// 照片檔案
	private File[] Photofiles;

	private DetialGallery gallery;
	private TextView commt;

	// 感應器
	// private SensorManager mSensorManager;
	// private Sensor mSensor;

	// 旋轉時調整textview
	private LinearLayout lin_level;

	private ArrayList<Uri> imgResId;
	private DisplayMetrics metrics;

	private int init_save_count = 1;
	private int mCurTime = (int) System.currentTimeMillis() - 3000000,
			mLastTime;
	private LinearLayout main_ly;
	private LinearLayout Scroll_layout;
	private float x1,x2,y1,y2; //滑動方向
	// private boolean sensor_close = true;
	// private Handler handler = new Handler();; // sensor驅動者
	public static boolean screen = false; // 全螢幕手勢判斷
	private Bitmap bt_scroll = null;
	
	private int specialMode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sence = R.layout.main;// 記錄目前在main
		init();
	}

	public void init() {// 初始化
		// sensor_close = true;
		init_save_count = 1;
		
		Button btnEasy;
		Button btnMiddle;
		Button btnHard;
		Button btnTurnBeads;
		Button btnTwoLayer;
		// 取得按鈕元件
		btnEasy = (Button) findViewById(R.id.btn_3x3);
		btnMiddle = (Button) findViewById(R.id.btn_4x4);
		btnHard = (Button) findViewById(R.id.btn_5x5);
		btnTurnBeads = (Button) findViewById(R.id.btn_trunbeads);
		btnTwoLayer = (Button) findViewById(R.id.btn_twolayer);
		// 設定Click事件
		btnEasy.setOnClickListener(onbtnChoose);
		btnMiddle.setOnClickListener(onbtnChoose);
		btnHard.setOnClickListener(onbtnChoose);
		btnTurnBeads.setOnClickListener(onbtnChoose);
		btnTwoLayer.setOnClickListener(onbtnChoose);
		
		lin_level = (LinearLayout) findViewById(R.id.lin_level);

		main_ly = (LinearLayout) findViewById(R.id.main_ly);
		
		// 選取圖片的Button
		Button btn_next = (Button) findViewById(R.id.NEXT_BUTTON);// 設定觸碰更換圖片按鈕的反應
		btn_next.setOnClickListener(getchangeimageNEXT);

		Button btn_pre = (Button) findViewById(R.id.Previous_BUTTON);// 設定觸碰更換圖片按鈕的反應
		btn_pre.setOnClickListener(getchangeimagePrevious);

		Button btn_bro = (Button) findViewById(R.id.Browse_BUTTON);// 設定外面載入圖片按鈕的反應
		btn_bro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// Launch picker to choose photo for selected contact
					final Intent intent = getPhotoPickIntent();
					startActivityForResult(intent, PHOTO_PICKED_WITH_DATA); // 跳轉至相簿
				} catch (ActivityNotFoundException e) {

				}
			}
		});

		Button btn_cam = (Button) findViewById(R.id.Camera_BUTTON);// 設定拍照按鈕的反應
		btn_cam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// Launch picker to choose photo for selected contact
					Uri imageUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "workupload.jpg"));
					Intent intent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, CAMERA_WITH_DATA); // 跳轉至拍照
				} catch (ActivityNotFoundException e) {

				}
			}
		});
		imgResId = new ArrayList<Uri>();

		// 判斷目錄是否存在
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(local_file);
			if (!file.exists())
				file.mkdirs(); // 建立照片儲存目錄

			// 將原始圖片複製到指定路徑
			creatDecodeBitmap(R.drawable.a);
			creatDecodeBitmap(R.drawable.b);
			creatDecodeBitmap(R.drawable.c);
			creatDecodeBitmap(R.drawable.d);
			creatDecodeBitmap(R.drawable.e);
			creatDecodeBitmap(R.drawable.f);
			creatDecodeBitmap(R.drawable.g);
			creatDecodeBitmap(R.drawable.h);
		}

		// 用來顯示的圖片編號TextView
		commt = (TextView) findViewById(R.id.textView1);

		// 判斷目錄是否存在
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(local_file);
			if (!file.exists())
				file.mkdirs(); // 建立照片儲存目錄

			Photofiles = file.listFiles();
			file_length = Photofiles.length;
			i1 = 1;

			for (File f : file.listFiles()) { // 是否有儲存過照片
				imgResId.add(Uri.fromFile(new File(f.getAbsolutePath()))); // 將路徑下所有image以uri方式儲存
			}
		}

		gallery = (DetialGallery) findViewById(R.id.widget1);
		metrics = new DisplayMetrics();
		ImageAdapter imageAdapter = new ImageAdapter(PuzzleActivity.this,
				imgResId, metrics);
		gallery.setAdapter(imageAdapter);
		gallery.setSelection(file_length * 200);
		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				i1 = gallery.getSelectedItemPosition() % imgResId.size() + 1;
				i2 = String.valueOf(i1);
				commt.setText(i2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mLastTime = mCurTime;
				mCurTime = (int) System.currentTimeMillis();

				if (mCurTime - mLastTime < 500) {
					LayoutInflater inflater = getLayoutInflater();
					View framelayoutcontaints = inflater.inflate(
							R.layout.scroll_layout, null);
					Scroll_layout = (LinearLayout) framelayoutcontaints
							.findViewById(R.id.scroll_ly);
					LinearLayout.LayoutParams Scroll_lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					GestureImageView iv = (GestureImageView) framelayoutcontaints
							.findViewById(R.id.iv_scroll);
					
					
					try {
						bt_scroll = MediaStore.Images.Media.getBitmap(PuzzleActivity.this.getContentResolver(), imgResId.get(position % imgResId.size()));
						iv.setImageBitmap(bt_scroll);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PuzzleActivity.this
							.addContentView(Scroll_layout, Scroll_lp);

					main_ly.setVisibility(View.INVISIBLE);
				}
			}
		});

		// 感應器事件
		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		// handler.postDelayed(mSensorOpen, 1000);

		if (this.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			Portrait(); // 直式設定
			Log.e("Portrait", "Portrait");
		} else {
			Landscape(); // 橫式設定
			Log.e("Landscape", "Landscape");
		}
	}

	// private Runnable mSensorOpen = new Runnable() {// mSensorOpen開啟用
	// public void run() {
	// sensor_close = false;
	// }
	// };

	private void creatDecodeBitmap(int drawable) {
		// TODO Auto-generated method stub
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
				drawable);
//		largeIcon = PicZoom(largeIcon, largeIcon.getWidth() / 2,
//				largeIcon.getHeight() / 2);
		try {

			File filesave = new File(local_file,
					String.valueOf(init_save_count) + ".jpg");
			FileOutputStream out = new FileOutputStream(filesave);
			// 將 Bitmap壓縮成指定格式的圖片並寫入檔案串流
			largeIcon.compress(Bitmap.CompressFormat.JPEG, 90, out);
			// 刷新並關閉檔案串流
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("e", String.valueOf(e));
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("e", String.valueOf(e));
			e.printStackTrace();
		}

		init_save_count++; // 原始圖片名稱
		largeIcon.recycle();
		System.gc();
	}

	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null); // 相簿
		intent.setType("image/*");
		return intent;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 呼叫Gallery返回
			Uri uri = data.getData();
			boolean FileExist = false;

			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				File yourDir = new File(local_file);

				for (File f : yourDir.listFiles()) { // 是否有儲存過照片
					if (f.isFile())
						if (("/" + f.getName()).equals(uri.getPath().substring(
								uri.getPath().lastIndexOf("/"))
								+ ".jpg")) {
							FileExist = true;
						}
				}

				Log.e("FileExist", String.valueOf(FileExist));
				if (!FileExist) { // 沒有儲存過就把照片除入指定的路徑中
					ContentResolver cr = this.getContentResolver();
					Bitmap photo = null;

					try {
						photo = BitmapFactory.decodeStream(
								cr.openInputStream(uri), null,
								getBitmapOptions(4)); // 圖片存檔縮小4倍
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						// 取得外部儲存裝置路徑
						// 開啟檔案
						File filesave = new File(local_file, uri.getPath()
								.substring(uri.getPath().lastIndexOf("/"))
								+ ".jpg");
						// 開啟檔案串流
						FileOutputStream out = new FileOutputStream(filesave);
						// 將 Bitmap壓縮成指定格式的圖片並寫入檔案串流
						photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
						// 刷新並關閉檔案串流
						out.flush();
						out.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					photo.recycle();
					System.gc();

					imgResId.add(Uri.fromFile(new File(Environment
							.getExternalStorageDirectory()
							+ "/Puzzle_Photo/"
							+ uri.getPath().substring(
									uri.getPath().lastIndexOf("/")) + ".jpg")));// 以路徑+檔名方式轉成uri

					ImageAdapter imageAdapter = new ImageAdapter(
							PuzzleActivity.this, imgResId, metrics);
					gallery.setAdapter(imageAdapter); // 更新Gallery

					File file = new File(local_file);
					Photofiles = file.listFiles();
					file_length = Photofiles.length;

					i1 = file_length; // 圖片總數
					i2 = String.valueOf(i1);

					gallery.setSelection(imgResId.size() * 200 - 1);
					commt.setText(i2);

				} else {
					Toast.makeText(PuzzleActivity.this, "相同照片重複加入",
							Toast.LENGTH_SHORT).show();
				}

			}

			break;
		}
		case CAMERA_WITH_DATA: {// 照相機返回
			Bitmap camorabitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory() + "/workupload.jpg");
			if (null != camorabitmap) {

				Bitmap bitMap = PicZoom(camorabitmap,
						camorabitmap.getWidth() / 8,
						camorabitmap.getHeight() / 8);

				SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
				Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
				String str = formatter.format(curDate);
				try {
					File filesave = new File(local_file, str + ".jpg");
					FileOutputStream out = new FileOutputStream(filesave);
					// 將 Bitmap壓縮成指定格式的圖片並寫入檔案串流
					bitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
					// 刷新並關閉檔案串流
					out.flush();
					out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					Log.e("e", String.valueOf(e));
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("e", String.valueOf(e));
					e.printStackTrace();
				}

				camorabitmap.recycle();
				System.gc();
				bitMap.recycle();
				System.gc();

				// 以路徑+檔名方式轉成uri
				imgResId.add(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/Puzzle_Photo/"
						+ str
						+ ".jpg")));

				ImageAdapter imageAdapter = new ImageAdapter(
						PuzzleActivity.this, imgResId, metrics);
				gallery.setAdapter(imageAdapter); // 更新Gallery

				File file = new File(local_file);
				Photofiles = file.listFiles();
				file_length = Photofiles.length;

				i1 = file_length;
				i2 = String.valueOf(i1);
				gallery.setSelection(imgResId.size() * 200 - 1);
				commt.setText(i2);
			}

			break;
		}
		}
	}

	public BitmapFactory.Options getBitmapOptions(int scale) { // 縮小圖片尺寸
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = scale;
		return options;
	}

	public static int reckonThumbnail(int oldWidth, int oldHeight,
			int newWidth, int newHeight) {
		if ((oldHeight > newHeight && oldWidth > newWidth)
				|| (oldHeight <= newHeight && oldWidth > newWidth)) {
			int be = (int) (oldWidth / (float) newWidth);
			if (be <= 1)
				be = 1;
			return be;
		} else if (oldHeight > newHeight && oldWidth <= newWidth) {
			int be = (int) (oldHeight / (float) newHeight);
			if (be <= 1)
				be = 1;
			return be;
		}
		return 1;
	}

	public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);

		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}

	private Button.OnClickListener onbtnChoose = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_3x3:
				x_count = 3;
				y_count = 3;
				PuzzleGame game3x3 = new PuzzleGame(x_count, y_count,
						v.getContext(), returnTitle);
				try {
					bmpBuffer = MediaStore.Images.Media.getBitmap(
							PuzzleActivity.this.getContentResolver(),
							imgResId.get(gallery.getSelectedItemPosition()
									% imgResId.size()));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				game3x3.game_start(bmpBuffer);

				for (int i = 0; i < imgResId.size(); i++) {
					if (imgResId.get(i) != null) {
						imgResId.set(i, null);
						System.gc();
						Log.e("imgResId gc", "imgResId gc");
					}
				}

				sence = R.layout.middle;// 記錄目前在middle
				break;
			case R.id.btn_4x4:
				x_count = 4;
				y_count = 4;
				PuzzleGame game4x4 = new PuzzleGame(x_count, y_count,
						v.getContext(), returnTitle);
				goal_image_id = R.drawable.chess;// 限定為棋盤

				bmpBuffer = BitmapFactory.decodeResource(
						PuzzleActivity.this.getResources(), goal_image_id);

				for (int i = 0; i < imgResId.size(); i++) {
					if (imgResId.get(i) != null) {
						imgResId.set(i, null);
						System.gc();
						Log.e("imgResId gc", "imgResId gc");
					}
				}

				game4x4.game_start(bmpBuffer);
				sence = R.layout.middle;// 記錄目前在middle
				break;
			case R.id.btn_5x5:
				x_count = 5;
				y_count = 5;
				PuzzleGame game5x5 = new PuzzleGame(x_count, y_count,
						v.getContext(), returnTitle);

				try {
					bmpBuffer = MediaStore.Images.Media.getBitmap(
							PuzzleActivity.this.getContentResolver(),
							imgResId.get(gallery.getSelectedItemPosition()
									% imgResId.size()));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				game5x5.game_start(bmpBuffer);

				for (int i = 0; i < imgResId.size(); i++) {
					if (imgResId.get(i) != null) {
						imgResId.set(i, null);
						System.gc();
						Log.e("imgResId gc", "imgResId gc");
					}
				}

				sence = R.layout.middle;// 記錄目前在middle
				break;
			case R.id.btn_trunbeads:
				specialMode = 1;
				x_count = 4;
				y_count = 4;
				PuzzleTurnBeads gametrunbeads = new PuzzleTurnBeads(x_count, y_count,
						v.getContext(), returnTitle);
				goal_image_id = R.drawable.chess;// 限定為棋盤

				bmpBuffer = BitmapFactory.decodeResource(
						PuzzleActivity.this.getResources(), goal_image_id);
				
				for (int i = 0; i < imgResId.size(); i++) {
					if (imgResId.get(i) != null) {
						imgResId.set(i, null);
						System.gc();
						Log.e("imgResId gc", "imgResId gc");
					}
				}

				gametrunbeads.game_start(bmpBuffer);
				sence = R.layout.middle_trunbeads;// 記錄目前在middle
				break;
			case R.id.btn_twolayer:
				specialMode = 2;
				x_count = 4;
				y_count = 4;
				PuzzleTwoLayer gametwolayer = new PuzzleTwoLayer(x_count, y_count,
						v.getContext(), returnTitle);
				goal_image_id = R.drawable.chess;// 限定為棋盤

				bmpBuffer = BitmapFactory.decodeResource(
						PuzzleActivity.this.getResources(), goal_image_id);

				bmpBuffer_red = BitmapFactory.decodeResource(
						PuzzleActivity.this.getResources(), R.drawable.chess_red);
				
				for (int i = 0; i < imgResId.size(); i++) {
					if (imgResId.get(i) != null) {
						imgResId.set(i, null);
						System.gc();
						Log.e("imgResId gc", "imgResId gc");
					}
				}

				gametwolayer.game_start(bmpBuffer, bmpBuffer_red);
				sence = R.layout.middle_twolayer;// 記錄目前在middle
				break;
			}
			// mSensorManager.unregisterListener(PuzzleActivity.this);
		}
	};

	private OnClickListener getchangeimageNEXT = new OnClickListener() {// 設定當觸碰螢幕中那個更換圖片的按鈕後會出現的反應
		public void onClick(View v) {
			gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
	};

	private OnClickListener getchangeimagePrevious = new OnClickListener() {// 設定當觸碰螢幕中那個更換圖片的按鈕後會出現的反應
		public void onClick(View v) {
			// String i2 = "";
			gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
		}
	};

	private DialogInterface.OnClickListener returnTitle = new DialogInterface.OnClickListener() {// 回主選單
		public void onClick(DialogInterface arg0, int arg1) {
			setContentView(R.layout.main);
			sence = R.layout.main;// 記錄目前在main
			init();

			if (x_count == 4  && specialMode == 1){
				if (PuzzleTurnBeads.puzzle_goal != null  //轉珠
						&& !PuzzleTurnBeads.puzzle_goal.isRecycled()) {
					PuzzleTurnBeads.puzzle_goal.recycle();
					PuzzleTurnBeads.puzzle_goal = null;
					System.gc();
					Log.e("puzzle_goal gc", "puzzle_goal gc");
				}
				
				for (int i = 0; i < PuzzleTurnBeads.Puzzles.length; i++) { // 釋放PuzzleGame裡的bitmap
				
					if (PuzzleTurnBeads.puzzles_image[i] != null
							&& !PuzzleTurnBeads.puzzles_image[i].isRecycled()) {
						PuzzleTurnBeads.puzzles_image[i].recycle();
						PuzzleTurnBeads.puzzles_image[i] = null;
						System.gc();
						Log.e("puzzles_image gc", "puzzles_image gc");
					}
				}
				
				if (PuzzleTurnBeads.bitmapBuffer != null
						&& !PuzzleTurnBeads.bitmapBuffer.isRecycled()) {
					PuzzleTurnBeads.bitmapBuffer.recycle();
					PuzzleTurnBeads.bitmapBuffer = null;
					System.gc();
					Log.e("bitmapBuffer gc", "bitmapBuffer gc");
				}
			}else if (x_count == 4  && specialMode == 2){
				if (PuzzleTwoLayer.puzzle_goal != null  //轉珠
				&& !PuzzleTwoLayer.puzzle_goal.isRecycled()) {
					PuzzleTwoLayer.puzzle_goal.recycle();
					PuzzleTwoLayer.puzzle_goal = null;
					System.gc();
					Log.e("puzzle_goal gc", "puzzle_goal gc");
				}
				
				for (int i = 0; i < PuzzleTwoLayer.Puzzles.length; i++) { // 釋放PuzzleGame裡的bitmap
				
					if (PuzzleTwoLayer.puzzles_image[i] != null
							&& !PuzzleTwoLayer.puzzles_image[i].isRecycled()) {
						PuzzleTwoLayer.puzzles_image[i].recycle();
						PuzzleTwoLayer.puzzles_image[i] = null;
						System.gc();
						Log.e("puzzles_image gc", "puzzles_image gc");
					}
				}
				
				if (PuzzleTwoLayer.bitmapBuffer != null
						&& !PuzzleTwoLayer.bitmapBuffer.isRecycled()) {
					PuzzleTwoLayer.bitmapBuffer.recycle();
					PuzzleTwoLayer.bitmapBuffer = null;
					System.gc();
					Log.e("bitmapBuffer gc", "bitmapBuffer gc");
				}
				PuzzleTwoLayer.handler.removeCallbacks(PuzzleTwoLayer.tick);
				PuzzleTwoLayer.exitGame();
				
			}else{ 
				if (PuzzleGame.puzzle_goal != null
						&& !PuzzleGame.puzzle_goal.isRecycled()) {
					PuzzleGame.puzzle_goal.recycle();
					PuzzleGame.puzzle_goal = null;
					System.gc();
					Log.e("puzzle_goal gc", "puzzle_goal gc");
				}
				
				for (int i = 0; i < PuzzleGame.Puzzles.length; i++) { // 釋放PuzzleGame裡的bitmap
				
					if (PuzzleGame.puzzles_image[i] != null
							&& !PuzzleGame.puzzles_image[i].isRecycled()) {
						PuzzleGame.puzzles_image[i].recycle();
						PuzzleGame.puzzles_image[i] = null;
						System.gc();
						Log.e("puzzles_image gc", "puzzles_image gc");
					}
				}
				
				if (PuzzleGame.bitmapBuffer != null
						&& !PuzzleGame.bitmapBuffer.isRecycled()) {
					PuzzleGame.bitmapBuffer.recycle();
					PuzzleGame.bitmapBuffer = null;
					System.gc();
					Log.e("bitmapBuffer gc", "bitmapBuffer gc");
				}
				
				PuzzleGame.handler.removeCallbacks(PuzzleGame.tick);
				PuzzleGame.exitGame();
			}



			
			screen = false;
			// mSensorManager.registerListener(PuzzleActivity.this, mSensor,
			// SensorManager.SENSOR_DELAY_NORMAL);
		}
	};

	@Override
	public void onBackPressed() {// 按下系統的回上頁鈕
		if (main_ly.getVisibility() == View.VISIBLE) {
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(
					PuzzleActivity.this);
			alertMessage.setTitle("系統訊息");
			alertMessage.setMessage("您確定要離開?");
			alertMessage.setPositiveButton("確定", exitGame);
			alertMessage.setNegativeButton("取消", null);
			alertMessage.create().show();
		} else {
			Scroll_layout.setVisibility(View.GONE);
			main_ly.setVisibility(View.VISIBLE);
			if (bt_scroll != null && !bt_scroll.isRecycled()){
				bt_scroll.recycle();
				System.gc();
				Log.e("bt_scroll gc", "bt_scroll gc");
			}
		}
	}

	private DialogInterface.OnClickListener exitGame = new DialogInterface.OnClickListener() {// 關閉遊戲
		public void onClick(DialogInterface arg0, int arg1) {
			PuzzleActivity.this.finish();
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {// 螢幕翻轉，覆寫讓他不會執行onCreate()
		super.onConfigurationChanged(newConfig);
		if (sence == R.layout.middle) {// 如果是middle翻轉，則調整option_panel的設定
			LinearLayout option_panel = (LinearLayout) findViewById(R.id.option_panel);
			FrameLayout.LayoutParams p1 = (FrameLayout.LayoutParams) option_panel
					.getLayoutParams();
			// 取得目前的螢幕翻轉方向
			int vOrientation = newConfig.orientation;
			// 依翻轉方向進行調整
			if (vOrientation == Configuration.ORIENTATION_PORTRAIT) {// 直立狀態
				option_panel.setOrientation(LinearLayout.HORIZONTAL);
				p1.height = p1.width;
				p1.width = LayoutParams.MATCH_PARENT;
				p1.gravity = Gravity.BOTTOM;
			} else {// 橫立
				option_panel.setOrientation(LinearLayout.VERTICAL);
				p1.width = p1.height;
				p1.height = LayoutParams.MATCH_PARENT;
				p1.gravity = Gravity.RIGHT;
			}
		} else if (sence == R.layout.main) {
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) // 直立狀態
				Portrait();
			else
				Landscape();// 橫立

		}
	}

	private void Landscape() {
		// TODO Auto-generated method stub
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// 設定難易度按鈕大小
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.33));
		lin_level.setLayoutParams(p);

		// 設定底部和底部的間隔
		RelativeLayout bottomLin = (RelativeLayout) findViewById(R.id.re);
		LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		l.bottomMargin = (int) (metrics.heightPixels * 0.022);
		l.gravity = Gravity.CENTER_HORIZONTAL;
		bottomLin.setGravity(Gravity.CENTER_HORIZONTAL);
		bottomLin.setLayoutParams(l);

		// 設定圖片大小和圖片與下面按鈕間隔
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(
				(int) (metrics.widthPixels * 0.4),
				(int) (metrics.heightPixels * 0.35));

		p1.gravity = Gravity.CENTER;
		p1.bottomMargin = (int) (metrics.heightPixels * 0.02);
		// image1.setLayoutParams(p1);
		// image1.setDrawingCacheEnabled(false);
	}

	private void Portrait() {
		// TODO Auto-generated method stub
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// 設定難易度按鈕大小
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.35));
		lin_level.setLayoutParams(p);

		// 設定底部和底部的間隔
		RelativeLayout bottomLin = (RelativeLayout) findViewById(R.id.re);
		LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		l.bottomMargin = (int) (metrics.heightPixels * 0.035);
		l.gravity = Gravity.CENTER_HORIZONTAL;
		bottomLin.setGravity(Gravity.CENTER_HORIZONTAL);
		bottomLin.setLayoutParams(l);

		// 設定圖片大小和圖片與下面按鈕間隔
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(
				(int) (metrics.widthPixels * 0.8),
				(int) (metrics.heightPixels * 0.4));

		p1.gravity = Gravity.CENTER;
		p1.topMargin = (int) (metrics.heightPixels * 0.04);
		p1.bottomMargin = (int) (metrics.heightPixels * 0.01);
		gallery.setLayoutParams(p1);
		// image1.setDrawingCacheEnabled(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 註冊感應器事件

		super.onResume();
		// sensor_close = true;
		// handler.postDelayed(mSensorOpen, 1000);
		// mSensorManager.registerListener(this, mSensor,
		// SensorManager.SENSOR_DELAY_NORMAL);

		File file = new File(local_file);
		Photofiles = file.listFiles();
		file_length = Photofiles.length;
		Log.e("onResume", "onResume");

	}

	@Override
	protected void onPause() {
		// mSensorManager.unregisterListener(this);
		super.onPause();
	}

	// @Override
	// public void onAccuracyChanged(Sensor arg0, int arg1) {
	// // TODO Auto-generated method stub
	//
	// }

	// @Override
	// public void onSensorChanged(SensorEvent arg0) {
	// // TODO Auto-generated method stub
	// float range = arg0.values[0];
	//
	// if (range != mSensor.getMaximumRange() && !sensor_close) {
	// Toast.makeText(this, "接近感應器", Toast.LENGTH_SHORT).show();
	// View view = this.getWindow().getDecorView();
	// view.setDrawingCacheEnabled(true);
	// view.buildDrawingCache();
	// Bitmap b1 = view.getDrawingCache();
	//
	// // image1.setImageBitmap(b1);
	// // goal_image_id = image1.getId();
	//
	// // int scale = reckonThumbnail(b1.getWidth(), b1.getHeight(),
	// // 500,
	// // 600);
	// Bitmap bitMap = PicZoom(b1, b1.getWidth() / 2, b1.getHeight() / 2);
	//
	// SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
	// Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
	// String str = formatter.format(curDate);
	//
	// try {
	// // 取得外部儲存裝置路徑
	// // 開啟檔案
	// File filesave = new File(local_file, str + ".jpg");
	// // 開啟檔案串流
	// FileOutputStream out = new FileOutputStream(filesave);
	// // 將 Bitmap壓縮成指定格式的圖片並寫入檔案串流
	// bitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
	// // 刷新並關閉檔案串流
	// out.flush();
	// out.close();
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // image1.setImageBitmap(bitMap);
	// // goal_image_id = image1.getId();
	//
	// // imgResId.add(bitMap);
	// imgResId.add(Uri.fromFile(new
	// File(Environment.getExternalStorageDirectory() + "/Puzzle_Photo/" + str +
	// ".jpg")));
	//
	// ImageAdapter imageAdapter = new ImageAdapter(PuzzleActivity.this,
	// imgResId, metrics);
	// gallery.setAdapter(imageAdapter);
	//
	// File file = new File(local_file);
	// Photofiles = file.listFiles();
	// file_length = Photofiles.length;
	//
	// i1 = file_length;
	// i2 = String.valueOf(i1);
	// commt.setText(i2);
	// gallery.setSelection(imgResId.size() * 200 - 1);
	//
	// }
	// }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		new RecycleAllView(this);
		super.onDestroy();

		ActivityManager activityManger = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManger
				.getRunningAppProcesses();
		if (list != null)
			for (int i = 0; i < list.size(); i++) {
				ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

				System.out.println("pid " + apinfo.pid);
				System.out.println("processName " + apinfo.processName);
				System.out.println("importance " + apinfo.importance);
				String[] pkgList = apinfo.pkgList;

				if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					// Process.killProcess(apinfo.pid);
					for (int j = 0; j < pkgList.length; j++) {
						// 2.2以上是過時的,請用killBackgroundProcesses代替
						activityManger.killBackgroundProcesses(pkgList[j]);
					}
				}
			}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (screen){
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				x1 = event.getX();
				y1 = event.getY();
			}
			if(event.getAction() == MotionEvent.ACTION_UP) {
				x2 = event.getX();
				y2 = event.getY();
				if(y1 - y2 > 50) {
//					Toast.makeText(PuzzleActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(1);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(1);
				} else if(y2 - y1 > 50) {
//					Toast.makeText(PuzzleActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(2);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(2);
				} else if(x1 - x2 > 50) {
//					Toast.makeText(PuzzleActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(3);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(3);
				} else if(x2 - x1 > 50) {
//					Toast.makeText(PuzzleActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(4);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(4);
				}
			}
		}
		return super.onTouchEvent(event);
	}
}
