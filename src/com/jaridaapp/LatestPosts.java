package com.jaridaapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jaridaapp.adapter.PostsAdapter;
import com.jaridaapp.model.posts.AllPosts;
import com.jaridaapp.model.posts.Author;
import com.jaridaapp.model.posts.Posts;
import com.jaridaapp.utils.ConnectionDetector;

/**
 * This Activity displays the latest Posts from your Blog.<br>
 * This class is also selected as the StartFragment Activity,
 * but you can change that easily in the launcher class, 
 * with the help of the documentation.<br>
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class LatestPosts extends BaseActivity {
	
	List<Posts> postsList = null;
	List<Posts> finalList = null;
	AllPosts recentposts;
	PostsAdapter adapter;
	ListView postsListView = null;
	SwipeRefreshLayout swipeLayout;
	ConnectionDetector cd;
	InterstitialAd interstitial;
	View footer;
	Button more;
	Context con;
	int page = 1;
	int total_pages = 2;
	boolean isMore = false;
	String baseurl, api, dateformat, include;
    String url = null;
    
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_posts,0);
		
		// Admob
		boolean admob = getResources().getBoolean(R.bool.admob);
		if(admob == true) {
			interstitial = new InterstitialAd(LatestPosts.this);
			interstitial.setAdUnitId(getString(R.string.ad_unit_1));
			AdView adView = new AdView(this);
			adView.setAdSize(AdSize.BANNER);
			adView.setAdUnitId(getString(R.string.ad_unit_1));
			LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
			layout.setVisibility(View.VISIBLE);
			layout.addView(adView);
	        AdRequest adRequest = new AdRequest.Builder().build();
	        adView.loadAd(adRequest);
	        interstitial.loadAd(adRequest);
	        interstitial.setAdListener(new AdListener() {
				public void onAdLoaded() {
					if (interstitial.isLoaded()) {
						interstitial.show();
					}
				}
			});
		}
		
		
		//getSupportActionBar().setTitle(getString(R.string.title_recent_posts));
		getSupportActionBar().setIcon(R.drawable.action_bar_logo);
		
		baseurl = getString(R.string.blogurl);
		api = getString(R.string.api);
		dateformat = getString(R.string.dateformat);
		include = getString(R.string.include);
		
		url = baseurl+ api +"/get_recent_posts/?date_format="+dateformat+"&include="+include;
		
		// Swipe refresher settings
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.primary, R.color.flat_blue, R.color.flat_green, R.color.flat_red);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(isCon()) {
					new DownloadPostsTask().execute(url);
				}else {
					cd.makeAlert();
				}
			}
		});
		
		
		// ListView settings
		postsListView = (ListView) findViewById(R.id.posts_list);
		footer =  ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
		more = (Button) footer.findViewById(R.id.btn_more);
		
		if(postsListView != null) {
			postsListView.addFooterView(footer);
		}
		
		postsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem == 0) {
					swipeLayout.setEnabled(true);
				}else {
					swipeLayout.setEnabled(false);
				}
			}
		});
		
		// Check internet connection
		if(isCon()) {
			swipeLayout.post(new Runnable() {
				@Override
				public void run() {	
					swipeLayout.setRefreshing(true);
					new DownloadPostsTask().execute(url);
				}
			});
		}else {
			cd.makeAlert();
			new DownloadPostsTask().execute(url);
		}
	}
	
	private Boolean isCon() {
		cd = new ConnectionDetector(this);
		return cd.isConnectingToInternet();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.latest_posts_list, menu);
		
		// Create the searchbar
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	// update list with latest post
	public void updateList() {
		finalList = postsList;
		adapter = new PostsAdapter(this, finalList);
		postsListView.setVisibility(View.VISIBLE);
		postsListView.setAdapter(adapter);
		postsListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				Posts posts = (Posts) finalList.get(position);
				Author author = (Author) posts.getAuthor();
				
				Intent intent = new Intent(LatestPosts.this, PostDetails.class);
				intent.putExtra("post_url", posts.getUrl());
				intent.putExtra("post_title", posts.getTitle());
				intent.putExtra("post_id", String.valueOf(posts.getId()));
				intent.putExtra("post_com_status", posts.getCommentStatus());
				intent.putExtra("post_author", author.getName());
				startActivity(intent);
				
				
				
			}
		});
		
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				page++;
				isMore = true;
				if(isCon() == true){
					new DownloadPostsTask().execute(url+"&page="+page);
				}else{
					cd.makeAlert();
					
				}
			}
		});
	}
	
	// add items to list after the loading more action
	private void additems() {
	    for(Posts posts : postsList) {
				adapter.add(posts);
		}
	}

	// download the latest posts
	protected class DownloadPostsTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
			swipeLayout.setEnabled(true);
			swipeLayout.setRefreshing(true);
        }

		@Override
		protected void onPostExecute(Void result) {
			swipeLayout.setRefreshing(false);
			if(page == total_pages){
				more.setEnabled(false);
			    more.setText(getString(R.string.msg_last_page));
			}
			
			if (postsList != null) {			
				if(isMore == true) {
					additems();
				}else {
					updateList();
				}
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String uri = params[0];   
			uri = uri.replaceAll(" ", "%20");
	         try {
	        	 InputStream source;
	        	 if(isCon()){
	        	  source = retrieveStream(uri); 
	        	//  copyAssets(source);
	        	 
	        	  
	        	 }
	        	else{
	        	 source =getAssets().open("myBlog.json");
	        	}
	        	
	        	Gson gson = new Gson();
	        	Reader reader = new InputStreamReader(source);  
		        System.out.println(reader);
	        	recentposts = gson.fromJson(reader, AllPosts.class);
	        	 total_pages = recentposts.getPages();
	        	 postsList = new ArrayList<Posts>();
	        	   for (Posts posts : recentposts.getPosts()) {
	                     postsList.add(posts);
	               }
                 reader.close();
             } catch (Exception e) {
            	 e.printStackTrace();
             }
	        return null;
	    }
	    
	    private InputStream retrieveStream(String url) {
			DefaultHttpClient client = new DefaultHttpClient(); 
			HttpGet getRequest = new HttpGet(url);
			try {
				HttpResponse getResponse = client.execute(getRequest);
				final int statusCode = getResponse.getStatusLine().getStatusCode();
				if(statusCode != HttpStatus.SC_OK) { 
					Log.w("WPBA", "Error " + statusCode + " for URL " + url); 
					return null;
				}
				HttpEntity getResponseEntity = getResponse.getEntity();
				return getResponseEntity.getContent();
			} 
			catch (IOException e) {
				getRequest.abort();
				Log.w("WPBA", "Error for URL " + url, e);
			}
		return null;
	    }
	}
	private void copyAssets(InputStream inp) {
	   // AssetManager assetManager = getAssets();
	 
	        InputStream inpt = inp;
	        OutputStream out = null;
	        try {
	          File outFile = new File(getExternalFilesDir(null), "myBlog.json");
	          out = new FileOutputStream(outFile);
	          copyFile(inpt, out);
	          
	          System.out.println(out);
	          
	        } catch(IOException e) {
	            Log.e("tag", "Failed to copy asset file: " + "myBlog.json", e);
	        }     
	        finally {
	            if (inpt != null) {
	                try {
	                	inpt.close();
	                } catch (IOException e) {
	                    // NOOP
	                }
	            }
	            if (out != null) {
	                try {
	                    out.close();
	                } catch (IOException e) {
	                    // NOOP
	                }
	            }
	        }  
	    
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
}
