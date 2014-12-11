package appGame.Puzzle.ai.topic3;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
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

@SuppressLint("NewApi")
public class PuzzleTurnBeads {
	public Activity activity;
	public int x_count; // 拼圖鈕最大列數
	public int y_count; // 拼圖鈕最大欄數
	private int pre_x;
	private int pre_y;
	private int block; // 目前空格的拼圖鈕座標編號
	private int[] move_btn; // 目前要移動至空格的拼圖鈕座標編號,移動方向
	private int[] move_btn2; // 目前要移動至空格的拼圖鈕座標編號,移動方向

	public int goal_image_id; // 拼圖目標圖片的編號
	public static Bitmap puzzle_goal; // 拼圖目標圖片(拼圖面版背景+拼圖鈕圖片來源)
	public static Bitmap[] puzzles_image; // 拼圖矩陣
	public Rect goal_split; // 拼圖來源切割矩形區塊
	public static PuzzleObject Puzzles[]; // 拼圖鈕陣列
	public int Q_array[]; // 題目陣列，即random後的陣列
	public int A_array[]; // 答案陣列
	private int WASH_TIMES = 100; // 洗牌時的隨機次數
	private DialogInterface.OnClickListener returnTitle; // 從上層接過來的回主選單function

	private int puzzle_xdp = 70; // 拼圖鈕的dpi，這邊設定為
	private int puzzle_ydp = 70; // 拼圖鈕的dpi，這邊設定為
	private float dpi; // 解析度，即1dpi為多少像素
//	private int scene_flag = 0; // 播放動畫的flag，0為目前沒動畫
	private Handler handler; // 動畫tick的timer驅動者

	private ImageView imgAnswer; // 答案圖
	private ImageView btnBuffer; // 矩陣圖

	private ArrayList<ImageView> pics;
	private Button btnAnswer; // 答案按鈕
	private Button btnRestart; // 重新玩按鈕
	private Button btnReturnTitle; // 回主選單按紐
	private Button btn_show; // AI
	private Button btn_screen; // 全螢幕滑動
	
	private FrameLayout puzzle_panel; // 整張拼圖
	private int iv_new_x = 0;
	private int iv_new_y = 0;
	private int iv_old_x = 0;
	private int iv_old_y = 0;
	private int now_x = 0;
	private int now_y = 0;
	private DisplayMetrics metrics;
	private ImageView btn_start1; // 選擇的拼圖
	private ImageView btn_start2; // 被轉動的拼圖
	private boolean btn_start_end = false;
	private FrameLayout myLayout; // 貼上被選擇的拼圖layout
	private ImageView m_iv; // 選擇的拼圖浮起
	

	private static ImageView iv_ok[];
	
	public PuzzleTurnBeads(int _x_count, int _y_count, Context _activity,
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
//		handler.removeCallbacks(tick); // 事件
//		handler.postDelayed(tick, 33); // 延遲時間

		activity.setContentView(R.layout.middle);

	}

