package de.dviererbe.healthtrack.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.IDependencyResolver;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.ViewModelFactory;

import java.time.LocalDateTime;

public abstract class FragmentBase extends Fragment
{
    protected HealthTrackApp GetApplication()
    {
        return (HealthTrackApp)getActivity().getApplication();
    }

    protected IDependencyResolver GetDependencyResolver()
    {
        return GetApplication().GetDependencies();
    }

    protected ViewModelFactory GetViewModelFactory()
    {
        return GetDependencyResolver().GetViewModelFactory();
    }

    protected void MakeToastWithShortDuration(int stringId)
    {
        ShowToast(stringId, Toast.LENGTH_SHORT);
    }

    protected void ShowToastWithLongDuration(int stringId)
    {
        ShowToast(stringId, Toast.LENGTH_LONG);
    }

    protected void ShowToast(int stringId, int duration)
    {
        Toast.makeText(getContext(), stringId, duration).show();
    }

    /**
     * Shows the user a dialog to pick a date and time.
     *
     * @param initialValue The initial value that should be displayed.
     * @param callback The method that should be called when the user picked a date & time.
     */
    protected void PickDateTime(final LocalDateTime initialValue, final IDateTimePickedCallback callback)
    {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (DatePicker datePicker, int year, int month, int dayOfMonth) ->
            {
                final boolean is24HourView = true;
                final TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (TimePicker timePicker, int hourOfDay, int minute) ->
                    {
                        final LocalDateTime pickedDateTime = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute);
                        callback.OnDateTimePicked(pickedDateTime);
                    },
                    initialValue.getHour(),
                    initialValue.getMinute(),
                    is24HourView);

                timePickerDialog.show();
            },
            initialValue.getYear(),
            initialValue.getMonthValue(),
            initialValue.getDayOfMonth());

        datePickerDialog.show();
    }

    /**
     * Callback mechanism when the user picked a date & time.
     */
    public interface IDateTimePickedCallback
    {
        /**
         * Called when the user picked a date & time.
         *
         * @param pickedDateTime The date & time value the user picked.
         */
        void OnDateTimePicked(LocalDateTime pickedDateTime);
    }

    protected boolean TrySetToolBarTitle(int stringId)
    {
        final View toolbarView = getActivity().findViewById(R.id.toolbar);
        if (!(toolbarView instanceof Toolbar)) return false;

        final Toolbar toolbar = (Toolbar)toolbarView;
        toolbar.setTitle(stringId);

        return true;
    }

    protected boolean TryGoBack()
    {
        return NavHostFragment.findNavController(this).popBackStack();
    }
}
