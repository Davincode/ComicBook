package scut.lc;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class PicLoadUtil {
	
	   public static Bitmap LoadBitmap(Resources res, int picId)
	   {
		   Bitmap result = BitmapFactory.decodeResource(res, picId);
		   return result;
	   }
	   
	   public static Bitmap[][] splitPic
	   ( 
			   int cols,
			   int rows,
			   Bitmap srcPic,
			   int dstWitdh,
			   int dstHeight
	   ) 
	   {   
		   
		   final float width=srcPic.getWidth();
		   final float height=srcPic.getHeight();
		   
		   final int tempWidth=(int)(width/rows);
		   final int tempHeight=(int)(height/cols);
		   
		   Bitmap[][] result=new Bitmap[cols][rows];
		   
		   for(int i=0;i<cols;i++)
		   {
			   for(int j=0;j<rows;j++)
			   {
				   Bitmap tempBm=Bitmap.createBitmap(srcPic, j*tempWidth, i*tempHeight,tempWidth, tempHeight);		
				   result[i][j]=scaleToFit(tempBm,dstWitdh,dstHeight);
			   }
		   }
		   
		   return result;
	   }
	   
	   public static Bitmap scaleToFit(Bitmap bm,int dstWidth,int dstHeight)
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
}
