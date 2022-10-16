package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssemblerUtils {
    private static final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat longFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public static String toDTODate(Date date) {
        return formatter.format(date);
    }

    public static String toDTOLongDate(Date date) {
        return longFormatter.format(date);
    }

    public static Date fromDTODate(String date) {
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Error formatting date '%s' with format 'dd/MM/yyyy'", date), e);
        }
    }
}
