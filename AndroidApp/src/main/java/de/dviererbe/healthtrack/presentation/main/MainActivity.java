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

package de.dviererbe.healthtrack.presentation.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.ActivityMainBinding;
import de.dviererbe.healthtrack.presentation.main.feedback.FeedbackFragment;
import de.dviererbe.healthtrack.presentation.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding _binding;

    private AppBarConfiguration _appBarConfiguration;

    private MainViewViewModel _viewModel;

    private IDisposable _viewModelEventListener;

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * {@link #setContentView(int)} to inflate the activity's UI, using {@link #findViewById(int)} to
     * programmatically interact with widgets in the UI, calling
     * {@link #managedQuery(android.net.Uri, String[], String, String[], String)} to retrieve cursors
     * for data being displayed, etc.
     *
     * You can call {@link #finish()} from within this function, in which case {@link #onDestroy()} will be
     * immediately called after {@link #onCreate(Bundle)} without any of the rest of the activity lifecycle
     * ({@link #onStart()}, {@link #onResume()}, {@link #onPause()}, etc) executing.
     *
     * <p><i>Derived classes must call through to the super class's implementation of this method.
     * If they do not, an exception will be thrown.</i></p>
     *
     * <p>This method must be called from the main thread of your app. If you override this method you must call
     * through to the superclass implementation.</p>
     *
     * @param savedInstanceState
     *      If the activity is being re-initialized after previously being shut down then this Bundle contains
     *      the data it most recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *      <b>Note: Otherwise it is null.</b>
     */
    @Override
    protected void onCreate(
        @Nullable @org.jetbrains.annotations.Nullable
        final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _viewModel = HealthTrackApp
            .GetDependenciesViaActivity(this)
            .GetViewModelFactory()
            .CreateMainActivityViewModel();

        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        setSupportActionBar(_binding.appBarMain.toolbar);

        final DrawerLayout drawer = _binding.activityMain;
        final NavigationView navigationView = _binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        _appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_home,
            R.id.nav_food,
            R.id.nav_weight,
            R.id.nav_stepcounter,
            R.id.nav_bloodpressure,
            R.id.nav_bloodsugar,
            R.id.nav_about)
            .setOpenableLayout(drawer)
            .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, _appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final Menu menu = _binding.navView.getMenu();
        menu.findItem(R.id.nav_settings).setOnMenuItemClickListener(menuItem -> TryNavigateToSettings());
        menu.findItem(R.id.nav_feedback).setOnMenuItemClickListener(menuItem -> TryShowFeedbackDialog());
    }

    /**
     * Dispatch {@link #onResume()} to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        final Menu menu = _binding.navView.getMenu();

        final MenuItem stepsMenuItem = menu.findItem(R.id.nav_stepcounter);
        final MenuItem foodMenuItem = menu.findItem(R.id.nav_food);
        final MenuItem weightMenuItem = menu.findItem(R.id.nav_weight);
        final MenuItem bloodPressureMenuItem = menu.findItem(R.id.nav_bloodpressure);
        final MenuItem bloodSugarMenuItem = menu.findItem(R.id.nav_bloodsugar);

        stepsMenuItem.setVisible(_viewModel.IsStepCounterWidgetEnabled());
        foodMenuItem.setVisible(_viewModel.IsFoodWidgetEnabled());
        weightMenuItem.setVisible(_viewModel.IsWeightWidgetEnabled());
        bloodPressureMenuItem.setVisible(_viewModel.IsBloodPressureWidgetEnabled());
        bloodSugarMenuItem.setVisible(_viewModel.IsBloodSugarWidgetEnabled());

        _viewModelEventListener = _viewModel.RegisterEventHandler(new ViewModelEventListener());
    }

    /**
     * Dispatch {@link #onPause()} to fragments.
     */
    @Override
    protected void onPause()
    {
        _viewModelEventListener.Dispose();
        _viewModelEventListener = null;

        super.onPause();
    }

    /**
     * <p>Perform any final cleanup before an activity is destroyed. This can happen either because the activity
     * is finishing (someone called {@link #finish()} on it), or because the system is temporarily destroying
     * this instance of the activity to save space. You can distinguish between these two scenarios with the
     * {@link #isFinishing()} method.</p>
     *
     * <p>Note: do not count on this method being called as a place for saving data! For example, if an activity
     * is editing data in a content provider, those edits should be committed in either {@link #onPause()} or
     * {@link #onSaveInstanceState(Bundle)}, not here. This method is usually implemented to free resources like
     * threads that are associated with an activity, so that a destroyed activity does not leave such things
     * around while the rest of its application is still running.
     * There are situations where the system will simply kill the activity's hosting process without calling
     * this method (or any others) in it, so it should not be used to do things that are intended to remain
     * around after the process goes away.</p>
     *
     * <p>Derived classes must call through to the super class's implementation of this method.
     * If they do not, an exception will be thrown.</p>
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        _viewModel.Dispose();
        _viewModel = null;
        _binding = null;
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(TaskStackBuilder)}
     * to supply those arguments.</p>
     *
     * <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and
     * Back Stack</a> from the developer guide and
     * <a href="{@docRoot}design/patterns/navigation.html">Navigation</a> from the design guide
     * for more information about navigating within your app.</p>
     *
     * <p>See the {@link TaskStackBuilder} class and the Activity methods
     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(Intent)}, and
     * {@link #supportNavigateUpTo(Intent)} for help implementing custom Up navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        return NavigationUI.navigateUp(navController, _appBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * Shows the user a UI dialog for sending feedback.
     */
    private boolean TryShowFeedbackDialog()
    {
        try
        {
            _binding.activityMain.closeDrawer(GravityCompat.START);

            FeedbackFragment fragment = FeedbackFragment.NewInstance();
            fragment.show(getSupportFragmentManager(), "Feedback");

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    /**
     * Tries to navigate the user to the settings UI (User Interface).
     */
    private boolean TryNavigateToSettings()
    {
        try
        {
            Intent gotoSettingsIntent = new Intent(
                    getApplicationContext(),
                    SettingsActivity.class);

            startActivity(gotoSettingsIntent);

            return true;
        }
        catch (Exception exception)
        {
            return false;
        }
    }

    private class ViewModelEventListener implements MainViewViewModel.IMainViewViewModelEventHandler
    {
        @Override
        public void IsStepCounterWidgetEnabledChanged()
        {
            final Menu menu = _binding.navView.getMenu();
            final MenuItem stepsMenuItem = menu.findItem(R.id.nav_stepcounter);

            stepsMenuItem.setVisible(_viewModel.IsStepCounterWidgetEnabled());
        }

        @Override
        public void IsWeightWidgetEnabledChanged()
        {
            final Menu menu = _binding.navView.getMenu();
            final MenuItem weightMenuItem = menu.findItem(R.id.nav_weight);

            weightMenuItem.setVisible(_viewModel.IsWeightWidgetEnabled());
        }

        @Override
        public void IsFoodWidgetEnabledChanged()
        {
            final Menu menu = _binding.navView.getMenu();
            final MenuItem foodMenuItem = menu.findItem(R.id.nav_food);

            foodMenuItem.setVisible(_viewModel.IsFoodWidgetEnabled());
        }

        @Override
        public void IsBloodPressureWidgetEnabledChanged()
        {
            final Menu menu = _binding.navView.getMenu();
            final MenuItem bloodPressureMenuItem = menu.findItem(R.id.nav_bloodpressure);

            bloodPressureMenuItem.setVisible(_viewModel.IsBloodPressureWidgetEnabled());
        }

        @Override
        public void IsBloodSugarWidgetEnabledChanged()
        {
            final Menu menu = _binding.navView.getMenu();
            final MenuItem bloodSugarMenuItem = menu.findItem(R.id.nav_bloodsugar);

            bloodSugarMenuItem.setVisible(_viewModel.IsBloodSugarWidgetEnabled());
        }
    }
}