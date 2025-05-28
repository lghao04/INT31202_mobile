package com.example.project1.database;

public class OrderTime {
    private int date, day, hours, minutes, month, seconds, year;
    private long time;
    private int timezoneOffset;

    public OrderTime() {
    }

    // Getters & setters
    public int getDate() { return date; }
    public void setDate(int date) { this.date = date; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }

    public int getMinutes() { return minutes; }
    public void setMinutes(int minutes) { this.minutes = minutes; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getSeconds() { return seconds; }
    public void setSeconds(int seconds) { this.seconds = seconds; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public int getTimezoneOffset() { return timezoneOffset; }
    public void setTimezoneOffset(int timezoneOffset) { this.timezoneOffset = timezoneOffset; }
}