package ac.unindra.roemah_duren_spring.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    public static String strDateFromLocalDateTime(String localDateTimeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime transDate = LocalDateTime.parse(localDateTimeString, dateTimeFormatter);

        DateTimeFormatter indonesianFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        return indonesianFormatter.format(transDate);
    }

}
