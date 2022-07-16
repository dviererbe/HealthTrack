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

    public static int ToMillimetreOfMercury(int kilopascal)
    {
        return (int)Math.round(kilopascal / KilopascalPerMillimetreOfMercury);
    }

    public static int ToKilopascal(int millimetreOfMercury)
    {
        return (int)Math.round(KilopascalPerMillimetreOfMercury * millimetreOfMercury);
    }

    public static int Convert(int value, BloodPressureUnit from, BloodPressureUnit to)
    {
        if (from == Kilopascal && to == MillimetreOfMercury)
            return ToMillimetreOfMercury(value);

        if (from == MillimetreOfMercury && to == Kilopascal)
            return ToKilopascal(value);

        return value;
    }
}
