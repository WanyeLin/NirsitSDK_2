package obelab.com.sdkexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * create by:Fxymine4ever
 * time: 2019/7/12
 */
public class WaveCircleView extends CircleImageView {

    private int radius;
    private Paint paint = new Paint();

    public WaveCircleView(Context context) {
        super(context);
    }

    public WaveCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radius = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAlpha();
        paint
    }
}
