package appGame.Puzzle.CombineMode;

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
	public int x_count; // ���϶s�̤j�C��
	public int y_count; // ���϶s�̤j���
	private int pre_x;
	private int pre_y;
	private int block; // �ثe�Ů檺���϶s�y�нs��
	private int[] move_btn; // �ثe�n���ʦܪŮ檺���϶s�y�нs��,���ʤ�V
	private int[] move_btn2; // �ثe�n���ʦܪŮ檺���϶s�y�нs��,���ʤ�V

	public int goal_image_id; // ���ϥؼйϤ����s��
	public static Bitmap puzzle_goal; // ���ϥؼйϤ�(���ϭ����I��+���϶s�Ϥ��ӷ�)
	public static Bitmap[] puzzles_image; // ���ϯx�}
	public Rect goal_split; // ���Ϩӷ����ίx�ΰ϶�
	public static PuzzleObject Puzzles[]; // ���϶s�}�C
	public int Q_array[]; // �D�ذ}�C�A�Yrandom�᪺�}�C
	public int A_array[]; // ���װ}�C
	private int WASH_TIMES = 100; // �~�P�ɪ��H������
	private DialogInterface.OnClickListener returnTitle; // �q�W�h���L�Ӫ��^�D���function

	private int puzzle_xdp = 70; // ���϶s��dpi�A�o��]�w��
	private int puzzle_ydp = 70; // ���϶s��dpi�A�o��]�w��
	private float dpi; // �ѪR�סA�Y1dpi���h�ֹ���
