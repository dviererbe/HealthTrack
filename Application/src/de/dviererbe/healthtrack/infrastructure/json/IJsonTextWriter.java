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

package de.dviererbe.healthtrack.infrastructure.json;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;

public interface IJsonTextWriter extends AutoCloseable, Closeable
{
    IJsonTextWriter WriteStartArray() throws JsonError, IOException;

    IJsonTextWriter WriteStartObject() throws JsonError, IOException;

    IJsonTextWriter WriteEndArray() throws JsonError, IOException;

    IJsonTextWriter WriteEndObject() throws JsonError, IOException;

    IJsonTextWriter WritePropertyName(final String propertyName) throws JsonError, IOException;

    IJsonTextWriter WriteNull() throws JsonError, IOException;

    IJsonTextWriter WriteValue(final int value) throws JsonError, IOException;

    IJsonTextWriter WriteValue(final double value) throws JsonError, IOException;

    IJsonTextWriter WriteValue(final String value) throws JsonError, IOException;

    IJsonTextWriter WriteValue(final LocalDateTime value) throws JsonError, IOException;
}
