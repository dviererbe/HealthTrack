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
import de.dviererbe.healthtrack.domain.BloodPressureRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * A storage mechanism for the blood pressure widget data.
 */
public interface IBloodPressureWidgetRepository extends
        IRepository<BloodPressureRecord, IBloodPressureWidgetRepository.RepositoryException>,
        IDisposable
{
    /**
     * Gets the name of the blood pressure widget repository provider.
     *
     * @return {@link String} representation of the blood pressure widget repository provider name.
     */
    String GetProviderName();

    /**
     * Gets the version of the blood pressure widget repository provider.
     *
     * @return {@link String} representation of the blood pressure widget repository provider version.
     */
    String GetProviderVersion();

    /**
     * Gets the count of all stored records.
     *
     * @return scalar count of all stored records.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    long GetRecordCount() throws
            RepositoryDisposed,
            RepositoryException;

    /**
     * Gets a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record that should be retrieved.
     * @return the {@link BloodPressureRecord} with the specified identifier
     * @throws RecordIdentifierIsNull when {@code recordIdentifier} is {@code null}.
     * @throws RecordNotFound when no record with the specified {@code recordIdentifier} was found.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    BloodPressureRecord GetRecord(UUID recordIdentifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException;


    /**
     * Gets a sequence of blood pressure records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count maximum number of record to be read from the repository
     * @return read sequence of blood pressure records
     * @throws OffsetIsNegative when {@code offset} is negative.
     * @throws CountIsNotPositive when {@code count} is not positive.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    List<BloodPressureRecord> GetRecordsDescending(long offset, int count) throws
            OffsetIsNegative,
            CountIsNotPositive,
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when {@link IBloodPressureWidgetRepository#GetRecordsDescending} is called
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
     * The {@link Exception} that is thrown when {@link IBloodPressureWidgetRepository#GetRecordsDescending} is called
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
     * Gets all records for a specified day in descending order sorted by the time of measurement.
     *
     * @param dateOfDay The specific day to get the records for.
     * @return All records for the specified day in descending order sorted by the time of measurement.
     * @throws DateOfDayIsNull when {@code dateOfDay} is {@code null}.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    List<BloodPressureRecord> GetRecordsForDayDescending(LocalDate dateOfDay) throws
            DateOfDayIsNull,
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when {@link IBloodPressureWidgetRepository#GetRecordsForDayDescending} is called
     * and the parameter {@code dateOfDay} is {@code null}.
     */
    class DateOfDayIsNull extends RepositoryException
    {
        public DateOfDayIsNull()
        {
            super("Parameter dateOfDay is null.");
        }
    }

    /**
     * Created or updates an existing record for the specified identifier with the specified measurements.
     *
     * @param bloodPressureRecord The record that should be created or updated.
     * @throws RecordIsNull when {@code bloodPressureRecord} is {@code null}.
     * @throws RecordParameterAreNull when {@code bloodPressureRecord} has {@code null} parameter.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void CreateOrUpdateRecord(BloodPressureRecord bloodPressureRecord) throws
            RecordIsNull,
            RecordParameterAreNull,
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when {@link IBloodPressureWidgetRepository#CreateOrUpdateRecord} is called
     * and the parameter {@code bloodPressureRecord} is {@code null}.
     */
    class RecordIsNull extends RepositoryException
    {
        public RecordIsNull()
        {
            super("Provided record instance is null.");
        }
    }

    /**
     * The {@link Exception} that is thrown when a function of {@link IBloodPressureWidgetRepository} gets called
     * with a {@link BloodPressureRecord} instance that has {@code null} parameter.
     */
    class RecordParameterAreNull extends RepositoryException
    {
        public RecordParameterAreNull()
        {
            super("Parameter of the blood pressure record are null.");
        }
    }

    /**
     * Deletes a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record that should be deleted.
     * @throws RecordIdentifierIsNull when {@code recordIdentifier} is {@code null}.
     * @throws RecordNotFound when no record with the specified {@code recordIdentifier} was found.
     * @throws RepositoryDisposed when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    void DeleteRecord(UUID recordIdentifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException;

    /**
     * The {@link Exception} that is thrown when a specified record identifier is {@code null}.
     */
    class RecordIdentifierIsNull extends RepositoryException
    {
        public RecordIdentifierIsNull()
        {
            super("Parameter record identifier is null.");
        }
    }

    /**
     * The {@link RepositoryException} that is thrown when no record for with a specific identifier was found.
     */
    class RecordNotFound extends RepositoryException
    {
        /**
         * Identifier of the record that was not found.
         */
        public final UUID RecordIdentifier;

        public RecordNotFound(UUID recordIdentifier)
        {
            super("A record with the specified identifier (" + recordIdentifier + ") was not found.");
            RecordIdentifier = recordIdentifier;
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
