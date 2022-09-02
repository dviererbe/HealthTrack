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
import de.dviererbe.healthtrack.persistence.IDefaultStepCountGoalGetter;
import de.dviererbe.healthtrack.persistence.IPerDayBulkQueryable;
import de.dviererbe.healthtrack.persistence.repositories.*;
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

    public final Runnable StepCountContextCommand;

    public HomeViewModel(
            final INavigationRouter navigationRouter,
            final IDateTimeProvider dateTimeProvider,
            final IPerDayBulkQueryable<BloodPressureRecord> bloodPressureRecordReader,
            final IPerDayBulkQueryable<WeightRecord> weightRecordReader,
            final IPerDayBulkQueryable<StepCountRecord> stepCountRecordReader,
            final IDefaultStepCountGoalGetter defaultStepCountGoalGetter,
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
                = TryGetLatestRecordOfToday(today, bloodPressureRecordReader, logger);

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
                = TryGetLatestRecordOfToday(today, stepCountRecordReader, logger);

            if (stepCountRecordOfToday == null)
            {
                final int defaultStepCountGoal = TryGetDefaultStepCountGoal(defaultStepCountGoalGetter, logger);

                StepCountOfToday =
                    new StepCountListItemViewModel(
                        numericValueConverter,
                        defaultStepCountGoal);

                StepCountContextCommand = navigationRouter::TryNavigateToCreateStepCountRecord;
            }
            else
            {
                StepCountOfToday =
                    new StepCountListItemViewModel(
                        dateTimeConverter,
                        numericValueConverter,
                        stepCountRecordOfToday);

                StepCountContextCommand = () -> navigationRouter.TryNavigateToStepCountRecordDetails(StepCountOfToday.Identifier);
            }
        }
        else
        {
            StepCountOfToday = null;
            StepCountContextCommand = () -> {};
        }

        if (widgetConfigurationRepository.IsWeightWidgetEnabled())
        {
            final WeightUnit preferredUnit = preferredUnitRepository
                .GetPreferredMassUnit()
                .ToDomainWeightUnit();

            final WeightRecord latestWeightRecordOfToday
                = TryGetLatestRecordOfToday(today, weightRecordReader, logger);

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

    private static <TRecord> TRecord TryGetLatestRecordOfToday(
            final LocalDate today,
            final IPerDayBulkQueryable<TRecord> repository,
            final ILogger logger)
    {
        try
        {
            final List<TRecord> recordsOfToday =
                    repository.GetRecordsForDayDescending(today);

            return  recordsOfToday.size() >= 1
                    ? recordsOfToday.get(0)
                    : null;
        }
        catch (Exception exception)
        {
            logger.LogError(TAG, "Failed to read the step count record of today.", exception);
            return null;
        }
    }

    private static int TryGetDefaultStepCountGoal(
            final IDefaultStepCountGoalGetter defaultStepCountGoalGetter,
            final ILogger logger)
    {
        try
        {
            return defaultStepCountGoalGetter.GetDefaultStepCountGoal();
        }
        catch (Exception exception)
        {
            logger.LogError(TAG, "Failed to read default step count goal.", exception);
            return 5000;
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