package com.plfort.stringoperation.operation;

public class SubStringOperation extends AbstractStringOperation {

	public int startIndex=-1;
	public int endIndex=-1;
	public boolean reverse = false;
	
	
	@Override
	public String evaluate(String context,int maxDepth) {
		if(maxDepth<0){
			throw new RuntimeException("Max depth reached");
		}
		int contextLength = context.length();
		if( startIndex < 0){
			throw new IllegalStateException("Error in SubStringOperation : startInddex="+startIndex+" endIndex="+endIndex+" contextLength="+contextLength);
		}
		String subString;
		if(endIndex != -1){
			if(startIndex>endIndex){
				this.reverse = true;
				subString =  context.substring(endIndex+1, startIndex+1);
			}else{
				subString =  context.substring(startIndex, endIndex);
			}
		}else{
			subString =  context.substring(startIndex);
		}
		
		if(reverse == true){
			return new StringBuilder(subString).reverse().toString();
		}

		return subString;
	}

}
