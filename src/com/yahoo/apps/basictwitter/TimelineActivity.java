package com.yahoo.apps.basictwitter;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.basictwitter.models.Tweet;

import eu.erikw.PullToRefreshListView;

public class TimelineActivity extends Activity {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private PullToRefreshListView lvTweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		populateTimeline();
		
		lvTweets = (PullToRefreshListView)findViewById(R.id.lvTweets);
		setupViews(lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	public void setupViews(PullToRefreshListView lvTweets) {
		lvTweets.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				aTweets.clear();
				populateTimeline();
			}
		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (totalItemsCount >= 2) {
					Log.d("total", totalItemsCount + "");
					getMoreData(String.valueOf(aTweets.getItem(
							totalItemsCount - 2).getUid()));
				}
			}
		});
	}

	public void getMoreData(String maxId) {
		client.getMoreData(maxId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				aTweets.addAll(Tweet.fromJSONArray(jsonArray));
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s);
			}
		});
	}

	public void onCompose(MenuItem mi) {
		Intent intent = new Intent(this, ComposeActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			aTweets.clear();
			populateTimeline();
		}
	}
	
	public void populateTimeline() {
		client.getHomeTimeline(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray json) {
				aTweets.addAll(Tweet.fromJSONArray(json));
				lvTweets.onRefreshComplete();
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		});
	}
}
