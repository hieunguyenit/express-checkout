package com.mbv.mca.checkout.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static final String DATETIME_DD_MM_YYYY 	= "dd/MM/yyyy HH:mm:ss";
	
	public static final String DATE_YYYY_MM_DD 		= "yyyyMMdd";
	
	public static SimpleDateFormat sdf;
	
	public static String formatDate(Date date, String pattern) {
		sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}	
	
}
