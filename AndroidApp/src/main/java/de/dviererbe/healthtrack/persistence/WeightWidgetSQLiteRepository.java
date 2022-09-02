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
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.persistence.exceptions.OneOrMorePropertiesAreInvalid;

import java.time.LocalDate;
import java.util.UUID;

public class WeightWidgetSQLiteRepository extends SQLiteRepositoryBase<WeightRecord>
{
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
        super(context, "WeightWidget.db", 1, TableWeight_Name, TableWeight_Key_Id, TableWeight_Columns);
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

    @Override
    protected WeightRecord ParseRecordFromCursor(Cursor cursor)
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

    @Override
    protected Cursor QueryRecordByIdentifier(SQLiteDatabase database, String identifier)
    {
        return database.query(
            /* FROM  */   TableWeight_Name,
            /* SELECT */  TableWeight_Columns,
            /* WHERE */   TableWeight_Key_Id + " = ?",
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

        return database.rawQuery(query, parameter);
    }

    @Override
    protected Cursor QueryRecordsForDayDescending(SQLiteDatabase database, LocalDate day)
    {
        return database.query(
            /* FROM */ TableWeight_Name,
            /* SELECT */ TableWeight_Columns,
            /* WHERE */ TableWeight_Key_TimeOfMeasurementDate + " = ?",
            /* WHERE parameter */ new String[] { TimeStringConversion.ConvertDateToDateString(day) },
            /* GROUP BY */ null,
            /* HAVING */ null,
            /* ORDER BY */ TableWeight_Key_TimeOfMeasurementDate + " DESC," +
                           TableWeight_Key_TimeOfMeasurementTime + " DESC");

    }

    @Override
    protected void ValidateRecord(final WeightRecord weightRecord) throws OneOrMorePropertiesAreInvalid
    {
        if (weightRecord.Identifier == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "weightRecord",
                "Identifier",
                "Identifier is null.");
        }

        if (weightRecord.TimeOfMeasurement == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "weightRecord",
                "TimeOfMeasurement",
                "TimeOfMeasurement is null.");
        }

        if (weightRecord.Unit == null)
        {
            throw new OneOrMorePropertiesAreInvalid(
                "weightRecord",
                "Unit",
                "Weight unit is null.");
        }

        if (weightRecord.Value < 0.0)
        {
            throw new OneOrMorePropertiesAreInvalid(
                    "weightRecord",
                    "Value",
                    "Weight value is negative.");
        }
    }

    @Override
    protected ContentValues PackageValues(WeightRecord weightRecord)
    {
        final ContentValues values = new ContentValues();
        values.put(TableWeight_Key_Id, weightRecord.Identifier.toString());
        values.put(TableWeight_Key_Value, weightRecord.Value);
        values.put(TableWeight_Key_Unit, weightRecord.Unit.name());
        values.put(TableWeight_Key_TimeOfMeasurementDate,
                   TimeStringConversion.ConvertDateTimeToDateString(weightRecord.TimeOfMeasurement));
        values.put(TableWeight_Key_TimeOfMeasurementTime,
                   TimeStringConversion.ConvertDateTimeToTimeString(weightRecord.TimeOfMeasurement));

        return values;
    }
}