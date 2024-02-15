package com.abluva;

import com.abluva.cypher.modify.AbluvaParserPojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonUtility {

	private GsonBuilder builder = new GsonBuilder();

	public String generate(Object obj) {
		Gson gson = builder.create();
		return gson.toJson(obj);
	}

	public AbluvaParserPojo map(String str) {
		return builder.create().fromJson(str, AbluvaParserPojo.class);
	}

}
