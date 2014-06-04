package com.example.networkapp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryAdapter extends ArrayAdapter<Category> {

	Context context;
	int layoutResourceId;
	List<Category> data = null;

	public CategoryAdapter(Context context, int layoutResourceId,
			List<Category> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		CategoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new CategoryHolder();
			//holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

			row.setTag(holder);
		} else {
			holder = (CategoryHolder) row.getTag();
		}

		Category category = data.get(position);
		holder.txtTitle.setText(category.getTitle());
		//holder.imgIcon.setImageResource(category.getIcon());
		holder.txtTitle.setTag( String.valueOf(category.getId()) );
		//holder.item_id.setText(category.getId());

		holder.txtTitle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				// code to be written to handle the click event
				String category_id = (String)v.getTag() ;							
				/*CharSequence text = "Hello toast! "+s;
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();*/
		        
		        Intent myIntent = new Intent(context, ItemsActivity.class);
				myIntent.putExtra("category_id", Integer.parseInt(category_id));
				context.startActivity(myIntent);
				
				((Activity) context).overridePendingTransition( R.anim.animation_enter,R.anim.animation_leave);
				
			
			}
		});

		return row;
	}
	
	public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
		//Context context = getApplicationContext();
		CharSequence text = "Hello toast!"+pos;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		System.out.print("posiciont"+pos);
	    /*Intent i = new Intent(this, ProductActivity.class);
	    i.putExtra("item_id", manager.getItemIdAtIndex(pos));
	    startActivity(i); */
	}


	static class CategoryHolder {
		ImageView imgIcon;
		TextView txtTitle;
		int item_id;
	}
}
