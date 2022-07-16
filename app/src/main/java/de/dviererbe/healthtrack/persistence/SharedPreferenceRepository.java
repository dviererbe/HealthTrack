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

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses the application shared preferences as a storage mechanism.
 */
public class SharedPreferenceRepository implements
        IPreferredThemeRepository,
        IPreferredUnitRepository,
        IWidgetConfigurationRepository
{
    private static final String ThemePreferenceKey = "theme";
    private static final String IsStepCounterWidgetEnabledPreferenceKey = "widgets_stepcounter_enabled";
    private static final String IsFoodWidgetEnabledPreferenceKey = "widgets_food_enabled";
    private static final String IsWeightWidgetEnabledPreferenceKey = "widgets_weight_enabled";
    private static final String IsBloodPressureWidgetEnabledPreferenceKey = "widgets_bloodpressure_enabled";
    private static final String IsBloodSugarWidgetEnabledPreferenceKey = "widgets_bloodsugar_enabled";

    private final SharedPreferences Preferences;

    private final SharedPreferences.OnSharedPreferenceChangeListener ChangeListener;

    private final List<OnPreferredThemeChangedListener> OnPreferredThemeChangedListeners = new ArrayList<>();
    private final List<OnWidgetConfigurationChangedListener> OnWidgetConfigurationChangedListeners = new ArrayList<>();

    /**
     * Instantiates a new SharedPreferenceRepository object.
     *
     * @param preferences SharedPreferences to read/write values from/to.
     */
    public SharedPreferenceRepository(SharedPreferences preferences)
    {
        Preferences = preferences;
        ChangeListener = new OnSharedPreferenceChangeListener();
    }

    /**
     * Creates a new instance from a given application context.
     *
     * @param context Current application context to get the shared preference from.
     * @return The instantiated repository based on the application context.
     */
    public static SharedPreferenceRepository FromContext(Context context)
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new SharedPreferenceRepository(preferences);
    }

    /**
     * Reads the theme the user prefers for this application.
     *
     * @return App specific theme the user prefers.
     */
    @Override
    public PreferredTheme GetPreferredTheme()
    {
        final String preferredTheme = GetString(ThemePreferenceKey, "system");

        switch (preferredTheme)
        {
            case "light":
                return PreferredTheme.Light;
            case "dark":
                return PreferredTheme.Dark;
            case "system":
            default:
                return PreferredTheme.FollowSystem;
        }
    }

    /**
     * Registers a listener instance that gets notified when the
     * user preferred theme for this application changed.
     *
     * @param listener The listener instance that gets notified.
     */
    @Override
    public void RegisterOnPreferredThemeChangedListener(OnPreferredThemeChangedListener listener)
    {
        if (listener == null) return;
        OnPreferredThemeChangedListeners.add(listener);
    }

    /**
     * Removes a listener instance from the notification list when
     * the user preferred theme for this application changed.
     *
     * @param listener The listener instance that gets removed from the notification list.
     */
    @Override
    public void UnregisterOnPreferredThemeChangedListener(OnPreferredThemeChangedListener listener)
    {
        if (listener == null) return;
        OnPreferredThemeChangedListeners.remove(listener);
    }

    /**
     * Gets the user preferred unit for measuring energy.
     *
     * @return User preferred unit for measuring energy.
     */
    @Override
    public EnergyUnit GetPreferredEnergyUnit()
    {
        final String preferredEnergyUnit = GetString("unit_energy", "Kilojoule");

        switch (preferredEnergyUnit)
        {
            case "Kilocalories":
                return EnergyUnit.Kilocalories;
            case "Kilojoule":
            default:
                return EnergyUnit.Kilocalories;
        }
    }

    /**
     * Gets the user preferred unit for measuring mass.
     *
     * @return User preferred unit for measuring mass.
     */
    @Override
    public MassUnit GetPreferredMassUnit()
    {
        final String preferredMassUnit = GetString("unit_mass", "Kilogram");

        switch (preferredMassUnit)
        {
            case "Pound":
                return MassUnit.Pound;
            case "Kilogram":
            default:
                return MassUnit.Kilogram;
        }
    }

    /**
     * Gets the user preferred unit for measuring blood pressure.
     *
     * @return User preferred unit for measuring blood pressure.
     */
    @Override
    public BloodPressureUnit GetPreferredBloodPressureUnit()
    {
        final String preferredBloodPressureUnit = GetString("unit_bloodpressure", "Kilopascal");

        switch (preferredBloodPressureUnit)
        {
            case "MillimetreOfMercury":
                return BloodPressureUnit.MillimetreOfMercury;
            case "Kilopascal":
            default:
                return BloodPressureUnit.Kilopascal;
        }
    }

    /**
     * Gets the user preferred unit for measuring blood sugar.
     *
     * @return User preferred unit for measuring blood sugar.
     */
    @Override
    public BloodSugarUnit GetPreferredBloodSugarUnit()
    {
        final String preferredBloodSugarUnit = GetString("unit_bloodsugar", "MilligramPerDecilitre");

        switch (preferredBloodSugarUnit)
        {
            case "MillimolPerLitre":
                return BloodSugarUnit.MillimolPerLitre;
            case "MilligramPerDecilitre":
            default:
                return BloodSugarUnit.MilligramPerDecilitre;
        }
    }

    /**
     * Gets if the step counter widget is enabled.
     *
     * @return true if the step counter widget is enabled; otherwise false.
     */
    @Override
    public boolean IsStepCounterWidgetEnabled()
    {
        return GetBoolean(IsStepCounterWidgetEnabledPreferenceKey, true);
    }

    /**
     * Gets if the weight widget is enabled.
     *
     * @return true if the weight widget is enabled; otherwise false.
     */
    @Override
    public boolean IsWeightWidgetEnabled()
    {
        return GetBoolean(IsWeightWidgetEnabledPreferenceKey, true);
    }

    /**
     * Gets if the food widget is enabled.
     *
     * @return true if the food widget is enabled; otherwise false.
     */
    @Override
    public boolean IsFoodWidgetEnabled()
    {
        return GetBoolean(IsFoodWidgetEnabledPreferenceKey, false);
    }

    /**
     * Gets if the blood pressure widget is enabled.
     *
     * @return true if the blood pressure widget is enabled; otherwise false.
     */
    @Override
    public boolean IsBloodPressureWidgetEnabled()
    {
        return GetBoolean(IsBloodPressureWidgetEnabledPreferenceKey, true);
    }

    /**
     * Gets if the blood sugar widget is enabled.
     *
     * @return true if the blood sugar widget is enabled; otherwise false.
     */
    @Override
    public boolean IsBloodSugarWidgetEnabled()
    {
        return GetBoolean(IsBloodSugarWidgetEnabledPreferenceKey, true);
    }

    /**
     * Registers a listener instance that gets notified when the
     * widget configuration changed.
     *
     * @param listener The listener instance that gets notified.
     */
    @Override
    public void RegisterOnWidgetConfigurationChangedListener(OnWidgetConfigurationChangedListener listener)
    {
        if (listener == null) return;
        OnWidgetConfigurationChangedListeners.add(listener);
    }

    /**
     * Removes a listener instance from the notification list when
     * the widget configuration changed.
     *
     * @param listener The listener instance that gets removed from the notification list.
     */
    @Override
    public void UnregisterOnWidgetConfigurationChangedListener(OnWidgetConfigurationChangedListener listener)
    {
        if (listener == null) return;
        OnWidgetConfigurationChangedListeners.remove(listener);
    }

    private boolean GetBoolean(String key, boolean defaultValue)
    {
        return Preferences.getBoolean(key, defaultValue);
    }

    private String GetString(String key, String defaultValue)
    {
        return Preferences.getString(key, defaultValue);
    }

    private void OnPreferredThemeChanged()
    {
        PreferredTheme preferredTheme = GetPreferredTheme();

        for (OnPreferredThemeChangedListener listener : OnPreferredThemeChangedListeners)
        {
            listener.OnPreferredThemeChanged(preferredTheme);
        }
    }

    private void OnWidgetConfigurationChanged()
    {
        for (OnWidgetConfigurationChangedListener listener : OnWidgetConfigurationChangedListeners)
        {
            listener.OnWidgetConfigurationChanged(this);
        }
    }

    public void RegisterOnSharedPreferenceChangeListener()
    {
        Preferences.registerOnSharedPreferenceChangeListener(ChangeListener);
    }

    public void UnregisterOnSharedPreferenceChangeListener()
    {
        Preferences.unregisterOnSharedPreferenceChangeListener(ChangeListener);
    }

    private class OnSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences,
                String changedPreferenceKey)
        {
            if (sharedPreferences == Preferences)
            {
                switch (changedPreferenceKey)
                {
                    case ThemePreferenceKey:
                        OnPreferredThemeChanged();
                        return;
                    case IsStepCounterWidgetEnabledPreferenceKey:
                    case IsFoodWidgetEnabledPreferenceKey:
                    case IsWeightWidgetEnabledPreferenceKey:
                    case IsBloodPressureWidgetEnabledPreferenceKey:
                    case IsBloodSugarWidgetEnabledPreferenceKey:
                        OnWidgetConfigurationChanged();
                        return;
                }
            }
        }
    }
}
