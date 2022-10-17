package com.gemadec.cimr.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String GetDateFormat(String datestring) {
        String finaldate = datestring;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = fmt.parse(datestring);
        } catch (ParseException e) {
            Log.i("SecondstepActivity", "GetDateFormat error : "+e.getMessage());
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyMMdd");
        finaldate = fmtOut.format(date);
        Log.i("SecondstepActivity", "finaldate : "+finaldate);
        return finaldate;
    }
}
