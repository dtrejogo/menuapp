package com.example.networkapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.example.networkapp.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.networkapp.Category;

public class MainActivity extends Activity {
	private static final String TAG = "MyActivity";
	TextView textView;

	public static final String serverUrl = "http://dtrejogo.com/menuapp/admin/mobile/categoria/get_xml";

	private CategoryDataSource categoryDataSource;
	private ItemDataSource itemDataSource;
	
	ProgressDialog dialog;
	
	Dialog dialogSettings=null;
	
	public static Dialog dialogPedido=null;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	 

	public static List<Item> pedido= new ArrayList<Item>();
	 
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListView listView = (ListView) findViewById(R.id.list);

		categoryDataSource = new CategoryDataSource(this);
		categoryDataSource.open();

		List<Category> values = categoryDataSource.getAllCategories();

		CategoryAdapter adapter = new CategoryAdapter(this,
				R.layout.listview_category_row, values);
		
		listView.setAdapter(adapter);
		
		categoryDataSource.close();
		
		  // We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	    /*  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("email", "danny@hotmail.com");

	      // Commit the edits!
	      editor.commit(); 
*/
	}

	public void next(View view) {

		Intent myIntent = new Intent(view.getContext(), ItemsActivity.class);
		myIntent.putExtra("category_id", 2);
		startActivityForResult(myIntent, 0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:

	        	dialogSettings = new Dialog(this);
	        	dialogSettings.setContentView(R.layout.dialog_settings);
	        	dialogSettings.setTitle("Configuraci—n");
	        	
	        	Button closeButton = (Button) dialogSettings.findViewById(R.id.close);
		        closeButton.setOnClickListener(new Button.OnClickListener() {      
		               public void onClick(View view) { 
		            	   dialogSettings.dismiss();     
		               } 
		        });
		        
		        Button loginButton = (Button) dialogSettings.findViewById(R.id.login);
		        loginButton.setOnClickListener(new Button.OnClickListener() {      
		               public void onClick(View view) {
		            	   EditText email = (EditText) dialogSettings.findViewById(R.id.email);
		            	   EditText password = (EditText) dialogSettings.findViewById(R.id.password);
		            	   
		            	   authenticateUser(email.getText(), password.getText());
		            	  
		            	   
		            	   dialogSettings.dismiss();     
		               }

					
		        });
		        
		       /* SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			     String user = settings.getString("restaurant_user", "none");
			     String passw = settings.getString("restaurant_password", "none");
			       
		 	      Toast toast = Toast.makeText(getApplicationContext(), user + "/"+ passw, Toast.LENGTH_SHORT);
				  toast.show();*/
		       

	        	dialogSettings.show();
	            return true;
	        case R.id.menu_pedido:
	        	
	        	Intent myIntent = new Intent(this, PedidoActivity.class);
	    		startActivityForResult(myIntent, 0);
	        	
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	protected void authenticateUser(Editable email,Editable password) {
		// TODO Auto-generated method stub
			       
	      /* toast = Toast.makeText(getApplicationContext(), email + " "+password, Toast.LENGTH_SHORT);
		  toast.show(); */
		  
		  String myUrl = serverUrl + "?email=dannytrejo@gmail.com&password=12345"; 
		  Log.i(TAG,myUrl);
			try {
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					new DownloadWebpageText().execute(myUrl);
					Log.i(TAG, "Hay conex llamo hilo.");
				    dialog = ProgressDialog.show(MainActivity.this, "", "Cargando. Por favor espere...", true);						
					
				} else {
					// textView.setText("No network connection available.");
				}
			} catch (Exception e) {
				Log.i(TAG, "Kaboom");
			}
		
		
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class DownloadWebpageText extends AsyncTask<Object, Object, String> {

		@Override
		protected String doInBackground(Object... params) {
			
			Log.i(TAG, "Arranco hilo. doInBackground");
		   try {
				return loadXmlFromNetwork((String) params[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				return getResources().getString(R.string.xml_error);
			}

		}

		// onPostExecute displays the results of the AsyncTask.
		protected void onPostExecute(String result) {
			Log.i(TAG, "aqui termima " + result);
			dialog.dismiss();
			
			 Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
			 toast.show();
			 finish();
			 startActivity(getIntent()); 


		}

	}

	// Uploads XML from stackoverflow.com, parses it, and combines it with
	// HTML markup. Returns HTML string.
	private String loadXmlFromNetwork(String urlString)
			throws XmlPullParserException, IOException {
		Log.i(TAG, "loadXmlFromNetwork");
		InputStream response = null;
		InputStream stream = null;
		MenuXmlParser menuXmlParser = new MenuXmlParser();
		List<Category> categories = null;
		Root root =  null;
		String result = null;

		try {
			Log.i(TAG, "try download Url: "+ urlString);
			
			/*
			 *  2 Conexiones para obtener la respuesta "Success or Error"
			 *  y otra para parsear el xml.
			 */
		
			stream = downloadUrl(urlString); 
			response = downloadUrl(urlString);
			result = menuXmlParser.parseResponse(response);
			if (result.equals("success")){
				root = menuXmlParser.parseRoot(stream);
			}else{
				return result;
			}
			
			//root = menuXmlParser.parseRoot(stream);
			
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		/*Config config = root.getConfig();
		List<Item> configItems = config.getItems();
		
		String values = "";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    

	      // Commit the edits!
	      editor.commit(); 
		for (Item configItem : configItems) {
			values += configItem.getKey() +" -> "+configItem.getValue();
			editor.putString(configItem.getKey(), configItem.getValue());
		}
		editor.commit(); */
		
		  

		/*MySQLiteHelper dbHelper = new MySQLiteHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		dbHelper.onUpgrade(db, 0, 1);
		categoryDataSource = new CategoryDataSource(this);
		categoryDataSource.open();
		itemDataSource = new ItemDataSource(this);
		itemDataSource.open();
 
		categories = root.getCategories();
		
		for (Category category : categories) {
			Category newCategory = categoryDataSource.createCategory(category);
			for (Item item : category.getItems()) {
				item.setCategoryId(newCategory.getId());
				itemDataSource.createItem(item);
				
			}
		}
		
		dbHelper.close();*/
		return result;
	}

	// Given a string representation of a URL, sets up a connection and gets
	// an input stream.
	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
	}

}
