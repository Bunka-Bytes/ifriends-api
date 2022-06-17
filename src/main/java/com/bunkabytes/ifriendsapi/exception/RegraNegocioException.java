package com.bunkabytes.ifriendsapi.exception;

@SuppressWarnings("serial")
public class RegraNegocioException extends RuntimeException {
	
	public RegraNegocioException (String mensagem) {
		super(mensagem);
	}
}
