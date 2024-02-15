package com.abluva.cypher.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypherdsl.core.Comparison;
import org.neo4j.cypherdsl.core.InternalPropertyImpl;
import org.neo4j.cypherdsl.core.NodeLabel;
import org.neo4j.cypherdsl.core.ProcedureCall;
import org.neo4j.cypherdsl.core.Return;
import org.neo4j.cypherdsl.core.Where;
import org.neo4j.cypherdsl.core.With;
import org.neo4j.cypherdsl.core.ast.Visitable;
import org.neo4j.cypherdsl.core.ast.Visitor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AbluvaVisitor implements Visitor {

	private Return returnClause;
	private Where whereClause;
	private Set<AbluvaExpressionList> withClause = new HashSet<>();
	private Set<ProcedureUtility> procedureUtility = new HashSet<>();

	private List<NodeLabel> nodelList = new ArrayList<>();
	private Map<String, List<ExtractedExpression>> list = new HashMap<String, List<ExtractedExpression>>();

	public static final String STR_RETURN = "return";
	public static final String STR_WHERE = "where";
	public static final String STR_WITH = "with";

	class PropertyVisitor implements Visitor {
		List<InternalPropertyImpl> propertyList = new ArrayList<>();

		@Override
		public void enter(Visitable segment) {

			if (segment instanceof InternalPropertyImpl) {
				propertyList.add((InternalPropertyImpl) segment);
			}

		}

		public List<InternalPropertyImpl> getList() {
			return propertyList;
		}

	}

	class ConditionVisitor implements Visitor {
		List<Comparison> conditionList = new ArrayList<>();

		@Override
		public void enter(Visitable segment) {

			if (segment instanceof Comparison) {
				conditionList.add((Comparison) segment);
			}
		}

		public List<Comparison> getList() {
			return conditionList;
		}
	}

	@Override
	public void enter(Visitable segment) {
		// System.out.println(segment);
		if (segment instanceof Return) {
			returnClause = (Return) segment;
		} else if (segment instanceof Where) {
			
			whereClause = (Where) segment;
		} else if (segment instanceof NodeLabel) {
			nodelList.add((NodeLabel) segment);
		} else if (segment instanceof With) {
			withClause.addAll(new AbluvaWithClauseParse((With) segment).process());

		} else if (segment instanceof ProcedureCall) {
			
			procedureUtility.add(new AbluvaProcedureParse((ProcedureCall) segment).process());

		}

	}

	public Map<String, List<ExtractedExpression>> list() {
		return list;
	}

	public Set<AbluvaExpressionList> listWithClause() {
		return withClause;

	}

	public List<NodeLabel> listAllNodes() {
		return nodelList;
	}

	public AbluvaVisitor with() {
		return processCondition().processReturn();
	}

	public AbluvaVisitor processCondition() {
		if (null != whereClause) {
			List<ExtractedExpression> returnList = new ArrayList<>();
			ConditionVisitor conditionVisitor = new ConditionVisitor();
			whereClause.accept(conditionVisitor);
			List<Comparison> conditionList = conditionVisitor.getList();
			for (Comparison comparison : conditionList) {
				ExtractedExpression property = new ExtractedExpression();
				property.setExpression(comparison);
				PropertyVisitor visitor = new PropertyVisitor();
				comparison.accept(visitor);
				if (!visitor.getList().isEmpty()) {
					property.setPropertyList(visitor.getList());
				}
				returnList.add(property);

			}
			list.put(STR_WHERE, returnList);
		}
		return this;
	}

	public AbluvaVisitor processReturn() {
		List<ExtractedExpression> returnList = new ArrayList<>();
		returnClause.getBody().getReturnItems().getExpression().forEach(expression -> {
			ExtractedExpression property = new ExtractedExpression();
			property.setExpression(expression);
			PropertyVisitor returnVisitor = new PropertyVisitor();
			expression.accept(returnVisitor);
			if (!returnVisitor.getList().isEmpty()) {
				property.setPropertyList(returnVisitor.getList());
			}
			returnList.add(property);
		});
		list.put(STR_RETURN, returnList);
		return this;
	}
}
