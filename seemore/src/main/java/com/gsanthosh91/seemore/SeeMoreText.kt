package com.gsanthosh91.seemore

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat


class SeeMoreText : AppCompatTextView {

    companion object {

        private val DEFAULT_TRIM_LENGTH = 250
        private const val DEFAULT_SHOW_TRIM_EXPANDED_TEXT = true
        private val ELLIPSIZE = "... "

        private var readMore = true
        private var trimCollapsedText: CharSequence? = "show more"
        private var trimExpandedText: CharSequence? = "show less"
        private var colorClickableText = 0
        private var trimLength = 0
        private var mText: CharSequence? = ""
        private var showTrimExpandedText = true
        private var bufferType: BufferType? = null

    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.SeeMoreText,
                defStyleAttr,
                0
            )
            trimLength = typedArray.getInt(
                R.styleable.SeeMoreText_trimLength,
                DEFAULT_TRIM_LENGTH
            )
            showTrimExpandedText =
                typedArray.getBoolean(
                    R.styleable.SeeMoreText_showTrimExpandedText,
                    DEFAULT_SHOW_TRIM_EXPANDED_TEXT
                );
            colorClickableText = typedArray.getColor(
                R.styleable.SeeMoreText_colorClickableText, ContextCompat.getColor(
                    context,
                    R.color.accent
                )
            )


            trimCollapsedText = typedArray.getString(R.styleable.SeeMoreText_trimCollapsedText)
            if (trimCollapsedText.isNullOrEmpty()) {
                trimCollapsedText = resources.getString(R.string.show_more)
            }

            trimExpandedText = typedArray.getString(R.styleable.SeeMoreText_trimExpandedText)
            if (trimExpandedText.isNullOrEmpty()) {
                trimExpandedText = resources.getString(R.string.show_less)
            }

            invalidate()
            typedArray.recycle()
            setText()
        }
    }


    private fun setText() {
        super.setText(getDisplayableText(), bufferType)
        movementMethod = LinkMovementMethod.getInstance()
    }

    override fun setText(text: CharSequence, type: BufferType) {
        if (tag == null) {
            tag = text
        }
        mText = tag.toString()
        bufferType = type
        setText()
    }

    private fun getDisplayableText(): CharSequence? {
        return getTrimmedText(mText)
    }

    private fun getTrimmedText(text: CharSequence?): CharSequence? {
        if (text!!.length > trimLength) {
            if (readMore) {
                return updateCollapsedText();
            } else {
                return updateExpandedText();
            }
        }
        return text
    }


    private fun updateCollapsedText(): CharSequence? {
        val trimEndIndex: Int = trimLength + 1

        val s: SpannableStringBuilder = SpannableStringBuilder(mText, 0, trimEndIndex)
            .append(ELLIPSIZE)
            .append(trimCollapsedText)
        return addClickableSpan(s, trimCollapsedText!!)
    }

    private fun updateExpandedText(): CharSequence? {
        if (showTrimExpandedText) {
            val s: SpannableStringBuilder = SpannableStringBuilder(mText, 0, mText!!.length)
                .append(trimExpandedText)
            return addClickableSpan(s, trimExpandedText!!)
        }
        return mText
    }

    private fun addClickableSpan(s: SpannableStringBuilder, trimText: CharSequence): CharSequence? {
        s.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                readMore = !readMore
                setText()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = colorClickableText
            }
        }, s.length - trimText.length, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }
}

