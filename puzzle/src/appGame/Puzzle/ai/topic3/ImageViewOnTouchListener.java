package appGame.Puzzle.ai.topic3;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageViewOnTouchListener implements OnTouchListener {

	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;

	private int mode = NONE;
	private Matrix matrix;
	private Matrix currMatrix = new Matrix();
	private PointF starPoint = new PointF();
	private PointF midPoint;
	private float startDistance;
	private ImageView imgDisPlay;

	public ImageViewOnTouchListener(ImageView iv, Matrix matrix) {
		super();
		this.imgDisPlay = iv;
		this.matrix = matrix;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// Log.e("ACTION_DOWN", "ACTION_DOWN");
			currMatrix.set(matrix);
			starPoint.set(event.getX(), event.getY());

			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// Log.e("ACTION_POINTER_DOWN", "ACTION_POINTER_DOWN");
			mode = ZOOM;
			startDistance = distance(event);

			if (startDistance > 10f) {

				currMatrix.set(matrix);

				midPoint = getMidPoint(event);

			}

			break;

		case MotionEvent.ACTION_MOVE:
			// Log.e("ACTION_MOVE", "ACTION_MOVE");
			if (mode == DRAG) {

				float dx = event.getX() - starPoint.x;
				float dy = event.getY() - starPoint.y;

				matrix.set(currMatrix);
				matrix.postTranslate(dx, dy);
			} else if (mode == ZOOM) {

				float distance = distance(event);
				if (distance > 10f) {

					float cale = distance / startDistance;
					float[] matrix_v = new float[9];
					matrix.getValues(matrix_v);

					if (matrix_v[0] > 3.5 && matrix_v[0] < 6.2) {
						matrix.set(currMatrix);
						matrix.preScale(cale, cale, midPoint.x, midPoint.y);
					}else if (matrix_v[0] < 3.5 && cale > 1){
						matrix.set(currMatrix);
						matrix.preScale(cale, cale, midPoint.x, midPoint.y);
					}else if (matrix_v[0] > 6.2 && cale < 1){
						matrix.set(currMatrix);
						matrix.preScale(cale, cale, midPoint.x, midPoint.y);
					}
					 Log.e("midPoint.x", String.valueOf(midPoint.x));
					 Log.e("midPoint.y", String.valueOf(midPoint.y));
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			mode = 0;
			break;
		case MotionEvent.ACTION_POINTER_UP:

			mode = NONE;
			break;
		default:
			break;
		}

		imgDisPlay.setImageMatrix(matrix);

		return true;
	}

	private float distance(MotionEvent e) {

		float eX = e.getX(1) - e.getX(0);
		float eY = e.getY(1) - e.getY(0);
		return FloatMath.sqrt(eX * eX + eY * eY);
	}

	private PointF getMidPoint(MotionEvent event) {

		float x = (event.getX(1) - event.getX(0)) / 2;
		float y = (event.getY(1) - event.getY(0)) / 2;
		return new PointF(x, y);
	}

}
