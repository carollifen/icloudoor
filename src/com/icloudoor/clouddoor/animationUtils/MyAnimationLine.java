package com.icloudoor.clouddoor.animationUtils;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class MyAnimationLine extends View {
	Paint paint;
	 Paint mypaint;
	public MyAnimationLine(Context context) {  
        super(context);  
       
          
         
    }  
	
	public MyAnimationLine(Context context, AttributeSet attr) {
		super(context, attr); 
		
		// 首先定义一个paint   
        paint = new Paint();   
         mypaint =new Paint();  
        // 绘制矩形区域-实心矩形   
        // 设置颜色   
        paint.setColor(Color.GREEN);  
        mypaint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        
        mypaint.setStyle(Style.FILL); 
//        mypaint.setAlpha(17);
        // 设置样式-填充   
        paint.setStyle(Style.STROKE); 
	}
      
    public void setColor(int color) {  
        // 设置颜色   
        paint.setColor(color);  
    }  
    @Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
        float m_width=getWidth();  
        float m_heigh=getHeight(); 
        
         float x_center=m_width/2;
         float y_center=m_heigh/2;
         
         float r=(y_center-140)/5;
         Resources res = this.getContext().getResources();  
       //以数据流的方式读取资源  
//       InputStream is = res.openRawResource(R.drawable.rara);  
//       BitmapDrawable  bmpDraw = new BitmapDrawable(is);  
//       Bitmap bmp = bmpDraw.getBitmap();  
       
       //画图片
       // canvas.drawBitmap(bmp,0, y_center, null);
       
       //画扇形   左上右下
     // canvas.drawArc(new RectF(-((m_heigh-160)/2-m_width/2),140,m_width+(m_heigh-m_width)/2,m_heigh-20), 45, 90, true, paint);  
         
         for(int i = 1; i <= 10; i++){
        	 mypaint.setAlpha((int) (6 - i *0.6));
        	 canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 45, 90 - 2*i, true, mypaint);
         }
//         canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 45, 90, true, mypaint);
         
       
////       mypaint.setAlpha(25);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 80, 86, true, mypaint);
////       mypaint.setAlpha(30);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 75, 81, true, mypaint);
////       mypaint.setAlpha(35);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 70, 76, true, mypaint);
////       mypaint.setAlpha(40);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 65, 71, true, mypaint);
////       mypaint.setAlpha(45);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 60, 66, true, mypaint);
////       mypaint.setAlpha(50);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 55, 61, true, mypaint);
////       mypaint.setAlpha(55);
////       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 50, 56, true, mypaint);
////       mypaint.setAlpha(60);
//       canvas.drawArc(new RectF(x_center-5*r,140,x_center+5*r,10*r+140), 45, 51, true, mypaint);
//       //画线
       //canvas.drawLine(x_center, 140,x_center,y_center, paint);
       
       //画圆
      //canvas.drawCircle(x_center,y_center, r*5, mypaint);  
//        // draw circle  
//        canvas.drawCircle(x_center,y_center, r*4, paint);  
//        canvas.drawCircle(x_center,y_center, r*3, paint);  
//        canvas.drawCircle(x_center,y_center, r*5, paint);  
       // canvas.drawCircle(x_center,y_center, x_center-50, paint);  
    }  

}
