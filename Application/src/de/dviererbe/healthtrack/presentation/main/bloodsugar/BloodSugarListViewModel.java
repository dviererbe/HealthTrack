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

package de.dviererbe.healthtrack.presentation.main.bloodsugar;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.infrastructure.ILogger;

public class BloodSugarListViewModel implements IDisposable
{
    private final static String TAG = "BloodSugarListViewModel";
    private final ILogger _logger;

    public BloodSugarListViewModel(final ILogger logger)
    {

        _logger = logger;
    }

    public void CreateRecord()
    {
        _logger.LogDebug(TAG, "CreateRecord");
    }

    public void DeleteAll()
    {
        _logger.LogDebug(TAG, "DeleteAll");
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        // Nothing to do here.
    }
}