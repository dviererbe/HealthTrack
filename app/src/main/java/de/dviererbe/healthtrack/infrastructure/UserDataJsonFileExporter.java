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

import android.net.Uri;
import android.util.JsonWriter;
import android.util.Log;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.persistence.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implements the user data exporter mechanism by writing to a file in JSON (JavaScript Object Notation) format.
 */
public class UserDataJsonFileExporter implements IUserDataExporter
{
    private static final String TAG = "UserDataJsonFileExporter";

    private final IContentResolver _contentResolver;

    private final IStepWidgetRepository _stepWidgetRepository;
    private final IWeightWidgetRepository _weightWidgetRepository;
    private final IFoodWidgetRepository _foodWidgetRepository;
    private final IBloodPressureWidgetRepository _bloodPressureWidgetRepository;
    private final IBloodSugarWidgetRepository _bloodSugarWidgetRepository;
    private final IDateTimeProvider _dateTimeProvider;

    /**
     * Creates a new {@link UserDataJsonFileExporter} instance with a reference to the user data repositories.
     *
     * @param contentResolver reference to an implementation that allows to resolve content URI's.
     * @param stepWidgetRepository reference to a repository that holds step count related data.
     * @param weightWidgetRepository reference to a repository that holds weight related data.
     * @param foodWidgetRepository reference to a repository that holds food related data.
     * @param bloodPressureWidgetRepository reference to a repository that holds blood pressure related data.
     * @param bloodSugarWidgetRepository reference to a repository that holds blood sugar related data.
     * @param dateTimeProvider reference to a mechanism that can provide the current date time
     */
    public UserDataJsonFileExporter(
        final IContentResolver contentResolver,
        final IStepWidgetRepository stepWidgetRepository,
        final IWeightWidgetRepository weightWidgetRepository,
        final IFoodWidgetRepository foodWidgetRepository,
        final IBloodPressureWidgetRepository bloodPressureWidgetRepository,
        final IBloodSugarWidgetRepository bloodSugarWidgetRepository,
        final IDateTimeProvider dateTimeProvider)
    {
        _contentResolver = contentResolver;

        _stepWidgetRepository = stepWidgetRepository;
        _weightWidgetRepository = weightWidgetRepository;
        _foodWidgetRepository = foodWidgetRepository;
        _bloodPressureWidgetRepository = bloodPressureWidgetRepository;
        _bloodSugarWidgetRepository = bloodSugarWidgetRepository;
        _dateTimeProvider = dateTimeProvider;
    }

    /**
     * Exports specific user data.
     *
     * @param storageLocation         the location where the user data should be stored.
     * @param exportStepCountData     {@code true} when step count related user data should be imported; otherwise {@code false}.
     * @param exportWeightData        {@code true} when weight related user data should be imported; otherwise {@code false}.
     * @param exportFoodData          {@code true} when food related user data should be imported; otherwise {@code false}.
     * @param exportBloodPressureData {@code true} when blood pressure related user data should be imported; otherwise {@code false}.
     * @param exportBloodSugarData    {@code true} when blood sugar related user data should be imported; otherwise {@code false}.
     * @throws ExportError when an error occurred during the export user data operation.
     */
    @Override
    public void ExportUserData(
            Uri storageLocation,
            boolean exportStepCountData,
            boolean exportWeightData,
            boolean exportFoodData,
            boolean exportBloodPressureData,
            boolean exportBloodSugarData)
            throws ExportError
    {
        try
        {
            try (final OutputStream fileStream = _contentResolver.OpenOutputStream(storageLocation))
            {
                try (final OutputStreamWriter streamWriter = new OutputStreamWriter(fileStream))
                {
                    try (final JsonWriter jsonWriter = new JsonWriter(streamWriter))
                    {
                        ExportUserData(jsonWriter, exportStepCountData, exportWeightData, exportFoodData, exportBloodPressureData, exportBloodSugarData);
                    }
                }
            }
        }
        catch (Exception exception)
        {
            final String errorMessage = "Failed to create file (path: " +  storageLocation + ").";

            Log.d(TAG, errorMessage, exception);
            throw new ExportError(errorMessage, exception);
        }
    }

    private void ExportUserData(
        final JsonWriter jsonWriter,
        final boolean exportStepCountData,
        final boolean exportWeightData,
        final boolean exportFoodData,
        final boolean exportBloodPressureData,
        final boolean exportBloodSugarData)
        throws IOException, IWeightWidgetRepository.RepositoryException
    {
        jsonWriter.beginObject();
        jsonWriter.name("created").value(GetIsoDateTimeString(_dateTimeProvider.Now()));

        if (exportStepCountData)
        {
            ExportStepData(jsonWriter);
        }

        if (exportWeightData)
        {
            ExportWeightData(jsonWriter);
        }

        if (exportFoodData)
        {
            ExportFoodData(jsonWriter);
        }

        if (exportBloodPressureData)
        {
            ExportBloodPressureData(jsonWriter);
        }

        if (exportBloodSugarData)
        {
            ExportBloodSugarData(jsonWriter);
        }

        jsonWriter.endObject();
    }

    private void ExportStepData(final JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("steps").beginArray().endArray();
    }

    private void ExportWeightData(final JsonWriter jsonWriter) throws
            IOException,
            IWeightWidgetRepository.RepositoryException
    {
        jsonWriter.name("weight").beginArray();

        final int count = _weightWidgetRepository.GetRecordCount();
        final int limit = 128;
        long offset = 0;

        while (offset < count)
        {
            List<WeightRecord> records = _weightWidgetRepository.GetRecordsDescending(offset, limit);
            WriteWeightRecords(jsonWriter, records);
            offset += records.size();
        }

        jsonWriter.endArray();
    }

    private void WriteWeightRecords(JsonWriter jsonWriter, List<WeightRecord> records) throws IOException
    {
        for (WeightRecord weightRecord : records)
        {
            jsonWriter
            .beginObject()
            .name("identifier").value(weightRecord.Identifier.toString())
            .name("value").value(weightRecord.Value)
            .name("unit").value(weightRecord.Unit.name())
            .name("timeOfMeasurement").value(GetIsoDateTimeString(weightRecord.TimeOfMeasurement))
            .endObject();
        }
    }

    private void ExportFoodData(final JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("food").beginArray().endArray();
    }

    private void ExportBloodPressureData(final JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("bloodPressure").beginArray().endArray();
    }

    private void ExportBloodSugarData(final JsonWriter jsonWriter) throws IOException
    {
        jsonWriter.name("bloodSugar").beginArray().endArray();
    }

    private static String GetIsoDateTimeString(LocalDateTime dateTime)
    {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
    }
}
