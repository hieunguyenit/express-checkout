package com.mbv.mca.checkout.core;

import java.io.Serializable;

import com.google.gson.Gson;

public class GsonDataObject implements Serializable{
	private static final long serialVersionUID = 29250119358161959L;

	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
