package com.test.isd.delegate;

import java.util.Calendar;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Calendar c = Calendar.getInstance();
		System.out.println(c.getTime().getMonth());
		System.out.println(c.get(Calendar.MONTH));
		System.out.println(c.get(Calendar.YEAR)%100);
		
		if(c.get(Calendar.MONTH) < 9) {
			System.out.println(0+""+c.get(Calendar.MONTH));
		}
		
	}

}
