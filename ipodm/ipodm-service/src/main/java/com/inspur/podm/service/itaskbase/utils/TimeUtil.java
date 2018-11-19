package com.inspur.podm.service.itaskbase.utils;

import java.util.Date;

import org.quartz.impl.triggers.CronTriggerImpl;

public class TimeUtil {
	
	/**
	 * 1天.
	 */
	private static int dayTime = 24*60;
	
	/**
	 * 1小时.
	 */
	private static int hourTime = 60;
	
	/**
	 * 半小时.
	 */
	private static int halfHour = 30;
//    
//	/**
//	 * 时间解析.
//	 * @param dateStr 时间格式数据.
//	 * @return String[]
//	 */
//    public String[] convertToTime(String dateStr) {
//        String[] timeArray = new String[6];
//        String[] str = dateStr.split(" ");
//        timeArray[0] = str[0].split("-")[0];
//        timeArray[1] = str[0].split("-")[1];
//        timeArray[2] = str[0].split("-")[2];
//        timeArray[3] = str[1].split(":")[0];
//        timeArray[4] = str[1].split(":")[1];
//        // timeArray[MagicNumber.M5] = str[1].split(":")[2];
//        return timeArray;
//    }
//
//    /**
//     * 定时时间串拼接.
//     * @param periodType 类型
//     *   1每天  2每周  3每月 4自定义时间
//     *   5每隔几分钟  6每隔几小时
//     * @param year 年
//     * @param month 月
//     * @param day 日
//     * @param hour 时
//     * @param minute 分
//     * @return string
//     */
//    public String getPeriodTime(String periodType, String year, String month, String day, String hour, String minute) {
//        String runTime = null;
//        if ("1".equals(periodType)) {
//            // 日计划
//            runTime = "0 " + minute + " " + hour + " * * ?";
//        } else if ("2".equals(periodType)) {
//            // 周计划
//            runTime = "0 " + minute + " " + hour + " ? * " + day;
//        } else if ("3".equals(periodType)) {
//            // 月计划
//            runTime = "0 " + minute + " " + hour + " " + day + " * ?";
//        } else if ("4".equals(periodType)) {
//            // 自定义计划
//            runTime = "0 " + minute + " " + hour + " " + day + " " + month + " ? " + year;
//        } else if ("5".equals(periodType)) {
//        	// 每隔几分钟
//            runTime = "0 0/" + minute + " * * * ?";
//        } else if ("6".equals(periodType)) {
//        	// 每隔几小时
//        	runTime = "0 0 0/"+ hour + " * * ?";
//        } else {
//        	runTime = null;
//        }
//        return runTime;
//    }
//
//    /**
//     * 根据传入的日期进行定时时间转换.
//     * @param periodType 定时类型.
//     * @param dateStr 日期
//     * @return String
//     */
//    public String getTimingTime(String periodType, String dateStr) {
//       String[] time = convertToTime(dateStr);
//       String year = time[0];
//       String month = time[1];
//       String day = time[2];
//       String hour = time[3];
//       String minute = time[4];
//       return getPeriodTime(periodType, year, month, day, hour, minute);
//    }
    
	/**
	 * 判断cron表达式是否有效.
	 * @param cron
	 * @return boolean
	 */
    public static boolean isValidExpression(String cron) {
    	CronTriggerImpl trigger = new CronTriggerImpl();
    	try {
    		trigger.setCronExpression(cron);
    		Date date = trigger.computeFirstFireTime(null);
    		return date != null && date.after(new Date());
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    /**
     * 根据采集分钟值获取cron
     * @param time 采集频率（分钟）
     * @return cron
     */
    public static String getAcquisitionCron(int time) {
    	String cron = "0 0/5 * * * ? *";
    	//分钟
    	if (time < hourTime) {
    		cron = "0 " + "0/" + time + " * * * ? *";
        //小时
    	} else if (time < dayTime) {
    		if (time % hourTime > halfHour) {
    			cron = "0 0 0/" + (time/hourTime + 1) + " * * ? *";
    		} else {
    			cron = "0 0 0/" + time/hourTime + " * * ? *";
    		}
    	}
    	return cron;
    }
}
