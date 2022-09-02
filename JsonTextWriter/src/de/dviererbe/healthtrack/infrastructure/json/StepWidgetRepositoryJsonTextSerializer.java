package de.dviererbe.healthtrack.infrastructure.json;

import de.dviererbe.healthtrack.domain.StepCountRecord;
import de.dviererbe.healthtrack.persistence.IBulkQueryable;
import de.dviererbe.healthtrack.persistence.IDefaultStepCountGoalGetter;
import de.dviererbe.healthtrack.persistence.IRepositoryImplementationDetailsProvider;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

import java.io.IOException;

public class StepWidgetRepositoryJsonTextSerializer extends RepositoryJsonTextSerializerBase<StepCountRecord>
{
    protected final IDefaultStepCountGoalGetter _defaultStepCountGoalGetter;

    public StepWidgetRepositoryJsonTextSerializer(
            final IRepositoryImplementationDetailsProvider repositoryImplementationDetailsProvider,
            final IBulkQueryable<StepCountRecord> recordRepository,
            final IDefaultStepCountGoalGetter defaultStepCountGoalGetter)
    {
        super(repositoryImplementationDetailsProvider, recordRepository);
        _defaultStepCountGoalGetter = defaultStepCountGoalGetter;
    }

    @Override
    public void SerializeAsJson(IJsonTextWriter jsonTextWriter) throws RepositoryException, JsonError, IOException
    {
        jsonTextWriter.WriteStartObject();

        jsonTextWriter.WritePropertyName(ImplementationsPropertyName);
        WriteRepositoryImplementationDetails(jsonTextWriter);

        jsonTextWriter
                .WritePropertyName("defaultStepCountGoal")
                .WriteValue(_defaultStepCountGoalGetter.GetDefaultStepCountGoal());

        jsonTextWriter.WritePropertyName(RecordsPropertyName);
        WriteRecords(jsonTextWriter);

        jsonTextWriter.WriteEndObject();
    }

    @Override
    protected void WriteRecord(
        final IJsonTextWriter jsonTextWriter,
        final StepCountRecord stepCountRecord)
        throws
            JsonError,
            IOException
    {
        jsonTextWriter
        .WriteStartObject()
        .WritePropertyName("identifier").WriteValue(stepCountRecord.Identifier.toString())
        .WritePropertyName("stepCount").WriteValue(stepCountRecord.StepCount)
        .WritePropertyName("goal").WriteValue(stepCountRecord.Goal)
        .WritePropertyName("timeOfMeasurement").WriteValue(stepCountRecord.TimeOfMeasurement)
        .WriteEndObject();
    }
}
