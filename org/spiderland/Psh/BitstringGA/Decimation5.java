package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation5 extends BitstringGA {
	private static final long serialVersionUID = 1L;
	
	protected void InitFromParameters() throws Exception {
		
		super.InitFromParameters();
	}

	/**
	 * "Deb's Deceptive 4-bit Function"
	 * There are _size / 4 sub-functions, where each one is the following:
	 * 		if all 4 bits are 1s: 0
	 *		else : 1 + [number of 1s]
	 *	where trying to minimize fitness to 0 with all 1s.
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		//TODO: implement this
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
