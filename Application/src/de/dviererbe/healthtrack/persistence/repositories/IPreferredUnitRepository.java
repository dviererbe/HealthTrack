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

package de.dviererbe.healthtrack.persistence.repositories;

import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.WeightUnit;

/**
 * A storage mechanism for the user preferred units.
 */
public interface IPreferredUnitRepository
{
    /**
     * Gets the user preferred unit for measuring energy.
     *
     * @return User preferred unit for measuring energy.
     */
    EnergyUnit GetPreferredEnergyUnit();

    /**
     * Gets the user preferred unit for measuring mass.
     *
     * @return User preferred unit for measuring mass.
     */
    MassUnit GetPreferredMassUnit();

    /**
     * Gets the user preferred unit for measuring blood pressure.
     *
     * @return User preferred unit for measuring blood pressure.
     */
    BloodPressureUnit GetPreferredBloodPressureUnit();

    /**
     * Gets the user preferred unit for measuring blood sugar.
     *
     * @return User preferred unit for measuring blood sugar.
     */
    BloodSugarUnit GetPreferredBloodSugarUnit();

    /**
     * Energy units that can be selected by the user.
     */
    enum EnergyUnit
    {
        /**
         * Use kcal (kilocalories).
         */
        Kilocalories,

        /**
         * Use kJ (kilojoule).
         */
        Kilojoule,
    }

    /**
     * Mass units that can be selected by the user.
     */
    enum MassUnit
    {
        /**
         * Use kg (kilogram).
         */
        Kilogram,

        /**
         * Use lb (pound-mass).
         */
        Pound;

        /**
         * Converts the unit to the weight unit of the domain model.
         *
         * @return unit as domain model unit.
         */
        public WeightUnit ToDomainWeightUnit()
        {
            if (this == Pound) return WeightUnit.Pound;
            return WeightUnit.Kilogram;
        }
    }

    /**
     * Blood pressure units that can be selected by the user.
     */
    enum BloodPressureUnit
    {
        /**
         * Use mmHg (millimetre of mercury).
         */
        MillimetreOfMercury,

        /**
         * Use kPa (kilopascal).
         */
        Kilopascal;

        /**
         * Converts the unit to the blood pressure unit of the domain model.
         *
         * @return unit as domain model unit.
         */
        public de.dviererbe.healthtrack.domain.BloodPressureUnit ToDomainBloodPressureUnit()
        {
            if (this == MillimetreOfMercury) return de.dviererbe.healthtrack.domain.BloodPressureUnit.MillimetreOfMercury;
            return de.dviererbe.healthtrack.domain.BloodPressureUnit.Kilopascal;
        }
    }

    /**
     * Blood sugar units that can be selected by the user.
     */
    enum BloodSugarUnit
    {
        /**
         * Use mg/dl (milligram per decilitre).
         */
        MilligramPerDecilitre,

        /**
         * Use mmol/l (millimol per litre).
         */
        MillimolPerLitre,
    }
}
