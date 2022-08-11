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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@link RepositoryException} that gets thrown when an argument has invalid properties.
 */
public class OneOrMorePropertiesAreInvalid extends InvalidArgumentException
{
    private final Map<String, String> _propertyErrors;

    /**
     * Constructs a {@link OneOrMorePropertiesAreInvalid} instance for one invalid property.
     *
     * @param argumentName The name of the argument with an invalid property.
     * @param invalidPropertyName The name of the invalid property.
     * @param invalidPropertyErrorMessage The error message that describes the error.
     */
    public OneOrMorePropertiesAreInvalid(
            final String argumentName,
            final String invalidPropertyName,
            final String invalidPropertyErrorMessage)
    {
        this(argumentName, new HashMap<String, String>() {{ put(invalidPropertyName, invalidPropertyErrorMessage); }});
    }

    /**
     * Constructs a {@link OneOrMorePropertiesAreInvalid} instance.
     *
     * @param argumentName The name of the argument with an invalid property.
     * @param propertyErrors Instance that maps the names of the invalid properties to the error descriptions.
     */
    public OneOrMorePropertiesAreInvalid(
        final String argumentName,
        final Map<String, String> propertyErrors)
    {
        super(argumentName, ConstructExceptionMessage(argumentName, propertyErrors));
        _propertyErrors = propertyErrors;
    }

    private static String ConstructExceptionMessage(
        final String argumentName,
        final Map<String, String> propertyErrors)
    {
        final StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("One or more properties of '").append(argumentName).append("' are invalid");

        if (propertyErrors.isEmpty())
        {
            errorMessage.append('.');
        }
        else
        {
            errorMessage.append(':');

            for (String propertyName : propertyErrors.keySet())
            {
                errorMessage.append("\n\t").append(propertyName).append(": ").append(propertyErrors.get(propertyName));
            }
        }

        return errorMessage.toString();
    }

    /**
     * Gets the names of the properties which are invalid.
     *
     * @return
     *      <p>A {@link Set} that contains the names of the properties which are invalid as {@link String}.</p>
     *      <p><i>Note: The absence of an property name does not indicate it's validity.
     *      The validation logic may be exited early before all properties were validated.</i></p>
     */
    public Set<String> GetInvalidPropertyNames()
    {
        return _propertyErrors.keySet();
    }

    /**
     * Gets an error description for an invalid property by name.
     *
     * @param propertyName
     *      The name of the property to get the error description for,
     * @return
     *      <p>The error description for the specified property, or {@code null} if
     *      the specifies property has no validation error or does not exist.</p>
     *      <p><i>Note: The absence (return value {@code null}) of an error description does not indicate it's validity.
     *      The validation logic may be exited early before all properties were validated.</i></p>
     */
    public String GetErrorDescriptionForProperty(String propertyName)
    {
        return _propertyErrors.get(propertyName);
    }
}
