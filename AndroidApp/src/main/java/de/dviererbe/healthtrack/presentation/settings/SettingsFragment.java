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

package de.dviererbe.healthtrack.presentation.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import de.dviererbe.healthtrack.ApplicationContextDependencyResolver;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.application.ExportUserDataAsJsonOperation;
import de.dviererbe.healthtrack.infrastructure.IUserDataJsonTextWriterProvider;
import de.dviererbe.healthtrack.infrastructure.json.IJsonTextWriter;
import de.dviererbe.healthtrack.infrastructure.json.JsonError;
import de.dviererbe.healthtrack.persistence.SharedPreferenceRepository;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel.ISettingsViewModelEventHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SettingsFragment
    extends PreferenceFragmentCompat
{
    private static final String TAG = "SettingsFragment";
    private static final int RequestCode_CreateFile = 1;
    private static final int RequestCode_ExportDataDialog = 2;

    private SettingsViewModel _viewModel;
    private SharedPreferenceRepository _sharedPreferenceRepository;

    private UserDataJsonTextWriterProvider _userDataJsonTextWriterProvider;

    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment. Subclasses are expected
     * to call {@link #setPreferenceScreen(PreferenceScreen)} either directly or via helper methods such as
     * {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(
        final Bundle savedInstanceState,
        final String rootKey)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        {
            final Activity activity = getActivity();
            final HealthTrackApp app = (HealthTrackApp)activity.getApplication();
            final ApplicationContextDependencyResolver dependencyResolver = (ApplicationContextDependencyResolver)app.GetDependencies();

            _sharedPreferenceRepository = GetSharedPreferenceRepository();
            _userDataJsonTextWriterProvider = new UserDataJsonTextWriterProvider();
            _viewModel = dependencyResolver.GetViewModelFactory().CreatSettingsViewModel(_userDataJsonTextWriterProvider);
        }
    }

    private SharedPreferenceRepository GetSharedPreferenceRepository()
    {
        final Activity activity = getActivity();
        final HealthTrackApp app = (HealthTrackApp)activity.getApplication();
        final ApplicationContextDependencyResolver dependencyResolver = (ApplicationContextDependencyResolver)app.GetDependencies();

        return dependencyResolver.GetSharedPreferenceRepository();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume()
    {
        super.onResume();
        _sharedPreferenceRepository.RegisterOnSharedPreferenceChangeListener();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause()
    {
        super.onPause();
        _sharedPreferenceRepository.UnregisterOnSharedPreferenceChangeListener();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        _viewModel.Dispose();
        _viewModel = null;
        _userDataJsonTextWriterProvider = null;
        _sharedPreferenceRepository = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        if (preference.getKey().equals("widgets_user_data_export"))
        {
            ExportUserData();
            return true;
        }

        if (preference.getKey().equals("widgets_user_data_delete"))
        {
            DeleteUserData();
            return true;
        }

        return false;
    }

    private void ExportUserData()
    {
        ExportDataFragment dialog = ExportDataFragment.NewInstance();
        dialog.setTargetFragment(this, RequestCode_ExportDataDialog);
        dialog.show(getActivity().getSupportFragmentManager(), "ExportDataDialog");
    }

    private void DeleteUserData()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.settings_user_data_delete_dialog_title);
        dialogBuilder.setMessage(R.string.settings_user_data_delete_dialog_message);

        dialogBuilder.setPositiveButton(
                R.string.settings_user_data_delete_dialog_button_confirm,
                (DialogInterface dialog, int which) -> _viewModel.DeleteUserData());

        dialogBuilder.setNegativeButton(
                R.string.settings_user_data_delete_dialog_button_cancel,
                (DialogInterface dialog, int which) -> {});

        dialogBuilder.show();
    }



    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(
        final int requestCode,
        final int resultCode,
        @Nullable @org.jetbrains.annotations.Nullable
        final Intent data)
    {
        if (requestCode == RequestCode_CreateFile)
        {
            _userDataJsonTextWriterProvider.StorageLocationSelected(resultCode, data);
        }
        else if (requestCode == RequestCode_ExportDataDialog)
        {
            if (resultCode != Activity.RESULT_OK) return;

            final ExportUserDataAsJsonOperation.Options exportOptions =
                    ExportDataFragment.UnpackBundle(data.getExtras());

            _viewModel.ExportUserData(exportOptions);
        }
    }

    private class SettingsViewModelEventHandler implements ISettingsViewModelEventHandler
    {
        /**
         * Called when the request to export the user data succeeded.
         */
        @Override
        public void ExportingUserDataSucceeded()
        {
            ShowToast(R.string.settings_user_data_export_dialog_notifications_save_success);
        }

        /**
         * Called when the request to export the user data failed.
         */
        @Override
        public void ExportingUserDataFailed()
        {
            ShowToast(R.string.settings_user_data_export_dialog_notifications_save_failure);
        }

        /**
         * Called when the request to delete the user data succeeded.
         */
        @Override
        public void DeletingUserDataSucceeded()
        {
            ShowToast(R.string.settings_user_data_delete_notification_success);
        }

        /**
         * Called when the request to delete the user data failed.
         */
        @Override
        public void DeletingUserDataFailed()
        {
            ShowToast(R.string.settings_user_data_delete_notification_failure);
        }

        private void ShowToast(int stringId)
        {
            Toast.makeText(getContext(), stringId, Toast.LENGTH_LONG).show();
        }
    }

    private class UserDataJsonTextWriterProvider implements IUserDataJsonTextWriterProvider
    {
        private ProvideUserDataJsonTextWriterRequestCallback _callback = null;

        /**
         * Provides an {@link IJsonTextWriter} where the user data can be exported to.
         *
         * @param callback
         */
        @Override
        public void ProvideUserDataJsonTextWriter(final ProvideUserDataJsonTextWriterRequestCallback callback)
        {
            _callback = callback;
            ShowSelectStorageLocationDialog("application/json", "healthtrack-data.json");
        }

        /**
         * Shows the user a UI dialog for selection a location for storing the user data.
         *
         * @param mediaType MIME type (IANA media; defined by IETF RFC 6838) of the document that should be created.
         * @param nameSuggestion Suggestion for the user for the name of document that should be created.
         */
        private void ShowSelectStorageLocationDialog(
            final String mediaType,
            final String nameSuggestion)
        {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mediaType);
            intent.putExtra(Intent.EXTRA_TITLE, nameSuggestion);

            startActivityForResult(intent, RequestCode_CreateFile);
        }

        public void StorageLocationSelected(final int resultCode, final Intent data)
        {
            if (_callback == null) return;

            final ProvideUserDataJsonTextWriterRequestCallback callback = _callback;
            _callback = null;

            try
            {
                if (resultCode != Activity.RESULT_OK) callback.UserDataJsonTextWriterCouldNotBeProvided(new Exception("User Aborted"));

                final Uri storagePath = data.getData();
                final OutputStream storageFile = getContext().getContentResolver().openOutputStream(storagePath);

                callback.UserDataJsonTextWriterProvided(new JsonTextWriterAdapter(new OutputStreamWriter(storageFile)));
            }
            catch (Exception exception)
            {
                Log.d(TAG, "Failed to get storage path from CREATE_DOCUMENT_ACTION intent.", exception);
                _callback.UserDataJsonTextWriterCouldNotBeProvided(exception);
            }
        }

        // TODO: placeholder for custom implementation, because Android JsonWriter can not be used in unit tests.
        // custom implementation is not yet done
        private class JsonTextWriterAdapter implements IJsonTextWriter
        {
            private final JsonWriter _jsonWriter;
            public JsonTextWriterAdapter(Writer writer)
            {
                _jsonWriter = new JsonWriter(writer);
            }

            @Override
            public IJsonTextWriter WriteStartArray() throws JsonError, IOException
            {
                _jsonWriter.beginArray();
                return this;
            }

            @Override
            public IJsonTextWriter WriteStartObject() throws JsonError, IOException
            {
                _jsonWriter.beginObject();
                return this;
            }

            @Override
            public IJsonTextWriter WriteEndArray() throws JsonError, IOException
            {
                _jsonWriter.endArray();
                return this;
            }

            @Override
            public IJsonTextWriter WriteEndObject() throws JsonError, IOException
            {
                _jsonWriter.endObject();
                return this;
            }

            @Override
            public IJsonTextWriter WritePropertyName(String propertyName) throws JsonError, IOException
            {
                _jsonWriter.name(propertyName);
                return this;
            }

            @Override
            public IJsonTextWriter WriteNull() throws JsonError, IOException
            {
                _jsonWriter.nullValue();
                return this;
            }

            @Override
            public IJsonTextWriter WriteValue(int value) throws JsonError, IOException
            {
                _jsonWriter.value(value);
                return this;
            }

            @Override
            public IJsonTextWriter WriteValue(double value) throws JsonError, IOException
            {
                _jsonWriter.value(value);
                return this;
            }

            @Override
            public IJsonTextWriter WriteValue(String value) throws JsonError, IOException
            {
                _jsonWriter.value(value);
                return this;
            }

            @Override
            public IJsonTextWriter WriteValue(LocalDateTime value) throws JsonError, IOException
            {
                _jsonWriter.value(value.format(DateTimeFormatter.ISO_DATE_TIME));
                return this;
            }

            @Override
            public void close() throws IOException
            {
                _jsonWriter.close();
            }
        }
    }
}
