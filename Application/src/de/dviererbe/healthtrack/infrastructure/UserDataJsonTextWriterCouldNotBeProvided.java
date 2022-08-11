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

import de.dviererbe.healthtrack.infrastructure.json.IJsonTextWriter;

/**
 * The exception that is thrown when no {@link IJsonTextWriter} could be provided for
 * exporting the user data as a json file.
 */
public class UserDataJsonTextWriterCouldNotBeProvided extends Exception
{
    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and
     * may subsequently be initialized by a call to {@code Throwable.initCause(java.lang.Throwable)}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@code Throwable.getMessage()} method.
     */
    public UserDataJsonTextWriterCouldNotBeProvided(String message)
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
    public UserDataJsonTextWriterCouldNotBeProvided(String message, Throwable cause)
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
    public UserDataJsonTextWriterCouldNotBeProvided(Throwable cause)
    {
        super(cause);
    }
}
