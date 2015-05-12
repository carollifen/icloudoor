package com.icloudoor.clouddoor;

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
	//起点坐标
	private int mov_x;
	private int mov_y;	
	private Paint paint;
	private Canvas canvas;
	private Bitmap bitmap;
	private List<SetGesturePoint> list;
	private List<Pair<SetGesturePoint, SetGesturePoint>> linelist;  //记录画过的线
	private Map<String, SetGesturePoint> autoCheckPointMap;  //自动选中的情况点
	private boolean isDrawEnable = true;
	
	private int[] screenDispaly;
	
	private SetGesturePoint currentPoint;
	private SetGestureCallBack GestureCallBack;  //用户绘图的回调
	
	private StringBuilder passWordSb;  //用户当前绘制的图形密码
	
	private boolean isVerify;
	private String passWord; //用户传入的passWord
	
	public SetGestureDrawLineView(Context context, List<SetGesturePoint> list, boolean isVerify,
			String passWord, SetGestureCallBack callBack){
		super(context);		
		screenDispaly = SetGestureUtil.getScreenDispaly(context);
		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[0], Bitmap.Config.ARGB_8888);
		canvas = new Canvas();
		canvas.setBitmap(bitmap);
		paint.setStyle(Style.STROKE);// 设置非填充
		paint.setStrokeWidth(10);// 笔宽5像素
		paint.setColor(Color.rgb(245, 142, 33));// 设置默认连线颜色
		paint.setAntiAlias(true);// 不显示锯齿
		
		this.list = list;
		this.linelist = new ArrayList<Pair<SetGesturePoint, SetGesturePoint>>();
		
		initAutoCheckPointMap();
		this.GestureCallBack = callBack; 
		
		// 初始化密码缓存
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
	
	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isDrawEnable == false) {
			// 当期不允许绘制
			return true;
		}
		paint.setColor(Color.rgb(245, 142, 33));// 设置默认连线颜色
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mov_x = (int) event.getX();
			mov_y = (int) event.getY();
			// 判断当前点击的位置是处于哪个点之内
			currentPoint = getPointAt(mov_x, mov_y);
			if (currentPoint != null) {
				currentPoint.setPointState(SetGestureConstants.POINT_STATE_SELECTED);
				passWordSb.append(currentPoint.getNum());
			}
			// canvas.drawPoint(mov_x, mov_y, paint);// 画点
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			clearScreenAndDrawList();

			// 得到当前移动位置是处于哪个点内
			SetGesturePoint pointAt = getPointAt((int) event.getX(),
					(int) event.getY());
			// 代表当前用户手指处于点与点之前
			if (currentPoint == null && pointAt == null) {
				return true;
			} else {// 代表用户的手指移动到了点上
				if (currentPoint == null) {// 先判断当前的point是不是为null
					// 如果为空，那么把手指移动到的点赋值给currentPoint
					currentPoint = pointAt;
					// 把currentPoint这个点设置选中为true;
					currentPoint.setPointState(SetGestureConstants.POINT_STATE_SELECTED);
					passWordSb.append(currentPoint.getNum());
				}
			}
			if (pointAt == null
					|| currentPoint.equals(pointAt)
					|| SetGestureConstants.POINT_STATE_SELECTED == pointAt
							.getPointState()) {
				// 点击移动区域不在圆的区域，或者当前点击的点与当前移动到的点的位置相同，或者当前点击的点处于选中状态
				// 那么以当前的点中心为起点，以手指移动位置为终点画线
				canvas.drawLine(currentPoint.getCenterX(),
						currentPoint.getCenterY(), event.getX(), event.getY(),
						paint);// 画线
			} else {
				// 如果当前点击的点与当前移动到的点的位置不同
				// 那么以前前点的中心为起点，以手移动到的点的位置画线
				canvas.drawLine(currentPoint.getCenterX(),
						currentPoint.getCenterY(), pointAt.getCenterX(),
						pointAt.getCenterY(), paint);// 画线
				pointAt.setPointState(SetGestureConstants.POINT_STATE_SELECTED);

				// 判断是否中间点需要选中
				SetGesturePoint betweenPoint = getBetweenCheckPoint(currentPoint,
						pointAt);
				if (betweenPoint != null
						&& SetGestureConstants.POINT_STATE_SELECTED != betweenPoint
								.getPointState()) {
					// 存在中间点并且没有被选中
					Pair<SetGesturePoint, SetGesturePoint> pair1 = new Pair<SetGesturePoint, SetGesturePoint>(
							currentPoint, betweenPoint);
					linelist.add(pair1);
					passWordSb.append(betweenPoint.getNum());
					Pair<SetGesturePoint, SetGesturePoint> pair2 = new Pair<SetGesturePoint, SetGesturePoint>(
							betweenPoint, pointAt);
					linelist.add(pair2);
					passWordSb.append(pointAt.getNum());
					// 设置中间点选中
					betweenPoint.setPointState(SetGestureConstants.POINT_STATE_SELECTED);
					// 赋值当前的point;
					currentPoint = pointAt;
				} else {
					Pair<SetGesturePoint, SetGesturePoint> pair = new Pair<SetGesturePoint, SetGesturePoint>(
							currentPoint, pointAt);
					linelist.add(pair);
					passWordSb.append(pointAt.getNum());
					// 赋值当前的point;
					currentPoint = pointAt;
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:// 当手指抬起的时候
			if (isVerify) {
				// 手势密码校验
				// 清掉屏幕上所有的线，只画上集合里面保存的线
				if (passWord.equals(passWordSb.toString())) {
					// 代表用户绘制的密码手势与传入的密码相同
					GestureCallBack.checkedSuccess();
				} else {
					// 用户绘制的密码与传入的密码不同。
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

	/**
	 * 指定时间去清除绘制的状态
	 * @param delayTime 延迟执行时间
	 */
	public void clearDrawlineState(long delayTime) {
		if (delayTime > 0) {
			// 绘制红色提示路线
			isDrawEnable = false;
			drawErrorPathTip();
		}
		new Handler().postDelayed(new clearStateRunnable(), delayTime);
	}
	
	/**
	 * 清除绘制状态的线程
	 */
	final class clearStateRunnable implements Runnable {
		public void run() {
			// 重置passWordSb
			passWordSb = new StringBuilder();
			// 清空保存点的集合
			linelist.clear();
			// 重新绘制界面
			clearScreenAndDrawList();
			for (SetGesturePoint p : list) {
				p.setPointState(SetGestureConstants.POINT_STATE_NORMAL);
			}
			invalidate();
			isDrawEnable = true;
		}
	}
	
	/**
	 * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
	 * 
	 * @param x
	 * @param y
	 * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
	 */
	private SetGesturePoint getPointAt(int x, int y) {

		for (SetGesturePoint point : list) {
			// 先判断x
			int leftX = point.getLeftX();
			int rightX = point.getRightX();
			if (!(x >= leftX && x < rightX)) {
				// 如果为假，则跳到下一个对比
				continue;
			}

			int topY = point.getTopY();
			int bottomY = point.getBottomY();
			if (!(y >= topY && y < bottomY)) {
				// 如果为假，则跳到下一个对比
				continue;
			}

			// 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
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

	/**
	 * 清掉屏幕上所有的线，然后画出集合里面的线
	 */
	private void clearScreenAndDrawList() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<SetGesturePoint, SetGesturePoint> pair : linelist) {
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
	}
	
	/**
	 * 校验错误/两次绘制不一致提示
	 */
	private void drawErrorPathTip() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		paint.setColor(Color.rgb(154, 7, 21));// 设置默认线路颜色
		for (Pair<SetGesturePoint, SetGesturePoint> pair : linelist) {
			pair.first.setPointState(SetGestureConstants.POINT_STATE_WRONG);
			pair.second.setPointState(SetGestureConstants.POINT_STATE_WRONG);
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
		invalidate();
	}
	
	public interface SetGestureCallBack {

		/**
		 * 用户设置/输入了手势密码
		 */
		public abstract void onGestureCodeInput(String inputCode);

		/**
		 * 代表用户绘制的密码与传入的密码相同
		 */
		public abstract void checkedSuccess();

		/**
		 * 代表用户绘制的密码与传入的密码不相同
		 */
		public abstract void checkedFail();
	}
}