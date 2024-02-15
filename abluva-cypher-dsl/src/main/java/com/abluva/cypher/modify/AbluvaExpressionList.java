package com.abluva.cypher.modify;

import org.neo4j.cypherdsl.core.Expression;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class AbluvaExpressionList {

	private boolean relatedToProerty = false;
	private Expression expression;
	private String nodeName;
	private String value;
	private String alias;

}
