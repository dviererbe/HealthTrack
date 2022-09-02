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
 * Unit used to measure weight. (Technically the weight is measured
 * in the force acting equivalent to one quantity of the following
 * mass units at standard earth gravity.
 * See <a href="https://en.wikipedia.org/wiki/Weight">the Wikipedia explanation</a>
 * for more details)
 */
public enum WeightUnit
{
    /**
     * kg (kilogram)
     */
    Kilogram,

    /**
     * lb (pound-mass)
     */
    Pound;

    // Source: https://en.wikipedia.org/wiki/Pound_(mass)
    private static final double KilogramPerPound = 0.45359237;

    public static double ToKilogram(double value, WeightUnit unit)
    {
        switch (unit)
        {
            case Kilogram:
                return value;
            case Pound:
                return value * KilogramPerPound;
            default:
                throw new RuntimeException("Unknown weight unit to convert from.");
        }
    }

    public static double ToPound(double value, WeightUnit unit)
    {
        switch (unit)
        {
            case Kilogram:
                return value / KilogramPerPound;
            case Pound:
                return value;
            default:
                throw new RuntimeException("Unknown weight unit to convert from.");
        }
    }

    public static double Convert(double value, WeightUnit from, WeightUnit to)
    {
        switch (to)
        {
            case Kilogram:
                return ToKilogram(value, from);
            case Pound:
                return ToPound(value, from);
            default:
                throw new RuntimeException("Unknown weight unit to convert to.");
        }
    }
}
