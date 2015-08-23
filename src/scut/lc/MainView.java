package scut.lc;

import static scut.lc.Constant.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


enum STATE { NONE, DRAW, ERASER, PICTURE }

public class MainView extends SurfaceView implements SurfaceHolder.Callback {

	StartActivity Pictionary;
	MainViewDrawThread mainViewDrawThread;
	Paint paint;
	Bitmap background;  
	Bitmap ban; 
	
	Bitmap huabi[];
	Bitmap cuxi[];  
    Boolean yanse[];
    Boolean houdu[];
    
    Icon[] icons;
    MyBitmap current;
    STATE state;
    
    List<MyBitmap> app_pictures;
    ReadWriteLock lock_app_pictures = new ReentrantReadWriteLock();
	
    public void addAppPictures(MyBitmap my_bitmap)
    {
    	try {
    		lock_app_pictures.writeLock().lock();
		
    		app_pictures.add(my_bitmap);
		}
		finally
		{
			lock_app_pictures.writeLock().unlock();
		}
    }
    
    public void EmptyAppPictures()
    {
    	try {
    		lock_app_pictures.writeLock().lock();
		
    		app_pictures.clear();
		}
		finally
		{
			lock_app_pictures.writeLock().unlock();
		}
    }
    
    public void ModifyAppPicture(MyBitmap current, float x, float y)
    {
    	try {
    		lock_app_pictures.writeLock().lock();
		
			Bitmap b = Record.fromBytesToBitmap(current.data);
			current.setX(x-b.getWidth()/2);
			current.setY(y-b.getHeight()/2);
			
//			Iterator<MyBitmap> it = SELECT.iterator();
//			while(it.hasNext())
//			{
//				MyBitmap bit = it.next();
//				if(bit == current)
//				{
//					bit.setX(x-b.getWidth()/2);
//					bit.setY(y-b.getHeight()/2);
//					break;
//				}
//			}
		}
		finally
		{
			lock_app_pictures.writeLock().unlock();
		}
    }
    
    public void removeAppPictures(MyBitmap my_bitmap)
    {
    	try {
    		lock_app_pictures.writeLock().lock();
		
    		app_pictures.remove(my_bitmap);
		}
		finally
		{
			lock_app_pictures.writeLock().unlock();
		}
    }
    
    public void setAppPictures(List<MyBitmap> app_pictures)
    {
    	try {
    		lock_app_pictures.writeLock().lock();
    		
    		this.app_pictures.clear();
    		for(MyBitmap my_bitmap : app_pictures)
    		{
    			this.app_pictures.add(new MyBitmap(my_bitmap));
    		}
		}
		finally
		{
			lock_app_pictures.writeLock().unlock();
		}
    }
    
    public List<MyBitmap> getAppPictures()
	{
		try {
			lock_app_pictures.readLock().lock();
		
			// return a copy
			List<MyBitmap> temp = new ArrayList<MyBitmap>();
			for(MyBitmap my_bitmap : this.app_pictures)
			{
				temp.add(new MyBitmap(my_bitmap));
			}
			return temp;
		}
		finally
		{
			lock_app_pictures.readLock().unlock();
		}	
	}
    
    public MyBitmap whichAppPictures(int x, int y)
    {
    	try {
			lock_app_pictures.readLock().lock();
			
			for(int i = app_pictures.size() - 1; i > -1; i--)
			{
				if(app_pictures.get(i).trigger((int)x, (int)y))
				{
					return app_pictures.get(i);
				}
			}
			return null;
    	}
		finally
		{
			lock_app_pictures.readLock().unlock();
		}	
    }
    
	class Icon
	{
		int x;
		int y;
		int width;
		int height;
		Bitmap bitmap;
		
		public Icon(int x, int y, int width, int height, Bitmap srcPic)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.bitmap = srcPic;
		}
		
		public Point getCoordinate()
		{
			return new Point(this.x, this.y);
		}
		
		public Bitmap getBitmap()
		{
			return this.bitmap;
		}
		
