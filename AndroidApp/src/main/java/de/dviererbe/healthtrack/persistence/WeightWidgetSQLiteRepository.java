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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.domain.WeightUnit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implements the {@link IWeightWidgetRepository} interface using an SQLite database.
 */
public class WeightWidgetSQLiteRepository
        extends SQLiteOpenHelper
        implements IWeightWidgetRepository
{
    private static final String DatabaseName = "WeightWidget.db";
    private static final int DatabaseVersion = 1;
    private static final SQLiteDatabase.CursorFactory CursorFactory = null;

    private boolean _disposed = false;
    private boolean _downgradeRequested = false;
    private boolean _upgradeRequested = false;

    private static final String TableWeight_Name = "Weight";
    private static final String TableWeight_Key_Id = "Id";
    private static final String TableWeight_Key_Value = "Value";
    private static final String TableWeight_Key_Unit = "Unit";
    private static final String TableWeight_Key_TimeOfMeasurementDate = "TimeOfMeasurementDate";
    private static final String TableWeight_Key_TimeOfMeasurementTime = "TimeOfMeasurementTime";

    private static final String[] TableWeight_Columns =
    {
        TableWeight_Key_Id,
        TableWeight_Key_Value,
        TableWeight_Key_Unit,
        TableWeight_Key_TimeOfMeasurementDate,
        TableWeight_Key_TimeOfMeasurementTime
    };

    public WeightWidgetSQLiteRepository(@Nullable Context context)
    {
        super(context, DatabaseName, CursorFactory, DatabaseVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        final String createTableWeightStatement =
            "CREATE TABLE " + TableWeight_Name +
            " (" +
                TableWeight_Key_Id + " TEXT PRIMARY KEY NOT NULL," +
                TableWeight_Key_Value + " REAL NOT NULL," +
                TableWeight_Key_Unit + " TEXT NOT NULL," +
                TableWeight_Key_TimeOfMeasurementDate + " TEXT NOT NULL," +
                TableWeight_Key_TimeOfMeasurementTime + " TEXT NOT NULL" +
            ")";

        database.execSQL(createTableWeightStatement);
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
     * Gets the name of the weight widget repository provider.
     *
     * @return {@link String} representation of the weight widget repository provider name.
     */
    @Override
    public String GetProviderName()
    {
        return getClass().getName();
    }

    /**
     * Gets the version of the weight widget repository provider.
     *
     * @return {@link String} representation of the weight widget repository provider version.
     */
    @Override
    public String GetProviderVersion()
    {
        return String.valueOf(DatabaseVersion);
    }

    /**
     * Gets the count of all stored records.
     *
     * @return scalar count of all stored records.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public long GetRecordCount() throws RepositoryDisposed, RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();

        try
        {
            final String selectCountQuery = "SELECT COUNT(*) FROM " + TableWeight_Name;

            final SQLiteDatabase database = getReadableDatabase();

            try (Cursor cursor = database.rawQuery(selectCountQuery, null))
            {
                return ReadScalar(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read record count.", exception);
        }
    }

    /**
     * Reads the result set with the assumption that it consists only of
     * one record of type INTEGER and returns it.
     *
     * @param cursor the {@link Cursor} used to navigate in the result set.
     * @return the scalar value.
     * @throws RepositoryException when more thn one record was found.
     */
    private long ReadScalar(Cursor cursor) throws RepositoryException
    {
        if (!cursor.moveToFirst())
            throw new RepositoryException("Result set has no row.");

        if (!cursor.isLast())
            throw new RepositoryException("Result set has more than one row.");

        if (cursor.getColumnCount() != 1)
            throw new RepositoryException("Result set has more than one column.");

        return cursor.getLong(0);
    }

    /**
     * Gets a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record that should be retrieved.
     * @return the {@link WeightRecord} with the specified identifier
     * @throws RecordIdentifierIsNull when {@code recordIdentifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code recordIdentifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public WeightRecord GetRecord(UUID recordIdentifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (recordIdentifier == null) throw new RecordIdentifierIsNull();

        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = database.query(
                    /* FROM  */   TableWeight_Name,
                    /* SELECT */  TableWeight_Columns,
                    /* WHERE */   TableWeight_Key_Id + " = ?",
                    /* WHERE parameter */ new String[] { recordIdentifier.toString() },
                    /* GROUP BY */ null,
                    /* HAVING */ null,
                    /* ORDER BY */ null))
            {
                if (!cursor.moveToFirst())
                    throw new RecordNotFound(recordIdentifier);

                if (!cursor.isLast())
                    throw new RepositoryException("More than one returned record with the id (" + recordIdentifier +") were found.");

                return ParseWeightRecordFromCursor(cursor);
            }
        }
        catch (RecordNotFound exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read records.", exception);
        }
    }

    /**
     * Gets a sequence of weight records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count  maximum number of record to be read from the repository
     * @return read sequence of weight records
     * @throws OffsetIsNegative    when {@code offset} is negative.
     * @throws CountIsNotPositive  when {@code count} is not positive.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<WeightRecord> GetRecordsDescending(long offset, int count) throws
            OffsetIsNegative,
            CountIsNotPositive,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (offset < 0L) throw new OffsetIsNegative();
        if (count <= 0) throw new CountIsNotPositive();

        final String query =
            "SELECT " +
                TableWeight_Key_Id + "," +
                TableWeight_Key_Value + "," +
                TableWeight_Key_Unit + "," +
                TableWeight_Key_TimeOfMeasurementDate + "," +
                TableWeight_Key_TimeOfMeasurementTime +
            " FROM " +
                TableWeight_Name +
            " ORDER BY " +
                TableWeight_Key_TimeOfMeasurementDate + " DESC," +
                TableWeight_Key_TimeOfMeasurementTime + " DESC" +
            " LIMIT ? OFFSET ?";

        final String[] parameter =
            {
                String.valueOf(count),
                String.valueOf(offset),
            };

        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = database.rawQuery(query, parameter))
            {
                return ParseWeightRecordsFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read records.", exception);
        }
    }

    /**
     * Gets all records for a specified day in descending order sorted by the time of measurement.
     *
     * @param dateOfDay The specific day to get the record for.
     * @return All records for the specified day in descending order sorted by the time of measurement.
     * @throws DateOfDayIsNull     when {@code dateOfDay} is {@code null}.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<WeightRecord> GetRecordsForDayDescending(LocalDate dateOfDay) throws
            DateOfDayIsNull,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (dateOfDay == null) throw new DateOfDayIsNull();

        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = database.query(
                /* FROM */ TableWeight_Name,
                /* SELECT */ TableWeight_Columns,
                /* WHERE */ TableWeight_Key_TimeOfMeasurementDate + " = ?",
                /* WHERE parameter */ new String[] { TimeStringConversion.ConvertDateToDateString(dateOfDay) },
                /* GROUP BY */ null,
                /* HAVING */ null,
                /* ORDER BY */ TableWeight_Key_TimeOfMeasurementDate + " DESC," +
                               TableWeight_Key_TimeOfMeasurementTime + " DESC"))
            {
                return ParseWeightRecordsFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to retrieve records.", exception);
        }
    }

    private ArrayList<WeightRecord> ParseWeightRecordsFromCursor(Cursor cursor)
    {
        ArrayList<WeightRecord> records = new ArrayList<>();

        while (cursor.moveToNext())
        {
            records.add(ParseWeightRecordFromCursor(cursor));
        }

        return records;
    }

    private WeightRecord ParseWeightRecordFromCursor(Cursor cursor)
    {
        final String identifier = cursor.getString(0);
        final double value = cursor.getDouble(1);
        final String unit = cursor.getString(2);
        final String dateOfMeasurement = cursor.getString(3);
        final String timeOfMeasurement = cursor.getString(4);

        return new WeightRecord(
            UUID.fromString(identifier),
            value,
            WeightUnit.valueOf(unit),
            TimeStringConversion.ConvertDateStringAndTimeStringToDateTime(dateOfMeasurement, timeOfMeasurement));
    }

    /**
     * Created or updates an existing record for the specified identifier with the specified measurements.
     *
     * @param weightRecord The record that should be created or updated.
     * @throws WeightRecordIsNull  when {@code weightRecord} is {@code null}.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public void CreateOrUpdateRecord(WeightRecord weightRecord) throws
            WeightRecordIsNull,
            WeightRecordIdentifierIsNull,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (weightRecord == null) throw new WeightRecordIsNull();
        if (weightRecord.Identifier == null) throw new WeightRecordIdentifierIsNull();

        final ContentValues values = new ContentValues();
        values.put(TableWeight_Key_Id, weightRecord.Identifier.toString());
        values.put(TableWeight_Key_Value, weightRecord.Value);
        values.put(TableWeight_Key_Unit, weightRecord.Unit.name());
        values.put(TableWeight_Key_TimeOfMeasurementDate,
                   TimeStringConversion.ConvertDateTimeToDateString(weightRecord.TimeOfMeasurement));
        values.put(TableWeight_Key_TimeOfMeasurementTime,
                   TimeStringConversion.ConvertDateTimeToTimeString(weightRecord.TimeOfMeasurement));

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                if (!TryInsertRecord(database, values) && !TryUpdateRecord(database, values))
                {
                    throw new Exception("Insert and update operation was not successful.");
                }

                database.setTransactionSuccessful();
            }
            finally
            {
                database.endTransaction();
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to create or update record (" + weightRecord.Identifier +").", exception);
        }
    }

    private boolean TryInsertRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final long rowId = database.insert(TableWeight_Name, null, values);
        return rowId != -1L;
    }

    private boolean TryUpdateRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final String selection = TableWeight_Key_Id + " = ?";
        final String[] selectionArgs = { (String)values.get(TableWeight_Key_Id) };

        final int affectedRows = database.update(TableWeight_Name, values, selection, selectionArgs);
        return affectedRows == 1;
    }

    /**
     * Deletes a record with a specific timestamp.
     *
     * @param recordIdentifier The identifier of the record that should be deleted.
     * @throws RecordIdentifierIsNull when {@code recordIdentifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code recordIdentifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteRecord(UUID recordIdentifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (recordIdentifier == null) throw new RecordIdentifierIsNull();

        final String selection = TableWeight_Key_Id + " = ?";
        final String[] selectionArgs = { recordIdentifier.toString() };

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                final int affectedRows = database.delete(TableWeight_Name, selection, selectionArgs);

                if (affectedRows == 0) throw new RecordNotFound(recordIdentifier);
                if (affectedRows > 1) throw new RepositoryException("More than one record was affected. Rollback will be initiated.");

                database.setTransactionSuccessful();
            }
            finally
            {
                database.endTransaction();
            }
        }
        catch (RecordNotFound exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to delete record (" + recordIdentifier +").", exception);
        }
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

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.delete(TableWeight_Name, null, null);
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to deletes all records.", exception);
        }
    }

    private void ThrowWhenDatabaseStateIsBad() throws RepositoryDisposed, RepositoryException
    {
        if (_disposed) throw new RepositoryDisposed();
        if (_upgradeRequested) throw new RepositoryException("Database upgrade failed.");
        if (_downgradeRequested) throw new RepositoryException("Database downgrade failed.");
    }

    /**
     * DO NOT USE IN PRODUCTION! Just for Debugging.
     */
    public void Reset()
    {
        final SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TableWeight_Name);
        onCreate(database);
        Log.d("DB", "RESET");
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