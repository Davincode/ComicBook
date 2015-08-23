package scut.lc;

import static scut.lc.Constant.*;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

public class MyBitmap implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	byte[] data;
	float x;
	float y;
	int width;
	int height;

	public MyBitmap(Bitmap bitmap)
	{
		data = fromBitmapToBytes(bitmap);
		x = AREA_WIDTH/2 - bitmap.getWidth()/2;
		y = AREA_HEIGHT/2 - bitmap.getHeight()/2;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}
	
	// Deep copy
	public MyBitmap(MyBitmap my_bitmap)
	{
		byte[] temp = my_bitmap.getData();
		data = new byte[temp.length];
		for(int i = 0; i < temp.length; i++)
		{
			data[i] = temp[i];
		}
		x = my_bitmap.getX();
		y = my_bitmap.getY();
		width = my_bitmap.getWidth();
		height = my_bitmap.getHeight();
	}

	public byte[] fromBitmapToBytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();     
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);     
		return baos.toByteArray();   
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
	
	public float getX() 
	{
		return x;
	}

	public void setX(float x) 
	{
		this.x = x;
	}

	public float getY() 
	{
		return y;
	}

	public void setY(float y) 
	{
		this.y = y;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
