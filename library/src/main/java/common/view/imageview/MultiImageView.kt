package common.view.imageview

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.kotlin.library.glide.load
import common.presentation.utils.SizeUtils
import java.util.*

/**
 * 列表多图
 */
class MultiImageView : LinearLayout {

    // 照片的Url列表
    private var imagesList: List<String>? = null

    /**
     * 长度 单位为Pixel *
     */
    private var pxOneMaxWandH: Int = 0  // 单张图最大允许宽高
    private var pxMoreWandH = 0// 多张图的宽高
    private val pxImagePadding = SizeUtils.dp2px(context, 8f)// 图片间的间距

    private var MAX_PER_ROW_COUNT = 4// 每行显示最大数

    private var onePicPara: LinearLayout.LayoutParams? = null
    private var morePara: LinearLayout.LayoutParams? = null
    private var moreParaColumnFirst: LinearLayout.LayoutParams? = null
    private var rowPara: LinearLayout.LayoutParams? = null

    private var mOnItemClickListener: OnItemClickListener? = null

    private var raduis = 0

    val imageList: ArrayList<String>
        get() = imagesList as ArrayList<String>

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setList(lists: List<String>, raduis: Int) {
        this.raduis = raduis
        setList(lists)
    }

    @Throws(IllegalArgumentException::class)
    fun setList(lists: List<String>?) {
        if (lists == null) {
            throw IllegalArgumentException("imageList is null...")
        }
        imagesList = lists

        if (MAX_WIDTH > 0) {
            // 如果需要两张和四张图横向铺满，这里去掉注释即可。
            //            if (lists.size() == 2 || lists.size() == 4) {
            //                pxMoreWandH = (MAX_WIDTH - pxImagePadding) / 2;
            //            } else {
            pxMoreWandH = (MAX_WIDTH - pxImagePadding * 2) / 3 //解决右侧图片和内容对不齐问题
            //            }
            pxOneMaxWandH = MAX_WIDTH * 2 / 3  // 一张图的时候，图片宽度
            initImageLayoutParams()
        }

        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (MAX_WIDTH == 0) {
            val width = measureWidth(widthMeasureSpec)
            if (width > 0) {
                MAX_WIDTH = width - paddingLeft - paddingRight
                if (imagesList != null && imagesList!!.size > 0) {
                    setList(imagesList)
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private fun measureWidth(measureSpec: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize
        } else {
            // Measure the text
            // prizeStatus = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == View.MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    private fun initImageLayoutParams() {
        val wrap = LinearLayout.LayoutParams.WRAP_CONTENT
        val match = LinearLayout.LayoutParams.MATCH_PARENT

        onePicPara = LinearLayout.LayoutParams(pxOneMaxWandH, wrap)

        moreParaColumnFirst = LinearLayout.LayoutParams(pxMoreWandH, pxMoreWandH)
        morePara = LinearLayout.LayoutParams(pxMoreWandH, pxMoreWandH)
        morePara!!.setMargins(pxImagePadding, 0, 0, 0)

        rowPara = LinearLayout.LayoutParams(match, wrap)
    }

    // 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
    private fun initView() {
        this.orientation = LinearLayout.VERTICAL
        this.removeAllViews()
        if (MAX_WIDTH == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
            addView(View(context))
            return
        }

        if (imagesList == null || imagesList!!.size == 0) {
            return
        }

        if (imagesList!!.size == 1) {
            addView(createImageView(0, false))
        } else {
            val allCount = imagesList!!.size
            if (allCount == 4) {
                MAX_PER_ROW_COUNT = 2
            } else {
                MAX_PER_ROW_COUNT = 4
            }
            val rowCount = allCount / MAX_PER_ROW_COUNT + if (allCount % MAX_PER_ROW_COUNT > 0) 1 else 0// 行数
            for (rowCursor in 0 until rowCount) {
                val rowLayout = LinearLayout(context)
                rowLayout.orientation = LinearLayout.HORIZONTAL

                rowLayout.layoutParams = rowPara
                if (rowCursor != 0) {
                    rowLayout.setPadding(0, pxImagePadding, 0, 0)
                }

                var columnCount = if (allCount % MAX_PER_ROW_COUNT == 0)
                    MAX_PER_ROW_COUNT
                else
                    allCount % MAX_PER_ROW_COUNT//每行的列数
                if (rowCursor != rowCount - 1) {
                    columnCount = MAX_PER_ROW_COUNT
                }
                addView(rowLayout)

                val rowOffset = rowCursor * MAX_PER_ROW_COUNT// 行偏移
                for (columnCursor in 0 until columnCount) {
                    val position = columnCursor + rowOffset
                    rowLayout.addView(createImageView(position, true))
                }
            }
        }
    }

    private fun createImageView(position: Int, isMultiImage: Boolean): ImageView {
        var url = ""
        if (!TextUtils.isEmpty(imagesList!![position])) {
            url = imagesList!![position]
        }

        val imageView = ColorFilterImageView(context)
        if (isMultiImage) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.layoutParams = if (position % MAX_PER_ROW_COUNT == 0) moreParaColumnFirst else morePara
        } else {
            imageView.adjustViewBounds = true
            imageView.scaleType = ImageView.ScaleType.FIT_START
            imageView.maxHeight = pxOneMaxWandH
            imageView.layoutParams = onePicPara
        }

        imageView.id = url.hashCode()
        imageView.setOnClickListener { v ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(v, position)
            }
        }
        // 加载网络图片/设置图片显示
        if (raduis != 0) {
            imageView.load(url, raduis.toFloat())
        } else {
            imageView.load(url)
        }
        return imageView
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    companion object {
        var MAX_WIDTH = 0
    }
}