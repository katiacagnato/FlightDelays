package it.polito.tdp.extflightdelays.model;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class TestModel {

	public static void main(String[] args) {
		
		ExtFlightDelaysDAO dao= new ExtFlightDelaysDAO();
		Model model = new Model();
		int minimoCompagnie= 7;
		
		model.CreaGrafo(minimoCompagnie);
		
		/**
				23   numero input : 11
				34   numero input : 10
				50   numero input : 9
				59   numero input : 8
				59   numero input : 9
				72   numero input : 7
				86   numero input : 6
				86   numero input : 9
				127  numero input : 4

		 */
	}

}
