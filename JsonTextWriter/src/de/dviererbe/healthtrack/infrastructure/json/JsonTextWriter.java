package de.dviererbe.healthtrack.infrastructure.json;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;

/**
 * Provides a simple API for writing Json Text formatted (as defined by
 * <a href="https://www.rfc-editor.org/rfc/rfc8259.html">RFC8259</a>)
 * data to a {@link Writer}.
 */
public class JsonTextWriter implements IJsonTextWriter
{
    private static final char LeftSquareBracket = '\u005B';
    private static final char LeftCurlyBracket = '\u007B';
    private static final char RightSquareBracket = '\u005D';
    private static final char RightCurlyBracket = '\u007D';
    private static final char Colon = '\u003A';
    private static final char Comma = '\u002C';

    private final Writer _writer;

    /**
     * Initializes a new instance of the {@link JsonTextWriter} class using a specified {@link Writer}
     * and the default configuration.
     *
     * @param writer The {@link Writer} instance to write the json text to. Note: If the {@link Writer}
     *               instance encodes the text in binary format the UTF-8 [<a href="https://www.rfc-editor.org/rfc/rfc3629.html">RFC3629</a>]
     *               encoding must be used to comply with <a href="https://www.rfc-editor.org/rfc/rfc8259.html#section-8.1">RFC8259 Section 8.1</a>.
     */
    public JsonTextWriter(final Writer writer)
    {
        _writer = writer;
    }

    @Override
    public IJsonTextWriter WriteStartArray() throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteStartObject() throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteEndArray() throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteEndObject() throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WritePropertyName(String propertyName) throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteNull() throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteValue(int value) throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteValue(double value) throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteValue(String value) throws JsonError, IOException
    {
        return this;
    }

    @Override
    public IJsonTextWriter WriteValue(LocalDateTime value) throws JsonError, IOException
    {
        return this;
    }

    @Override
    public void close() throws IOException
    {
        _writer.close();
    }
}