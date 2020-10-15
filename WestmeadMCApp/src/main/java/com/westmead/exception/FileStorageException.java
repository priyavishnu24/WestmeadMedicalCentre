package com.westmead.exception;

public class FileStorageException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7510740880006301063L;
	
	public FileStorageException(String msg) {
		super(msg);
	}
	
	public FileStorageException(String msg, Throwable t) {
		super(msg, t);
	}

}
