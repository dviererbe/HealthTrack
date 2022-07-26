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

package de.dviererbe.healthtrack.presentation.main.weight;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.annotation.Nullable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentWeightMergeBinding;
import de.dviererbe.healthtrack.domain.WeightUnit;
import de.dviererbe.healthtrack.infrastructure.INavigationRouter;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.weight.WeightMergeViewModel.IWeightMergeView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class WeightMergeFragment
        extends FragmentBase
        implements IWeightMergeView, INavigationRouter
{
    private static final String PARAM_Identifier = "Identifier";

    private WeightMergeViewModel _viewModel;
    private FragmentWeightMergeBinding _binding;

    private UUID _recordIdentifier;

    private boolean _pullingValues = false;

    public static Bundle BundleParameter(UUID recordIdentifier)
    {
        final Bundle parameter = new Bundle();
        parameter.putString(PARAM_Identifier, recordIdentifier.toString());

        return parameter;
    }

    private void UnbundleParameter(Bundle parameter)
    {
        if (parameter == null || !parameter.containsKey(PARAM_Identifier)) return;

        _recordIdentifier = UUID.fromString(parameter.getString(PARAM_Identifier));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UnbundleParameter(getArguments());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        _viewModel = GetViewModelFactory().CreateWeightMergeViewModel(
                getLifecycle(),
                this,
                this,
                _recordIdentifier);

        _binding = FragmentWeightMergeBinding.inflate(inflater, container, false);

        SetUpBindingToViewModel();

        return _binding.getRoot();
    }

    private void SetUpBindingToViewModel()
    {
        _binding.editTextValue.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // nothing to do here
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (_pullingValues) return;
                _viewModel.SetWeightValue(editable.toString());
            }
        });

        final ArrayAdapter<CharSequence> unitsAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.weight_units,
                android.R.layout.simple_spinner_item);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        _binding.dropdownUnits.setAdapter(unitsAdapter);
        _binding.dropdownUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (position == 0)
                    _viewModel.SetWeightUnit(WeightUnit.Kilogram);
                else if (position == 1)
                    _viewModel.SetWeightUnit(WeightUnit.Pound);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                // nothing to do here
            }
        });

        _binding.btnSetDateTimeToNow.setOnClickListener(view -> _viewModel.SetDateTimeToNow());
        _binding.btnPickDateTime.setOnClickListener(view -> _viewModel.PickDateAndTime());
        _binding.btnSaveWeightRecord.setOnClickListener(view -> _viewModel.Save());

        if (_viewModel.IsEditView())
        {
            TrySetToolBarTitle(R.string.weight_merge_header_update);
            _binding.btnSaveWeightRecord.setText(R.string.weight_merge_button_update);
        }
        else
        {
            TrySetToolBarTitle(R.string.weight_merge_header_create);
            _binding.btnSaveWeightRecord.setText(R.string.weight_merge_button_create);
        }

        if (_viewModel.AreInputControlsDisabled())
        {
            _binding.editTextValue.setEnabled(false);
            _binding.dropdownUnits.setEnabled(false);
            _binding.btnPickDateTime.setEnabled(false);
            _binding.btnSetDateTimeToNow.setEnabled(false);
            _binding.btnSaveWeightRecord.setEnabled(false);
            _binding.errorTextRecordCouldNotLoad.setVisibility(View.VISIBLE);
        }
        else
        {
            _viewModel.ShowValueErrorMessage.observe(
                getViewLifecycleOwner(), (showValueErrorMessage) ->
                {
                    _binding.errorTextValue.setVisibility(
                            showValueErrorMessage
                            ? View.VISIBLE
                            : View.INVISIBLE);
                });
            _viewModel.IsSaveButtonEnabled.observe(
                getViewLifecycleOwner(), (isSaveButtonEnabled) ->
                {
                    _binding.btnSaveWeightRecord.setEnabled(isSaveButtonEnabled);
                });
        }

        PullValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _binding = null;
        _viewModel = null;
    }

    /**
     * Notifies the user that the record could not be created because of an error.
     */
    @Override
    public void NotifyUserThatRecordCouldNotBeCreated()
    {
        ShowToastWithLongDuration(R.string.weight_merge_notifications_creation_failed);
    }

    /**
     * Notifies the user that the record could not be updated because of an error.
     */
    @Override
    public void NotifyUserThatRecordCouldNotBeUpdated()
    {
        ShowToastWithLongDuration(R.string.weight_merge_notifications_update_failed);
    }

    /**
     * Shows the user a dialog to pick a date and time.
     *
     * @param callback a reference to a callback mechanism when the user made a decision.
     */
    @Override
    public void PickDateTimeDialog(IPickDateTimeDialogObserver callback)
    {
        final LocalDateTime dateTimeOfMeasurement = _viewModel.GetDateTime();

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (DatePicker datePicker, int year, int month, int dayOfMonth) ->
            {
                final boolean is24HourView = true;
                final TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (TimePicker timePicker, int hourOfDay, int minute) ->
                    {
                        callback.OnComplete((LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute)));
                    },
                    dateTimeOfMeasurement.getHour(),
                    dateTimeOfMeasurement.getMinute(),
                    is24HourView);

                timePickerDialog.show();
            },
            dateTimeOfMeasurement.getYear(),
            dateTimeOfMeasurement.getMonthValue(),
            dateTimeOfMeasurement.getDayOfMonth());

        datePickerDialog.show();
    }

    /**
     * Notifies the view that it should update its values.
     */
    @Override
    public void PullValues()
    {
        _pullingValues = true;
        PullValue();
        PullUnit();
        PullDateTime();
        _pullingValues = false;
    }

    private void PullValue()
    {
        _binding.editTextValue.setText(_viewModel.GetWeightValue());
    }

    private void PullUnit()
    {
        switch (_viewModel.GetWeightUnit())
        {
            case Pound:
                _binding.dropdownUnits.setSelection(1);
                break;
            case Kilogram:
            default:
                _binding.dropdownUnits.setSelection(0);
                break;
        }
    }

    private void PullDateTime()
    {
        _binding.textShowDateTime.setText(_viewModel.GetDateTimeString());
    }

    /**
     * Tries to navigate to the user settings UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToSettings()
    {
        return false;
    }

    /**
     * Tries to navigate to the create blood pressure record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateBloodPressureRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the blood pressure record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToBloodPressureRecordDetails(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit blood pressure record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditBloodPressureRecord(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the default step count goal editor user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToDefaultStepCountGoalEditor()
    {
        return false;
    }

    /**
     * Tries to navigate to the create step count record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateStepCountRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the step count record details user interface for
     * a record with a specific identifier.
     *
     * @param dateOfDay The date of the day of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToStepCountRecordDetails(LocalDate dateOfDay)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit step count record user interface for
     * a record with a specific identifier.
     *
     * @param dateOfDay The date of the day of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditStepCountRecord(LocalDate dateOfDay)
    {
        return false;
    }

    /**
     * Tries to navigate to the create weight record user interface.
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToCreateWeightRecord()
    {
        return false;
    }

    /**
     * Tries to navigate to the weight record details user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to see the details for.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToWeightRecordDetails(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the edit weight record user interface for
     * a record with a specific identifier.
     *
     * @param recordIdentifier The identifier of the record to edit.
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateToEditWeightRecord(UUID recordIdentifier)
    {
        return false;
    }

    /**
     * Tries to navigate to the preceding UI (User Interface).
     *
     * @return {@code true} if the navigation attempt was successfully; otherwise {@code false}.
     */
    @Override
    public boolean TryNavigateBack()
    {
        return TryGoBack();
    }
}