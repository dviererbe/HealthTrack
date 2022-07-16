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

import android.content.Intent;
import de.dviererbe.healthtrack.IDisposable;

import java.util.ArrayList;

/**
 * (NO LONGER USED!) Mediates the result data of launched activities which exited to obervers.
 */
@Deprecated
public class ActivityResultMediator implements IDisposable
{
    private ArrayList<ActivityResultObserver> _observers = new ArrayList<>();

    /**
     * Adds an observer to the notification list when an activity that was launched exits.
     *
     * @param observer the {@link ActivityResultObserver} instance that should be added to the notification list.
     * @throws IllegalStateException when the {@link ActivityResultObserver#Dispose()} method was called.
     */
    public void RegisterObserver(ActivityResultObserver observer)
    {
        if (_observers == null) throw new IllegalStateException("ActivityResultMediator was disposed.");
        if (observer == null) return;

        _observers.add(observer);
    }

    /**
     * Removes an observer from the notification list when an activity that was launched exits.
     *
     * @param observer the {@link ActivityResultObserver} instance that should be removed from the notification list.
     * @throws IllegalStateException when the {@link ActivityResultObserver#Dispose()} method was called.
     */
    public void UnregisterObserver(ActivityResultObserver observer)
    {
        if (_observers == null) throw new IllegalStateException("ActivityResultMediator was disposed.");
        if (observer == null) return;

        _observers.remove(observer);
    }

    /**
     * Notifies all observers when an activity that was launched exits, giving
     * the requestCode it was started with, the resultCode it returned, and any additional data from it.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    public void OnActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (_observers == null) return;

        for(ActivityResultObserver observer : _observers)
        {
            observer.OnActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        if (_observers == null) return;

        _observers.clear();
        _observers = null;
    }

    /**
     * Observes results of activities that were launched which exited.
     */
    public interface ActivityResultObserver
    {
        /**
         * Called when an activity was launched exits, giving the requestCode it was
         * started with, the resultCode it returned, and any additional data from it.
         *
         * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
         * @param resultCode The integer result code returned by the child activity through its setResult().
         * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
         */
        void OnActivityResult(final int requestCode, final int resultCode, final Intent data);
    }
}
