package common.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.kotlin.library.R;
import com.kotlin.library.glide.GlideUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import common.presentation.utils.SizeUtils;

/**
 * Created by itte on 2016/4/16.
 */
public class BannerLayout extends RelativeLayout {

    private ViewPager pager;
    //指示器容器
    private LinearLayout indicatorContainer;

    private Drawable unSelectedDrawable;
    private Drawable selectedDrawable;

    private int WHAT_AUTO_PLAY = 1000;

    private boolean isAutoPlay = true;

    public int itemCount;

    private int selectedIndicatorColor = 0xffff0000;
    private int unSelectedIndicatorColor = 0x88888888;

    private Shape indicatorShape = Shape.oval;
    private int selectedIndicatorHeight = 10;
    private int selectedIndicatorWidth = 10;
    private int unSelectedIndicatorHeight = 10;
    private int unSelectedIndicatorWidth = 10;

    private Position indicatorPosition = Position.centerBottom;
    private int autoPlayDuration = 4000;
    private int scrollDuration = 900;

    private int indicatorSpace = 4;
    private int indicatorMargin = 10;

    private int defaultImage;
    private int imageHeight = -1;
    private int imageWidth = -1;

    private enum Shape {
        rect, oval
    }

    private enum Position {
        centerBottom,
        rightBottom,
        leftBottom,
        centerTop,
        rightTop,
        leftTop
    }

    private OnBannerItemClickListener onBannerItemClickListener;
    private BannerListener bannerListener;


    public void setDuration(int duration){
        autoPlayDuration=duration;
    }

    public void setBannerListener(BannerListener bannerListener) {
        this.bannerListener = bannerListener;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                if (pager != null) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
                }
            }
            return false;
        }
    });

    public BannerLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BannerLayout, defStyle, 0);
        selectedIndicatorColor = array.getColor(R.styleable.BannerLayout_selectedIndicatorColor, selectedIndicatorColor);
        unSelectedIndicatorColor = array.getColor(R.styleable.BannerLayout_unSelectedIndicatorColor, unSelectedIndicatorColor);

        int shape = array.getInt(R.styleable.BannerLayout_indicatorShape, Shape.oval.ordinal());
        for (Shape shape1 : Shape.values()) {
            if (shape1.ordinal() == shape) {
                indicatorShape = shape1;
                break;
            }
        }
        selectedIndicatorHeight = (int) array.getDimension(R.styleable.BannerLayout_selectedIndicatorHeight, selectedIndicatorHeight);
        selectedIndicatorWidth = (int) array.getDimension(R.styleable.BannerLayout_selectedIndicatorWidth, selectedIndicatorWidth);
        unSelectedIndicatorHeight = (int) array.getDimension(R.styleable.BannerLayout_unSelectedIndicatorHeight, unSelectedIndicatorHeight);
        unSelectedIndicatorWidth = (int) array.getDimension(R.styleable.BannerLayout_unSelectedIndicatorWidth, unSelectedIndicatorWidth);
        int position = array.getInt(R.styleable.BannerLayout_indicatorPosition, Position.centerBottom.ordinal());
        for (Position position1 : Position.values()) {
            if (position == position1.ordinal()) {
                indicatorPosition = position1;
            }
        }
        indicatorSpace = (int) array.getDimension(R.styleable.BannerLayout_indicatorSpace, indicatorSpace);
        indicatorMargin = (int) array.getDimension(R.styleable.BannerLayout_indicatorMargin, indicatorMargin);
        autoPlayDuration = array.getInt(R.styleable.BannerLayout_autoPlayDuration, autoPlayDuration);
        scrollDuration = array.getInt(R.styleable.BannerLayout_scrollDuration, scrollDuration);
        isAutoPlay = array.getBoolean(R.styleable.BannerLayout_isAutoPlay, isAutoPlay);
        defaultImage = array.getResourceId(R.styleable.BannerLayout_defaultImage, defaultImage);
        imageHeight = (int) array.getDimension(R.styleable.BannerLayout_imageHeight, -1);
        imageWidth = (int) array.getDimension(R.styleable.BannerLayout_imageWidth, -1);
        array.recycle();

        //绘制未选中状态图形
        LayerDrawable unSelectedLayerDrawable;
        LayerDrawable selectedLayerDrawable;
        GradientDrawable unSelectedGradientDrawable;
        unSelectedGradientDrawable = new GradientDrawable();

        //绘制选中状态图形
        GradientDrawable selectedGradientDrawable;
        selectedGradientDrawable = new GradientDrawable();
        switch (indicatorShape) {
            case rect:
                unSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                selectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
            case oval:
                unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
                selectedGradientDrawable.setShape(GradientDrawable.OVAL);
                break;
        }
        unSelectedGradientDrawable.setColor(unSelectedIndicatorColor);
        unSelectedGradientDrawable.setSize(unSelectedIndicatorWidth, unSelectedIndicatorHeight);
        unSelectedLayerDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        unSelectedDrawable = unSelectedLayerDrawable;

        selectedGradientDrawable.setColor(selectedIndicatorColor);
        selectedGradientDrawable.setSize(selectedIndicatorWidth, selectedIndicatorHeight);
        selectedLayerDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        selectedDrawable = selectedLayerDrawable;

