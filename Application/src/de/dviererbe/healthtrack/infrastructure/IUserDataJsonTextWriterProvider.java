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

import de.dviererbe.healthtrack.infrastructure.json.IJsonTextWriter;

/**
 * A mechanism that can provide an {@link IJsonTextWriter}
 * where the user data can be exported to as json text.
 */
public interface IUserDataJsonTextWriterProvider
{
    /**
     * Provides an {@link IJsonTextWriter} where the user data can be exported to.
     */
    void ProvideUserDataJsonTextWriter(final ProvideUserDataJsonTextWriterRequestCallback callback);

    interface ProvideUserDataJsonTextWriterRequestCallback
    {
        /**
         * Called when an {@link IJsonTextWriter} could be provided where the user data can be written to as json text
         *
         * @param jsonTextWriter The {@link IJsonTextWriter} where the user data can be written to as json text
         */
        void UserDataJsonTextWriterProvided(final IJsonTextWriter jsonTextWriter);

        /**
         * Called when an {@link IJsonTextWriter} could not be provided.
         *
         * @param exception An exception that describes the occurred error.
         */
        void UserDataJsonTextWriterCouldNotBeProvided(final Exception exception);
    }
}
