package de.dviererbe.healthtrack.infrastructure.json;

import de.dviererbe.healthtrack.domain.BloodPressureRecord;
import de.dviererbe.healthtrack.persistence.IBulkQueryable;
import de.dviererbe.healthtrack.persistence.IRepositoryImplementationDetailsProvider;

import java.io.IOException;

public class BloodPressureWidgetRepositoryJsonTextSerializer
    extends RepositoryJsonTextSerializerBase<BloodPressureRecord>
{
    public BloodPressureWidgetRepositoryJsonTextSerializer(
            IRepositoryImplementationDetailsProvider repositoryImplementationDetailsProvider,
            IBulkQueryable<BloodPressureRecord> recordRepository)
    {
        super(repositoryImplementationDetailsProvider, recordRepository);
    }

    @Override
    protected void WriteRecord(
        final IJsonTextWriter jsonTextWriter,
        final BloodPressureRecord bloodPressureRecord)
        throws JsonError, IOException
    {
        jsonTextWriter
        .WriteStartObject()
        .WritePropertyName("identifier").WriteValue(bloodPressureRecord.Identifier.toString())
        .WritePropertyName("systolic").WriteValue(bloodPressureRecord.Systolic)
        .WritePropertyName("diastolic").WriteValue(bloodPressureRecord.Diastolic)
        .WritePropertyName("unit").WriteValue(bloodPressureRecord.Unit.name())
        .WritePropertyName("pulse").WriteValue(bloodPressureRecord.Pulse)
        .WritePropertyName("medication").WriteValue(bloodPressureRecord.Medication.name())
        .WritePropertyName("timeOfMeasurement").WriteValue(bloodPressureRecord.TimeOfMeasurement)
        .WritePropertyName("note").WriteValue(bloodPressureRecord.Note)
        .WriteEndObject();
    }
}
