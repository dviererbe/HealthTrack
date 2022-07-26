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

package de.dviererbe.healthtrack.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * Implements the {@link IFoodWidgetRepository} interface using an SQLite database.
 */
public class FoodWidgetSQLiteRepository
        extends SQLiteOpenHelper
        implements IFoodWidgetRepository
{
    private static final String DatabaseName = "FoodWidget.db";
    private static final int DatabaseVersion = 1;
    private static final SQLiteDatabase.CursorFactory CursorFactory = null;

    private boolean _disposed = false;
    private boolean _downgradeRequested = false;
    private boolean _upgradeRequested = false;


    public FoodWidgetSQLiteRepository(@Nullable Context context)
    {
        super(context, DatabaseName, CursorFactory, DatabaseVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        throw new IllegalStateException("Not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(
            final SQLiteDatabase database,
            final int oldVersion,
            final int newVersion)
    {
        if (oldVersion == newVersion) return;

        _upgradeRequested = true;
        throw new IllegalStateException("Database upgrade strategy is not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDowngrade(
            final SQLiteDatabase database,
            final int oldVersion,
            final int newVersion)
    {
        if (oldVersion == newVersion) return;

        _downgradeRequested = true;
        throw new IllegalStateException("Database downgrade strategy is not implemented.");
    }

    /**
     * Gets the name of the food widget repository provider.
     *
     * @return {@link String} representation of the food widget repository provider name.
     */
    @Override
    public String GetProviderName()
    {
        return getClass().getName();
    }

    /**
     * Gets the version of the food widget repository provider.
     *
     * @return {@link String} representation of the food widget repository provider version.
     */
    @Override
    public String GetProviderVersion()
    {
        return String.valueOf(DatabaseVersion);
    }

    /**
     * Deletes all records.
     *
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteAllRecords() throws RepositoryDisposed, RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();

        throw new RepositoryException("Not implemented yet.");
    }

    private void ThrowWhenDatabaseStateIsBad() throws
            RepositoryDisposed,
            RepositoryException
    {
        if (_disposed) throw new RepositoryDisposed();
        if (_upgradeRequested) throw new RepositoryException("Database upgrade failed.");
        if (_downgradeRequested) throw new RepositoryException("Database downgrade failed.");
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        if (!_disposed)
        {
            _disposed = true;
            close();
        }
    }
}
