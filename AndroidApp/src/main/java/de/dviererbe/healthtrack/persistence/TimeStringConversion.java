package de.dviererbe.healthtrack.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Converts between {@link LocalDateTime}, {@link LocalDate} and {@link String} representations.
 *
 * Used for the internal SQLite Repositories implementations.
 */
class TimeStringConversion
{
    /**
     * Converts a {@link LocalDate} instance to a ISO local date {@link String} representation.
     *
     * @param date the {@link LocalDate} instance that should be converted
     * @return ISO local date {@link String} representation
     */
    public static String ConvertDateToDateString(final LocalDate date)
    {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }

    /**
     * Converts a {@link LocalDateTime} instance to a ISO local date {@link String} representation.
     *
     * @param dateTime the {@link LocalDateTime} instance that should be converted
     * @return ISO local date {@link String} representation
     */
    public static String ConvertDateTimeToDateString(final LocalDateTime dateTime)
    {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(dateTime);
    }

    /**
     * Converts a {@link LocalDateTime} instance to a ISO local time {@link String} representation.
     *
     * @param dateTime the {@link LocalDateTime} instance that should be converted
     * @return ISO local time {@link String} representation
     */
    public static String ConvertDateTimeToTimeString(final LocalDateTime dateTime)
    {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(dateTime);
    }

    /**
     * Converts a ISO local date and time {@link String} representation to a {@link LocalDate} instance.
     *
     * @param isoLocalDateString the ISO local date {@link String} representation
     * @param isoLocalTimeString the ISO local time {@link String} representation
     * @return parsed {@link LocalDate} instance from {@code isoLocalDateString} and {@code isoLocalTimeString}
     */
    public static LocalDateTime ConvertDateStringAndTimeStringToDateTime(
            final String isoLocalDateString,
            final String isoLocalTimeString)
    {
        final String isoLocalDateTimeString = isoLocalDateString + "T" + isoLocalTimeString;
        return LocalDateTime.parse(isoLocalDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
