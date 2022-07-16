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

import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.OutputStream;

/**
 *  Provides applications access to the content model.
 */
public interface IContentResolver
{
    /**
     * Open a stream on to the content associated with a content URI.
     *
     * @param uri The desired URI. This value cannot be {@code null}.
     * @return an OutputStream or {@code null} if the provider recently crashed.
     * @throws FileNotFoundException when no
     */
    OutputStream OpenOutputStream(Uri uri) throws FileNotFoundException;
}
