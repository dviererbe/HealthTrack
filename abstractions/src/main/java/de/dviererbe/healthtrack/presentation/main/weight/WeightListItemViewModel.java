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

package de.dviererbe.healthtrack.presentation.main.weight;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.infrastructure.IDateTimeConverter;
import de.dviererbe.healthtrack.infrastructure.INumericValueConverter;
import de.dviererbe.healthtrack.presentation.ConversionHelper;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListViewModel.IWeightListView;

import java.util.UUID;

public class WeightListItemViewModel implements IDisposable
{
    private static final String NullValue = "(null)";
    private static final String ErrorValue = "(error)";

    private final IWeightListView _view;
    private final UUID _recordIdentifier;

    public final String Weight;
    public final String Unit;
    public final String DateTime;
    public final Boolean Converted;

    public WeightListItemViewModel(
            IWeightListView parentView,
            IDateTimeConverter dateTimeConverter,
            INumericValueConverter numericValueConverter,
            WeightRecord weightRecord,
            WeightUnit preferredUnit)
    {
        _view = parentView;

        if (weightRecord == null)
        {
            _recordIdentifier = null;

            Weight = Unit = DateTime = NullValue;
            Converted = false;

            return;
        }

        _recordIdentifier = weightRecord.Identifier;

        double value = weightRecord.Value;
        WeightUnit unit = weightRecord.Unit;

        if (Converted = (unit != preferredUnit))
        {
            value = WeightUnit.Convert(value, unit, preferredUnit);
            unit = preferredUnit;
        }

        Weight = ConversionHelper.TryConvertToString(value, ErrorValue, numericValueConverter);
        Unit = ConversionHelper.TryConvertToString(unit, ErrorValue, numericValueConverter);
        DateTime = ConversionHelper.TryConvertToString(weightRecord.TimeOfMeasurement, ErrorValue, dateTimeConverter);
    }

    public void ShowDetails()
    {
        if (_recordIdentifier == null) return;

        _view.NavigateToDetailsView(_recordIdentifier);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }
}
