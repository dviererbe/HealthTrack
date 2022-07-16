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

import android.app.Activity;
import android.app.Application;

public class HealthTrackApp extends Application
{
    private IDependencyResolver _dependencyResolver;
    private ServiceContainer _serviceContainer;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _dependencyResolver = InitializeDependencyResolver();
        _serviceContainer = SetupServices();
    }

    private IDependencyResolver InitializeDependencyResolver()
    {
        return new ApplicationContextDependencyResolver(getApplicationContext());
    }

    private ServiceContainer SetupServices()
    {
        return new ServiceContainer(_dependencyResolver);

    }

    /**
     *
     * @return
     */
    public IDependencyResolver GetDependencies()
    {
        return _dependencyResolver;
    }

    /**
     *
     * @param activity
     * @return
     */
    public static IDependencyResolver GetDependenciesViaActivity(Activity activity)
    {
        return ((HealthTrackApp)activity.getApplication()).GetDependencies();
    }
}
