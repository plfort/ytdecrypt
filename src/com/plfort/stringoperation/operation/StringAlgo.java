package com.plfort.stringoperation.operation;

public class StringAlgo {

	public final static int DEFAULT_MAX_DEPTH = 5;
	public int stringLengthCondition;
	public AbstractStringOperation entryPointOperation;
	
	public String evaluate(String context,int maxDepth){
		if(context != null && stringLengthCondition == context.length() && entryPointOperation != null){
			return entryPointOperation.evaluate(context,maxDepth);
		}
		return null;
	}
	
	public String evaluate(String context){
		return evaluate(context,DEFAULT_MAX_DEPTH);
	}
	
	
}
