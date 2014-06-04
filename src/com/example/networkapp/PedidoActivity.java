package com.example.networkapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PedidoActivity extends Activity {
	
	String jsonPedido;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pedido_activity);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		  

		ListView listView = (ListView) findViewById(R.id.list_items);
		TextView totalTextView = (TextView) findViewById(R.id.total);

		PedidoAdapter adapter = new PedidoAdapter(this,
				R.layout.listview_pedido_row, MainActivity.pedido);

		Iterator<Item> iterator = MainActivity.pedido.iterator();
		float total = 0;
		Item i;
		while (iterator.hasNext()) {
			i = iterator.next();
			total = total + (i.getQuantity() * i.getPrice());
		}
		totalTextView.setText(String.valueOf(total));

		listView.setAdapter(adapter);

		Button closePedido = (Button)findViewById(R.id.close);
		closePedido.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				PedidoActivity.super.onBackPressed();
				PedidoActivity.this.overridePendingTransition( R.anim.animation_static, R.anim.animation_leave_top_back);            
			}
		});
		
		Button submitPedido = (Button) findViewById(R.id.send);
		submitPedido.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {

				try {
					jsonPedido = getJson();
					if (jsonPedido.equals("empty")){
						AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);

						// 2. Chain together various setter methods to set the dialog characteristics
						builder.setMessage("Debes Selecionar items para tu pedido");
						
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   dialog.dismiss();
					   			   PedidoActivity.super.onBackPressed();
					   			   PedidoActivity.this.overridePendingTransition( R.anim.animation_static, R.anim.animation_leave_top_back);            
					   		
					           }
					       }); 

						// 3. Get the AlertDialog from create()
						AlertDialog dialog = builder.create();
						dialog.show();
						
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);

						// 2. Chain together various setter methods to set the dialog characteristics
						builder.setMessage("Esta seguro de su pedido ?");
						
						builder.setPositiveButton("Sip", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   new SendPedidoThread().execute(jsonPedido);
					           }
					       }); 
						
						builder.setNegativeButton("Nop", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					               dialog.dismiss();
					           }
					       });

						// 3. Get the AlertDialog from create()
						AlertDialog dialog = builder.create();
						dialog.show();
						
					}
				} catch (Exception e) {

				}

			}
		});

	}
	public static String getJson() {

       if (MainActivity.pedido.size() == 0){
    	   return "empty";
       }
		List<String> list = new ArrayList<String>();
		JSONObject json = new JSONObject();

		Iterator<Item> iterator = MainActivity.pedido.iterator();
		Item i;
		while (iterator.hasNext()) {
			i = iterator.next();
			list.add("{'id':'" + String.valueOf(i.getId()) + "','quantity:'"
					+ String.valueOf(i.getQuantity()) + "'}");
		}

		try {
			json.accumulate("pedido", list);
			json.accumulate("restaurant_id", "25");
		} catch (Exception e) {

		}

		Log.i("MyActivity", json.toString());

		return json.toString();
	}
	public String sendPedido(String pedido) throws IOException {
		String query = "pedido=" + pedido;
		URLConnection connection = new URL(getResources().getString(R.string.send_pedido_url))
				.openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset=" + "UTF-8");
		OutputStream output = null;
		try {
			output = connection.getOutputStream();
			output.write(query.getBytes("UTF-8"));
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException logOrIgnore) {
				}
		}
		InputStream response = connection.getInputStream();

		return "cool";
	}

	public class SendPedidoThread extends
			AsyncTask<Object, Object, String> {

		@Override
		protected String doInBackground(Object... params) {
			try {
				return sendPedido((String) params[0]);
			} catch (Exception e) {
				return "error";
			}

		}

		// onPostExecute displays the results of the AsyncTask.
		protected void onPostExecute(String result) {
			MainActivity.pedido = new ArrayList<Item>();
			PedidoActivity.super.onBackPressed();
			PedidoActivity.this.overridePendingTransition( R.anim.animation_static, R.anim.animation_leave_top_back);            
			
			Toast toast = Toast.makeText(PedidoActivity.this, "Gracias, tu pedido ha sido enviado!", Toast.LENGTH_LONG);
			toast.show();
		}

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
	        	
	        	super.onBackPressed();
	        	this.overridePendingTransition( R.anim.animation_static, R.anim.animation_leave_top_back);            
	           		
	            return true;
            case R.id.menu_pedido:
	        	
	            //Pedido.menuPedido(this);
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
