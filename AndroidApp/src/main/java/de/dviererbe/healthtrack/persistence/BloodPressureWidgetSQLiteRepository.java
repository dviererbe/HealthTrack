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
import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.MedicationState;
import de.dviererbe.healthtrack.persistence.exceptions.OneOrMorePropertiesAreInvalid;

import java.time.LocalDate;
import java.util.UUID;

public class BloodPressureWidgetSQLiteRepository extends SQLiteRepositoryBase<BloodPressureRecord>
{
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
        super(  context,
                "BloodPressureWidget.db",
                1,
                TableBloodPressure_Name,
                TableBloodPressure_Key_Id,
                TableBloodPressure_Columns);
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

    @Override
    protected BloodPressureRecord ParseRecordFromCursor(final Cursor cursor)
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

    @Override
    protected Cursor QueryRecordByIdentifier(SQLiteDatabase database, String identifier)
    {
        return  database.query(
            /* FROM  */   TableBloodPressure_Name,
            /* SELECT */  TableBloodPressure_Columns,
            /* WHERE */   TableBloodPressure_Key_Id + " = ?",
            /* WHERE parameter */ new String[] { identifier },
            /* GROUP BY */ null,
            /* HAVING */ null,
            /* ORDER BY */ null);
    }

    @Override
    protected Cursor QueryRecordsDescending(SQLiteDatabase database, long offset, int count)
    {
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

        return database.rawQuery(query, parameter);
    }

    @Override
    protected Cursor QueryRecordsForDayDescending(SQLiteDatabase database, LocalDate day)
    {
        return database.query(
                /* FROM */ TableBloodPressure_Name,
                /* SELECT */ TableBloodPressure_Columns,
                /* WHERE */ TableBloodPressure_Key_TimeOfMeasurementDate + " = ?",
                /* WHERE parameter */ new String[] { TimeStringConversion.ConvertDateToDateString(day) },
                /* GROUP BY */ null,
                /* HAVING */ null,
                /* ORDER BY */ TableBloodPressure_Key_TimeOfMeasurementDate + " DESC," +
                               TableBloodPressure_Key_TimeOfMeasurementTime + " DESC");
    }

    @Override
    protected void ValidateRecord(BloodPressureRecord bloodPressureRecord) throws OneOrMorePropertiesAreInvalid
    {
        if (bloodPressureRecord.Identifier == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "Identifier",
                    "Identifier is null.");
        }

        if (bloodPressureRecord.TimeOfMeasurement == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "TimeOfMeasurement",
                    "TimeOfMeasurement is null.");
        }

        if (bloodPressureRecord.Systolic < 0)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "Systolic",
                    "Systolic blood pressure is negative.");
        }

        if (bloodPressureRecord.Diastolic < 0)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "Diastolic",
                    "Diastolic blood pressure is negative.");
        }

        if (bloodPressureRecord.Medication == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "Medication",
                    "Medication state is null.");
        }

        if (bloodPressureRecord.Pulse < 0)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "bloodPressureRecord",
                "Pulse",
                "Pulse is negative.");
        }

        if (bloodPressureRecord.Unit == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "Unit",
                    "Unit is null.");
        }

        if (bloodPressureRecord.Note == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "bloodPressureRecord",
                    "Note",
                    "Note is null.");
        }
    }

    @Override
    protected ContentValues PackageValues(BloodPressureRecord bloodPressureRecord)
    {
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

        return values;
    }
}