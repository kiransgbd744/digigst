package com.ey.advisory.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * There are several instances where we need to maintain a Key and Value 
 * together. Since java does not have the concept of a tuple, we can use
 * this class to instantiate and use a Key/Value pair.  
 * 
 * @author Hareesh.Ravindran
 *
 * @param <K>
 * @param <V>
 */
public class KeyValuePair<K, V> {
	
	@Expose @SerializedName("key")
	protected K key;
	
	@Expose @SerializedName("value")
	protected V value;
	
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "KeyValuePair [key=" + key + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		
		KeyValuePair<?, ?> other = (KeyValuePair<?, ?>) obj;
		if(key == null) {
			if(other.key != null) return false;
		} else if(!key.equals(other.key)) {
			return false;
		}
		
		if(value == null) {
			if(other.value != null) return false;
		} else if(!value.equals(other.value)) {
			return false;
		}
		
		return true;
	}	
	
}
