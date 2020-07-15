package iss.team1.ca.memorygame.comm.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static long getCurrentTimestamp(){
        return new Date().getTime();
    }

    public static String convertTimestampToyyyyMMddHHmm(long timestamp){
        Timestamp ts = new Timestamp(timestamp);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String tsStr = sdf.format(ts);
        return tsStr;
    }
}