	public void game_start(Bitmap _goalImage) {// 遊戲初始化

		iv_ok = new ImageView[x_count * y_count];
		for(int i = 0; i < iv_ok.length; i++)
		{
			iv_ok[i] = new ImageView(activity);
			iv_ok[i].setImageResource(R.drawable.anwser_ok);
		}
		
		metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// 重新定義整張圖片大小
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

		// 從xml載入imageview至activity
		LayoutInflater inflater = activity.getLayoutInflater();
		View framelayoutcontaints = null;
		pics = new ArrayList<ImageView>();
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
		
		// 預先載入浮起拼圖的layout
		View itemView = LayoutInflater.from(activity).inflate(
				R.layout.imageview_up, null, false);
		myLayout = (FrameLayout) itemView.findViewById(R.id.imageview_ly);
		m_iv = (ImageView) itemView.findViewById(R.id.imageview_up);
		btn_start1 = (ImageView) framelayoutcontaints
				.findViewById(R.id.btn_move);
		btn_start2 = (ImageView) framelayoutcontaints
				.findViewById(R.id.btn_move2);

		// 初始化遊戲資料記錄陣列
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
//		p.topMargin = (int) (metrics.heightPixels * 0.065);
//		p.leftMargin = (int) (metrics.widthPixels * 0.082);
		p.gravity = Gravity.CENTER_HORIZONTAL;
		puzzle_panel.setLayoutParams(p);
		activity.addContentView(framelayoutcontaints, p); // 加入到目前的activity

		// 設定答案拼圖大小與間隔
		LinearLayout answer_panel = (LinearLayout) activity
				.findViewById(R.id.answer_panel);
		FrameLayout.LayoutParams a = new FrameLayout.LayoutParams(
				(int) (metrics.widthPixels * 0.8),
				(int) (metrics.heightPixels * 0.5));
		a.topMargin = (int) (metrics.heightPixels * 0.04);
		a.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		answer_panel.setLayoutParams(a);

		
		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < x_count; j++) {
				FrameLayout.LayoutParams iv_params = new FrameLayout.LayoutParams(
					puzzle_xdp, puzzle_ydp, Gravity.TOP | Gravity.LEFT);
				iv_params.setMargins(j * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
						i * puzzle_ydp + (int) (metrics.heightPixels * 0.065), j, i);
				iv_ok[i * x_count + j].setLayoutParams(iv_params);
				puzzle_panel.addView(iv_ok[i * x_count + j]);
				iv_ok[i * x_count + j].setVisibility(View.INVISIBLE);
			}
		}
		
		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {

				// 將值設定入遊戲資料記錄陣列
				pics.get(i * x_count + j).setImageBitmap(
						puzzles_image[Q_array[i * x_count + j]]);
				Puzzles[i * x_count + j] = new PuzzleObject();
				Puzzles[i * x_count + j].no = Q_array[i * x_count + j];
				Puzzles[i * x_count + j].values = A_array[Q_array[i * x_count
						+ j]];
				Puzzles[i * x_count + j].id = pics.get(i * x_count + j).getId();
				Puzzles[i * x_count + j].image = puzzles_image[Q_array[i
						* x_count + j]];
				Puzzles[i * x_count + j].display_object = pics.get(i * x_count
						+ j);
				// 設定按鈕座標及重心於左上角
				FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
						puzzle_xdp, puzzle_ydp, Gravity.TOP | Gravity.LEFT);
				params1.setMargins(j * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
						i * puzzle_ydp + (int) (metrics.heightPixels * 0.065), j, i);
				pics.get(i * x_count + j).setLayoutParams(params1);
				// 設定按鈕的觸控事件
				pics.get(i * x_count + j).setOnTouchListener(onTouchPuzzle);
				// 將按鈕加入拼圖面板中

				// 刷新按鈕狀態
				pics.get(i * x_count + j).invalidate();
				
				if (Puzzles[i * x_count + j].values == A_array[i * x_count + j]){
					iv_ok[i * x_count + j].setVisibility(View.VISIBLE);
				}
			}
		}

		// 答案面版安裝
		imgAnswer = (ImageView) activity.findViewById(R.id.img_answer);
		imgAnswer.setImageBitmap(puzzle_goal);

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
		// AI
		btn_show = (Button) activity.findViewById(R.id.btn_show);
		btn_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "只有3*3有解答", Toast.LENGTH_SHORT).show();
			}
		});
		// 全屏
		btn_screen = (Button) activity.findViewById(R.id.btn_screen);
		btn_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "轉珠模式不支援全螢幕滑動", Toast.LENGTH_SHORT)
						.show();
			}
		});
		

	}

	public void set_QA_array(int _x_count, int _y_count) {
		// 暫存序列化陣列(即ser_array[0] = 0,ser_array[1]=1...)
		int[] ser_array = new int[_x_count * _y_count];
		for (int i = 0; i < _x_count * _y_count; i++) {
			ser_array[i] = i;
		}

		// 初始化A_array
		// 棋盤版
		A_array = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6, 6, 6 };
		FrameLayout Puzzle_bg = (FrameLayout) activity
				.findViewById(R.id.puzzle_bg);
		Puzzle_bg.setBackgroundResource(R.drawable.wood4);

		Q_array = random_puzzle(ser_array);

	}

	public int[] random_puzzle(int[] _old) {// 洗牌
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
	private Bitmap[] BitmapSpliter(Rect split_rect, int x_split, int y_split,
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

		return result;
	}

	private ImageView.OnTouchListener onTouchPuzzle = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(final View view, MotionEvent event) {
			// 動畫中不接受事件move_direction
//			if (scene_flag != 0) {
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					btn_start_end = true;
//					m_iv.setVisibility(View.INVISIBLE);
//					btn_start1.setVisibility(View.VISIBLE);
//					if (Puzzles[0].values == 0 && btn_start_end) {
//						check_win();
//						Log.e("check_win", "check_win");
//					}
//				}
//				return true;
//			}

			switch (event.getAction()) {// 依touch事件不同作不同的事情
			case MotionEvent.ACTION_DOWN:
				// 按下時，取得目前的x,y
				btnBuffer = (ImageView) activity.findViewById(view.getId());
				pre_x = (int) event.getX();
				pre_y = (int) event.getY() + (int) (metrics.widthPixels * 0.15);
				iv_old_x = (int) event.getRawX();
				iv_old_y = (int) event.getRawY();
				for (int i = 0; i < pics.size(); i++) {
					if (Puzzles[i].id == btnBuffer.getId()) {
						m_iv.setImageBitmap(Puzzles[i].image); // 設定浮起圖片
						btn_start1 = Puzzles[i].display_object; // 設定被選擇的拼圖要被隱藏的圖
					}
				}
				puzzle_panel.removeView(myLayout);
				puzzle_panel.addView(myLayout);
				m_iv.setVisibility(View.INVISIBLE);
				break;
			case MotionEvent.ACTION_MOVE:
				// 移動時，判斷按鈕移動
				btnBuffer.setVisibility(View.INVISIBLE);
				btn_start1.setVisibility(View.INVISIBLE);
				m_iv.setVisibility(View.VISIBLE);
				btn_start_end = false;
				now_x = (int) event.getRawX() - pre_x;
				now_y = (int) event.getRawY() - pre_y;
				iv_new_x = (int) event.getRawX();
				iv_new_y = (int) event.getRawY();

				activity.runOnUiThread(new Runnable() { // 更新UI用的Thread
					public void run() {
						// UI code goes here
						m_iv.layout(now_x, now_y, now_x + view.getWidth(), // 設定浮起拼圖坐標
								now_y + view.getHeight());
					}
				});

				int[] result = new int[] { 0, 0 };// 選擇的拼圖
				int[] move_direction = new int[] { 0, 0 };
				int[] result2 = new int[] { 0, 0 };// 被轉珠的拼圖
				int[] move_direction2 = new int[] { 0, 0 };

				for (int i = 0; i < pics.size(); i++) {
					if (Puzzles[i].id == btnBuffer.getId()) { // 取得選擇的拼圖
						result[0] = i;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						move_btn = result;
						move_btn2 = result;
					}
				}

				if (Math.abs(iv_old_x - iv_new_x) > Math.abs(iv_old_y - iv_new_y)) { // 判斷X軸轉動距離  /2是一半距離
					if (now_x < (result[0] % 4) * puzzle_xdp - puzzle_xdp / 2 + (int) (metrics.widthPixels * 0.125) && result[0] % x_count != 0) {// 往左
						result2[0] = result[0] - 1;
						result[1] = 3;
						result2[1] = 4;
						move_direction[0] = 3;
						move_direction2[0] = 4;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // 轉動選擇的珠子
						puzzle_move2(); // 轉動被轉的珠子
					} else if (now_x > (result[0] % 4) * puzzle_xdp + puzzle_xdp / 2 + (int) (metrics.widthPixels * 0.125)
							&& (result[0] + 1) % x_count != 0) {// 往右
						result2[0] = result[0] + 1;
						result[1] = 4;
						result2[1] = 3;
						move_direction[0] = 4;
						move_direction2[0] = 3;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // 轉動選擇的珠子
						puzzle_move2(); // 轉動被轉的珠子
					}
					iv_old_x = iv_new_x;
				} else { // 判斷Y軸轉動距離
					int y_result = 0;
					if (result[0] < 4)    //正規化至 0000 1111 2222 3333位置
						y_result = 0;
					else if (result[0] < 8)
						y_result = 1;
					else if (result[0] < 12)
						y_result = 2;
					else if (result[0] < 16)
						y_result = 3;

					if ((now_y < y_result * puzzle_ydp - puzzle_ydp / 2 + (int) (metrics.heightPixels * 0.065)) && result[0] - x_count >= 0) {// 往上
						result2[0] = result[0] - x_count;
						result[1] = 1;
						result2[1] = 2;
						move_direction[1] = 1;
						move_direction2[1] = 2;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // 轉動選擇的珠子
						puzzle_move2(); // 轉動被轉的珠子
					} else if ((now_y > y_result * puzzle_ydp + puzzle_ydp / 2 + (int) (metrics.heightPixels * 0.065))
							&& result[0] + x_count <= x_count * y_count - 1) {// 往下
						result2[0] = result[0] + x_count;
						result[1] = 2;
						result2[1] = 1;
						move_direction[1] = 2;
						move_direction2[1] = 1;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // 轉動選擇的珠子
						puzzle_move2(); // 轉動被轉的珠子
					}
					iv_old_y = iv_new_y;
				}
				break;
			case MotionEvent.ACTION_UP:
				btn_start_end = true;
				btn_start1.setVisibility(View.VISIBLE); // 顯示被隱藏的拼圖
				m_iv.setVisibility(View.INVISIBLE); // 隱藏浮起的圖片
				check_ivIsOK();
				if (Puzzles[0].values == 0 && btn_start_end) { // 檢查勝負
					check_win();
					Log.e("check_win", "check_win");
				}
				break;
			}
			return true;
		}
	};

	private ImageView.OnClickListener onClickRestart = new ImageView.OnClickListener() {
		@Override
		public void onClick(View view) {
			// 動畫中不接受事件
//			if (scene_flag != 0)
//				return;

			// 按下時，詢問重新啟動遊戲
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("系統公告");
			alertMessage.setMessage("是否要重新洗牌，您將失去目前遊玩的進度");
			alertMessage.setPositiveButton("確定", playAgain);
			alertMessage.setNegativeButton("取消", null);
			alertMessage.create().show();
		}
	};

	private ImageView.OnTouchListener onTouchAnswer = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// 動畫中不接受事件
