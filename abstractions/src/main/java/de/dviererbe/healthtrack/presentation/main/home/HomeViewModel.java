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

package de.dviererbe.healthtrack.presentation.main.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.dviererbe.healthtrack.IDisposable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel implements IDisposable
{
    private final MutableLiveData<List<DaySummaryViewModel>> _daySummaries;

    public HomeViewModel()
    {
        ArrayList<DaySummaryViewModel> daySummaries = new ArrayList<DaySummaryViewModel>();
        daySummaries.add(new DaySummaryViewModel(new Date(), 312, 5000));

        DaySummaries = _daySummaries = new MutableLiveData<>(daySummaries);
    }

    public final LiveData<List<DaySummaryViewModel>> DaySummaries;

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        // Nothing to do here.
    }
}