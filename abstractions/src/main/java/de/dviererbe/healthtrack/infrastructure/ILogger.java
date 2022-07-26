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

/**
 * Represents a mechanism used to perform logging.
 */
public interface ILogger
{
    /**
     * Formats and writes an error log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     */
    void LogError(String tag, String message);

    /**
     * Formats and writes an error log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     * @param throwable
     *       An exception to log This value may be {@code null}.
     */
    void LogError(String tag, String message, Throwable throwable);

    /**
     * Formats and writes a warning log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     */
    void LogWarning(String tag, String message);

    /**
     * Formats and writes a warning log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     * @param throwable
     *       An exception to log This value may be {@code null}.
     */
    void LogWarning(String tag, String message, Throwable throwable);

    /**
     * Formats and writes an informational log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     */
    void LogInformation(String tag, String message);

    /**
     * Formats and writes an informational log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     * @param throwable
     *       An exception to log This value may be {@code null}.
     */
    void LogInformation(String tag, String message, Throwable throwable);

    /**
     * Formats and writes a debug log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     */
    void LogDebug(String tag, String message);

    /**
     * Formats and writes a debug log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     * @param throwable
     *       An exception to log This value may be {@code null}.
     */
    void LogDebug(String tag, String message, Throwable throwable);

    /**
     * Formats and writes a verbose log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     */
    void LogVerbose(String tag, String message);

    /**
     * Formats and writes a verbose log message.
     *
     * @param tag
     *      Used to identify the source of a log message. It usually identifies
     *      the class where the log call occurs. This value may be {@code null}.
     * @param message
     *      The message you would like logged. This value may be {@code null}.
     * @param throwable
     *       An exception to log This value may be {@code null}.
     */
    void LogVerbose(String tag, String message, Throwable throwable);
}
