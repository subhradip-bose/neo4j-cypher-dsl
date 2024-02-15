package com.abluva;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ABFunction {

	private Map<String, String> functionList = new LinkedHashMap<>();

	public ABFunction() {
		functionList.put("full_mask", "REDUCE(s = '', c IN SPLIT(%s.%s, '') | s + '%s')");
		functionList.put("sha256", "apoc.util.sha256(['%s.%s'])");
	}
 
	public String getDefination(String key) throws Exception {
		if (null == key || key.trim().length() == 0) {
			throw new Exception("Function name cant be empty");
		} else if (!functionList.containsKey(key)) {
			throw new Exception(String.format(" Function name (%s) not found", key));
		} else {
			return functionList.get(key.trim());
		}

	}
}
