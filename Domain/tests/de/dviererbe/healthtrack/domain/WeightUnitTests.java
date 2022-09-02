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

import static org.junit.Assert.*;

public class WeightUnitTests
{
    @Test
    public void ConvertFromKilogramToKilogram_Should_ReturnOriginalValue_Variation1()
    {
        // Arrange:
        final double weightValue = 1;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 1;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilogramToKilogram_Should_ReturnOriginalValue_Variation2()
    {
        // Arrange:
        final double weightValue = 42;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 42;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilogramToKilogram_Should_ReturnOriginalValue_Variation3()
    {
        // Arrange:
        final double weightValue = 0;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToPound_Should_ReturnOriginalValue_Variation1()
    {
        // Arrange:
        final double weightValue = 1;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 1;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToPound_Should_ReturnOriginalValue_Variation2()
    {
        // Arrange:
        final double weightValue = 42;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 42;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToPound_Should_ReturnOriginalValue_Variation3()
    {
        // Arrange:
        final double weightValue = 0;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilogramToPound_Should_ReturnExpectedValue_Variation1()
    {
        // Arrange:
        final double weightValue = 1.0;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 2.2;
        final double delta = 0.009;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilogramToPound_Should_ReturnExpectedValue_Variation2()
    {
        // Arrange:
        final double weightValue = 0.453592;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 1;
        final double delta = 0.009;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilogramToPound_Should_ReturnExpectedValue_Variation3()
    {
        // Arrange:
        final double weightValue = 0;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilogramToPound_Should_ReturnExpectedValue_Variation4()
    {
        // Arrange:
        final double weightValue = 10;
        final WeightUnit from = WeightUnit.Kilogram;
        final WeightUnit to = WeightUnit.Pound;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 22.04623;
        final double delta = 0.009;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToKilogram_Should_ReturnExpectedValue_Variation1()
    {
        // Arrange:
        final double weightValue = 1.0;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 0.45359237;
        final double delta = 0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToKilogram_Should_ReturnExpectedValue_Variation2()
    {
        // Arrange:
        final double weightValue = 2.204623;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 1;
        final double delta = 0.009;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToKilogram_Should_ReturnExpectedValue_Variation3()
    {
        // Arrange:
        final double weightValue = 0;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromPoundToKilogram_Should_ReturnExpectedValue_Variation4()
    {
        // Arrange:
        final double weightValue = 10;
        final WeightUnit from = WeightUnit.Pound;
        final WeightUnit to = WeightUnit.Kilogram;

        // Act:
        final double actualValue = WeightUnit.Convert(weightValue, from, to);

        // Assert:
        final double expectedValue = 4.535924;
        final double delta = 0.009;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void Convert_Should_ThrowError_When_FromOrToUnitIsNull()
    {
        assertThrows(RuntimeException.class, () -> WeightUnit.Convert(1, null, null));
        assertThrows(RuntimeException.class, () -> WeightUnit.Convert(1, null, WeightUnit.Kilogram));
        assertThrows(RuntimeException.class, () -> WeightUnit.Convert(1, null, WeightUnit.Pound));
        assertThrows(RuntimeException.class, () -> WeightUnit.Convert(1, WeightUnit.Kilogram, null));
        assertThrows(RuntimeException.class, () -> WeightUnit.Convert(1, WeightUnit.Pound, null));
    }
}
