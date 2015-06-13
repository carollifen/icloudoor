package com.icloudoor.cloudoor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class OpenDoorRingView extends ImageView {
	Context context;
	Paint paint;
	int[] colors;
	Shader radialGradient;
	int center;
	
	public OpenDoorRingView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}
	
	public OpenDoorRingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.paint = new Paint();
        
        colors = new int[]{Color.WHITE, Color.TRANSPARENT};
//        radialGradient = new RadialGradient(210, 210, 210, colors, null, TileMode.CLAMP);
//		paint.setShader(radialGradient);
    }
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		center = getWidth()/2;
		Log.e("test center", String.valueOf(center));
		
		radialGradient = new RadialGradient(center, center, center, colors, null, TileMode.CLAMP);
		paint.setShader(radialGradient);
		
		canvas.drawCircle(center, center, center, paint);
		postInvalidate();
	}
}