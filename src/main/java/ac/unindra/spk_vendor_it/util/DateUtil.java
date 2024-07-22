package ac.unindra.spk_vendor_it.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {
    public static LocalDate localDateFromString(String dateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, dateTimeFormatter);
    }


    public static String strDateFromLocalDateTime(String localDateTimeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime transDate = LocalDateTime.parse(localDateTimeString, dateTimeFormatter);

        DateTimeFormatter indonesianFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        return indonesianFormatter.format(transDate);
    }

    public static String strDateFromLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter indonesianFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy ", new Locale("id", "ID"));
        return indonesianFormatter.format(localDateTime);
    }

    public static String strDateFromLocalDateTime(LocalDate localDate) {
        DateTimeFormatter indonesianFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy ", new Locale("id", "ID"));
        return indonesianFormatter.format(localDate);
    }

    public static String strDateTimeFromLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter indonesianFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
        return indonesianFormatter.format(localDateTime);
    }

    public static String strDateTimeFromString(String localDateTimeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString, dateTimeFormatter);
        return strDateTimeFromLocalDateTime(localDateTime);
    }

}
