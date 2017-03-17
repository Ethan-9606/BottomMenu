package com.ethan.menu.lib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by zhangshusen on 2017/3/14.
 * <p>
 * 从底部弹起的菜单，xml中第一个child为展开按钮
 */

public class BottomMenu extends ViewGroup implements View.OnClickListener {


    /**
     * 默认值
     */
    private static final int DEFAULT_ANMI_DURATION = 300;
    private static final int DEFAULT_ARC_HEIGHT = 180;
    private static final int DEFAULT_HEGIHT = 150;


    /**
     * 菜单弹出半径
     */
    private int mRadius;
    /**
     * 菜单距离底部距离  便于调整位置
     */
    private int mMarginBottom;
    /**
     * 主菜单按钮
     */
    private View mButton;


    /**
     * 背景弧最终半径
     */
    private int mBackgroundRadius;
    /**
     * 绘制过程中背景弧的动态半径
     */
    private int mDrawingBackgroundRadius;
    /**
     * 背景高度
     */
    private int mBackgrondheight;
    /**
     * 背景弧高（最高点）
     */
    private int mBackgroudArcHeight;

    /**
     * 菜单元素距边缘的距离
     */
    private int mItemMarginEdge;

    /**
     * 菜单放置的角度区间
     */
    private double mItemAngleSection;
    /**
     * 背景色
     */
    @ColorInt
    private int mBackgroudColor;
    // 画背景圆心坐标
    private int mDrawX;
    private int mDrawY;

    /**
     * 圆心与屏幕底边距离
     */
    private int mPointOffetScreenY;

    private int mStartDrawingY;
    private int mDrawingY;
    private int mDrawYOffset;

    private Paint mPaint;

    // 对外提供的接口
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnAnimationRunningListener mOnAnimationRunningListener;

    // 状态
    private Status mStatus = Status.CLOSE;

    @IntRange(from = 10, to = 2000)
    private int mAnimDuration;
    private boolean isAnimRunning = false;


    public BottomMenu(Context context) {
        this(context, null);
    }

