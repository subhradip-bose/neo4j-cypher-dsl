package com.abluva.cypher.modify;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.cypherdsl.core.NodeLabel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AbluvaCypherFormatter {
	
	
	private NodeLabel node;

	private Set<AbluvaProperty> returnClause = new HashSet<>();

	private Set<AbluvaProperty> whereClause = new HashSet<>();

}