//	private int scene_flag = 0; // ����ʵe��flag�A0���ثe�S�ʵe
	private Handler handler; // �ʵetick��timer�X�ʪ�

	private ImageView imgAnswer; // ���׹�
	private ImageView btnBuffer; // �x�}��

	private ArrayList<ImageView> pics;
	private Button btnAnswer; // ���׫��s
	private Button btnRestart; // ���s�����s
	private Button btnReturnTitle; // �^�D������
	
	private FrameLayout puzzle_panel; // ��i����
	private int iv_new_x = 0;
	private int iv_new_y = 0;
	private int iv_old_x = 0;
	private int iv_old_y = 0;
	private int now_x = 0;
	private int now_y = 0;
	private DisplayMetrics metrics;
	private ImageView btn_start1; // ��ܪ�����
	private ImageView btn_start2; // �Q��ʪ�����
	private boolean btn_start_end = false;
	private FrameLayout myLayout; // �K�W�Q��ܪ�����layout
	private ImageView m_iv; // ��ܪ����ϯB�_
	

	private static ImageView iv_ok[];
	
	public PuzzleTurnBeads(int _x_count, int _y_count, Context _activity,
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
//		handler.removeCallbacks(tick); // �ƥ�
//		handler.postDelayed(tick, 33); // ����ɶ�

		activity.setContentView(R.layout.middle_trunbeads);

	}

	public void game_start(Bitmap _goalImage) {// �C����l��

		iv_ok = new ImageView[x_count * y_count];
		for(int i = 0; i < iv_ok.length; i++)
		{
			iv_ok[i] = new ImageView(activity);
			iv_ok[i].setImageResource(R.drawable.anwser_ok);
		}
		
		metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// ���s�w�q��i�Ϥ��j�p
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

		// �qxml���Jimageview��activity
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
		
		// �w�����J�B�_���Ϫ�layout
		View itemView = LayoutInflater.from(activity).inflate(
				R.layout.imageview_up, null, false);
		myLayout = (FrameLayout) itemView.findViewById(R.id.imageview_ly);
		m_iv = (ImageView) itemView.findViewById(R.id.imageview_up);
		btn_start1 = (ImageView) framelayoutcontaints
				.findViewById(R.id.btn_move);
		btn_start2 = (ImageView) framelayoutcontaints
				.findViewById(R.id.btn_move2);

		// ��l�ƹC����ưO���}�C
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
//		p.topMargin = (int) (metrics.heightPixels * 0.065);
//		p.leftMargin = (int) (metrics.widthPixels * 0.082);
		p.gravity = Gravity.CENTER_HORIZONTAL;
		puzzle_panel.setLayoutParams(p);
		activity.addContentView(framelayoutcontaints, p); // �[�J��ثe��activity

		// �]�w���׫��Ϥj�p�P���j
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

				// �N�ȳ]�w�J�C����ưO���}�C
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
				// �]�w���s�y�Фέ��ߩ󥪤W��
				FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
						puzzle_xdp, puzzle_ydp, Gravity.TOP | Gravity.LEFT);
				params1.setMargins(j * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
						i * puzzle_ydp + (int) (metrics.heightPixels * 0.065), j, i);
				pics.get(i * x_count + j).setLayoutParams(params1);
				// �]�w���s��Ĳ���ƥ�
				pics.get(i * x_count + j).setOnTouchListener(onTouchPuzzle);
				// �N���s�[�J���ϭ��O��

				// ��s���s���A
				pics.get(i * x_count + j).invalidate();
				
				if (Puzzles[i * x_count + j].values == A_array[i * x_count + j]){
					iv_ok[i * x_count + j].setVisibility(View.VISIBLE);
				}
			}
		}

		// ���׭����w��
		imgAnswer = (ImageView) activity.findViewById(R.id.img_answer);
		imgAnswer.setImageBitmap(puzzle_goal);

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
	}

	public void set_QA_array(int _x_count, int _y_count) {
		// �Ȧs�ǦC�ư}�C(�Yser_array[0] = 0,ser_array[1]=1...)
		int[] ser_array = new int[_x_count * _y_count];
		for (int i = 0; i < _x_count * _y_count; i++) {
			ser_array[i] = i;
		}

		// ��l��A_array
		// �ѽL��
		A_array = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 6, 6, 6 };
		FrameLayout Puzzle_bg = (FrameLayout) activity
				.findViewById(R.id.puzzle_bg);
		Puzzle_bg.setBackgroundResource(R.drawable.wood4);

		Q_array = random_puzzle(ser_array);

	}

	public int[] random_puzzle(int[] _old) {// �~�P
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
	private Bitmap[] BitmapSpliter(Rect split_rect, int x_split, int y_split,
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

		return result;
	}

	private ImageView.OnTouchListener onTouchPuzzle = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(final View view, MotionEvent event) {
			// �ʵe���������ƥ�move_direction
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

			switch (event.getAction()) {// ��touch�ƥ󤣦P�@���P���Ʊ�
			case MotionEvent.ACTION_DOWN:
				// ���U�ɡA���o�ثe��x,y
				btnBuffer = (ImageView) activity.findViewById(view.getId());
				pre_x = (int) event.getX();
				pre_y = (int) event.getY() + (int) (metrics.widthPixels * 0.15);
				iv_old_x = (int) event.getRawX();
				iv_old_y = (int) event.getRawY();
				for (int i = 0; i < pics.size(); i++) {
					if (Puzzles[i].id == btnBuffer.getId()) {
						m_iv.setImageBitmap(Puzzles[i].image); // �]�w�B�_�Ϥ�
						btn_start1 = Puzzles[i].display_object; // �]�w�Q��ܪ����ϭn�Q���ê���
					}
				}
				puzzle_panel.removeView(myLayout);
				puzzle_panel.addView(myLayout);
				m_iv.setVisibility(View.INVISIBLE);
				break;
			case MotionEvent.ACTION_MOVE:
				// ���ʮɡA�P�_���s����
				btnBuffer.setVisibility(View.INVISIBLE);
				btn_start1.setVisibility(View.INVISIBLE);
				m_iv.setVisibility(View.VISIBLE);
				btn_start_end = false;
				now_x = (int) event.getRawX() - pre_x;
				now_y = (int) event.getRawY() - pre_y;
				iv_new_x = (int) event.getRawX();
				iv_new_y = (int) event.getRawY();

				activity.runOnUiThread(new Runnable() { // ��sUI�Ϊ�Thread
					public void run() {
						// UI code goes here
						m_iv.layout(now_x, now_y, now_x + view.getWidth(), // �]�w�B�_���ϧ���
								now_y + view.getHeight());
					}
				});

				int[] result = new int[] { 0, 0 };// ��ܪ�����
				int[] move_direction = new int[] { 0, 0 };
				int[] result2 = new int[] { 0, 0 };// �Q��]������
				int[] move_direction2 = new int[] { 0, 0 };

				for (int i = 0; i < pics.size(); i++) {
					if (Puzzles[i].id == btnBuffer.getId()) { // ���o��ܪ�����
						result[0] = i;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						move_btn = result;
						move_btn2 = result;
					}
				}

				if (Math.abs(iv_old_x - iv_new_x) > Math.abs(iv_old_y - iv_new_y)) { // �P�_X�b��ʶZ��  /2�O�@�b�Z��
					if (now_x < (result[0] % 4) * puzzle_xdp - puzzle_xdp / 2 + (int) (metrics.widthPixels * 0.125) && result[0] % x_count != 0) {// ����
						result2[0] = result[0] - 1;
						result[1] = 3;
						result2[1] = 4;
						move_direction[0] = 3;
						move_direction2[0] = 4;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // ��ʿ�ܪ��]�l
						puzzle_move2(); // ��ʳQ�઺�]�l
					} else if (now_x > (result[0] % 4) * puzzle_xdp + puzzle_xdp / 2 + (int) (metrics.widthPixels * 0.125)
							&& (result[0] + 1) % x_count != 0) {// ���k
						result2[0] = result[0] + 1;
						result[1] = 4;
						result2[1] = 3;
						move_direction[0] = 4;
						move_direction2[0] = 3;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // ��ʿ�ܪ��]�l
						puzzle_move2(); // ��ʳQ�઺�]�l
					}
					iv_old_x = iv_new_x;
				} else { // �P�_Y�b��ʶZ��
					int y_result = 0;
					if (result[0] < 4)    //���W�Ʀ� 0000 1111 2222 3333��m
						y_result = 0;
					else if (result[0] < 8)
						y_result = 1;
					else if (result[0] < 12)
						y_result = 2;
					else if (result[0] < 16)
						y_result = 3;

					if ((now_y < y_result * puzzle_ydp - puzzle_ydp / 2 + (int) (metrics.heightPixels * 0.065)) && result[0] - x_count >= 0) {// ���W
						result2[0] = result[0] - x_count;
						result[1] = 1;
						result2[1] = 2;
						move_direction[1] = 1;
						move_direction2[1] = 2;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // ��ʿ�ܪ��]�l
						puzzle_move2(); // ��ʳQ�઺�]�l
					} else if ((now_y > y_result * puzzle_ydp + puzzle_ydp / 2 + (int) (metrics.heightPixels * 0.065))
							&& result[0] + x_count <= x_count * y_count - 1) {// ���U
						result2[0] = result[0] + x_count;
						result[1] = 2;
						result2[1] = 1;
						move_direction[1] = 2;
						move_direction2[1] = 1;
						move_btn = result;
						move_btn2 = result2;
						iv_ok[result[0]].setVisibility(View.INVISIBLE);
						iv_ok[result2[0]].setVisibility(View.INVISIBLE);
						puzzle_move(); // ��ʿ�ܪ��]�l
						puzzle_move2(); // ��ʳQ�઺�]�l
					}
					iv_old_y = iv_new_y;
				}
				break;
			case MotionEvent.ACTION_UP:
				btn_start_end = true;
				btn_start1.setVisibility(View.VISIBLE); // ��ܳQ���ê�����
				m_iv.setVisibility(View.INVISIBLE); // ���ïB�_���Ϥ�
				check_ivIsOK();
				if (Puzzles[0].values == 0 && btn_start_end) { // �ˬd�ӭt
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
			// �ʵe���������ƥ�
//			if (scene_flag != 0)
//				return;

			// ���U�ɡA�߰ݭ��s�ҰʹC��
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("�t�Τ��i");
			alertMessage.setMessage("�O�_�n���s�~�P�A�z�N���h�ثe�C�����i��");
			alertMessage.setPositiveButton("�T�w", playAgain);
			alertMessage.setNegativeButton("����", null);
			alertMessage.create().show();
		}
	};

	private ImageView.OnTouchListener onTouchAnswer = new ImageView.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// �ʵe���������ƥ�
//			if (scene_flag != 0)
//				return true;

			LinearLayout answer_panel;

			switch (event.getAction()) {// ��touch�ƥ󤣦P�@���P���Ʊ�
			case MotionEvent.ACTION_DOWN:
				// ���U�ɡA��ܵ��׵e��
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.GONE);// ����
				answer_panel.setVisibility(View.VISIBLE);// ���

				break;
			case MotionEvent.ACTION_UP:
				// ��}�ɡA���õ��׵e��
				// puzzle_panel = (FrameLayout) activity
				// .findViewById(R.id.puzzle_panel);
				answer_panel = (LinearLayout) activity
						.findViewById(R.id.answer_panel);
				puzzle_panel.setVisibility(View.VISIBLE);// ���
				answer_panel.setVisibility(View.GONE);// ����
				break;
			}
			return true;
		}
	};

	private ImageView.OnClickListener onClickReturntitle = new ImageView.OnClickListener() {
		@Override
		public void onClick(View view) {
			// �ʵe���������ƥ�
//			if (scene_flag != 0)
//				return;

			// �u���߰ݰT��
			AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
			alertMessage.setTitle("�t�Τ��i");
			alertMessage.setMessage("�O�_��^�D���?");
			alertMessage.setPositiveButton("�^�D���", returnTitle);
			alertMessage.setNegativeButton("����", null);
			alertMessage.create().show();
		}
	};

	public int[] get_canMove(int myButton) {// ���o�ثemyButton���϶s���y�нs���M�i�H���ʪ���V
		int[] result = new int[] { 0, 0 };// [0]���ثe���϶s���y�нs�� [1]���i���ʤ�V

		for (int i = 0; i < x_count; i++) {
			for (int j = 0; j < y_count; j++) {
				if (block != i * x_count + j) {
					if (Puzzles[i * x_count + j].id == myButton) {

						result[0] = i * x_count + j; // �����Ϥ����ʪ��s��
						// Log.e("i,j", String.valueOf(i + "," + j));

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
//	private Runnable tick = new Runnable() {// TION
//		public void run() {
//			if (scene_flag != 0) {
//				puzzle_move(); // ��ʿ�ܪ��]�l
//				puzzle_move2(); // ��ʳQ�઺�]�l
//			}
//			handler.postDelayed(this, 0);
//		}
//	};

	public void puzzle_move() {
		// �]�w�C���첾���q
//		int x_speed = (int) (50 * dpi);
//		int y_speed = (int) (50 * dpi);
		btn_start1 = Puzzles[move_btn[0]].display_object;
		FrameLayout.LayoutParams p_start = (FrameLayout.LayoutParams) btn_start1
				.getLayoutParams();
		// ���o�����y��
//		int new_x = p_start.leftMargin; // ���ʫ᪺X(���w�]���ثe�_�l���I)
//		int new_y = p_start.topMargin; // ���ʫ᪺Y(���w�]���ثe�_�l���I)

		int goal_x = 0, goal_y = 0;
		// ���ʳB�z
		switch (move_btn[1]) { // �ھڭn���ʪ����ϳ]�w���I����
		case 1:// �V�W
			goal_x = (move_btn[0] - x_count) % x_count; // �ت��a��X
			goal_y = (move_btn[0] - x_count) / y_count; // �ت��a��Y
			
//			if ((p_start.topMargin - y_speed) <= (goal_y * puzzle_ydp))
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y -= y_speed;
			break;
		case 2:// �V�U
			goal_x = (move_btn[0] + x_count) % x_count; // �ت��a��X
			goal_y = (move_btn[0] + x_count) / y_count; // �ت��a��Y

//			if ((p_start.topMargin + y_speed) >= goal_y * puzzle_ydp)
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y += y_speed;
			break;
		case 3:// �V��
			goal_x = (move_btn[0] - 1) % x_count; // �ت��a��X
			goal_y = (move_btn[0] - 1) / y_count; // �ت��a��Y

//			if ((p_start.leftMargin - x_speed) <= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x -= x_speed;
			break;
		case 4:// �V�k
			goal_x = (move_btn[0] + 1) % x_count; // �ت��a��X
			goal_y = (move_btn[0] + 1) / y_count; // �ت��a��Y

//			if ((p_start.leftMargin + x_speed) >= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x += x_speed;
			break;
		}
		// �N�s�y�иm�J
		p_start.setMargins(goal_x * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
				goal_y * puzzle_ydp + (int) (metrics.heightPixels * 0.065), 0, 0);
		btn_start1.setLayoutParams(p_start);
	}

	public void puzzle_move2() {
		// �]�w�C���첾���q
//		int x_speed = (int) (42 * dpi);
//		int y_speed = (int) (42 * dpi);
		btn_start2 = Puzzles[move_btn2[0]].display_object;
		FrameLayout.LayoutParams p_start = (FrameLayout.LayoutParams) btn_start2
				.getLayoutParams();
		// ���o�����y��
//		int new_x = p_start.leftMargin; // ���ʫ᪺X(���w�]���ثe�_�l���I)
//		int new_y = p_start.topMargin; // ���ʫ᪺Y(���w�]���ثe�_�l���I)

		int goal_x = 0, goal_y = 0;
		// ���ʳB�z
		switch (move_btn2[1]) {
		case 1:// �V�W
			goal_x = (move_btn2[0] - x_count) % x_count; // �ت��a��X
			goal_y = (move_btn2[0] - x_count) / y_count; // �ت��a��Y
//			if ((p_start.topMargin - y_speed) <= (goal_y * puzzle_ydp))
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y -= y_speed;
			break;
		case 2:// �V�U
			goal_x = (move_btn2[0] + x_count) % x_count; // �ت��a��X
			goal_y = (move_btn2[0] + x_count) / y_count; // �ت��a��Y

//			if ((p_start.topMargin + y_speed) >= goal_y * puzzle_ydp)
//				new_y = goal_y * puzzle_ydp;
//			else
//				new_y += y_speed;
			break;
		case 3:// �V��
			goal_x = (move_btn2[0] - 1) % x_count; // �ت��a��X
			goal_y = (move_btn2[0] - 1) / y_count; // �ت��a��Y

//			if ((p_start.leftMargin - x_speed) <= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x -= x_speed;
			break;
		case 4:// �V�k
			goal_x = (move_btn2[0] + 1) % x_count; // �ت��a��X
			goal_y = (move_btn2[0] + 1) / y_count; // �ت��a��Y

//			if ((p_start.leftMargin + x_speed) >= goal_x * puzzle_xdp)
//				new_x = goal_x * puzzle_xdp;
//			else
//				new_x += x_speed;
			break;
		}
		// �N�s�y�иm�J
		p_start.setMargins(goal_x * puzzle_xdp + (int) (metrics.widthPixels * 0.125), 
				goal_y * puzzle_ydp + (int) (metrics.heightPixels * 0.065), 0, 0);
		btn_start2.setLayoutParams(p_start);

//		if (new_x == goal_x * puzzle_xdp && new_y == goal_y * puzzle_ydp) {// �w���ʨ�ؼ��I�A�����ʵe
//			scene_flag = 0;
			PuzzleObject temp;

			switch (move_btn[1]) {
			case 1:// �V�W
				temp = Puzzles[move_btn[0] - x_count];
				Puzzles[move_btn[0] - x_count] = new PuzzleObject();
				Puzzles[move_btn[0] - x_count] = Puzzles[move_btn[0]]; // �洫�Q�����Ů檺��m
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;

				break;
			case 2:// �V�U
				temp = Puzzles[move_btn[0] + x_count];
				Puzzles[move_btn[0] + x_count] = new PuzzleObject();
				Puzzles[move_btn[0] + x_count] = Puzzles[move_btn[0]]; // �洫�Q�����Ů檺��m
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;

				break;
			case 3:// �V��
				temp = Puzzles[move_btn[0] - 1];
				Puzzles[move_btn[0] - 1] = new PuzzleObject();
				Puzzles[move_btn[0] - 1] = Puzzles[move_btn[0]]; // �洫�Q�����Ů檺��m
				Puzzles[move_btn[0]] = new PuzzleObject();
				Puzzles[move_btn[0]] = temp;
				break;
			case 4:// �V�k
				temp = Puzzles[move_btn[0] + 1];
				Puzzles[move_btn[0] + 1] = new PuzzleObject();
				Puzzles[move_btn[0] + 1] = Puzzles[move_btn[0]]; // �洫�Q�����Ů檺��m
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
			// �p�G�ťճB�����W���A�h�P�_�ӭt

			if (btn_start_end) { // ��U�]�l ������ê�����
				btn_start1.setVisibility(View.VISIBLE);
			} else {
				btn_start_end = false;
			}
			
			check_ivIsOK();
//		}
	}

	private void check_ivIsOK() {
		// TODO Auto-generated method stub
		if (Puzzles[move_btn[0]].values == A_array[move_btn[0]]) // �Q��ʪ�Puzzles����
			iv_ok[move_btn[0]].setVisibility(View.VISIBLE);
		else
			iv_ok[move_btn[0]].setVisibility(View.INVISIBLE);
				
				
		if (btn_start_end){
			if (Puzzles[move_btn2[0]].values == A_array[move_btn2[0]]) // �����Puzzles����
				iv_ok[move_btn2[0]].setVisibility(View.VISIBLE);
			else
				iv_ok[move_btn2[0]].setVisibility(View.INVISIBLE);
		}
	}
	
	public void check_win() {// �P�_�O�_�w�g�L��

		for (int i = 0; i < x_count * y_count - 1; i++) {
			Log.e("A_array",
					String.valueOf(Puzzles[i].values + " " + A_array[i]));

			if (Puzzles[i].values != A_array[i]) {// �X�{���ۦP�h�����P�_

				return;
			}
		}
		// ���̫�@��ҬۦP�A�h���\�L��
		game_end();
	}

	public void game_end() {// �L���B�z
							// �u���L���T��
		for (int i = 0; i < pics.size(); i++) {
			pics.get(i).setEnabled(false);
		}
		AlertDialog.Builder alertMessage = new AlertDialog.Builder(activity);
		alertMessage.setTitle("�t�Τ��i");
		alertMessage.setMessage("���߱z���\�F��!!!");
		alertMessage.setPositiveButton("�A���@��", playAgain);
		alertMessage.setNeutralButton("�^�D���", returnTitle);
		alertMessage.setNegativeButton("�����F", exitGame);
		alertMessage.create().show();
	}

	private DialogInterface.OnClickListener playAgain = new DialogInterface.OnClickListener() {// �A���@��
		public void onClick(DialogInterface arg0, int arg1) {
//			if (scene_flag != 0)
//				return;
			new PuzzleTurnBeads(x_count, y_count, activity, returnTitle);
			game_start(puzzle_goal);
			for (int i = 0; i < pics.size(); i++) {
				pics.get(i).setEnabled(true);
			}
		}
	};

	private DialogInterface.OnClickListener exitGame = new DialogInterface.OnClickListener() {// �����F
		public void onClick(DialogInterface arg0, int arg1) {
			activity.finish();
		}
	};

}
