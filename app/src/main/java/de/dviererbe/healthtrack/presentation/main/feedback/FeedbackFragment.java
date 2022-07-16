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

package de.dviererbe.healthtrack.presentation.main.feedback;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.dviererbe.healthtrack.R;
import de.dviererbe.healthtrack.databinding.FragmentFeedbackBinding;

public class FeedbackFragment extends DialogFragment
{
    private FragmentFeedbackBinding _binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link FeedbackFragment}.
     */
    public static FeedbackFragment NewInstance()
    {
        return new FeedbackFragment();
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
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        _binding.buttonOpenGithubIssues.setOnClickListener(new OpenGithubIssuesOnClick());
        _binding.buttonWriteEmail.setOnClickListener(new WriteFeedbackEmailOnClick());

        return _binding.getRoot();
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

    private class OpenGithubIssuesOnClick implements View.OnClickListener
    {
        private final Uri OpenGithubIssuesWebLink;

        public OpenGithubIssuesOnClick()
        {
            OpenGithubIssuesWebLink = Uri.parse(getString(R.string.github_issues_link));
        }

        @Override
        public void onClick(View view)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, OpenGithubIssuesWebLink);
            startActivity(browserIntent);
        }
    }

    private class WriteFeedbackEmailOnClick implements View.OnClickListener
    {
        private final String FeedbackEMailAddress;

        public WriteFeedbackEmailOnClick()
        {
            FeedbackEMailAddress = getString(R.string.feedback_email_address);
        }

        @Override
        public void onClick(View view)
        {
            Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
            sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {FeedbackEMailAddress});
            sendEmailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(sendEmailIntent, getString(R.string.intent_title_send_email)));
        }
    }
}