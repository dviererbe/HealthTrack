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

package de.dviererbe.healthtrack.presentation.main.home;

import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.domain.*;
import de.dviererbe.healthtrack.infrastructure.*;
import de.dviererbe.healthtrack.persistence.*;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureListItemViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListItemViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListItemViewModel;

import java.time.LocalDate;
import java.util.List;

public class HomeViewModel implements IDisposable
{
    private static final String TAG = "HomeViewModel";
    public final BloodPressureListItemViewModel LatestBloodPressureOfToday;
    public final StepCountListItemViewModel StepCountOfToday;
    public final WeightListItemViewModel LatestWeightOfToday;

    public HomeViewModel(
        final INavigationRouter navigationRouter,
        final IDateTimeProvider dateTimeProvider,
        final IBloodPressureWidgetRepository bloodPressureWidgetRepository,
        final IStepWidgetRepository stepWidgetRepository,
        final IWeightWidgetRepository weightWidgetRepository,
        final IPreferredUnitRepository preferredUnitRepository,
        final IDateTimeConverter dateTimeConverter,
        final INumericValueConverter numericValueConverter,
        final IWidgetConfigurationRepository widgetConfigurationRepository,
        final ILogger logger)
    {
        final LocalDate today = dateTimeProvider.Today();

        if (widgetConfigurationRepository.IsBloodPressureWidgetEnabled())
        {
            final BloodPressureUnit preferredUnit = preferredUnitRepository
                    .GetPreferredBloodPressureUnit()
                    .ToDomainBloodPressureUnit();

            final BloodPressureRecord latestBloodPressureRecordOfToday
                = TryGetLatestBloodPressureRecordOfToday(today, bloodPressureWidgetRepository, logger);

            if (latestBloodPressureRecordOfToday == null)
            {
                LatestBloodPressureOfToday =
                    new BloodPressureListItemViewModel(
                        navigationRouter,
                        numericValueConverter,
                        preferredUnit);
            }
            else
            {
                LatestBloodPressureOfToday =
                    new BloodPressureListItemViewModel(
                        navigationRouter,
                        dateTimeConverter,
                        numericValueConverter,
                        latestBloodPressureRecordOfToday,
                        preferredUnit);
            }
        }
        else
        {
            LatestBloodPressureOfToday = null;
        }

        if (widgetConfigurationRepository.IsStepCounterWidgetEnabled())
        {
            final StepCountRecord stepCountRecordOfToday
                = TryGetStepCountRecordOfToday(today, stepWidgetRepository, logger);

            if (stepCountRecordOfToday == null)
            {
                final int defaultStepCountGoal = TryGetDefaultStepCountGoal(stepWidgetRepository, logger);

                StepCountOfToday =
                    new StepCountListItemViewModel(
                        navigationRouter,
                        numericValueConverter,
                        defaultStepCountGoal);
            }
            else
            {
                StepCountOfToday =
                    new StepCountListItemViewModel(
                        navigationRouter,
                        dateTimeConverter,
                        numericValueConverter,
                        stepCountRecordOfToday);
            }
        }
        else
        {
            StepCountOfToday = null;
        }

        if (widgetConfigurationRepository.IsWeightWidgetEnabled())
        {
            final WeightUnit preferredUnit = preferredUnitRepository
                .GetPreferredMassUnit()
                .ToDomainWeightUnit();

            final WeightRecord latestWeightRecordOfToday
                = TryGetLatestWeightRecordOfToday(today, weightWidgetRepository, logger);

            if (latestWeightRecordOfToday == null)
            {
                LatestWeightOfToday =
                    new WeightListItemViewModel(
                        navigationRouter,
                        numericValueConverter,
                        preferredUnit);
            }
            else
            {
                LatestWeightOfToday =
                    new WeightListItemViewModel(
                        navigationRouter,
                        dateTimeConverter,
                        numericValueConverter,
                        latestWeightRecordOfToday,
                        preferredUnit);
            }
        }
        else
        {
            LatestWeightOfToday = null;
        }



    }

    private static BloodPressureRecord TryGetLatestBloodPressureRecordOfToday(
            final LocalDate today,
            final IBloodPressureWidgetRepository repository,
            final ILogger logger)
    {
        try
        {
            final List<BloodPressureRecord> recordsOfToday =
                    repository.GetRecordsForDayDescending(today);

            return  recordsOfToday.size() >= 1
                    ? recordsOfToday.get(0)
                    : null;
        }
        catch (Exception exception)
        {
            logger.LogError(TAG, "Failed to read latest blood pressure record of today.", exception);
            return null;
        }
    }

    private static StepCountRecord TryGetStepCountRecordOfToday(
            final LocalDate today,
            final IStepWidgetRepository repository,
            final ILogger logger)
    {
        try
        {
            return repository.GetRecordForDay(today);
        }
        catch (IStepWidgetRepository.RecordNotFound exception)
        {
            return null;
        }
        catch (Exception exception)
        {
            logger.LogError(TAG, "Failed to read the step count record of today.", exception);
            return null;
        }
    }

    private static int TryGetDefaultStepCountGoal(
            final IStepWidgetRepository repository,
            final ILogger logger)
    {
        try
        {
            return repository.GetDefaultStepCountGoal();
        }
        catch (Exception exception)
        {
            logger.LogError(TAG, "Failed to read default step count goal.", exception);
            return 5000;
        }
    }

    private static WeightRecord TryGetLatestWeightRecordOfToday(
            final LocalDate today,
            final IWeightWidgetRepository repository,
            final ILogger logger)
    {
        try
        {
            final List<WeightRecord> recordsOfToday =
                    repository.GetRecordsForDayDescending(today);

            return  recordsOfToday.size() >= 1
                    ? recordsOfToday.get(0)
                    : null;
        }
        catch (Exception exception)
        {
            logger.LogError(TAG, "Failed to read latest weight record of today.", exception);
            return null;
        }
    }


    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        // Nothing to do here.
    }
}