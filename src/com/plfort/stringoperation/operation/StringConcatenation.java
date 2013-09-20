package com.plfort.stringoperation.operation;

public class StringConcatenation extends AbstractStringOperation {

	
	@Override
	public String evaluate(String context,int maxDepth) {
		if(maxDepth<0){
			throw new RuntimeException("Max depth reached");
		}
		if(null != children && children.size()>0){
			StringBuilder sb = new StringBuilder();
			for(AbstractStringOperation so :children){
				String eval = so.evaluate(context,maxDepth-1);
				if(eval != null){
					sb.append(eval);
				}
			}
			return sb.toString();
		}
		return null;
	}

}
