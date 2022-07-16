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

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.StepCountRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A storage mechanism for the steps widget data.
 */
public interface IStepWidgetRepository extends IDisposable

{
    /**
     * Gets the default step count goal for a day.
     *
     * @return scalar count of the default step count goal for a day.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    int GetDefaultStepCountGoal() throws
            RepositoryDisposed,
            RepositoryException;

    /**
     * Sets the default step count goal for a day.
     *
     * @param stepCountGoal the new default step count goal.
     * @throws StepCountGoalIsNotPositive when {@code stepCountGoal} is not positive.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void SetDefaultStepCountGoal(int stepCountGoal) throws
            StepCountGoalIsNotPositive,
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when {@link IStepWidgetRepository#SetDefaultStepCountGoal} is called
     * and the parameter {@code stepCountGoal} is not positive.
     */
    class StepCountGoalIsNotPositive extends RepositoryException
    {
        public StepCountGoalIsNotPositive()
        {
            super("Parameter stepCountGoal is not positive.");
        }
    }

    /**
     * Gets the count of all stored records.
     *
     * @return scalar count of all stored records.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    int GetRecordCount() throws
            RepositoryDisposed,
            RepositoryException;

    /**
     * Gets a sequence of step count records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count maximum number of record to be read from the repository
     * @return read sequence of step count records
     * @throws OffsetIsNegative when {@code offset} is negative.
     * @throws CountIsNotPositive when {@code count} is not positive.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    List<StepCountRecord> GetRecordsDescending(long offset, int count) throws
        OffsetIsNegative,
        CountIsNotPositive,
        RepositoryDisposed,
        RepositoryException;

    /**
     * The {@link Exception} that is thrown when {@link IStepWidgetRepository#GetRecordsDescending} is called
     * and the parameter {@code offset} is negative.
     */
    class OffsetIsNegative extends RepositoryException
    {
        public OffsetIsNegative()
        {
            super("Parameter offset is negative.");
        }
    }

    /**
     * The {@link Exception} that is thrown when {@link IStepWidgetRepository#GetRecordsDescending} is called
     * and the parameter {@code count} is not positive.
     */
    class CountIsNotPositive extends RepositoryException
    {
        public CountIsNotPositive()
        {
            super("Parameter count is not positive.");
        }
    }

    /**
     * Gets the record for a specified day.
     *
     * @param dateOfDay The specific day to get the record for.
     * @return The record for the specified day.
     * @throws DateOfDayIsNull when {@code dateOfDay} is {@code null}.
     * @throws RecordNotFound when no record was found fpr the day.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    StepCountRecord GetRecordForDay(LocalDate dateOfDay) throws
            DateOfDayIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException;

    /**
     * Created or updates an existing record for the specified day with the specified measurements.
     *
     * @param stepCountRecord The record that should be created or updated.
     * @throws StepCountRecordIsNull when {@code stepCountRecord} is {@code null}.
     * @throws StepCountRecordParameterAreNull when parameter of {@code stepCountRecord} are {@code null}.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void CreateOrUpdateRecord(StepCountRecord stepCountRecord) throws
            StepCountRecordIsNull,
            StepCountRecordParameterAreNull,
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when a function of {@link IStepWidgetRepository} gets called
     * with a {@link StepCountRecord} null instance.
     */
    class StepCountRecordIsNull extends RepositoryException
    {
        public StepCountRecordIsNull()
        {
            super("Step count record is null.");
        }
    }

    /**
     * The {@link Exception} that is thrown when a function of {@link IStepWidgetRepository} gets called
     * with a {@link StepCountRecord} instance that has {@code null} parameter.
     */
    class StepCountRecordParameterAreNull extends RepositoryException
    {
        public StepCountRecordParameterAreNull()
        {
            super("Parameter of the step count record instance are null.");
        }
    }

    /**
     * Deletes a record for a specific day.
     *
     * @param dateOfDay the day for which the record should be deleted.
     * @throws DateOfDayIsNull  when {@code dateOfDay} is {@code null}.
     * @throws RecordNotFound when no record with the specified {@code dateOfDay} was found.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void DeleteRecordOfDay(LocalDate dateOfDay) throws
        DateOfDayIsNull,
        RecordNotFound,
        RepositoryDisposed,
        RepositoryException;

    /**
     * The {@link Exception} that is thrown when {@link IStepWidgetRepository#GetRecordForDay(LocalDate)} or
     * {@link IStepWidgetRepository#DeleteRecordOfDay(LocalDate)} is called and the parameter {@code dateOfDay}
     * is {@code null}.
     */
    class DateOfDayIsNull extends RepositoryException
    {
        public DateOfDayIsNull()
        {
            super("Parameter dateOfDay is null.");
        }
    }

    /**
     * The {@link Exception} that is thrown when no record for a specific date of day was found.
     */
    class RecordNotFound extends RepositoryException
    {
        /**
         * The day for which no record was found.
         */
        public final LocalDate DateOfDay;

        public RecordNotFound(LocalDate dateOfDay)
        {
            super("No record for the day (" + dateOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE) + ") was not found.");
            DateOfDay = dateOfDay;
        }
    }

    /**
     * Deletes all records.
     *
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void DeleteAllRecords() throws
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when a method is called after the repository was disposed.
     */
    class RepositoryDisposed extends RepositoryException
    {
        public RepositoryDisposed()
        {
            super("The repository resources were already disposed.");
        }
    }

    /**
     * The {@link Exception} that is thrown when an unexpected I/O error occurs in the repository implementation.
     */
    class RepositoryException extends Exception
    {
        /**
         * Constructs a new exception with the specified detail message. The cause is not initialized, and
         * may subsequently be initialized by a call to {@code Throwable.initCause(java.lang.Throwable)}.
         *
         * @param message the detail message. The detail message is saved for later retrieval by the {@code Throwable.getMessage()} method.
         */
        public RepositoryException(String message)
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
        public RepositoryException(String message, Throwable cause)
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
        public RepositoryException(Throwable cause)
        {
            super(cause);
        }
    }
}
