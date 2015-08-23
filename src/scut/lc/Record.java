package scut.lc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Record
{
	LinkedHashSet<AtomAction> alAction;
	ArrayList<ActionGroup> allActiongroup;
	HashMap<Long,Bitmap> hmhb;
	Bitmap bmResult;
	List<MyBitmap> SELECT;
	
	public Record()
	{
		this.alAction=Constant.alAction;
		this.allActiongroup=Constant.allActiongroup;
		this.hmhb=Constant.hmhb;
		this.bmResult=Constant.bmBuff;
	}
	
	public void setSelect(List<MyBitmap> Select)
	{
		this.SELECT = Select;
	}
	
	public Record(LinkedHashSet<AtomAction> alAction,ArrayList<ActionGroup> allActiongroup,HashMap<Long,Bitmap> hmhb,Bitmap bmResult,ArrayList<MyBitmap> SELECT)
	{
		this.alAction=alAction;
		this.allActiongroup=allActiongroup;
		this.hmhb=hmhb;
		this.bmResult=bmResult;
		this.SELECT=SELECT;
	}
	
	public byte[] toBytes()
	{
		byte[] result=null;		
		  
		try 
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			ObjectOutputStream oout=new ObjectOutputStream(baos);
			oout.writeObject(this.alAction);
			oout.writeObject(this.allActiongroup);
			oout.writeObject(fromBitmapMapToBytesMap(hmhb));
			oout.writeObject(fromBitmapToBytes(bmResult));
			oout.writeObject(this.SELECT);
			
			result=baos.toByteArray();
			oout.close();
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Record fromBytesToRecord(byte[] data)
	{
		Record result=null;
		try 
		{
			ByteArrayInputStream bais=new ByteArrayInputStream(data);
			ObjectInputStream oin;
			oin = new ObjectInputStream(bais);			
			LinkedHashSet<AtomAction> alAction=(LinkedHashSet<AtomAction>)oin.readObject();
			ArrayList<ActionGroup> allActiongroup=(ArrayList<ActionGroup>)oin.readObject();
			HashMap<Long,byte[]>hmbb=(HashMap<Long,byte[]>)oin.readObject();
			HashMap<Long,Bitmap> hmhb=fromBytesMapToBitmapMap(hmbb);
			Bitmap bmResult=fromBytesToBitmap((byte[])oin.readObject());	
			ArrayList<MyBitmap> SELECT=(ArrayList<MyBitmap>)oin.readObject();
			
			result=new Record(alAction,allActiongroup,hmhb,bmResult,SELECT);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static HashMap<Long,byte[]> fromBitmapMapToBytesMap(HashMap<Long,Bitmap> hmhb)
	{
		Set<Long> ks=hmhb.keySet();
		HashMap<Long,byte[]> tm=new HashMap<Long,byte[]>();
		
		for(Long l:ks)
		{
			Bitmap bm=hmhb.get(l);
			byte[] data=fromBitmapToBytes(bm);
			tm.put(l, data);
		}
		return tm;
	}
	
	public static HashMap<Long,Bitmap> fromBytesMapToBitmapMap(HashMap<Long,byte[]> hmbb)
	{
		Set<Long> ks=hmbb.keySet();
		HashMap<Long,Bitmap> hmhb=new HashMap<Long,Bitmap>();
		
		for(Long l:ks)
		{
			byte[] data=hmbb.get(l);
			Bitmap bm=fromBytesToBitmap(data);
			hmhb.put(l, bm);
		}
		
		return hmhb;
	}
	
	public static byte[] fromBitmapToBytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();     
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);     
		return baos.toByteArray();   
	}
	
	public static Bitmap fromBytesToBitmap(byte[] data)
	{	
		if(data.length != 0)
		{   
		  Bitmap bm=BitmapFactory.decodeByteArray(data, 0, data.length);
		  if(bm == null)
		  {
			  throw new RuntimeException(data.length+"empty painting");
		  }
		  else
		  {
			  return bm;
		  }		  
		}   
		else 
		{   
			throw new RuntimeException("data.length==0----");
			//return null;   
		}   
	}
}
