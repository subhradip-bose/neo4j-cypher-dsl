package com.abluva;

import com.abluva.cypher.modify.AbluvaCypherExtractor;
import com.abluva.cypher.modify.AbluvaCypherModifer;
import com.abluva.cypher.modify.AbluvaParserPojo;

public class ExtractUtility {
	JsonUtility utility = new JsonUtility();

	public String extractQuery(String query) {
		return extract(query).toString();
	}

	public byte[] extract(String query) {

		try {
			AbluvaCypherExtractor extractor = new AbluvaCypherExtractor(query);
			AbluvaParserPojo pojo = new AbluvaParserPojo();
			pojo.setParsedResult(extractor.extract()); 
			return utility.generate(pojo).getBytes();

		} catch (Exception e) {
			return utility.generate("PARSE_ERROR").getBytes();
		}
	}
 
	public byte[] modify(String query, String parsedStr) {

		try {

			AbluvaParserPojo input = utility.map(parsedStr);
			AbluvaCypherModifer modifier = new AbluvaCypherModifer(query, input);
			return modifier.modifyQuery().getBytes();

		} catch (Exception e) {
			return utility.generate("PARSE_ERROR").getBytes();
		}

	}

}
