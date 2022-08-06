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

package de.dviererbe.healthtrack.domain;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class StepCountRecordTests
{
    @Test
    public void CalculatePercentageOfGoalReached_Should_Return0Percent_When_StepCountIs0_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
            0,
            1,
            LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 0;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return0Percent_When_StepCountIs0_Variant2()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                0,
                1000,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 0;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return0Percent_When_StepCountIs0_Variant3()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                0,
                Integer.MAX_VALUE,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 0;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_GoalIs0_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                0,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_GoalIs0_Variant2()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                Integer.MAX_VALUE,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_GoalIs0_Variant3()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                2345,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return1Percent_When_StepCountIsLargerThan0ButSmallerThan1PercentOfGoal_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1,
                Integer.MAX_VALUE,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 1;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsSameValueAsGoal_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                0,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsSameValueAsGoal_Variant2()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsSameValueAsGoal_Variant3()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1,
                1,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsSameValueAsGoal_Variant4()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1234,
                1234,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant2()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1234,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant3()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                Integer.MAX_VALUE,
                0,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant4()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                2,
                1,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant5()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1234,
                1,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant6()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                Integer.MAX_VALUE,
                1,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant7()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1234,
                1233,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return100Percent_When_StepCountIsLargerThanGoal_Variant8()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                Integer.MAX_VALUE,
                Integer.MAX_VALUE - 1,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 100;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return50Percent_When_StepCountIsHalfOfGoal_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1,
                2,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 50;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return50Percent_When_StepCountIsHalfOfGoal_Variant2()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                10000,
                20000,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 50;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return25Percent_When_StepCountIsQuarterOfGoal_Variant1()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                1,
                4,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 25;
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void CalculatePercentageOfGoalReached_Should_Return25Percent_When_StepCountIsQuarterOfGoal_Variant2()
    {
        // Arrange:
        final StepCountRecord stepCountRecord = new StepCountRecord(
                10000,
                40000,
                LocalDateTime.of(2022, 6, 1, 12, 0, 0));

        // Act:
        final int actualValue = stepCountRecord.CalculatePercentageOfGoalReached();

        // Assert:
        final int expectedValue = 25;
        assertEquals(expectedValue, actualValue);
    }
}
