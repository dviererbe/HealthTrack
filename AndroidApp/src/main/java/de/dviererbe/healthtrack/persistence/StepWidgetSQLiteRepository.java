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
import androidx.annotation.Nullable;
import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.persistence.exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class StepWidgetSQLiteRepository
    extends
        SQLiteRepositoryBase<StepCountRecord>
    implements
        IDefaultStepCountGoalGetter,
        IDefaultStepCountGoalSetter,
        IQueryableByDay<StepCountRecord>,
        IDeletableByDay
{
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
        super(context, "StepsWidget.db", 1, TableSteps_Name, TableSteps_Key_Id, TableSteps_Columns);
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
        if (stepCountGoal <= 0) throw new StepCountGoalIsNotPositive(stepCountGoal);

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
     * Gets a record with a specific identifier.
     *
     * @param identifier The unique identifier of the record that should be retrieved.
     * @return the {@link StepCountRecord} with the specified identifier
     * @throws RecordIdentifierIsNull when {@code identifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code identifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public StepCountRecord GetRecord(LocalDate identifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (identifier == null) throw new RecordIdentifierIsNull();

        final String identifierAsString = TimeStringConversion.ConvertDateToDateString(identifier);

        return GetRecord(identifierAsString, database -> QueryRecordByDay(database, identifierAsString));
    }

    /**
     * Created or updates an existing record for the specified identifier with the specified measurements.
     *
     * @param stepCountRecord The record that should be created or updated.
     * @throws RecordIsNull                  when {@code record} is {@code null}.
     * @throws OneOrMorePropertiesAreInvalid when {@code record} contains invalid properties.
     * @throws RepositoryDisposed            when the repository was already disposed.
     * @throws RepositoryException           when an unexpected I/O error occurs.
     */
    @Override
    public void CreateOrUpdateRecord(final StepCountRecord stepCountRecord) throws
            RecordIsNull,
            OneOrMorePropertiesAreInvalid,
            RepositoryDisposed,
            RepositoryException
    {
        CreateOrUpdateRecordCore(
            stepCountRecord,
            database ->
            {
                final String whereClause = TableSteps_Key_TimeOfMeasurementDate + " = ?";
                final String[] whereArgs =
                    {
                        TimeStringConversion.ConvertDateTimeToDateString(stepCountRecord.TimeOfMeasurement)
                    };

                database.delete(TableSteps_Name, whereClause, whereArgs);
            });
    }

    /**
     * Deletes a record with a specific identifier.
     *
     * @param identifier The value that identifies the record that should be deleted uniquely.
     * @throws RecordIdentifierIsNull when {@code identifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code identifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteRecord(LocalDate identifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (identifier == null) throw new RecordIdentifierIsNull();

        final String whereClause = TableSteps_Key_TimeOfMeasurementDate + " = ?";
        final String whereArg = TimeStringConversion.ConvertDateToDateString(identifier);

        DeleteRecord(whereClause, whereArg);
    }

    @Override
    protected StepCountRecord ParseRecordFromCursor(final Cursor cursor)
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

    @Override
    protected Cursor QueryRecordByIdentifier(
        final SQLiteDatabase database,
        final String identifier)
    {
        return database.query(
            /* FROM  */   TableSteps_Name,
            /* SELECT */  TableSteps_Columns,
            /* WHERE */   TableSteps_Key_Id + " = ?",
            /* WHERE parameter */ new String[] { identifier },
            /* GROUP BY */ null,
            /* HAVING */ null,
            /* ORDER BY */ null);
    }

    private Cursor QueryRecordByDay(
            final SQLiteDatabase database,
            final String day)
    {
        return database.query(
            /* FROM  */   TableSteps_Name,
            /* SELECT */  TableSteps_Columns,
            /* WHERE */   TableSteps_Key_TimeOfMeasurementDate + " = ?",
            /* WHERE parameter */ new String[] { day },
            /* GROUP BY */ null,
            /* HAVING */ null,
            /* ORDER BY */ null);
    }

    @Override
    protected Cursor QueryRecordsDescending(
        final SQLiteDatabase database,
        final long offset,
        final int count)
    {
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

        return database.rawQuery(query, parameter);
    }

    @Override
    protected Cursor QueryRecordsForDayDescending(SQLiteDatabase database, LocalDate day)
    {
        return database.query(
                /* FROM */ TableSteps_Name,
                /* SELECT */ TableSteps_Columns,
                /* WHERE */ TableSteps_Key_TimeOfMeasurementDate + " = ?",
                /* WHERE parameter */ new String[] { TimeStringConversion.ConvertDateToDateString(day) },
                /* GROUP BY */ null,
                /* HAVING */ null,
                /* ORDER BY */ TableSteps_Key_TimeOfMeasurementDate + " DESC," +
                               TableSteps_Key_TimeOfMeasurementTime + " DESC");
    }

    @Override
    protected void ValidateRecord(StepCountRecord stepCountRecord) throws OneOrMorePropertiesAreInvalid
    {
        if (stepCountRecord.Identifier == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "stepCountRecord",
                "Identifier",
                "Identifier is null.");
        }

        if (stepCountRecord.TimeOfMeasurement == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "stepCountRecord",
                "TimeOfMeasurement",
                "TimeOfMeasurement is null.");
        }

        if (stepCountRecord.StepCount < 0)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "stepCountRecord",
                    "StepCount",
                    "StepCount is negative.");
        }

        if (stepCountRecord.Goal < 0)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "stepCountRecord",
                "Goal",
                "Goal is negative.");
        }
    }

    @Override
    protected ContentValues PackageValues(StepCountRecord stepCountRecord)
    {
        final String dateOfMeasurementString = TimeStringConversion.ConvertDateTimeToDateString(stepCountRecord.TimeOfMeasurement);
        final String timeOfMeasurementString = TimeStringConversion.ConvertDateTimeToTimeString(stepCountRecord.TimeOfMeasurement);

        final ContentValues values = new ContentValues();
        values.put(TableSteps_Key_Id, stepCountRecord.Identifier.toString());
        values.put(TableSteps_Key_Count, stepCountRecord.StepCount);
        values.put(TableSteps_Key_Goal, stepCountRecord.Goal);
        values.put(TableSteps_Key_TimeOfMeasurementDate, dateOfMeasurementString);
        values.put(TableSteps_Key_TimeOfMeasurementTime, timeOfMeasurementString);

        return values;
    }
}


