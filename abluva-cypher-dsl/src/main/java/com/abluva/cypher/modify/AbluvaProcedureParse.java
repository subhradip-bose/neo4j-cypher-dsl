package com.abluva.cypher.modify;

import org.neo4j.cypherdsl.core.Arguments;
import org.neo4j.cypherdsl.core.ProcedureCall;
import org.neo4j.cypherdsl.core.Where;
import org.neo4j.cypherdsl.core.ast.Visitable;
import org.neo4j.cypherdsl.core.ast.Visitor;
import org.neo4j.cypherdsl.core.internal.ProcedureName;
import org.neo4j.cypherdsl.core.internal.YieldItems;

public class AbluvaProcedureParse {

	private ProcedureCall items;
	private ProcedureUtility procedure = new ProcedureUtility();

	public AbluvaProcedureParse(ProcedureCall items) {

		this.items = items;
	}

	public ProcedureUtility process() {
		this.items.accept(new ExpressionVisitor());
		return procedure;
	}

	class ExpressionVisitor implements Visitor {

		@Override
		public void enter(Visitable segment) {
			if (segment instanceof YieldItems) {
				procedure.setYieldItems((YieldItems) segment);
			} else if (segment instanceof ProcedureName) {
				procedure.setName((ProcedureName) segment);
			} else if (segment instanceof Where) {
				procedure.setWhere((Where) segment);
			} else if (segment instanceof Arguments) {
				procedure.setArguments((Arguments) segment);
			}

			 
		}

	}

}
