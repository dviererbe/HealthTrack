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

import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.IUserDataJsonTextWriterProvider;
import de.dviererbe.healthtrack.infrastructure.json.IJsonTextWriter;
import de.dviererbe.healthtrack.infrastructure.json.IRepositoryJsonTextSerializer;
import de.dviererbe.healthtrack.infrastructure.json.JsonError;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the logic to export collected user data as JSON.
 */
public class ExportUserDataAsJsonOperation
{
    private static final String TAG = "ExportUserDataAsJsonOperation";

    private final Options _options;
    private final IUserDataJsonTextWriterProvider _userDataJsonTextWriterProvider;
    private final Map<Widget, IRepositoryJsonTextSerializer> _widgetRepositoriesJsonTextSerializer;
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
     * @param userDataJsonTextWriterProvider
     *      a mechanism that provides an {@link IJsonTextWriter}
     *      for exporting the user data in the json text
     *      format to.
     * @param widgetRepositoriesJsonTextSerializer
     *      reference to the serializers that export the
     *      repository data as json text.
     * @param dateTimeProvider
     *      reference to a mechanism that provides the
     *      current date and time.
     * @param logger
     *      reference to a mechanism to write logs.
     */
    public ExportUserDataAsJsonOperation(
            final Options options,
            final IUserDataJsonTextWriterProvider userDataJsonTextWriterProvider,
            final Map<Widget, IRepositoryJsonTextSerializer> widgetRepositoriesJsonTextSerializer,
            final IDateTimeProvider dateTimeProvider,
            final ILogger logger)
    {
        _options = options;
        _userDataJsonTextWriterProvider = userDataJsonTextWriterProvider;
        _widgetRepositoriesJsonTextSerializer = widgetRepositoriesJsonTextSerializer;
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
            try (final IJsonTextWriter jsonTextWriter = _userDataJsonTextWriterProvider.ProvideUserDataJsonTextWriter())
            {
                ExportUserData(jsonTextWriter);
            }
        }
        catch (Exception exception)
        {
            final String errorMessage = "Failed to create user data json file.";

            _logger.LogError(TAG, errorMessage, exception);
            throw new ExportError(errorMessage, exception);
        }
    }

    private void ExportUserData(final IJsonTextWriter jsonTextWriter)
        throws
            RepositoryException,
            JsonError,
            IOException
    {
        jsonTextWriter.WriteStartObject();
        jsonTextWriter.WritePropertyName("created").WriteValue(_dateTimeProvider.Now());

        for (Widget widgetToExport : _options.WidgetsToExport)
        {
           final IRepositoryJsonTextSerializer repositoryJsonTextSerializer =
                   _widgetRepositoriesJsonTextSerializer.get(widgetToExport);

           if (repositoryJsonTextSerializer == null)
           {
               _logger.LogError(TAG, "Widget-Repository JsonTextSerializer for Widget '" + widgetToExport  +"' is missing!");
               continue;
           }

           jsonTextWriter.WritePropertyName(widgetToExport.name());
           repositoryJsonTextSerializer.SerializeAsJson(jsonTextWriter);
        }

        jsonTextWriter.WriteEndObject();
    }

    /**
     * Immutable data structure that hols the configured options
     * how the {@link ExportUserDataAsJsonOperation} should behave
     * when {@link ExportUserDataAsJsonOperation#Execute()} is called.
     */
    public static class Options
    {
        /**
         * Gets an immutable List that contains the widgets that should be exported.
         */
        public List<Widget> WidgetsToExport;

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
            final ArrayList<Widget> widgetsToExport = new ArrayList<>();

            if (exportBloodPressureData) widgetsToExport.add(Widget.bloodPressure);
            if (exportBloodSugarData) widgetsToExport.add(Widget.bloodSugar);
            if (exportFoodData) widgetsToExport.add(Widget.food);
            if (exportStepCountData) widgetsToExport.add(Widget.steps);
            if (exportWeightData) widgetsToExport.add(Widget.weight);

            WidgetsToExport = Collections.unmodifiableList(widgetsToExport);
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