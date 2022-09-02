package de.dviererbe.healthtrack.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.persistence.exceptions.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public abstract class SQLiteRepositoryBase<TRecord>
    extends
        SQLiteOpenHelper
    implements
        IRepositoryImplementationDetailsProvider,
        IQueryableById<TRecord>,
        IBulkQueryable<TRecord>,
        IPerDayBulkQueryable<TRecord>,
        IMergable<TRecord>,
        IDeletableById,
        IBulkDeletable,
        IDisposable
{
    private static final SQLiteDatabase.CursorFactory CursorFactory = null;
    protected final String DatabaseName;
    protected final int DatabaseVersion;

    protected final String RecordTableName;
    protected final String RecordTableIdColumnName;
    protected final String[] RecordTableNameColumns;

    private boolean _disposed = false;
    private boolean _downgradeRequested = false;
    private boolean _upgradeRequested = false;

    public SQLiteRepositoryBase(
        @Nullable
        final Context context,
        final String databaseName,
        final int databaseVersion,
        final String recordTableName,
        final String recordTableIdColumnName,
        final String[] recordTableNameColumns)
    {
        super(context, databaseName, CursorFactory, databaseVersion);

        DatabaseName = databaseName;
        DatabaseVersion = databaseVersion;
        RecordTableName = recordTableName;
        RecordTableIdColumnName = recordTableIdColumnName;
        RecordTableNameColumns = recordTableNameColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(
            final SQLiteDatabase database,
            final int oldVersion,
            final int newVersion)
    {
        if (oldVersion == newVersion) return;

        _upgradeRequested = true;
        throw new IllegalStateException("Database upgrade strategy is not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDowngrade(
            final SQLiteDatabase database,
            final int oldVersion,
            final int newVersion)
    {
        if (oldVersion == newVersion) return;

        _downgradeRequested = true;
        throw new IllegalStateException("Database downgrade strategy is not implemented.");
    }

    @Override
    public List<Map<String, String>> GetImplementationDetails()
    {
        return new ArrayList<Map<String, String>>()
        {{
            add(new HashMap<String, String>()
            {{
                put("name", getClass().getName());
                put("version", String.valueOf(DatabaseVersion));
            }});
        }};
    }

    /**
     * Gets the count of all stored records.
     *
     * @return scalar count of all stored records.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public long GetRecordCount() throws RepositoryDisposed, RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();

        try
        {
            final String selectCountQuery = "SELECT COUNT(*) FROM " + RecordTableName;

            final SQLiteDatabase database = getReadableDatabase();

            try (Cursor cursor = database.rawQuery(selectCountQuery, null))
            {
                return ReadScalar(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read record count.", exception);
        }
    }

    /**
     * Gets a record with a specific identifier.
     *
     * @param identifier The unique identifier of the record that should be retrieved.
     * @return the {@link TRecord} with the specified identifier
     * @throws RecordIdentifierIsNull when {@code identifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code identifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public TRecord GetRecord(UUID identifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (identifier == null) throw new RecordIdentifierIsNull();

        final String identifierAsString = identifier.toString();

        return GetRecord(identifierAsString, database -> QueryRecordByIdentifier(database, identifierAsString));
    }

    protected TRecord GetRecord(final String identifier, final CursorProvider cursorProvider) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = cursorProvider.Query(database))
            {
                if (!cursor.moveToFirst())
                    throw new RecordNotFound(identifier);

                if (!cursor.isLast())
                    throw new RepositoryException("More than one returned record with identifying value (" + identifier +") were found.");

                return ParseRecordFromCursor(cursor);
            }
        }
        catch (RecordNotFound exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read record.", exception);
        }
    }

    /**
     * Gets a sequence of records with the maximum length of {@code count}
     * in descending order sorted by the time of measurement beginning at the {@code offset}.
     *
     * @param offset zero based n-th element in descending order where to begin reading records
     * @param count  maximum number of record to be read from the repository
     * @return read sequence of records
     * @throws OffsetIsNegative    when {@code offset} is negative.
     * @throws CountIsNotPositive  when {@code count} is not positive.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<TRecord> GetRecordsDescending(long offset, int count)
        throws
            OffsetIsNegative,
            CountIsNotPositive,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (offset < 0L) throw new OffsetIsNegative();
        if (count <= 0) throw new CountIsNotPositive();

        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = QueryRecordsDescending(database, offset, count))
            {
                return ParseRecordsFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to read records.", exception);
        }
    }

    /**
     * Gets all records for a specified day in descending order sorted by the time of measurement.
     *
     * @param day The specific day to get the records for.
     * @return All records for the specified day in descending order sorted by the time of measurement.
     * @throws DayIsNull           when {@code day} is {@code null}.
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public List<TRecord> GetRecordsForDayDescending(final LocalDate day)
        throws
            DayIsNull,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (day == null) throw new DayIsNull();

        return GetRecords(database -> QueryRecordsForDayDescending(database, day));
    }

    protected List<TRecord> GetRecords(CursorProvider cursorProvider) throws RepositoryException
    {
        try
        {
            final SQLiteDatabase database = getReadableDatabase();

            try (final Cursor cursor = cursorProvider.Query(database))
            {
                return ParseRecordsFromCursor(cursor);
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to retrieve records.", exception);
        }
    }

    /**
     * Created or updates an existing record for the specified identifier with the specified measurements.
     *
     * @param record The record that should be created or updated.
     * @throws RecordIsNull                  when {@code record} is {@code null}.
     * @throws OneOrMorePropertiesAreInvalid when {@code record} contains invalid properties.
     * @throws RepositoryDisposed            when the repository was already disposed.
     * @throws RepositoryException           when an unexpected I/O error occurs.
     */
    @Override
    public void CreateOrUpdateRecord(TRecord record) throws
            RecordIsNull,
            OneOrMorePropertiesAreInvalid,
            RepositoryDisposed,
            RepositoryException
    {
        CreateOrUpdateRecordCore(record, null);
    }

    protected void CreateOrUpdateRecordCore(
        final TRecord record,
        final Consumer<SQLiteDatabase> preCreateOrUpdateRoutine) throws
            RecordIsNull,
            OneOrMorePropertiesAreInvalid,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (record == null) throw new RecordIsNull();

        ValidateRecord(record);
        final ContentValues values = PackageValues(record);

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                if (preCreateOrUpdateRoutine != null)
                {
                    preCreateOrUpdateRoutine.accept(database);
                }

                if (!TryInsertRecord(database, values) && !TryUpdateRecord(database, values))
                {
                    throw new Exception("Insert and update operation was not successful.");
                }

                database.setTransactionSuccessful();
            }
            finally
            {
                database.endTransaction();
            }
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to create or update record.", exception);
        }
    }


    protected boolean TryInsertRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final long rowId = database.insert(RecordTableName, null, values);
        return rowId != -1L;
    }

    protected boolean TryUpdateRecord(final SQLiteDatabase database, final ContentValues values)
    {
        final String selection = RecordTableIdColumnName + " = ?";
        final String[] selectionArgs = { (String)values.get(RecordTableIdColumnName) };

        final int affectedRows = database.update(RecordTableName, values, selection, selectionArgs);
        return affectedRows == 1;
    }

    /**
     * Deletes a record with a specific identifier.
     *
     * @param identifier The value that identifies the record that should be deleted uniquely.
     * @throws RecordIdentifierIsNull when {@code identifier} is {@code null}.
     * @throws RecordNotFound         when no record with the specified {@code identifier} was found.
     * @throws RepositoryDisposed     when the repository was already disposed.
     * @throws RepositoryException    when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteRecord(UUID identifier) throws
            RecordIdentifierIsNull,
            RecordNotFound,
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();
        if (identifier == null) throw new RecordIdentifierIsNull();

        final String whereClause = RecordTableIdColumnName + " = ?";
        final String whereArg = identifier.toString();

        DeleteRecord(whereClause, whereArg);
    }

    protected void DeleteRecord(
        final String whereClause,
        final String whereArg)
        throws
            RecordNotFound,
            RepositoryException
    {
        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.beginTransaction();

            try
            {
                final int affectedRows = database.delete(RecordTableName, whereClause, new String[] { whereArg });

                if (affectedRows == 0) throw new RecordNotFound(whereArg);
                if (affectedRows > 1) throw new RepositoryException("More than one record was affected. Rollback will be initiated.");

                database.setTransactionSuccessful();
            }
            finally
            {
                database.endTransaction();
            }
        }
        catch (RecordNotFound exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to delete record (" + whereArg +").", exception);
        }
    }

    /**
     * Deletes all records.
     *
     * @throws RepositoryDisposed  when the repository was already disposed.
     * @throws RepositoryException when an unexpected I/O error occurs.
     */
    @Override
    public void DeleteAllRecords()
        throws
            RepositoryDisposed,
            RepositoryException
    {
        ThrowWhenDatabaseStateIsBad();

        try
        {
            final SQLiteDatabase database = getWritableDatabase();
            database.delete(RecordTableName, null, null);
        }
        catch (Exception exception)
        {
            throw new RepositoryException("Failed to deletes all records.", exception);
        }
    }

    /**
     * Reads the result set with the assumption that it consists only of
     * one record of type INTEGER and returns it.
     *
     * @param cursor the {@link Cursor} used to navigate in the result set.
     * @return the scalar value.
     * @throws RepositoryException when more thn one record was found.
     */
    protected long ReadScalar(
        final Cursor cursor)
        throws RepositoryException
    {
        if (!cursor.moveToFirst())
            throw new RepositoryException("Result set has no row.");

        if (!cursor.isLast())
            throw new RepositoryException("Result set has more than one row.");

        if (cursor.getColumnCount() != 1)
            throw new RepositoryException("Result set has more than one column.");

        return cursor.getLong(0);
    }

    protected List<TRecord> ParseRecordsFromCursor(
        final Cursor cursor)
    {
        ArrayList<TRecord> records = new ArrayList<>();

        while (cursor.moveToNext())
        {
            records.add(ParseRecordFromCursor(cursor));
        }

        return records;
    }

    protected abstract TRecord ParseRecordFromCursor(
        final Cursor cursor);

    protected abstract Cursor QueryRecordByIdentifier(
        final SQLiteDatabase database,
        final String identifier);

    protected abstract Cursor QueryRecordsDescending(
        final SQLiteDatabase database,
        final long offset,
        final int count);

    protected abstract Cursor QueryRecordsForDayDescending(
        final SQLiteDatabase database,
        final LocalDate day);

    protected abstract void ValidateRecord(final TRecord record)
        throws OneOrMorePropertiesAreInvalid;

    protected abstract ContentValues PackageValues(final TRecord record);

    protected void ThrowWhenDatabaseStateIsBad()
        throws
            RepositoryDisposed,
            RepositoryException
    {
        if (_disposed) throw new RepositoryDisposed();
        if (_upgradeRequested) throw new RepositoryException("Database upgrade failed.");
        if (_downgradeRequested) throw new RepositoryException("Database downgrade failed.");
    }

    /**
     * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
     */
    @Override
    public void Dispose()
    {
        if (!_disposed)
        {
            _disposed = true;
            close();
        }
    }

    protected interface CursorProvider
    {
        Cursor Query(final SQLiteDatabase database);
    }
}
