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

public class EmptyLogger implements ILogger
{
    @Override
    public void LogError(String tag, String message)
    {
    }

    @Override
    public void LogError(String tag, String message, Throwable throwable)
    {
    }

    @Override
    public void LogWarning(String tag, String message)
    {
    }

    @Override
    public void LogWarning(String tag, String message, Throwable throwable)
    {
    }

    @Override
    public void LogInformation(String tag, String message)
    {
    }

    @Override
    public void LogInformation(String tag, String message, Throwable throwable)
    {
    }

    @Override
    public void LogDebug(String tag, String message)
    {
    }

    @Override
    public void LogDebug(String tag, String message, Throwable throwable)
    {
    }

    @Override
    public void LogVerbose(String tag, String message)
    {
    }

    @Override
    public void LogVerbose(String tag, String message, Throwable throwable)
    {
    }
}
