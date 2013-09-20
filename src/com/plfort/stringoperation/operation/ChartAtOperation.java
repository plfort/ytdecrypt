package com.plfort.stringoperation.operation;


public class ChartAtOperation extends AbstractStringOperation {

	
	public int index;
	
	@Override
	public String evaluate(String context,int maxDepth) {
		if(maxDepth<0){
			throw new RuntimeException("Max depth reached");
		}
		return ""+context.charAt(index);
	}

}
