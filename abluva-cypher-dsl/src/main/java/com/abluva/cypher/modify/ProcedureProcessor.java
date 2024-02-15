package com.abluva.cypher.modify;

import java.util.List;

import org.neo4j.cypherdsl.core.Expression;
import org.neo4j.cypherdsl.core.StatementBuilder.OngoingStandaloneCallWithoutArguments;
import org.neo4j.cypherdsl.core.ast.TypedSubtree;
import org.neo4j.cypherdsl.core.internal.YieldItems;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class ProcedureProcessor {

	 

	public List<Expression> yieldItems() {
		// element.accept(new ExpressionVisitor());
		//return ((YieldItems) element).get();
	return null;
	}

	public void process(ProcedureUtility procedure, OngoingStandaloneCallWithoutArguments cypher) {
		// TODO Auto-generated method stub
		
	}

}
