package com.example.rssclientest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class StartActivity extends Activity {

	private ListView myListView;
	private MyAdapter myAdapter;
	private ArrayList<RssInfo> rssinfoAList = new ArrayList<RssInfo>();

	private enum RSSXMLTag {
		TITLE, DATE, LINK, CONTENT, GUID, IGNORETAG;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myListView = (ListView) findViewById(R.id.listView);

		Intent intent = getIntent();

		new HTTPConnection().execute(intent.getStringExtra("URL"));

		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				RssInfo data = rssinfoAList.get(pos);
				Bundle postInfo = new Bundle();
				postInfo.putString("content", data.getPostContent());
				Intent postviewIntent = new Intent(StartActivity.this,
						Web_view.class);
				postviewIntent.putExtras(postInfo);
				startActivity(postviewIntent);
			}
		});

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				myAdapter.notifyDataSetChanged();
				handler.postDelayed(this, 60 * 3000);
				Log.d("MyLog", "tick..");
			}
		}, 60 * 3000);
	}

	public class HTTPConnection extends
			AsyncTask<String, Void, ArrayList<RssInfo>> {
		XmlPullParserFactory xmlFactoryObject;
		HttpURLConnection urlConnection = null;
		private RSSXMLTag currentTag;
		RssInfo rss;

		protected ArrayList<RssInfo> doInBackground(String... params) {

			ArrayList<RssInfo> postDataList = new ArrayList<RssInfo>();
			try {
				URL url = new URL(params[0]);

				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setReadTimeout(10000);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				xmlFactoryObject = XmlPullParserFactory.newInstance();
				XmlPullParser myparser = xmlFactoryObject.newPullParser();

				myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
						false);
				myparser.setInput(inputStream, null);

				int eventType = myparser.getEventType();
				RssInfo rssinfo = null;

				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_DOCUMENT) {
					} else if (eventType == XmlPullParser.START_TAG) {

						if (myparser.getName().equals("item")) {
							rssinfo = new RssInfo();
							currentTag = RSSXMLTag.IGNORETAG;
						} else if (myparser.getName().equals("title")) {
							currentTag = RSSXMLTag.TITLE;
						} else if (myparser.getName().equals("link")) {
							currentTag = RSSXMLTag.LINK;
						} else if (myparser.getName().equals("pubDate")) {
							currentTag = RSSXMLTag.DATE;
						} else if (myparser.getName().equals("description")) {
							currentTag = RSSXMLTag.CONTENT;
						} else if (myparser.getName().equals("guid")) {
							currentTag = RSSXMLTag.GUID;
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if (myparser.getName().equals("item")) {
							postDataList.add(rssinfo);
						} else {
							currentTag = RSSXMLTag.IGNORETAG;
						}
					} else if (eventType == XmlPullParser.TEXT) {
						String content = myparser.getText();
						content = content.trim();
						if (rssinfo != null) {
							switch (currentTag) {

							case TITLE:
								if (content.length() != 0) {
									if (rssinfo.getPostTitle() != null) {
										String s = rssinfo.getPostTitle();
										rssinfo.setPostTitle(s += content);
									} else {
										rssinfo.setPostTitle(content);
									}
								}
								break;
							case LINK:
								if (content.length() != 0) {
									if (rssinfo.getPostLink() != null) {
										String s = rssinfo.getPostLink();
										rssinfo.setPostTitle(s += content);
									} else {
										rssinfo.setPostTitle(content);
									}
								}
								break;
							case CONTENT:
								if (content.length() != 0) {
									if (rssinfo.getPostContent() != null) {
										String s = rssinfo.getPostContent();
										rssinfo.setPostContent(s += content);
									} else {
										rssinfo.setPostContent(content);
									}
								}
								break;
							case DATE:
								if (content.length() != 0) {
									if (rssinfo.getPostData() != null) {
										String s = rssinfo.getPostData();
										rssinfo.setPostData(s += content);
									} else {
										rssinfo.setPostData(content);
									}
								}
								break;
							case GUID:
								if (content.length() != 0) {
									if (rssinfo.getPostGuid() != null) {
										String s = rssinfo.getPostGuid();
										rssinfo.setPostGuid(s += content);
									} else {
										rssinfo.setPostGuid(content);
									}
								}
								break;
							default:
								break;
							}
						}
					}
					eventType = myparser.next();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return postDataList;
		}

		@Override
		protected void onPostExecute(ArrayList<RssInfo> result) {
			if (result.size() == 0) {
				Toast toast = Toast
						.makeText(
								getApplicationContext(),
								"Oops!..wrong URL or maybe there is no internet connection",
								Toast.LENGTH_SHORT);
				toast.show();
				finish();
			}
			for (int i = 0; i < result.size(); i++) {
				rssinfoAList.add(result.get(i));
			}

			myAdapter = new MyAdapter(StartActivity.this,
					R.layout.activity_main, rssinfoAList);
			myListView.setAdapter(myAdapter);
			super.onPostExecute(result);
		}
	}
}
