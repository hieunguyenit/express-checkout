package com.mbv.mca.checkout.core;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.mbv.mca.checkout.utils.DateSerializer;

public abstract class DataObject implements Serializable {
	private static final long serialVersionUID = 8473313351726775414L;
	
	Long id;
	Date createdAt;	
	Date updatedAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonSerialize(using=DateSerializer.class)
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
		
	@JsonSerialize(using=DateSerializer.class)
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
