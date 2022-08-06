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

package de.dviererbe.healthtrack;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.presentation.main.MainViewViewModel;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureDetailsViewModel;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureDetailsViewModel.IBloodPressureDetailsView;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureListViewModel;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureListViewModel.IBloodPressureListView;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureMergeViewModel;
import de.dviererbe.healthtrack.presentation.main.bloodsugar.BloodSugarListViewModel;
import de.dviererbe.healthtrack.presentation.main.food.FoodListViewModel;
import de.dviererbe.healthtrack.presentation.main.home.HomeViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountDetailsViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountDetailsViewModel.IStepCountDetailsView;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountGoalDefaultEditorViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListViewModel.IStepCountListView;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountMergeViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightDetailsViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightDetailsViewModel.IWeightDetailsView;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListViewModel.IWeightListView;
import de.dviererbe.healthtrack.presentation.main.weight.WeightMergeViewModel;
import de.dviererbe.healthtrack.presentation.settings.ExportDataDialogViewModel;
import de.dviererbe.healthtrack.presentation.settings.ExportDataDialogViewModel.IExportDataDialogView;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel.ISettingsView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class ViewModelFactory
{
    private final IDependencyResolver DependencyResolver;

    public ViewModelFactory(IDependencyResolver dependencyResolver)
    {
        DependencyResolver = dependencyResolver;
    }

    public BloodPressureListViewModel CreateBloodPressureListViewModel(
            final Lifecycle viewModelLifecycle,
            final IBloodPressureListView view,
            final INavigationRouter navigationRouter)
    {
        final BloodPressureListViewModel viewModel = new BloodPressureListViewModel(
            view,
            navigationRouter,
            DependencyResolver.CreateBloodPressureWidgetRepository(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetPreferredUnitRepository(),
            DependencyResolver.GetLogger());

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public BloodPressureDetailsViewModel CreateBloodPressureDetailsViewModel(
            final Lifecycle viewModelLifecycle,
            final IBloodPressureDetailsView view,
            final INavigationRouter navigationRouter,
            final UUID recordIdentifier)
    {
        final BloodPressureDetailsViewModel viewModel = new BloodPressureDetailsViewModel(
            view,
            navigationRouter,
            DependencyResolver.CreateBloodPressureWidgetRepository(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetPreferredUnitRepository(),
            DependencyResolver.GetLogger(),
            recordIdentifier);

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public BloodPressureMergeViewModel CreateBloodPressureMergeViewModel(
            final UUID recordIdentifier)
    {
        return new BloodPressureMergeViewModel(
            DependencyResolver.CreateBloodPressureWidgetRepository(),
            DependencyResolver.GetDateTimeProvider(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetPreferredUnitRepository(),
            DependencyResolver.GetLogger(),
            recordIdentifier);
    }

    public BloodSugarListViewModel CreateBloodSugarListViewModel(final Lifecycle viewModelLifecycle)
    {
        final BloodSugarListViewModel viewModel = new BloodSugarListViewModel(DependencyResolver.GetLogger());
        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public ExportDataDialogViewModel CreateExportDataViewModel(
            final Lifecycle viewModelLifecycle,
            final IExportDataDialogView exportDataView)
    {
        final ExportDataDialogViewModel viewModel = new ExportDataDialogViewModel(exportDataView);
        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public FoodListViewModel CreateFoodListViewModel(final Lifecycle viewModelLifecycle)
    {
        final FoodListViewModel viewModel = new FoodListViewModel(DependencyResolver.GetLogger());
        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public HomeViewModel CreateHomeViewModel(
            final Lifecycle viewModelLifecycle,
            final INavigationRouter navigationRouter)
    {
        final HomeViewModel viewModel = new HomeViewModel(
            navigationRouter,
            DependencyResolver.GetDateTimeProvider(),
            DependencyResolver.CreateBloodPressureWidgetRepository(),
            DependencyResolver.CreateStepWidgetRepository(),
            DependencyResolver.CreateWeightWidgetRepository(),
            DependencyResolver.GetPreferredUnitRepository(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetWidgetConfigurationRepository(),
            DependencyResolver.GetLogger());

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public SettingsViewModel CreatSettingsViewModel(
            final Lifecycle viewModelLifecycle,
            final ISettingsView view)
    {
        final SettingsViewModel viewModel = new SettingsViewModel(
            view,
            (params) -> DependencyResolver.CreateExportUserDataAsJsonOperation(params.Options, params.UserDataJsonFileOutputStreamProvider),
            (params) -> DependencyResolver.CreateDeleteAllUserDataOperation(),
            DependencyResolver.GetLogger());

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public StepCountGoalDefaultEditorViewModel CreateStepCountGoalDefaultEditorViewModel()
    {
        return new StepCountGoalDefaultEditorViewModel(
            DependencyResolver.CreateStepWidgetRepository(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger());
    }

    public StepCountListViewModel CreateStepCountListViewModel(
            final Lifecycle viewModelLifecycle,
            final IStepCountListView view,
            final INavigationRouter navigationRouter)
    {
        final StepCountListViewModel viewModel = new StepCountListViewModel(
            view,
            navigationRouter,
            DependencyResolver.CreateStepWidgetRepository(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger());

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public StepCountDetailsViewModel CreateStepCountDetailsViewModel(
            final Lifecycle viewModelLifecycle,
            final IStepCountDetailsView view,
            final INavigationRouter navigationRouter,
            final LocalDate day)
    {
        final StepCountDetailsViewModel viewModel = new StepCountDetailsViewModel(
            view,
            navigationRouter,
            DependencyResolver.CreateStepWidgetRepository(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger(),
            day);

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public StepCountMergeViewModel CreateStepCountMergeViewModel(final LocalDate day)
    {
        return new StepCountMergeViewModel(
            DependencyResolver.CreateStepWidgetRepository(),
            DependencyResolver.GetDateTimeProvider(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger(),
            day);
    }

    public WeightListViewModel CreateWeightListViewModel(
            final Lifecycle viewModelLifecycle,
            final IWeightListView view,
            final INavigationRouter navigationRouter)
    {
        final WeightListViewModel viewModel = new WeightListViewModel(
                view,
                navigationRouter,
                DependencyResolver.CreateWeightWidgetRepository(),
                DependencyResolver.GetDateTimeConverter(),
                DependencyResolver.GetNumericValueConverter(),
                DependencyResolver.GetPreferredUnitRepository(),
                DependencyResolver.GetLogger());

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public WeightDetailsViewModel CreateWeightDetailsViewModel(
            final Lifecycle viewModelLifecycle,
            final IWeightDetailsView view,
            final INavigationRouter navigationRouter,
            final UUID recordIdentifier)
    {
        final WeightDetailsViewModel viewModel = new WeightDetailsViewModel(
                view,
                navigationRouter,
                DependencyResolver.CreateWeightWidgetRepository(),
                DependencyResolver.GetDateTimeConverter(),
                DependencyResolver.GetNumericValueConverter(),
                DependencyResolver.GetLogger(),
                recordIdentifier);

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public WeightMergeViewModel CreateWeightMergeViewModel(
        final UUID recordIdentifier)
    {
        return new WeightMergeViewModel(
            DependencyResolver.CreateWeightWidgetRepository(),
            DependencyResolver.GetPreferredUnitRepository(),
            DependencyResolver.GetDateTimeProvider(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger(),
            recordIdentifier);
    }

    public MainViewViewModel CreateMainActivityViewModel()
    {
        return new MainViewViewModel(DependencyResolver.GetWidgetConfigurationRepository());
    }

    private static void DisposeViewModelWhenLifecycleEnds(
        final IDisposable viewModel,
        final Lifecycle viewModelLifecycle)
    {
        new ViewModelDisposer(viewModel, viewModelLifecycle);
    }

    /**
     * Disposes a view model when
     */
    private static class ViewModelDisposer implements LifecycleEventObserver
    {
        final IDisposable ViewModel;
        final Lifecycle ViewModelLifecycle;

        public ViewModelDisposer(
            final IDisposable viewModel,
            final Lifecycle viewModelLifecycle)
        {
            ViewModel = viewModel;
            ViewModelLifecycle = viewModelLifecycle;
            ViewModelLifecycle.addObserver(this);
        }

        /**
         * Called when a state transition event happens.
         *
         * @param source The source of the event
         * @param event  The event
         */
        @Override
        public void onStateChanged(
            @NonNull @NotNull
            final LifecycleOwner source,
            @NonNull @NotNull
            final Lifecycle.Event event)
        {
            if (event == Lifecycle.Event.ON_DESTROY)
            {
                ViewModelLifecycle.removeObserver(this);
                ViewModel.Dispose();
            }
        }
    }
}
