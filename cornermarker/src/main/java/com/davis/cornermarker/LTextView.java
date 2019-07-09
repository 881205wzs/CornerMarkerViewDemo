package com.davis.cornermarker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by davis on 19/7/8.
 */

public class LTextView extends AppCompatTextView {

    private Context mContext;
    // 宽高
    private int mWidth, mHeight;
    private float _density = 1;
    //LEFT_TOP = 1; RIGHT_TOP = 2; LEFT_BOTTOM = 3; RIGHT_BOTTOM = 4
    private int cornerPosition = 2;

    // 文本画笔
    private Paint txtPaint = null;
    // 字体颜色
    private int txtColor = Color.WHITE;
    // 字体大小 8sp
    private int txtSize = 8;
    // 画笔宽度
    private int txtStrokeWidth = 2;
    // 文本内容
    private CharSequence txtContent = null;
    private float txtContentWidth = 0;
    private float txtContentHeight = 0;
    //数字在x轴的位移
    private float txtOffsetX = 4;
    //数字在y轴的位移
    private float txtOffsetY = 0;
    private float txtX = 0;
    private float txtY = 0;

    //数字的最大宽度
    private float maxWidth = 0;

    // 背景画笔
    private Paint circlePaint = null;
    // 圆圈颜色
    private int circleColor = Color.RED;
    // 圆圈画笔宽度
    private int circleStrokeWidth = 2;
    private float circleY = 0;
    private float circleX = 0;


    // 底部横线画笔
    private Paint linePaint = null;
    // 底部横线颜色
    private int lineColor = Color.GREEN;
    // 底部横线画笔宽度
    private int lineStrokeWidth = 6;
    // 底部横线宽度
    private int lineWidth = 30;
    //
    private float lineOffsetY = 8;
    // 底部横线是否显示
    private boolean showLine = false;


    public LTextView(Context context) {
        //super(context);
        this(context, null);
    }

