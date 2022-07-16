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

package de.dviererbe.healthtrack.presentation.main.about;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import de.dviererbe.healthtrack.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment
{
    private FragmentAboutBinding _binding;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        _binding = FragmentAboutBinding.inflate(inflater, container, false);
        ConfigureTabView();

        return _binding.getRoot();
    }

    private void ConfigureTabView()
    {
        final AboutFragmentsViewPagerAdapter adapter = new AboutFragmentsViewPagerAdapter(this);
        _binding.ViewPager.setAdapter(adapter);
        _binding.TabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        adapter.AttachTabsToFragment(_binding.TabLayout, _binding.ViewPager);
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
}