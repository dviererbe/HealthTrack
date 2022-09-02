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

import de.dviererbe.healthtrack.IDisposable;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewModel<TEventHandler> implements IDisposable
{
    private static final IDisposable NullEventHandlerDisposeAction = () -> {};

    private final List<TEventHandler> _eventHandlers = new ArrayList<>();

    private boolean _disposed = false;

    protected void NotifyEventHandlers(EventHandlerNotification<TEventHandler> notificationAction)
    {
        for (TEventHandler eventHandler: _eventHandlers)
        {
            notificationAction.NotifyEventHandler(eventHandler);
        }
    }

    public IDisposable RegisterEventHandler(TEventHandler eventHandler)
    {
        if (eventHandler == null) return NullEventHandlerDisposeAction;

        _eventHandlers.add(eventHandler);
        return new RemoveEventHandler(eventHandler);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        if (_disposed) return;

        _disposed = true;
        _eventHandlers.clear();
    }

    protected interface EventHandlerNotification<TEventHandler>
    {
        void NotifyEventHandler(TEventHandler eventHandler);
    }

    private class RemoveEventHandler implements IDisposable
    {
        private TEventHandler _eventHandler;

        public RemoveEventHandler(TEventHandler eventHandler)
        {
            _eventHandler = eventHandler;
        }

        /**
         * Removes the specified Event Handler from the {@link ViewModel#_eventHandlers} list.
         */
        @Override
        public void Dispose()
        {
            if (_disposed || _eventHandler == null) return;
            _eventHandlers.remove(_eventHandler);
        }
    }
}
