package com.mbv.mca.checkout.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExceptionHandler {
	void handle(HttpServletRequest request, 
			HttpServletResponse response, 
			Throwable exception) throws ServletException; 
}
