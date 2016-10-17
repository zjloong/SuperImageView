package com.heihei.hehe.roundimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形/圆角 图片
 *
 * @author zhujinlong@ichoice.com
 * @date 2016/10/13
 * @time 11:27
 * @description Describe the place where the class needs to pay attention.
 */
public class SuperImageView extends ImageView{

    int w,h,type,mBorderRadius;
    static final int TYPE_ROUND = 1, TYPE_CIRCLE = 2;
    Paint paint;
    PorterDuffXfermode porterDuffXfermode;
    RectF rectF;

    public SuperImageView(Context context) {
        this(context,null);
    }

    public SuperImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SuperImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperImageView);
        mBorderRadius = a.getDimensionPixelSize(R.styleable.SuperImageView_imageRadius, (int) (context.getResources().getDisplayMetrics().density * 8 + 0.5f));
        type = a.getInt(R.styleable.SuperImageView_imageType, 0);
        a.recycle();
        if(type == TYPE_ROUND || type == TYPE_CIRCLE){
            porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
            paint = new Paint();
            paint.setAntiAlias(true);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 也可以通过画布裁剪实现(应该是代价最小的解决方案),但需要解决抗锯齿问题
        // Xfermode 相对于 BitmapShaper 速度更块,内存更小,所以使用 Xfermode 实现

        if(type != TYPE_ROUND && type != TYPE_CIRCLE){
            // 默认情况,
            super.onDraw(canvas);
        }else if(getDrawable() != null){
            // 保存画布
            int save = canvas.saveLayer(0,0,w,h,null,Canvas.ALL_SAVE_FLAG);
            // 绘制圆形或圆角矩形
            if(type == TYPE_CIRCLE){
                canvas.drawCircle(w/2,h/2, w > h ? h/2 : w/2,paint);
            }else {
                if(rectF == null){
                    rectF = new RectF(0,0,w,h);
                }
                canvas.drawRoundRect(rectF,mBorderRadius,mBorderRadius,paint);
            }
            // 设置混合模式为 src_in
            paint.setXfermode(porterDuffXfermode);
            // 将绘制过的画布再次保存
            canvas.saveLayer(0, 0, w,h, paint, Canvas.ALL_SAVE_FLAG);
            // 绘制原图片
            getDrawable().draw(canvas);
            // 恢复Layer
            canvas.restoreToCount(save);
            paint.setXfermode(null);
        }
    }
}