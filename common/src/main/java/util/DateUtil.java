package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author weigangpeng
 * @date 2017/11/29 ÏÂÎç3:50
 */

public class DateUtil {

    public static final String TIME_PATTON_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTON_DEFAULT = "yyyy-MM-dd";


    public static String format(Date date){
        return format(date, null);
    }

    public static String format(Date date, String format) {
        if(format == null){
            format = TIME_PATTON_DEFAULT;
        }
        if (date == null){
            return null;
        }

        SimpleDateFormat dateFromat = new SimpleDateFormat();
        dateFromat.applyPattern(format);
        return dateFromat.format(date);
    }


    public static Date parseDate(String dateValue) {
        return parseDate(dateValue, DATE_PATTON_DEFAULT);
    }


    public static Date parseDate(String dateValue, String strFormat) {
        if (dateValue == null) {
            return null;
        }
        if (strFormat == null) {
            strFormat = TIME_PATTON_DEFAULT;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date newDate = null;

        try {
            newDate = dateFormat.parse(dateValue);
        } catch (ParseException pe) {
            newDate = null;
        }

        return newDate;
    }
}
