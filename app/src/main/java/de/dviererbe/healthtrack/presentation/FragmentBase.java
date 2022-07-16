package de.dviererbe.healthtrack.presentation;

import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import de.dviererbe.healthtrack.HealthTrackApp;
import de.dviererbe.healthtrack.IDependencyResolver;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.ViewModelFactory;

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
