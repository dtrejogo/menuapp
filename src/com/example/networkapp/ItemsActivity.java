package com.example.networkapp;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ItemsActivity extends Activity {

	private static final String CATEGORY_ID = "category_id";

	/*
	 * called when the activity starts (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.items_activity);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		int category_id = this.getIntent().getIntExtra(CATEGORY_ID, 0);

		ListView listView = (ListView) findViewById(R.id.list_items);

		ItemDataSource itemDataSource = new ItemDataSource(this);
		itemDataSource.open();

		List<Item> values = itemDataSource.getItemsByCategory(category_id);

		ItemAdapter adapter = new ItemAdapter(this, R.layout.listview_item_row,
				values);

		listView.setAdapter(adapter);
		itemDataSource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:

			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			this.overridePendingTransition(R.anim.animation_enter_back,
					R.anim.animation_leave_back);

			return true;
		case R.id.menu_pedido:

			Intent myIntent = new Intent(this, PedidoActivity.class);
			startActivityForResult(myIntent, 0);

			this.overridePendingTransition(R.anim.animation_enter_top,
					R.anim.animation_static);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
