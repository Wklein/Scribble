package drawing;

import java.util.UUID;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.drawingfun.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class MainActivity extends Activity implements OnClickListener {
	private Context context;
	private DrawingView drawView;
	private float smallBrush, mediumBrush, largeBrush;
	private float ptsize1, ptsize2, ptsize3, ptsize4, ptsize5, ptsize6;
	private ImageButton currPaint, drawBtn, eraseBtn;
	private Button undoBtn, redoBtn, newBtn, saveBtn, toolBtn, shapeBtn, sizeBtn, colorBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawView = (DrawingView)findViewById(R.id.drawing);

		//get the palette and first color button
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);


		drawView.setBrushSize(mediumBrush);


		newBtn = (Button)findViewById(R.id.newDraw);
		newBtn.setOnClickListener(this);

		undoBtn = (Button)findViewById(R.id.undo);
		undoBtn.setOnClickListener(this);

		redoBtn = (Button)findViewById(R.id.redo);
		redoBtn.setOnClickListener(this);

		saveBtn = (Button)findViewById(R.id.save);
		saveBtn.setOnClickListener(this);

		toolBtn = (Button)findViewById(R.id.tools);
		toolBtn.setOnClickListener(this);

		shapeBtn = (Button)findViewById(R.id.shapes);
		shapeBtn.setOnClickListener(this);

		sizeBtn = (Button)findViewById(R.id.sizes);
		sizeBtn.setOnClickListener(this);

		colorBtn = (Button)findViewById(R.id.colors);
		colorBtn.setOnClickListener(this);
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	public void onClick(View v) {
		if(v.getId() == R.id.newDraw){
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

		else if(v.getId() == R.id.save){
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
		else if(v.getId() == R.id.undo){
			Toast.makeText(getApplicationContext(),
					"UNDO", Toast.LENGTH_SHORT).show();
			drawView.undo();

		}
		else if(v.getId() == R.id.redo){
			Toast.makeText(getApplicationContext(),
					"REDO", Toast.LENGTH_SHORT).show();
			drawView.redo();
		}
		else if (v.getId() == R.id.tools) {
			//Creating the instance of PopupMenu  
			PopupMenu popup = new PopupMenu(MainActivity.this, toolBtn);  
			//Inflating the Popup using xml file  
			popup.getMenuInflater().inflate(R.menu.tools_popup, popup.getMenu());  

			//registering popup with OnMenuItemClickListener  
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {  
					Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
					return true;  
				}
			});  
			popup.show();//showing popup menu  
		}
		else if (v.getId() == R.id.shapes) {
			PopupMenu popup = new PopupMenu(MainActivity.this, shapeBtn);
			popup.getMenuInflater().inflate(R.menu.shapes_popup, popup.getMenu());
			
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {  
					Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
					return true;  
				}
			});
			popup.show();
		}
		else if (v.getId() == R.id.sizes) {
			PopupMenu popup = new PopupMenu(MainActivity.this, sizeBtn);
			popup.getMenuInflater().inflate(R.menu.sizes_popup, popup.getMenu());
			
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					
					Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
					
					if(item.getTitle().equals("1")) {
						ptsize1 = getResources().getInteger(R.integer.ptsize1);
						drawView.setBrushSize(ptsize1);
					}
					else if(item.getTitle().equals("2")) {
						ptsize2 = getResources().getInteger(R.integer.ptsize3);
						drawView.setBrushSize(ptsize2);
					}
					else if(item.getTitle().equals("3")) {
						ptsize3 = getResources().getInteger(R.integer.ptsize5);
						drawView.setBrushSize(ptsize3);
					}
					else if(item.getTitle().equals("4")) {
						ptsize4 = getResources().getInteger(R.integer.ptsize10);
						drawView.setBrushSize(ptsize4);
					}
					else if(item.getTitle().equals("5")) {
						ptsize5 = getResources().getInteger(R.integer.ptsize20);
						drawView.setBrushSize(ptsize5);
					}
					else if(item.getTitle().equals("6")) {
						ptsize6 = getResources().getInteger(R.integer.ptsize30);
						drawView.setBrushSize(ptsize6);
					}
					
					return true;
				}
			});
			popup.show();
		}
		else if (v.getId() == R.id.colors) {
			PopupMenu popup = new PopupMenu(MainActivity.this, colorBtn);
			popup.getMenuInflater().inflate(R.menu.colors_popup, popup.getMenu());
			
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {  
					Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
					String selectedColor;
					
					if (item.getTitle().equals("Black")) {
						selectedColor = "#FF000000";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("White")) {
						selectedColor = "#FFFFFFFF";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("Red")) {
						selectedColor = "#FFFF0000";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("Orange")) {
						selectedColor = "#FFFF6600";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("Yellow")) {
						selectedColor = "#FFFFCC00";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("Green")) {
						selectedColor = "#FF009900";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("Blue")) {
						selectedColor = "#FF0000FF";
						drawView.setColor(selectedColor);
					}
					else if (item.getTitle().equals("Purple")) {
						selectedColor = "#FF990099";
						drawView.setColor(selectedColor);
					}
					
					return true;
				}
			});
			popup.show();
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
