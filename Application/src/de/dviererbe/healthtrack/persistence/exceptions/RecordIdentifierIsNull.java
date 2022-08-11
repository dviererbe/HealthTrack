package de.dviererbe.healthtrack.persistence.exceptions;

/**
 * The {@link InvalidArgumentException} that is thrown when a specified record identifier is {@code null}.
 */
public class RecordIdentifierIsNull extends InvalidArgumentException
{
    /**
     * Initializes a new {@link RecordIdentifierIsNull} instance.
     */
    public RecordIdentifierIsNull()
    {
        super("recordIdentifier", "The specified record identifier is null.");
    }
}
