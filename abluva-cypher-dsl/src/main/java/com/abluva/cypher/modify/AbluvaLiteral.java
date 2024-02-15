package com.abluva.cypher.modify;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.neo4j.cypherdsl.core.LiteralBase;

public class AbluvaLiteral extends LiteralBase<CharSequence> {

	public AbluvaLiteral(String statement) {
		super(statement.toString());
	}

	@NotNull
	@Override
	public String asString() {
		return String.format(Locale.ENGLISH, getContent().toString());
	}

	@Override
	public CharSequence getContent() {
		return content;
	}

	@Override
	public String toString() {
		return content.toString();
	}

}
