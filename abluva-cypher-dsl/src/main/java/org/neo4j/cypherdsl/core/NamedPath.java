/*
 * Copyright (c) 2019-2024 "Neo4j,"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.cypherdsl.core;

import static org.apiguardian.api.API.Status.STABLE;

import java.util.Optional;

import org.apiguardian.api.API;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.neo4j.cypherdsl.core.FunctionInvocation.FunctionDefinition;
import org.neo4j.cypherdsl.core.ast.Visitable;
import org.neo4j.cypherdsl.core.ast.Visitor;
import org.neo4j.cypherdsl.core.utils.Assertions;

/**
 * Represents a named path. A named path can be either a {@link RelationshipPattern} that has been assigned to a variable
 * as in {@code p := (a)-->(b)}, a call to functions known to return paths or an existing, symbolic name that might come
 * from an arbitrary procedure returning path elements.
 * <br>
 * <b>Note</b>: We cannot check a value that has been yielded from a procedure upfront to verify that it is a named
 * path. This is up to the caller.
 *
 * @author Michael J. Simons
 * @soundtrack Freddie Mercury - Never Boring
 * @since 1.1
 */
@API(status = STABLE, since = "1.1")
public final class NamedPath implements PatternElement, Named {

	/**
	 * The name of this path expression.
	 */
	private final SymbolicName name;

	/**
	 * The pattern defining this path.
	 */
	private final Visitable optionalPattern;

	static OngoingDefinitionWithName named(String name) {

		return named(SymbolicName.of(name));
	}

	static OngoingDefinitionWithName named(SymbolicName name) {

		Assertions.notNull(name, "A name is required");
		return new Builder(name);
	}

	static OngoingShortestPathDefinitionWithName named(String name, FunctionDefinition algorithm) {

		return new ShortestPathBuilder(SymbolicName.of(name), algorithm);
	}

	static OngoingShortestPathDefinitionWithName named(SymbolicName name, FunctionDefinition algorithm) {

		Assertions.notNull(name, "A name is required");
		return new ShortestPathBuilder(name, algorithm);
	}

	/**
	 * Partial path that has a name ({@code p = }).
	 */
	public interface OngoingDefinitionWithName {

		/**
		 * Create a new named path based on a {@link PatternElement} single node.
		 * If a {@link NamedPath} will be provided, it will get used directly.
		 *
		 * @param patternElement The PatternElement to be used in named path.
		 * @return A named path.
		 */
		@NotNull @Contract(pure = true)
		NamedPath definedBy(PatternElement patternElement);

		/**
		 * Create a new named path that references a given, symbolic name. No checks are done if the referenced name
		 * actually points to a path.
		 *
		 * @return A named path.
		 * @since 2020.1.4
		 */
		@NotNull @Contract(pure = true)
		NamedPath get();
	}

	/**
	 * Partial path that has a name ({@code p = }) and is based on a graph algorithm function.
	 */
	public interface OngoingShortestPathDefinitionWithName {

		/**
		 * Create a new named path based on a single relationship.
		 *
		 * @param relationship The relationship to be passed to {@code shortestPath}.
		 * @return A named path.
		 */
		NamedPath definedBy(Relationship relationship);
	}

	private record Builder(SymbolicName name) implements OngoingDefinitionWithName {

		@NotNull
		@Override
		public NamedPath definedBy(PatternElement pattern) {
			if (pattern instanceof NamedPath namedPath) {
				return namedPath;
			}
			return new NamedPath(name, pattern);
		}

		@NotNull
		@Override
		public NamedPath get() {
			return new NamedPath(name);
		}
	}

	private record ShortestPathBuilder(
		SymbolicName name, FunctionDefinition algorithm) implements OngoingShortestPathDefinitionWithName {

		@Override
		public NamedPath definedBy(Relationship relationship) {
			return new NamedPath(name, FunctionInvocation.create(algorithm, relationship));
		}
	}

	private NamedPath(SymbolicName name) {
		this.name = name;
		this.optionalPattern = null;
	}

	private NamedPath(SymbolicName name, PatternElement optionalPattern) {
		this.name = name;
		this.optionalPattern = optionalPattern;
	}

	private NamedPath(SymbolicName name, FunctionInvocation algorithm) {
		this.name = name;
		this.optionalPattern = algorithm;
	}

	@Override
	@NotNull
	public Optional<SymbolicName> getSymbolicName() {
		return Optional.of(name);
	}

	@Override
	public void accept(Visitor visitor) {

		visitor.enter(this);
		this.name.accept(visitor);
		if (optionalPattern != null) {
			Operator.ASSIGMENT.accept(visitor);
			this.optionalPattern.accept(visitor);
		}
		visitor.leave(this);
	}
}
