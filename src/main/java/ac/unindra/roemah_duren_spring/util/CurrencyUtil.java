package ac.unindra.roemah_duren_spring.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    public static String formatCurrencyIDR(Long item) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();

        decimalFormatSymbols.setCurrencySymbol("Rp");
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        return decimalFormat.format(item);
    }

    public static String getNumericValue(String value) {
        return value.replaceAll("\\D", "");
    }
}
