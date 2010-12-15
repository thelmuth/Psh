package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;

public class Decimation1 extends BitstringGA {
	private static final long serialVersionUID = 1L;

	public float _lambda;
	
	protected void InitFromParameters() throws Exception {
		_lambda = GetFloatParam("lambda");
		
		super.InitFromParameters();
	}

	/**
	 * Fitness = _size - [# of ones + (# of leading zeros) * _lambda], where
	 * _lambda is in (0,1)
	 */
	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual ind = (BitstringGAIndividual) inIndividual;

		float fitness = _size;
		boolean leadingFalse = true;
		for (int i = 0; i < _size; i++) {
			if (ind._bits.get(i) == true) {
				fitness -= 1;
				leadingFalse = false;
			} else {
				if (leadingFalse) {
					fitness -= _lambda;
				}
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
