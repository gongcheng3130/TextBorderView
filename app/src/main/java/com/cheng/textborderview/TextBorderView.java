package com.cheng.textborderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义textview
 * 可用于背景边框点击效果等设置
 * 注：如果设置了选中颜色效果需要设置onclick事件
 * @author cheng
 */
public class TextBorderView extends AppCompatTextView {

	private int border_Select_Color;//线框选中颜色
	private int border_Color;//线框颜色
	private int background_Select_Color;//背景选中颜色
	private int background_Color;//背景颜色
	private int border_Width = 0;//线框宽度
	private int border_Radius = 0;//圆角
	private Paint paint1;//画背景的
	private RectF rec1;//背景矩形
	private Paint paint2;//画边框的
	private RectF rec2;//边框矩形
	private DrawFilter mDrawFilter;

	public TextBorderView(Context context) {
		super(context);
	}

	public TextBorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttr(attrs);
	}

	public TextBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		parseAttr(attrs);
	}

	private void parseAttr(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextBorderView);
		if (typedArray == null)
			return;
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
				case R.styleable.TextBorderView_Border_Color:
					border_Color = typedArray.getColor(attr, 0);
					break;
				case R.styleable.TextBorderView_Border_Select_Color:
					border_Select_Color = typedArray.getColor(attr, 0);
					break;
				case R.styleable.TextBorderView_Background_Color:
					background_Color = typedArray.getColor(attr, 0);
					break;
				case R.styleable.TextBorderView_Background_Select_Color:
					background_Select_Color = typedArray.getColor(attr, 0);
					break;
				case R.styleable.TextBorderView_Border_Width:
					border_Width = typedArray.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.TextBorderView_Border_Radius:
					border_Radius = typedArray.getDimensionPixelSize(attr, 0);
					break;
			}
		}
		typedArray.recycle();

		paint1 = new Paint();
		paint1.setColor(background_Color);// 画笔颜色

		paint2 = new Paint();
		paint2.setColor(border_Color);// 画笔颜色
		paint2.setStrokeWidth((float) border_Width);// 线宽
		paint2.setStyle(Style.STROKE);// 空心效果
		paint2.setAntiAlias(true);// 无锯齿
		paint2.setDither(true);// 防抖动

		mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(mDrawFilter);// 从canvas层面去除绘制时锯齿
		canvas.drawRoundRect(rec1, border_Radius, border_Radius, paint1); // 绘制圆角矩形
		canvas.drawRoundRect(rec2, border_Radius, border_Radius, paint2); // 绘制圆角矩形
		super.onDraw(canvas);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				actionDown();
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				actionUp();
				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(event);
	}

	private void actionDown(){
		if(background_Select_Color != 0) paint1.setColorFilter(new LightingColorFilter(0, background_Select_Color));
		if(border_Select_Color != 0) paint2.setColorFilter(new LightingColorFilter(0, border_Select_Color));
		if(background_Select_Color!=0 || border_Select_Color != 0) postInvalidateDelayed(10);
	}

	private void actionUp(){
		if(background_Color != 0) paint1.setColorFilter(new LightingColorFilter(0, background_Color));
		if(border_Color != 0) paint2.setColorFilter(new LightingColorFilter(0, border_Color));
		if(background_Color != 0 || border_Color != 0) postInvalidateDelayed(10);
	}

	private boolean inRangeOfView(View view, MotionEvent event){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if(event.getX() < x || event.getX() > (x + view.getWidth()) || event.getY() < y || event.getY() > (y + view.getHeight())){
			return false;
		}
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		rec1 = new RectF(); // RectF对象
		rec1.left = w-getWidth()+border_Width/2; // 左边
		rec1.top = h-getHeight()+border_Width/2; // 上边
		rec1.right = w-border_Width/2; // 右边
		rec1.bottom = h-border_Width/2; // 下边

		rec2 = new RectF(); // RectF对象
		rec2.left = w-getWidth()+border_Width/2; // 左边
		rec2.top = h-getHeight()+border_Width/2; // 上边
		rec2.right = w-border_Width/2; // 右边
		rec2.bottom = h-border_Width/2; // 下边
	}

	public void setBorderSelectColor(int border_Select_Color) {
		this.border_Select_Color = border_Select_Color;
		paint2.setColorFilter(new LightingColorFilter(0, border_Select_Color));
		postInvalidateDelayed(10);
	}

	public void setBorderColor(int border_Color) {
		this.border_Color = border_Color;
		paint2.setColorFilter(new LightingColorFilter(0, border_Color));
		postInvalidateDelayed(10);
	}

	public void setBackgroundSelectColor(int background_Select_Color) {
		this.background_Select_Color = background_Select_Color;
		paint1.setColorFilter(new LightingColorFilter(0, background_Select_Color));
		postInvalidateDelayed(10);
	}

	@Override
	public void setBackgroundColor(int color) {
		this.background_Color = color;
		paint1.setColorFilter(new LightingColorFilter(0, background_Color));
		postInvalidateDelayed(10);
	}

}