package de.dviererbe.healthtrack.infrastructure.json;

import de.dviererbe.healthtrack.persistence.IBulkQueryable;
import de.dviererbe.healthtrack.persistence.IRepositoryImplementationDetailsProvider;
import de.dviererbe.healthtrack.persistence.exceptions.RepositoryException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class RepositoryJsonTextSerializerBase<TRecord> implements IRepositoryJsonTextSerializer
{
    protected static final String ImplementationsPropertyName = "implementations";
    protected static final String RecordsPropertyName = "records";

    protected final IRepositoryImplementationDetailsProvider _repositoryImplementationDetailsProvider;
    protected final IBulkQueryable<TRecord> _recordRepository;

    public RepositoryJsonTextSerializerBase(
        final IRepositoryImplementationDetailsProvider repositoryImplementationDetailsProvider,
        final IBulkQueryable<TRecord> recordRepository)
    {
        _repositoryImplementationDetailsProvider = repositoryImplementationDetailsProvider;
        _recordRepository = recordRepository;
    }

    @Override
    public void SerializeAsJson(final IJsonTextWriter jsonTextWriter)
        throws
            RepositoryException,
            JsonError,
            IOException
    {
        jsonTextWriter.WriteStartObject();

        jsonTextWriter.WritePropertyName(ImplementationsPropertyName);
        WriteRepositoryImplementationDetails(jsonTextWriter);

        jsonTextWriter.WritePropertyName(RecordsPropertyName);
        WriteRecords(jsonTextWriter);

        jsonTextWriter.WriteEndObject();
    }

    protected void WriteRepositoryImplementationDetails(final IJsonTextWriter jsonTextWriter)
        throws
            RepositoryException,
            JsonError,
            IOException
    {
        jsonTextWriter.WriteStartObject();

        for (final Map<String, String> implementationDetails
                : _repositoryImplementationDetailsProvider.GetImplementationDetails())
        {
            for (final Map.Entry<String, String> entry : implementationDetails.entrySet())
            {
                jsonTextWriter.WritePropertyName(entry.getKey()).WriteValue(entry.getValue());
            }
        }

        jsonTextWriter.WriteEndObject();
    }

    protected void WriteRecords(final IJsonTextWriter jsonTextWriter)
        throws
            RepositoryException,
            JsonError,
            IOException
    {
        final long count = _recordRepository.GetRecordCount();
        long offset = 0;

        final int maxBufferSize = 128;
        List<TRecord> buffer;

        jsonTextWriter.WriteStartArray();

        while (offset < count)
        {
            buffer = _recordRepository.GetRecordsDescending(offset, maxBufferSize);

            for (TRecord record : buffer)
            {
                WriteRecord(jsonTextWriter, record);
            }

            offset += buffer.size();
        }

        jsonTextWriter.WriteEndArray();
    }

    protected abstract void WriteRecord(final IJsonTextWriter jsonTextWriter, final TRecord record)
        throws
            JsonError,
            IOException;
}
