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
		//绘制中间透明区域矩形边界的Paint
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.RED);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(100f);
		mLinePaint.setAlpha(0);

		//绘制四周阴影区域
		mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAreaPaint.setColor(Color.GRAY);
		mAreaPaint.setStyle(Style.FILL);
		mAreaPaint.setAlpha(50);
		
		
		
	}
	public void setCenterRect(Rect r){
		this.mCenterRect = r;
		postInvalidate();  //界面刷新
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
		//绘制四周阴影区域
		canvas.drawRect(0, 0, widthScreen, mCenterRect.top, mAreaPaint);    //上方阴影区域
		canvas.drawRect(0, mCenterRect.bottom + 1, widthScreen, heightScreen, mAreaPaint);  //下方阴影区域
		canvas.drawRect(0, mCenterRect.top, mCenterRect.left - 1, mCenterRect.bottom  + 1, mAreaPaint);  //左侧阴影区域
		canvas.drawRect(mCenterRect.right + 1, mCenterRect.top, widthScreen, mCenterRect.bottom + 1, mAreaPaint);  //右侧阴影区域 

		//绘制目标透明区域
		canvas.drawRect(mCenterRect, mLinePaint);
		super.onDraw(canvas);
	}



}