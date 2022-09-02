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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import de.dviererbe.healthtrack.application.ExportUserDataAsJsonOperation;
import de.dviererbe.healthtrack.databinding.FragmentExportDataBinding;
import org.jetbrains.annotations.NotNull;

public class ExportDataFragment extends DialogFragment
{
    private FragmentExportDataBinding _binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link ExportDataFragment}.
     */
    public static ExportDataFragment NewInstance()
    {
        return new ExportDataFragment();
    }

    private Bundle PackBundle()
    {
        final Bundle bundle = new Bundle();
        bundle.putBooleanArray("ExportUserDataDialogResult", new boolean[]
        {
            _binding.checkboxExportBloodpressureData.isChecked(),
            _binding.checkboxExportBloodsugarData.isChecked(),
            _binding.checkboxExportFoodData.isChecked(),
            _binding.checkboxExportStepsData.isChecked(),
            _binding.checkboxExportWeightData.isChecked()
        });

        return bundle;
    }

    public static ExportUserDataAsJsonOperation.Options UnpackBundle(final Bundle bundle)
    {
        boolean[] values = bundle.getBooleanArray("ExportUserDataDialogResult");

        return new ExportUserDataAsJsonOperation.Options(values[0],values[1],values[2],values[3],values[4]);
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
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState)
    {
        _binding = FragmentExportDataBinding.inflate(inflater, container, false);
        _binding.setLifecycleOwner(getViewLifecycleOwner());

        return _binding.getRoot();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(
            @NonNull @NotNull
            final View view,
            @Nullable @org.jetbrains.annotations.Nullable
            final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        _binding.setViewModel(new ExportDataFragmentViewModel());
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _binding = null;
    }

    public class ExportDataFragmentViewModel
    {
        public void ExportData()
        {
            final Intent intent = new Intent();

            Bundle bundle = PackBundle();
            intent.putExtras(bundle);

            Close(Activity.RESULT_OK, intent);
        }

        public void Close()
        {
            Close(Activity.RESULT_CANCELED, new Intent());
        }

        private void Close(final int resultCode, final Intent intent)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
            dismiss();
        }
    }

}