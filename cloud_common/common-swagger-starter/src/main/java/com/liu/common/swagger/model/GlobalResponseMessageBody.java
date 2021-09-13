package com.liu.common.swagger.model;

public class GlobalResponseMessageBody {

	/**
	 * 响应码
	 **/
	private String code;

	/**
	 * 响应消息
	 **/
	private String message;

	/**
	 * 响应体
	 **/
	private String modelRef;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getModelRef() {
		return modelRef;
	}

	public void setModelRef(String modelRef) {
		this.modelRef = modelRef;
	}

}