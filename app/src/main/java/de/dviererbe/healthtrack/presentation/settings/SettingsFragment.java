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
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import de.dviererbe.healthtrack.ApplicationContextDependencyResolver;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.persistence.SharedPreferenceRepository;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel.ISettingsView;
import de.dviererbe.healthtrack.presentation.settings.SettingsViewModel.ISettingsView.IExportUserDataDialogObserver.ExportUserDataDialogResult;

public class SettingsFragment extends PreferenceFragmentCompat implements ISettingsView
{
    private static final String TAG = "SettingsFragment";
    private static final int RequestCode_CreateFile = 1;
    private static final int RequestCode_ExportDataDialog = 2;

    private SettingsViewModel _viewModel;
    private SharedPreferenceRepository _sharedPreferenceRepository;

    private IExportUserDataDialogObserver _exportUserDataDialogObserver;
    private ISelectStoragePathDialogObserver _selectStoragePathDialogObserver;


    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreatePreferences(
            Bundle savedInstanceState,
            String rootKey)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        {
            final Activity activity = getActivity();
            final HealthTrackApp app = (HealthTrackApp)activity.getApplication();
            final ApplicationContextDependencyResolver dependencyResolver = (ApplicationContextDependencyResolver)app.GetDependencies();

            _sharedPreferenceRepository = GetSharedPreferenceRepository();
            _viewModel = dependencyResolver.GetViewModelFactory().CreatSettingsViewModel(getLifecycle(), this);
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
        _viewModel = null;
        _selectStoragePathDialogObserver = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        if (preference.getKey().equals("widgets_user_data_export"))
        {
            _viewModel.ExportUserData();
            return true;
        }

        if (preference.getKey().equals("widgets_user_data_delete"))
        {
            _viewModel.DeleteUserData();
            return true;
        }

        return false;
    }

    /**
     * Notifies the user that the attempt to export the user data succeeded.
     */
    @Override
    public void NotifyUserThatExportingUserDataSucceeded()
    {
        ShowToast(R.string.settings_user_data_export_dialog_notifications_save_success);
    }

    /**
     * Notifies the user that the attempt to export the user data failed.
     */
    @Override
    public void NotifyUserThatExportingUserDataFailed()
    {
        ShowToast(R.string.settings_user_data_export_dialog_notifications_save_failure);
    }

    /**
     * Notifies the user that the attempt to delete all user data succeeded.
     */
    @Override
    public void NotifyUserThatDeletingUserDataSucceeded()
    {
        ShowToast(R.string.settings_user_data_delete_notification_success);
    }

    /**
     * Notifies the user that the attempt to delete all user data failed.
     */
    @Override
    public void NotifyUserThatDeletingUserDataFailed()
    {
        ShowToast(R.string.settings_user_data_delete_notification_failure);
    }

    private void ShowToast(int stringId)
    {
        Toast.makeText(getContext(), stringId, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows the user a UI dialog for deleting all user related data.
     *
     * @param callback reference to a callback mechanism for when the user data delete dialog finishes.
     */
    @Override
    public void ShowExportUserDataDialog(final IExportUserDataDialogObserver callback)
    {
        _exportUserDataDialogObserver = callback;

        ExportDataFragment dialog = ExportDataFragment.NewInstance();
        dialog.setTargetFragment(this, RequestCode_ExportDataDialog);
        dialog.show(getActivity().getSupportFragmentManager(), "ExportDataDialog");
    }

    /**
     * Shows the user a UI dialog for selection a location for storing the user data.
     *
     * @param mediaType MIME type (IANA media; defined by IETF RFC 6838) of the document that should be created.
     * @param nameSuggestion Suggestion for the user for the name of document that should be created.
     * @param callback reference to a callback mechanism for when the select storage path dialog finishes.
     */
    @Override
    public void ShowSelectStorageLocationDialog(
            final String mediaType,
            final String nameSuggestion,
            final ISelectStoragePathDialogObserver callback)
    {
        _selectStoragePathDialogObserver = callback;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mediaType);
        intent.putExtra(Intent.EXTRA_TITLE, nameSuggestion);

        startActivityForResult(intent, RequestCode_CreateFile);
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
            final @Nullable @org.jetbrains.annotations.Nullable Intent data)
    {
        if (requestCode == RequestCode_CreateFile)
        {
            if (_selectStoragePathDialogObserver == null) return;

            try
            {
                if (resultCode != Activity.RESULT_OK)
                {
                    _selectStoragePathDialogObserver.OnCompleted(null);
                    return;
                }

                final Uri storagePath = data.getData();
                _selectStoragePathDialogObserver.OnCompleted(storagePath);
            }
            catch (Exception exception)
            {
                Log.d(TAG, "Failed to get storage path from CREATE_DOCUMENT_ACTION intent.", exception);
                _selectStoragePathDialogObserver.OnCompleted(null);
            }
            finally
            {
                _selectStoragePathDialogObserver = null;
            }
        }
        else if (requestCode == RequestCode_ExportDataDialog)
        {
            if (_exportUserDataDialogObserver == null) return;

            try
            {
                if (resultCode != Activity.RESULT_OK)
                {
                    _exportUserDataDialogObserver.OnCompleted(null);
                    return;
                }

                boolean[] values = data.getExtras().getBooleanArray("ExportUserDataDialogResult");
                ExportUserDataDialogResult result = new ExportUserDataDialogResult(values[0],values[1],values[2],values[3],values[4]);

                _exportUserDataDialogObserver.OnCompleted(result);
            }
            catch (Exception exception)
            {
                Log.d(TAG, "Failed to get ExportUserDataDialogResult.", exception);
                _exportUserDataDialogObserver.OnCompleted(null);
            }
            finally
            {
                _exportUserDataDialogObserver = null;
            }
        }
    }

    /**
     * Shows the user a UI dialog for deleting all user related data.
     *
     * @param callback reference to a callback mechanism for when the user data delete dialog finishes.
     */
    @Override
    public void ShowDeleteUserDataDialog(final IDeleteUserDataDialogObserver callback)
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.settings_user_data_delete_dialog_title);
        dialogBuilder.setMessage(R.string.settings_user_data_delete_dialog_message);

        dialogBuilder.setPositiveButton(
            R.string.settings_user_data_delete_dialog_button_confirm,
            (DialogInterface dialog, int which) ->
            {
                callback.OnCompleted(true);
            });

        dialogBuilder.setNegativeButton(
            R.string.settings_user_data_delete_dialog_button_cancel,
            (DialogInterface dialog, int which) ->
            {
                callback.OnCompleted(false);
            });

        dialogBuilder.show();
    }
}