//        Drawable drawable1=getResources().getDrawable(R.drawable.load_succeed);
//        Drawable drawable2=getResources().getDrawable(R.drawable.load_failed);
//        unSelectedDrawable = new LayerDrawable(new Drawable[]{drawable1});
//        selectedDrawable = new LayerDrawable(new Drawable[]{drawable2});
    }

    public void setDrawable(int choiceRes,int unChoiceRes){
        Drawable drawable1=getResources().getDrawable(unChoiceRes);
        Drawable drawable2=getResources().getDrawable(choiceRes);
        unSelectedDrawable = new LayerDrawable(new Drawable[]{drawable1});
        selectedDrawable = new LayerDrawable(new Drawable[]{drawable2});
    }
    //添加本地图片路径
    public void setViewRes(List<Integer> viewRes) {
        List<View> views = new ArrayList<>();
        itemCount = viewRes.size();
        //主要是解决当item为小于3个的时候滑动有问题，这里将其拼凑成3个以上
        if (itemCount < 1) {//当item个数0
            throw new IllegalStateException("item count not equal zero");
        } else if (itemCount < 2) {//当item个数为1
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(0), 0));
        } else if (itemCount < 3) {//当item个数为2
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(1), 1));
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(1), 1));
        } else {
            for (int i = 0; i < viewRes.size(); i++) {
                views.add(getImageView(viewRes.get(i), i));
            }
        }
        setViews(views);
    }


    public void setCornerImg(ArrayList<String> viewRes,int raduis){
        if(viewRes.size()==0)return;
        List<View> views = new ArrayList<>();
        itemCount = viewRes.size();
        //主要是解决当item为小于3个的时候滑动有问题，这里将其拼凑成3个以上
        if (itemCount < 1) {//当item个数0
            throw new IllegalStateException("item count not equal zero");
        } else if (itemCount < 2) {//当item个数为1
            views.add(getContentView(viewRes.get(0), 0,raduis));
            views.add(getContentView(viewRes.get(0), 0,raduis));
            views.add(getContentView(viewRes.get(0), 0,raduis));
        } else if (itemCount < 3) {//当item个数为2,raduis
            views.add(getContentView(viewRes.get(0), 0,raduis));
            views.add(getContentView(viewRes.get(1), 1,raduis));
            views.add(getContentView(viewRes.get(0), 0,raduis));
            views.add(getContentView(viewRes.get(1), 1,raduis));
        } else {
            for (int i = 0; i < viewRes.size(); i++) {
                views.add(getContentView(viewRes.get(i), i,raduis));
            }
        }
        setViews(views);
    }

    public View getContentView(String  res, final int position,int raduis){
        View v= LayoutInflater.from(getContext()).inflate(R.layout.image_corner,null);
        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        GlideUtils.loadRound(getContext(),res,imageView,raduis);
//        GlideApp.with(getContext()).load(res).placeholder(R.mipmap.placeholder_gray).into(imageView);
        imageView.setOnClickListener(v1 -> {
            if (onBannerItemClickListener != null) {
                onBannerItemClickListener.onItemClick(position);
            }
        });
        return v;
    }

    @NonNull
    private ImageView getImageView(Integer res, final int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setOnClickListener(v -> {
            if (onBannerItemClickListener != null) {
                onBannerItemClickListener.onItemClick(position);
            }
        });
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideUtils.load(getContext(),res,imageView);
        return imageView;
    }

    //添加网络图片路径
    public void setViewUrls(List<String> urls) {
            List<View> views = new ArrayList<>();
            itemCount = urls.size();
            if(itemCount==0)return;
            //主要是解决当item为小于3个的时候滑动有问题，这里将其拼凑成3个以上
            if (itemCount < 1) {//当item个数0
                throw new IllegalStateException("item count not equal zero");
            } else if (itemCount < 2) { //当item个数为1
                views.add(getImageView(urls.get(0), 0));
                views.add(getImageView(urls.get(0), 0));
                views.add(getImageView(urls.get(0), 0));
            } else if (itemCount < 3) {//当item个数为2
                views.add(getImageView(urls.get(0), 0));
                views.add(getImageView(urls.get(1), 1));
                views.add(getImageView(urls.get(0), 0));
                views.add(getImageView(urls.get(1), 1));
            } else {
                for (int i = 0; i < urls.size(); i++) {
                    views.add(getImageView(urls.get(i), i));
                }
            }
            setViews(views);
    }




    @NonNull
    private ImageView getImageView(String url, final int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setOnClickListener(v -> {
            if (onBannerItemClickListener != null) {
                onBannerItemClickListener.onItemClick(position);
            }
        });
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (getImageHeight() != -1) {
            ViewGroup.LayoutParams ivParams = this.getLayoutParams();
            ivParams.width = SizeUtils.getDisplayWidth(getContext());
            if(imageWidth!=-1){
                ivParams.width=imageWidth;
            }
            ivParams.height = getImageHeight();
            imageView.setLayoutParams(ivParams);
        }
        GlideUtils.load(getContext(),url,imageView);
//        GlideApp.with(getContext()).load(url).placeholder(R.mipmap.placeholder_gray).into(imageView);
        return imageView;
    }

    //添加任意View视图
    public void setViews(final List<View> views) {
        //初始化pager
        pager = new ViewPager(getContext());
        //添加viewpager到SliderLayout
        addView(pager);
        setSliderTransformDuration(scrollDuration);
        //初始化indicatorContainer
        indicatorContainer = new LinearLayout(getContext());
        indicatorContainer.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        switch (indicatorPosition) {
            case centerBottom:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case centerTop:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case leftBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case leftTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case rightBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case rightTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }
        //设置margin
        params.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);
        //添加指示器容器布局到SliderLayout
        //初始化指示器，并添加到指示器容器布局
        if (itemCount > 1){
            addView(indicatorContainer, params);
            for (int i = 0; i < itemCount; i++) {
                ImageView indicator = new ImageView(getContext());
                indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                indicator.setPadding(indicatorSpace, indicatorSpace, indicatorSpace, indicatorSpace);
                indicator.setImageDrawable(unSelectedDrawable);
                indicatorContainer.addView(indicator);
            }
        }

        LoopPagerAdapter pagerAdapter = new LoopPagerAdapter(views);
        pager.setAdapter(pagerAdapter);
        //设置当前item到Integer.MAX_VALUE中间的一个值，看起来像无论是往前滑还是往后滑都是ok的
        //如果不设置，用户往左边滑动的时候已经划不动了
        int targetItemPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % itemCount;
        pager.setCurrentItem(targetItemPosition);
        switchIndicator(targetItemPosition % itemCount);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (bannerListener != null){
                    bannerListener.onPageSelected(position % itemCount);
                }
                switchIndicator(position % itemCount);
            }
        });
        startAutoPlay();

    }


    //Arc Shape
  /*

    private int mWidth;
    private int mHeight;
    private int mArcHeight=SystemUtil.dip2px(getContext(),10);

    private enum ArcShape {
        inSide, outSide
    }
    private ArcShape arcShape = ArcShape.outSide;
    private Path createClipPath() {
        final Path path = new Path();
      *//*  path.moveTo(0, 0);
        path.lineTo(0, mHeight);
        path.quadTo(mWidth / 2, mHeight - 2 * mArcHeight, mWidth, mHeight);
        path.lineTo(mWidth, 0);
        path.close();
*//*
        path.moveTo(0, 0);
        path.lineTo(0, mHeight - mArcHeight);
        path.quadTo(mWidth / 2, mHeight + mArcHeight, mWidth, mHeight - mArcHeight);
        path.lineTo(mWidth, 0);
        path.close();
        return path;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            calculateLayout();
        }
    }

    Path clipPath = new Path();
    *//**
     *calculate layout
     *//*
    private void calculateLayout() {
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        if (mWidth > 0 && mHeight > 0) {

            clipPath = createClipPath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && arcShape != ArcShape.inSide) {
                setOutlineProvider(new ViewOutlineProvider() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setConvexPath(clipPath);
                    }
                });
            }
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mArcHeight > 0) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawPath(clipPath, paint);
            canvas.restoreToCount(saveCount);
            paint.setXfermode(null);
        } else {
            super.dispatchDraw(canvas);
        }
    }*/
     public void setSliderTransformDuration(int duration) {
         try {
             Field mScroller = ViewPager.class.getDeclaredField("mScroller");
             mScroller.setAccessible(true);
             FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), null, duration);
             mScroller.set(pager, scroller);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
    /**
     * 开始自动轮播
     */
    public void startAutoPlay() {
        stopAutoPlay(); // 避免重复消息
        if (itemCount >= 2) {
            if (isAutoPlay) {
                handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
            }
        }

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }


    /**
     * 停止自动轮播
     */
    public void stopAutoPlay() {
        if (isAutoPlay) {
            handler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 切换指示器状态
     *
     * @param currentPosition 当前位置
     */
    private void switchIndicator(int currentPosition) {
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            ((ImageView) indicatorContainer.getChildAt(i)).setImageDrawable(i == currentPosition ? selectedDrawable : unSelectedDrawable);
        }
    }


    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    public interface OnBannerItemClickListener {
        void onItemClick(int position);
    }

    public class LoopPagerAdapter extends PagerAdapter {
        private List<View> views;

        public LoopPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            //Integer.MAX_VALUE = 2147483647
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (views.size() > 0) {
                //position % view.size()是指虚拟的position会在[0，view.size()）之间循环
                View view = views.get(position % views.size());
                if (container.equals(view.getParent())) {
                    container.removeView(view);
                }
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            this(context, interpolator);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public interface BannerListener{
        void onPageSelected(int index);
    }
}
