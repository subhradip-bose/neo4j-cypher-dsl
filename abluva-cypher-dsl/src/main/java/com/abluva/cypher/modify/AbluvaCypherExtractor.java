package com.abluva.cypher.modify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.InternalPropertyImpl;
import org.neo4j.cypherdsl.core.NodeLabel;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.cypherdsl.core.StatementBuilder.OngoingStandaloneCallWithoutArguments;
import org.neo4j.cypherdsl.core.SymbolicName;
import org.neo4j.cypherdsl.core.renderer.Renderer;

import com.querydsl.core.types.Expression;

import lombok.ToString;

@ToString
public class AbluvaCypherExtractor {

	private String cypherQuery;
	private List<AbluvaCypherFormatter> resultList = new ArrayList<>();
	private List<NodeLabel> nodeList = new ArrayList<>();
	private Set<AbluvaExpressionList> withCluaseList = new HashSet<>();
	private Set<ProcedureUtility> procedureList = new HashSet<>();
	
	private static final Renderer cypherRenderer = Renderer.getDefaultRenderer();

	
	public AbluvaCypherExtractor(String cypherQuery) {
		this.cypherQuery = cypherQuery;
	}

	public Set<AbluvaExpressionList> list() {
		return withCluaseList;
	}
	
	public Map<String, List<ExtractedExpression>> getExtractedExpression() {
		AbluvaVisitor visitor = new AbluvaVisitor();
		Statement statement = ExtendedParser.parseStatement(this.cypherQuery);
		statement.accept(visitor);
		this.procedureList = visitor.getProcedureUtility();
		this.nodeList = visitor.getNodelList();
		withCluaseList = visitor.listWithClause();
		ProcedureProcessor convertToExpression = new ProcedureProcessor();
		this.procedureList.forEach(procedure -> {
			OngoingStandaloneCallWithoutArguments cypher = Cypher.call(procedure.getName().getValue());
			convertToExpression.process(procedure, cypher);
			cypher.withArgs(procedure.getArguments().get());
			 
			System.err.println(">>--"+ cypher.yield("<<VALUE 1>>", "<<VALUE 2>>").build().getCypher());
			 
			
		});
		
		return visitor.with().list();

	}

	
	
	public String getSourceProperty(String nodeValue, String aliasName) {

		if (!withCluaseList.isEmpty()) {
			for (AbluvaExpressionList with : withCluaseList) {
				if (with.isRelatedToProerty() && with.getNodeName().equalsIgnoreCase(nodeValue)
						&& with.getAlias().equalsIgnoreCase(aliasName)) {
					return with.getValue();
				}
			}

		}
		return "";
	}

	public List<AbluvaCypherFormatter> extract() {
		AbluvaCypherUtili util = new AbluvaCypherUtili();
		Map<String, List<ExtractedExpression>> propertyList = this.getExtractedExpression();
		
		nodeList.stream().forEach(node -> {

			AbluvaCypherFormatter formatter = new AbluvaCypherFormatter();
			formatter.setNode(node);
			Set<AbluvaProperty> returnClause = new HashSet<>();
			Set<AbluvaProperty> conditionList = new HashSet<>();

			if (propertyList.containsKey(AbluvaVisitor.STR_RETURN)) {

				propertyList.get(AbluvaVisitor.STR_RETURN).stream().forEach(returnStmt -> {

					if (!returnStmt.getPropertyList().isEmpty()) {
						for (InternalPropertyImpl individual : returnStmt.getPropertyList()) {

							if (util.extractNameFromProperty(individual) == node.getAlias()) {
								returnClause.add(new AbluvaProperty(util.extractNameFromProperty(individual),
										individual.getName(), null));

							}

						}
					} else {
						if (returnStmt.getExpression() instanceof SymbolicName) {
							SymbolicName symbolicName = (SymbolicName) returnStmt.getExpression();
							String propertyName = getSourceProperty(node.getAlias(), symbolicName.getValue());
							 if (!propertyName.isEmpty()) {
									returnClause.add(new AbluvaProperty(node.getAlias(),
											propertyName, null));
							 }
						}
					}

				});

			}
			if (propertyList.containsKey(AbluvaVisitor.STR_WHERE)) {

				propertyList.get(AbluvaVisitor.STR_WHERE).stream().forEach(returnStmt -> {
					returnStmt.getPropertyList().stream()
							.filter(k -> util.extractNameFromProperty(k) == node.getAlias()).forEach(k -> {
								conditionList
										.add(new AbluvaProperty(util.extractNameFromProperty(k), k.getName(), null));

							});

				});

			}

			 

			formatter.setWhereClause(conditionList);
			formatter.setReturnClause(returnClause);
			resultList.add(formatter);
		});

		return resultList;
	}
}
