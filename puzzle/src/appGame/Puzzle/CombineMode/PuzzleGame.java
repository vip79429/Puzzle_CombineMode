package appGame.Puzzle.CombineMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PuzzleGame {
	public static Activity activity;
	public static int x_count; // ���϶s�̤j�C��
	public static int y_count; // ���϶s�̤j���
	private int CurrentButtonNumber = 5; // ���϶s��id�s��(�i�H�N�C)
	private static int pre_x;
	private static int pre_y;
	private static int block; // �ثe�Ů檺���϶s�y�нs��
	private static int[] move_btn; // �ثe�n���ʦܪŮ檺���϶s�y�нs��,���ʤ�V

	public int goal_image_id; // ���ϥؼйϤ����s��
	public static Bitmap puzzle_goal; // ���ϥؼйϤ�(���ϭ����I��+���϶s�Ϥ��ӷ�)
	public static Bitmap[] puzzles_image; // ���ϯx�}
	public static Rect goal_split; // ���Ϩӷ����ίx�ΰ϶�
	public static PuzzleObject Puzzles[]; // ���϶s�}�C
	public static int Q_array[]; // �D�ذ}�C�A�Yrandom�᪺�}�C
	public static int A_array[]; // ���װ}�C
	private static int WASH_TIMES = 100; // �~�P�ɪ��H������
	private static DialogInterface.OnClickListener returnTitle; // �q�W�h���L�Ӫ��^�D���function

	private static int puzzle_xdp = 70; // ���϶s��dpi�A�o��]�w��
	private static int puzzle_ydp = 70; // ���϶s��dpi�A�o��]�w��
	private static float dpi; // �ѪR�סA�Y1dpi���h�ֹ���
	private static int scene_flag = 0; // ����ʵe��flag�A0���ثe�S�ʵe
	public static Handler handler; // �ʵetick��timer�X�ʪ�

	private static ImageView imgAnswer; // ���׹�
	private ImageView btnBuffer; // �x�}��

	private static ArrayList<ImageView> pics;
	private static int pics_index = 1;
	private static Button btnAnswer; // ���׫��s
	private static Button btnRestart; // ���s�����s
	private static Button btnReturnTitle; // �^�D������
	private static Button btn_show; // AI
	private static Button btn_screen; //���ù��ư�
	private static Button btn_gyro; //������
	
	private static FrameLayout puzzle_panel; // ��i����
	private static int block_count = 0; // ����random�X�ӫ�0���Ů��m
	private static int AI_count = 1; // ���װ}�C�����ʱq1�}�l�A�]��0�����װ}�C�O�_�l�I
	private static boolean AI_isrun = false; // AI�O�_���b����
	private static int[] Puzzles_moveArray = new int[9];; // �x�s�g�L��ưʫ᪺�Ϥ��}�C�s��
	private static boolean image_move = false; // �Ϥ��O�_�b�ưʤ�
	private static boolean game_end = false; // �L���P�_
	private static ImageView iv_ok[];
	public static boolean gyroEnable = false;
	
	private static SensorManager sensorManager;
	private static SensorEventListener mySensorListener;
	private static int gyroDelay = 0;
	private static float gyro_x, gyro_y, gyro_z;
	
	public PuzzleGame(int _x_count, int _y_count, Context _activity,
			DialogInterface.OnClickListener _returnTitle) {// �غc��
															// �q�~��Ū�J�Ѽ�
		x_count = _x_count;
		y_count = _y_count;
		activity = (Activity) _activity;
		returnTitle = _returnTitle;
		// ���oDPI
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.scaledDensity;
		puzzle_xdp = (int) (metrics.widthPixels * 0.75 / x_count);// ���o�ثex�̤j��dpi�A������x_count���U���϶s
		// �]�wtimer
		handler = new Handler();
		handler.removeCallbacks(tick); // �ƥ�
		handler.postDelayed(tick, 33); // ����ɶ�

		activity.setContentView(R.layout.middle);
		
	}

	public static void get_canMoveFullScreen(int move_from) {  //���ù��ư�
		// TODO Auto-generated method stub
		int[] result = new int[] { 0, 0 };
		int[] move_direction = new int[] { 0, 0 };
		if (!image_move && !AI_isrun) {
			if (move_from == 1 && block + x_count <= x_count * y_count - 1) { // �W
				result[0] = block + x_count;
				result[1] = 1;
				move_direction[1] = 1;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			} else if (move_from == 2 && block - x_count >= 0) { // �U
				result[0] = block - x_count;
				result[1] = 2;
				move_direction[1] = 2;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			} else if (move_from == 3 && (block + 1) % x_count != 0) { // ��
				result[0] = block + 1;
				result[1] = 3;
				move_direction[0] = 3;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			} else if (move_from == 4 && block % x_count != 0) { // �k
				result[0] = block - 1;
				result[1] = 4;
				move_direction[0] = 4;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			}
			puzzle_panel.removeView(iv_ok[result[0]]);
		}
	}

	public static void game_start(Bitmap _goalImage) {// �C����l��
												// ���o������middle.xml(�C���D��)
		pics_index = 1;// ���ϰ}�C�k0
		block_count = 0; //
		AI_count = 1; // AI�_�l�I
		AI_isrun = false; // AI �O�_��run
		game_end = false;
		scene_flag = 0;
		iv_ok = new ImageView[x_count * y_count];
		for(int i = 0; i < iv_ok.length; i++)
		{
			iv_ok[i] = new ImageView(activity);
			iv_ok[i].setImageResource(R.drawable.anwser_ok);
		}
		
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// ���s�w�q��i�Ϥ��j�p
		if (x_count == 4) {
			int width = _goalImage.getWidth();
			int height = _goalImage.getHeight();
			int newWidth = (int) (metrics.widthPixels * 0.7);
			int newHeight = (int) (metrics.heightPixels * 0.45);
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			puzzle_goal = Bitmap.createBitmap(_goalImage, 0, 0, width, height,
					matrix, true);
		} else {
			int width = _goalImage.getWidth();
			int height = _goalImage.getHeight();
			int newWidth = (int) (metrics.widthPixels * 0.7);
			int newHeight = (int) (metrics.heightPixels * 0.35);
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			puzzle_goal = Bitmap.createBitmap(_goalImage, 0, 0, width, height,
					matrix, true);
		}

		// �qxml���Jimageview��activity
		LayoutInflater inflater = activity.getLayoutInflater();
		View framelayoutcontaints = null;
		pics = new ArrayList<ImageView>();

		if (x_count == 3) { // 3*3 xml
			framelayoutcontaints = inflater.inflate(R.layout.middle33, null);
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer0));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer1));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer2));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer3));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer4));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer5));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer6));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer7));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer8));
		} else if (x_count == 4) { // 4*4 xml
			framelayoutcontaints = inflater.inflate(R.layout.middle44, null);
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer0));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer1));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer2));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer3));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer4));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer5));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer6));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer7));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer8));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer9));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer10));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer11));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer12));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer13));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer14));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer15));
		} else if (x_count == 5) { // 5*5 xml
			framelayoutcontaints = inflater.inflate(R.layout.middle55, null);
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer0));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer1));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer2));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer3));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer4));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer5));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer6));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer7));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer8));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer9));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer10));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer11));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer12));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer13));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer14));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer15));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer16));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer17));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer18));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer19));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer20));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer21));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer22));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer23));
			pics.add((ImageView) framelayoutcontaints
					.findViewById(R.id.btnBuffer24));
		}

		// ��l�ƹC����ưO���}�C
		// puzzle_bg.setBackgroundDrawable(new BitmapDrawable(puzzle_goal));
		goal_split = new Rect(0, 0, puzzle_goal.getWidth(),
				puzzle_goal.getHeight());// ���ίx�ΰ϶�����i��

		puzzles_image = BitmapSpliter(goal_split, x_count, y_count, puzzle_goal);
		set_QA_array(x_count, y_count);// ���oA_array��Q_array
		Puzzles = new PuzzleObject[x_count * y_count];
		// �ʺA�ͦ����϶s
		puzzle_ydp = (int) (puzzle_xdp * (goal_split.bottom - goal_split.top) / (goal_split.right - goal_split.left));// �̫��ϹϤ����(xdp*��/�e)�վ���϶s��dpi

		// �]�w��i���ϻP�W�����j
		puzzle_panel = (FrameLayout) framelayoutcontaints
				.findViewById(R.id.puzzle_panel);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, puzzle_ydp * 9);
		p.topMargin = (int) (metrics.heightPixels * 0.065);
		p.leftMargin = (int) (metrics.widthPixels * 0.082);
		p.gravity = Gravity.CENTER_HORIZONTAL;
		puzzle_panel.setLayoutParams(p);
		activity.addContentView(framelayoutcontaints, p); // �[�J��ثe��activity
		
		// �]�w���׫��Ϥj�p�P���j
		LinearLayout answer_panel = (LinearLayout) activity
				.findViewById(R.id.answer_panel);
		FrameLayout.LayoutParams a = new FrameLayout.LayoutParams(
				(int) (metrics.widthPixels * 0.8), (int) (metrics.heightPixels * 0.5));
		a.topMargin = (int) (metrics.heightPixels * 0.04);
		a.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		answer_panel.setLayoutParams(a);
		
		// ���X0�Ů檺��m
		for (int i = 0; i < Q_array.length; i++) {
			if (Q_array[i] == 0) {
				block = i;
				block_count = i;
			}
		}

		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (block_count != i * x_count + j) {// ���L�Ů檺��m��]�wPuzzles����

					// ���ͷs��button
					// btnBuffer = new ImageView(activity);
					// btnBuffer.setId(CurrentButtonNumber);
					// CurrentButtonNumber++;
					// �N�ȳ]�w�J�C����ưO���}�C
					pics.get(pics_index).setImageBitmap(
							puzzles_image[Q_array[i * x_count + j]]);
					Puzzles[i * x_count + j] = new PuzzleObject();
					Puzzles[i * x_count + j].no = Q_array[i * x_count + j];
					Puzzles[i * x_count + j].values = A_array[Q_array[i
							* x_count + j]];
					Puzzles[i * x_count + j].id = pics.get(pics_index).getId();
					Puzzles[i * x_count + j].image = puzzles_image[Q_array[i
							* x_count + j]];
					Puzzles[i * x_count + j].display_object = pics
							.get(pics_index);
					// �]�w���s�y�Фέ��ߩ󥪤W��
					FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
							puzzle_xdp, puzzle_ydp, Gravity.TOP | Gravity.LEFT);
					params1.setMargins(j * puzzle_xdp, i * puzzle_ydp, j, i);
					pics.get(pics_index).setLayoutParams(params1);
					// �]�w���s��Ĳ���ƥ�
					pics.get(pics_index).setOnTouchListener(onTouchPuzzle);
					// �N���s�[�J���ϭ��O��
