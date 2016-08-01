package com.mbv.mca.checkout.xacct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An event object defines parameters and context for a source object 
 * which can be an account or transaction.
 * @author Nam Pham
 *
 * @param <T>
 */
public class XEvent<T> {
	String name;
	T source;
	
	Map<String, Object> params;
	
	public XEvent(T src, Map<String, Object> params) {
		if (src==null) 
			throw new NullPointerException("src cannot be null");
		source = src;
		this.params = params;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public T getSource() {
		return source;
	}

	protected void setSource(T source) {
		this.source = source;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	protected void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	/**
	 * set a param value
	 * @param name
	 * @param value
	 */
	public void setParam(String name, Object value) {
		if (params==null)
			params = new HashMap<String, Object>();
		params.put(name, value);
	}
	
	/**
	 * return a param value
	 * @param param
	 * @return
	 */
	public Object getParam(String param) {
		return params!=null?params.get(param):null;
	}
	
	
	
	/**
	 * Return an integer value of a param.
	 * @param param
	 * @return
	 */
	public int getIntParam(String param) {
		if (!params.containsKey(param))
			return 0;
		Object value = params.get(param);
		if (value==null)
			return 0;
		if (value instanceof Number)
			return ((Number)value).intValue();
		if (value instanceof String)
			return Integer.parseInt((String)value);
		throw new RuntimeException("Could not convert integer from unknown type: " + value.getClass().getCanonicalName());
	}
	
	/**
	 * Return a long value of a param.
	 * @param param
	 * @return
	 */
	public long getLongParam(String param) {
		if (!params.containsKey(param))
			return 0;
		Object value = params.get(param);
		if (value==null)
			return 0;
		if (value instanceof Number)
			return ((Number)value).longValue();
		if (value instanceof String)
			return Long.parseLong((String)value);
		throw new RuntimeException("Could not convert long from unknown type: " + value.getClass().getCanonicalName());
	}
	
	/**
	 * Return a float value of a param.
	 * @param param
	 * @return
	 */
	public float getFloatParam(String param) {
		if (!params.containsKey(param))
			return 0;
		Object value = params.get(param);
		if (value==null)
			return 0;
		if (value instanceof Number)
			return ((Number)value).floatValue();
		if (value instanceof String)
			return Float.parseFloat((String)value);
		throw new RuntimeException("Could not convert float from unknown type: " + value.getClass().getCanonicalName());
	}
	
	/**
	 * Return an double value of a param.
	 * @param param
	 * @return
	 */
	public double getDoubleParam(String param) {
		if (!params.containsKey(param))
			return 0;
		Object value = params.get(param);
		if (value==null)
			return 0;
		if (value instanceof Number)
			return ((Number)value).doubleValue();
		if (value instanceof String)
			return Double.parseDouble((String)value);
		throw new RuntimeException("Could not convert doule from unknown type: " + value.getClass().getCanonicalName());
	}
	
	/**
	 * Return an integer value of a param.
	 * @param param
	 * @return
	 */
	public Date getDateParam(String param) {
		if (!params.containsKey(param))
			return null;
		Object value = params.get(param);
		if (value==null)
			return null;
		if (value instanceof Date)
			return (Date)value;
		try {
			if (value instanceof String)
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String)value);
		}
		catch (ParseException ex) {
			throw new RuntimeException("Unable to parse date from " + value, ex);
		}
		throw new RuntimeException("Could not convert date from unknown type: " + value.getClass().getCanonicalName());
	}
	
	/**
	 * get list value of a param
	 * @param param
	 * @return
	 */
	public List<?> getListParam(String param) {
		if (!params.containsKey(param))
			return null;
		Object value = params.get(param);
		if (value==null) return null;
		if (value instanceof List)
			return (List<?>)value;
		throw new RuntimeException("Could not convert list from unknown type: " + value.getClass().getCanonicalName());
	}
	
	/**get map value of a param
	 * 
	 * @param param
	 * @return
	 */
	public Map<?,?> getMapParam(String param) {
		if (!params.containsKey(param))
			return null;
		Object value = params.get(param);
		if (value==null) return null;
		if (value instanceof Map)
			return (Map<?,?>)value;
		throw new RuntimeException("Could not convert map from unknown type: " + value.getClass().getCanonicalName());
	}
	
}
