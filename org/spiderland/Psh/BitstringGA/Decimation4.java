package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation4 extends BitstringGA {
	private static final long serialVersionUID = 1L;
	
	protected void InitFromParameters() throws Exception {
		
		super.InitFromParameters();
	}

	/**
	 * Fitness = # of transitions
	 * where a transition is a 0 bit next to a 1 bit (in either order)
	 * NB: Lower is better, so best solution is all 1s or all 0s
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		float fitness = 0;
		
		for (int i = 1; i < _size; i++) {
			if (ind._bits.get(i) != ind._bits.get(i - 1)) {
				fitness++;
			}
		}
		
		ArrayList<Float> errors = new ArrayList<Float>();
		errors.add(fitness);
		inIndividual.SetFitness(fitness);
		inIndividual.SetErrors(errors);
	}

	/**
	 * Unused, since there's only one test case.
	 */
	@Override
	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		return 0;
	}

}
