package com.icloudoor.clouddoor;

import com.icloudoor.clouddoor.TakePicDisplayUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TakePicMaskView extends ImageView {

	private Paint mLinePaint;
	private Paint mAreaPaint;
	private Rect mCenterRect = null;
	private Context mContext;


	public TakePicMaskView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initPaint();
		mContext = context;
		Point p	= TakePicDisplayUtil.getScreenMetrics(mContext);
		widthScreen = p.x;
		heightScreen = p.y;
	}

	private void initPaint(){
		
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.RED);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(100f);
		mLinePaint.setAlpha(0);

		
		mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAreaPaint.setColor(Color.GRAY);
		mAreaPaint.setStyle(Style.FILL);
		mAreaPaint.setAlpha(50);
		
		
		
	}
	public void setCenterRect(Rect r){
		this.mCenterRect = r;
		postInvalidate();  
	}
	public void clearCenterRect(Rect r){
		this.mCenterRect = null;
	}

	int widthScreen, heightScreen;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(mCenterRect == null)
			return;
		
		canvas.drawRect(0, 0, widthScreen, mCenterRect.top, mAreaPaint);    // top
		canvas.drawRect(0, mCenterRect.bottom + 1, widthScreen, heightScreen, mAreaPaint);  //bottom
		canvas.drawRect(0, mCenterRect.top, mCenterRect.left - 1, mCenterRect.bottom  + 1, mAreaPaint);  // left side
		canvas.drawRect(mCenterRect.right + 1, mCenterRect.top, widthScreen, mCenterRect.bottom + 1, mAreaPaint);  // right side

		
		canvas.drawRect(mCenterRect, mLinePaint);
		super.onDraw(canvas);
	}



}