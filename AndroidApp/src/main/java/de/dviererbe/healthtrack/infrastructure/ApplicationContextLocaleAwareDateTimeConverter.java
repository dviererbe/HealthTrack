/*
    Health Track
    Copyright (C) 2022  Dominik Viererbe

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package de.dviererbe.healthtrack.infrastructure;

import android.content.Context;
import de.dviererbe.healthtrack.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Uses the application context to convert date and/or time values' locale aware.
 */
public class ApplicationContextLocaleAwareDateTimeConverter implements IDateTimeConverter
{
    private final Context _applicationContext;

    /**
     * Initializes a new instance using the application {@link Context}.
     *
     * @param applicationContext a reference to the application {@link Context}.
     */
    public ApplicationContextLocaleAwareDateTimeConverter(Context applicationContext)
    {
        _applicationContext = applicationContext;
    }

    /**
     * Gets a {@link String} representation of an {@link LocalDate} instance.
     *
     * @param date the {@link LocalDate} instance that should be converted
     * @return {@link String} representation
     * @throws ConversionError when an error during the conversion occurred
     */
    @Override
    public String GetString(LocalDate date) throws ConversionError
    {
        try
        {
            final String formatPattern = _applicationContext.getString(R.string.format_date);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);

            return formatter.format(date);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert to time string.", exception);
        }
    }

    /**
     * Gets a {@link String} representation of an {@link LocalTime} instance.
     *
     * @param time the {@link LocalTime} instance that should be converted
     * @return {@link String} representation
     * @throws ConversionError when an error during the conversion occurred
     */
    @Override
    public String GetString(LocalTime time) throws ConversionError
    {
        try
        {
            final String formatPattern = _applicationContext.getString(R.string.format_time);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);

            return formatter.format(time);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert to time string.", exception);
        }
    }

    /**
     * Gets a {@link String} representation of an {@link LocalDateTime} instance.
     *
     * @param dateTime the {@link LocalDateTime} instance that should be converted
     * @return {@link String} representation
     * @throws ConversionError when an error during the conversion occurred
     */
    @Override
    public String GetString(LocalDateTime dateTime) throws ConversionError
    {
        try
        {
            final String formatPattern = _applicationContext.getString(R.string.format_datetime);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);

            return formatter.format(dateTime);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert to date time string.", exception);
        }
    }

    /**
     * Parses a {@link LocalDate} instance from a {@link String} representation.
     *
     * @param dateString the {@link String} representation that should be parsed
     * @return the parsed {@link LocalDate} instance
     * @throws ConversionError when an error during the conversion occurred
     */
    @Override
    public LocalDate GetDate(String dateString) throws ConversionError
    {
        try
        {
            final String formatPattern = _applicationContext.getString(R.string.format_date);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            final TemporalAccessor temporalAccessor = formatter.parse(dateString);

            return LocalDate.from(temporalAccessor);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to parse date string.", exception);
        }
    }

    /**
     * Parses a {@link LocalTime} instance from a {@link String} representation.
     *
     * @param timeString the {@link String} representation that should be parsed
     * @return the parsed {@link LocalTime} instance
     * @throws ConversionError when an error during the conversion occurred
     */
    @Override
    public LocalTime GetTime(String timeString) throws ConversionError
    {
        try
        {
            final String formatPattern = _applicationContext.getString(R.string.format_time);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            final TemporalAccessor temporalAccessor = formatter.parse(timeString);

            return LocalTime.from(temporalAccessor);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to parse time string.", exception);
        }
    }

    /**
     * Parses a {@link LocalDateTime} instance from a {@link String} representation.
     *
     * @param dateTimeString the {@link String} representation that should be parsed
     * @return the parsed {@link LocalDateTime} instance
     * @throws ConversionError when an error during the conversion occurred
     */
    @Override
    public LocalDateTime GetDateTime(String dateTimeString) throws ConversionError
    {
        try
        {
            final String formatPattern = _applicationContext.getString(R.string.format_datetime);
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            final TemporalAccessor temporalAccessor = formatter.parse(dateTimeString);

            return LocalDateTime.from(temporalAccessor);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to parse date time string.", exception);
        }
    }


}
