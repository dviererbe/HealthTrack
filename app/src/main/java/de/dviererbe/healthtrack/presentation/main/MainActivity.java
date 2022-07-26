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
import android.view.MenuItem;
import android.view.Menu;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.ActivityMainBinding;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.presentation.main.MainViewViewModel.IMainView;
import de.dviererbe.healthtrack.presentation.main.feedback.FeedbackFragment;
import de.dviererbe.healthtrack.presentation.settings.SettingsActivity;

import java.time.LocalDate;
import java.util.UUID;

public class MainActivity
        extends AppCompatActivity
        implements IMainView, INavigationRouter
{
    private MainViewViewModel _viewModel;
    private ActivityMainBinding _binding;
    private AppBarConfiguration _appBarConfiguration;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _viewModel = HealthTrackApp
                .GetDependenciesViaActivity(this)
                .GetViewModelFactory()
                .CreateMainActivityViewModel(getLifecycle(), this, this);

        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        ConfigureNavigation();
        ConfigureMenu();
    }

    private void ConfigureNavigation()
    {
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


    }

    private void ConfigureMenu()
    {
        final Menu menu = _binding.navView.getMenu();

        final MenuItem stepsMenuItem = menu.findItem(R.id.nav_stepcounter);
        final MenuItem foodMenuItem = menu.findItem(R.id.nav_food);
        final MenuItem weightMenuItem = menu.findItem(R.id.nav_weight);
        final MenuItem bloodPressureMenuItem = menu.findItem(R.id.nav_bloodpressure);
        final MenuItem bloodSugarMenuItem = menu.findItem(R.id.nav_bloodsugar);

        _viewModel.IsStepCounterWidgetEnabled().observe(this, stepsMenuItem::setVisible);
        _viewModel.IsFoodWidgetEnabled().observe(this, foodMenuItem::setVisible);
        _viewModel.IsWeightWidgetEnabled().observe(this, weightMenuItem::setVisible);
        _viewModel.IsBloodPressureWidgetEnabled().observe(this, bloodPressureMenuItem::setVisible);
        _viewModel.IsBloodSugarWidgetEnabled().observe(this, bloodSugarMenuItem::setVisible);

        final MenuItem settingsMenuItem = menu.findItem(R.id.nav_settings);
        final MenuItem feedbackMenuItem = menu.findItem(R.id.nav_feedback);

        settingsMenuItem.setOnMenuItemClickListener(menuItem ->
        {
            _viewModel.NavigateToSettings();
            return true;
        });

        feedbackMenuItem.setOnMenuItemClickListener(menuItem ->
        {
            _viewModel.ShowFeedbackDialog();
            return true;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        return NavigationUI.navigateUp(navController, _appBarConfiguration)
               || super.onSupportNavigateUp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        _binding = null;
        _viewModel = null;
    }

    /**
     * Shows the user a UI dialog for sending feedback.
     */
    @Override
    public void ShowFeedbackDialog()
    {
        _binding.activityMain.closeDrawer(GravityCompat.START);

        FeedbackFragment fragment = FeedbackFragment.NewInstance();
        fragment.show(getSupportFragmentManager(), "Feedback");
    }

    /**
     * Tries to navigate to the user settings UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToSettings()
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

    /**
     * Tries to navigate to the create blood pressure record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateBloodPressureRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the blood pressure record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToBloodPressureRecordDetails(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit blood pressure record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditBloodPressureRecord(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the default step count goal editor user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToDefaultStepCountGoalEditor()
    {
        return false;
    }

    /**
     * Tries to navigate to the create step count record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateStepCountRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the step count record details user interface for
     * a record with a specific identifier.
     *
     * @param dateOfDay The date of the day of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToStepCountRecordDetails(LocalDate dateOfDay)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit step count record user interface for
     * a record with a specific identifier.
     *
     * @param dateOfDay The date of the day of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditStepCountRecord(LocalDate dateOfDay)
    {
        return false;
    }

    /**
     * Tries to navigate to the create weight record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateWeightRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the weight record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToWeightRecordDetails(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit weight record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditWeightRecord(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the preceding UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateBack()
    {
        return false;
    }
}