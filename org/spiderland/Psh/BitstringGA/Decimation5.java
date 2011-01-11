package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation5 extends BitstringGA {
	private static final long serialVersionUID = 1L;
	
	protected void InitFromParameters() throws Exception {
		
		super.InitFromParameters();
		if (_size % 4 != 0) {
			throw new Exception(
					"For Deb's Deceptive 4-bit Function, bitstring-size must be divisible by 4.");
		}
	}

	/**
	 * "Deb's Deceptive 4-bit Function"
	 * There are (_size / 4) sub-functions that are summed, where each
	 * sub-function is the following:
	 * - if all 4 bits are 1s: 0
	 * - else : 2 + [number of 1s]
	 * where trying to minimize fitness to 0. Best individual has all 1s. Worst
	 * individual, which has three out of four 1s, has fitness 5 (_size / 4).
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		float fitness = 0;
		
		for (int i = 0; i < _size; i += 4) {
			fitness += DebsDeceptive4Bit(ind, i);
		}
		
		ArrayList<Float> errors = new ArrayList<Float>();
		errors.add(fitness);
		inIndividual.SetFitness(fitness);
		inIndividual.SetErrors(errors);
	}
	
	private float DebsDeceptive4Bit(BitstringGAIndividual ind, int startIndex) {
		int countOnes = 0;
		
		for(int i = startIndex; i < startIndex + 4; i++){
			if(ind._bits.get(i) == true){
				countOnes++;
			}
		}
		
		if(countOnes == 4){
			return 0;
		}
		
		return 2 + countOnes;
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
