package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation3 extends BitstringGA {
	private static final long serialVersionUID = 1L;
	
	protected void InitFromParameters() throws Exception {
		
		super.InitFromParameters();
	}

	/**
	 * Fitness = (_size - 1) - (# of transitions)
	 * where a transition is a 0 bit next to a 1 bit (in either order)
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		float fitness = _size - 1;
		boolean prevBit = ind._bits.get(0);
		
		for (int i = 1; i < _size; i++) {
			if (ind._bits.get(i) != prevBit) {
				fitness -= 1;
			}
			prevBit = ind._bits.get(i);
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
