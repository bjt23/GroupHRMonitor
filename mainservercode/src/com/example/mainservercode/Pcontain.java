package com.example.mainservercode;

import java.io.Serializable;

//Container class for phone objects
public class Pcontain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String aname;
	public String arate;
	public String astat;
	public double alat;
	public double alon;


	//ServerMain objects store phone data
	Pcontain(String name, String hrate, String stat, double lat, double lon) {
		aname=name;
		arate=hrate;
		astat=stat;
		alat=lat;
		alon=lon;
	}

}
