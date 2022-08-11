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

package de.dviererbe.healthtrack.persistence.exceptions;

/**
 * The super class of all {@link RepositoryException}'s that is thrown when a repository method/function is called
 * with invalid arguments.
 */
public class InvalidArgumentException extends RepositoryException
{
    /**
     * <p>Name of the invalid argument.</p>
     *
     * <p><i>(Note: The exception message contains the error description.)</i></p>
     */
    public final String ArgumentName;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param argumentName Name of the invalid argument.
     * @param errorMessage A message that describes the error.
     */
    public InvalidArgumentException(final String argumentName, final String errorMessage)
    {
        super(errorMessage);
        ArgumentName = argumentName;
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param argumentName Name of the invalid argument.
     * @param errorMessage A message that describes the error.
     * @param cause the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public InvalidArgumentException(final String argumentName, final String errorMessage, final Throwable cause)
    {
        super(errorMessage, cause);
        ArgumentName = argumentName;
    }
}
