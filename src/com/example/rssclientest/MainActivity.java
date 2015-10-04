package com.example.rssclientest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button b;
	private EditText et;
	private String URL;
	private ListView lv;
	private SimpleAdapter adapter;
	private ArrayList<Map<String, String>> list;
	private HashMap<String, String> map;
	private final String tableName = "RssURL";
	private MySQLiteDb myDb = new MySQLiteDb(this, tableName, null, 1);
	private SQLiteDatabase db;
	private Cursor cursor;
	private ContentValues values;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);

		lv = (ListView) findViewById(R.id.listView);
		et = (EditText) findViewById(R.id.editText);
		b = (Button) findViewById(R.id.addRssBut);

		list = new ArrayList<Map<String, String>>();

		db = myDb.getWritableDatabase();
		cursor = db.query(tableName, null, null, null, null, null, null);

		if (cursor != null && cursor.getCount() != 0) {
			cursor.moveToFirst();
			int URLIndex = cursor.getColumnIndex("URL");
			do {
				String URL2 = cursor.getString(URLIndex);
				map = new HashMap<String, String>();
				map.put("URL", URL2);
				list.add(map);
			} while (cursor.moveToNext());
		}

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				String url = ((TextView) (view
						.findViewById(R.id.textViewInListItem))).getText()
						.toString();
				Intent intent = new Intent(MainActivity.this,
						StartActivity.class);
				intent.putExtra("URL", url);
				startActivity(intent);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				String del = "";
				db = myDb.getWritableDatabase();
				cursor.moveToFirst();
				cursor.moveToPosition(pos);
				int index = cursor.getColumnIndex("URL");
				del = "URL = '" + cursor.getString(index) + "'";
				db.delete(tableName, del, null);
				db.close();
				list.remove(pos);
				adapter.notifyDataSetChanged();
				Toast toast = Toast.makeText(getApplicationContext(),
						"Removed..", Toast.LENGTH_SHORT);
				toast.show();
				cursor.moveToFirst();
				return false;
			}
		});

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				values = new ContentValues();

				if (et.getText().length() > 1) {
					db = myDb.getWritableDatabase();
					map = new HashMap<String, String>();
					URL = "http://" + et.getText().toString();
					map.put("URL", URL);
					values.put("URL", URL);
					db.insert(tableName, null, values);
					list.add(map);
					db.close();
					adapter.notifyDataSetChanged();
				}
			}
		});

		adapter = new SimpleAdapter(this, list, R.layout.list_item,
				new String[] { "URL" }, new int[] { R.id.textViewInListItem });
		lv.setAdapter(adapter);
		db.close();
	}
}
