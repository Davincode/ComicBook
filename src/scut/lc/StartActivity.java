package scut.lc;

import static scut.lc.Constant.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class StartActivity extends Activity {

	enum WhichView { MAIN_VIEW, EXPAND_VIEW, GALLERY_VIEW }
	
	protected MainView myMainView;
	protected ShowGalleryView myGalleryView;
	
	private WhichView current_view;	
	private Dialog nameInputdialog; 
	
	public Handler hd = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case 0:
					gotoExpandView();
					break;
				case 1:
					gotoMainView();
					break;
				case 2:
					gotoGalleryView();
					break;
				case 3:
					if(myMainView != null)
					{
						myMainView = null;
					}
					gotoMainView();
					break;
				case 4:
//					Bundle b = msg.getData();
//					int index = b.getInt("index");
//					if(index > -1 && index < bitmaps.size())
//					{
//						bitmaps.remove(b.getInt("index"));
//					}
					//history.remove(b.getInt("index"));
					break;
				case 5:
					//showDialog(NAME_INPUT_DIALOG_ID);
					break;
			}
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);         
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        if(dm.widthPixels > dm.heightPixels)
        {
         	 SCREEN_WIDTH = dm.heightPixels;
             SCREEN_HEIGHT = dm.widthPixels;
        }
        else
        {
       	    SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
        }
		Constant.getRatio();				
		initPicture(this.getResources());
		
		gotoMainView();
    }
    
    public void gotoMainView()
    {
    	if(myMainView == null)
    	{
    		myMainView = new MainView(StartActivity.this);
    	}
    	StartActivity.this.setContentView(myMainView);
    	current_view=WhichView.MAIN_VIEW;
    } 
    
	public void gotoGalleryView()
	{
		myGalleryView = (ShowGalleryView)findViewById(R.id.sgView);
		StartActivity.this.setContentView(R.layout.showgallerymain);
		current_view = WhichView.GALLERY_VIEW;
	}
    
    public Dialog onCreateDialog(int id)
    {    	
        Dialog result=null;
    	switch(id)
    	{
    		case NAME_INPUT_DIALOG_ID:
    			nameInputdialog=new MyDialog(this); 	    
    			result=nameInputdialog;				
    			break;	
    		case DELETE_DIALOG_ID:
    			Builder b=new AlertDialog.Builder(this);  
    			b.setMessage("Want to delete?");
    			b.setPositiveButton
    			(
    				"confirm", 
    				new DialogInterface.OnClickListener()
	        		{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(StartActivity.this, "deleted", Toast.LENGTH_SHORT).show();					
							gotoGalleryView();
						}      			
	        		}
    			);
    			b.setNegativeButton
    			(
    				"cancel",
    				new DialogInterface.OnClickListener()
    				{
    					public void onClick(DialogInterface dialog, int which){}
					}
    			);
    			result=b.create();
    		break;
    	}
		return result;
    }
    
    public void onPrepareDialog(int id, final Dialog dialog)
    {
    	switch(id)
    	{
    	   case NAME_INPUT_DIALOG_ID:
    		   
    		   Button bjhmcok=(Button)nameInputdialog.findViewById(R.id.saveOk);
       		   Button bjhmccancel=(Button)nameInputdialog.findViewById(R.id.saveCancle);
       		   
       		   bjhmcok.setOnClickListener
               (
    	          new OnClickListener()
    	          {
    				@Override
    				public void onClick(View v) 
    				{
    					EditText et=(EditText)nameInputdialog.findViewById(R.id.etname);
    					String name=et.getText().toString();    					
    					//Record r=new Record(); 
    					//history.add(r.toBytes());
    					
//    					try
//    			    	{
//        			    	File f=new File("/sdcard/sp.data");
//        			    	FileOutputStream fout=new FileOutputStream(f);
//        			    	ObjectOutputStream oout=new ObjectOutputStream(fout);
//        			    	oout.writeObject(Constant.history);
//        			    	oout.close();
//        			    	fout.close();
//    			    	}
//    			    	catch(Exception e)
//    			    	{
//    			    		e.printStackTrace();
//    			    	}
    					
    					nameInputdialog.cancel();
    				}        	  
    	          }
    	        );   
       		    
       		    bjhmccancel.setOnClickListener(new OnClickListener() 
       		    {
		 				@Override
		 				public void onClick(View v) 
		 				{
		 					nameInputdialog.cancel();					
		 				}        	  
	 	        });   
       		    break;	
    	}
    }    
    
    public void gotoExpandView(){
		setContentView(R.layout.expand_view);
		current_view = WhichView.EXPAND_VIEW;
		
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.list);
		expandableListView.setAdapter(new BaseExpandableListAdapter() {
			TextView getTextView() {
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
				TextView textView = new TextView(StartActivity.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(36, 0, 0, 0);
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				return textView;
			}

			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return generalsTypes.length;
			}

			@Override
			public Object getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return generalsTypes[groupPosition];
			}

			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return generals[groupPosition].length;
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return generals[groupPosition][childPosition];
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childPosition;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(StartActivity.this);
				ll.setOrientation(0);
				
				ImageView logo = new ImageView(StartActivity.this);
				logo.setImageResource(logos[groupPosition]);
				logo.setPadding(50, 0, 0, 0);
				ll.addView(logo);
				
				TextView textView = getTextView();
				textView.setTextColor(Color.BLACK);
				textView.setText(getGroup(groupPosition).toString());
				ll.addView(textView);

				return ll;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LinearLayout ll = new LinearLayout(StartActivity.this);
				ll.setOrientation(0);
				
				ImageView generallogo = new ImageView(StartActivity.this);
				//generallogo.setImageResource(generallogos[groupPosition][childPosition]);
				
				Bitmap srcPic = PicLoadUtil.LoadBitmap(getResources(), generallogos[groupPosition][childPosition]);
				Bitmap child = PicLoadUtil.scaleToFit(srcPic, (int)SCREEN_WIDTH / 5, (int)SCREEN_WIDTH / 5);
				generallogo.setImageBitmap(child);
				ll.addView(generallogo);
				
				TextView textView = getTextView();
				textView.setText(getChild(groupPosition, childPosition).toString());
				ll.addView(textView);
				
				return ll;
			}

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				// TODO Auto-generated method stub
				return true;
			}

		});
	
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				MyBitmap my_bitmap = new MyBitmap(Pic_AN_ARRAY[groupPosition][childPosition]);
				myMainView.addAppPictures(my_bitmap);
				
				hd.sendEmptyMessage(1);
				return false;
			}
		});
    }

}