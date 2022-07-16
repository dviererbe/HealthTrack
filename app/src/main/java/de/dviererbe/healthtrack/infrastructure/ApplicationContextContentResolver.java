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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.OutputStream;

/**
 * Resolves content using the application context.
 */
public class ApplicationContextContentResolver implements IContentResolver
{
    private final ContentResolver _contentResolver;

    /**
     * Initializes the {@link ApplicationContextContentResolver} with a specified application {@link Context}.
     *
     * @param applicationContext a reference to the application {@link Context}.
     */
    public ApplicationContextContentResolver(Context applicationContext)
    {
        _contentResolver = applicationContext.getContentResolver();
    }

    /**
     * Open a stream on to the content associated with a content URI.
     *
     * @param uri The desired URI. This value cannot be {@code null}.
     * @return an OutputStream or {@code null} if the provider recently crashed.
     * @throws FileNotFoundException when no
     */
    @Override
    public OutputStream OpenOutputStream(Uri uri) throws FileNotFoundException
    {
        return _contentResolver.openOutputStream(uri);
    }
}
