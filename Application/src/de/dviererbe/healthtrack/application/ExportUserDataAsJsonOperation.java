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

package de.dviererbe.healthtrack.application;

//import android.util.JsonWriter;
import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.IUserDataJsonFileOutputStreamProvider;
import de.dviererbe.healthtrack.persistence.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Encapsulates the logic to export collected user data as JSON.
 */
public class ExportUserDataAsJsonOperation
{
    private static final String TAG = "ExportUserDataAsJsonOperation";

    private final Options _options;
    private final IUserDataJsonFileOutputStreamProvider _userDataJsonFileOutputStreamProvider;
    private final IBloodPressureWidgetRepository _bloodPressureWidgetRepository;
    private final IBloodSugarWidgetRepository _bloodSugarWidgetRepository;
    private final IFoodWidgetRepository _foodWidgetRepository;
    private final IStepWidgetRepository _stepWidgetRepository;
    private final IWeightWidgetRepository _weightWidgetRepository;
    private final IDateTimeProvider _dateTimeProvider;
    private final ILogger _logger;

    /**
     * Initializes a new {@link ExportUserDataAsJsonOperation}
     * instance with references to all necessary parameters and dependencies.
     *
     * @param options
     *      specifies how this {@link ExportUserDataAsJsonOperation}
     *      instance should behave when {@link ExportUserDataAsJsonOperation#Execute()}
     *      is called.
     * @param userDataJsonFileOutputStreamProvider
     *      a mechanism that provides an {@link OutputStream}
     *      for exporting the user data in the json format to.
     * @param bloodPressureWidgetRepository
     *      reference to a repository that holds blood
     *      pressure related data.
     * @param bloodSugarWidgetRepository
     *      reference to a repository that holds blood
     *      sugar related data.
     * @param foodWidgetRepository
     *      reference to a repository that holds food
     *      related data.
     * @param stepWidgetRepository
     *      reference to a repository that holds step
     *      count related data.
     * @param weightWidgetRepository
     *      reference to a repository that holds weight
     *      related data.
     * @param dateTimeProvider
     *      reference to a mechanism that provides the
     *      current date and time.
     * @param logger
     *      reference to a mechanism to write logs.
     */
    public ExportUserDataAsJsonOperation(
            final Options options,
            final IUserDataJsonFileOutputStreamProvider userDataJsonFileOutputStreamProvider,
            final IBloodPressureWidgetRepository bloodPressureWidgetRepository,
            final IBloodSugarWidgetRepository bloodSugarWidgetRepository,
            final IFoodWidgetRepository foodWidgetRepository,
            final IStepWidgetRepository stepWidgetRepository,
            final IWeightWidgetRepository weightWidgetRepository,
            final IDateTimeProvider dateTimeProvider,
            final ILogger logger)
    {
        _options = options;
        _userDataJsonFileOutputStreamProvider = userDataJsonFileOutputStreamProvider;
        _bloodPressureWidgetRepository = bloodPressureWidgetRepository;
        _bloodSugarWidgetRepository = bloodSugarWidgetRepository;
        _foodWidgetRepository = foodWidgetRepository;
        _stepWidgetRepository = stepWidgetRepository;
        _weightWidgetRepository = weightWidgetRepository;
        _dateTimeProvider = dateTimeProvider;
        _logger = logger;
    }

    /**
     * Executes the operation to export the user data as specified.
     *
     * @throws ExportError When an error occurred during the export operation.
     */
    public void Execute() throws ExportError
    {
        try
        {
            try (final OutputStream fileStream = _userDataJsonFileOutputStreamProvider.ProvideUserDataJsonFileOutputStream())
            {
                try (final OutputStreamWriter streamWriter = new OutputStreamWriter(fileStream))
                {
                    try (final JsonWriter jsonWriter = new JsonWriter(streamWriter))
                    {
                        ExportUserData(jsonWriter);
                    }
                }
            }
        }
        catch (Exception exception)
        {
            final String errorMessage = "Failed to create user data json file.";

            _logger.LogError(TAG, errorMessage, exception);
            throw new ExportError(errorMessage, exception);
        }
    }

    private void ExportUserData(final JsonWriter jsonWriter) throws
        IOException,
        IBloodPressureWidgetRepository.RepositoryException,
        IBloodSugarWidgetRepository.RepositoryException,
        IFoodWidgetRepository.RepositoryException,
        IStepWidgetRepository.RepositoryException,
        IWeightWidgetRepository.RepositoryException
    {
        jsonWriter.beginObject();
        jsonWriter.name("created").value(GetIsoDateTimeString(_dateTimeProvider.Now()));

        if (_options.ExportBloodPressureData)
        {
            ExportBloodPressureData(jsonWriter);
        }

        if (_options.ExportBloodSugarData)
        {
            ExportBloodSugarData(jsonWriter);
        }

        if (_options.ExportFoodData)
        {
            ExportFoodData(jsonWriter);
        }

        if (_options.ExportStepCountData)
        {
            ExportStepCountData(jsonWriter);
        }

        if (_options.ExportWeightData)
        {
            ExportWeightData(jsonWriter);
        }

        jsonWriter.endObject();
    }

