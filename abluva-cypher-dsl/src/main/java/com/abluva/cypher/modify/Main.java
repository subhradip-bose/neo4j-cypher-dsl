package com.abluva.cypher.modify;

import com.abluva.JsonUtility;

public class Main {

	 private static String query = """

	 		CALL test_proceudre('1212', 'sasas')
	 			YIELD name, signature, description as text
	 			WHERE name STARTS WITH 'db.'
	 		 		RETURN * ORDER BY name ASC
							""";
 
	//private static String query  = "MATCH (node0:`Album`) RETURN node0.Name";
	/* private static String json = """
			{"parsedResult":[{"node":{"value":"Album","alias":"node0"},"returnClause":[{"name":"node0","value":"Name","access_type":"full_mask"}],"whereClause":[]}]}
			""";
	*/
 	private static String json = """

			{"parsedResult":[{"node":{"value":"Album","alias":"node0"},"returnClause":[{"name":"node0","value":"Name","access_type":"full_mask"}],"whereClause":[]}]}	""";
 
	public static void main(String[] args) {

		JsonUtility utility = new JsonUtility();

		AbluvaCypherExtractor extractor = new AbluvaCypherExtractor(query);
		 AbluvaParserPojo pojo = new AbluvaParserPojo();
		 System.err.println(extractor.extract());

		//AbluvaParserPojo input = utility.map(json);
		//AbluvaCypherModifer modifier = new AbluvaCypherModifer(query, input);
		//System.err.println(">>>" + modifier.modifyQuery());

	}

}
