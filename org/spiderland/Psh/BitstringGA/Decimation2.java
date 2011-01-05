package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation2 extends BitstringGA {
	private static final long serialVersionUID = 1L;

	public float _lambda;
	
	protected void InitFromParameters() throws Exception {
		_lambda = GetFloatParam("lambda");
		
		super.InitFromParameters();
	}

	/**
	 * Fitness = _size - [# of leading ones + (# of trailing zeros) *
	 * _lambda], where _lambda is in (0,1)
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		float fitness = _size;
		boolean leadingOne = true;
		int trailingZero = 0;
		for (int i = 0; i < _size; i++) {
			if (ind._bits.get(i) == true) {
				if(leadingOne){
					fitness -= 1;
				}
				trailingZero = 0;
			} else {
				trailingZero++;
				leadingOne = false;
			}
		}
		
		fitness = fitness - (((float)trailingZero) * _lambda);

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
