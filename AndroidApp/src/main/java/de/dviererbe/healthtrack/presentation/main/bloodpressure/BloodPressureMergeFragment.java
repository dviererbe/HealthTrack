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

package de.dviererbe.healthtrack.presentation.main.bloodpressure;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;
import de.dviererbe.healthtrack.BR;
import de.dviererbe.healthtrack.IDisposable;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentBloodpressureMergeBinding;
import de.dviererbe.healthtrack.domain.BloodPressureUnit;
import de.dviererbe.healthtrack.domain.MedicationState;
import de.dviererbe.healthtrack.presentation.FragmentBase;
import de.dviererbe.healthtrack.presentation.main.bloodpressure.BloodPressureMergeViewModel.IBloodPressureMergeViewModelEventHandler;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class BloodPressureMergeFragment extends FragmentBase
{
    private static final String PARAM_Identifier = "Identifier";

    private BloodPressureMergeViewModelDataBindingAdapter _viewModelAdapter;
    private FragmentBloodpressureMergeBinding _binding;
    private UUID _recordIdentifier;

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
     * Called to do initial creation of a fragment. This is called after {@link Fragment#onAttach(Activity)} and
     * before {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * Note that this can be called while the fragment's activity is still in the process of being created.
     * As such, you can not rely on things like the activity's content view hierarchy being initialized at this point.
     * If you want to do work once the activity itself is created, see {@link Fragment#onActivityCreated(Bundle)}.
     * Any restored child fragments will be created before the base Fragment.onCreate method returns
     *
     * @param savedInstanceState
     *      If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UnbundleParameter(getArguments());
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and non-graphical
     * fragments can return null. This will be called between {@link Fragment#onCreate(Bundle)} and
     * {@link Fragment#onActivityCreated(Bundle)}. A default View can be returned by calling
     * {@link Fragment#Fragment(int)} in your constructor. Otherwise, this method returns null.
     * It is recommended to only inflate the layout in this method and move logic that operates on the returned
     * View to {@link Fragment#onViewCreated(View, Bundle)}.
     * If you return a View from here, you will later be called in {@link Fragment#onDestroyView} when the view
     * is being released.
     *
     * @param inflater
     *      The LayoutInflater object that can be used to inflate
     *      any views in the fragment,
     * @param container
     *      If non-null, this is the parent view that the fragment's
     *      UI should be attached to.  The fragment should not add the view itself,
     *      but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState
     *      If non-null, this fragment is being re-constructed
     *      from a previous saved state as given here.
     *
     * @return Instantiate user interface view.
     */
    @Override
    public View onCreateView(
        @NotNull
        final LayoutInflater inflater,
        final ViewGroup container,
        final Bundle savedInstanceState)
    {
        _binding = FragmentBloodpressureMergeBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Called immediately after {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their
     * view hierarchy has been completely created. The fragment's view hierarchy is not
     * however attached to its parent at this point.
     *
     * @param view
     *      The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState
     *      If non-null, this fragment is being re-constructed
     *      from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(
        @NonNull @NotNull
        final View view,
        @Nullable @org.jetbrains.annotations.Nullable
        final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final BloodPressureMergeViewModel viewModel = GetViewModelFactory().
                CreateBloodPressureMergeViewModel(_recordIdentifier);

        _viewModelAdapter = new BloodPressureMergeViewModelDataBindingAdapter(viewModel);

        _binding.setViewModel(_viewModelAdapter);
        _binding.setLifecycleOwner(this);
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved, but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        _viewModelAdapter.Dispose();
        _viewModelAdapter = null;
        _binding = null;
    }

    public class BloodPressureMergeViewModelDataBindingAdapter
        extends BaseObservable
        implements IDisposable
    {
        private final BloodPressureMergeViewModel _viewModel;

        private final IDisposable _viewModelEventListener;

        public final ArrayAdapter<CharSequence> BloodPressureUnitsAdapter;

        public final ArrayAdapter<CharSequence> MedicationStateAdapter;

        public final boolean IsEditable;

        public final int SaveButtonLabel;

        public final int RecordCouldNotLoadedErrorMessageVisibility;

        public BloodPressureMergeViewModelDataBindingAdapter(
                final BloodPressureMergeViewModel viewModel)
        {
            _viewModel = viewModel;

            if (_viewModel.IsExistingRecordEdited())
            {
                TrySetToolBarTitle(R.string.bloodpressure_merge_header_update);
                SaveButtonLabel = R.string.bloodpressure_merge_button_update;
            }
            else
            {
                TrySetToolBarTitle(R.string.bloodpressure_merge_header_create);
                SaveButtonLabel = R.string.bloodpressure_merge_button_create;
            }

            BloodPressureUnitsAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.bloodpressure_units,
                android.R.layout.simple_spinner_dropdown_item);

            MedicationStateAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.bloodpressure_medication,
                android.R.layout.simple_spinner_dropdown_item);

            IsEditable = !_viewModel.IsReadOnly();

            RecordCouldNotLoadedErrorMessageVisibility =
                _viewModel.IsReadOnly()
                ? View.VISIBLE
                : View.INVISIBLE;

            _viewModelEventListener = _viewModel.RegisterEventHandler(new ViewModelEventListener());
        }

        @Bindable
        public String getSystolicValue()
        {
            return _viewModel.GetSystolicString();
        }

        public void setSystolicValue(String value)
        {
            _viewModel.SetSystolic(value);
        }

        @Bindable
        public int getSystolicValueErrorMessageVisibility()
        {
            return _viewModel.IsSystolicValueInvalid()
               ? View.VISIBLE
               : View.INVISIBLE;
        }

        @Bindable
        public String getDiastolicValue()
        {
            return _viewModel.GetDiastolicString();
        }

        public void setDiastolicValue(final String value)
        {
            _viewModel.SetDiastolic(value);
        }

        @Bindable
        public int getDiastolicValueErrorMessageVisibility()
        {
            return _viewModel.IsDiastolicValueInvalid() ?
                   View.VISIBLE :
                   View.INVISIBLE;
        }

        @Bindable
        public int getSelectedBloodPressureUnit()
        {
            switch (_viewModel.GetBloodPressureUnit())
            {
                default:
                case MillimetreOfMercury:
                    return 0;
                case Kilopascal:
                    return 1;
            }
        }

        public void setSelectedBloodPressureUnit(final int position)
        {
            if (position == 0)
                _viewModel.SetBloodPressureUnit(BloodPressureUnit.MillimetreOfMercury);
            else if (position == 1)
                _viewModel.SetBloodPressureUnit(BloodPressureUnit.Kilopascal);
        }

        @Bindable
        public String getPulseValue()
        {
            return _viewModel.GetPulseString();
        }

        public void setPulseValue(final String value)
        {
            _viewModel.SetPulse(value);
        }

        @Bindable
        public int getPulseValueErrorMessageVisibility()
        {
            return _viewModel.IsPulseValueInvalid() ?
                   View.VISIBLE :
                   View.INVISIBLE;
        }

        @Bindable
        public int getSelectedMedicationState()
        {
            switch (_viewModel.GetMedicationState())
            {
                default:
                case None:
                    return 0;
                case Taken:
                    return 1;
                case NotTaken:
                    return 2;
            }
        }

        public void setSelectedMedicationState(final int position)
        {
            if (position == 0)
                _viewModel.SetMedicationState(MedicationState.None);
            else if (position == 1)
                _viewModel.SetMedicationState(MedicationState.Taken);
            else if (position == 2)
                _viewModel.SetMedicationState(MedicationState.NotTaken);
        }

        @Bindable
        public String getDateTimeOfMeasurement()
        {
            return _viewModel.GetDateTimeOfMeasurementAsString();
        }

        public void OnPickDateTimeOfMeasurementButtonClicked()
        {
            final LocalDateTime dateTimeOfMeasurement = _viewModel.GetDateTimeOfMeasurement();
            PickDateTime(dateTimeOfMeasurement, _viewModel::SetDateTimeOfMeasurement);
        }

        public void OnSetDateTimeOfMeasurementToNowButtonClicked()
        {
            _viewModel.SetDateTimeOfMeasurementToNow();
        }

        @Bindable
        public String getNote()
        {
            return _viewModel.GetNote();
        }

        public void setNote(final String value)
        {
            _viewModel.SetNote(value);
        }

        @Bindable
        public boolean getIsSaveButtonEnabled()
        {
            return _viewModel.CanValuesBeSaved();
        }

        public void OnSaveButtonClicked()
        {
            _viewModel.Save();
        }

        /**
         * Performs application-defined tasks associated with freeing, releasing, or resetting resources.
         */
        @Override
        public void Dispose()
        {
            _viewModelEventListener.Dispose();
            _viewModel.Dispose();
        }

        private class ViewModelEventListener implements IBloodPressureMergeViewModelEventHandler
        {
            /**
             * Called when the systolic property changed.
             */
            @Override
            public void SystolicValueChanged()
            {
                notifyPropertyChanged(BR.systolicValue);
            }

            /**
             * Called when the validity of the systolic property changed.
             */
            @Override
            public void SystolicValueValidityChanged()
            {
                notifyPropertyChanged(BR.systolicValueErrorMessageVisibility);
            }

            /**
             * Called when the diastolic property changed.
             */
            @Override
            public void DiastolicValueChanged()
            {
                notifyPropertyChanged(BR.diastolicValue);
            }

            /**
             * Called when the validity of the diastolic property changed.
             */
            @Override
            public void DiastolicValueValidityChanged()
            {
                notifyPropertyChanged(BR.diastolicValueErrorMessageVisibility);
            }

            /**
             * Called when the blood-pressure unit property changed.
             */
            @Override
            public void BloodPressureUnitChanged()
            {
                notifyPropertyChanged(BR.selectedBloodPressureUnit);
            }

            /**
             * Called when the pulse property changed.
             */
            @Override
            public void PulseValueChanged()
            {
                notifyPropertyChanged(BR.pulseValue);
            }

            /**
             * Called when the validity of the pulse property changed.
             */
            @Override
            public void PulseValueValidityChanged()
            {
                notifyPropertyChanged(BR.pulseValueErrorMessageVisibility);
            }

            /**
             * Called when the medication state property changed.
             */
            @Override
            public void MedicationStateChanged()
            {
                notifyPropertyChanged(BR.selectedMedicationState);
            }

            /**
             * Called when the date-time of measurement property changed.
             */
            @Override
            public void DateTimeOfMeasurementChanged()
            {
                notifyPropertyChanged(BR.dateTimeOfMeasurement);
            }

            /**
             * Called when the note property changed.
             */
            @Override
            public void NoteChanged()
            {
                notifyPropertyChanged(BR.note);
            }

            /**
             * Called if the state changes if {@link BloodPressureMergeViewModel#Save()} has any effects.
             */
            @Override
            public void CanValuesBeSavedChanged()
            {
                notifyPropertyChanged(BR.isSaveButtonEnabled);
            }

            /**
             * Called when the record was saved.
             */
            @Override
            public void RecordSaved()
            {
                TryGoBack();
            }

            /**
             * Called when the record could not be saved because of an error.
             */
            @Override
            public void RecordCouldNotBeSaved()
            {
                if (_viewModel.IsExistingRecordEdited())
                {
                    ShowToastWithLongDuration(R.string.bloodpressure_merge_notifications_update_failed);
                }
                else
                {
                    ShowToastWithLongDuration(R.string.bloodpressure_merge_notifications_creation_failed);
                }
            }
        }
    }
}