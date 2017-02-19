/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author stevan
 */
public class DateHelper {
    
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        if (date1 == null || date2 == null || timeUnit == null) {
            return -1;
        }
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    public static String getFormatedDate(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }
    
    public static Date getDateFromString(String date) throws ParseException{
        return DATE_FORMAT.parse(date);
    }
    
    public static Date getTimestampFromString(String timestamp) throws ParseException{
        return TIMESTAMP_FORMAT.parse(timestamp);
    } 

    boolean isWithinRange(Date checkDate, Date startDate, Date endDate) {
        return !(checkDate.before(startDate) || checkDate.after(endDate));
    }
}