		public boolean trigger(int x, int y)
		{
			if(x < this.x + width && x > this.x
				&& y < this.y + height && y > this.y)
			{
				return true;
			}
			return false;
		}
	}
	
	public MainView(StartActivity Pictionary) 
	{
		super(Pictionary);
		getHolder().addCallback(this);
		this.Pictionary = Pictionary;
		
		yanse = new Boolean [7];
		houdu = new Boolean [4];
		icons = new Icon[6];
		state = STATE.NONE;
		app_pictures = new ArrayList<MyBitmap>();
		
		initialize();
		drawAllAction(canvasBuff, paint);
	}
	
	public void initialize()
	{
		initIcons();
		initPaint();
		initYanse();
		initHoudu();
		initCards(Pictionary.getResources());
	}
	
	public void initIcons()
	{
		for(int i = 0; i < icons.length; i++)
		{
			icons[i] = new Icon(i * MAIN_AN_ARRAY[i].getWidth(), (int)SCREEN_HEIGHT - MAIN_AN_ARRAY[i].getHeight()
					, MAIN_AN_ARRAY[i].getWidth(), MAIN_AN_ARRAY[i].getHeight()
					, MAIN_AN_ARRAY[i]);
		}
	}
	
	public void initPaint()
	{
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setTextSize(18);
	}
	
	public void initYanse()
	{
		for(int i = 0; i < yanse.length; i++)
		{
			yanse[i] = false;
		}
	}
	
	public void initHoudu()
	{
		for(int i = 0; i < houdu.length; i++)
		{
			houdu[i] = false;
		}
	}
	
	public void initCards(Resources r)
	{
		Bitmap srcPic = PicLoadUtil.LoadBitmap(r, R.drawable.bg);
		background = PicLoadUtil.scaleToFit(srcPic, (int)SCREEN_WIDTH,(int)SCREEN_HEIGHT);
		
		Bitmap srcPic_2 = PicLoadUtil.LoadBitmap(r, R.drawable.work_grid_image_bg);
		ban = PicLoadUtil.scaleToFit(srcPic_2, (int)SCREEN_WIDTH * 5 / 6, (int)SCREEN_HEIGHT * 4 / 5);
		
		Bitmap srcPic_3 = PicLoadUtil.LoadBitmap(r, R.drawable.huabi);
		huabi = PicLoadUtil.splitPic(1, 7, srcPic_3, PAINT_WIDTH, PAINT_HEIGHT + MOVE_YOFFSET)[0];
		
		Bitmap srcPic_4 = PicLoadUtil.LoadBitmap(r, R.drawable.cuxi);
		cuxi = PicLoadUtil.splitPic(1, 4, srcPic_4, PAINT_WIDTH, PAINT_HEIGHT + MOVE_YOFFSET)[0];
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE); 
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.drawBitmap(ban, AREA_WIDTH/2 - ban.getWidth()/2, 10, paint);
		
		List<MyBitmap> temp = getAppPictures();
		for(int i = 0; i < temp.size(); i++)
		{
			Bitmap b = Record.fromBytesToBitmap(temp.get(i).data);
			
			canvas.save();
			
			//canvas.scale(rate, rate, SELECT.get(i).getX()+b.getWidth()/2, SELECT.get(i).getY()+b.getHeight()/2);
			canvas.drawBitmap(b, temp.get(i).getX(), temp.get(i).getY(), paint);
			
			canvas.restore();
		}
		
		if(state == STATE.DRAW)
		{
			for(int i = 0; i < huabi.length; i++)
			{
				if( yanse[i] == false )
				{
					canvas.drawBitmap(huabi[i], i*PAINT_WIDTH, AREA_HEIGHT, paint);
				}
				else
				{
					canvas.drawBitmap(huabi[i], i*PAINT_WIDTH, AREA_HEIGHT - MOVE_YOFFSET, paint);
				}
			}	
			
			for(int i = 0; i < cuxi.length; i++)
			{
				if( houdu[i] == false )
				{
					canvas.drawBitmap(cuxi[i], (i+7)*PAINT_WIDTH, AREA_HEIGHT, paint);
				}
				else
				{
					canvas.drawBitmap(cuxi[i], (i+7)*PAINT_WIDTH, AREA_HEIGHT - MOVE_YOFFSET, paint);
				}
			}	
		}
		else 
		{
			for(int i = 0; i < icons.length; i++)
			{
				canvas.drawBitmap(icons[i].getBitmap(), icons[i].getCoordinate().x, icons[i].getCoordinate().y, paint);	
			}
		}
		
		canvas.drawBitmap(bmBuff, 0, 0, paint);
	}

	public void drawAllAction(Canvas canvas,Paint paint)
	{
		Bitmap bmBuffTemp=Bitmap.createBitmap
		(
			(int)(ban.getWidth()),
			(int)(ban.getHeight()), 
			Bitmap.Config.ARGB_8888
		);		
		bmBuffTemp.eraseColor(Color.TRANSPARENT);
		Canvas canvasBuffTemp = new Canvas(bmBuffTemp); 
		synchronized(actionLock)
		{		
			for(AtomAction aa:alAction)
			{
				aa.drawSelf(canvasBuffTemp,paint);
			}
		}	
		if(bmBuff!=null)
		{
			bmBuff.recycle();
		}
		
		bmBuff=bmBuffTemp;
		canvasBuff=canvasBuffTemp;
	}
	
	public void drawSpecAction(Canvas canvas,Paint paint,AtomAction aa)
	{
		synchronized(actionLock)
		{			
			aa.drawSelf(canvas,paint);
		}
	}
	
	float xPre=-1;
	float yPre=-1;
	float actionXPre=-1;
	float actionYPre=-1;
	boolean moveFlag=false;
	int touchArea=-1;
	
	ActionGroup ag;
	
	@SuppressLint("WrongCall")
	public boolean onTouchEvent(MotionEvent event) 
	{
		float x = event.getX();
		float y = event.getY();

		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			xPre=x;
			yPre=y;
			moveFlag=false;
			
			if(y > AREA_HEIGHT )
			{
				if(state == STATE.DRAW)
				{
					if( x < 7*PAINT_WIDTH )
					{
						touchArea = 7;
					}
					else 
					{
						touchArea = 8;
					}
				}
				else
				{
					for( int i = 0; i < 6; i ++ )
					{
						if(icons[i].trigger((int)x, (int)y))
						{
							touchArea = i;
							break;
						}
					}
				}
			}
		    
			if((state == STATE.DRAW || state == STATE.ERASER)
					  && x>AREA_WIDTH/2 - ban.getWidth()/2 
					  && x<AREA_WIDTH/2 + ban.getWidth()/2 
					  && y>10 
					  && y<10 + ban.getHeight() )
			{
				touchArea=6;
				ag=new ActionGroup(hbSize,rgb);
			}
			
			if(current == null)
			{
				current = whichAppPictures((int)x, (int)y);
				if(current != null)
				{
					state = STATE.PICTURE;
				}
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if( Math.abs(x-xPre)>15 || Math.abs(y-yPre)>15 )
			{
				moveFlag=true;
			}
		
			if(touchArea==6 
					&& (state == STATE.DRAW || state == STATE.ERASER)
					&& x>AREA_WIDTH/2-ban.getWidth()/2 
					&& x<AREA_WIDTH/2+ban.getWidth()/2
					&& y>10
					&& y<10+ban.getHeight() * 9 / 10)
			{
				synchronized(actionLock)
				{
					if(actionXPre!=-1&&actionYPre!=-1)
					{
						if(state == STATE.DRAW)
						{
							AtomAction aa=new AtomAction(ActionType.TS,actionXPre,actionYPre,x,y,hbSize,ag.id,rgb);
							boolean flag=alAction.add(aa);
							if(flag)
							{
								ag.actionG.add(aa);	
								drawSpecAction(canvasBuff,paint,aa);
							}
						}
						else if(state == STATE.ERASER)
						{
							AtomAction aa=new AtomAction(ActionType.QS,actionXPre,actionYPre,x,y,hbSize,ag.id,rgb);
							boolean flag=alAction.add(aa);	
							if(flag)
							{
								ag.actionG.add(aa);	
								drawSpecAction(canvasBuff,paint,aa);
							}
						}
					}
				}	
				actionXPre=x;
				actionYPre=y;
			}
			
			if( state == STATE.PICTURE && y < AREA_HEIGHT && current != null)
			{	
				if(x < 10 || x > AREA_WIDTH - 10)
				{
					removeAppPictures(current);
					current = null;
					state = STATE.NONE;
				}
				else
				{
					ModifyAppPicture(current, x, y);
				}
				
				this.invalidate();
			}
			
			//test
//			if( isDraw && touchArea ==7 && y < AREA_HEIGHT){
//				touchArea = 1;
//			}
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			if(!moveFlag)
			{
				switch(touchArea)
				{
					case 0:  
						state = STATE.DRAW;
						break;
					case 1:
						state = STATE.ERASER;
						break;
					case 2:
						touchArea=-1;
						Pictionary.hd.sendEmptyMessage(0);
						break;
					case 3:
						alAction.clear();
						allActiongroup.clear();
						hmhb.clear();
						EmptyAppPictures();
						Pictionary.hd.sendEmptyMessage(3);
						break;
					case 4:    					
    					Record r = new Record();
    					r.setSelect(getAppPictures());
    					history.add(r.toBytes());
    					alr.add(r);
    					
    					Bitmap bitmap = Bitmap.createBitmap(this.getWidth(),
    				            this.getHeight(), Config.ARGB_8888);
    				    Canvas canvas = new Canvas(bitmap);
    				    this.onDraw(canvas);
    				    int width = ban.getWidth() - 60;
    				    int height = ban.getHeight() - 130;
    				    bitmap = Bitmap.createBitmap(bitmap, (int)AREA_WIDTH/2 - width/2, 30, width, height);
    				    bitmaps.add(bitmap);
    				    
    				    
    					//try {
    			    	//	File f=new File("/sdcard/sp.data");
    			    	//	FileOutputStream fout=new FileOutputStream(f);
    			    	//	ObjectOutputStream oout=new ObjectOutputStream(fout);
    			    	//	oout.writeObject(Constant.history);
    			    	//	oout.close();
    			    	//	fout.close();
    			    	//}
    			    	//catch(Exception e)
    			    	//{
    			    	//	e.printStackTrace();
    			    	//}
    					
    					Pictionary.hd.sendEmptyMessage(5);
						break;
					case 5:
						Pictionary.hd.sendEmptyMessage(2);
						break;
					case 6:
						break;
					case 7:
						int i = (int)x/PAINT_WIDTH;
						boolean yan = yanse[i];
						initYanse();
						yanse[i] = !yan;
						rgb=i;
						break;
					case 8:
						int j = (int)x/PAINT_WIDTH;
						boolean hou = houdu[j-7];
						initHoudu();
						houdu[j-7] = !hou;
				    	hbSize=2*(j-6);
				    	break;
				}
			}
			else if( y > AREA_HEIGHT && (touchArea == 7 || touchArea == 8) ){
				state = STATE.NONE;
				touchArea=-1;
			}
			else 
			{
				//state = STATE.NONE;
				allActiongroup.add(ag);	
			}
			
			moveFlag=false;	
			actionXPre=-1;	
			actionYPre=-1; 
			current = null;
		}
		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub
		mainViewDrawThread = new MainViewDrawThread(this);
		this.mainViewDrawThread.flag=true;
		mainViewDrawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub
		boolean flag = true;
		mainViewDrawThread.flag=false;
        while (flag) {
            try {
            	mainViewDrawThread.join();
            	flag = false;
            } 
            catch (Exception e) {
            	e.printStackTrace();
            }
        }
	}
	
//    public boolean onKeyDown(int keyCode,KeyEvent e)
//    {
//    	if(keyCode==4)
//    	{	
//    		if(isDraw || isEraser)
//    		{
//				synchronized(actionLock)
//				{
//					int delIndex=allActiongroup.size()-1;
//					if(delIndex>=0)
//					{
//						allActiongroup.remove(delIndex);
//						alAction.clear();
//						for(ActionGroup tag:allActiongroup)
//						{
//							for(AtomAction aa:tag.actionG)
//							{
//								alAction.add(aa);
//							}
//						}
//						drawAllAction(canvasBuff,paint);
//					}
//				}	
//    		}
//    	}
//    	return true;
//    }
	
}