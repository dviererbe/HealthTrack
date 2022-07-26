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

import android.content.Context;
import de.dviererbe.healthtrack.application.DeleteAllUserDataOperation;
import de.dviererbe.healthtrack.application.ExportUserDataAsJsonOperation;
import de.dviererbe.healthtrack.infrastructure.*;
import de.dviererbe.healthtrack.persistence.*;
import de.dviererbe.healthtrack.presentation.AndroidUIThemeSetter;
import de.dviererbe.healthtrack.presentation.IUIThemeSetter;

/**
 * Resolves the dependencies using the application context.
 */
public class ApplicationContextDependencyResolver implements IDependencyResolver
{
    private final AndroidUtilsLogger _logger;
    private final SystemDateTimeProvider _dateTimeProvider;
    private final ApplicationContextLocaleAwareValueConverter _valueConverter;
    private final ApplicationContextLocaleAwareDateTimeConverter _dateTimeConverter;
    private final ViewModelFactory _viewModelFactory;
    private final AndroidUIThemeSetter _androidUIThemeSetter;
    private final SharedPreferenceRepository _sharedPreferenceRepository;

    // User Data Repositories
    private final WeightWidgetSQLiteRepository _weightWidgetRepository;
    private final FoodWidgetSQLiteRepository _foodWidgetRepository;
    private final StepWidgetSQLiteRepository _stepsWidgetRepository;
    private final BloodPressureWidgetSQLiteRepository _bloodPressureWidgetRepository;
    private final BloodSugarWidgetSQLiteRepository _bloodSugarWidgetRepository;

    /**
     * Creates a new {@link ApplicationContextDependencyResolver} instance from a given application {@link Context}.
     *
     * @param applicationContext an application {@link Context} instance used to create the dependencies.
     */
    public ApplicationContextDependencyResolver(Context applicationContext)
    {
        _logger = new AndroidUtilsLogger();
        _dateTimeProvider = new SystemDateTimeProvider();
        _dateTimeConverter = new ApplicationContextLocaleAwareDateTimeConverter(applicationContext);
        _valueConverter = new ApplicationContextLocaleAwareValueConverter(applicationContext);
        _viewModelFactory = new ViewModelFactory(this);
        _androidUIThemeSetter = new AndroidUIThemeSetter();
        _sharedPreferenceRepository = SharedPreferenceRepository.FromContext(applicationContext);

        _foodWidgetRepository = null; //new FoodWidgetSQLiteRepository(applicationContext);
        _weightWidgetRepository = new WeightWidgetSQLiteRepository(applicationContext);
        _stepsWidgetRepository = new StepWidgetSQLiteRepository(applicationContext);
        _bloodPressureWidgetRepository = new BloodPressureWidgetSQLiteRepository(applicationContext);
        _bloodSugarWidgetRepository = null; //new BloodSugarWidgetSQLiteRepository(applicationContext);
    }

    /**
     * Resolves an {@link ILogger} implementation.
     *
     * @return {@link ILogger} implementation
     */
    @Override
    public ILogger GetLogger()
    {
        return _logger;
    }

    /**
     * Resolves an {@link IDateTimeProvider} implementation.
     *
     * @return {@link IDateTimeProvider} implementation
     */
    @Override
    public IDateTimeProvider GetDateTimeProvider()
    {
        return _dateTimeProvider;
    }

    /**
     * Resolves an {@link IDateTimeConverter} implementation.
     *
     * @return {@link IDateTimeConverter} implementation
     */
    @Override
    public IDateTimeConverter GetDateTimeConverter()
    {
        return _dateTimeConverter;
    }

    /**
     * Resolves an {@link INumericValueConverter} implementation.
     *
     * @return {@link INumericValueConverter} implementation
     */
    @Override
    public INumericValueConverter GetNumericValueConverter()
    {
        return _valueConverter;
    }

    /**
     * Initializes an {@link ExportUserDataAsJsonOperation} instance.
     *
     * @param options                              Specifies which data should be exported.
     * @param userDataJsonFileOutputStreamProvider Mechanism for opening a json file stream.
     * @return Initialized {@link ExportUserDataAsJsonOperation} instance.
     */
    @Override
    public ExportUserDataAsJsonOperation CreateExportUserDataAsJsonOperation(
            ExportUserDataAsJsonOperation.Options options,
            IUserDataJsonFileOutputStreamProvider userDataJsonFileOutputStreamProvider)
    {
        return new ExportUserDataAsJsonOperation(
            options,
            userDataJsonFileOutputStreamProvider,
            CreateBloodPressureWidgetRepository(),
            CreateBloodSugarWidgetRepository(),
            CreateFoodWidgetRepository(),
            CreateStepWidgetRepository(),
            CreateWeightWidgetRepository(),
            GetDateTimeProvider(),
            GetLogger());
    }