    public LTextView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }

    public LTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        this.mContext = context;
        _density = context.getResources().getDisplayMetrics().density;

        txtSize = (int)(txtSize * _density);
        lineWidth = (int)(lineWidth * _density);
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LCorner);
            circleColor = typedArray.getColor(R.styleable.LCorner_corner_background, circleColor);
            txtColor = typedArray.getColor(R.styleable.LCorner_corner_text_color, txtColor);
            txtSize = typedArray.getDimensionPixelSize(R.styleable.LCorner_corner_text_size, txtSize);
            cornerPosition = typedArray.getInteger(R.styleable.LCorner_corner_position, cornerPosition);
            lineColor = typedArray.getColor(R.styleable.LCorner_corner_bottom_line_color, lineColor);
            txtContent = typedArray.getString(R.styleable.LCorner_corner_text);
            showLine = typedArray.getBoolean(R.styleable.LCorner_corner_bottom_line_state, showLine);
            lineWidth = typedArray.getDimensionPixelSize(R.styleable.LCorner_corner_bottom_line_width, lineWidth);
        }

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(txtColor);
        txtPaint.setStrokeWidth(txtStrokeWidth);
        txtPaint.setTextSize(txtSize);
        txtPaint.setStyle(Paint.Style.FILL);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(circleStrokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineStrokeWidth);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        //计算出数字的可能的最大宽度，这里默认最大能显示数字是99，对于99用99+表示
        maxWidth = txtPaint.measureText("99+");
        initText();
    }

    private void initText(){
        // 当前数字的真正宽度
        txtContentWidth = txtPaint.measureText(String.valueOf(txtContent));


        Paint.FontMetrics metrics = txtPaint.getFontMetrics();
        //因为数字的外面要包一个圆，所以为了让圆能显示完全，就让数字的高也和宽相等，减去位移量，获取数字的高度
        float textH = maxWidth + txtOffsetY;
        //如果看过http://www.jianshu.com/p/a3d15421a718我这篇文章，应该知道这是获取baseline，
        //获取到这个位置就能将文字在指定位置的y轴中心显示
        txtContentHeight = (textH - (metrics.descent - metrics.ascent))/2 - metrics.ascent;
        if(cornerPosition == 1){
            //左上角
            // 计算的是数字的x坐标
            // 因为已经限定数字的最大宽度是"99+"字符串的宽度
            // mWidth-maxWidth/2就是圆心的位置，
            // 如果我们要将数字画在园的中心，就要
            // 获取当前数字的宽度的一半
            // mWidth-maxWidth/2-mPointTextWidth/2，
            // 然后再减去自己设置的x轴位移量
            txtX = maxWidth/2 - txtContentWidth/2 + txtOffsetX;
            //为了能让圆圈显示完整，所以+1*_density
            txtY = txtContentHeight + 1 * _density;
            //圆的x轴
            circleX = (maxWidth)/2 + txtOffsetX;
            //圆的y轴
            circleY = textH/2 + 1 * _density;
        } else if(cornerPosition == 2){
            //右上角
            // 计算的是数字的x坐标
            // 因为已经限定数字的最大宽度是"99+"字符串的宽度
            // mWidth-maxWidth/2就是圆心的位置，
            // 如果我们要将数字画在园的中心，就要
            // 获取当前数字的宽度的一半
            // mWidth-maxWidth/2-mPointTextWidth/2，
            // 然后再减去自己设置的x轴位移量
            txtX = mWidth - maxWidth/2 - txtContentWidth/2 - txtOffsetX;
            //为了能让圆圈显示完整，所以+1*_density
            txtY = txtContentHeight + 1 * _density;
            //圆的x轴
            circleX = mWidth - (maxWidth)/2 - txtOffsetX;
            //圆的y轴
            circleY = textH/2 + 1 * _density;
        } else if(cornerPosition == 3){
            //左下角
            // 计算的是数字的x坐标
            // 因为已经限定数字的最大宽度是"99+"字符串的宽度
            // mWidth-maxWidth/2就是圆心的位置，
            // 如果我们要将数字画在园的中心，就要
            // 获取当前数字的宽度的一半
            // mWidth-maxWidth/2-mPointTextWidth/2，
            // 然后再减去自己设置的x轴位移量
            txtX = maxWidth/2 - txtContentWidth/2 + txtOffsetX;

            //圆的x轴
            circleX = (maxWidth)/2 + txtOffsetX;
            //圆的y轴
            circleY = mHeight - (textH/2 + 1 * _density);
            //为了能让圆圈显示完整，所以+1*_density
            txtY = circleY * 2 - mHeight + txtContentHeight + 1 * _density;
        } else {
            //右下角
            // 计算的是数字的x坐标
            // 因为已经限定数字的最大宽度是"99+"字符串的宽度
            // mWidth-maxWidth/2就是圆心的位置，
            // 如果我们要将数字画在园的中心，就要
            // 获取当前数字的宽度的一半
            // mWidth-maxWidth/2-mPointTextWidth/2，
            // 然后再减去自己设置的x轴位移量
            txtX = mWidth - maxWidth/2 - txtContentWidth/2 - txtOffsetX;
            //圆的x轴
            circleX = mWidth - (maxWidth)/2 - txtOffsetX;
            //圆的y轴
            circleY = mHeight - (textH/2 + 1 * _density);

            //为了能让圆圈显示完整，所以+1*_density
            txtY = circleY * 2 - mHeight + txtContentHeight + 1 * _density;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (txtContent != null && !txtContent.equals("")) {
            float r = maxWidth/2 + 1 * _density;
            circlePaint.setStrokeWidth(r);
            canvas.drawCircle(circleX, circleY, r/2, circlePaint);
            if (Integer.parseInt(txtContent.toString()) > 99) {
                canvas.drawText("99+", txtX, txtY, txtPaint);
            }else
                canvas.drawText(txtContent.toString(), txtX, txtY, txtPaint);
        }
        if(showLine){
            canvas.drawLine(mWidth/2 - lineWidth/2, mHeight - lineOffsetY, mWidth/2 + lineWidth/2, mHeight - lineOffsetY, linePaint);
        }
    }

    public void setShowLine(boolean bl){
        showLine = bl;
        refresh();
    }

    public boolean getShowLineState(){
        return showLine;
    }

    public void setTipText(CharSequence txt) {
        this.txtContent = txt;
        refresh();
    }

    public void refresh(){
        initText();
        invalidate();
    }

    public void setLineWidth(int width){
        lineWidth = width;
        refresh();
    }

    public void setLineHeight(int height){
        lineStrokeWidth = height;
        if(linePaint != null){
            linePaint.setStrokeWidth(lineStrokeWidth);
        }
        refresh();
    }
}
