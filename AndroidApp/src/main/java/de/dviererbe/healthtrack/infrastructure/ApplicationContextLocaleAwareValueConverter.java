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
import android.icu.text.NumberFormat;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.MedicationState;
import de.dviererbe.healthtrack.domain.WeightUnit;

/**
 * Uses the application context to convert values' locale aware.
 */
public class ApplicationContextLocaleAwareValueConverter implements INumericValueConverter
{
    private final Context _applicationContext;

    /**
     * Initializes a new instance using the application {@link Context}.
     *
     * @param applicationContext a reference to the application {@link Context}.
     */
    public ApplicationContextLocaleAwareValueConverter(Context applicationContext)
    {
        _applicationContext = applicationContext;
    }

    /**
     * Converts a {@link int} value to a {@link String} representation.
     *
     * @param value the value that should be converted.
     * @return the {@link String} representation of the {@code value}.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public String ToString(int value) throws ConversionError
    {
        try
        {
            return _applicationContext.getString(R.string.format_int, value);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert integer to String.", exception);
        }
    }

    /**
     * Converts a {@link double} instance to a {@link String} instance.
     *
     * @param value the value that should be converted.
     * @return the converted value.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public String ToString(double value) throws ConversionError
    {
        try
        {
            return _applicationContext.getString(R.string.format_double, value);
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert double to String.", exception);
        }
    }

    /**
     * Converts a {@link WeightUnit} instance to a {@link String} instance.
     *
     * @param value the value that should be converted.
     * @return the converted value.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public String ToString(WeightUnit value) throws ConversionError
    {
        try
        {
            switch (value)
            {
                case Kilogram:
                    return _applicationContext.getString(R.string.kilogram_short);
                case Pound:
                    return _applicationContext.getString(R.string.pound_short);
                default:
                    throw new IllegalStateException("Unknown unit " + value.name());
            }
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert weight unit.", exception);
        }
    }

    /**
     * Converts a {@link BloodPressureUnit} value to a {@link String} representation.
     *
     * @param value the value that should be converted.
     * @return the {@link String} representation of the {@code value}.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public String ToString(BloodPressureUnit value) throws ConversionError
    {
        try
        {
            switch (value)
            {
                case Kilopascal:
                    return _applicationContext.getString(R.string.kilopascal_short);
                case MillimetreOfMercury:
                    return _applicationContext.getString(R.string.millimetreofmercury_short);
                default:
                    throw new IllegalStateException("Unknown unit " + value.name());
            }
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert weight unit.", exception);
        }
    }

    /**
     * Converts a {@link MedicationState} value to a {@link String} representation.
     *
     * @param value the value that should be converted.
     * @return the {@link String} representation of the {@code value}.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public String ToString(MedicationState value) throws ConversionError
    {
        try
        {
            switch (value)
            {
                case None:
                    return _applicationContext.getString(R.string.medication_state_none);
                case Taken:
                    return _applicationContext.getString(R.string.medication_state_taken);
                case NotTaken:
                    return _applicationContext.getString(R.string.medication_state_not_taken);
                default:
                    throw new IllegalStateException("Unknown unit " + value.name());
            }
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to convert weight unit.", exception);
        }
    }

    /**
     * Converts a {@link String} instance to a {@link int} value.
     *
     * @param number the value that should be converted.
     * @return the converted value.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public int ToInteger(String number) throws ConversionError
    {
        try
        {
            final NumberFormat numberFormat = NumberFormat.getNumberInstance();
            final Number parsedNumber = numberFormat.parse(number);

            return parsedNumber.intValue();
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to parse integer String.", exception);
        }
    }

    /**
     * Converts a {@link String} instance to a {@link double} instance.
     *
     * @param number the value that should be converted.
     * @return the converted value.
     * @throws ConversionError when the value can't be converted
     */
    @Override
    public double ToDouble(String number) throws ConversionError
    {
        try
        {
            final NumberFormat numberFormat = NumberFormat.getNumberInstance();
            final Number parsedNumber = numberFormat.parse(number);

            return parsedNumber.doubleValue();
        }
        catch (Exception exception)
        {
            throw new ConversionError("Failed to parse number String.", exception);
        }
    }
}
