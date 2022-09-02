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
import de.dviererbe.healthtrack.infrastructure.IUserDataJsonTextWriterProvider;
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
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountGoalDefaultEditorViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountListViewModel;
import de.dviererbe.healthtrack.presentation.main.stepcount.StepCountMergeViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightDetailsViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightDetailsViewModel.IWeightDetailsView;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListViewModel;
import de.dviererbe.healthtrack.presentation.main.weight.WeightListViewModel.IWeightListView;
import de.dviererbe.healthtrack.presentation.main.weight.WeightMergeViewModel;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ViewModelFactory
{
    private final ApplicationContextDependencyResolver DependencyResolver;

    public ViewModelFactory(ApplicationContextDependencyResolver dependencyResolver)
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
            DependencyResolver._bloodPressureWidgetRepository,
            DependencyResolver._bloodPressureWidgetRepository,
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
            DependencyResolver._bloodPressureWidgetRepository,
            DependencyResolver._bloodPressureWidgetRepository,
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
            DependencyResolver._bloodPressureWidgetRepository,
            DependencyResolver._bloodPressureWidgetRepository,
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
            DependencyResolver._bloodPressureWidgetRepository,
            DependencyResolver._weightWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver.GetPreferredUnitRepository(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetWidgetConfigurationRepository(),
            DependencyResolver.GetLogger());

        DisposeViewModelWhenLifecycleEnds(viewModel, viewModelLifecycle);

        return viewModel;
    }

    public SettingsViewModel CreatSettingsViewModel(
        final IUserDataJsonTextWriterProvider userDataJsonTextWriterProvider)
    {
        return new SettingsViewModel(
            (options) -> DependencyResolver.CreateExportUserDataAsJsonOperation(options, userDataJsonTextWriterProvider),
            (params) -> DependencyResolver.CreateDeleteAllUserDataOperation(),
            DependencyResolver.GetLogger());
    }

    public StepCountGoalDefaultEditorViewModel CreateStepCountGoalDefaultEditorViewModel()
    {
        return new StepCountGoalDefaultEditorViewModel(
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger());
    }

    public StepCountListViewModel CreateStepCountListViewModel()
    {
        return new StepCountListViewModel(
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger());
    }

    public StepCountDetailsViewModel CreateStepCountDetailsViewModel(final UUID identifier)
    {
        return new StepCountDetailsViewModel(
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger(),
            identifier);
    }

    public StepCountMergeViewModel CreateStepCountMergeViewModel(final UUID identifier)
    {
        return new StepCountMergeViewModel(
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver._stepsWidgetRepository,
            DependencyResolver.GetDateTimeProvider(),
            DependencyResolver.GetDateTimeConverter(),
            DependencyResolver.GetNumericValueConverter(),
            DependencyResolver.GetLogger(),
            identifier);
    }

    public WeightListViewModel CreateWeightListViewModel(
            final Lifecycle viewModelLifecycle,
            final IWeightListView view,
            final INavigationRouter navigationRouter)
    {
        final WeightListViewModel viewModel = new WeightListViewModel(
                view,
                navigationRouter,
                DependencyResolver._weightWidgetRepository,
                DependencyResolver._weightWidgetRepository,
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
                DependencyResolver._weightWidgetRepository,
                DependencyResolver._weightWidgetRepository,
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
            DependencyResolver._weightWidgetRepository,
            DependencyResolver._weightWidgetRepository,
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