    private void ExportBloodPressureData(final JsonWriter jsonWriter) throws
            IOException, IBloodPressureWidgetRepository.RepositoryException
    {
        jsonWriter.name("bloodPressure").beginObject();

        WriteRepositoryProperties(
            _bloodPressureWidgetRepository,
            ExportUserDataAsJsonOperation::WriteBloodPressureRecord,
            jsonWriter);

        jsonWriter.endObject();
    }

    private static void WriteBloodPressureRecord(
            final BloodPressureRecord bloodPressureRecord,
            final JsonWriter jsonWriter)
            throws IOException
    {
        jsonWriter
            .beginObject()
            .name("identifier").value(bloodPressureRecord.Identifier.toString())
            .name("systolic").value(bloodPressureRecord.Systolic)
            .name("diastolic").value(bloodPressureRecord.Diastolic)
            .name("unit").value(bloodPressureRecord.Unit.name())
            .name("pulse").value(bloodPressureRecord.Pulse)
            .name("medication").value(bloodPressureRecord.Medication.name())
            .name("timeOfMeasurement").value(GetIsoDateTimeString(bloodPressureRecord.TimeOfMeasurement))
            .name("note").value(bloodPressureRecord.Note)
            .endObject();
    }

    private void ExportBloodSugarData(
            final JsonWriter jsonWriter)
            throws IOException
    {
        jsonWriter.name("bloodSugar").beginObject();

        // TODO: implement blood sugar data export

        jsonWriter.endObject();
    }

    private void ExportFoodData(
            final JsonWriter jsonWriter)
            throws IOException
    {
        jsonWriter.name("food").beginObject();

        // TODO: implement food data export

        jsonWriter.endObject();
    }

    private void ExportStepCountData(final JsonWriter jsonWriter) throws
            IOException, IStepWidgetRepository.RepositoryException
    {
        jsonWriter.name("stepCount").beginObject();

        WriteRepositoryProperties(
            _stepWidgetRepository,
            ExportUserDataAsJsonOperation::WriteStepCountRecord,
            jsonWriter);

        jsonWriter
            .name("defaultStepCountGoal")
            .value(_stepWidgetRepository.GetDefaultStepCountGoal());


        jsonWriter.endObject();
    }

    private static void WriteStepCountRecord(
            final StepCountRecord stepCountRecord,
            final JsonWriter jsonWriter)
            throws IOException
    {
        jsonWriter
            .beginObject()
            .name("stepCount").value(stepCountRecord.StepCount)
            .name("goal").value(stepCountRecord.Goal)
            .name("timeOfMeasurement").value(GetIsoDateTimeString(stepCountRecord.TimeOfMeasurement))
            .endObject();
    }

    private void ExportWeightData(final JsonWriter jsonWriter) throws
            IOException, IWeightWidgetRepository.RepositoryException
    {
        jsonWriter.name("weight").beginObject();

        WriteRepositoryProperties(
            _weightWidgetRepository,
            ExportUserDataAsJsonOperation::WriteWeightRecord,
            jsonWriter);

        jsonWriter.endObject();
    }

    private static void WriteWeightRecord(
            final WeightRecord weightRecord,
            final JsonWriter jsonWriter)
            throws IOException
    {
        jsonWriter
            .beginObject()
            .name("identifier").value(weightRecord.Identifier.toString())
            .name("value").value(weightRecord.Value)
            .name("unit").value(weightRecord.Unit.name())
            .name("timeOfMeasurement").value(GetIsoDateTimeString(weightRecord.TimeOfMeasurement))
            .endObject();
    }

    private <TRecord, TRepositoryException extends Exception>
    void WriteRepositoryProperties(
            final IRepository<TRecord, TRepositoryException> repository,
            final IRecordJsonSerializer<TRecord> recordJsonSerializer,
            final JsonWriter jsonWriter)
            throws IOException, TRepositoryException
    {
        jsonWriter.name("provider")
            .beginObject()
                .name("name").value(repository.GetProviderName())
                .name("version").value(repository.GetProviderVersion())
            .endObject();

        jsonWriter.name("records");
        WriteRecords(
            repository,
            recordJsonSerializer,
            jsonWriter);
    }

    private <TRecord, TRepositoryException extends Exception>
        void WriteRecords(
            final IRepository<TRecord, TRepositoryException> repository,
            final IRecordJsonSerializer<TRecord> recordJsonSerializer,
            final JsonWriter jsonWriter)
            throws IOException, TRepositoryException
    {
        final long count = repository.GetRecordCount();
        long offset = 0;

        final int maxBufferSize = 128;
        List<TRecord> buffer;

        jsonWriter.beginArray();

        while (offset < count)
        {
            buffer = repository.GetRecordsDescending(offset, maxBufferSize);

            for (TRecord record : buffer)
            {
                recordJsonSerializer.Serialize(record, jsonWriter);
            }

            offset += buffer.size();
        }

        jsonWriter.endArray();
    }

