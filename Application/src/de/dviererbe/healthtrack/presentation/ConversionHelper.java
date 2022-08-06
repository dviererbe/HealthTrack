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


package de.dviererbe.healthtrack.presentation;

import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.MedicationState;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ConversionHelper
{
    public static String TryConvertToString(
        final int value,
        final String fallbackValue,
        final INumericValueConverter numericValueConverter)
    {
        try
        {
            return numericValueConverter.ToString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
        final double value,
        final String fallbackValue,
        final INumericValueConverter numericValueConverter)
    {
        try
        {
            return numericValueConverter.ToString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
        final WeightUnit value,
        final String fallbackValue,
        final INumericValueConverter numericValueConverter)
    {
        try
        {
            return numericValueConverter.ToString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
            final BloodPressureUnit value,
            final String fallbackValue,
            final INumericValueConverter numericValueConverter)
    {
        try
        {
            return numericValueConverter.ToString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
            final LocalDate value,
            final String fallbackValue,
            final IDateTimeConverter dateTimeConverter)
    {
        try
        {
            return dateTimeConverter.GetString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
            final LocalTime value,
            final String fallbackValue,
            final IDateTimeConverter dateTimeConverter)
    {
        try
        {
            return dateTimeConverter.GetString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
            final LocalDateTime value,
            final String fallbackValue,
            final IDateTimeConverter dateTimeConverter)
    {
        try
        {
            return dateTimeConverter.GetString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }

    public static String TryConvertToString(
            final MedicationState value,
            final String fallbackValue,
            final INumericValueConverter numericValueConverter)
    {
        try
        {
            return numericValueConverter.ToString(value);
        }
        catch (Exception exception)
        {
            return fallbackValue;
        }
    }
}
