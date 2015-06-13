package com.icloudoor.cloudoor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

public class SetGestureDrawLineView extends View {

	private int mov_x;
	private int mov_y;	
	private Paint paint;
	private Canvas canvas;
	private Bitmap bitmap;
	private List<SetGesturePoint> list;
	private List<Pair<SetGesturePoint, SetGesturePoint>> linelist;  
	private Map<String, SetGesturePoint> autoCheckPointMap;  
	private boolean isDrawEnable = true;
	
	private int[] screenDispaly;
	
	private SetGesturePoint currentPoint;
	private SetGestureCallBack GestureCallBack;  
	
	private StringBuilder passWordSb;  
	
	private boolean isVerify;
	private String passWord; 
	
	public SetGestureDrawLineView(Context context, List<SetGesturePoint> list, boolean isVerify,
			String passWord, SetGestureCallBack callBack){
		super(context);		
		screenDispaly = SetGestureUtil.getScreenDispaly(context);
		paint = new Paint(Paint.DITHER_FLAG);
		bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[0], Bitmap.Config.ARGB_8888);
		canvas = new Canvas();
		canvas.setBitmap(bitmap);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(8);
		paint.setColor(Color.rgb(245, 142, 33));
		paint.setAntiAlias(true);
		
		this.list = list;
		this.linelist = new ArrayList<Pair<SetGesturePoint, SetGesturePoint>>();
		
