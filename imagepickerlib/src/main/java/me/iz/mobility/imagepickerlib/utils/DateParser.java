/*
 * Copyright 2015 Basit Parkar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  @date 2/25/15 10:26 AM
 *  @modified 2/25/15 10:26 AM
 */

package me.iz.mobility.imagepickerlib.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A helper class to conveniently parse date information.
 */
@SuppressLint("SimpleDateFormat")
public class DateParser {
    //ISO 8601 international standard date format
    public final static String dateFormat = "yyyy-MM-dd HH:mm:ss.SSSZ";

    public final static TimeZone utc = TimeZone.getTimeZone("UTC");

    /**
     * Converts a Date object to a string representation.
     *
     * @param date
     * @return date as String
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        } else {
            DateFormat df = new SimpleDateFormat(dateFormat);
            df.setTimeZone(utc);
            return df.format(date);
        }
    }

    /**
     * Converts a string representation of a date to its Date object.
     *
     * @param dateAsString
     * @return Date
     */
    public static Date stringToDate(String dateAsString) {
        try {
            DateFormat df = new SimpleDateFormat(dateFormat);
            df.setTimeZone(utc);
            return df.parse(dateAsString);
        } catch (ParseException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }
}