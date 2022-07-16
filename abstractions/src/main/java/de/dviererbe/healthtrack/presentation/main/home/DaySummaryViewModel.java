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

import android.util.Log;

import java.util.Date;

public class DaySummaryViewModel
{
    public final Date DateOfSummarizedDay;

    public final int StepsAchieved;

    public final int StepsGoal;

    public final int StepsGoalReachedPercentage;

    public final boolean StepsGoalReached;

    public DaySummaryViewModel(
            Date dateOfSummarizedDay,
            int stepsAchieved,
            int stepsGoal)
    {
        DateOfSummarizedDay = dateOfSummarizedDay;
        StepsAchieved = stepsAchieved;
        StepsGoal = stepsGoal;
        StepsGoalReachedPercentage = CalculatePercentage(stepsAchieved, stepsGoal);
        StepsGoalReached = StepsGoalReachedPercentage >= 100;
    }

    private static int CalculatePercentage(final int value, final int goal)
    {
        if (value >= goal) return 100;
        if (value <= 0) return 0;

        int percentage = Math.floorDiv(value * 100, goal);
        return percentage > 0 ? percentage : 1;
    }

    public void OpenStepsDetails()
    {
        Log.d(getClass().getSimpleName(), "Go to steps.");
    }

    public void OpenWeightDetails()
    {
        Log.d(getClass().getSimpleName(), "Go to weight.");
    }

    public void OpenFoodDetails()
    {
        Log.d(getClass().getSimpleName(), "Go to food.");
    }

    public void OpenBloodPressureDetails()
    {
        Log.d(getClass().getSimpleName(), "Go to blood pressure.");
    }

    public void OpenBloodSugarDetails()
    {
        Log.d(getClass().getSimpleName(), "Go to blood sugar.");
    }
}
