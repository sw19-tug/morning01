package at.tugraz.ist.swe.note;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

/**
 * Adapted from original Android Source Code in file android-27/android/widget/MultiAutoCompleteTextView.java
 */
public class CharacterTokenizer implements MultiAutoCompleteTextView.Tokenizer {
    private final char character;

    public CharacterTokenizer(char character) {
        this.character = character;
    }

    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;

        while (i > 0 && text.charAt(i - 1) != this.character) {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }

        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len) {
            if (text.charAt(i) == this.character) {
                return i;
            } else {
                i++;
            }
        }

        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == this.character) {
            return text;
        } else {
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + String.valueOf(this.character));
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                        Object.class, sp, 0);
                return sp;
            } else {
                return text + String.valueOf(this.character);
            }
        }
    }
}