package scut.lc;

import static scut.lc.Constant.*;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

enum Func { None, Scroll, Slide, Long_Click, Animate };

public class ShowGalleryView extends View
{
	static Bitmap background;  
	static Bitmap ban;  
	Paint paint;
	int index = -1;				
	float prex;						
	float prey;						
	float xoffset=0;
	float yoffset=0;
	Func func = Func.None;
	boolean isSelect=false;			
	Context context;
	
	float currV=0;					
	float acce=0;					
	float preXForV;					
	boolean isAutoGo=false;		
	float delta = 0;
	
	static boolean isLongClick=false;
	double hf;
	RectF rectF;
	
	public ShowGalleryView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context = context;
		
		paint = new Paint();						
		paint.setAntiAlias(true);
		rectF = new RectF(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		Bitmap srcPic=PicLoadUtil.LoadBitmap(getResources(), R.drawable.bg_2);
		background=PicLoadUtil.splitPic(1, 1, srcPic, (int)SCREEN_WIDTH, (int)SCREEN_HEIGHT)[0][0];
		Bitmap srcPic_2=PicLoadUtil.LoadBitmap(getResources(), R.drawable.work_grid_image_bg);
		ban=PicLoadUtil.splitPic(1, 1, srcPic_2, 300, 400)[0][0];
	}
	
