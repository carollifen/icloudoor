package com.icloudoor.clouddoor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.icloudoor.clouddoor.SetGestureDrawLineView.SetGestureCallBack;

public class SetGestureContentView extends ViewGroup {
	
	private int baseNum = 6;
	private int[] screenDispaly;
	
	/**
	 * 每个点区域的宽度
	 */
	private int blockWidth;
	
	private List<SetGesturePoint> list;
	private Context context;
	private boolean isVerify;
	private SetGestureDrawLineView getstureDrawLine;
	
	/**
	 * 包含9个ImageView的容器，初始化
	 * @param context
	 * @param isVerify 是否为校验手势密码
	 * @param passWord 用户传入密码
	 * @param callBack 手势绘制完毕的回调
	 */
	public SetGestureContentView(Context context, boolean isVerify, String passWord, SetGestureCallBack callBack){
		super(context);
		screenDispaly = SetGestureUtil.getScreenDispaly(context);
		blockWidth = screenDispaly[0]/3;
		this.list = new ArrayList<SetGesturePoint>();
		this.context = context;
		this.isVerify = isVerify;
		// 添加9个图标
		addChild();
		// 初始化一个可以画线的view
		getstureDrawLine = new SetGestureDrawLineView(context, list, isVerify, passWord, callBack);
	}
	
	private void addChild(){
		for (int i = 0; i < 9; i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.sign_gray);
			this.addView(image);
			invalidate();
			// 第几行
			int row = i / 3;
			// 第几列
			int col = i % 3;
			// 定义点的每个属性
			int leftX = col*blockWidth+blockWidth/baseNum;
			int topY = row*blockWidth+blockWidth/baseNum;
			int rightX = col*blockWidth+blockWidth-blockWidth/baseNum;
			int bottomY = row*blockWidth+blockWidth-blockWidth/baseNum;
			SetGesturePoint p = new SetGesturePoint(leftX, rightX, topY, bottomY, image,i+1);
			this.list.add(p);
		}
	}
	
	public void setParentView(ViewGroup parent){
		// 得到屏幕的宽度
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
			//第几行
			int row = i/3;
			//第几列
			int col = i%3;
			View v = getChildAt(i);
			v.layout(col*blockWidth+blockWidth/baseNum, row*blockWidth+blockWidth/baseNum, 
					col*blockWidth+blockWidth-blockWidth/baseNum, row*blockWidth+blockWidth-blockWidth/baseNum);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 遍历设置每个子view的大小
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
	}
	
	/**
	 * 保留路径delayTime时间长
	 * @param delayTime
	 */
	public void clearDrawlineState(long delayTime) {
		getstureDrawLine.clearDrawlineState(delayTime);
	}
}