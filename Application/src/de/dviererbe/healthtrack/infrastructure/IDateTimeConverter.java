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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Abstracts the date and/or time value conversion logic.
 */
public interface IDateTimeConverter
{
    /**
     * Gets a {@link String} representation of an {@link LocalDate} instance.
     *
     * @param date the {@link LocalDate} instance that should be converted
     * @return {@link String} representation
     * @throws ConversionError when an error during the conversion occurred
     */
    String GetString(LocalDate date) throws ConversionError;

    /**
     * Gets a {@link String} representation of an {@link LocalTime} instance.
     *
     * @param time the {@link LocalTime} instance that should be converted
     * @return {@link String} representation
     * @throws ConversionError when an error during the conversion occurred
     */
    String GetString(LocalTime time) throws ConversionError;

    /**
     * Gets a {@link String} representation of an {@link LocalDateTime} instance.
     *
     * @param dateTime the {@link LocalDateTime} instance that should be converted
     * @return {@link String} representation
     * @throws ConversionError when an error during the conversion occurred
     */
    String GetString(LocalDateTime dateTime) throws ConversionError;

    /**
     * Parses a {@link LocalDate} instance from a {@link String} representation.
     *
     * @param dateString the {@link String} representation that should be parsed
     * @return the parsed {@link LocalDate} instance
     * @throws ConversionError when an error during the conversion occurred
     */
    LocalDate GetDate(String dateString) throws ConversionError;

    /**
     * Parses a {@link LocalTime} instance from a {@link String} representation.
     *
     * @param timeString the {@link String} representation that should be parsed
     * @return the parsed {@link LocalTime} instance
     * @throws ConversionError when an error during the conversion occurred
     */
    LocalTime GetTime(String timeString) throws ConversionError;

    /**
     * Parses a {@link LocalDateTime} instance from a {@link String} representation.
     *
     * @param dateTimeString the {@link String} representation that should be parsed
     * @return the parsed {@link LocalDateTime} instance
     * @throws ConversionError when an error during the conversion occurred
     */
    LocalDateTime GetDateTime(String dateTimeString) throws ConversionError;

    /**
     * The exception that gets thrown when a date and/or time value can not be converted.
     */
    class ConversionError extends Exception
    {
        /**
         * Constructs a new exception with the specified detail message. The cause is not initialized, and
         * may subsequently be initialized by a call to {@code Throwable.initCause(java.lang.Throwable)}.
         *
         * @param message the detail message. The detail message is saved for later retrieval by the {@code Throwable.getMessage()} method.
         */
        public ConversionError(String message)
        {
            super(message);
        }

        /**
         * Constructs a new exception with the specified detail message and cause.
         *
         * Note that the detail message associated with cause is not automatically incorporated in this exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval by the {@code Throwable.getMessage()} method).
         * @param cause  the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        public ConversionError(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new exception with the specified cause and a detail message of
         * {@code (cause==null ? null : cause.toString())} (which typically contains the
         * class and detail message of cause). This constructor is useful for exceptions
         * that are little more than wrappers for other throwables (for example, {@code PrivilegedActionException}).
         *
         * @param cause the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        public ConversionError(Throwable cause)
        {
            super(cause);
        }
    }
}
