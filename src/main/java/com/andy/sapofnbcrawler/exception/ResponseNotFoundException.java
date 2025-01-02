package com.andy.sapofnbcrawler.exception;


public class ResponseNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResponseNotFoundException (String message) {
		super(message);
	}
}
