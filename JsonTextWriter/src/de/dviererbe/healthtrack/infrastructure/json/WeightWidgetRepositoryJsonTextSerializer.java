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

import de.dviererbe.healthtrack.domain.WeightRecord;
import de.dviererbe.healthtrack.persistence.IBulkQueryable;
import de.dviererbe.healthtrack.persistence.IRepositoryImplementationDetailsProvider;

import java.io.IOException;

public class WeightWidgetRepositoryJsonTextSerializer
    extends RepositoryJsonTextSerializerBase<WeightRecord>
{
    public WeightWidgetRepositoryJsonTextSerializer(
            IRepositoryImplementationDetailsProvider repositoryImplementationDetailsProvider,
            IBulkQueryable<WeightRecord> recordRepository)
    {
        super(repositoryImplementationDetailsProvider, recordRepository);
    }

    @Override
    protected void WriteRecord(IJsonTextWriter jsonTextWriter, WeightRecord weightRecord)
        throws
            JsonError,
            IOException
    {
        jsonTextWriter
        .WriteStartObject()
        .WritePropertyName("identifier").WriteValue(weightRecord.Identifier.toString())
        .WritePropertyName("value").WriteValue(weightRecord.Value)
        .WritePropertyName("unit").WriteValue(weightRecord.Unit.name())
        .WritePropertyName("timeOfMeasurement").WriteValue(weightRecord.TimeOfMeasurement)
        .WriteEndObject();
    }
}
