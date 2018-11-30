package demo.sollian.com.shrinktextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * @author lishouxian on 2017/12/11.
 */
public class ShrinkTextView extends TextView {
    private static final int HIGHLIGHT_COLOR_RES_ID = 0x3e609e;

    private boolean isPrepared;
    private int color;
    private CharSequence originText;
    private boolean isRefreshingText;

    private int curMaxLines;
    private int lastMaxLine;

    public ShrinkTextView(Context context) {
        super(context);
    }

    public ShrinkTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShrinkTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void toggle() {
        setMaxLines(lastMaxLine);
        setText(originText);
    }

    @Override
    public void setMaxLines(int maxlines) {
        super.setMaxLines(maxlines);
        curMaxLines = maxlines;
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
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
        if (isPrepared) {
            isPrepared = false;
            super.onDraw(canvas);
        } else {
            innerRefresh(canvas);
        }
    }

    private void innerRefresh(Canvas canvas) {
        if (curMaxLines <= 0) {
            curMaxLines = Integer.MAX_VALUE;
        }

        if (lastMaxLine <= 0) {
            lastMaxLine = Integer.MAX_VALUE;
        }

        Layout layout = getLayout();
        if (layout == null) {
            return;
        }

        CharSequence charSequence = getText();

        int textLines = layout.getLineCount();
        if (textLines < curMaxLines || layout.getEllipsisCount(textLines - 1) == 0) {
            isPrepared = true;
            invalidate();
            return;
        }

        int lastLineStart = layout.getLineStart(textLines - 1);
        CharSequence textBeforeLastLine = charSequence.subSequence(0, lastLineStart);
        CharSequence textInLastLine = charSequence.subSequence(lastLineStart,
                lastLineStart + layout.getEllipsisStart(textLines - 1));
        if (TextUtils.isEmpty(textInLastLine)) {
            return;
        }

        Paint paint = layout.getPaint();
        CharSequence textInlastLineSuffix = addSuffix(textInLastLine);
        float wLastLineEllipsis = paint.measureText(textInlastLineSuffix.toString());

        while (wLastLineEllipsis > layout.getEllipsizedWidth()) {
            textInLastLine = textInLastLine.subSequence(0, textInLastLine.length() - 1);
            textInlastLineSuffix = addSuffix(textInLastLine);
            wLastLineEllipsis = paint.measureText(textInlastLineSuffix.toString());
        }
        Log.e("---", textInLastLine.toString());

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
            color = getResources().getColor(HIGHLIGHT_COLOR_RES_ID);
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
