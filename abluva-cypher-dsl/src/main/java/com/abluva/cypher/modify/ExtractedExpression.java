package com.abluva.cypher.modify;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypherdsl.core.Expression;
import org.neo4j.cypherdsl.core.InternalPropertyImpl;

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
public class ExtractedExpression {

	private Expression expression;
	private List<InternalPropertyImpl> propertyList = new ArrayList<>();
}
