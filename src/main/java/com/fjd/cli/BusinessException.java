package com.fjd.cli;

public class BusinessException extends RuntimeException{
	private String description;


	public String getDescription() {
		return description==null?"":description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public BusinessException(String description){
		super(description);
		
	}
}
