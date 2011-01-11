package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation6 extends BitstringGA {
	private static final long serialVersionUID = 1L;
	
	protected void InitFromParameters() throws Exception {
		
		super.InitFromParameters();
		if (_size % 8 != 0) {
			throw new Exception(
					"For Deb's Deceptive 8-bit Function, bitstring-size must be divisible by 8.");
		}
	}

	/**
	 * "Deb's Deceptive 8-bit Function"
	 * There are (_size / 8) sub-functions that are summed, where each
	 * sub-function is the following:
	 * - if all 8 bits are 1s: 0
	 * - else : 2 + [number of 1s]
	 * where trying to minimize fitness to 0. Best individual has all 1s.
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		float fitness = 0;
		
		for (int i = 0; i < _size; i += 8) {
			fitness += DebsDeceptive8Bit(ind, i);
		}
		
		ArrayList<Float> errors = new ArrayList<Float>();
		errors.add(fitness);
		inIndividual.SetFitness(fitness);
		inIndividual.SetErrors(errors);
	}
	
	private float DebsDeceptive8Bit(BitstringGAIndividual ind, int startIndex) {
		int countOnes = 0;
		
		for(int i = startIndex; i < startIndex + 8; i++){
			if(ind._bits.get(i) == true){
				countOnes++;
			}
		}
		
		if(countOnes == 8){
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
