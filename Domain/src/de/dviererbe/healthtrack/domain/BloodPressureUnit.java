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

package de.dviererbe.healthtrack.domain;

/**
 * Unit used to measure blood pressure.
 */
public enum BloodPressureUnit
{
    /**
     * mmHg (millimetre of mercury).
     */
    MillimetreOfMercury,

    /**
     * kPa (kilopascal).
     */
    Kilopascal;

    // Source: https://en.wikipedia.org/wiki/Millimetre_of_mercury
    private static final double KilopascalPerMillimetreOfMercury =  0.133322387415;

    public static double ToMillimetreOfMercury(double value, BloodPressureUnit unit)
    {
        switch (unit)
        {
            case MillimetreOfMercury:
                return value;
            case Kilopascal:
                return value / KilopascalPerMillimetreOfMercury;
            default:
                throw new RuntimeException("Unknown blood pressure unit to convert from.");
        }
    }

    public static double ToKilopascal(double value, BloodPressureUnit unit)
    {
        switch (unit)
        {
            case MillimetreOfMercury:
                return value * KilopascalPerMillimetreOfMercury;
            case Kilopascal:
                return value;
            default:
                throw new RuntimeException("Unknown blood pressure unit to convert from.");
        }
    }

    public static double Convert(double value, BloodPressureUnit from, BloodPressureUnit to)
    {
        switch (to)
        {
            case MillimetreOfMercury:
                return ToMillimetreOfMercury(value, from);
            case Kilopascal:
                return ToKilopascal(value, from);
            default:
                throw new RuntimeException("Unknown blood pressure unit to convert to.");
        }
    }
}
