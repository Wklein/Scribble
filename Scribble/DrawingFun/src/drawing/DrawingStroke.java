package drawing;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawingStroke {
	
	private Path path;
	private Paint paint;
	private DrawingView.StrokeType type;
	
	public DrawingStroke(Path p, Paint pa, DrawingView.StrokeType t){
		type = t;
		path = p;
		paint = pa;
	}

	public DrawingView.StrokeType getType() {
		return type;
	}

	public void setType(DrawingView.StrokeType type) {
		this.type = type;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}
}
