package com.ferdifir.slowgram.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ferdifir.slowgram.R


class EditTextPassword : AppCompatEditText {

    private var charLength = 0

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

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                charLength = s.length
                error =
                    if (charLength < 6) context.getString(R.string.UI_validation_password_rules) else null
            }

            override fun afterTextChanged(edt: Editable?) {

            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "Password"
        context.apply {
            background = ContextCompat.getDrawable(this, R.drawable.bg_form_input)
            setTextColor(ContextCompat.getColor(this, R.color.black))
            setHintTextColor(ContextCompat.getColor(this, R.color.gray))
        }
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}