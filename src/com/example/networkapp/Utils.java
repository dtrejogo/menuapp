package com.example.networkapp;

import java.util.Iterator;
import java.util.List;

public class Utils {
	
	public static Item pedidoHasItem(List<Item> pedido,Item item){
		
		Iterator<Item> iterator = pedido.iterator();
		Item i;
		while (iterator.hasNext()) {
			i = iterator.next();
			if (i.getId()==item.getId())
				return i;
		}
			
		return null;
		
	}

}
