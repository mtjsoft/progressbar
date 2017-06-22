package com.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.example.myprogressbar.R;

/**
 *
 * @author android.MTJ
 * @data.time 2016年8月26日---上午10:23:22
 *
 */
public class HorizontalProgressBarWithNumber extends ProgressBar {

	private static int DEFAULT_TEXT_SIZE = 10;
	private static int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
	private static int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
	private static int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
	private static int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
	private static int DEFAULT_SIZE_TEXT_OFFSET = 10;
	/**
	 * 画笔
	 */
	protected Paint mPaint = new Paint();
	/**
	 * 进度条的数字颜色
	 */
	protected int mTextColor = DEFAULT_TEXT_COLOR;
	/**
	 * 字体大小sp
	 */
	protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

	/**
	 * 进度条数字的偏移量
	 */
	protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

	/**
	 * 已完成进度条的高度
	 */
	protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

	/**
	 * 已完成进度条的颜色
	 */
	protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
	/**
	 * 未完成进度条的颜色
	 */
	protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
	/**
	 * 未完成进度条的高度
	 */
	protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
	/**
	 * view width except padding
	 */
	protected int mRealWidth;

	protected boolean mIfDrawText = true;

	protected static final int VISIBLE = 0;

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setHorizontalScrollBarEnabled(true);
		obtainStyledAttributes(attrs);
		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);
	}

	/**
	 * 获取自定义的属性
	 * 
	 * @param attrs
	 */
	private void obtainStyledAttributes(AttributeSet attrs) {
		// init values from custom attributes
		final TypedArray attributes = getContext().obtainStyledAttributes(
				attrs, R.styleable.HorizontalProgressBarWithNumber);

		mTextColor = attributes
				.getColor(
						R.styleable.HorizontalProgressBarWithNumber_progress_text_color,
						DEFAULT_TEXT_COLOR);
		mTextSize = (int) attributes.getDimension(
				R.styleable.HorizontalProgressBarWithNumber_progress_text_size,
				mTextSize);

		mReachedBarColor = attributes
				.getColor(
						R.styleable.HorizontalProgressBarWithNumber_progress_reached_color,
						mTextColor);
		mUnReachedBarColor = attributes
				.getColor(
						R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color,
						DEFAULT_COLOR_UNREACHED_COLOR);
		mReachedProgressBarHeight = (int) attributes
				.getDimension(
						R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height,
						mReachedProgressBarHeight);
		mUnReachedProgressBarHeight = (int) attributes
				.getDimension(
						R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height,
						mUnReachedProgressBarHeight);
		mTextOffset = (int) attributes
				.getDimension(
						R.styleable.HorizontalProgressBarWithNumber_progress_text_offset,
						mTextOffset);

		int textVisible = attributes
				.getInt(R.styleable.HorizontalProgressBarWithNumber_progress_text_visibility,
						VISIBLE);
		if (textVisible != VISIBLE) {
			mIfDrawText = false;
		}
		attributes.recycle();
	}

	/**
	 * 测量自定义view的高度
	 */
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			float textHeight = (mPaint.descent() + mPaint.ascent());
			int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() + Math
					.max(Math.max(mReachedProgressBarHeight,
							mUnReachedProgressBarHeight), Math.abs(textHeight)));
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
					MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 绘制view
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		canvas.save();
		// 画笔平移到指定paddingLeft， getHeight() / 2位置，注意以后坐标都为以此为0，0
		canvas.translate(getPaddingLeft(), getHeight() / 2);
		boolean noNeedBg = false;
		// 当前进度和总值的比例
		float radio = getProgress() * 1.0f / getMax();
		// 已到达的宽度
		float progressPosX = (int) (mRealWidth * radio);
		// 绘制的文本
		String text = (int) (radio * 100) + "%";
		// 拿到字体的宽度和高度
		float textWidth = mPaint.measureText(text);
		float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
		// 如果到达最后，则未到达的进度条不需要绘制
		if (progressPosX + textWidth > mRealWidth) {
			progressPosX = mRealWidth - textWidth;
			noNeedBg = true;
		}

		// 绘制已到达的进度
		float endX = progressPosX - mTextOffset / 2;
		if (endX > 0) {
			mPaint.setColor(mReachedBarColor);
			mPaint.setStrokeWidth(mReachedProgressBarHeight);
			canvas.drawLine(0, 0, endX, 0, mPaint);
		}

		// 绘制文本
		if (mIfDrawText) {
			mPaint.setColor(mTextColor);
			canvas.drawText(text, progressPosX, -textHeight, mPaint);
		}

		// 绘制未到达的进度条
		if (!noNeedBg) {
			float start = progressPosX + mTextOffset / 2 + textWidth;
			mPaint.setColor(mUnReachedBarColor);
			mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
			canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
		}

		canvas.restore();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mRealWidth = w - getPaddingRight() - getPaddingLeft();

	}

	/**
	 * dp 2 px
	 * 
	 * @param dpVal
	 */
	protected int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	/**
	 * sp 2 px
	 * 
	 * @param spVal
	 * @return
	 */
	protected int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, getResources().getDisplayMetrics());

	}
}