package scut.lc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

import static scut.lc.Constant.*;

enum ActionType{TS,QS};

public class AtomAction implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	ActionType at;
	
	float xStart;
	float yStart;
	float xEnd;
	float yEnd;
	
	float r;
	
	double length;
	
	long hbid;
	int rgb;
	
	public AtomAction(ActionType at,float xStart,float yStart,float xEnd,float yEnd,float r,long hbid,int rgb)
	{
		this.at=at;
		this.xStart=xStart;
		this.yStart=yStart;
		this.xEnd=xEnd;
		this.yEnd=yEnd;
		this.r=r;		
		this.hbid=hbid;
		this.rgb=rgb;
		
		float xSpan=xEnd-xStart;
		float ySpan=yEnd-yStart;
		length=Math.sqrt(xSpan*xSpan + ySpan*ySpan);		
	}
	
	public void drawSelf(Canvas canvas,Paint paint)
	{		
		paint.reset();
		if(at == ActionType.QS)
		{
			paint.setColor(0xFF000000);
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2*r);	
			
			canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);	
			paint.reset();
		}
		else if(at == ActionType.TS)
		{
			paint.setStyle(Style.FILL);
			paint.setColor(Color.rgb(SANDCOLOR[rgb][0],SANDCOLOR[rgb][1],SANDCOLOR[rgb][2]));	
			int steps=(int)(length/hbSize)*4;
			float xSpan = xEnd-xStart;  
			float ySpan = yEnd-yStart;
			float xStep = xSpan/steps;
			float yStep = ySpan/steps;
			Bitmap bm=hmhb.get(hbid);
			for(int i=-1; i<=steps; i++)
			{
				float xc=xStart + i*xStep;
				float yc=yStart + i*yStep;
				canvas.drawBitmap(bm, xc-r,yc-r, paint);
			}
		}
	}   
	
	@Override
	public int hashCode()
	{
		if(at == ActionType.TS)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		
		if(!(o instanceof AtomAction))
		{
			return false;
		}
		
		AtomAction aa=(AtomAction)o;
		if(this.at==aa.at&&this.xStart==aa.xStart&&this.xEnd==aa.xEnd
						 &&this.yStart==aa.yStart&&this.yEnd==aa.yEnd&&this.r==aa.r)
		{
			return true;
		}
		
		return false;
	}
}
