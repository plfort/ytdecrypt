package com.plfort.stringoperation.operation;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({

    @JsonSubTypes.Type(value=StringConcatenation.class, name="stringConcat"),
    @JsonSubTypes.Type(value=SubStringOperation.class, name="subString"),
    @JsonSubTypes.Type(value=ChartAtOperation.class, name="charAt")

})
public abstract class AbstractStringOperation {

	public List<AbstractStringOperation> children;
	
	public abstract String evaluate(String context,int maxDepth);
	
	
}
