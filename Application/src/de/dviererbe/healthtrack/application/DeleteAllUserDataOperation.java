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

import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.persistence.IBulkDeletable;
import de.dviererbe.healthtrack.persistence.repositories.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the logic to delete all collected user data.
 */
public class DeleteAllUserDataOperation
{
    private static final String TAG = "DeleteAllUserDataOperation";

    private final IBulkDeletable _stepWidgetRepository;
    private final IBulkDeletable _weightWidgetRepository;
    private final IBulkDeletable _foodWidgetRepository;
    private final IBulkDeletable _bloodPressureWidgetRepository;
    private final IBulkDeletable _bloodSugarWidgetRepository;
    private final ILogger _logger;

    /**
     * Creates a new {@link DeleteAllUserDataOperation} instance with references to all user data repositories.
     *
     * @param stepWidgetRepository reference to a repository that holds step count related data.
     * @param weightWidgetRepository reference to a repository that holds weight related data.
     * @param foodWidgetRepository reference to a repository that holds food related data.
     * @param bloodPressureWidgetRepository reference to a repository that holds blood pressure related data.
     * @param bloodSugarWidgetRepository reference to a repository that holds blood sugar related data.
     * @param logger reference to a logger instance.
     */
    public DeleteAllUserDataOperation(
            final IBulkDeletable stepWidgetRepository,
            final IBulkDeletable weightWidgetRepository,
            final IBulkDeletable foodWidgetRepository,
            final IBulkDeletable bloodPressureWidgetRepository,
            final IBulkDeletable bloodSugarWidgetRepository,
            final ILogger logger)
    {
        _stepWidgetRepository = stepWidgetRepository;
        _weightWidgetRepository = weightWidgetRepository;
        _foodWidgetRepository = foodWidgetRepository;
        _bloodPressureWidgetRepository = bloodPressureWidgetRepository;
        _bloodSugarWidgetRepository = bloodSugarWidgetRepository;
        _logger = logger;
    }

    /**
     * Executes the operation to delete all collected user data.
     *
     * @throws OneOrMoreErrorsOccurred when one or more errors occurred while tyring to delete all user related data.
     */
    public void Execute() throws OneOrMoreErrorsOccurred
    {
        final ArrayList<Exception> exceptionList = new ArrayList<>();

        try
        {
            _stepWidgetRepository.DeleteAllRecords();
        }
        catch (Exception exception)
        {
            _logger.LogError(TAG, "Failed to delete step records.", exception);
            exceptionList.add(exception);
        }

        try
        {
            _weightWidgetRepository.DeleteAllRecords();
        }
        catch (Exception exception)
        {
            _logger.LogError(TAG, "Failed to delete weight records.", exception);
            exceptionList.add(exception);
        }

        try
        {
            _foodWidgetRepository.DeleteAllRecords();
        }
        catch (Exception exception)
        {
            _logger.LogError(TAG, "Failed to delete food records.", exception);
            exceptionList.add(exception);
        }

        try
        {
            _bloodPressureWidgetRepository.DeleteAllRecords();
        }
        catch (Exception exception)
        {
            _logger.LogError(TAG, "Failed to delete blood pressure records.", exception);
            exceptionList.add(exception);
        }

        try
        {
            _bloodSugarWidgetRepository.DeleteAllRecords();
        }
        catch (Exception exception)
        {
            _logger.LogError(TAG, "Failed to delete blood sugar records.", exception);
            exceptionList.add(exception);
        }

        if (!exceptionList.isEmpty())
        {
            exceptionList.trimToSize();
            throw new OneOrMoreErrorsOccurred(exceptionList);
        }
    }

    /**
     * The {@link Exception} that is thrown when one or more errors occurred while tyring to delete all user related data.
     */
    public static class OneOrMoreErrorsOccurred extends Exception
    {
        /**
         * The list of errors that occurred while trying to delete all records.
         */
        public final List<Exception> InnerErrors;

        /**
         * Instantiates a new {@link OneOrMoreErrorsOccurred} instance with the specific list of errors that occurred.
         *
         * @param innerErrors the list of errors that occurred while trying to delete all records.
         */
        public OneOrMoreErrorsOccurred(final List<Exception> innerErrors)
        {
            super("One or more errors occurred while trying to delete all user data.");
            InnerErrors = innerErrors;
        }
    }
}
