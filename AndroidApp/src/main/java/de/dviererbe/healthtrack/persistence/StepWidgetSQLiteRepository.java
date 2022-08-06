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
import de.dviererbe.healthtrack.domain.StepCountRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implements the {@link IStepWidgetRepository} interface using an SQLite database.
 */
public class StepWidgetSQLiteRepository
        extends SQLiteOpenHelper
        implements IStepWidgetRepository
{
    private static final String DatabaseName = "StepsWidget.db";
    private static final int DatabaseVersion = 1;
    private static final SQLiteDatabase.CursorFactory CursorFactory = null;

    private boolean _disposed = false;
    private boolean _downgradeRequested = false;
    private boolean _upgradeRequested = false;

    /**
     * The default step count goal for a day.
     */
    private static final int DefaultStepCountGoal = 5000;

    private static final String TablePreferences_Name = "Preferences";
    private static final String TablePreferences_Key_Name = "Name";
    private static final String TablePreferences_Key_Value = "Value";

    private static final String PreferenceName_DefaultStepCountGoal = "'DefaultStepCountGoal'";

    private static final String TableSteps_Name = "Steps";

    private static final String TableSteps_Key_Id = "Id";
    private static final String TableSteps_Key_Count = "Count";
    private static final String TableSteps_Key_Goal = "Goal";
    private static final String TableSteps_Key_TimeOfMeasurementDate = "TimeOfMeasurementDate";
    private static final String TableSteps_Key_TimeOfMeasurementTime = "TimeOfMeasurementTime";

    private static final String[] TableSteps_Columns =
            {
                TableSteps_Key_Id,
                TableSteps_Key_Count,
                TableSteps_Key_Goal,
                TableSteps_Key_TimeOfMeasurementDate,
                TableSteps_Key_TimeOfMeasurementTime,
            };

    public StepWidgetSQLiteRepository(final @Nullable Context context)
    {
        super(context, DatabaseName, CursorFactory, DatabaseVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final SQLiteDatabase database)
    {
        database.beginTransaction();

        try
        {
            CreateStepsTable(database);
            CreatePreferencesTable(database);

            database.setTransactionSuccessful();
        }
        finally
        {
            database.endTransaction();
        }
    }

    private void CreateStepsTable(final SQLiteDatabase database)
    {
        final String createStepsTableStatement =
            "CREATE TABLE " + TableSteps_Name +
            " (" +
                TableSteps_Key_Id + " TEXT PRIMARY KEY NOT NULL, " +
                TableSteps_Key_Count + " INTEGER NOT NULL," +
                TableSteps_Key_Goal + " INTEGER NOT NULL," +
                TableSteps_Key_TimeOfMeasurementDate + " TEXT UNIQUE NOT NULL," +
                TableSteps_Key_TimeOfMeasurementTime + " TEXT NOT NULL" +
            ")";

        database.execSQL(createStepsTableStatement);
    }

    private void CreatePreferencesTable(final SQLiteDatabase database)
    {
        final String createTablePreferencesStatement =
            "CREATE TABLE " + TablePreferences_Name +
            " (" +
                TablePreferences_Key_Name + " TEXT PRIMARY KEY NOT NULL," +
                TablePreferences_Key_Value + " TEXT NOT NULL" +
            ")";

        final String insertDefaultStepCountGoalStatement =
            "INSERT INTO " +
                TablePreferences_Name +
            "(" +
                TablePreferences_Key_Name + "," +
                TablePreferences_Key_Value +
            ") VALUES (" +
                    PreferenceName_DefaultStepCountGoal + "," +
                "'" + DefaultStepCountGoal + "'" +
            ")";

        database.execSQL(createTablePreferencesStatement);
        database.execSQL(insertDefaultStepCountGoalStatement);
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
     * Gets the name of the step widget repository provider.
     *
     * @return {@link String} representation of the step widget repository provider name.
     */
    @Override
    public String GetProviderName()
    {
        return getClass().getName();
    }

    /**
     * Gets the version of the step widget repository provider.
     *
     * @return {@link String} representation of the step widget repository provider version.
     */
    @Override
    public String GetProviderVersion()
    {
        return String.valueOf(DatabaseVersion);
    }

    /**
     * Gets the default step count goal for a day.
     *
     * @return scalar count of the default step count goal for a day.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public int GetDefaultStepCountGoal() throws RepositoryDisposed, RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();

        final String selectDefaultStepCountQuery =
            "SELECT " +
                "CAST(" + TablePreferences_Key_Value + " AS INTEGER)" +
            " FROM " +
                TablePreferences_Name +
            " WHERE " +
                TablePreferences_Key_Name + " = " + PreferenceName_DefaultStepCountGoal;
        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = database.rawQuery(selectDefaultStepCountQuery, null))
            {
                return (int)ReadScalar(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read default step count preference.", exception);
        }
    }

    /**
     * Sets the default step count goal for a day.
     *
     * @param stepCountGoal the new default step count goal.
     * @throws StepCountGoalIsNotPositive when {@code stepCountGoal} is not positive.
     * @throws RepositoryDisposed         when the repository was already disposed.
     * @throws RepositoryException        when an unexpected I/O error occurs.
     */
    @Override
    public void SetDefaultStepCountGoal(final int stepCountGoal) throws
            StepCountGoalIsNotPositive,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (stepCountGoal <= 0) throw new StepCountGoalIsNotPositive();

        final String updateCountQuery =
            "UPDATE " +
                TablePreferences_Name +
            " SET " +
                TablePreferences_Key_Value + " = '" + stepCountGoal + "'" +
            " WHERE " +
                TablePreferences_Key_Name + " = " + PreferenceName_DefaultStepCountGoal;

        try
        {
            final SQLiteDatabase database = getReadableDatabase();
            database.execSQL(updateCountQuery);
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to update default step count preference.", exception);
        }
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
            final String selectCountQuery = "SELECT COUNT(*) FROM " + TableSteps_Name;

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
    private long ReadScalar(final Cursor cursor) throws RepositoryException
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
     * Gets a sequence of step count records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count  maximum number of record to be read from the repository
     * @return read sequence of step count records
     * @throws OffsetIsNegative    when {@code offset} is negative.
     * @throws CountIsNotPositive  when {@code count} is not positive.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<StepCountRecord> GetRecordsDescending(final long offset, final int count) throws
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
                TableSteps_Key_Id + "," +
                TableSteps_Key_Count + "," +
                TableSteps_Key_Goal + "," +
                TableSteps_Key_TimeOfMeasurementDate + "," +
                TableSteps_Key_TimeOfMeasurementTime +
            " FROM " +
                TableSteps_Name +
            " ORDER BY " +
                TableSteps_Key_TimeOfMeasurementDate + " DESC," +
                TableSteps_Key_TimeOfMeasurementTime + " DESC" +
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
                return ParseStepCountRecordsFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read records.", exception);
        }
    }

    /**
     * Gets the record for a specified day.
     *
     * @param dateOfDay The specific day to get the record for.
     * @return The record for the specified day. {@code null} when no step count was record for the day.
     * @throws DateOfDayIsNull     when {@code dateOfDay} is {@code null}.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public StepCountRecord GetRecordForDay(final LocalDate dateOfDay) throws
            DateOfDayIsNull,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (dateOfDay == null) throw new DateOfDayIsNull();

        final String dateOfDayString = TimeStringConversion.ConvertDateToDateString(dateOfDay);

        try
        {
            final SQLiteDatabase database = getReadableDatabase();


            try (final Cursor cursor = database.query(
                    /* FROM  */   TableSteps_Name,
                    /* SELECT */  TableSteps_Columns,
                    /* WHERE */   TableSteps_Key_TimeOfMeasurementDate + " = ?",
                    /* WHERE parameter */ new String[] { dateOfDayString },
                    /* GROUP BY */ null,
                    /* HAVING */ null,
                    /* ORDER BY */ null))
            {
                if (!cursor.moveToFirst())
                    throw new RecordNotFound(dateOfDay);

                if (!cursor.isLast())
                    throw new RepositoryException("More than one returned record with the time of measurement date (" + dateOfDayString +") were found.");

                return ParseStepCountRecordFromCursor(cursor);
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

    private List<StepCountRecord> ParseStepCountRecordsFromCursor(final Cursor cursor)
    {
        ArrayList<StepCountRecord> records = new ArrayList<>();

        while (cursor.moveToNext())
        {
            records.add(ParseStepCountRecordFromCursor(cursor));
        }

        return records;
    }

    private StepCountRecord ParseStepCountRecordFromCursor(final Cursor cursor)
    {
        final String identifierText = cursor.getString(0);
        final int stepCount = cursor.getInt(1);
        final int stepGoal = cursor.getInt(2);
        final String dateOfMeasurement = cursor.getString(3);
        final String timeOfMeasurement = cursor.getString(4);

        final UUID identifier = UUID.fromString(identifierText);

        final LocalDateTime dateTimeOfMeasurement =
            TimeStringConversion
            .ConvertDateStringAndTimeStringToDateTime(dateOfMeasurement, timeOfMeasurement);

        return new StepCountRecord(identifier, stepCount, stepGoal,dateTimeOfMeasurement);
    }

    /**
     * Created or updates an existing record for the specified day with the specified measurements.
     *
     * @param stepCountRecord The record that should be created or updated.
     * @throws StepCountRecordIsNull           when {@code stepCountRecord} is {@code null}.
     * @throws StepCountRecordParameterAreNull when parameter of {@code stepCountRecord} are {@code null}.
     * @throws RepositoryDisposed              when the repository was already disposed.
     * @throws RepositoryException             when an unexpected I/O error occurs.
     */
    @Override
    public void CreateOrUpdateRecord(final StepCountRecord stepCountRecord) throws
        StepCountRecordIsNull,
        StepCountRecordParameterAreNull,
        RepositoryDisposed,
        RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (stepCountRecord == null) throw new StepCountRecordIsNull();
        if (stepCountRecord.Identifier == null || stepCountRecord.TimeOfMeasurement == null) throw new StepCountRecordParameterAreNull();

        final String dateOfMeasurementString = TimeStringConversion.ConvertDateTimeToDateString(stepCountRecord.TimeOfMeasurement);
        final String timeOfMeasurementString = TimeStringConversion.ConvertDateTimeToTimeString(stepCountRecord.TimeOfMeasurement);

        final ContentValues values = new ContentValues();
        values.put(TableSteps_Key_Id, stepCountRecord.Identifier.toString());
        values.put(TableSteps_Key_Count, stepCountRecord.StepCount);
        values.put(TableSteps_Key_Goal, stepCountRecord.Goal);
        values.put(TableSteps_Key_TimeOfMeasurementDate, dateOfMeasurementString);
        values.put(TableSteps_Key_TimeOfMeasurementTime, timeOfMeasurementString);

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                database.delete(
                    TableSteps_Name,
                    TableSteps_Key_TimeOfMeasurementDate + " = ?",
                    new String[] { dateOfMeasurementString });

                if (!TryInsertRecord(database, values) && !TryUpdateRecord(database, values))
                {
                    throw new Exception("Insert or update operation was not successful.");
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
            throw new RepositoryException("Failed to create or update record (" + dateOfMeasurementString +").", exception);
        }
    }

    private boolean TryInsertRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final long rowId = database.insert(TableSteps_Name, null, values);
        return rowId != -1L;
    }

    private boolean TryUpdateRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final String selection = TableSteps_Key_Id + " = ?";
        final String[] selectionArgs = { (String)values.get(TableSteps_Key_Id) };

        final int affectedRows = database.update(TableSteps_Name, values, selection, selectionArgs);
        return affectedRows == 1;
    }

    /**
     * Deletes a record for a specific day.
     *
     * @param dateOfDay the day for which the record should be deleted.
     * @throws DateOfDayIsNull     when {@code dateOfDay} is {@code null}.
     * @throws RecordNotFound      when no record with the specified {@code dateOfDay} was found.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteRecordOfDay(final LocalDate dateOfDay) throws
            DateOfDayIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (dateOfDay == null) throw new DateOfDayIsNull();

        final String dateOfDayString = TimeStringConversion.ConvertDateToDateString(dateOfDay);
        final String selection = TableSteps_Key_TimeOfMeasurementDate + " = ?";
        final String[] selectionArgs = { dateOfDayString };

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                final int affectedRows = database.delete(TableSteps_Name, selection, selectionArgs);

                if (affectedRows == 0) throw new RecordNotFound(dateOfDay);
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
            throw new RepositoryException("Failed to delete record for day " + dateOfDayString + ".", exception);
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
            database.delete(TableSteps_Name, null, null);
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