    public BottomMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initBackground();
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BottomMenu, defStyleAttr, 0);


        mMarginBottom = (int) a.getDimension(R.styleable.BottomMenu_menu_marginBottom,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
                        getResources().getDisplayMetrics()));

        mBackgroudArcHeight = (int) a.getDimension(R.styleable.BottomMenu_menu_backgroundArcHeghit,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ARC_HEIGHT,
                        getResources().getDisplayMetrics()));

        mBackgrondheight = (int) a.getDimension(R.styleable.BottomMenu_menu_backgroundHeight,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEGIHT,
                        getResources().getDisplayMetrics()));

        mItemMarginEdge = (int) a.getDimension(R.styleable.BottomMenu_menu_item_marginEdge,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
                        getResources().getDisplayMetrics()));

        mBackgroudColor = a.getColor(R.styleable.BottomMenu_menu_backgroundColor, Color.BLUE);

        mAnimDuration = a.getInt(R.styleable.BottomMenu_menu_animDuration, DEFAULT_ANMI_DURATION);

        a.recycle();

        if (mBackgrondheight >= mBackgroudArcHeight) {
            throw new RuntimeException("Backgrondheight must be smaller than BackgroudArcHeight");
        }

    }

    private void initBackground() {
        setBackgroundColor(Color.argb(0, 255, 255, 255));
        mPaint = new Paint();
        mPaint.setColor(mBackgroudColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int widthSpec = 0;
            int heightSpec = 0;
            LayoutParams params = v.getLayoutParams();
            if (params.width > 0) {
                widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
            } else if (params.width == -1) {
                widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            } else if (params.width == -2) {
                widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
            }

            if (params.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
            } else if (params.height == -1) {
                heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
            } else if (params.height == -2) {
                heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
            }
            v.measure(widthSpec, heightSpec);
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = Math.min(widthSize, getChildAt(1).getMeasuredWidth() * (getChildCount() - 1));
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = mBackgroudArcHeight;
        }
        setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() < 2) {
            throw new IllegalStateException("At least one menu item");
        }
        layoutCenterButton();
        layoutChildItems();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mDrawX, mDrawingY, mDrawingBackgroundRadius, mPaint);
    }

    private void layoutCenterButton() {

        mButton = getChildAt(0);
        int bWidth = mButton.getMeasuredWidth();
        int bHeight = mButton.getMeasuredHeight();
        int bX = getMeasuredWidth() / 2 - bWidth / 2;
        int bY = getMeasuredHeight() - bHeight - mMarginBottom;
        mButton.layout(bX, bY, bX + bWidth, bY + bHeight);
        mButton.setOnClickListener(this);

        mDrawX = getMeasuredWidth() / 2;
        int h = mBackgroudArcHeight - mBackgrondheight;
        mBackgroundRadius = (int) (h / 2 + Math.pow(getMeasuredWidth(), 2) / (8 * h));
        mDrawY = getMeasuredHeight() + mBackgroundRadius - mBackgroudArcHeight;

        mStartDrawingY = getMeasuredHeight() - mMarginBottom - bHeight;
        mDrawYOffset = mDrawY - mStartDrawingY;

        mRadius = mBackgroundRadius - mItemMarginEdge - mMarginBottom;
        mPointOffetScreenY = mDrawY - getMeasuredHeight();
        mItemAngleSection = Math.atan((double) mDrawX / (mPointOffetScreenY + mBackgrondheight / 2)) * 2;


    }

    private void layoutChildItems() {
        int count = getChildCount();
        double baseAngle = (Math.PI - mItemAngleSection) / 2;
        for (int i = 1; i < count; i++) {
            View child = getChildAt(i);
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();

            int l = (int) (getMeasuredWidth() / 2 - mRadius * Math.cos(mItemAngleSection / count * i + baseAngle) - cWidth / 2);
            int t = (int) (getMeasuredHeight() - (mRadius * Math.sin(mItemAngleSection / count * i + baseAngle) - mPointOffetScreenY)
                    - mMarginBottom);

            child.layout(l, t, l + cWidth, t + cHeight);
            child.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mStatus == Status.OPEN) {
            if (!isAnimRunning) {
                toggleMenu(mAnimDuration);
            }
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (!isAnimRunning) {
            toggleMenu(mAnimDuration);
        }
    }

    public void toggleMenu(final int duration) {

        isAnimRunning = true;
        if (mOnAnimationRunningListener != null) {
            mOnAnimationRunningListener.onAnimationStart();
        }
        int count = getChildCount();
        for (int i = 1; i < count; i++) {

            final View childView = getChildAt(i);

            toggleMenuItemAnim(i, duration);
            final int pos = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isAnimRunning) {
                        menuItemClickAnim(pos);
                        toggleBackgroundAnim(duration);
                    }
                }
            });
        }
        toggleBackgroundAnim(duration);

    }

    private void toggleMenuItemAnim(int pos, int duration) {

        final View childView = getChildAt(pos);

        TranslateAnimation tranlateAnim = null;
        AlphaAnimation alphaAnimation = null;
        // to open
        if (mStatus == Status.CLOSE) {
            tranlateAnim = new TranslateAnimation(0, 0, childView.getMeasuredHeight(), 0);
            tranlateAnim.setInterpolator(new DecelerateInterpolator());

            alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(duration / 2);

            childView.setClickable(true);
            childView.setFocusable(true);
        } else {
            tranlateAnim = new TranslateAnimation(0, 0, 0, childView.getMeasuredHeight());
            tranlateAnim.setInterpolator(new DecelerateInterpolator());

            alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(duration / 2);

        }
        tranlateAnim.setDuration(duration);

        AnimationSet animset = new AnimationSet(false);
        animset.addAnimation(tranlateAnim);
        animset.addAnimation(alphaAnimation);

        animset.setFillAfter(true);
        childView.startAnimation(animset);

    }

    /**
     * 添加menuItem的点击动画
     */
    private void menuItemClickAnim(int pos) {
        isAnimRunning = true;
        if (mOnAnimationRunningListener != null) {
            mOnAnimationRunningListener.onAnimationStart();
        }
        for (int i = 1; i < getChildCount(); i++) {

            View childView = getChildAt(i);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(300, childView, pos - 1));
            } else {
                childView.startAnimation(scaleSmallAnim(300, childView));
            }
        }
    }

    private Animation scaleSmallAnim(int duration, final View child) {

        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        alphaAnim.setFillAfter(true);
        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);

        return animationSet;

    }

    private Animation scaleBigAnim(int duration, final View child, final int position) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);

        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onClick(child, position);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animationSet;

    }


    private void toggleBackgroundAnim(int duration) {
        final ValueAnimator animator;
        if (mStatus == Status.OPEN) {
            animator = ValueAnimator.ofFloat(1, 0);
        } else {
            animator = ValueAnimator.ofFloat(0, 1);
        }
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 变化区间设在0~1  方便映射到其他区间
                float cVal = (float) animation.getAnimatedValue();
                setBackgroundColor(Color.argb((int) (cVal * 180), 255, 255, 255));
                mDrawingBackgroundRadius = (int) (mBackgroundRadius * cVal);
                mDrawingY = (int) (mStartDrawingY + mDrawYOffset * cVal);
                invalidate();
                if (mOnAnimationRunningListener != null) {
                    mOnAnimationRunningListener.onAnimationRunning(cVal);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mStatus == Status.CLOSE) {
                    for (int i = 1; i < getChildCount(); i++) {
                        getChildAt(i).clearAnimation();
                        getChildAt(i).setFocusable(false);
                        getChildAt(i).setClickable(false);
                        getChildAt(i).setVisibility(GONE);
                    }
                }
                isAnimRunning = false;
                if (mOnAnimationRunningListener != null) {
                    mOnAnimationRunningListener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        changeStatus();
    }

    private void changeStatus() {
        mStatus = (mStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }


    public View getToggleView() {
        return mButton;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    public void setmOnAnimationRunningListener(OnAnimationRunningListener listener) {
        mOnAnimationRunningListener = listener;
    }

    public boolean isOpen() {
        return mStatus == Status.OPEN;
    }

    /**
     * 点击子菜单项的回调接口
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    /**
     * 动画监听接口
     */
    public interface OnAnimationRunningListener {

        void onAnimationStart();

        void onAnimationRunning(float val);

        void onAnimationEnd();
    }

    /**
     * 菜单的状态枚举
     */
    public enum Status {
        OPEN, CLOSE
    }
}
