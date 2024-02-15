package com.abluva.cypher.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.neo4j.cypherdsl.core.Clauses;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Expression;
import org.neo4j.cypherdsl.core.InternalPropertyImpl;
import org.neo4j.cypherdsl.core.Return;
import org.neo4j.cypherdsl.core.SymbolicName;
import org.neo4j.cypherdsl.parser.CypherParser;
import org.neo4j.cypherdsl.parser.Options;
import org.neo4j.cypherdsl.parser.ReturnDefinition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AbluvaCypherModifer {

	private String cypherQuery;
	private AbluvaParserPojo parsedRequest = new AbluvaParserPojo();
	AbluvaCypherUtili cypherUtil = new AbluvaCypherUtili();

	public AbluvaCypherModifer(String cypherQuery) {
		this.cypherQuery = cypherQuery;
	}

	public AbluvaCypherModifer(String cypherQuery, AbluvaParserPojo input) {
		this.parsedRequest = input;
		this.cypherQuery = cypherQuery;
	}

	private String checkForAccess(String name, String value) {
		List<AbluvaCypherFormatter> parsedResult = this.parsedRequest.getParsedResult();
		for (AbluvaCypherFormatter result : parsedResult) {
			for (AbluvaProperty access : result.getReturnClause()) {
				if (access.getName().equals(name) && access.getValue().equals(value)) {
					return access.getAccess_type();
				}
			}
		}
		return "NoAccess";
	}

	public String replace(Expression expresssion) {
		
		String expr = expresssion.toString();
		String alias = "";
		 
		if (expresssion instanceof InternalPropertyImpl) {
			InternalPropertyImpl property = (InternalPropertyImpl) expresssion;
			
			SymbolicName symbolicName = (SymbolicName) property.getContainerReference();
			System.out.println(">>> "+property.getName() + " "+ symbolicName.getValue());
			if (!symbolicName.getValue().isEmpty() && !property.getName().isEmpty()) {
				alias = String.format(" as `%s.%s` ", symbolicName.getValue(),  property.getName());
			}
			else if (symbolicName.getValue().isEmpty() && !property.getName().isEmpty()) {
				alias = String.format(" as `%s` ",  property.getName());
			}
			
			 
		}
		Map<String, String> accessList = getAccess();
		for (Entry<String, String> entry : accessList.entrySet()) {

			if (accessList.containsKey(entry.getKey())) {
				if (!accessList.get(entry.getKey()).equalsIgnoreCase("NoAccess")) {
					expr = expr.replace(entry.getKey(), entry.getValue());

				}

			}
			
		}
	 
		if (!alias.isEmpty()) {
			expr = expr + alias ;
			//Name__Album_
		}
		return expr;
	}

	public boolean hasAccess(List<InternalPropertyImpl> propertyList) {

		for (InternalPropertyImpl property : propertyList) {

			String name = cypherUtil.extractNameFromProperty(property);
			String value = property.getName();
			// name = n value = name
			if (!checkForAccess(name, value).equals("NoAccess")) {
				return true;
			}
		}
		return false;
	}

	public String applyFunction(String access, String replaceWith) {

		if (access.equalsIgnoreCase("full_mask")) {
			return String.format("REDUCE(s = '', c IN SPLIT(%s, '') | s + '%s')", replaceWith, "*");
		} else if (access.equalsIgnoreCase("sha256")) {
			return String.format("apoc.util.sha256(['%s'])", replaceWith);

		}

		else {
			return replaceWith;
		}
	}

	public Map<String, String> getAccess() {
		Map<String, String> result = new HashMap<>();
		this.parsedRequest.getParsedResult().stream().forEach(k -> {
			k.getReturnClause().stream().forEach(b -> {
				String pattern = String.format("%s.%s", b.getName(), b.getValue());
				result.put(pattern, applyFunction(b.getAccess_type(), pattern));
			});
		});
		return result;
	}

	
	@Getter
	@Setter
	@NoArgsConstructor
	public class FinalResult {
		Expression expresion;
		boolean hasAcess;
	}
	
	public Set<String> processRetun() {
		Set<String> returnList = new HashSet<>();
		AbluvaCypherExtractor extractor = new AbluvaCypherExtractor(cypherQuery);
		Map<String, List<ExtractedExpression>> parsedResult = extractor.getExtractedExpression();
		
		if (! extractor.list().isEmpty()) {
			
		}
		
		
		if (parsedResult.containsKey(AbluvaVisitor.STR_RETURN)) {
			
			parsedResult.get(AbluvaVisitor.STR_RETURN).stream().forEach(k -> {
				 
				if (hasAccess(k.getPropertyList())) {
					String str = replace(k.getExpression());
					returnList.add(str);
				}
				
			});

		}
		return returnList;
	}

	private Function<ReturnDefinition, Return> modifyReturn() {
		Function<ReturnDefinition, Return> returnClauseFactory = d -> {
			List<Expression> templIst = new ArrayList<>();
			Set<String> returnList = processRetun();
			 
			 
			returnList.stream().forEach(statement -> {
				templIst.add(Cypher.literalOf(new AbluvaLiteral(statement)));

			});

			return Clauses.returning(d.isDistinct(), templIst, d.getOptionalSortItems(), d.getOptionalSkip(),
					d.getOptionalLimit());
		};
		return returnClauseFactory;
	}

	public String modifyQuery() {
		Function<ReturnDefinition, Return> returnClauseFactory = this.modifyReturn();
		String parser = CypherParser
				.parse(cypherQuery, Options.newOptions().withReturnClauseFactory(returnClauseFactory).build())
				.getCypher();
		return parser;
	}

}
