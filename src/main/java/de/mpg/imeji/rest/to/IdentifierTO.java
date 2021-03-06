package de.mpg.imeji.rest.to;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@XmlRootElement
@XmlType(propOrder = {	 
		"type",
		"value"
		})
@JsonInclude(Include.NON_NULL)
public class IdentifierTO implements Serializable{


	private static final long serialVersionUID = 1633020264591234236L;
	
	private String type = "imeji";
	
	private String value;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
