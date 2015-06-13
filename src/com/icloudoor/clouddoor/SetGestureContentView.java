package com.icloudoor.cloudoor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.icloudoor.cloudoor.SetGestureDrawLineView.SetGestureCallBack;

public class SetGestureContentView extends ViewGroup {
	
	private int baseNum = 6;
	private int[] screenDispaly;
	

	private int blockWidth;
	
	private List<SetGesturePoint> list;
	private Context context;
	private boolean isVerify;
	private SetGestureDrawLineView getstureDrawLine;
	

	public SetGestureContentView(Context context, boolean isVerify, String passWord, SetGestureCallBack callBack){
		super(context);
		screenDispaly = SetGestureUtil.getScreenDispaly(context);
		blockWidth = screenDispaly[0]/3;
		this.list = new ArrayList<SetGesturePoint>();
		this.context = context;
		this.isVerify = isVerify;
	
		addChild();
	
		getstureDrawLine = new SetGestureDrawLineView(context, list, isVerify, passWord, callBack);
	}
	
	private void addChild(){
		for (int i = 0; i < 9; i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.sign_gray);
			this.addView(image);
			invalidate();
			
			int row = i / 3;
			
			int col = i % 3;
			
			int leftX = col*blockWidth+blockWidth/baseNum;
			int topY = row*blockWidth+blockWidth/baseNum;
			int rightX = col*blockWidth+blockWidth-blockWidth/baseNum;
			int bottomY = row*blockWidth+blockWidth-blockWidth/baseNum;
			SetGesturePoint p = new SetGesturePoint(leftX, rightX, topY, bottomY, image,i+1);
			this.list.add(p);
		}
	}
	
	public void setParentView(ViewGroup parent){
		
		int width = screenDispaly[0];
		LayoutParams layoutParams = new LayoutParams(width, width);
		this.setLayoutParams(layoutParams);
		getstureDrawLine.setLayoutParams(layoutParams);
		parent.addView(getstureDrawLine);
		parent.addView(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			
			int row = i/3;
			
			int col = i%3;
			View v = getChildAt(i);
			v.layout(col*blockWidth+blockWidth/baseNum, row*blockWidth+blockWidth/baseNum, 
					col*blockWidth+blockWidth-blockWidth/baseNum, row*blockWidth+blockWidth-blockWidth/baseNum);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
	}

	public void clearDrawlineState(long delayTime) {
		getstureDrawLine.clearDrawlineState(delayTime);
	}
}