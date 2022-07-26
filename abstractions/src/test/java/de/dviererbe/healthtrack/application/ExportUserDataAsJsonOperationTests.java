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

import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.infrastructure.EmptyLogger;
import de.dviererbe.healthtrack.infrastructure.IDateTimeProvider;
import de.dviererbe.healthtrack.infrastructure.ILogger;
import de.dviererbe.healthtrack.infrastructure.IUserDataJsonFileOutputStreamProvider;
import de.dviererbe.healthtrack.persistence.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ExportUserDataAsJsonOperationTests
{
    private final static ExportUserDataAsJsonOperation.Options ExportAll =
            new ExportUserDataAsJsonOperation.Options(
                true,
                true,
                true,
                true,
                true);

    @Test
    public void When_ExportingEmptyRepositories_Should_ThrowNoException() throws ExportUserDataAsJsonOperation.ExportError, UnsupportedEncodingException
    {
        // Arrange:
        final ExportUserDataAsJsonOperation.Options options = ExportAll;

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final IUserDataJsonFileOutputStreamProvider userDataJsonFileOutputStreamProvider
                = new IUserDataJsonFileOutputStreamProvider()
        {
            private boolean called = false;

            @Override
            public OutputStream ProvideUserDataJsonFileOutputStream()
                    throws UserDataJsonFileOutputStreamCouldNotBeProvided
            {
                assertFalse(called);
                called = true;
                return outputStream;
            }
        };

        final IBloodPressureWidgetRepository bloodPressureWidgetRepository
                = new IBloodPressureWidgetRepository()
        {
            @Override
            public String GetProviderName()
            {
                return "Mocked Blood-Pressure Widget Repository";
            }

            @Override
            public String GetProviderVersion()
            {
                return "1";
            }

            @Override
            public long GetRecordCount()
            {
                return 0;
            }

            @Override
            public BloodPressureRecord GetRecord(UUID recordIdentifier) throws RepositoryException
            {
                if (recordIdentifier == null) throw new RecordIdentifierIsNull();

                throw new RecordNotFound(recordIdentifier);
            }

            @Override
            public List<BloodPressureRecord> GetRecordsDescending(long offset, int count) throws RepositoryException
            {
                if (offset < 0) throw new OffsetIsNegative();
                if (count <= 0) throw new CountIsNotPositive();

                return new ArrayList<>();
            }

            @Override
            public List<BloodPressureRecord> GetRecordsForDayDescending(LocalDate dateOfDay) throws RepositoryException
            {
                if (dateOfDay == null) throw new DateOfDayIsNull();

                return new ArrayList<>();
            }

            @Override
            public void CreateOrUpdateRecord(BloodPressureRecord bloodPressureRecord)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void DeleteRecord(UUID recordIdentifier)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void DeleteAllRecords()
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void Dispose()
            {
                throw new IllegalStateException("This method should not be called.");
            }
        };

        final IBloodSugarWidgetRepository bloodSugarWidgetRepository = null;

        final IFoodWidgetRepository foodWidgetRepository = null;

        final IStepWidgetRepository stepWidgetRepository
                = new IStepWidgetRepository()
        {
            @Override
            public String GetProviderName()
            {
                return "Mocked Step-Count Widget Repository";
            }

            @Override
            public String GetProviderVersion()
            {
                return "1";
            }

            @Override
            public int GetDefaultStepCountGoal()
            {
                return 1234;
            }

            @Override
            public void SetDefaultStepCountGoal(int stepCountGoal)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public long GetRecordCount()
            {
                return 0;
            }

            @Override
            public List<StepCountRecord> GetRecordsDescending(long offset, int count) throws RepositoryException
            {
                if (offset < 0) throw new OffsetIsNegative();
                if (count <= 0) throw new CountIsNotPositive();

                return new ArrayList<>();
            }

            @Override
            public StepCountRecord GetRecordForDay(LocalDate dateOfDay) throws RepositoryException
            {
                if (dateOfDay == null) throw new DateOfDayIsNull();

                throw new RecordNotFound(dateOfDay);
            }

            @Override
            public void CreateOrUpdateRecord(StepCountRecord stepCountRecord)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void DeleteRecordOfDay(LocalDate dateOfDay)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void DeleteAllRecords()
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void Dispose()
            {
                throw new IllegalStateException("This method should not be called.");
            }
        };

        final IWeightWidgetRepository weightWidgetRepository
                = new IWeightWidgetRepository()
        {
            @Override
            public String GetProviderName()
            {
                return "Mocked Weight Widget Repository";
            }

            @Override
            public String GetProviderVersion()
            {
                return "1";
            }

            @Override
            public long GetRecordCount()
            {
                return 0;
            }

            @Override
            public WeightRecord GetRecord(UUID recordIdentifier) throws RepositoryException
            {
                if (recordIdentifier == null) throw new RecordIdentifierIsNull();

                throw new RecordNotFound(recordIdentifier);
            }

            @Override
            public List<WeightRecord> GetRecordsDescending(long offset, int count) throws RepositoryException
            {
                if (offset < 0) throw new OffsetIsNegative();
                if (count <= 0) throw new CountIsNotPositive();

                return new ArrayList<>();
            }

            @Override
            public List<WeightRecord> GetRecordsForDayDescending(LocalDate dateOfDay) throws RepositoryException
            {
                if (dateOfDay == null) throw new DateOfDayIsNull();

                return new ArrayList<>();
            }

            @Override
            public void CreateOrUpdateRecord(WeightRecord weightRecord)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void DeleteRecord(UUID recordIdentifier)
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void DeleteAllRecords()
            {
                throw new IllegalStateException("This method should not be called.");
            }

            @Override
            public void Dispose()
            {
                throw new IllegalStateException("This method should not be called.");
            }
        };

        final IDateTimeProvider dateTimeProvider = new IDateTimeProvider()
        {
            @Override
            public LocalDateTime Now()
            {
                return LocalDateTime.of(2022, 7, 1, 12, 0, 0);
            }

            @Override
            public LocalDate Today()
            {
                return LocalDate.of(2022, 7, 1);
            }
        };

        final ILogger logger = new EmptyLogger();

        final ExportUserDataAsJsonOperation operation =
            new ExportUserDataAsJsonOperation(
                options,
                userDataJsonFileOutputStreamProvider,
                bloodPressureWidgetRepository,
                bloodSugarWidgetRepository,
                foodWidgetRepository,
                stepWidgetRepository,
                weightWidgetRepository,
                dateTimeProvider,
                logger);

        // Act:
        operation.Execute();

        // Assert:
        final String actualValue = outputStream.toString("UTF-8");
        final String expectedValue = "{}";
        assertEquals(expectedValue, actualValue);
    }
}
