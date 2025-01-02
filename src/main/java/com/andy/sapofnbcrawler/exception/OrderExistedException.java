package com.andy.sapofnbcrawler.exception;


public class OrderExistedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderExistedException (String message) {
		super(message);
	}
}
