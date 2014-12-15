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
	public static int x_count; // 拼圖鈕最大列數
	public static int y_count; // 拼圖鈕最大欄數
	private int CurrentButtonNumber = 5; // 拼圖鈕的id編號(可隨意列)
	private static int pre_x;
	private static int pre_y;
	private static int block; // 目前空格的拼圖鈕座標編號
	private static int[] move_btn; // 目前要移動至空格的拼圖鈕座標編號,移動方向

	public int goal_image_id; // 拼圖目標圖片的編號
	public static Bitmap puzzle_goal; // 拼圖目標圖片(拼圖面版背景+拼圖鈕圖片來源)
	public static Bitmap[] puzzles_image; // 拼圖矩陣
	public static Rect goal_split; // 拼圖來源切割矩形區塊
	public static PuzzleObject Puzzles[]; // 拼圖鈕陣列
	public static int Q_array[]; // 題目陣列，即random後的陣列
	public static int A_array[]; // 答案陣列
	private static int WASH_TIMES = 100; // 洗牌時的隨機次數
	private static DialogInterface.OnClickListener returnTitle; // 從上層接過來的回主選單function

	private static int puzzle_xdp = 70; // 拼圖鈕的dpi，這邊設定為
	private static int puzzle_ydp = 70; // 拼圖鈕的dpi，這邊設定為
	private static float dpi; // 解析度，即1dpi為多少像素
	private static int scene_flag = 0; // 播放動畫的flag，0為目前沒動畫
	public static Handler handler; // 動畫tick的timer驅動者

	private static ImageView imgAnswer; // 答案圖
	private ImageView btnBuffer; // 矩陣圖

	private static ArrayList<ImageView> pics;
	private static int pics_index = 1;
	private static Button btnAnswer; // 答案按鈕
	private static Button btnRestart; // 重新玩按鈕
	private static Button btnReturnTitle; // 回主選單按紐
	private static Button btn_show; // AI
	private static Button btn_screen; //全螢幕滑動
	private static Button btn_gyro; //陀螺儀
	
	private static FrameLayout puzzle_panel; // 整張拼圖
	private static int block_count = 0; // 紀錄random出來後0的空格位置
	private static int AI_count = 1; // 答案陣列的移動從1開始，因為0的答案陣列是起始點
	private static boolean AI_isrun = false; // AI是否正在執行
	private static int[] Puzzles_moveArray = new int[9];; // 儲存經過手滑動後的圖片陣列編號
	private static boolean image_move = false; // 圖片是否在滑動中
	private static boolean game_end = false; // 過關判斷
	private static ImageView iv_ok[];
	public static boolean gyroEnable = false;
	
	private static SensorManager sensorManager;
	private static SensorEventListener mySensorListener;
	private static Sensor accelerSensor;
	private static int gyroDelay = 0;
	private static float gyro_x, gyro_y, gyro_z;
	
	public PuzzleGame(int _x_count, int _y_count, Context _activity,
			DialogInterface.OnClickListener _returnTitle) {// 建構式
															// 從外部讀入參數
		x_count = _x_count;
		y_count = _y_count;
		activity = (Activity) _activity;
		returnTitle = _returnTitle;
		// 取得DPI
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.scaledDensity;
		puzzle_xdp = (int) (metrics.widthPixels * 0.75 / x_count);// 取得目前x最大的dpi，均分給x_count的各拼圖鈕
		// 設定timer
		handler = new Handler();
		handler.removeCallbacks(tick); // 事件
		handler.postDelayed(tick, 33); // 延遲時間

		activity.setContentView(R.layout.middle);
		
	}

	public static void get_canMoveFullScreen(int move_from) {  //全螢幕滑動
		// TODO Auto-generated method stub
		int[] result = new int[] { 0, 0 };
		int[] move_direction = new int[] { 0, 0 };
		if (!image_move && !AI_isrun) {
			if (move_from == 1 && block + x_count <= x_count * y_count - 1) { // 上
				result[0] = block + x_count;
				result[1] = 1;
				move_direction[1] = 1;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			} else if (move_from == 2 && block - x_count >= 0) { // 下
				result[0] = block - x_count;
				result[1] = 2;
				move_direction[1] = 2;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			} else if (move_from == 3 && (block + 1) % x_count != 0) { // 左
				result[0] = block + 1;
				result[1] = 3;
				move_direction[0] = 3;
				move_btn = result;
				scene_flag = 1;
				image_move = true;
			} else if (move_from == 4 && block % x_count != 0) { // 右
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

	public static void game_start(Bitmap _goalImage) {// 遊戲初始化
												// 取得切換到middle.xml(遊戲主頁)
		pics_index = 1;// 拼圖陣列歸0
		block_count = 0; //
		AI_count = 1; // AI起始點
		AI_isrun = false; // AI 是否有run
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

		// 重新定義整張圖片大小
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

		// 從xml載入imageview至activity
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

		// 初始化遊戲資料記錄陣列
		// puzzle_bg.setBackgroundDrawable(new BitmapDrawable(puzzle_goal));
		goal_split = new Rect(0, 0, puzzle_goal.getWidth(),
				puzzle_goal.getHeight());// 切割矩形區塊為整張圖

		puzzles_image = BitmapSpliter(goal_split, x_count, y_count, puzzle_goal);
		set_QA_array(x_count, y_count);// 取得A_array跟Q_array
		Puzzles = new PuzzleObject[x_count * y_count];
		// 動態生成拼圖鈕
		puzzle_ydp = (int) (puzzle_xdp * (goal_split.bottom - goal_split.top) / (goal_split.right - goal_split.left));// 依拼圖圖片比例(xdp*高/寬)調整拼圖鈕的dpi

		// 設定整張拼圖與上面間隔
		puzzle_panel = (FrameLayout) framelayoutcontaints
				.findViewById(R.id.puzzle_panel);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, puzzle_ydp * 9);
		p.topMargin = (int) (metrics.heightPixels * 0.065);
		p.leftMargin = (int) (metrics.widthPixels * 0.082);
		p.gravity = Gravity.CENTER_HORIZONTAL;
		puzzle_panel.setLayoutParams(p);
		activity.addContentView(framelayoutcontaints, p); // 加入到目前的activity
		
		// 設定答案拼圖大小與間隔
		LinearLayout answer_panel = (LinearLayout) activity
				.findViewById(R.id.answer_panel);
		FrameLayout.LayoutParams a = new FrameLayout.LayoutParams(
				(int) (metrics.widthPixels * 0.8), (int) (metrics.heightPixels * 0.5));
		a.topMargin = (int) (metrics.heightPixels * 0.04);
		a.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		answer_panel.setLayoutParams(a);
		
		// 取出0空格的位置
		for (int i = 0; i < Q_array.length; i++) {
			if (Q_array[i] == 0) {
				block = i;
				block_count = i;
			}
		}

		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (block_count != i * x_count + j) {// 跳過空格的位置後設定Puzzles拼圖

					// 產生新的button
					// btnBuffer = new ImageView(activity);
					// btnBuffer.setId(CurrentButtonNumber);
					// CurrentButtonNumber++;
					// 將值設定入遊戲資料記錄陣列
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
					// 設定按鈕座標及重心於左上角
					FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
							puzzle_xdp, puzzle_ydp, Gravity.TOP | Gravity.LEFT);
					params1.setMargins(j * puzzle_xdp, i * puzzle_ydp, j, i);
					pics.get(pics_index).setLayoutParams(params1);
					// 設定按鈕的觸控事件
					pics.get(pics_index).setOnTouchListener(onTouchPuzzle);
					// 將按鈕加入拼圖面板中
//					puzzle_panel.removeView(pics.get(pics_index));
//					puzzle_panel.addView(pics.get(pics_index));
					// 刷新按鈕狀態
					pics.get(pics_index).invalidate();
					// Log.e("pics_index", String.valueOf(pics_index));
					pics_index++; // 每個拼圖的目前個數

					if (Puzzles[i * x_count + j].values == A_array[i * x_count + j]){
						iv_ok[i * x_count + j].setLayoutParams(params1);
						puzzle_panel.addView(iv_ok[i * x_count + j]);
					}
				}
			}
		}

		// 答案面版安裝
		imgAnswer = (ImageView) activity.findViewById(R.id.img_answer);
		imgAnswer.setImageBitmap(puzzle_goal);
		// if (_goalImage != null && !_goalImage.isRecycled()) { //目前回收會掛
		// _goalImage.recycle();
		// _goalImage = null;
		// System.gc();
		// Log.e("_goalImage gc", "_goalImage gc");
		// }

		// 主功能選單事件安裝
		// --重玩鈕--
		btnRestart = (Button) activity.findViewById(R.id.btn_restart);
		btnRestart.setOnClickListener(onClickRestart);
		// --答案鈕--
		btnAnswer = (Button) activity.findViewById(R.id.btn_answer);
		btnAnswer.setOnTouchListener(onTouchAnswer);
		// 回主選單
		btnReturnTitle = (Button) activity.findViewById(R.id.btn_returntitle);
		btnReturnTitle.setOnClickListener(onClickReturntitle);
		
		sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);  
		accelerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorListener = new SensorEventListener() {  
					@Override
					public void onSensorChanged(SensorEvent event) {
						// TODO Auto-generated method stub
//						Log.e("onSensorChanged", "onSensorChanged");
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
//							Log.e("image_move", String.valueOf(image_move));
							float x = event.values[0];  
				            float y = event.values[1];  
				            float z = event.values[2];   
				            
				            int[] result = new int[] { 0, 0 };
				    		int[] move_direction = new int[] { 0, 0 };
				            if (x > gyro_x && !image_move && !AI_isrun && Math.abs(x - gyro_x) > 3 && (block + 1) % x_count != 0){ //左
								result[0] = block + 1;
								result[1] = 3;
								move_direction[0] = 3;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }else if (x < gyro_x && !image_move && !AI_isrun && Math.abs(x - gyro_x) > 3 && block % x_count != 0){ //右
				            	result[0] = block - 1;
								result[1] = 4;
								move_direction[0] = 4;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }else if (y < gyro_y && !image_move && !AI_isrun &&  Math.abs(y - gyro_y) > 2.5 && block + x_count <= x_count * y_count - 1){ //上
								result[0] = block + x_count;
								result[1] = 1;
								move_direction[1] = 1;
								move_btn = result;
								scene_flag = 1;
								image_move = true;
								gyroDelay ++;
								puzzle_panel.removeView(iv_ok[result[0]]);
				            }else if (y > gyro_y && !image_move && !AI_isrun && Math.abs(y - gyro_y) > 2.5 && block - x_count >= 0){ //下
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
		
		
		//陀螺儀
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
						Toast.makeText(activity, "陀螺儀模式開啟", Toast.LENGTH_SHORT)
						.show();
					}
					else{
						gyroEnable = false;
						sensorManager.unregisterListener(mySensorListener);
						Toast.makeText(activity, "陀螺儀模式關閉", Toast.LENGTH_SHORT)
								.show();
					}
				}else{
					Toast.makeText(activity, "您的設備沒有陀螺儀功能", Toast.LENGTH_SHORT)
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
				if (x_count == 3 && !AI_isrun && !game_end && !image_move) { // 只有在3*3拼圖和AI未執行時
					AI_isrun = true;
					handler.postDelayed(tick_AI, 0);
				} else if (AI_isrun) {
					Toast.makeText(activity, "正在自動拼圖中..", Toast.LENGTH_SHORT)
							.show();
				} else if (game_end) {
					Toast.makeText(activity, "遊戲已經結束囉..", Toast.LENGTH_SHORT)
							.show();
				} else if (image_move){
					
				}
				else{
					Toast.makeText(activity, "只有3*3有解答", Toast.LENGTH_SHORT)
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
					Toast.makeText(activity, "關閉全螢幕滑動", Toast.LENGTH_SHORT).show();
					PuzzleActivity.screen = false;
					for (int i = 0; i < pics.size(); i++){
						pics.get(i).setEnabled(true);
					}
				}else{
					Toast.makeText(activity, "開啟全螢幕滑動", Toast.LENGTH_SHORT).show();
					PuzzleActivity.screen = true;
					for (int i = 0; i < pics.size(); i++){
						pics.get(i).setEnabled(false);
					}
				}
			}
		});
	}

	public static void set_QA_array(int _x_count, int _y_count) {
		// 暫存序列化陣列(即ser_array[0] = 0,ser_array[1]=1...)
		int[] ser_array = new int[_x_count * _y_count];
		for (int i = 0; i < _x_count * _y_count; i++) {
			ser_array[i] = i;
		}
		// 初始化A_array
		if (_x_count == 4 && _y_count == 4) {// 棋盤版
			A_array = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6, 6,
					6 };
			FrameLayout Puzzle_bg = (FrameLayout) activity
					.findViewById(R.id.puzzle_bg);
			Puzzle_bg.setBackgroundResource(R.drawable.wood4);
		} else {// 一般圖片
			A_array = ser_array.clone();
			FrameLayout Puzzle_bg = (FrameLayout) activity
					.findViewById(R.id.puzzle_bg);
			Puzzle_bg.setBackgroundResource(R.drawable.background);
		}
		// 初始化Q_array
		if (x_count == 3) { // 只有3*3才有檢查規則
			AI ai = new AI();
			boolean check = false;
			while (!check) { // 檢查Ramdom後的拼圖是否符合規則
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

	public static int[] random_puzzle(int[] _old) {// 洗牌
		int[] result = _old.clone();// 先複製答案陣列到結果陣列中
		int swap1;
		int swap2;
		int randBuffer;
		int rand_count = result.length - 1;// -1因為最後一格不洗牌

		for (int i = 0; i < WASH_TIMES; i++) {
			// 隨機出要交換的陣列元素編號
			swap1 = (int) ((Math.random() * rand_count));
			swap2 = (int) ((Math.random() * rand_count));
			if (swap1 != swap2) {// 陣列元素不同，則交換
				randBuffer = result[swap1];
				result[swap1] = result[swap2];
				result[swap2] = randBuffer;
			} else {// 陣列元素不同，重洗一次
				i--;
			}
		}
		return result;
	}

	/**
	 * 將大張BITMAP依參數切成等份小塊BITMAP，置入陣列並回傳 private Bitmap[] BitmapSpliter(Rect
	 * 切割矩形區塊,int x的份數,int y的份數 ,Bitmap 被切割的圖片)
	 */
	public static Bitmap bitmapBuffer = null;
	private static Bitmap[] BitmapSpliter(Rect split_rect, int x_split, int y_split,
			Bitmap split_source) {
		Bitmap[] result;
		
		int postion_width = split_rect.right - split_rect.left; // 計算切割矩型的寬
		int postion_height = split_rect.bottom - split_rect.top; // 計算切割矩型的高
		int split_width = postion_width / x_split; // 計算切割區塊每塊的寬
		int split_height = postion_height / y_split; // 計算切割區塊每塊的高

		result = new Bitmap[x_split * y_split];

		for (int i = 0; i < x_split; i++) {
			for (int j = 0; j < y_split; j++) {

				// 取出指定區塊的bitmap
				bitmapBuffer = Bitmap
						.createBitmap(split_source, split_rect.left
								+ (j * split_width), split_rect.top
								+ (i * split_height), split_width, split_height);
				// 存入結果陣列
				result[i * y_split + j] = bitmapBuffer;
			}
		}

		// if (bitmapBuffer != null && !bitmapBuffer.isRecycled()) { //目前回收會掛
		// bitmapBuffer.recycle();
		// bitmapBuffer = null;
		// }

		return result;
	}

	private static ImageView.OnTouchListener onTouchPuzzle = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// 動畫中不接受事件move_direction
			if (scene_flag != 0 || AI_isrun)
				return true;

			switch (event.getAction()) {// 依touch事件不同作不同的事情
			case MotionEvent.ACTION_DOWN:
				// 按下時，取得目前的x,y
				pre_x = (int) event.getX();
				pre_y = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				// 移動時，判斷按鈕移動
				image_move = true;
				AI_isrun = false;
				ImageView btnBuffer = (ImageView) activity.findViewById(view
						.getId());
				// 判斷move的方向
				int now_x = (int) event.getX();
				int now_y = (int) event.getY();
				int[] move_direction = new int[] { 0, 0 };
				if (pre_x > now_x && pre_x - now_x > puzzle_xdp / 2){// 往左
					move_direction[0] = 3;
					pre_x = pre_x + puzzle_xdp / 2;
				}
				else if (pre_x < now_x && now_x - pre_x > puzzle_xdp / 2){// 往右
					move_direction[0] = 4;
					pre_x = pre_x - puzzle_xdp / 2;
				}
				if (pre_y > now_y && pre_y - now_y > puzzle_xdp / 2){// 往上
					move_direction[1] = 1;
					pre_y = pre_y + puzzle_xdp / 2;
				}
				else if (pre_y < now_y && now_y - pre_y > puzzle_xdp / 2){// 往下
					move_direction[1] = 2;
					pre_y = pre_y - puzzle_xdp / 2;
				}
				// 取得按下的拼圖鈕座標及可移動方向

				int[] canMove = get_canMove(btnBuffer.getId());
				
				// 判斷是否可移動
				// canMove[0]存放被移動的圖片編號，canMove[1]存放移動方向
				if ((canMove[1] == move_direction[0] || canMove[1] == move_direction[1])
						&& canMove[1] != 0) {// 如果目前的移動方向等於可移動方向，最後得判斷!=0，以確保不會出現都是0結果進入的情況
												// 將此拼圖鈕設定為移動目標
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
			// 動畫中不接受事件
			if (scene_flag != 0 || AI_isrun)
				return;

			// 按下時，詢問重新啟動遊戲
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("系統公告");
			alertMessage.setMessage("是否要重新洗牌，您將失去目前遊玩的進度");
			alertMessage.setPositiveButton("確定", playAgain);
			alertMessage.setNegativeButton("取消", null);
			alertMessage.create().show();
		}
	};

	private static ImageView.OnTouchListener onTouchAnswer = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// 動畫中不接受事件
			if (scene_flag != 0 || AI_isrun)
				return true;

			// FrameLayout puzzle_panel;
			LinearLayout answer_panel;

			switch (event.getAction()) {// 依touch事件不同作不同的事情
			case MotionEvent.ACTION_DOWN:
				// 按下時，顯示答案畫面
				// puzzle_panel = (FrameLayout) activity
				// .findViewById(R.id.puzzle_panel);
				image_move = true;
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.GONE);// 隱藏
				answer_panel.setVisibility(View.VISIBLE);// 顯示

				break;
			case MotionEvent.ACTION_UP:
				// 放開時，隱藏答案畫面
				// puzzle_panel = (FrameLayout) activity
				// .findViewById(R.id.puzzle_panel);
				image_move = false;
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.VISIBLE);// 顯示
				answer_panel.setVisibility(View.GONE);// 隱藏
				break;
			}
			return true;
		}
	};

	private static ImageView.OnClickListener onClickReturntitle = new ImageView.OnClickListener() {
		@Override
		public void onClick(View view) {
			// 動畫中不接受事件
			if (scene_flag != 0 || AI_isrun)
				return;

			// 彈跳詢問訊息
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("系統公告");
			alertMessage.setMessage("是否返回主選單?");
			alertMessage.setPositiveButton("回主選單", returnTitle);
			alertMessage.setNegativeButton("取消", null);
			alertMessage.create().show();
		}
	};

	public static int[] get_canMove(int myButton) {// 取得目前myButton拼圖鈕的座標編號和可以移動的方向
		int[] result = new int[] { 0, 0 };// [0]為目前拼圖鈕的座標編號 [1]為可移動方向

		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (block != i * x_count + j) {
					if (Puzzles[i * x_count + j].id == myButton) {

						result[0] = i * x_count + j; // 紀錄圖片移動的編號
//						Log.e("i,j", String.valueOf(i + "," + j));

						if (((i - 1) * x_count + j) == block)// 可上
							result[1] = 1; // 紀錄移動的方向
						else if (((i + 1) * x_count + j) == block)// 可下
							result[1] = 2;
						else if ((i * x_count + j - 1) == block)// 可左
							result[1] = 3;
						else if ((i * x_count + j + 1) == block)// 可右
							result[1] = 4;
						// 強制結束迴圈
						i = 4;
						j = 4;
					}
				}
			}
		}
		return result;
	}

	// 動畫用timer
	public static Runnable tick = new Runnable() {// TION
		public void run() {
			if (scene_flag != 0) {
				puzzle_move();
			}
			handler.postDelayed(this, 45);
		}
	};

	// AI動畫用timer
	private static Runnable tick_AI = new Runnable() {
		public void run() {
			if (AI_count < AI.slideto.size()) { // 直到答案陣列全部移動完為止

				// if (scene_flag == 0){
				move_btn = new int[2];
				int[] canMove = get_canMove(Puzzles[AI.slideto.get(AI_count)].id); // 取得移動的拼圖編號canMove[0]和移動的方向canMove[1]
				puzzle_panel.removeView(iv_ok[canMove[0]]);
				move_btn = canMove;
				scene_flag = 1; // 持續滑動
				// }
//				Log.e("AI.slideto", String.valueOf(AI.slideto.get(AI_count)));
//				Log.e("AI_count", String.valueOf(AI_count));
				AI_count++; // 目前已移動多少的答案陣列
			}
		}
	};

	public static void puzzle_move() {
		// 設定每次位移的量
		int x_speed = (int) (10 * dpi);
		int y_speed = (int) (10 * dpi);
		ImageView btn_start = Puzzles[move_btn[0]].display_object;
		// Log.e("move_btn[0]", String.valueOf(move_btn[0]));
		FrameLayout.LayoutParams p_start = (FrameLayout.LayoutParams) btn_start
				.getLayoutParams();
		// 取得相關座標
		int new_x = p_start.leftMargin; // 移動後的X(先預設為目前起始的點)
		int new_y = p_start.topMargin; // 移動後的Y(先預設為目前起始的點)
		int goal_x = block % x_count; // 目的地的X
		int goal_y = block / y_count; // 目的地的Y
		// 移動處理
		switch (move_btn[1]) {
		case 1:// 向上
			if ((p_start.topMargin - y_speed) <= (goal_y * puzzle_ydp))
				new_y = goal_y * puzzle_ydp;
			else
				new_y -= y_speed;
			break;
		case 2:// 向下
			if ((p_start.topMargin + y_speed) >= goal_y * puzzle_ydp)
				new_y = goal_y * puzzle_ydp;
			else
				new_y += y_speed;
			break;
		case 3:// 向左
			if ((p_start.leftMargin - x_speed) <= goal_x * puzzle_xdp)
				new_x = goal_x * puzzle_xdp;
			else
				new_x -= x_speed;
			break;
		case 4:// 向右
			if ((p_start.leftMargin + x_speed) >= goal_x * puzzle_xdp)
				new_x = goal_x * puzzle_xdp;
			else
				new_x += x_speed;
			break;
		}
		// 將新座標置入
		p_start.setMargins(new_x, new_y, 0, 0);
		btn_start.setLayoutParams(p_start);

		if (new_x == goal_x * puzzle_xdp && new_y == goal_y * puzzle_ydp) {// 已移動到目標點，關閉動畫
			scene_flag = 0;
			Puzzles[block] = Puzzles[move_btn[0]]; // 交換被換成空格的位置
			Puzzles[move_btn[0]] = new PuzzleObject();
			block = move_btn[0]; // 交換被換成空格的位置

			// 如果空白處為左上角，則判斷勝負
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

			if (x_count == 3 && image_move) { // 當在3*3拼圖和圖片被手動滑動時，重新計算答案陣列
//				String bbb = "";

				for (int i = 0; i < Puzzles.length; i++) { //紀錄目前陣列位置
					Puzzles_moveArray[i] = Puzzles[i].values;
//					bbb = bbb + String.valueOf(Puzzles_moveArray[i]);
				}
//				Log.e("目前陣列", bbb);
				AI ai = new AI();
				ai.AI_check(Puzzles_moveArray , 12000);
			}
			image_move = false; // 設定目前拼圖沒在移動
			if (AI_isrun)  //如果AI有啟動下一步拼圖自動移動
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
				if (Puzzles[i * x_count + j].values == A_array[i * x_count + j] && i * x_count + j != 0 ) {// 跳過空格的位置後設定Puzzles拼圖

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

	public static void check_win() {// 判斷是否已經過關

		for (int i = 0; i < x_count * y_count - 1; i++) {
			if (Puzzles[i].values != A_array[i]) {// 出現不相同則結束判斷
				return;
			}
		}
		// 除最後一格皆相同，則成功過關
		game_end();
	}

	public static void game_end() {// 過關處理
							// 彈跳過關訊息
		AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
		alertMessage.setTitle("系統公告");
		alertMessage.setMessage("恭喜您成功達成!!!");
		alertMessage.setPositiveButton("再玩一次", playAgain);
		alertMessage.setNeutralButton("回主選單", returnTitle);
		alertMessage.setNegativeButton("不玩了", exitGame);
		alertMessage.create().show();
		AI_isrun = false;
		game_end = true;
		handler.removeCallbacks(tick_AI);
	}

	private static DialogInterface.OnClickListener playAgain = new DialogInterface.OnClickListener() {// 再玩一次
		public void onClick(DialogInterface arg0, int arg1) {
			if (scene_flag != 0 || AI_isrun)
				return;
			handler.removeCallbacks(tick);
			scene_flag = 0;
			sensorManager.unregisterListener(mySensorListener);
			new PuzzleGame(x_count, y_count, activity, returnTitle);
			game_start(puzzle_goal);
			if (gyroEnable)
				sensorManager.registerListener(mySensorListener, accelerSensor, SensorManager.SENSOR_DELAY_UI);
		}
	};

	private static DialogInterface.OnClickListener exitGame = new DialogInterface.OnClickListener() {// 不玩了
		public void onClick(DialogInterface arg0, int arg1) {
			activity.finish();
		}
	};
	
	public static void exitGame(){
		sensorManager.unregisterListener(mySensorListener);
		gyroEnable = false;
		image_move = false;
		Log.e("unregisterListener", "unregisterListener");
	}

}
