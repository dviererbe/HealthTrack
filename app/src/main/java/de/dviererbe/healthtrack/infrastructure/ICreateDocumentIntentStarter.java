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

/**
 * (NO LONGER USED!) Mechanism interface for starting {@link Intent#ACTION_CREATE_DOCUMENT} {@link Intent}'s.
 */
@Deprecated
public interface ICreateDocumentIntentStarter
{
    /**
     * Starts an {@link Intent#ACTION_CREATE_DOCUMENT} {@link Intent}.
     *
     * @param mediaType MIME type (IANA media; defined by IETF RFC 6838) of the document that should be created.
     * @param title Suggestion for the user for the name of document that should be created.
     * @param resultCallback Callback mechanism that will be invoked when the activity finishes.
     */
    void StartActivityForResult(
        final String mediaType,
        final String title,
        final ICreateDocumentIntentResultObserver resultCallback);

    /**
     * Callback interface for the result of the intent.
     */
    interface ICreateDocumentIntentResultObserver
    {
        /**
         * Gets called when a specific {@link Intent#ACTION_CREATE_DOCUMENT} {@link Intent} exits.
         *
         * @param resultCode The integer result code returned by the child activity through its setResult().
         * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
         * @see android.app.Activity#onActivityResult(int, int, Intent)
         */
        void OnActivityResult(final int resultCode, final Intent data);
    }
}
