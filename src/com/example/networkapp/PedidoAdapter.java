package com.example.networkapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class PedidoAdapter extends ArrayAdapter<Item> {

	Context context;
    int layoutResourceId;   
    List<Item> data = null;
    Dialog dialog=null;
   
    public PedidoAdapter(Context context, int layoutResourceId, List<Item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;    
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new ItemHolder();
           
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtQuantity = (TextView)row.findViewById(R.id.txtQuantity);
            holder.buttonRemove = (ImageButton)row.findViewById(R.id.buttonRemove);
            holder.txtPrice = (TextView)row.findViewById(R.id.txtPrice);
           
            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)row.getTag();
        }
       
        Item item = data.get(position);
        item.setPosition(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.txtPrice.setText("Bs. "+String.valueOf(item.getQuantity() * item.getPrice()));
        holder.buttonRemove.setTag( String.valueOf(item.getId()) );
        holder.buttonRemove.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				String item_id = (String)v.getTag() ;	
				
				ItemDataSource itemDataSource = new ItemDataSource(context);
				itemDataSource.open();

				Item item = itemDataSource.get(Integer.parseInt(item_id));
				itemDataSource.close();
				
				item = Utils.pedidoHasItem(MainActivity.pedido, item);
				if (item.getQuantity()>1){
					item.setQuantity(item.getQuantity()-1);
				}else{
					MainActivity.pedido.remove(item);
				}
				
				Iterator<Item> iterator = MainActivity.pedido.iterator();
	    		float total = 0;
	    		Item i;
	    		while (iterator.hasNext()) {
	    			i = iterator.next();
	    			total = total + (i.getQuantity() * i.getPrice());
	    		}
	    		
	    		TextView totalTextView = (TextView) ((Activity) context).findViewById(R.id.total);
	    		totalTextView.setText(String.valueOf(total));

				
					
			    notifyDataSetChanged();
			    
				Toast toast = Toast.makeText(context, item.getTitle()+ " eliminado del pedido!", Toast.LENGTH_SHORT);
				toast.show();
			
			}
		});
        
  
       
        return row;
    }
    
   
    static class ItemHolder
    {
        TextView txtTitle;
        TextView txtQuantity;
        ImageButton buttonRemove;
        TextView txtPrice;
    }
}
