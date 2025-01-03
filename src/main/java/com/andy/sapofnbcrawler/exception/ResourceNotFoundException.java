package com.andy.sapofnbcrawler.exception;


public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException (String resourceName, String fieldName, String fieldValue) {
		super(String.format("%s không tìm thấy %s : %s. Vui lòng kiểm tra lại!", resourceName, fieldName, fieldValue));
	}
}