//			if (scene_flag != 0)
//				return true;

			LinearLayout answer_panel;

			switch (event.getAction()) {// 依touch事件不同作不同的事情
			case MotionEvent.ACTION_DOWN:
				// 按下時，顯示答案畫面
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.GONE);// 隱藏
				answer_panel.setVisibility(View.VISIBLE);// 顯示

				break;
			case MotionEvent.ACTION_UP:
				// 放開時，隱藏答案畫面
				// puzzle_panel = (FrameLayout) activity
				// .findViewById(R.id.puzzle_panel);
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.VISIBLE);// 顯示
				answer_panel.setVisibility(View.GONE);// 隱藏
				break;
			}
			return true;
		}
	};

	private ImageView.OnClickListener onClickReturntitle = new ImageView.OnClickListener() {
		@Override
		public void onClick(View view) {
			// 動畫中不接受事件
//			if (scene_flag != 0)
//				return;

			// 彈跳詢問訊息
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("系統公告");
			alertMessage.setMessage("是否返回主選單?");
			alertMessage.setPositiveButton("回主選單", returnTitle);
			alertMessage.setNegativeButton("取消", null);
			alertMessage.create().show();
		}
	};

	public int[] get_canMove(int myButton) {// 取得目前myButton拼圖鈕的座標編號和可以移動的方向
		int[] result = new int[] { 0, 0 };// [0]為目前拼圖鈕的座標編號 [1]為可移動方向

		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (block != i * x_count + j) {
					if (Puzzles[i * x_count + j].id == myButton) {

						result[0] = i * x_count + j; // 紀錄圖片移動的編號
						// Log.e("i,j", String.valueOf(i + "," + j));

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
//	private Runnable tick = new Runnable() {// TION
//		public void run() {
//			if (scene_flag != 0) {
//				puzzle_move(); // 轉動選擇的珠子
//				puzzle_move2(); // 轉動被轉的珠子
//			}
//			handler.postDelayed(this, 0);
//		}
//	};

	public void puzzle_move() {
		// 設定每次位移的量
//		int x_speed = (int) (50 * dpi);
//		int y_speed = (int) (50 * dpi);
		btn_start1 = Puzzles[move_btn[0]].display_object;
		FrameLayout.LayoutParams p_start = (FrameLayout.LayoutParams) btn_start1
				.getLayoutParams();
		// 取得相關座標
//		int new_x = p_start.leftMargin; // 移動後的X(先預設為目前起始的點)
//		int new_y = p_start.topMargin; // 移動後的Y(先預設為目前起始的點)

		int goal_x = 0, goal_y = 0;
		// 移動處理
		switch (move_btn[1]) { // 根據要移動的拼圖設定終點坐標
		case 1:// 向上
			goal_x = (move_btn[0] - x_count) % x_count; // 目的地的X
			goal_y = (move_btn[0] - x_count) / y_count; // 目的地的Y
			
//			if ((p_start.topMargin - y_speed) <= (goal_y * puzzle_ydp))
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y -= y_speed;
			break;
		case 2:// 向下
			goal_x = (move_btn[0] + x_count) % x_count; // 目的地的X
			goal_y = (move_btn[0] + x_count) / y_count; // 目的地的Y

//			if ((p_start.topMargin + y_speed) >= goal_y * puzzle_ydp)
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y += y_speed;
			break;
		case 3:// 向左
			goal_x = (move_btn[0] - 1) % x_count; // 目的地的X
			goal_y = (move_btn[0] - 1) / y_count; // 目的地的Y

//			if ((p_start.leftMargin - x_speed) <= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x -= x_speed;
			break;
		case 4:// 向右
			goal_x = (move_btn[0] + 1) % x_count; // 目的地的X
			goal_y = (move_btn[0] + 1) / y_count; // 目的地的Y

//			if ((p_start.leftMargin + x_speed) >= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x += x_speed;
			break;
		}
		// 將新座標置入
		p_start.setMargins(goal_x * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
				goal_y * puzzle_ydp + (int) (metrics.heightPixels * 0.065), 0, 0);
		btn_start1.setLayoutParams(p_start);
	}

	public void puzzle_move2() {
		// 設定每次位移的量
//		int x_speed = (int) (42 * dpi);
//		int y_speed = (int) (42 * dpi);
		btn_start2 = Puzzles[move_btn2[0]].display_object;
		FrameLayout.LayoutParams p_start = (FrameLayout.LayoutParams) btn_start2
				.getLayoutParams();
		// 取得相關座標
//		int new_x = p_start.leftMargin; // 移動後的X(先預設為目前起始的點)
//		int new_y = p_start.topMargin; // 移動後的Y(先預設為目前起始的點)

		int goal_x = 0, goal_y = 0;
		// 移動處理
		switch (move_btn2[1]) {
		case 1:// 向上
			goal_x = (move_btn2[0] - x_count) % x_count; // 目的地的X
			goal_y = (move_btn2[0] - x_count) / y_count; // 目的地的Y
//			if ((p_start.topMargin - y_speed) <= (goal_y * puzzle_ydp))
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y -= y_speed;
			break;
		case 2:// 向下
			goal_x = (move_btn2[0] + x_count) % x_count; // 目的地的X
			goal_y = (move_btn2[0] + x_count) / y_count; // 目的地的Y

//			if ((p_start.topMargin + y_speed) >= goal_y * puzzle_ydp)
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y += y_speed;
			break;
		case 3:// 向左
			goal_x = (move_btn2[0] - 1) % x_count; // 目的地的X
			goal_y = (move_btn2[0] - 1) / y_count; // 目的地的Y

//			if ((p_start.leftMargin - x_speed) <= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x -= x_speed;
			break;
		case 4:// 向右
			goal_x = (move_btn2[0] + 1) % x_count; // 目的地的X
			goal_y = (move_btn2[0] + 1) / y_count; // 目的地的Y

//			if ((p_start.leftMargin + x_speed) >= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x += x_speed;
			break;
		}
		// 將新座標置入
		p_start.setMargins(goal_x * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
				goal_y * puzzle_ydp + (int) (metrics.heightPixels * 0.065), 0, 0);
		btn_start2.setLayoutParams(p_start);

//		if (new_x == goal_x * puzzle_xdp && new_y == goal_y * puzzle_ydp) {// 已移動到目標點，關閉動畫
//			scene_flag = 0;
			PuzzleObject temp;

			switch (move_btn[1]) {
			case 1:// 向上
				temp = Puzzles[move_btn[0] - x_count];
				Puzzles[move_btn[0] - x_count] = new PuzzleObject();
				Puzzles[move_btn[0] - x_count] = Puzzles[move_btn[0]]; // 交換被換成空格的位置
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;

				break;
			case 2:// 向下
				temp = Puzzles[move_btn[0] + x_count];
				Puzzles[move_btn[0] + x_count] = new PuzzleObject();
				Puzzles[move_btn[0] + x_count] = Puzzles[move_btn[0]]; // 交換被換成空格的位置
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;

				break;
			case 3:// 向左
				temp = Puzzles[move_btn[0] - 1];
				Puzzles[move_btn[0] - 1] = new PuzzleObject();
				Puzzles[move_btn[0] - 1] = Puzzles[move_btn[0]]; // 交換被換成空格的位置
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;
				break;
			case 4:// 向右
				temp = Puzzles[move_btn[0] + 1];
				Puzzles[move_btn[0] + 1] = new PuzzleObject();
				Puzzles[move_btn[0] + 1] = Puzzles[move_btn[0]]; // 交換被換成空格的位置
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;

				break;
			}

			// for (int i = 0; i < 9; i += 3) {
			// if (Puzzles != null)
			// Log.e("Puzzles[i].values",
			// String.valueOf(Puzzles[i].values + " "
			// + Puzzles[i + 1].values + " "
			// + Puzzles[i + 2].values));
			// }
			// 如果空白處為左上角，則判斷勝負

			if (btn_start_end) { // 放下珠子 顯示隱藏的拼圖
				btn_start1.setVisibility(View.VISIBLE);
			} else {
				btn_start_end = false;
			}
			
			check_ivIsOK();
//		}
	}

	private void check_ivIsOK() {
		// TODO Auto-generated method stub
		if (Puzzles[move_btn[0]].values == A_array[move_btn[0]]) // 被轉動的Puzzles拼圖
			iv_ok[move_btn[0]].setVisibility(View.VISIBLE);
		else
			iv_ok[move_btn[0]].setVisibility(View.INVISIBLE);
				
				
		if (btn_start_end){
			if (Puzzles[move_btn2[0]].values == A_array[move_btn2[0]]) // 選取的Puzzles拼圖
				iv_ok[move_btn2[0]].setVisibility(View.VISIBLE);
			else
				iv_ok[move_btn2[0]].setVisibility(View.INVISIBLE);
		}
	}
	
	public void check_win() {// 判斷是否已經過關

		for (int i = 0; i < x_count * y_count - 1; i++) {
			Log.e("A_array",
					String.valueOf(Puzzles[i].values + " " + A_array[i]));

			if (Puzzles[i].values != A_array[i]) {// 出現不相同則結束判斷

				return;
			}
		}
		// 除最後一格皆相同，則成功過關
		game_end();
	}

	public void game_end() {// 過關處理
							// 彈跳過關訊息
		for (int i = 0; i < pics.size(); i++) {
			pics.get(i).setEnabled(false);
		}
		AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
		alertMessage.setTitle("系統公告");
		alertMessage.setMessage("恭喜您成功達成!!!");
		alertMessage.setPositiveButton("再玩一次", playAgain);
		alertMessage.setNeutralButton("回主選單", returnTitle);
		alertMessage.setNegativeButton("不玩了", exitGame);
		alertMessage.create().show();
	}

	private DialogInterface.OnClickListener playAgain = new DialogInterface.OnClickListener() {// 再玩一次
		public void onClick(DialogInterface arg0, int arg1) {
//			if (scene_flag != 0)
//				return;
			new PuzzleGame(x_count, y_count, activity, returnTitle);
			game_start(puzzle_goal);
			for (int i = 0; i < pics.size(); i++) {
				pics.get(i).setEnabled(true);
			}
		}
	};

	private DialogInterface.OnClickListener exitGame = new DialogInterface.OnClickListener() {// 不玩了
		public void onClick(DialogInterface arg0, int arg1) {
			activity.finish();
		}
	};

}
