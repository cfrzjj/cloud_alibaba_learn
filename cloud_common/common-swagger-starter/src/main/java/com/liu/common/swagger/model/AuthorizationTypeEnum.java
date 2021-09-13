package com.liu.common.swagger.model;

import java.util.NoSuchElementException;

/**
 * 鉴权策略，可选 ApiKey | BasicAuth | None，默认ApiKey
 * @author ： <a href="https://github.com/hiwepy">wandl</a>
 */
public enum AuthorizationTypeEnum {

	APIKEY("ApiKey"), BASICAUTH("BasicAuth"), NONE("None");

	private final String type;

	AuthorizationTypeEnum(String type) {
		this.type = type;
	}

	public String get() {
		return type;
	}
	
	public boolean equals(AuthorizationTypeEnum type){
		return this.compareTo(type) == 0;
	}
	
	public boolean equals(String type){
		return this.compareTo(AuthorizationTypeEnum.valueOfIgnoreCase(type)) == 0;
	}
	
	public static AuthorizationTypeEnum valueOfIgnoreCase(String type) {
		for (AuthorizationTypeEnum transport : AuthorizationTypeEnum.values()) {
			if(transport.get().equalsIgnoreCase(type)) {
				return transport;
			}
		}
    	throw new NoSuchElementException("Cannot found AuthorizationType with type '" + type + "'.");
    }
	
}