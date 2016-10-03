package com.jaridaapp.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.jaridaapp.R;
import com.jaridaapp.SettingsActivity;

public class GeneralFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
		
		setHasOptionsMenu(true);
		((SettingsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			getActivity().onBackPressed();
			return true;
		}
		return false;
	}
}