//					puzzle_panel.removeView(pics.get(pics_index));
//					puzzle_panel.addView(pics.get(pics_index));
					// ��s���s���A
					pics.get(pics_index).invalidate();
					// Log.e("pics_index", String.valueOf(pics_index));
					pics_index++; // �C�ӫ��Ϫ��ثe�Ӽ�

					if (Puzzles[i * x_count + j].values == A_array[i * x_count + j]){
						iv_ok[i * x_count + j].setLayoutParams(params1);
						puzzle_panel.addView(iv_ok[i * x_count + j]);
					}
				}
			}
		}

		// ���׭����w��
		imgAnswer = (ImageView) activity.findViewById(R.id.img_answer);
		imgAnswer.setImageBitmap(puzzle_goal);
		// if (_goalImage != null && !_goalImage.isRecycled()) { //�ثe�^���|��
		// _goalImage.recycle();
		// _goalImage = null;
		// System.gc();
		// Log.e("_goalImage gc", "_goalImage gc");
		// }

		// �D�\����ƥ�w��
		// --�����s--
		btnRestart = (Button) activity.findViewById(R.id.btn_restart);
		btnRestart.setOnClickListener(onClickRestart);
		// --���׶s--
		btnAnswer = (Button) activity.findViewById(R.id.btn_answer);
		btnAnswer.setOnTouchListener(onTouchAnswer);
		// �^�D���
		btnReturnTitle = (Button) activity.findViewById(R.id.btn_returntitle);
		btnReturnTitle.setOnClickListener(onClickReturntitle);
		
		sensorManager = (SensorManager)activity.getSystemService(activity.SENSOR_SERVICE);  
		final Sensor accelerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorListener = new SensorEventListener() {  
					@Override
					public void onSensorChanged(SensorEvent event) {
						// TODO Auto-generated method stub
						Log.e("onSensorChanged", "onSensorChanged");
						if (gyroDelay == 0){
							float x = event.values[0];  
				            float y = event.values[1];  
				            float z = event.values[2];         
	
				            gyro_x = x;
				            gyro_y = y;
				            gyro_z = z;
//				            Log.e("X Y Z", String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z));
				            gyroDelay ++;
						}else if (gyroDelay == 15){
							float x = event.values[0];  
				            float y = event.values[1];  
				            float z = event.values[2];   
				            
				            int[] result = new int[] { 0, 0 };
				    		int[] move_direction = new int[] { 0, 0 };
				            if (!image_move && !AI_isrun && x - gyro_x > 4 && (block + 1) % x_count != 0){ //��
								result[0] = block + 1;
								result[1] = 3;
								move_direction[0] = 3;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }else if (!image_move && !AI_isrun && x - gyro_x < -4 && block % x_count != 0){ //�k
				            	result[0] = block - 1;
								result[1] = 4;
								move_direction[0] = 4;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }else if (!image_move && !AI_isrun && y - gyro_y < -3 && block + x_count <= x_count * y_count - 1){ //�W
								result[0] = block + x_count;
								result[1] = 1;
								move_direction[1] = 1;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }else if (!image_move && !AI_isrun && z - gyro_z < -3 && block - x_count >= 0){ //�U
								result[0] = block - x_count;
								result[1] = 2;
								move_direction[1] = 2;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }

						}else if (gyroDelay == 24){
							gyroDelay = 1;
						}else{
							gyroDelay ++;
						}
						
					}  
					
			        @Override  
			        public void onAccuracyChanged(Sensor sensor, int accuracy) {  
			        }
			}  ;
		
		
		//������
		btn_gyro = (Button) activity.findViewById(R.id.btn_gyro);
		btn_gyro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (accelerSensor != null){
					if (!gyroEnable) {
						gyroDelay = 0;
						gyroEnable = true;
						sensorManager.registerListener(mySensorListener, accelerSensor, SensorManager.SENSOR_DELAY_UI);
						Toast.makeText(activity, "�������Ҧ��}��", Toast.LENGTH_SHORT)
						.show();
					}
					else{
						gyroEnable = false;
						sensorManager.unregisterListener(mySensorListener);
						Toast.makeText(activity, "�������Ҧ�����", Toast.LENGTH_SHORT)
								.show();
					}
				}else{
					Toast.makeText(activity, "�z���]�ƨS���������\��", Toast.LENGTH_SHORT)
					.show();
				}
			}
		});
		
		// AI
		btn_show = (Button) activity.findViewById(R.id.btn_show);
		btn_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (x_count == 3 && !AI_isrun && !game_end && !image_move) { // �u���b3*3���ϩMAI�������
					AI_isrun = true;
					handler.postDelayed(tick_AI, 0);
				} else if (AI_isrun) {
					Toast.makeText(activity, "���b�۰ʫ��Ϥ�..", Toast.LENGTH_SHORT)
							.show();
				} else if (game_end) {
					Toast.makeText(activity, "�C���w�g�����o..", Toast.LENGTH_SHORT)
							.show();
				} else if (image_move){
					
				}
				else{
					Toast.makeText(activity, "�u��3*3���ѵ�", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		btn_screen = (Button) activity.findViewById(R.id.btn_screen);
		btn_screen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (PuzzleActivity.screen){
					Toast.makeText(activity, "�������ù��ư�", Toast.LENGTH_SHORT).show();
					PuzzleActivity.screen = false;
					for (int i = 0; i < pics.size(); i++){
						pics.get(i).setEnabled(true);
					}
				}else{
					Toast.makeText(activity, "�}�ҥ��ù��ư�", Toast.LENGTH_SHORT).show();
					PuzzleActivity.screen = true;
					for (int i = 0; i < pics.size(); i++){
						pics.get(i).setEnabled(false);
					}
				}
			}
		});
	}

	public static void set_QA_array(int _x_count, int _y_count) {
		// �Ȧs�ǦC�ư}�C(�Yser_array[0] = 0,ser_array[1]=1...)
		int[] ser_array = new int[_x_count * _y_count];
		for (int i = 0; i < _x_count * _y_count; i++) {
			ser_array[i] = i;
		}
		// ��l��A_array
		if (_x_count == 4 && _y_count == 4) {// �ѽL��
			A_array = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6, 6,
					6 };
			FrameLayout Puzzle_bg = (FrameLayout) activity
					.findViewById(R.id.puzzle_bg);
			Puzzle_bg.setBackgroundResource(R.drawable.wood4);
		} else {// �@��Ϥ�
			A_array = ser_array.clone();
			FrameLayout Puzzle_bg = (FrameLayout) activity
					.findViewById(R.id.puzzle_bg);
			Puzzle_bg.setBackgroundResource(R.drawable.background);
		}
		// ��l��Q_array
		if (x_count == 3) { // �u��3*3�~���ˬd�W�h
			AI ai = new AI();
			boolean check = false;
			while (!check) { // �ˬdRamdom�᪺���ϬO�_�ŦX�W�h
				Q_array = random_puzzle(ser_array);
				check = ai.AI_check(Q_array, 2000);
				Log.e("check", String.valueOf(check));
			}
			String aaa = "";
			for (int i = 0; i < Q_array.length; i++) {
				aaa = aaa + String.valueOf(Q_array[i]);
			}
			Log.e("Q_array", aaa);
		} else {
			Q_array = random_puzzle(ser_array);
		}

	}

	public static int[] random_puzzle(int[] _old) {// �~�P
		int[] result = _old.clone();// ���ƻs���װ}�C�쵲�G�}�C��
		int swap1;
		int swap2;
		int randBuffer;
		int rand_count = result.length - 1;// -1�]���̫�@�椣�~�P

		for (int i = 0; i < WASH_TIMES; i++) {
			// �H���X�n�洫���}�C�����s��
			swap1 = (int) ((Math.random() * rand_count));
			swap2 = (int) ((Math.random() * rand_count));
			if (swap1 != swap2) {// �}�C�������P�A�h�洫
				randBuffer = result[swap1];
				result[swap1] = result[swap2];
				result[swap2] = randBuffer;
			} else {// �}�C�������P�A���~�@��
				i--;
			}
		}
		return result;
	}

	/**
	 * �N�j�iBITMAP�̰ѼƤ��������p��BITMAP�A�m�J�}�C�æ^�� private Bitmap[] BitmapSpliter(Rect
	 * ���ίx�ΰ϶�,int x������,int y������ ,Bitmap �Q���Ϊ��Ϥ�)
	 */
	public static Bitmap bitmapBuffer = null;
	private static Bitmap[] BitmapSpliter(Rect split_rect, int x_split, int y_split,
			Bitmap split_source) {
		Bitmap[] result;
		
		int postion_width = split_rect.right - split_rect.left; // �p����ίx�����e
		int postion_height = split_rect.bottom - split_rect.top; // �p����ίx������
		int split_width = postion_width / x_split; // �p����ΰ϶��C�����e
		int split_height = postion_height / y_split; // �p����ΰ϶��C������

		result = new Bitmap[x_split * y_split];

		for (int i = 0; i < x_split; i++) {
			for (int j = 0; j < y_split; j++) {

				// ���X���w�϶���bitmap
				bitmapBuffer = Bitmap
						.createBitmap(split_source, split_rect.left
								+ (j * split_width), split_rect.top
								+ (i * split_height), split_width, split_height);
				// �s�J���G�}�C
				result[i * y_split + j] = bitmapBuffer;
			}
		}

		// if (bitmapBuffer != null && !bitmapBuffer.isRecycled()) { //�ثe�^���|��
		// bitmapBuffer.recycle();
		// bitmapBuffer = null;
		// }

		return result;
	}

	private static ImageView.OnTouchListener onTouchPuzzle = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// �ʵe���������ƥ�move_direction
			if (scene_flag != 0 || AI_isrun)
				return true;

			switch (event.getAction()) {// ��touch�ƥ󤣦P�@���P���Ʊ�
			case MotionEvent.ACTION_DOWN:
				// ���U�ɡA���o�ثe��x,y
				pre_x = (int) event.getX();
				pre_y = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				// ���ʮɡA�P�_���s����
				image_move = true;
				AI_isrun = false;
				ImageView btnBuffer = (ImageView) activity.findViewById(view
						.getId());
				// �P�_move����V
				int now_x = (int) event.getX();
				int now_y = (int) event.getY();
				int[] move_direction = new int[] { 0, 0 };
				if (pre_x > now_x && pre_x - now_x > puzzle_xdp / 2){// ����
					move_direction[0] = 3;
					pre_x = pre_x + puzzle_xdp / 2;
				}
				else if (pre_x < now_x && now_x - pre_x > puzzle_xdp / 2){// ���k
					move_direction[0] = 4;
					pre_x = pre_x - puzzle_xdp / 2;
				}
				if (pre_y > now_y && pre_y - now_y > puzzle_xdp / 2){// ���W
					move_direction[1] = 1;
					pre_y = pre_y + puzzle_xdp / 2;
				}
				else if (pre_y < now_y && now_y - pre_y > puzzle_xdp / 2){// ���U
					move_direction[1] = 2;
					pre_y = pre_y - puzzle_xdp / 2;
				}
				// ���o���U�����϶s�y�ФΥi���ʤ�V

				int[] canMove = get_canMove(btnBuffer.getId());
				
				// �P�_�O�_�i����
				// canMove[0]�s��Q���ʪ��Ϥ��s���AcanMove[1]�s�񲾰ʤ�V
				if ((canMove[1] == move_direction[0] || canMove[1] == move_direction[1])
						&& canMove[1] != 0) {// �p�G�ثe�����ʤ�V����i���ʤ�V�A�̫�o�P�_!=0�A�H�T�O���|�X�{���O0���G�i�J�����p
												// �N�����϶s�]�w�����ʥؼ�
					move_btn = canMove;
					scene_flag = 1;
					puzzle_panel.removeView(iv_ok[canMove[0]]);
				}
				break;
			}
			return true;
		}
	};

	private static ImageView.OnClickListener onClickRestart = new ImageView.OnClickListener() {
		@Override
		public void onClick(View view) {
			// �ʵe���������ƥ�
			if (scene_flag != 0 || AI_isrun)
				return;

			// ���U�ɡA�߰ݭ��s�ҰʹC��
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("�t�Τ��i");
			alertMessage.setMessage("�O�_�n���s�~�P�A�z�N���h�ثe�C�����i��");
			alertMessage.setPositiveButton("�T�w", playAgain);
			alertMessage.setNegativeButton("����", null);
			alertMessage.create().show();
		}
	};

	private static ImageView.OnTouchListener onTouchAnswer = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// �ʵe���������ƥ�
			if (scene_flag != 0 || AI_isrun)
				return true;

			// FrameLayout puzzle_panel;
			LinearLayout answer_panel;

			switch (event.getAction()) {// ��touch�ƥ󤣦P�@���P���Ʊ�
			case MotionEvent.ACTION_DOWN:
				// ���U�ɡA��ܵ��׵e��
				// puzzle_panel = (FrameLayout) activity
				// .findViewById(R.id.puzzle_panel);
				image_move = true;
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.GONE);// ����
				answer_panel.setVisibility(View.VISIBLE);// ���

				break;
			case MotionEvent.ACTION_UP:
				// ��}�ɡA���õ��׵e��
				// puzzle_panel = (FrameLayout) activity
				// .findViewById(R.id.puzzle_panel);
				image_move = false;
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.VISIBLE);// ���
				answer_panel.setVisibility(View.GONE);// ����
				break;
			}
			return true;
		}
	};

	private static ImageView.OnClickListener onClickReturntitle = new ImageView.OnClickListener() {
		@Override
		public void onClick(View view) {
			// �ʵe���������ƥ�
			if (scene_flag != 0 || AI_isrun)
				return;

			// �u���߰ݰT��
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("�t�Τ��i");
			alertMessage.setMessage("�O�_��^�D���?");
			alertMessage.setPositiveButton("�^�D���", returnTitle);
			alertMessage.setNegativeButton("����", null);
			alertMessage.create().show();
		}
	};

	public static int[] get_canMove(int myButton) {// ���o�ثemyButton���϶s���y�нs���M�i�H���ʪ���V
		int[] result = new int[] { 0, 0 };// [0]���ثe���϶s���y�нs�� [1]���i���ʤ�V

		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (block != i * x_count + j) {
					if (Puzzles[i * x_count + j].id == myButton) {

						result[0] = i * x_count + j; // �����Ϥ����ʪ��s��
//						Log.e("i,j", String.valueOf(i + "," + j));

						if (((i - 1) * x_count + j) == block)// �i�W
							result[1] = 1; // �������ʪ���V
						else if (((i + 1) * x_count + j) == block)// �i�U
							result[1] = 2;
						else if ((i * x_count + j - 1) == block)// �i��
							result[1] = 3;
						else if ((i * x_count + j + 1) == block)// �i�k
							result[1] = 4;
						// �j����j��
						i = 4;
						j = 4;
					}
				}
			}
		}
		return result;
	}

	// �ʵe��timer
	public static Runnable tick = new Runnable() {// TION
		public void run() {
			if (scene_flag != 0) {
				puzzle_move();
			}
			handler.postDelayed(this, 45);
		}
	};

	// AI�ʵe��timer
	private static Runnable tick_AI = new Runnable() {
		public void run() {
			if (AI_count < AI.slideto.size()) { // ���쵪�װ}�C�������ʧ�����

				// if (scene_flag == 0){
				move_btn = new int[2];
				int[] canMove = get_canMove(Puzzles[AI.slideto.get(AI_count)].id); // ���o���ʪ����Ͻs��canMove[0]�M���ʪ���VcanMove[1]
				puzzle_panel.removeView(iv_ok[canMove[0]]);
				move_btn = canMove;
				scene_flag = 1; // ����ư�
				// }
//				Log.e("AI.slideto", String.valueOf(AI.slideto.get(AI_count)));
//				Log.e("AI_count", String.valueOf(AI_count));
				AI_count++; // �ثe�w���ʦh�֪����װ}�C
			}
		}
	};

	public static void puzzle_move() {
		// �]�w�C���첾���q
		int x_speed = (int) (10 * dpi);
		int y_speed = (int) (10 * dpi);
		ImageView btn_start = Puzzles[move_btn[0]].display_object;
		// Log.e("move_btn[0]", String.valueOf(move_btn[0]));
		FrameLayout.LayoutParams p_start = (FrameLayout.LayoutParams) btn_start
				.getLayoutParams();
		// ���o�����y��
		int new_x = p_start.leftMargin; // ���ʫ᪺X(���w�]���ثe�_�l���I)
		int new_y = p_start.topMargin; // ���ʫ᪺Y(���w�]���ثe�_�l���I)
		int goal_x = block % x_count; // �ت��a��X
		int goal_y = block / y_count; // �ت��a��Y
		// ���ʳB�z
		switch (move_btn[1]) {
		case 1:// �V�W
			if ((p_start.topMargin - y_speed) <= (goal_y * puzzle_ydp))
				new_y = goal_y * puzzle_ydp;
			else
				new_y -= y_speed;
			break;
		case 2:// �V�U
			if ((p_start.topMargin + y_speed) >= goal_y * puzzle_ydp)
				new_y = goal_y * puzzle_ydp;
			else
				new_y += y_speed;
			break;
		case 3:// �V��
			if ((p_start.leftMargin - x_speed) <= goal_x * puzzle_xdp)
				new_x = goal_x * puzzle_xdp;
			else
				new_x -= x_speed;
			break;
		case 4:// �V�k
			if ((p_start.leftMargin + x_speed) >= goal_x * puzzle_xdp)
				new_x = goal_x * puzzle_xdp;
			else
				new_x += x_speed;
			break;
		}
		// �N�s�y�иm�J
		p_start.setMargins(new_x, new_y, 0, 0);
		btn_start.setLayoutParams(p_start);

		if (new_x == goal_x * puzzle_xdp && new_y == goal_y * puzzle_ydp) {// �w���ʨ�ؼ��I�A�����ʵe
			scene_flag = 0;
			Puzzles[block] = Puzzles[move_btn[0]]; // �洫�Q�����Ů檺��m
			Puzzles[move_btn[0]] = new PuzzleObject();
			block = move_btn[0]; // �洫�Q�����Ů檺��m

			// �p�G�ťճB�����W���A�h�P�_�ӭt
			if (block == 0) {
				check_win();
			}

			check_ivIsOK();
			// for (int i = 0; i < 9; i += 3) {
			// if (Puzzles != null)
			// Log.e("Puzzles[i].values",
			// String.valueOf(Puzzles[i].values + " "
			// + Puzzles[i + 1].values + " "
			// + Puzzles[i + 2].values));
			// }

			if (x_count == 3 && image_move) { // ��b3*3���ϩM�Ϥ��Q��ʷưʮɡA���s�p�⵪�װ}�C
//				String bbb = "";

				for (int i = 0; i < Puzzles.length; i++) { //�����ثe�}�C��m
					Puzzles_moveArray[i] = Puzzles[i].values;
//					bbb = bbb + String.valueOf(Puzzles_moveArray[i]);
				}
//				Log.e("�ثe�}�C", bbb);
				AI ai = new AI();
				ai.AI_check(Puzzles_moveArray , 12000);
			}
			image_move = false; // �]�w�ثe���ϨS�b����
			if (AI_isrun)  //�p�GAI���ҰʤU�@�B���Ϧ۰ʲ���
				handler.postDelayed(tick_AI, 150);
		}
		// String error_info =
		// "move_btn="+move_btn[0]+","+move_btn[1]+" ,block="+block+" ,new_xy="+new_x+","+new_y+" ,goal_xy="+goal_x*puzzle_dp+","+goal_y*puzzle_dp;
		// debug_view.setText(error_info);

	}

	private static void check_ivIsOK() {
		// TODO Auto-generated method stub
		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (Puzzles[i * x_count + j].values == A_array[i * x_count + j] && i * x_count + j != 0 ) {// ���L�Ů檺��m��]�wPuzzles����

					FrameLayout.LayoutParams iv_params = new FrameLayout.LayoutParams(
							puzzle_xdp, puzzle_ydp, Gravity.TOP | Gravity.LEFT);
					iv_params.setMargins(j * puzzle_xdp, i * puzzle_ydp, j, i);
					iv_ok[i * x_count + j].setLayoutParams(iv_params);
					puzzle_panel.removeView(iv_ok[i * x_count + j]);
					puzzle_panel.addView(iv_ok[i * x_count + j]);

				}else{
					puzzle_panel.removeView(iv_ok[i * x_count + j]);
				}
			}
		}
	}

	public static void check_win() {// �P�_�O�_�w�g�L��

		for (int i = 0; i < x_count * y_count - 1; i++) {
			if (Puzzles[i].values != A_array[i]) {// �X�{���ۦP�h�����P�_
				return;
			}
		}
		// ���̫�@��ҬۦP�A�h���\�L��
		game_end();
	}

	public static void game_end() {// �L���B�z
							// �u���L���T��
		AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
		alertMessage.setTitle("�t�Τ��i");
		alertMessage.setMessage("���߱z���\�F��!!!");
		alertMessage.setPositiveButton("�A���@��", playAgain);
		alertMessage.setNeutralButton("�^�D���", returnTitle);
		alertMessage.setNegativeButton("�����F", exitGame);
		alertMessage.create().show();
		AI_isrun = false;
		game_end = true;
		handler.removeCallbacks(tick_AI);
	}

	private static DialogInterface.OnClickListener playAgain = new DialogInterface.OnClickListener() {// �A���@��
		public void onClick(DialogInterface arg0, int arg1) {
			if (scene_flag != 0 || AI_isrun)
				return;
			handler.removeCallbacks(tick);
			scene_flag = 0;
			new PuzzleGame(x_count, y_count, activity, returnTitle);
			game_start(puzzle_goal);
		}
	};

	private static DialogInterface.OnClickListener exitGame = new DialogInterface.OnClickListener() {// �����F
		public void onClick(DialogInterface arg0, int arg1) {
			activity.finish();
		}
	};
	
	public static void exitGame(){
		sensorManager.unregisterListener(mySensorListener);
		gyroEnable = false;
		Log.e("unregisterListener", "unregisterListener");
	}

}
