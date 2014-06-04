/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.networkapp;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses XML feeds from stackoverflow.com.
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class MenuXmlParser {
    private static final String ns = null;
    private static final String TAG = "MyActivity";
    
    // Read the root of XML
    public String parseResponse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, ns, "root");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("error")) {
                	return readString(parser,"error");
                }else{
                    skip(parser);
                }
                
            }
            
            return "success";
        } finally {
            in.close();
        }
    }

    // Read the root of XML
    public Root parseRoot(InputStream in) throws XmlPullParserException, IOException {
        try {
        	Log.i(TAG, "Start to read from root");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private Root readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Category> categories = new ArrayList<Category>();
        Config config = null;
        Root root = null;
        Log.i(TAG, "readFeed root");
        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("category")) {
            	categories.add(readEntry(parser));
            }else if (name.equals("config")) {
                config = readConfig(parser);
            }else{
                skip(parser);
            }
            
            root = new Root();
            root.setCategories(categories);
            root.setConfig(config);
        }
        return root;
    }
    
    private Config readConfig(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Item> items = new ArrayList<Item>();
    	parser.require(XmlPullParser.START_TAG, ns, "config");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
            	items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        
        for (Item configItem : items) {
			Log.i(TAG, configItem.getKey() +" -> "+configItem.getValue());
		}
        
        Config config = new Config();
        config.setItems(items);
        
        return config;
        
    }
    

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private Category readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String title = null;
        List<Item> items = new ArrayList<Item>();
        String link = null;
        int id=0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                id = Integer.parseInt(readId(parser));
            }else if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("item")) {
               items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        Category category = new Category();
        category.setId(id);
        category.setTitle(title);
        category.setItems(items);
        return category;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }
    
    // Processes title tags in the feed.
    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return id;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private Item readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String description = null;
        String price = "";
        String imageUrl = null;
        String key = null;
        String value = null;
        Item item= null;
        int id =0;
        item = new Item();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
           
            String name = parser.getName();
            if (name.equals("id")) {
                item.setId( Integer.parseInt(readId(parser) ) );
            }else if (name.equals("title")) {
                item.setTitle( readTitle(parser) );
            } else if (name.equals("description")) {
            	item.setDescription( readDescription(parser) );
            } else if (name.equals("price")) {
            	item.setPrice( Float.parseFloat( readPrice(parser) ) );
            }else if(name.equals("image_url")){
            	item.setImageUrl( readImageUrl(parser) );
            }else if(name.equals("key")){
            	item.setKey( readString(parser,"key") );
            }else if(name.equals("value")){
            	item.setValue( readString(parser,"value") );
            }else {
                skip(parser);
            }
        	
        }
        Log.i(TAG,"key/value leido: "+item.getKey()+"/"+item.getValue());
        
        //types of food
       /* Item item = new Item();
        item.setId(id);
        item.setTitle(title);
        item.setDescription(description);
        item.setImageUrl(imageUrl);
        
        //config items
        item.setImageUrl(key);
        item.setImageUrl(value); */
        
        return item;
    }
       
    private String readImageUrl(XmlPullParser parser)throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "image_url");
        String imageUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "image_url");
        return imageUrl;
    }

    private String readPrice(XmlPullParser parser)throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "price");
        String price = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "price");
        return price;
    }

	private String readDescription(XmlPullParser parser)throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }
	private String readString(XmlPullParser parser,String key)throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, key);
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, key);
        return value;
    }

	// For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
