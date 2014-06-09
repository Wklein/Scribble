package drawing;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drawingfun.R;

import drawing.DrawingView.StrokeType;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class MainActivity extends Activity implements OnClickListener {
	@SuppressWarnings("unused")
	private Context context;
	private DrawingView drawView;
	private float smallBrush, mediumBrush, largeBrush;
	private float ptsize1, ptsize2, ptsize3, ptsize4, ptsize5, ptsize6;
	private ImageButton currPaint;
	private Button undoBtn, redoBtn, newBtn, saveBtn, toolBtn, shapeBtn, sizeBtn, colorBtn, emailBtn;
	private int newBrushSize = 0, rVal = 0, gVal = 0, bVal = 0;
	private SeekBar sizeSeek, rSeek, gSeek, bSeek;
	private TextView currentSize, rView, gView, bView;
	private ImageView colorPreview, sizePreview;
	private Button okBtn, cancBtn, okBtn2, cancBtn2;
	private Drawable sizeDrawable;
	private ScaleDrawable sd;
	private int initialBrushSize = 20, initialR = 0, initialG = 0, initialB = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawView = (DrawingView)findViewById(R.id.drawing);

		//get the palette and first color button

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
		
		emailBtn = (Button)findViewById(R.id.email);
		emailBtn.setOnClickListener(this);
	}
	
	//user clicked paint
	public void paintClicked(View view){
		//use chosen color

		//set erase false
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			drawView.setColor(0xFF000000);
			//update ui
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	public void onClick(View v) {
		switch(v.getId()){
		case (R.id.newDraw):
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
			break;

		case (R.id.save):
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
			break;
			
		case (R.id.email):
			drawView.setDrawingCacheEnabled(true);
			String imgSaved = MediaStore.Images.Media.insertImage(
					getContentResolver(), drawView.getDrawingCache(),
					UUID.randomUUID().toString()+".png", "drawing");
	
			drawView.destroyDrawingCache();
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
			emailIntent.setType("image/*");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,""); 
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ""); 
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgSaved));
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			
			break;
		
		case (R.id.undo):
			drawView.undo();
			break;
		
		case (R.id.redo):
			drawView.redo();
			break;
		
		case (R.id.tools):
			//Creating the instance of PopupMenu  
			PopupMenu popup = new PopupMenu(MainActivity.this, toolBtn);  
			//Inflating the Popup using xml file  
			popup.getMenuInflater().inflate(R.menu.tools_popup, popup.getMenu());  

			//registering popup with OnMenuItemClickListener  
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {  
					Toast.makeText(MainActivity.this, item.getTitle(),Toast.LENGTH_SHORT).show();  
					
					if(item.getTitle().equals("Eraser")) {
						drawView.setType(StrokeType.ERASER);
						drawView.setFilled(false);
					}
					else if(item.getTitle().equals("Brush")) {
						
						drawView.setType(StrokeType.BRUSH);
						drawView.setFilled(false);
					}
					
					return true;  
				}
			});  
			popup.show();//showing popup menu  
			break;
			
		case (R.id.shapes):
			popup = new PopupMenu(MainActivity.this, shapeBtn);
			popup.getMenuInflater().inflate(R.menu.shapes_popup, popup.getMenu());
			
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					
					//Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
					
					if(item.getTitle().equals("Rectangle")) {
						
						drawView.setType(StrokeType.RECTANGLE);
						drawView.setFilled(false);
					}
					else if(item.getTitle().equals("Circle")) {
						
						drawView.setType(StrokeType.CIRCLE);
						drawView.setFilled(false);
					}
					else if(item.getTitle().equals("Filled Circle")) {
						
						drawView.setType(StrokeType.CIRCLE);
						drawView.setFilled(true);
					}
					else if(item.getTitle().equals("Filled Rectangle")) {
						
						drawView.setType(StrokeType.RECTANGLE);
						drawView.setFilled(true);
					}
					else if(item.getTitle().equals("Line")) {
						
						drawView.setType(StrokeType.LINE);
						drawView.setFilled(false);
					}
					
					return true;
				}
			});
			popup.show();
			break;
			
		case (R.id.sizes):
				final Dialog sizeDialog = new Dialog(this);
				sizeDialog.setTitle("BRUSH SIZE");
				LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View sizeLayout = inflater.inflate(R.layout.size_chooser, (ViewGroup)findViewById(R.id.sizeDialogSetup));
				sizeDialog.setContentView(sizeLayout);
				
				//sizePreview = (ImageView)sizeLayout.findViewById(R.id.sizePreview);
				//sizeDrawable = (GradientDrawable)getResources().getDrawable(R.drawable.sizer);

				newBrushSize = (int) drawView.getLastBrushSize();
				sizeSeek = (SeekBar)sizeLayout.findViewById(R.id.sizeSeeker);
				//initial brush value size
				sizeSeek.setProgress(initialBrushSize);
				currentSize = (TextView)sizeLayout.findViewById(R.id.currentSize);
				currentSize.setText("Brush Size: " + initialBrushSize);
				okBtn = (Button)sizeLayout.findViewById(R.id.okSize);
				cancBtn = (Button)sizeLayout.findViewById(R.id.cancSize);
				
				OnSeekBarChangeListener yourSeekBarListener = new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						newBrushSize = seekBar.getProgress();
						//drawView.setLastBrushSize(newBrushSize);
						//sizePreview.getDrawable().setBounds(100 - newBrushSize, 50 - newBrushSize, 100 + newBrushSize, 50 + newBrushSize);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of the previous value.
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						// change progress text label with current seekbar value
						currentSize.setText("Brush Size: " + progress);
						//sizePreview.getDrawable().setBounds(100 - progress, 50 - progress, 100 + progress, 50 + progress);
					}
				};
				sizeSeek.setOnSeekBarChangeListener(yourSeekBarListener);
				
				okBtn.setOnClickListener(new OnClickListener(){
				    @Override
				    public void onClick(View v) {
				    	initialBrushSize = newBrushSize;
				    	drawView.setBrushSize(newBrushSize);
				    	Toast.makeText(MainActivity.this, "OK. " + "New Brush Size: " + newBrushSize, Toast.LENGTH_SHORT).show();
				        sizeDialog.dismiss();
				    }
				});
				
				cancBtn.setOnClickListener(new OnClickListener(){
				    @Override
				    public void onClick(View v) {
				    	newBrushSize = initialBrushSize;
				    	Toast.makeText(MainActivity.this, "Cancelled Brush Size Change", Toast.LENGTH_SHORT).show();
				        sizeDialog.dismiss();
				    }
				});

				sizeDialog.show();
			break;
			
		case (R.id.colors):				
				final Dialog colorDialog = new Dialog(this);
				colorDialog.setTitle("BRUSH COLOR");
				LayoutInflater inflater2 = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View colorLayout = inflater2.inflate(R.layout.color_chooser, (ViewGroup)findViewById(R.id.colorDialogSetup));
				colorDialog.setContentView(colorLayout);
				
				//initial color values to keep
				initialR = rVal;
				initialG = gVal;
				initialB = bVal;
				
				colorPreview = (ImageView)colorLayout.findViewById(R.id.colorPreview);
				colorPreview.setColorFilter(0xFF000000 + rVal * 65536 + gVal * 256 + bVal);

				rSeek = (SeekBar)colorLayout.findViewById(R.id.colorSeek1);
				gSeek = (SeekBar)colorLayout.findViewById(R.id.colorSeek2);
				bSeek = (SeekBar)colorLayout.findViewById(R.id.colorSeek3);
				
				rView = (TextView)colorLayout.findViewById(R.id.redC);
				rView.setText("RED: " + initialR);
				rView.setWidth(100);
				gView = (TextView)colorLayout.findViewById(R.id.greenC);
				gView.setText("GREEN: " + initialG);
				gView.setWidth(100);
				bView = (TextView)colorLayout.findViewById(R.id.blueC);
				bView.setText("BLUE: " + initialB);
				bView.setWidth(100);
				
				bSeek.setProgress(initialB);
				gSeek.setProgress(initialG);
				rSeek.setProgress(initialR);
				
				okBtn2 = (Button)colorLayout.findViewById(R.id.okColor);
				cancBtn2 = (Button)colorLayout.findViewById(R.id.cancColor);
				
				
				OnSeekBarChangeListener redSeekBarListener = new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						rVal = seekBar.getProgress();
						//drawView.setColor(0xFF000000 + rVal * 65536 + gVal * 256 + bVal);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						seekBar.setSecondaryProgress(seekBar.getProgress());
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

						rView.setText("RED: " + progress);
						colorPreview.setColorFilter(0xFF000000 + progress * 65536 + gVal * 256 + bVal);
					}
				};
				rSeek.setOnSeekBarChangeListener(redSeekBarListener);
				
				
				OnSeekBarChangeListener greenSeekBarListener = new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						gVal = seekBar.getProgress();
						//drawView.setColor(0xFF000000 + rVal * 65536 + gVal * 256 + bVal);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						seekBar.setSecondaryProgress(seekBar.getProgress());
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						gView.setText("Green: " + progress);
						colorPreview.setColorFilter(0xFF000000 + rVal * 65536 + progress * 256 + bVal);
					}
				};
				gSeek.setOnSeekBarChangeListener(greenSeekBarListener);
				
				
				OnSeekBarChangeListener blueSeekBarListener = new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						bVal = seekBar.getProgress();
						//drawView.setColor(0xFF000000 + rVal * 65536 + gVal * 256 + bVal);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						seekBar.setSecondaryProgress(seekBar.getProgress());
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						bView.setText("Blue: " + progress);
						colorPreview.setColorFilter(0xFF000000 + rVal * 65536 + gVal * 256 + progress);
					}
				};
				bSeek.setOnSeekBarChangeListener(blueSeekBarListener);
				
				
				okBtn2.setOnClickListener(new OnClickListener(){
				    @Override
				    public void onClick(View v) {
				    	initialR = rVal;
				    	initialG = gVal;
				    	initialB = bVal;
				    	drawView.setColor(0xFF000000 + rVal * 65536 + gVal * 256 + bVal);
				    	Toast.makeText(MainActivity.this, "New Color Is Set", Toast.LENGTH_SHORT).show();
				        colorDialog.dismiss();
				    }
				});
				
				cancBtn2.setOnClickListener(new OnClickListener(){
				    @Override
				    public void onClick(View v) {
				    	rVal = initialR;
				    	gVal = initialG;
				    	bVal = initialB;
				    	Toast.makeText(MainActivity.this, "Cancelled Color Change", Toast.LENGTH_SHORT).show();
				        colorDialog.dismiss();
				    }
				});

				colorDialog.show();
			break;
		}
	}
}
