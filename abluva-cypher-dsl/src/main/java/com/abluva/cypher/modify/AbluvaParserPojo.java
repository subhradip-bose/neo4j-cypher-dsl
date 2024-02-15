package com.abluva.cypher.modify;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AbluvaParserPojo {

	private List<AbluvaCypherFormatter> parsedResult = new ArrayList<>();

}
