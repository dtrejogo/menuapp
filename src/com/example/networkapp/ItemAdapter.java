package com.example.networkapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
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



public class ItemAdapter extends ArrayAdapter<Item> {

	Context context;
    int layoutResourceId;   
    List<Item> data = null;
    Dialog dialog=null;
   
    public ItemAdapter(Context context, int layoutResourceId, List<Item> data) {
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
            holder.txtDescription = (TextView)row.findViewById(R.id.txtDescription);
            holder.buttonInfo = (ImageButton)row.findViewById(R.id.buttonInfo);
            holder.txtPrice = (TextView)row.findViewById(R.id.txtPrice);
            holder.buttonAdd = (ImageButton)row.findViewById(R.id.buttonAdd);
           
            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)row.getTag();
        }
       
        Item item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtDescription.setText(item.getDescription(36));
        holder.txtPrice.setText("Bs. "+String.valueOf(item.getPrice()));
        holder.buttonInfo.setTag( String.valueOf(item.getId()) );
        holder.buttonInfo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				String item_id = (String)v.getTag() ;	
				
				ItemDataSource itemDataSource = new ItemDataSource(context);
				itemDataSource.open();

				Item item = itemDataSource.get(Integer.parseInt(item_id));
				
				itemDataSource.close();
				
				dialog = new Dialog(context);

		        dialog.setContentView(R.layout.dialog_item_detail);
		        dialog.setTitle(item.getTitle());

		        TextView text = (TextView) dialog.findViewById(R.id.text);
		        text.setText(item.getDescription());
		        
		        ImageView jpgView = (ImageView)dialog.findViewById(R.id.jpgview);
		        
		        String myJpgPath = Environment.getExternalStorageDirectory().toString() +"/"+context.getString(R.string.image_folder)+"/"+item.getImage();
			    Log.i("MyActivity" , myJpgPath);
		        
		        BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inSampleSize = 1;
		        Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
		        jpgView.setImageBitmap(bm); 
		        
		        Button closeButton = (Button) dialog.findViewById(R.id.close);
		        closeButton.setOnClickListener(new Button.OnClickListener() {      
		               public void onClick(View view) { 
		               dialog.dismiss();     
		               } 
		        });

		        
		       
                dialog.show();
				
			
			}
		});
        
        holder.buttonAdd.setTag( String.valueOf(item.getId()) );
        holder.buttonAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				String tag = (String)v.getTag() ;
				Integer item_id = Integer.parseInt(tag);
				
				ItemDataSource itemDataSource = new ItemDataSource(context);
				itemDataSource.open();

				Item item = itemDataSource.get(item_id);
				itemDataSource.close();
				
				Item oldItem = Utils.pedidoHasItem(MainActivity.pedido, item);
				if ( oldItem!=null ){
					Log.i("MyActivity", "existe item");
					//int index = MainActivity.pedido.indexOf(oldItem);
					oldItem.setQuantity(oldItem.getQuantity()+1);
					//MainActivity.pedido.remove(index);
					//MainActivity.pedido.add(index, oldItem);
					
				}else{
					Log.i("MyActivity", "item nuevo");
					MainActivity.pedido.add(item);
				}
				
				CharSequence texto = item.getTitle()+" Agregado a su pedido!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, texto, duration);
				toast.show();
				
			
			}
			});
       
        return row;
    }
    
   
    static class ItemHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtDescription;
        ImageButton buttonInfo;
        TextView txtPrice;
        ImageButton buttonAdd;
    }
}
