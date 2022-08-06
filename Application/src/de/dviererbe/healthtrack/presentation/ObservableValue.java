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

import java.util.ArrayList;
import java.util.List;

public abstract class ObservableValue<TValue>
{
    private final List<IValueObserver<TValue>> _observers;

    protected TValue _value;

    public ObservableValue(TValue initialValue)
    {
        _observers = new ArrayList<>();
        _value = initialValue;
    }

    public TValue GetValue()
    {
        return _value;
    }

    protected void NotifyObservers(TValue newValue)
    {
        for (IValueObserver<TValue> observer : _observers)
        {
            observer.OnValueChanged(newValue);
        }
    }

    public void AddObserver(final IValueObserver<TValue> observer)
    {
        AddObserver(observer, true);
    }

    public void AddObserver(
        final IValueObserver<TValue> observer,
        final boolean observeCurrentValue)
    {
        if (observer == null) return;

        if (observeCurrentValue) observer.OnValueChanged(_value);
        _observers.add(observer);
    }

    public void RemoveObserver(IValueObserver<TValue> observer)
    {
        if (observer == null) return;
        _observers.remove(observer);
    }

    public void RemoveAllObservers()
    {
        _observers.clear();
    }

    public interface IValueObserver<TValue>
    {
        void OnValueChanged(TValue newValue);
    }
}