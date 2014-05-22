package drawing;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawingStroke {
	
	private Path path;
	private Paint paint;
	
	public DrawingStroke(Path p, Paint pa){
		path = p;
		paint = pa;
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
