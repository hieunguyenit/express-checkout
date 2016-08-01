package com.mbv.mca.checkout.jdo;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class DataFilter implements Serializable {	
	String query;	
	Date fromDate;	
	Date toDate;	
	
	int pageNumber = 1;
	int pageSize = 20;	
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
