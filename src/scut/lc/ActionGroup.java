package scut.lc;

import static scut.lc.Constant.*;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ActionGroup implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	long id = System.nanoTime();
	ArrayList<AtomAction> actionG = new ArrayList<AtomAction>();
	float r;
	
	public ActionGroup(float r,int rgb)
	{
		this.r=r;
		Bitmap bm=Bitmap.createBitmap
		(
				(int)(2*r),
				(int)(2*r), 
				Bitmap.Config.ARGB_8888
		);		
		Canvas ct = new Canvas(bm); 
		Paint p = new Paint();
		p.setColor(Color.rgb(SANDCOLOR[rgb][0],SANDCOLOR[rgb][1],SANDCOLOR[rgb][2]));
		for(int i=0; i<tcl; i++)
		{	
			float rt = (float) (r*Math.random());
			double angle = 2*Math.PI*Math.random();
			float xt = (float) (rt*Math.sin(angle));
			float yt = (float) (rt*Math.cos(angle));
            ct.drawPoint(xt+r, yt+r, p);            
		}
		hmhb.put(id, bm);
	}
}
