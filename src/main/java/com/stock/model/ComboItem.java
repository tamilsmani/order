package com.stock.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ComboItem {
	String key;
	String value;
	String token;
	String lotSize;
	
	@Override
	public String toString() {
		return value;
	}
}