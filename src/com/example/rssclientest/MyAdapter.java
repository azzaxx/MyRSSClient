package com.example.rssclientest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<RssInfo> {
	Context context;
	List<RssInfo> rssinfo;

	public MyAdapter(Context context, int res, List<RssInfo> rssinfo) {
		super(context, res, rssinfo);
		this.context = context;
		this.rssinfo = rssinfo;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RssInfo rss = getItem(position);

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item, parent, false);
		}

		TextView txtView = (TextView) convertView
				.findViewById(R.id.textViewInListItem);

		if (position == 1) {
			Log.d("MyLog", "Guid:" + rss.getPostGuid());
			Log.d("MyLog", "postThumbUrl:" + rss.getPostThumbUrl());
			Log.d("MyLog", "postTitle:" + rss.getPostTitle());
			Log.d("MyLog", "postLink:" + rss.getPostLink());
			Log.d("MyLog", "postContent:" + rss.getPostContent());
		}

		txtView.setText(rss.getPostGuid());

		return convertView;
	}
}
