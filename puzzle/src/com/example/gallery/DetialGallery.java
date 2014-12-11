package com.example.gallery;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class DetialGallery extends Gallery {
	private Camera mCamera = new Camera();
	private int mMaxRotationAngle = 90;
	private int mMaxZoom = -150;
	private int mCoveflowCenter;
	private boolean mCircleMode = false;
	private boolean mAlphaMode = true;

	public DetialGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public int getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {
		// ���嗅�摮iew��敺�
		final int childCenter = getCenterOfView(child);
//		System.out.println("childCenter嚗� + childCenter);
		final int childWidth = child.getWidth();
		// ��閫漲
		int rotationAngle = 0;
		// �蔭頧����
		t.clear();
		// 閮剔蔭頧�憿�
		t.setTransformationType(Transformation.TYPE_MATRIX);
		// 憒���雿銝剖�雿蔭銝�閬�銵�頧�
		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			// �寞����狂allery銝剔�雿蔭靘�蝞�����閫漲
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			System.out.println("rotationAngle:" + rotationAngle);
			// 憒���閫漲蝯��澆之�潭�憭扳�頧�摨西���-mMaxRotationAngle�MaxRotationAngle;嚗�
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
						: mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}
		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {
		// 撠���銵�摮�
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		// ��擃漲
		final int imageHeight = child.getLayoutParams().height;
		// ��撖砍漲
		final int imageWidth = child.getLayoutParams().width;

		// 餈���閫漲��撠�
		final int rotation = Math.abs(rotationAngle);

		// �汶頠訾�甇��蝘餃�camera��閫�撖阡���銝箸憭批���
		// 憒��沆頠訾�蝘餃�嚗���銝�蝘餃�嚗頠訾�撠���撌血蝘餃���
		mCamera.translate(0.0f, 0.0f, 100.0f);
		if (rotation <= mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation * 10));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
			if (mCircleMode) {
				if (rotation < 40)
					mCamera.translate(0.0f, 155, 0.0f);
				else
					mCamera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
			}
			if (mAlphaMode) {
				((ImageView) (child)).setAlpha((int) (255 - rotation * 2));
			}
		}


		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate((int)-(imageWidth / 1.5), (int)-(imageHeight / 0.8));
		imageMatrix.postTranslate((int)(imageWidth / 1.5), (int)(imageHeight / 0.8));
		mCamera.restore();
	}

	//
	// private boolean isScrollingLeft(
	// MotionEvent e1,
	// MotionEvent e2) {
	// return e2.getX() > e1.getX();
	// }

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// int keyCode;
		// if (isScrollingLeft(e1, e2)) {
		// keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		// } else {
		// keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		// }
		// onKeyDown(keyCode, null);
		return false;
	}
}
