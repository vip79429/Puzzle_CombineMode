package appGame.Puzzle.CombineMode;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class PuzzleObject 
{
	public int no;						//初始序號，用來取圖等對照用
	public int values;					//拼圖鈕的值
	public int id;						//拼圖鈕的所屬物件id
	public Bitmap image;				//拼圖鈕的圖片在drawable裡的名稱
	public ImageView display_object;	//拼圖鈕
	public String text;					//拼圖鈕的顯示字元(用圖的話則不會使用)
	public int layer;
	public PuzzleObject()
	{
		
	}
}
