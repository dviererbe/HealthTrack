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

package de.dviererbe.healthtrack.persistence.exceptions;

/**
 * The {@link RepositoryException} that is thrown when a method or function is called after the repository was disposed.
 */
public class RepositoryDisposed extends RepositoryException
{
    /**
     * Initializes a new {@link RepositoryDisposed} instance.
     */
    public RepositoryDisposed()
    {
        super("The repository resources were already disposed.");
    }
}
