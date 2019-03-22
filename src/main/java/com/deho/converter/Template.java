package com.deho.converter;

public interface Template {

	DataHolder getData();
	
	DataHolder parseText(String[] lines);
}
