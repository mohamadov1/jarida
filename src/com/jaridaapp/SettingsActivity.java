package com.jaridaapp;

import android.os.Bundle;

import com.jaridaapp.settings.GeneralFragment;
import com.jaridaapp.settings.InfoFragment;
import com.jaridaapp.settings.LinksFragment;
import com.jaridaapp.settings.NotificationFragment;
import com.jaridaapp.settings.StartFragment;

/**
 * The Host Activity for the Settings
 * @author Deniz
 * @since 1.3
 */
public class SettingsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings,4);
		
		getSupportActionBar().setTitle("");
		getSupportActionBar().setIcon(R.drawable.action_bar_logo);
		String action = getIntent().getAction();
		
        if (action != null && action.equals("pref_dis_general")) {
        	displayDrawerToggle(false);
        	getFragmentManager()
        	.beginTransaction()
        	.replace(R.id.content_frame, new GeneralFragment())
        	.commit();
        }else if (action != null && action.equals("pref_dis_notification")) {  
        	displayDrawerToggle(false);
        	getFragmentManager()
        	.beginTransaction()
        	.replace(R.id.content_frame, new NotificationFragment())
        	.commit();
        }else if (action != null && action.equals("pref_dis_info")) {
        	displayDrawerToggle(false);
        	getFragmentManager()
        	.beginTransaction()
        	.replace(R.id.content_frame, new InfoFragment())
        	.commit();
        }else if (action != null && action.equals("pref_dis_links")) {
        	displayDrawerToggle(false);
        	getFragmentManager()
        	.beginTransaction()
        	.replace(R.id.content_frame, new LinksFragment())
        	.commit();
        }else {
        	displayDrawerToggle(true);
        	getFragmentManager()
        	.beginTransaction()
        	.replace(R.id.content_frame, new StartFragment())
        	.commit();
        }
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		displayDrawerToggle(true);
	}
}
