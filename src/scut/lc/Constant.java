package scut.lc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class Constant {
	
	public static final int NAME_INPUT_DIALOG_ID = 0;
	public static final int DELETE_DIALOG_ID = 1;
	
	public static int MOVE_YOFFSET = 20;  
	public static final float ws = 480; 	
	public static final float hs = 800; 	
	public static float SCREEN_WIDTH;	
	public static float SCREEN_HEIGHT;	
	public static float SCALE;
	public static float X_SCALE;
	public static float Y_SCALE;

	public static int [][] SANDCOLOR={{0, 0, 0},
									  {0,255,0},
									  {255,165,0},
									  {192,168,25},
									  {255,255,0},
									  {0,0,255},
									  {255,0,0}};
				
	public static float hbSize=5;		
	public static float tcl=100;			
	public static int rgb=0;			
	public static float AREA_WIDTH;	
	public static float AREA_HEIGHT;
	public static float pic_w;		
	public static float pic_h;		
	public static float bitmap_between_width=5;
	public static Object actionLock=new Object();
	
	static LinkedHashSet<AtomAction> alAction=new LinkedHashSet<AtomAction>();
	static ArrayList<ActionGroup> allActiongroup=new ArrayList<ActionGroup>();
	static HashMap<Long,Bitmap> hmhb=new HashMap<Long,Bitmap>();
	
	static Bitmap bmBuff;
	static Canvas canvasBuff; 
	
	static List<byte[]> history = new LinkedList<byte[]>();
	static List<Record> alr = new LinkedList<Record>();
	
	static List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	
    public static int PAINT_WIDTH; 
    public static int PAINT_HEIGHT; 
	
	
	public static int[] MAIN_AN_ID = 
	{
		R.drawable.a1,R.drawable.a2,
		R.drawable.a3,R.drawable.a4,
		R.drawable.a5,R.drawable.a6,
	};
	
	public static int[] logos = 
	{ 
		R.drawable.wei, R.drawable.shu, 
		R.drawable.wu
	};
	
	public static String[] generalsTypes = 
	{
		"1", "2", "3" 
	};
	
	public static String[][] generals = 
	{
		{ "xiahoudun", "zhenji", "xuchu", "guojia"},
		{ "machao", "zhangfei", "liubei", "zhugeliang"},
		{ "lvmeng", "luxun", "sunquan"}
	};
	
	public static int[][] generallogos = 
	{
		{ R.drawable.bao_1_1, R.drawable.bao_1_2,
			R.drawable.bao_1_3, R.drawable.bao_1_4},
		{ R.drawable.bao_2_1, R.drawable.bao_2_2,
			R.drawable.bao_2_3, R.drawable.bao_2_4 },
		{ R.drawable.bao_3_1,R.drawable.bao_3_2, 
			R.drawable.bao_3_3 } 
	};
	
	public static void getRatio()
	{
		float wratio = SCREEN_WIDTH/ws;				
		float hratio = SCREEN_HEIGHT/hs;	
		X_SCALE = wratio;
		Y_SCALE = hratio;
		if(wratio < hratio)
		{
			SCALE = wratio;
		}
		else
		{
			SCALE = hratio; 
		}
		hbSize = SCALE * hbSize;
		
		PAINT_WIDTH = (int)(SCREEN_WIDTH / 10);
		PAINT_HEIGHT = (int)(SCREEN_HEIGHT / 10);
	}	
	
	public static Bitmap [] MAIN_AN_ARRAY;
	public static Bitmap [][] Pic_AN_ARRAY;
	
	public static Bitmap scaleToFitXYRatio(Bitmap bm,int dstWidth,int dstHeight)
	   {
		   	float width = bm.getWidth();
		   	float height = bm.getHeight();
		   	float wRatio = dstWidth/width;
		   	float hRatio = dstHeight/height;
		   	
		   	Matrix m1 = new Matrix(); 
		   	m1.postScale(wRatio, hRatio);
		   	
		   	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, (int)width, (int)height, m1, true);       	
		   	return bmResult;
	   }
	
	public static void initPicture(Resources res)
	{
		MAIN_AN_ARRAY = new Bitmap[MAIN_AN_ID.length];
		Pic_AN_ARRAY = new Bitmap[generallogos.length][generallogos[0].length];
		
		for(int i=0;i<MAIN_AN_ID.length;i++)
		{
			MAIN_AN_ARRAY[i]=BitmapFactory.decodeResource(res, MAIN_AN_ID[i]);
			MAIN_AN_ARRAY[i]=Constant.scaleToFitXYRatio(MAIN_AN_ARRAY[i], (int)SCREEN_WIDTH / 6, (int)SCREEN_WIDTH / 6);
		}		
		
		for(int i=0;i<generallogos.length;i++)
		{
			for(int j=0;j<generallogos[i].length;j++)
			{
				Pic_AN_ARRAY[i][j]=BitmapFactory.decodeResource(res, generallogos[i][j]);
			}	
		}
		
		AREA_WIDTH=SCREEN_WIDTH;	
		AREA_HEIGHT=SCREEN_HEIGHT-MAIN_AN_ARRAY[0].getHeight();
		pic_h = SCREEN_HEIGHT * 3 / 4;
		pic_w=pic_h*AREA_WIDTH/AREA_HEIGHT;	
	}	
	
}
