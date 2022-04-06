package ru.mishin.utils;

import ru.mishin.utils.exceptions.TimeFormatException;

public class Time {
    public static final int MIN = 0;
    public static final int MAX_HOURS = 23;
    public static final int MAX_SECONDS_AND_MINUTES = 59;
    public static final String DEFAULT_TIME_FORMATTER = "%02d:%02d:%02d";


    public static String timeToFormattedHMS(String formatter,long seconds){
        int secs = (int) (seconds % 60);
        int minutes = (int) (seconds / 60);
        Integer hours = minutes / 60;
        minutes = minutes % 60;

        return String.format(formatter,hours,minutes,secs);
    }
    public static long timeToSeconds(int hours,int minutes,int seconds) throws TimeFormatException {
        if (hours < MIN){
            throw new TimeFormatException("Number of hours mustn't be negative, input: " + hours);
        }
        if(hours > MAX_HOURS) {
            throw new TimeFormatException("Number of hours mustn't more than 23, input: " + hours);
        }

        if (minutes < MIN){
            throw new TimeFormatException("Number of minutes mustn't be negative, input: " + hours);
        }
        if(seconds > MAX_SECONDS_AND_MINUTES) {
            throw new TimeFormatException("Number of minutes mustn't be more than 59, input: " + hours);
        }

        if (seconds < MIN){
            throw new TimeFormatException("Number of seconds mustn't be negative, input: " + hours);
        }
        if(seconds > MAX_SECONDS_AND_MINUTES) {
            throw new TimeFormatException("Number of seconds mustn't be be more than 59, input: " + hours);
        }
        return hours * 60 * 60 + minutes * 60 + seconds;
    }

}