    private interface IRecordJsonSerializer<TRecord>
    {
        void Serialize(TRecord record, JsonWriter jsonWriter) throws IOException;
    }

    private static String GetIsoDateTimeString(LocalDateTime dateTime)
    {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Immutable data structure that hols the configured options
     * how the {@link ExportUserDataAsJsonOperation} should behave
     * when {@link ExportUserDataAsJsonOperation#Execute()} is called.
     */
    public static class Options
    {
        /**
         * Gets if the blood pressure related data should be exported.
         * {@code true} when blood pressure related user data should be
         * exported; otherwise {@code false}.
         */
        public final boolean ExportBloodPressureData;

        /**
         * Gets if the blood sugar related data should be exported.
         * {@code true} when blood sugar related user data should be
         * exported; otherwise {@code false}.
         */
        public final boolean ExportBloodSugarData;

        /**
         * Gets if the food related data should be exported. {@code true}
         * when food related user data should be exported; otherwise {@code false}.
         */
        public final boolean ExportFoodData;

        /**
         * Gets if the step count related data should be exported.
         * {@code true} when step count related user data should
         * be exported; otherwise {@code false}.
         */
        public final boolean ExportStepCountData;

        /**
         * Gets if the weight related data should be exported. {@code true}
         * when food related user data should be exported; otherwise {@code false}.
         */
        public final boolean ExportWeightData;

        /**
         * Initializes a new {@link Options} instance with a specific configuration.
         *
         * @param exportBloodPressureData
         *      {@code true} when blood pressure related user data
         *      should be exported; otherwise {@code false}.
         * @param exportBloodSugarData
         *      {@code true} when blood sugar related user data should
         *      be exported; otherwise {@code false}.
         * @param exportFoodData
         *      {@code true} when food related user data should be
         *      exported; otherwise {@code false}.
         * @param exportStepCountData
         *      {@code true} when step count related user data
         *      should be exported; otherwise {@code false}.
         * @param exportWeightData
         *      {@code true} when weight related user data should
         *      be exported; otherwise {@code false}.
         */
        public Options(
            final boolean exportBloodPressureData,
            final boolean exportBloodSugarData,
            final boolean exportFoodData,
            final boolean exportStepCountData,
            final boolean exportWeightData)
        {
            ExportBloodPressureData = exportBloodPressureData;
            ExportBloodSugarData = exportBloodSugarData;
            ExportFoodData = exportFoodData;
            ExportStepCountData = exportStepCountData;
            ExportWeightData = exportWeightData;
        }
    }

    /**
     * The exception that is thrown when an error occurred during the export user data operation.
     */
    public static class ExportError extends Exception
    {
        /**
         * Constructs a new exception with the specified detail message. The cause is not initialized, and
         * may subsequently be initialized by a call to {@code Throwable.initCause(java.lang.Throwable)}.
         *
         * @param message the detail message. The detail message is saved for later retrieval by the {@code Throwable.getMessage()} method.
         */
        public ExportError(String message)
        {
            super(message);
        }

        /**
         * Constructs a new exception with the specified detail message and cause.
         *
         * Note that the detail message associated with cause is not automatically incorporated in this exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval by the {@code Throwable.getMessage()} method).
         * @param cause  the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        public ExportError(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructs a new exception with the specified cause and a detail message of
         * {@code (cause==null ? null : cause.toString())} (which typically contains the
         * class and detail message of cause). This constructor is useful for exceptions
         * that are little more than wrappers for other throwables (for example, {@code PrivilegedActionException}).
         *
         * @param cause the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        public ExportError(Throwable cause)
        {
            super(cause);
        }
    }
}

class JsonWriter implements AutoCloseable
{
    private final OutputStreamWriter _streamWriter;

    public JsonWriter(OutputStreamWriter streamWriter)
    {
        _streamWriter = streamWriter;
    }

    public JsonWriter beginObject()
    {
        return this;
    }

    public JsonWriter endObject()
    {
        return this;
    }

    public JsonWriter beginArray()
    {
        return this;
    }

    public JsonWriter endArray()
    {
        return this;
    }

    public JsonWriter name(String records)
    {
        return this;
    }

    public JsonWriter value(boolean value)
    {
        return this;
    }

    public JsonWriter value(int value)
    {
        return this;
    }

    public JsonWriter value(double value)
    {
        return this;
    }

    public JsonWriter value(String value)
    {
        return this;
    }

    @Override
    public void close() throws Exception
    {
        _streamWriter.close();
    }
}