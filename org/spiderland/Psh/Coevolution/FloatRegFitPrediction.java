/*
 * Copyright 2009-2010 Jon Klein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spiderland.Psh.Coevolution;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

/**
 * This fitness prediction class uses predictors that choose a small sample
 * of the test cases to predict the fitness of all test cases.
 * 
 * @author Tom
 *
 */
public class FloatRegFitPrediction extends PredictionGA {
	private static final long serialVersionUID = 1L;

	@Override
	protected void InitIndividual(GAIndividual inIndividual) {
		FloatRegFitPredictionIndividual i = (FloatRegFitPredictionIndividual) inIndividual;
		
		int[] samples = new int[FloatRegFitPredictionIndividual._sampleSize];
		for(int j = 0; j < samples.length; j++){
			samples[j] = _RNG.nextInt(_solutionGA._testCases.size());
		}
		i.SetSampleIndicesAndSolutionGA(_solutionGA, samples);
	}

	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		
		FloatRegFitPredictionIndividual predictor = (FloatRegFitPredictionIndividual) inIndividual;
		ArrayList<Float> errors = new ArrayList<Float>();

		for(int i = 0; i < _trainerPopulationSize; i++){			
			float predictedError = predictor.PredictSolutionFitness(_trainerPopulation.get(i));
			
			// Error is difference between predictedError and the actual fitness
			// of the trainer.
			float error = Math.abs(predictedError) - Math.abs(_trainerPopulation.get(i).GetFitness());
			errors.add(error);
		}
		
		predictor.SetFitness(AbsoluteAverageOfErrors(errors));
		predictor.SetErrors(errors);
	}

	/**
	 * Determines the predictor's fitness on a trainer, where the trainer is the
	 * inInput, and the trainer's actual fitness is inOutput. The fitness of
	 * the predictor is the absolute error between the prediction and the
	 * trainer's actual fitness.
	 * 
	 * @return Predictor's fitness (i.e. error) for the given trainer.
	 * @throws Exception 
	 */
	@Override
	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {

		PushGPIndividual trainer = (PushGPIndividual) inInput;
		float trainerFitness = (Float) inOutput;

		float predictedTrainerFitness = ((PredictionGAIndividual) inIndividual)
				.PredictSolutionFitness(trainer);

		return Math.abs(predictedTrainerFitness - trainerFitness);
	}

	@Override
	protected void EvaluateTrainerFitnesses() {
		for(PushGPIndividual trainer : _trainerPopulation){
			if(!trainer.FitnessIsSet()){
				EvaluateSolutionIndividual(trainer);
			}	
		}
	}

	protected void Reproduce() {
		int nextPopulation = _currentPopulation == 0 ? 1 : 0;

		for (int n = 0; n < _populations[_currentPopulation].length; n++) {
			float method = _RNG.nextInt(100);
			GAIndividual next;

			if (method < _mutationPercent) {
				next = ReproduceByMutation(n);
			} else if (method < _crossoverPercent + _mutationPercent) {
				next = ReproduceByCrossover(n);
			} else {
				next = ReproduceByClone(n);
			}

			// Make sure next isn't already in the population, so that all
			// predictors are unique.
			for (int k = 0; k < n; k++) {
				if (((FloatRegFitPredictionIndividual) next)
						.equalPredictors(_populations[nextPopulation][k])) {
					int index = _RNG
							.nextInt(FloatRegFitPredictionIndividual._sampleSize);
					((FloatRegFitPredictionIndividual) next).SetSampleIndex(
							index, _RNG.nextInt(_solutionGA._testCases.size()));
				}
			}
			
			_populations[nextPopulation][n] = next;
			
		}
	}
	
	/**
	 * Mutates an individual by choosing an index at random and randomizing
	 * its training point among possible individuals.
	 */
	@Override
	protected GAIndividual ReproduceByMutation(int inIndex) {
		FloatRegFitPredictionIndividual i = (FloatRegFitPredictionIndividual) ReproduceByClone(inIndex);

		int index = _RNG.nextInt(FloatRegFitPredictionIndividual._sampleSize);
		i.SetSampleIndex(index, _RNG.nextInt(_solutionGA._testCases.size()));
		
		return i;
	}

	@Override
	protected GAIndividual ReproduceByCrossover(int inIndex) {
		FloatRegFitPredictionIndividual a = (FloatRegFitPredictionIndividual) ReproduceByClone(inIndex);
		FloatRegFitPredictionIndividual b = (FloatRegFitPredictionIndividual) TournamentSelect(
				_tournamentSize, inIndex);
		
		// crossoverPoint is the first index of a that will be changed to the
		// gene from b.
		int crossoverPoint = _RNG
				.nextInt(FloatRegFitPredictionIndividual._sampleSize - 1) + 1;
		for (int i = crossoverPoint; i < FloatRegFitPredictionIndividual._sampleSize; i++) {
			a.SetSampleIndex(i, b.GetSampleIndex(i));
		}

		return a;
	}

	
}