		initAutoCheckPointMap();
		this.GestureCallBack = callBack; 
		
		
		this.isVerify = isVerify;
		this.passWordSb = new StringBuilder();
		this.passWord = passWord;
	}
	
	private void initAutoCheckPointMap() {
		autoCheckPointMap = new HashMap<String, SetGesturePoint>();
		autoCheckPointMap.put("1,3", getGesturePointByNum(2));
		autoCheckPointMap.put("1,7", getGesturePointByNum(4));
		autoCheckPointMap.put("1,9", getGesturePointByNum(5));
		autoCheckPointMap.put("2,8", getGesturePointByNum(5));
		autoCheckPointMap.put("3,7", getGesturePointByNum(5));
		autoCheckPointMap.put("3,9", getGesturePointByNum(6));
		autoCheckPointMap.put("4,6", getGesturePointByNum(5));
		autoCheckPointMap.put("7,9", getGesturePointByNum(8));
	}
	private SetGesturePoint getGesturePointByNum(int num) {
		for (SetGesturePoint point : list) {
			if (point.getNum() == num) {
				return point;
			}
		}
		return null;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isDrawEnable == false) {
			
			return true;
		}
		paint.setColor(Color.rgb(2, 139, 201));
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mov_x = (int) event.getX();
			mov_y = (int) event.getY();
			
			currentPoint = getPointAt(mov_x, mov_y);
			if (currentPoint != null) {
				currentPoint.setPointState(SetGestureConstants.POINT_STATE_SELECTED);
				passWordSb.append(currentPoint.getNum());
			}
			// canvas.drawPoint(mov_x, mov_y, paint);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			clearScreenAndDrawList();

			
			SetGesturePoint pointAt = getPointAt((int) event.getX(),
					(int) event.getY());
			
			if (currentPoint == null && pointAt == null) {
				return true;
			} else {
				if (currentPoint == null) {
					
					currentPoint = pointAt;
					
					currentPoint.setPointState(SetGestureConstants.POINT_STATE_SELECTED);
					passWordSb.append(currentPoint.getNum());
				}
			}
			if (pointAt == null
					|| currentPoint.equals(pointAt)
					|| SetGestureConstants.POINT_STATE_SELECTED == pointAt
							.getPointState()) {
				
				canvas.drawLine(currentPoint.getCenterX(),
						currentPoint.getCenterY(), event.getX(), event.getY(),
						paint);
			} else {
				
				canvas.drawLine(currentPoint.getCenterX(),
						currentPoint.getCenterY(), pointAt.getCenterX(),
						pointAt.getCenterY(), paint);
				pointAt.setPointState(SetGestureConstants.POINT_STATE_SELECTED);

				
				SetGesturePoint betweenPoint = getBetweenCheckPoint(currentPoint,
						pointAt);
				if (betweenPoint != null
						&& SetGestureConstants.POINT_STATE_SELECTED != betweenPoint
								.getPointState()) {
					
					Pair<SetGesturePoint, SetGesturePoint> pair1 = new Pair<SetGesturePoint, SetGesturePoint>(
							currentPoint, betweenPoint);
					linelist.add(pair1);
					passWordSb.append(betweenPoint.getNum());
					Pair<SetGesturePoint, SetGesturePoint> pair2 = new Pair<SetGesturePoint, SetGesturePoint>(
							betweenPoint, pointAt);
					linelist.add(pair2);
					passWordSb.append(pointAt.getNum());
					
					betweenPoint.setPointState(SetGestureConstants.POINT_STATE_SELECTED);
					
					currentPoint = pointAt;
				} else {
					Pair<SetGesturePoint, SetGesturePoint> pair = new Pair<SetGesturePoint, SetGesturePoint>(
							currentPoint, pointAt);
					linelist.add(pair);
					passWordSb.append(pointAt.getNum());
					
					currentPoint = pointAt;
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (isVerify) {
				
				if (passWord.equals(passWordSb.toString())) {
					
					GestureCallBack.checkedSuccess();
				} else {
					
					GestureCallBack.checkedFail();
				}
			} else {
				GestureCallBack.onGestureCodeInput(passWordSb.toString());
			}
			break;
		default:
			break;
		}
		return true;
	}


	public void clearDrawlineState(long delayTime) {
		if (delayTime > 0) {
		
			isDrawEnable = false;
			drawErrorPathTip();
		}
		new Handler().postDelayed(new clearStateRunnable(), delayTime);
	}
	

	final class clearStateRunnable implements Runnable {
		public void run() {
			
			passWordSb = new StringBuilder();
		
			linelist.clear();
			
			clearScreenAndDrawList();
			for (SetGesturePoint p : list) {
				p.setPointState(SetGestureConstants.POINT_STATE_NORMAL);
			}
			invalidate();
			isDrawEnable = true;
		}
	}
	
	
	private SetGesturePoint getPointAt(int x, int y) {

		for (SetGesturePoint point : list) {
			
			int leftX = point.getLeftX();
			int rightX = point.getRightX();
			if (!(x >= leftX && x < rightX)) {
				
				continue;
			}

			int topY = point.getTopY();
			int bottomY = point.getBottomY();
			if (!(y >= topY && y < bottomY)) {
				
				continue;
			}

			
			return point;
		}

		return null;
	}
	
	private SetGesturePoint getBetweenCheckPoint(SetGesturePoint pointStart, SetGesturePoint pointEnd) {
		int startNum = pointStart.getNum();
		int endNum = pointEnd.getNum();
		String key = null;
		if (startNum < endNum) {
			key = startNum + "," + endNum;
		} else {
			key = endNum + "," + startNum;
		}
		return autoCheckPointMap.get(key);
	}

	
	private void clearScreenAndDrawList() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<SetGesturePoint, SetGesturePoint> pair : linelist) {
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);
		}
	}
	

	private void drawErrorPathTip() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		paint.setColor(Color.rgb(154, 7, 21));
		for (Pair<SetGesturePoint, SetGesturePoint> pair : linelist) {
			pair.first.setPointState(SetGestureConstants.POINT_STATE_WRONG);
			pair.second.setPointState(SetGestureConstants.POINT_STATE_WRONG);
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);
		}
		invalidate();
	}
	
	public interface SetGestureCallBack {

	
		public abstract void onGestureCodeInput(String inputCode);

	
		public abstract void checkedSuccess();

		
		public abstract void checkedFail();
	}
}