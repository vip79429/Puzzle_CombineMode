package imac.gc;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
/**
 * 此類別檔案專門用清除所有有設定background、bitmap等等的View
 *
 * 請注意 假如是CoutomView的話並且有執行
 * @author bychen81
 * @version 1.0.0
 */
public class RecycleAllView {
	
	private int a;
	
	/**
     * 此建構子適用於 Activity OnDestroy 的 super之上
     * 或是 finish 的 super之上
     * @param context
     */
	public RecycleAllView(Context context) {
		ViewGroup VG = (ViewGroup) ((Activity) context).getWindow()
				.getDecorView().getRootView();
		RunAllView(VG);
		System.gc();
	}

	/**
     * 此建構子適用於單獨的View中，如 Fragment、CustomView中
     * @param context
     * @param view
     */
	public RecycleAllView(Context context, ViewGroup view) {
		RunAllInView(view);
		System.gc();
	}

	private void RunAllView(ViewGroup VG) {
		int SUM = VG.getChildCount();
		for (int i = 0; i < SUM; i++) {
			if (a == 1 && i == 0) {
			} else {
				int ChildSUM = getCountChildView(VG.getChildAt(i));
				if (ChildSUM == 0) {
					recycleView(VG.getChildAt(i));
				} else {
					a++;
					RunAllView((ViewGroup) VG.getChildAt(i));
				}
			}
		}
	}

	private void recycleView(View v) {
		v.setDrawingCacheEnabled(true);
		if (v.getBackground() != null) {
			v.getBackground().setCallback(null);
			if (v.getDrawingCache() != null
					&& !v.getDrawingCache().isRecycled()) {
				v.getDrawingCache().recycle();

			}
		}
		v = null;
	}

	private int getCountChildView(View v) {
		try {
			return ((ViewGroup) v).getChildCount();
		} catch (Exception e) {
			return 0;
		}
	}

	private void RunAllInView(ViewGroup VG) {
		int SUM = VG.getChildCount();
		for (int i = 0; i < SUM; i++) {
			int ChildSUM = getCountChildView(VG.getChildAt(i));
			if (ChildSUM == 0) {
				recycleView(VG.getChildAt(i));
			} else {
				RunAllInView((ViewGroup) VG.getChildAt(i));
			}
		}
	}

}
