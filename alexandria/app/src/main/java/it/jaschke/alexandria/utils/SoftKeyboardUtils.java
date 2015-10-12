package it.jaschke.alexandria.utils;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by iluz on 11/10/15.
 */
public class SoftKeyboardUtils {
    public static void hideSoftKeyboard(Context context, IBinder windowToken) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
    }
}
