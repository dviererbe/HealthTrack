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
import androidx.annotation.Nullable;
import de.dviererbe.healthtrack.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implements the {@link IBloodPressureWidgetRepository} interface using an SQLite database.
 */
public class BloodPressureWidgetSQLiteRepository
        extends SQLiteOpenHelper
        implements IBloodPressureWidgetRepository
{
    private static final String DatabaseName = "BloodPressureWidget.db";
    private static final int DatabaseVersion = 1;
    private static final SQLiteDatabase.CursorFactory CursorFactory = null;

    private boolean _disposed = false;
    private boolean _downgradeRequested = false;
    private boolean _upgradeRequested = false;

    private static final String TableBloodPressure_Name = "BloodPressure";
    private static final String TableBloodPressure_Key_Id = "Id";
    private static final String TableBloodPressure_Key_Systolic = "Systolic";
    private static final String TableBloodPressure_Key_Diastolic = "Diastolic";
    private static final String TableBloodPressure_Key_Unit = "Unit";
    private static final String TableBloodPressure_Key_Pulse = "Pulse";
    private static final String TableBloodPressure_Key_MedicationState = "MedicationState";
    private static final String TableBloodPressure_Key_TimeOfMeasurementDate = "TimeOfMeasurementDate";
    private static final String TableBloodPressure_Key_TimeOfMeasurementTime = "TimeOfMeasurementTime";
    private static final String TableBloodPressure_Key_Note = "Note";

    private static final String[] TableBloodPressure_Columns =
    {
        TableBloodPressure_Key_Id,
        TableBloodPressure_Key_Systolic,
        TableBloodPressure_Key_Diastolic,
        TableBloodPressure_Key_Unit,
        TableBloodPressure_Key_Pulse,
        TableBloodPressure_Key_MedicationState,
        TableBloodPressure_Key_TimeOfMeasurementDate,
        TableBloodPressure_Key_TimeOfMeasurementTime,
        TableBloodPressure_Key_Note,
    };


    public BloodPressureWidgetSQLiteRepository(@Nullable Context context)
    {
        super(context, DatabaseName, CursorFactory, DatabaseVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        final String createBloodPressureTableStatement =
            "CREATE TABLE " + TableBloodPressure_Name +
            " (" +
                TableBloodPressure_Key_Id + " TEXT PRIMARY KEY NOT NULL," +
                TableBloodPressure_Key_Systolic + " INTEGER NOT NULL," +
                TableBloodPressure_Key_Diastolic + " INTEGER NOT NULL," +
                TableBloodPressure_Key_Unit + " TEXT NOT NULL," +
                TableBloodPressure_Key_Pulse + " INTEGER NOT NULL," +
                TableBloodPressure_Key_MedicationState + " TEXT NOT NULL," +
                TableBloodPressure_Key_TimeOfMeasurementDate + " TEXT NOT NULL," +
                TableBloodPressure_Key_TimeOfMeasurementTime + " TEXT NOT NULL," +
                TableBloodPressure_Key_Note + " TEXT NOT NULL" +
            ")";

        database.execSQL(createBloodPressureTableStatement);
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
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion == newVersion) return;

        _downgradeRequested = true;
        throw new IllegalStateException("Database downgrade strategy is not implemented.");
    }

    /**
     * Gets the name of the blood pressure widget repository provider.
     *
     * @return {@link String} representation of the blood pressure widget repository provider name.
     */
    @Override
    public String GetProviderName()
    {
        return getClass().getName();
    }

    /**
     * Gets the version of the blood pressure widget repository provider.
     *
     * @return {@link String} representation of the blood pressure widget repository provider version.
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
            final String selectCountQuery = "SELECT COUNT(*) FROM " + TableBloodPressure_Name;

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
     * @return the {@link BloodPressureRecord} with the specified identifier
     * @throws RecordIdentifierIsNull when {@code recordIdentifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code recordIdentifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public BloodPressureRecord GetRecord(final UUID recordIdentifier) throws
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
                    /* FROM  */   TableBloodPressure_Name,
                    /* SELECT */  TableBloodPressure_Columns,
                    /* WHERE */   TableBloodPressure_Key_Id + " = ?",
                    /* WHERE parameter */ new String[] { recordIdentifier.toString() },
                    /* GROUP BY */ null,
                    /* HAVING */ null,
                    /* ORDER BY */ null))
            {
                if (!cursor.moveToFirst())
                    throw new RecordNotFound(recordIdentifier);

                if (!cursor.isLast())
                    throw new RepositoryException("More than one returned record with the id (" + recordIdentifier +") were found.");

                return ParseBloodPressureRecordFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read record.", exception);
        }
    }

    /**
     * Gets a sequence of blood pressure records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count  maximum number of record to be read from the repository
     * @return read sequence of blood pressure records
     * @throws OffsetIsNegative    when {@code offset} is negative.
     * @throws CountIsNotPositive  when {@code count} is not positive.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<BloodPressureRecord> GetRecordsDescending(final long offset, final int count) throws
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
                TableBloodPressure_Key_Id + "," +
                TableBloodPressure_Key_Systolic + "," +
                TableBloodPressure_Key_Diastolic + "," +
                TableBloodPressure_Key_Unit + "," +
                TableBloodPressure_Key_Pulse + "," +
                TableBloodPressure_Key_MedicationState + "," +
                TableBloodPressure_Key_TimeOfMeasurementDate + "," +
                TableBloodPressure_Key_TimeOfMeasurementTime + "," +
                TableBloodPressure_Key_Note +
            " FROM " +
                TableBloodPressure_Name +
            " ORDER BY " +
                TableBloodPressure_Key_TimeOfMeasurementDate + " DESC," +
                TableBloodPressure_Key_TimeOfMeasurementTime + " DESC" +
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
                return ParseBloodPressureRecordsFromCursor(cursor);
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
     * @param dateOfDay The specific day to get the records for.
     * @return All records for the specified day in descending order sorted by the time of measurement.
     * @throws DateOfDayIsNull     when {@code dateOfDay} is {@code null}.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<BloodPressureRecord> GetRecordsForDayDescending(LocalDate dateOfDay) throws
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
                    /* FROM */ TableBloodPressure_Name,
                    /* SELECT */ TableBloodPressure_Columns,
                    /* WHERE */ TableBloodPressure_Key_TimeOfMeasurementDate + " = ?",
                    /* WHERE parameter */ new String[] { TimeStringConversion.ConvertDateToDateString(dateOfDay) },
                    /* GROUP BY */ null,
                    /* HAVING */ null,
                    /* ORDER BY */ TableBloodPressure_Key_TimeOfMeasurementDate + " DESC," +
                                   TableBloodPressure_Key_TimeOfMeasurementDate + " DESC"))
            {
                return ParseBloodPressureRecordsFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to retrieve records.", exception);
        }
    }

    private ArrayList<BloodPressureRecord> ParseBloodPressureRecordsFromCursor(final Cursor cursor)
    {
        ArrayList<BloodPressureRecord> records = new ArrayList<>();

        while (cursor.moveToNext())
        {
            records.add(ParseBloodPressureRecordFromCursor(cursor));
        }

        return records;
    }

    private BloodPressureRecord ParseBloodPressureRecordFromCursor(final Cursor cursor)
    {
        final String identifier = cursor.getString(0);
        final int systolic = cursor.getInt(1);
        final int diastolic = cursor.getInt(2);
        final String unit = cursor.getString(3);
        final int pulse = cursor.getInt(4);
        final String medicationState = cursor.getString(5);
        final String dateOfMeasurement = cursor.getString(6);
        final String timeOfMeasurement = cursor.getString(7);
        final String note = cursor.getString(8);

        return new BloodPressureRecord(
                UUID.fromString(identifier),
                systolic,
                diastolic,
                BloodPressureUnit.valueOf(unit),
                pulse,
                MedicationState.valueOf(medicationState),
                TimeStringConversion.ConvertDateStringAndTimeStringToDateTime(dateOfMeasurement, timeOfMeasurement),
                note);
    }

    /**
     * Created or updates an existing record for the specified identifier with the specified measurements.
     *
     * @param bloodPressureRecord The record that should be created or updated.
     * @throws RecordIsNull           when {@code bloodPressureRecord} is {@code null}.
     * @throws RecordParameterAreNull when {@code bloodPressureRecord} has {@code null} parameter.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public void CreateOrUpdateRecord(final BloodPressureRecord bloodPressureRecord) throws
            RecordIsNull,
            RecordParameterAreNull,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (bloodPressureRecord == null) throw new RecordIsNull();

        if (bloodPressureRecord.Identifier == null ||
            bloodPressureRecord.Medication == null ||
            bloodPressureRecord.TimeOfMeasurement == null ||
            bloodPressureRecord.Note == null)
        {
            throw new RecordParameterAreNull();
        }

        final ContentValues values = new ContentValues();
        values.put(TableBloodPressure_Key_Id, bloodPressureRecord.Identifier.toString());
        values.put(TableBloodPressure_Key_Systolic, bloodPressureRecord.Systolic);
        values.put(TableBloodPressure_Key_Diastolic, bloodPressureRecord.Diastolic);
        values.put(TableBloodPressure_Key_Unit, bloodPressureRecord.Unit.name());
        values.put(TableBloodPressure_Key_Pulse, bloodPressureRecord.Pulse);
        values.put(TableBloodPressure_Key_MedicationState, bloodPressureRecord.Medication.name());
        values.put(TableBloodPressure_Key_TimeOfMeasurementDate,
                   TimeStringConversion.ConvertDateTimeToDateString(bloodPressureRecord.TimeOfMeasurement));
        values.put(TableBloodPressure_Key_TimeOfMeasurementTime,
                   TimeStringConversion.ConvertDateTimeToTimeString(bloodPressureRecord.TimeOfMeasurement));
        values.put(TableBloodPressure_Key_Note, bloodPressureRecord.Note);

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
            throw new RepositoryException("Failed to create or update record (" + bloodPressureRecord.Identifier +").", exception);
        }
    }

    private boolean TryInsertRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final long rowId = database.insert(TableBloodPressure_Name, null, values);
        return rowId != -1L;
    }

    private boolean TryUpdateRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final String selection = TableBloodPressure_Key_Id + " = ?";
        final String[] selectionArgs = { (String)values.get(TableBloodPressure_Key_Id) };

        final int affectedRows = database.update(TableBloodPressure_Name, values, selection, selectionArgs);
        return affectedRows == 1;
    }

    /**
     * Deletes a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record that should be deleted.
     * @throws RecordIdentifierIsNull when {@code recordIdentifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code recordIdentifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteRecord(final UUID recordIdentifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (recordIdentifier == null) throw new RecordIdentifierIsNull();

        final String selection = TableBloodPressure_Key_Id + " = ?";
        final String[] selectionArgs = { recordIdentifier.toString() };

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                final int affectedRows = database.delete(TableBloodPressure_Name, selection, selectionArgs);

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
    public void DeleteAllRecords() throws
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.delete(TableBloodPressure_Name, null, null);
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to deletes all records.", exception);
        }
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