package com.icloudoor.clouddoor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class LockIndicatorView extends View {
	
	private int rowNum = 3;
	private int columnNum = 3;
	private int patternWidth = 100;
	private int patternHeight = 100;
	private int f = 5;
	private int g = 5;
	private int strokeWidth = 3;
	private Paint paint = null;
	private Drawable patternNoraml = null;
	private Drawable patternPressed = null;
	private String lockPassStr; 
	
	public LockIndicatorView(Context context) {
		super(context);
	}
	
	public LockIndicatorView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);
		patternNoraml = getResources().getDrawable(R.drawable.sign_gray);
		patternPressed = getResources().getDrawable(R.drawable.sign_blue);
		if (patternPressed != null) {
			patternWidth = patternPressed.getIntrinsicWidth();
			patternHeight = patternPressed.getIntrinsicHeight();
			this.f = (patternWidth / 4);
			this.g = (patternHeight / 4);
			patternPressed.setBounds(0, 0, patternWidth, patternHeight);
			patternNoraml.setBounds(0, 0, patternWidth, patternHeight);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if ((patternPressed == null) || (patternNoraml == null)) {
			return;
		}
		// ����3*3��ͼ��
		for (int i = 0; i < rowNum; i++) {
			for (int j = 0; j < columnNum; j++) {
				paint.setColor(-16777216);
				int i1 = j * patternHeight + j * this.g;
				int i2 = i * patternWidth + i * this.f;
				canvas.save();
				canvas.translate(i1, i2);
				String curNum = String.valueOf(columnNum * i + (j + 1));
				if (!TextUtils.isEmpty(lockPassStr)) {
					if (lockPassStr.indexOf(curNum) == -1) {
						// δѡ��
						patternNoraml.draw(canvas);
					} else {
						// ��ѡ��
						patternPressed.draw(canvas);
					}
				} else {
					// ����״̬
					patternNoraml.draw(canvas);
				}
				canvas.restore();
			}
		}
	}
	
	@Override
	protected void onMeasure(int paramInt1, int paramInt2) {
		if (patternPressed != null)
			setMeasuredDimension(columnNum * patternHeight + this.g
					* (-1 + columnNum), rowNum * patternWidth + this.f
					* (-1 + rowNum));
	}
	
	public void setPath(String paramString) {
		lockPassStr = paramString;
		invalidate();
	}
}