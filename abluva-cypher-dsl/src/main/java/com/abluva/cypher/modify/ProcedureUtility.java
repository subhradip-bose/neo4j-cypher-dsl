package com.abluva.cypher.modify;

import org.neo4j.cypherdsl.core.Arguments;
import org.neo4j.cypherdsl.core.Where;
import org.neo4j.cypherdsl.core.internal.ProcedureName;
import org.neo4j.cypherdsl.core.internal.YieldItems;

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

public class ProcedureUtility {
	private ProcedureName name;
	private Arguments arguments;
	private YieldItems yieldItems;
	private Where where;
}
