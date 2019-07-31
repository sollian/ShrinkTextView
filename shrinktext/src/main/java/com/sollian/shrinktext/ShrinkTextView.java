package com.sollian.shrinktext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author lishouxian on 2017/12/11.
 */
public class ShrinkTextView extends TextView {
    private static final int COLOR_HIGHLIGHT = 0xff0099cc;

    private int color;
    private CharSequence originText;
    private boolean isRefreshingText;

    private int curMaxLines;
    private int lastMaxLine;

    public ShrinkTextView(Context context) {
        super(context);
    }

    public ShrinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShrinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void toggle() {
        setMaxLines(lastMaxLine);
        setText(originText);
        requestLayout();
    }

    @Override
    public void setMaxLines(int maxlines) {
        super.setMaxLines(maxlines);
        lastMaxLine = curMaxLines;
        curMaxLines = maxlines;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        setEllipsize(TextUtils.TruncateAt.END);
        if (!isRefreshingText) {
//            if (TextUtils.equals(originText, text)) {
//                return;
//            }
            originText = text;
        }
        super.setText(text, type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (curMaxLines <= 0) {
            curMaxLines = Integer.MAX_VALUE;
        }
        if (lastMaxLine <= 0) {
            lastMaxLine = Integer.MAX_VALUE;
        }

        Layout layout = getLayout();
        if (layout == null) {
            super.onDraw(canvas);
            return;
        }

        CharSequence charSequence = getText();

        int textLines = layout.getLineCount();
        if (textLines < curMaxLines || layout.getEllipsisCount(textLines - 1) == 0) {
            //行数不足最大行，或者最后一行没有被省略，说明当前文本被完全展示
            super.onDraw(canvas);
            return;
        }

        int lastLineStart = layout.getLineStart(textLines - 1);
        CharSequence textBeforeLastLine = charSequence.subSequence(0, lastLineStart);
        CharSequence textInLastLine = charSequence.subSequence(lastLineStart,
                lastLineStart + layout.getEllipsisStart(textLines - 1));
//        if (TextUtils.isEmpty(textInLastLine)) {
//            super.onDraw(canvas);
//            return;
//        }

        Paint paint = layout.getPaint();
        CharSequence textInlastLineSuffix = addSuffix(textInLastLine);
        float wLastLineEllipsis = paint.measureText(textInlastLineSuffix.toString());

        while (wLastLineEllipsis > layout.getEllipsizedWidth()) {
            textInLastLine = textInLastLine.subSequence(0, textInLastLine.length() - 1);
            textInlastLineSuffix = addSuffix(textInLastLine);
            wLastLineEllipsis = paint.measureText(textInlastLineSuffix.toString());
        }

        Editable result = new SpannableStringBuilder(textBeforeLastLine);
        result.append(textInlastLineSuffix);

        isRefreshingText = true;
        setText(result);
        isRefreshingText = false;
    }

    private CharSequence addSuffix(CharSequence text) {
        Editable processedText = new SpannableStringBuilder(text);
        processedText.append("…");

        SpannableString ss = new SpannableString("全部");

        if (color == 0) {
            color = COLOR_HIGHLIGHT;
        }
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        ss.setSpan(colorSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        processedText.append(ss);
        return processedText;
    }

    public void setHightlightColor(int color) {
        this.color = color;
    }
}
