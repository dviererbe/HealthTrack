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

package de.dviererbe.healthtrack.presentation.main.bloodpressure;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.presentation.ConversionHelper;

import java.util.UUID;

public class BloodPressureListItemViewModel implements IDisposable
{
    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";

    public final String Systolic;
    public final String Diastolic;
    public final String Unit;
    public final String Pulse;
    public final String DateTime;
    public final boolean Converted;

    private final UUID _recordIdentifier;
    private final INavigationRouter _navigationRouter;

    public BloodPressureListItemViewModel(
            final INavigationRouter navigationRouter,
            final INumericValueConverter numericValueConverter,
            final BloodPressureUnit preferredUnit)
    {
        _recordIdentifier = null;
        _navigationRouter = navigationRouter;

        Systolic = Diastolic = Pulse = "?";
        Unit = ConversionHelper.TryConvertToString(preferredUnit, ErrorValue, numericValueConverter);
        DateTime = "";
        Converted = false;
    }

    public BloodPressureListItemViewModel(
        final INavigationRouter navigationRouter,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final BloodPressureRecord bloodPressureRecord,
        final BloodPressureUnit preferredUnit)
    {
        _navigationRouter = navigationRouter;

        if (bloodPressureRecord == null)
        {
            _recordIdentifier = null;
            Systolic = Diastolic = Pulse = Unit = DateTime = NullValue;
            Converted = false;

            return;
        }

        _recordIdentifier = bloodPressureRecord.Identifier;

        int systolic = bloodPressureRecord.Systolic;
        int diastolic = bloodPressureRecord.Diastolic;
        BloodPressureUnit unit = bloodPressureRecord.Unit;

        Converted = unit != preferredUnit;
        if (Converted)
        {
            systolic = (int)Math.round(BloodPressureUnit.Convert(systolic, unit, preferredUnit));
            diastolic = (int)Math.round(BloodPressureUnit.Convert(diastolic, unit, preferredUnit));
            unit = preferredUnit;
        }

        Systolic = ConversionHelper.TryConvertToString(systolic, ErrorValue, numericValueConverter);
        Diastolic = ConversionHelper.TryConvertToString(diastolic, ErrorValue, numericValueConverter);
        Unit = ConversionHelper.TryConvertToString(unit, ErrorValue, numericValueConverter);
        Pulse = ConversionHelper.TryConvertToString(bloodPressureRecord.Pulse, ErrorValue, numericValueConverter);
        DateTime = ConversionHelper.TryConvertToString(bloodPressureRecord.TimeOfMeasurement, ErrorValue, dateTimeConverter);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }

    public void RunContextAction()
    {
        if (_recordIdentifier != null)
        {
            _navigationRouter.TryNavigateToBloodPressureRecordDetails(_recordIdentifier);
        }
        else
        {
            _navigationRouter.TryNavigateToCreateBloodPressureRecord();
        }
    }
}
