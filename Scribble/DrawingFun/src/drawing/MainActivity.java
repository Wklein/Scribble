package drawing;

import java.util.ArrayList;
import java.util.UUID;

import com.example.drawingfun.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private ExpandableListView mExpandableList;
	private Context context;
	private DrawingView drawView;
	private float smallBrush, mediumBrush, largeBrush;
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mExpandableList = (ExpandableListView)findViewById(R.id.expandable_list);
		ArrayList<Parent> arrayParents = new ArrayList<Parent>();
		ArrayList<String> arrayChildren = new ArrayList<String>();
		
		Parent parent = new Parent();
		parent.setTitle("Tool");
		
		arrayChildren = new ArrayList<String>();
		arrayChildren.add("test1");
		arrayChildren.add("test2");
		arrayChildren.add("test3");
		
		parent.setArrayChildren(arrayChildren);
		arrayParents.add(parent);
		
		mExpandableList.setAdapter(new CustomAdapter(MainActivity.this, arrayParents));
		mExpandableList.expandGroup(0);
		
		drawView = (DrawingView)findViewById(R.id.drawing);
		
		//get the palette and first color button
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
		
		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		
		//drawBtn = (ImageButton)findViewById(R.id.draw_btn);
		//drawBtn.setOnClickListener(this);
		
		//top menus
		TextView nw = (TextView)findViewById(R.id.newDraw);
		TextView un = (TextView)findViewById(R.id.undo);
		TextView re = (TextView)findViewById(R.id.redo);
		TextView sav = (TextView)findViewById(R.id.save);
		nw.setGravity(Gravity.CENTER_HORIZONTAL);
		un.setGravity(Gravity.CENTER_HORIZONTAL);
		re.setGravity(Gravity.CENTER_HORIZONTAL);
		sav.setGravity(Gravity.CENTER_HORIZONTAL);
		nw.setOnClickListener(this);
		un.setOnClickListener(this);
		re.setOnClickListener(this);
		sav.setOnClickListener(this);
		
		//bottom menus
		TextView ts = (TextView)findViewById(R.id.tools);
		TextView sha = (TextView)findViewById(R.id.shapes);
		TextView siz = (TextView)findViewById(R.id.sizes);
		TextView col = (TextView)findViewById(R.id.colors);
		ts.setGravity(Gravity.CENTER_HORIZONTAL);
		sha.setGravity(Gravity.CENTER_HORIZONTAL);
		siz.setGravity(Gravity.CENTER_HORIZONTAL);
		col.setGravity(Gravity.CENTER_HORIZONTAL);
		ts.setOnClickListener(this);
		sha.setOnClickListener(this);
		siz.setOnClickListener(this);
		col.setOnClickListener(this);
		
		
		
		
		
		
		
		
		
		
		
		drawView.setBrushSize(mediumBrush);
		
		//eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
		//eraseBtn.setOnClickListener(this);
		
		//newBtn = (ImageButton)findViewById(R.id.new_btn);
		//newBtn.setOnClickListener(this);
		
		//saveBtn = (ImageButton)findViewById(R.id.save_btn);
		//saveBtn.setOnClickListener(this);
	}
	
	//user clicked paint
	public void paintClicked(View view){
		//use chosen color

		//set erase false
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			//update ui
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.newDraw){
			TextView nw = (TextView)findViewById(R.id.newDraw);
			//nw.setTextColor(0xFF00FF00);
			nw.setBackgroundColor(Color.RED);
		    //start new drawing
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	TextView nw = (TextView)findViewById(R.id.newDraw);
			        nw.setBackgroundColor(Color.BLACK);
			        drawView.startNew();
			        dialog.dismiss();
			    }
			});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	TextView nw = (TextView)findViewById(R.id.newDraw);
			        nw.setBackgroundColor(Color.BLACK);
			        dialog.cancel();
			    }
			});
			newDialog.show();
		}
		
		else if(v.getId() == R.id.save){
            //save drawing
			TextView sav = (TextView)findViewById(R.id.save);
			sav.setBackgroundColor(Color.rgb(0, 200, 0));
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        //save drawing
			    	drawView.setDrawingCacheEnabled(true);
			    	String imgSaved = MediaStore.Images.Media.insertImage(
			    		    getContentResolver(), drawView.getDrawingCache(),
			    		    UUID.randomUUID().toString()+".png", "drawing");
			    	
			    	if(imgSaved!=null){
			    	    Toast savedToast = Toast.makeText(getApplicationContext(),
			    	        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
			    	    savedToast.show();
						TextView sav = (TextView)findViewById(R.id.save);
						sav.setBackgroundColor(Color.BLACK);
			    	}
			    	else{
			    	    Toast unsavedToast = Toast.makeText(getApplicationContext(),
			    	        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
			    	    unsavedToast.show();
						TextView sav = (TextView)findViewById(R.id.save);
						sav.setBackgroundColor(Color.BLACK);
			    	}
			    	
			    	drawView.destroyDrawingCache();
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
					TextView sav = (TextView)findViewById(R.id.save);
					sav.setBackgroundColor(Color.BLACK);
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
	}
	
	/*
	@Override
	public void onClick(View view) {
		if(view.getId()==R.id.draw_btn){
		    //draw button clicked			
			//drawView.setErase(false);
			//drawView.setBrushSize(drawView.getLastBrushSize());
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Brush size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(smallBrush);
			        drawView.setLastBrushSize(smallBrush);
			        drawView.setErase(false);
			        brushDialog.dismiss();
			    }
			});
			
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(mediumBrush);
			        drawView.setLastBrushSize(mediumBrush);
			        drawView.setErase(false);
			        brushDialog.dismiss();
			    }
			});
			 
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setBrushSize(largeBrush);
			        drawView.setLastBrushSize(largeBrush);
			        drawView.setErase(false);
			        brushDialog.dismiss();
			    }
			});
			
			brushDialog.show();
		}
		
		else if(view.getId()==R.id.erase_btn){
		    //switch to erase - choose size
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle("Eraser size:");
			brushDialog.setContentView(R.layout.brush_chooser);
			
			ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(smallBrush);
			        brushDialog.dismiss();
			    }
			});
			
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(mediumBrush);
			        brushDialog.dismiss();
			    }
			});
			
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
			    @Override
			    public void onClick(View v) {
			        drawView.setErase(true);
			        drawView.setBrushSize(largeBrush);
			        brushDialog.dismiss();
			    }
			});
			
			brushDialog.show();
		}
		
		else if(view.getId()==R.id.new_btn){
		    //new button
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New drawing");
			newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        drawView.startNew();
			        dialog.dismiss();
			    }
			});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			newDialog.show();
		}
		
		else if(view.getId()==R.id.save_btn){
            //save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        //save drawing
			    	drawView.setDrawingCacheEnabled(true);
			    	String imgSaved = MediaStore.Images.Media.insertImage(
			    		    getContentResolver(), drawView.getDrawingCache(),
			    		    UUID.randomUUID().toString()+".png", "drawing");
			    	
			    	if(imgSaved!=null){
			    	    Toast savedToast = Toast.makeText(getApplicationContext(),
			    	        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
			    	    savedToast.show();
			    	}
			    	else{
			    	    Toast unsavedToast = Toast.makeText(getApplicationContext(),
			    	        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
			    	    unsavedToast.show();
			    	}
			    	
			    	drawView.destroyDrawingCache();
			    }
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
		*/
	//}
	
}
