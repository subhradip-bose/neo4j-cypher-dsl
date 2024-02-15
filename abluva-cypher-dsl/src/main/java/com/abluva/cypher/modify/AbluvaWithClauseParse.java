package com.abluva.cypher.modify;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.cypherdsl.core.AliasedExpression;
import org.neo4j.cypherdsl.core.InternalPropertyImpl;
import org.neo4j.cypherdsl.core.Where;
import org.neo4j.cypherdsl.core.With;
import org.neo4j.cypherdsl.core.ast.Visitable;
import org.neo4j.cypherdsl.core.ast.Visitor;

public class AbluvaWithClauseParse {

	private With withClause;
	private Set<AbluvaExpressionList> expressionList = new HashSet<>();
	private Where whereCondition;

	public AbluvaWithClauseParse(With withClause) {

		this.withClause = withClause;
	}

	public Set<AbluvaExpressionList> process() {
		this.withClause.accept(new ExpressionVisitor());

		return expressionList;
	}

	class ExtendedVisitor implements Visitor {

		AbluvaExpressionList expression = new AbluvaExpressionList();
		String alias;
		
		public ExtendedVisitor(AbluvaExpressionList expression, String alias) {
			this.expression = expression;
			this.alias = alias;
		}

		@Override
		public void enter(Visitable segment) {

			if (segment instanceof InternalPropertyImpl) {
				InternalPropertyImpl property = (InternalPropertyImpl) segment;
				AbluvaCypherUtili util = new AbluvaCypherUtili();
				expression.setNodeName(util.extractNameFromProperty(property));
				expression.setValue(property.getName());
				expression.setRelatedToProerty(true);
				expression.setAlias(this.alias);
			}

		}

		public AbluvaExpressionList list() {
			return expression;
		}

	}

	class ExpressionVisitor implements Visitor {

		@Override
		public void enter(Visitable segment) {
			if (segment instanceof AliasedExpression) {
				AliasedExpression expression = (AliasedExpression) segment;
				AbluvaExpressionList expressionObj = new AbluvaExpressionList();
				expressionObj.setExpression(expression);
				ExtendedVisitor visitor = new ExtendedVisitor(expressionObj, expression.getAlias());
				 
				expression.accept(visitor);
				expressionList.add(visitor.list());
			} else if (segment instanceof Where) {
				whereCondition = (Where) segment;
			}
		}

	}

}