	public void onDraw(Canvas canvas)			
	{
		canvas.clipRect(rectF);
		
		float xMin=(SCREEN_WIDTH + pic_w)/2 -(bitmap_between_width+pic_w)*bitmaps.size();
		if(xMin>0)
		{
			xMin=0;
		}
		float xMax=0;		
		if(xoffset>xMax)						
		{
			xoffset=xMax;						
			isAutoGo=false;
		}		
		if(xoffset<xMin)
		{
			xoffset=xMin;						
			isAutoGo=false;
		}
		
		canvas.drawBitmap(background, 0, 0, paint);
		if(func == Func.Animate)
		{
			delta += 30;
			if(delta > pic_w + bitmap_between_width)
			{
				delta = 0;
				func = Func.None;
				if(index > -1 && index < bitmaps.size())
				{
					bitmaps.remove(index);
					//history.remove(index);
					alr.remove(index);
				}
			}
			this.invalidate();
		}
		
		for(int i = 0; i < bitmaps.size(); i++)
		{
			Bitmap bitmap = bitmaps.get(i);
			float xTemp = (bitmap_between_width+pic_w) * i + (SCREEN_WIDTH - pic_w)/2;
			float yTemp = (this.getHeight()-pic_h)/2;
			float ratioTemp = pic_w/bitmap.getWidth();
			float xr = pic_w/ban.getWidth();
			float yr = pic_h/ban.getHeight();
			
			if(func == Func.Animate)
			{
				if(i < index)
				{
					canvas.save();
					canvas.translate(xTemp+xoffset, yTemp+20);
					canvas.scale(xr, yr);
					canvas.drawBitmap(ban, 0, 0, paint);		
					canvas.restore();
					
					canvas.save();
					canvas.translate(xTemp+xoffset, yTemp+20);
					canvas.scale(ratioTemp, ratioTemp);
					canvas.drawBitmap(bitmap,0,0, paint);
					canvas.restore();
					
					paint.setColor(Color.WHITE);
					paint.setTextSize(20);
					paint.setAntiAlias(true);
					String nameTemp = Integer.toString(i);
					float nl=paint.measureText(nameTemp);
					canvas.drawText(nameTemp, xTemp+xoffset+(pic_w-nl)/2.0f, yTemp+pic_h+80, paint);
					paint.reset();
				}
				else if(i > index)
				{
					canvas.save();
					canvas.translate(xTemp+xoffset-delta, yTemp+20);
					canvas.scale(xr, yr);
					canvas.drawBitmap(ban, 0, 0, paint);		
					canvas.restore();
					
					canvas.save();
					canvas.translate(xTemp+xoffset-delta, yTemp+20);
					canvas.scale(ratioTemp, ratioTemp);
					canvas.drawBitmap(bitmap,0,0, paint);
					canvas.restore();
					
					paint.setColor(Color.WHITE);
					paint.setTextSize(20);
					paint.setAntiAlias(true);
					String nameTemp = Integer.toString(i);
					float nl=paint.measureText(nameTemp);
					canvas.drawText(nameTemp, xTemp+xoffset-delta+(pic_w-nl)/2.0f, yTemp+pic_h+80, paint);
					paint.reset();
				}
			}
			else if(func == Func.Slide && index == i)
			{
				canvas.save();
				canvas.translate(xTemp+xoffset, yTemp+yoffset+20);
				canvas.scale(xr, yr);
				canvas.drawBitmap(ban, 0, 0, paint);		
				canvas.restore();
				
				canvas.save();
				canvas.translate(xTemp+xoffset, yTemp+yoffset+20);
				canvas.scale(ratioTemp, ratioTemp);
				canvas.drawBitmap(bitmap, 0, 0, paint);
				canvas.restore();
				
				paint.setColor(Color.WHITE);
				paint.setTextSize(20);
				paint.setAntiAlias(true);
				String nameTemp = Integer.toString(i);
				float nl=paint.measureText(nameTemp);
				canvas.drawText(nameTemp, xTemp+xoffset+(pic_w-nl)/2.0f, yTemp+pic_h+yoffset+80, paint);
				paint.reset();
			}
			else
			{
				canvas.save();
				canvas.translate(xTemp+xoffset, yTemp+20);
				canvas.scale(xr, yr);
				canvas.drawBitmap(ban, 0, 0, paint);		
				canvas.restore();
				
				canvas.save();
				canvas.translate(xTemp+xoffset, yTemp+20);
				canvas.scale(ratioTemp, ratioTemp);
				canvas.drawBitmap(bitmap,0,0, paint);
				canvas.restore();
				
				paint.setColor(Color.WHITE);
				paint.setTextSize(20);
				paint.setAntiAlias(true);
				String nameTemp = Integer.toString(i);
				float nl=paint.measureText(nameTemp);
				canvas.drawText(nameTemp, xTemp+xoffset+(pic_w-nl)/2.0f, yTemp+pic_h+80, paint);
				paint.reset();
			}
		}
	}
	
	
	public boolean onTouchEvent(MotionEvent e)
	{
		float x = e.getX();								
		float y = e.getY();								
		switch(e.getAction())
		{
			case MotionEvent.ACTION_DOWN:					
				isAutoGo=false;
				
				isLongClick=false;
				prex=x;										
				prey=y;										
				preXForV=x;
				index=(int)((x-xoffset)/(pic_w+bitmap_between_width));
//				if(alr.size()==1&&x>=0&&x<=(pic_w)&&y>=(this.getHeight()-pic_h)/2&&y<=(this.getHeight()+pic_h)/2){
//					index=0;
//				}
//				if(alr.size()==1&&x>=(pic_w)||alr.size()<0){
//					index=-1;
//				}
				
				if(y>((this.getHeight()-pic_h)/2)&&y<((this.getHeight()+pic_h)/2))
				{
					isSelect=true;
				}

//				if(y>=(SCREEN_HEIGHT-pic_h)/2&&y<=(SCREEN_HEIGHT+pic_h)/2)
//				{	
//					new Thread()
//					{
//						public void run()
//						{
//							double tempHf=Math.random();
//							hf=tempHf;
//							try 
//							{
//								Thread.sleep(1500);
//							} catch (InterruptedException e) 
//							{
//								e.printStackTrace();
//							}
//							//if(hf==tempHf&&(!isMove))
//							if(hf == tempHf)
//							{
////								isLongClick=true;
////								Bundle b=new Bundle();
////								b.putInt("index",index);
////								Message msg=new Message();
////								msg.what=4;
////								msg.setData(b);
////								((GraduateActivity)context).hd.sendMessage(msg);
//				
//								if(index > -1 && index < alr.size())
//								{
//									Record pa=alr.get(index);									
//									Constant.alAction=pa.alAction;
//									Constant.allActiongroup=pa.allActiongroup;
//									Constant.hmhb=pa.hmhb;
//									Constant.bmBuff=pa.bmResult;
//									
//									//Constant.SELECT=pa.SELECT;
//									((GraduateActivity)context).myMainView.setAppPictures(pa.SELECT);
//								}
//				
//								((GraduateActivity)context).hd.sendEmptyMessage(1);
//							}
//						}
//					}.start();
//				}
				
				return true;
			case MotionEvent.ACTION_MOVE:			
				if(isSelect && Math.abs(x-prex)>10 && Math.abs(y-prey) < Math.abs(x-prex))	
				{
					func = Func.Scroll;
				}
				else if(isSelect && Math.abs(y-prey)>10 && Math.abs(y-prey) > Math.abs(x-prex))
				{
					func = Func.Slide;
				}
				
				if(func == Func.Slide)
				{
					isSelect=false;
					yoffset = (int)(yoffset + y - prey);
					prex=x;							
					prey=y;
					this.postInvalidate();	
				}
				else if(func == Func.Scroll)	
				{
					isSelect=false;					
					xoffset=(int)(xoffset + x - prex);	
					prex=x;							
					prey=y;
					
					float dx=x-preXForV;
					currV=dx/2.0f;
					preXForV=x;
					
					this.postInvalidate();	
				}
				return true;
			case MotionEvent.ACTION_UP:		
				if(func == Func.Slide)
				{
					if(yoffset > SCREEN_HEIGHT / 4)
					{
						func = Func.Animate;
						
//						Bundle b=new Bundle();
//						b.putInt("index",index);
//						Message msg=new Message();
//						msg.what=4;
//						msg.setData(b);
//						((GraduateActivity)context).hd.sendMessage(msg);	

//						if(index > -1 && index < bitmaps.size())
//						{
//							bitmaps.remove(index);
//						}
					}
					yoffset = 0;
					this.postInvalidate();	
					break;
				}
				else if(isSelect && func == Func.None)
				{
					if(index > -1 && index < alr.size())
					{
						//Record pa=alr.get(index);	
						Record pa = Record.fromBytesToRecord(history.get(index));
						Constant.alAction=pa.alAction;
						Constant.allActiongroup=pa.allActiongroup;
						Constant.hmhb=pa.hmhb;
						Constant.bmBuff=pa.bmResult;
						
						//Constant.SELECT=pa.SELECT;
						((StartActivity)context).myMainView.setAppPictures(pa.SELECT);
					}
	
					((StartActivity)context).hd.sendEmptyMessage(1);
				}
				
				func = Func.None;
				hf=Math.random();
				acce=10;
				if(currV>0)
				{
					acce=-10;
				}
				isAutoGo=true;
				
//				new Thread()
//				{
//					public void run()
//					{
//						while(isAutoGo&&!isLongClick)
//						{
//							if(currV>=0&&acce>0||currV<=0&&acce<0)
//							{
//								break;
//							}
//							xoffset=xoffset+currV;
//							currV=currV+acce;							
//							postInvalidate();
//							
//							try 
//							{
//								Thread.sleep(40);
//							} catch (InterruptedException e) 
//							{
//								e.printStackTrace();
//							}
//						}
//					}
//				}.start();				
				
//				if(isSelect&&!isLongClick&&alr.size()>0&&index>=0)
//				{
//					hf=Math.random();
//					isLongClick=false;
//					Record pa=alr.get(index);
//					Constant.alAction=pa.alAction;
//					Constant.allActiongroup=pa.allActiongroup;
//					Constant.hmhb=pa.hmhb;
//					Constant.bmBuff=pa.bmResult;
//					Constant.SELECT=pa.SELECT;
//
//					((GraduateActivity)context).hd.sendEmptyMessage(3);		
//				}	
				
				// test
				if(func == Func.None && y < 30)
				{
					((StartActivity)context).hd.sendEmptyMessage(1);
				}
				
			return true;
		}
		return false;
	}
}
