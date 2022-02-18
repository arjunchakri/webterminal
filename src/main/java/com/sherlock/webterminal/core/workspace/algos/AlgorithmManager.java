package com.sherlock.webterminal.core.workspace.algos;

import com.sherlock.webterminal.core.workspace.FormulaManager;
import com.sherlock.webterminal.formulas.data.CustomFormula;
import com.sherlock.webterminal.formulas.data.FormulaWebInput;

public class AlgorithmManager {

	public static void main(String[] args) throws Exception {

		String formulaId = "userAlgo";
		String formulaImpl = "webterminal-data\\formulae\\diskspace\\Formula.java";

		CustomFormula compileFormulae = FormulaManager.compileFormulae(formulaId, formulaImpl);

		FormulaWebInput executeFormula = FormulaManager.executeFormula(compileFormulae);

		System.out.println(executeFormula);

	}

}
