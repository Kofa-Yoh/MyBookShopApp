package com.example.MyBookShopApp.books.books;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@NoArgsConstructor
public class DateFilterDto {

    private String begin;

    private String end;

    private final String DATE_FORMAT = "dd.MM.yyyy";

    public DateFilterDto(String begin, String end) {
        this.begin = begin;
        this.end = end;
    }

    public DateFilterDto(Date beginDate, Date endDate) {
        this.begin = new SimpleDateFormat(DATE_FORMAT).format(beginDate);
        this.end = new SimpleDateFormat(DATE_FORMAT).format(endDate);
    }

    public Date getBeginDate() {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(begin);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date getEndDate() {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(end);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setBegin(Date beginDate) {
        this.begin = new SimpleDateFormat(DATE_FORMAT).format(beginDate);
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setEnd(Date endDate) {
        this.end = new SimpleDateFormat(DATE_FORMAT).format(end);
    }
}
