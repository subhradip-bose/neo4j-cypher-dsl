package com.abluva.cypher.modify;

import org.neo4j.cypherdsl.core.InternalPropertyImpl;
import org.neo4j.cypherdsl.core.SymbolicName;

public class AbluvaCypherUtili {

	public String propertyToString(InternalPropertyImpl property) {
		SymbolicName symbolicName = (SymbolicName) property.getContainerReference();
		return String.format("%s.%s", symbolicName.getValue(), property.getName());
	}

	public String extractNameFromProperty(InternalPropertyImpl property) {
		SymbolicName symbolicName = (SymbolicName) property.getContainerReference();
		return symbolicName.getValue();
	}
	 
	
}
