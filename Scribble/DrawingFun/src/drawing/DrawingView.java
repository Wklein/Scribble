package drawing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;



public class DrawingView extends View{
	//drawing path
	private Path drawPath;
	//drawing and canvas paint
	private Paint drawPaint, canvasPaint, erasePaint;
	//initial color
	private int paintColor = 0xFF000000;
	//canvas
	private Canvas drawCanvas;
	//canvas bitmap
	private Bitmap canvasBitmap;
	
	private List<DrawingStroke> drawing;
	private StrokeType type;
	private int strokes;
	private float[][] coords;
	
	private float brushSize, lastBrushSize;
	private boolean erase = false;
	
	public enum StrokeType {
		BRUSH,
		RECTANGLE,
		CIRCLE,
		LINE,
		ERASER;
		
	}
	
	public DrawingView(Context context, AttributeSet attrs){
	    super(context, attrs);
	    setupDrawing();
	}
	
	private void setupDrawing(){
		
		
		brushSize = 20;//getResources().getInteger(R.integer.medium_size);
		lastBrushSize = brushSize;
		
		//get drawing area setup for interaction
		coords = new float[2][2];
		drawing = new ArrayList<DrawingStroke>();
		strokes = 0;
		type = StrokeType.BRUSH;
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(brushSize);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		erasePaint = new Paint(drawPaint);
		erasePaint.setColor(0xFFFFFFFF);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//draw view
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		if(type == StrokeType.ERASER){
			canvas.drawPath(drawPath, erasePaint);
		}else{
			canvas.drawPath(drawPath, drawPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//detect user touch
		float touchX = event.getX();
		float touchY = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    downAction(touchX, touchY);
		    break;
		case MotionEvent.ACTION_MOVE:
			moveAction(touchX, touchY);
		    break;
		case MotionEvent.ACTION_UP:
			strokes++;
			upAction(touchX, touchY);
			if(type == StrokeType.ERASER){
				Paint tmp = new Paint(drawPaint);
				tmp.setColor(0xFFFFFFFF);
				drawCanvas.drawPath(drawPath, tmp);
				drawing.add(new DrawingStroke(new Path(drawPath), new Paint(tmp), type));
			}else{
				drawCanvas.drawPath(drawPath, drawPaint);
				drawing.add(new DrawingStroke(new Path(drawPath), new Paint(drawPaint), type));
			}
		    if(strokes < drawing.size())
		    	drawing = drawing.subList(0, strokes - 1);
		    
		    drawPath.reset();
		    break;
		default:
		    return false;
		}
		
		invalidate();
		return true;
	}
	
	public void downAction(float x, float y){
		switch (type) {
		case LINE:
			coords[0][0] = x;
			coords[0][1] = y;
			break;
		case RECTANGLE:
			coords[0][0] = x;
			coords[0][1] = y;
			break;
		case CIRCLE:
			coords[0][0] = x;
			coords[0][1] = y;
			break;
		case BRUSH:
			drawPath.moveTo(x, y);
			break;
		case ERASER:
			erasePaint = new Paint(drawPaint);
			erasePaint.setColor(0xFFFFFFFF);
			drawPath.moveTo(x, y);
			break;
		default:
			break;
		}
	}
	
	public void moveAction(float x, float y){
		switch (type) {
		case LINE:
			drawPath.reset();
			coords[1][0] = x;
			coords[1][1] = y;
			drawPath.moveTo(coords[0][0], coords[0][1]);
			drawPath.lineTo(coords[1][0], coords[1][1]);
			break;
		case RECTANGLE:
			coords[1][0] = x;
			coords[1][1] = y;
			drawPath.reset();
			drawPath.addRect(Math.min(coords[0][0], coords[1][0]), Math.min(coords[0][1], coords[1][1]), Math.max(coords[0][0], coords[1][0]), Math.max(coords[0][1], coords[1][1]), Direction.CW );
			break;
		case CIRCLE:
			coords[1][0] = x;
			coords[1][1] = y;
			RectF tmp = new RectF(Math.min(coords[0][0], coords[1][0]), Math.min(coords[0][1], coords[1][1]), Math.max(coords[0][0], coords[1][0]), Math.max(coords[0][1], coords[1][1]));
			drawPath.reset();
			drawPath.addOval(tmp, Direction.CW );
			break;
		case BRUSH:
			drawPath.lineTo(x, y);
			break;
		case ERASER:
			drawPath.lineTo(x, y);
			break;
		default:
			break;
		}
	}

	public void upAction(float x, float y){
		switch (type) {
		case LINE:
			drawPath.reset();
			coords[1][0] = x;
			coords[1][1] = y;
			drawPath.moveTo(coords[0][0], coords[0][1]);
			drawPath.lineTo(coords[1][0], coords[1][1]);
			break;
		case RECTANGLE:
			coords[1][0] = x;
			coords[1][1] = y;
			drawPath.reset();
			drawPath.addRect(Math.min(coords[0][0], coords[1][0]), Math.min(coords[0][1], coords[1][1]), Math.max(coords[0][0], coords[1][0]), Math.max(coords[0][1], coords[1][1]), Direction.CW );
			break;
		case CIRCLE:
			coords[1][0] = x;
			coords[1][1] = y;
			RectF tmp = new RectF(Math.min(coords[0][0], coords[1][0]), Math.min(coords[0][1], coords[1][1]), Math.max(coords[0][0], coords[1][0]), Math.max(coords[0][1], coords[1][1]));
			drawPath.reset();
			drawPath.addOval(tmp, Direction.CW );
			break;
		case BRUSH:
			drawPath.lineTo(x, y);
			break;
		case ERASER:
			drawPath.lineTo(x, y);
			break;
		default:
			break;
		}
	}
	
	//update color
	public void setColor(int newColor){
		invalidate();
		drawPaint.setColor(newColor);
	}
	
	public int getColor(){
		return drawPaint.getColor();
	}
	
	public void setBrushSize(float newSize){
		//update size
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
			brushSize=pixelAmount;
			drawPaint.setStrokeWidth(brushSize);
	}
	
	public void setLastBrushSize(float lastSize){
	    lastBrushSize=lastSize;
	}
	
	public float getLastBrushSize(){
	    return lastBrushSize;
	}
	
	public void setErase(boolean isErase){
		//set erase true or false
		erase = isErase;
		
		if(erase) {
			drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else { 
			drawPaint.setXfermode(null);
		}
		
	}
	
	public void startNew(){
		drawing = new ArrayList<DrawingStroke>();
		strokes = 0;
	    drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
	    invalidate();
	}
	
	public void undo(){
		if(strokes > 0)
		{
			strokes--;
			drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
			for(int i = 0; i < strokes; i++){
				drawCanvas.drawPath(drawing.get(i).getPath(), drawing.get(i).getPaint());
			}
		}
		
		invalidate();
	}
	
	public void redo(){
		if(strokes < drawing.size()){
			drawCanvas.drawPath(drawing.get(strokes).getPath(), drawing.get(strokes).getPaint());
			strokes++;
		}
		
		invalidate();
		
	}
	
	public void setType(StrokeType t){
		type = t;
	}
	
	public void setFilled(boolean t){
		if(t)
			drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		else
			drawPaint.setStyle(Paint.Style.STROKE);
	}
}
