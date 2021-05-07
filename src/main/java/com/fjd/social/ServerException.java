package com.fjd.social;


public class ServerException extends RuntimeException{
	
	private String description;


	public String getDescription() {
		return description==null?"":description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ServerException(Throwable e){
		super(e);
		
	}

	public ServerException(Throwable e,String errorCode){
		super(errorCode,e);
	}
	
	public ServerException(Throwable e,String errorCode,String description){
		super(errorCode,e);
		this.description=description;
	}
	
	public ServerException(String errorCode){
		super(errorCode);
	}
	
	public ServerException(String errorCode,String description){
		super(errorCode);
		this.description=description;
	}
}
