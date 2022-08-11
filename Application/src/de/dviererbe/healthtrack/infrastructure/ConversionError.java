package de.dviererbe.healthtrack.infrastructure;

/**
 * The exception that gets thrown when a value can not be converted.
 */
public class ConversionError extends Exception
{
    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and
     * may subsequently be initialized by a call to {@code Throwable.initCause(java.lang.Throwable)}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@code Throwable.getMessage()} method.
     */
    public ConversionError(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * Note that the detail message associated with cause is not automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@code Throwable.getMessage()} method).
     * @param cause  the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ConversionError(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the
     * class and detail message of cause). This constructor is useful for exceptions
     * that are little more than wrappers for other throwables (for example, {@code PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@code Throwable.getCause()} method). (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ConversionError(Throwable cause)
    {
        super(cause);
    }
}