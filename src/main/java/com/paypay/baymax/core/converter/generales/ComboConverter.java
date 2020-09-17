package com.paypay.baymax.core.converter.generales;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.paypay.baymax.commons.type.ComboType;

public class ComboConverter {

	public static List<ComboType> convertListEntityToType(List<?> lstTCombo) {

		List<ComboType> lstComboType = new ArrayList<ComboType>();
		if (lstTCombo.size() > 0) {
			for (Object tCombo : lstTCombo) {
				lstComboType.add(ComboConverter.convertEntityToType(tCombo));
			}
		}
		return lstComboType;
	}

	public static ComboType convertEntityToType(Object combo) {
		
		@SuppressWarnings("unchecked")
		HashMap<String,Object> hashCombo = (HashMap<String, Object>) combo;
		
		ComboType comboType = new ComboType();
		if (combo != null) {
			if(hashCombo.get("id") != null) {
				comboType.setId(((BigInteger) hashCombo.get("id")).longValue());
			}
			if(hashCombo.get("name") != null) {
				comboType.setName((String) hashCombo.get("name"));
			}
			if(hashCombo.get("status") != null) {
				
			}
		}
		return comboType;
	}

}
