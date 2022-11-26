package com.ferdifir.slowgram.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.ferdifir.slowgram.R

class EditTextEmail : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        doOnTextChanged { s, _, _, _ ->
            if (s != null) {
                error = if (s.isNotEmpty()) {
                    if (!s.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                        context.getString(R.string.UI_validation_invalid_email)
                    } else null
                } else {
                    context.getString(R.string.UI_validation_empty_email)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Email"
        context.apply {
            background = ContextCompat.getDrawable(this, R.drawable.bg_form_input)
            setTextColor(ContextCompat.getColor(this, R.color.black))
            setHintTextColor(ContextCompat.getColor(this, R.color.gray))
        }
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}