    /**
     * Initializes an {@link DeleteAllUserDataOperation} implementation.
     *
     * @return {@link DeleteAllUserDataOperation} implementation
     */
    @Override
    public DeleteAllUserDataOperation CreateDeleteAllUserDataOperation()
    {
        return new DeleteAllUserDataOperation(
            CreateStepWidgetRepository(),
            CreateWeightWidgetRepository(),
            CreateFoodWidgetRepository(),
            CreateBloodPressureWidgetRepository(),
            CreateBloodSugarWidgetRepository(),
            GetLogger());
    }

    /**
     * Resolves an {@link IPreferredThemeRepository} implementation as a singleton instance.
     *
     * @return {@link IPreferredThemeRepository} implementation
     */
    @Override
    public IPreferredThemeRepository GetPreferredThemeRepository()
    {
        return _sharedPreferenceRepository;
    }

    /**
     * Resolves an {@link IPreferredUnitRepository} implementation.
     *
     * @return {@link IPreferredUnitRepository} implementation
     */
    @Override
    public IPreferredUnitRepository GetPreferredUnitRepository()
    {
        return _sharedPreferenceRepository;
    }

    /**
     * Resolves an {@link IWidgetConfigurationRepository} implementation as a singleton instance.
     *
     * @return {@link IWidgetConfigurationRepository} implementation
     */
    @Override
    public IWidgetConfigurationRepository GetWidgetConfigurationRepository()
    {
        return _sharedPreferenceRepository;
    }

    /**
     * Returns the internally used {@link SharedPreferenceRepository} singleton instance.
     *
     * @return {@link SharedPreferenceRepository} singleton instance.
     */
    public SharedPreferenceRepository GetSharedPreferenceRepository()
    {
        return _sharedPreferenceRepository;
    }

    /**
     * Initializes an {@link IStepWidgetRepository} implementation.
     *
     * @return {@link IStepWidgetRepository} implementation
     */
    @Override
    public IStepWidgetRepository CreateStepWidgetRepository()
    {
        return _stepsWidgetRepository;
    }

    /**
     * Initializes an {@link } implementation.
     *
     * @return {@link } implementation
     */
    @Override
    public IFoodWidgetRepository CreateFoodWidgetRepository()
    {
        return _foodWidgetRepository;
    }

    /**
     * Initializes an {@link IWeightWidgetRepository} implementation.
     *
     * @return {@link IWeightWidgetRepository} implementation
     */
    @Override
    public IWeightWidgetRepository CreateWeightWidgetRepository()
    {
        return _weightWidgetRepository;
    }

    /**
     * Initializes an {@link IBloodPressureWidgetRepository} implementation.
     *
     * @return {@link IBloodPressureWidgetRepository} implementation
     */
    @Override
    public IBloodPressureWidgetRepository CreateBloodPressureWidgetRepository()
    {
        return _bloodPressureWidgetRepository;
    }

    /**
     * Initializes an {@link IBloodSugarWidgetRepository} implementation.
     *
     * @return {@link IBloodSugarWidgetRepository} implementation
     */
    @Override
    public IBloodSugarWidgetRepository CreateBloodSugarWidgetRepository()
    {
        return _bloodSugarWidgetRepository;
    }

    /**
     * Resolves an {@link IUIThemeSetter} implementation.
     *
     * @return {@link IUIThemeSetter} implementation
     */
    @Override
    public IUIThemeSetter GetUIThemeSetter()
    {
        return _androidUIThemeSetter;
    }

    /**
     * Resolves an {@link ViewModelFactory} instance.
     *
     * @return {@link ViewModelFactory} instance
     */
    @Override
    public ViewModelFactory GetViewModelFactory()
    {
        return _viewModelFactory;
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        _stepsWidgetRepository.Dispose();
        _weightWidgetRepository.Dispose();
        _foodWidgetRepository.Dispose();
        _bloodPressureWidgetRepository.Dispose();
        _bloodSugarWidgetRepository.Dispose();
    }
}
