package appGame.Puzzle.CombineMode;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class PuzzleObject 
{
	public int no;						//��l�Ǹ��A�ΨӨ��ϵ���ӥ�
	public int values;					//���϶s����
	public int id;						//���϶s�����ݪ���id
	public Bitmap image;				//���϶s���Ϥ��bdrawable�̪��W��
	public ImageView display_object;	//���϶s
	public String text;					//���϶s����ܦr��(�ιϪ��ܫh���|�ϥ�)
	public int layer;
	public PuzzleObject()
	{
		
	}
}
