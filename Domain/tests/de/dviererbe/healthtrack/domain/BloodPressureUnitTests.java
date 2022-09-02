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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BloodPressureUnitTests
{
    @Test
    public void ConvertFromMillimetreOfMercuryToMillimetreOfMercury_Should_ReturnOriginalValue_Variant1()
    {
        // Arrange:
        final double bloodPressureValue = 1.0;
        final BloodPressureUnit from = BloodPressureUnit.MillimetreOfMercury;
        final BloodPressureUnit to = BloodPressureUnit.MillimetreOfMercury;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 1.0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromMillimetreOfMercuryToMillimetreOfMercury_Should_ReturnOriginalValue_Variant2()
    {
        // Arrange:
        final double bloodPressureValue = 42;
        final BloodPressureUnit from = BloodPressureUnit.MillimetreOfMercury;
        final BloodPressureUnit to = BloodPressureUnit.MillimetreOfMercury;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 42;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromMillimetreOfMercuryToMillimetreOfMercury_Should_ReturnOriginalValue_Variant3()
    {
        // Arrange:
        final double bloodPressureValue = 0;
        final BloodPressureUnit from = BloodPressureUnit.MillimetreOfMercury;
        final BloodPressureUnit to = BloodPressureUnit.MillimetreOfMercury;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilopascalToKilopascal_Should_ReturnOriginalValue_Variant1()
    {
        // Arrange:
        final double bloodPressureValue = 1.0;
        final BloodPressureUnit from = BloodPressureUnit.Kilopascal;
        final BloodPressureUnit to = BloodPressureUnit.Kilopascal;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 1.0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilopascalToKilopascal_Should_ReturnOriginalValue_Variant2()
    {
        // Arrange:
        final double bloodPressureValue = 42;
        final BloodPressureUnit from = BloodPressureUnit.Kilopascal;
        final BloodPressureUnit to = BloodPressureUnit.Kilopascal;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 42;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilopascalToKilopascal_Should_ReturnOriginalValue_Variant3()
    {
        // Arrange:
        final double bloodPressureValue = 0;
        final BloodPressureUnit from = BloodPressureUnit.Kilopascal;
        final BloodPressureUnit to = BloodPressureUnit.Kilopascal;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromMillimetreOfMercuryToKilopascal_Should_ReturnExpectedValue_Variant1()
    {
        // Arrange:
        final double bloodPressureValue = 0;
        final BloodPressureUnit from = BloodPressureUnit.MillimetreOfMercury;
        final BloodPressureUnit to = BloodPressureUnit.Kilopascal;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromMillimetreOfMercuryToKilopascal_Should_ReturnExpectedValue_Variant2()
    {
        // Arrange:
        final double bloodPressureValue = 1;
        final BloodPressureUnit from = BloodPressureUnit.MillimetreOfMercury;
        final BloodPressureUnit to = BloodPressureUnit.Kilopascal;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 0.1333;
        final double delta = 0.09;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromMillimetreOfMercuryToKilopascal_Should_ReturnExpectedValue_Variant3()
    {
        // Arrange:
        final double bloodPressureValue = 42;
        final BloodPressureUnit from = BloodPressureUnit.MillimetreOfMercury;
        final BloodPressureUnit to = BloodPressureUnit.Kilopascal;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 5.5986;
        final double delta = 0.09;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilopascalToMillimetreOfMercury_Should_ReturnExpectedValue_Variant1()
    {
        // Arrange:
        final double bloodPressureValue = 0;
        final BloodPressureUnit from = BloodPressureUnit.Kilopascal;
        final BloodPressureUnit to = BloodPressureUnit.MillimetreOfMercury;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 0;
        final double delta = 0.0;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilopascalToMillimetreOfMercury_Should_ReturnExpectedValue_Variant2()
    {
        // Arrange:
        final double bloodPressureValue = 1;
        final BloodPressureUnit from = BloodPressureUnit.Kilopascal;
        final BloodPressureUnit to = BloodPressureUnit.MillimetreOfMercury;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 7.501875;
        final double delta = 0.09;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void ConvertFromKilopascalToMillimetreOfMercury_Should_ReturnExpectedValue_Variant3()
    {
        // Arrange:
        final double bloodPressureValue = 42;
        final BloodPressureUnit from = BloodPressureUnit.Kilopascal;
        final BloodPressureUnit to = BloodPressureUnit.MillimetreOfMercury;

        // Act:
        final double actualValue = BloodPressureUnit.Convert(bloodPressureValue, from, to);

        // Assert:
        final double expectedValue = 315.0788;
        final double delta = 0.09;
        assertEquals(expectedValue, actualValue, delta);
    }

    @Test
    public void Convert_Should_ThrowError_When_FromOrToUnitIsNull()
    {
        assertThrows(RuntimeException.class, () -> BloodPressureUnit.Convert(1, null, null));
        assertThrows(RuntimeException.class, () -> BloodPressureUnit.Convert(1, null, BloodPressureUnit.MillimetreOfMercury));
        assertThrows(RuntimeException.class, () -> BloodPressureUnit.Convert(1, null, BloodPressureUnit.Kilopascal));
        assertThrows(RuntimeException.class, () -> BloodPressureUnit.Convert(1, BloodPressureUnit.MillimetreOfMercury, null));
        assertThrows(RuntimeException.class, () -> BloodPressureUnit.Convert(1, BloodPressureUnit.Kilopascal, null));
    }
}
