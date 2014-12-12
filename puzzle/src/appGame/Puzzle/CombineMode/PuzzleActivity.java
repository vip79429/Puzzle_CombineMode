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

	private int x_count = 4; // ���϶s�̤j�C��
	private int y_count = 4; // ���϶s�̤j���
	private int sence; // �O���Ҧb��XML

	private int goal_image_id = R.drawable.a; // ��l�Ϥ��Ѽ�
	private int i1 = 1;
	private String i2 = "";
	private int file_length = 0;
	private Bitmap bmpBuffer = null;
	private Bitmap bmpBuffer_red = null;
	
	// �I�s�۾�
	private static final int CAMERA_WITH_DATA = 3023;
	// �I�s��ï
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	// �Ӥ��ؿ�
	private static final String local_file = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/Puzzle_Photo/";
	// �Ӥ��ɮ�
	private File[] Photofiles;

	private DetialGallery gallery;
	private TextView commt;

	// �P����
	// private SensorManager mSensorManager;
	// private Sensor mSensor;

	// ����ɽվ�textview
	private LinearLayout lin_level;

	private ArrayList<Uri> imgResId;
	private DisplayMetrics metrics;

	private int init_save_count = 1;
	private int mCurTime = (int) System.currentTimeMillis() - 3000000,
			mLastTime;
	private LinearLayout main_ly;
	private LinearLayout Scroll_layout;
	private float x1,x2,y1,y2; //�ưʤ�V
	// private boolean sensor_close = true;
	// private Handler handler = new Handler();; // sensor�X�ʪ�
	public static boolean screen = false; // ���ù���էP�_
	private Bitmap bt_scroll = null;
	
	private int specialMode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sence = R.layout.main;// �O���ثe�bmain
		init();
	}

	public void init() {// ��l��
		// sensor_close = true;
		init_save_count = 1;
		
		Button btnEasy;
		Button btnMiddle;
		Button btnHard;
		Button btnTurnBeads;
		Button btnTwoLayer;
		// ���o���s����
		btnEasy = (Button) findViewById(R.id.btn_3x3);
		btnMiddle = (Button) findViewById(R.id.btn_4x4);
		btnHard = (Button) findViewById(R.id.btn_5x5);
		btnTurnBeads = (Button) findViewById(R.id.btn_trunbeads);
		btnTwoLayer = (Button) findViewById(R.id.btn_twolayer);
		// �]�wClick�ƥ�
		btnEasy.setOnClickListener(onbtnChoose);
		btnMiddle.setOnClickListener(onbtnChoose);
		btnHard.setOnClickListener(onbtnChoose);
		btnTurnBeads.setOnClickListener(onbtnChoose);
		btnTwoLayer.setOnClickListener(onbtnChoose);
		
		lin_level = (LinearLayout) findViewById(R.id.lin_level);

		main_ly = (LinearLayout) findViewById(R.id.main_ly);
		
		// ����Ϥ���Button
		Button btn_next = (Button) findViewById(R.id.NEXT_BUTTON);// �]�wĲ�I�󴫹Ϥ����s������
		btn_next.setOnClickListener(getchangeimageNEXT);

		Button btn_pre = (Button) findViewById(R.id.Previous_BUTTON);// �]�wĲ�I�󴫹Ϥ����s������
		btn_pre.setOnClickListener(getchangeimagePrevious);

		Button btn_bro = (Button) findViewById(R.id.Browse_BUTTON);// �]�w�~�����J�Ϥ����s������
		btn_bro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					// Launch picker to choose photo for selected contact
					final Intent intent = getPhotoPickIntent();
					startActivityForResult(intent, PHOTO_PICKED_WITH_DATA); // ����ܬ�ï
				} catch (ActivityNotFoundException e) {

				}
			}
		});

		Button btn_cam = (Button) findViewById(R.id.Camera_BUTTON);// �]�w��ӫ��s������
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
					startActivityForResult(intent, CAMERA_WITH_DATA); // ����ܩ��
				} catch (ActivityNotFoundException e) {

				}
			}
		});
		imgResId = new ArrayList<Uri>();

		// �P�_�ؿ��O�_�s�b
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(local_file);
			if (!file.exists())
				file.mkdirs(); // �إ߷Ӥ��x�s�ؿ�

			// �N��l�Ϥ��ƻs����w���|
			creatDecodeBitmap(R.drawable.a);
			creatDecodeBitmap(R.drawable.b);
			creatDecodeBitmap(R.drawable.c);
			creatDecodeBitmap(R.drawable.d);
			creatDecodeBitmap(R.drawable.e);
			creatDecodeBitmap(R.drawable.f);
			creatDecodeBitmap(R.drawable.g);
			creatDecodeBitmap(R.drawable.h);
		}

		// �Ψ���ܪ��Ϥ��s��TextView
		commt = (TextView) findViewById(R.id.textView1);

		// �P�_�ؿ��O�_�s�b
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(local_file);
			if (!file.exists())
				file.mkdirs(); // �إ߷Ӥ��x�s�ؿ�

			Photofiles = file.listFiles();
			file_length = Photofiles.length;
			i1 = 1;

			for (File f : file.listFiles()) { // �O�_���x�s�L�Ӥ�
				imgResId.add(Uri.fromFile(new File(f.getAbsolutePath()))); // �N���|�U�Ҧ�image�Huri�覡�x�s
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

		// �P�����ƥ�
		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		// handler.postDelayed(mSensorOpen, 1000);

		if (this.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			Portrait(); // �����]�w
			Log.e("Portrait", "Portrait");
		} else {
			Landscape(); // ��]�w
			Log.e("Landscape", "Landscape");
		}
	}

	// private Runnable mSensorOpen = new Runnable() {// mSensorOpen�}�ҥ�
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
			// �N Bitmap���Y�����w�榡���Ϥ��üg�J�ɮצ�y
			largeIcon.compress(Bitmap.CompressFormat.JPEG, 90, out);
			// ��s�������ɮצ�y
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

		init_save_count++; // ��l�Ϥ��W��
		largeIcon.recycle();
		System.gc();
	}

	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null); // ��ï
		intent.setType("image/*");
		return intent;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// �I�sGallery��^
			Uri uri = data.getData();
			boolean FileExist = false;

			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				File yourDir = new File(local_file);

				for (File f : yourDir.listFiles()) { // �O�_���x�s�L�Ӥ�
					if (f.isFile())
						if (("/" + f.getName()).equals(uri.getPath().substring(
								uri.getPath().lastIndexOf("/"))
								+ ".jpg")) {
							FileExist = true;
						}
				}

				Log.e("FileExist", String.valueOf(FileExist));
				if (!FileExist) { // �S���x�s�L�N��Ӥ����J���w�����|��
					ContentResolver cr = this.getContentResolver();
					Bitmap photo = null;

					try {
						photo = BitmapFactory.decodeStream(
								cr.openInputStream(uri), null,
								getBitmapOptions(4)); // �Ϥ��s���Y�p4��
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						// ���o�~���x�s�˸m���|
						// �}���ɮ�
						File filesave = new File(local_file, uri.getPath()
								.substring(uri.getPath().lastIndexOf("/"))
								+ ".jpg");
						// �}���ɮצ�y
						FileOutputStream out = new FileOutputStream(filesave);
						// �N Bitmap���Y�����w�榡���Ϥ��üg�J�ɮצ�y
						photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
						// ��s�������ɮצ�y
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
									uri.getPath().lastIndexOf("/")) + ".jpg")));// �H���|+�ɦW�覡�নuri

					ImageAdapter imageAdapter = new ImageAdapter(
							PuzzleActivity.this, imgResId, metrics);
					gallery.setAdapter(imageAdapter); // ��sGallery

					File file = new File(local_file);
					Photofiles = file.listFiles();
					file_length = Photofiles.length;

					i1 = file_length; // �Ϥ��`��
					i2 = String.valueOf(i1);

					gallery.setSelection(imgResId.size() * 200 - 1);
					commt.setText(i2);

				} else {
					Toast.makeText(PuzzleActivity.this, "�ۦP�Ӥ����ƥ[�J",
							Toast.LENGTH_SHORT).show();
				}

			}

			break;
		}
		case CAMERA_WITH_DATA: {// �Ӭ۾���^
			Bitmap camorabitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory() + "/workupload.jpg");
			if (null != camorabitmap) {

				Bitmap bitMap = PicZoom(camorabitmap,
						camorabitmap.getWidth() / 8,
						camorabitmap.getHeight() / 8);

				SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
				Date curDate = new Date(System.currentTimeMillis()); // �����e�ɶ�
				String str = formatter.format(curDate);
				try {
					File filesave = new File(local_file, str + ".jpg");
					FileOutputStream out = new FileOutputStream(filesave);
					// �N Bitmap���Y�����w�榡���Ϥ��üg�J�ɮצ�y
					bitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
					// ��s�������ɮצ�y
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

				// �H���|+�ɦW�覡�নuri
				imgResId.add(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/Puzzle_Photo/"
						+ str
						+ ".jpg")));

				ImageAdapter imageAdapter = new ImageAdapter(
						PuzzleActivity.this, imgResId, metrics);
				gallery.setAdapter(imageAdapter); // ��sGallery

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

	public BitmapFactory.Options getBitmapOptions(int scale) { // �Y�p�Ϥ��ؤo
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

				sence = R.layout.middle;// �O���ثe�bmiddle
				break;
			case R.id.btn_4x4:
				x_count = 4;
				y_count = 4;
				PuzzleGame game4x4 = new PuzzleGame(x_count, y_count,
						v.getContext(), returnTitle);
				goal_image_id = R.drawable.chess;// ���w���ѽL

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
				sence = R.layout.middle;// �O���ثe�bmiddle
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

				sence = R.layout.middle;// �O���ثe�bmiddle
				break;
			case R.id.btn_trunbeads:
				specialMode = 1;
				x_count = 4;
				y_count = 4;
				PuzzleTurnBeads gametrunbeads = new PuzzleTurnBeads(x_count, y_count,
						v.getContext(), returnTitle);
				goal_image_id = R.drawable.chess;// ���w���ѽL

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
				sence = R.layout.middle_trunbeads;// �O���ثe�bmiddle
				break;
			case R.id.btn_twolayer:
				specialMode = 2;
				x_count = 4;
				y_count = 4;
				PuzzleTwoLayer gametwolayer = new PuzzleTwoLayer(x_count, y_count,
						v.getContext(), returnTitle);
				goal_image_id = R.drawable.chess;// ���w���ѽL

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
				sence = R.layout.middle_twolayer;// �O���ثe�bmiddle
				break;
			}
			// mSensorManager.unregisterListener(PuzzleActivity.this);
		}
	};

	private OnClickListener getchangeimageNEXT = new OnClickListener() {// �]�w��Ĳ�I�ù������ӧ󴫹Ϥ������s��|�X�{������
		public void onClick(View v) {
			gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
	};

	private OnClickListener getchangeimagePrevious = new OnClickListener() {// �]�w��Ĳ�I�ù������ӧ󴫹Ϥ������s��|�X�{������
		public void onClick(View v) {
			// String i2 = "";
			gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
		}
	};

	private DialogInterface.OnClickListener returnTitle = new DialogInterface.OnClickListener() {// �^�D���
		public void onClick(DialogInterface arg0, int arg1) {
			setContentView(R.layout.main);
			sence = R.layout.main;// �O���ثe�bmain
			init();

			if (x_count == 4  && specialMode == 1){
				if (PuzzleTurnBeads.puzzle_goal != null  //��]
						&& !PuzzleTurnBeads.puzzle_goal.isRecycled()) {
					PuzzleTurnBeads.puzzle_goal.recycle();
					PuzzleTurnBeads.puzzle_goal = null;
					System.gc();
					Log.e("puzzle_goal gc", "puzzle_goal gc");
				}
				
				for (int i = 0; i < PuzzleTurnBeads.Puzzles.length; i++) { // ����PuzzleGame�̪�bitmap
				
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
				if (PuzzleTwoLayer.puzzle_goal != null  //��]
				&& !PuzzleTwoLayer.puzzle_goal.isRecycled()) {
					PuzzleTwoLayer.puzzle_goal.recycle();
					PuzzleTwoLayer.puzzle_goal = null;
					System.gc();
					Log.e("puzzle_goal gc", "puzzle_goal gc");
				}
				
				for (int i = 0; i < PuzzleTwoLayer.Puzzles.length; i++) { // ����PuzzleGame�̪�bitmap
				
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
				
				for (int i = 0; i < PuzzleGame.Puzzles.length; i++) { // ����PuzzleGame�̪�bitmap
				
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
	public void onBackPressed() {// ���U�t�Ϊ��^�W���s
		if (main_ly.getVisibility() == View.VISIBLE) {
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(
					PuzzleActivity.this);
			alertMessage.setTitle("�t�ΰT��");
			alertMessage.setMessage("�z�T�w�n���}?");
			alertMessage.setPositiveButton("�T�w", exitGame);
			alertMessage.setNegativeButton("����", null);
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

	private DialogInterface.OnClickListener exitGame = new DialogInterface.OnClickListener() {// �����C��
		public void onClick(DialogInterface arg0, int arg1) {
			PuzzleActivity.this.finish();
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {// �ù�½��A�мg���L���|����onCreate()
		super.onConfigurationChanged(newConfig);
		if (sence == R.layout.middle) {// �p�G�Omiddle½��A�h�վ�option_panel���]�w
			LinearLayout option_panel = (LinearLayout) findViewById(R.id.option_panel);
			FrameLayout.LayoutParams p1 = (FrameLayout.LayoutParams) option_panel
					.getLayoutParams();
			// ���o�ثe���ù�½���V
			int vOrientation = newConfig.orientation;
			// ��½���V�i��վ�
			if (vOrientation == Configuration.ORIENTATION_PORTRAIT) {// ���ߪ��A
				option_panel.setOrientation(LinearLayout.HORIZONTAL);
				p1.height = p1.width;
				p1.width = LayoutParams.MATCH_PARENT;
				p1.gravity = Gravity.BOTTOM;
			} else {// ���
				option_panel.setOrientation(LinearLayout.VERTICAL);
				p1.width = p1.height;
				p1.height = LayoutParams.MATCH_PARENT;
				p1.gravity = Gravity.RIGHT;
			}
		} else if (sence == R.layout.main) {
			if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) // ���ߪ��A
				Portrait();
			else
				Landscape();// ���

		}
	}

	private void Landscape() {
		// TODO Auto-generated method stub
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// �]�w�����׫��s�j�p
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.33));
		lin_level.setLayoutParams(p);

		// �]�w�����M���������j
		RelativeLayout bottomLin = (RelativeLayout) findViewById(R.id.re);
		LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		l.bottomMargin = (int) (metrics.heightPixels * 0.022);
		l.gravity = Gravity.CENTER_HORIZONTAL;
		bottomLin.setGravity(Gravity.CENTER_HORIZONTAL);
		bottomLin.setLayoutParams(l);

		// �]�w�Ϥ��j�p�M�Ϥ��P�U�����s���j
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
		// �]�w�����׫��s�j�p
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.35));
		lin_level.setLayoutParams(p);

		// �]�w�����M���������j
		RelativeLayout bottomLin = (RelativeLayout) findViewById(R.id.re);
		LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		l.bottomMargin = (int) (metrics.heightPixels * 0.035);
		l.gravity = Gravity.CENTER_HORIZONTAL;
		bottomLin.setGravity(Gravity.CENTER_HORIZONTAL);
		bottomLin.setLayoutParams(l);

		// �]�w�Ϥ��j�p�M�Ϥ��P�U�����s���j
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
		// ���U�P�����ƥ�

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
	// Toast.makeText(this, "����P����", Toast.LENGTH_SHORT).show();
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
	// Date curDate = new Date(System.currentTimeMillis()); // �����e�ɶ�
	// String str = formatter.format(curDate);
	//
	// try {
	// // ���o�~���x�s�˸m���|
	// // �}���ɮ�
	// File filesave = new File(local_file, str + ".jpg");
	// // �}���ɮצ�y
	// FileOutputStream out = new FileOutputStream(filesave);
	// // �N Bitmap���Y�����w�榡���Ϥ��üg�J�ɮצ�y
	// bitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
	// // ��s�������ɮצ�y
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
						// 2.2�H�W�O�L�ɪ�,�Х�killBackgroundProcesses�N��
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
//					Toast.makeText(PuzzleActivity.this, "�V�W��", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(1);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(1);
				} else if(y2 - y1 > 50) {
//					Toast.makeText(PuzzleActivity.this, "�V�U��", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(2);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(2);
				} else if(x1 - x2 > 50) {
//					Toast.makeText(PuzzleActivity.this, "�V����", Toast.LENGTH_SHORT).show();
					if (x_count == 4 && specialMode == 2)
						PuzzleTwoLayer.get_canMoveFullScreen(3);
					else if (x_count != 4 || specialMode != 1)
						PuzzleGame.get_canMoveFullScreen(3);
				} else if(x2 - x1 > 50) {
//					Toast.makeText(PuzzleActivity.this, "�V�k��", Toast.LENGTH_SHORT).show();
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
