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

import android.text.Layout;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.presentation.ErrorFragment;
import org.jetbrains.annotations.NotNull;

public class AboutFragmentsViewPagerAdapter extends FragmentStateAdapter
{
    public AboutFragmentsViewPagerAdapter(final Fragment fragment)
    {
        super(fragment);
    }

    public void AttachTabsToFragment(final TabLayout tabLayout, final ViewPager2 viewPager)
    {
        new TabLayoutMediator(tabLayout, viewPager, this::MapTabTitle).attach();
    }

    private void MapTabTitle(final TabLayout.Tab tab, final int position)
    {
        final int textId = MapTabTitle(position);
        tab.setText(textId);
        DisableTextCapitalization(tab);
    }

    private void DisableTextCapitalization(final TabLayout.Tab tab)
    {
        for (int i = 0, max = tab.view.getChildCount(); i < max; ++i)
        {
            View view = tab.view.getChildAt(i);

            if (view instanceof TextView)
            {
                TextView textView = (TextView)view;
                textView.setAllCaps(false);
                textView.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
            }
        }
    }

    private int MapTabTitle(final int position)
    {
        switch (position)
        {
            case 0:
                return R.string.about_description_tabtitle;
            case 1:
                return R.string.about_privacy_policy_tabtitle;
            case 2:
                return R.string.about_license_tabtitle;
            case 3:
                return R.string.about_credits_tabtitle;
            default:
                return R.string.about_unknown_tabtitle;
        }
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 0:
                return new AboutDescriptionFragment();
            case 1:
                return new AboutPrivacyPolicyFragment();
            case 2:
                return new AboutLicenseFragment();
            case 3:
                return new AboutCreditsFragment();
            default:
                return new ErrorFragment();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount()
    {
        return 4;
    }
}
