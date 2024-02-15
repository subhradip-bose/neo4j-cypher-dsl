/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypherdsl.parser;

import static org.apiguardian.api.API.Status.STABLE;

import org.apiguardian.api.API;

/**
 * @author Michael J. Simons
 * @soundtrack Black Sabbath - The End: Live in Birmingham
 * @since 2022.1.1
 */
@API(status = STABLE, since = "2022.2.0")
public enum PatternElementCreatedEventType {

	/**
	 * A {@link org.neo4j.cypherdsl.core.PatternElement} is created during the creation of a {@code MATCH} clause.
	 */
	ON_MATCH,
	/**
	 * A {@link org.neo4j.cypherdsl.core.PatternElement} is created during the creation of a {@code CREATE} clause.
	 */
	ON_CREATE,
	/**
	 * A {@link org.neo4j.cypherdsl.core.PatternElement} is created during the creation of a {@code MERGE} clause.
	 */
	ON_MERGE,
}
