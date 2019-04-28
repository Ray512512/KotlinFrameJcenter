package common.view.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ray on 2017/1/3.
 *  * 图片文字居中按钮
 */

public class DrawableImgCenterButton extends android.support.v7.widget.AppCompatTextView {

private DisableToastCalBack disableToastCalBack;
    public DrawableImgCenterButton(Context context) {
        super(context);
    }

    public DrawableImgCenterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableImgCenterButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_UP)
        if(!isEnabled()){
             if(disableToastCalBack!=null){
                 disableToastCalBack.checkToast();
             }
        }
        return super.onTouchEvent(event);
    }


    public interface DisableToastCalBack{
        void checkToast();
    }

    public void setDisableToastCalBack(DisableToastCalBack calBack){
        this.disableToastCalBack=calBack;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        if (drawableLeft != null) {
            final float textWidth = getPaint().measureText(getText().toString());
            final int drawablePadding = getCompoundDrawablePadding();
            final int drawableWidth = drawableLeft.getIntrinsicWidth();
            final float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }
}
