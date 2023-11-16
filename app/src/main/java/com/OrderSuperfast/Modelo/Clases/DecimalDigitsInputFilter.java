package com.OrderSuperfast.Modelo.Clases;

import android.text.InputFilter;
import android.text.Spanned;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String decimalPattern = "([0-9]{1,3}(,[0-9]{3})*|(\\d+))\\.\\d{0,2}";

        String newString = dest.toString().substring(0, dstart) + source.toString().substring(start, end) + dest.toString().substring(dend);
        String newStringWithCommas = newString.replaceAll(",", "");
        Matcher matcher = Pattern.compile(decimalPattern).matcher(newStringWithCommas);

        if (!matcher.matches()) {
            return "";
        }

        String formattedString = NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(newStringWithCommas));
        return formattedString.substring(0, formattedString.length() - 3) + formattedString.substring(formattedString.length() - 2);
    }
}
