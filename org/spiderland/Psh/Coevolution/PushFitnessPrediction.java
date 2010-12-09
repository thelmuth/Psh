package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.*;

/**
 * A class for using generic fitness prediction using Push programs as fitness
 * predictors.
 * @author Tom
 *
 */
public class PushFitnessPrediction extends PredictionGA {
	private static final long serialVersionUID = 1L;

	protected int _maxPointsInProgram = 500;
	protected int _maxRandomCodeSize = 100;
	protected int _executionLimit = 2000;

	String _instructionSet = "(registered.boolean registered.integer registered.float registered.exec registered.code input.makeinputs1)";
	
	// The prediction interpreter, which must be different from the solution
	// interpreter
	protected Interpreter _interpreter;

	@Override
	protected void InitFromParameters() throws Exception {

		// Setup interpreter
		_interpreter = new Interpreter();
		_interpreter.SetInstructions(new Program(_instructionSet));
		_interpreter.SetRandomParameters(-100, 100, 1, -100.0f, 100.0f, 0.1f,
				_maxRandomCodeSize, _maxPointsInProgram);
		_interpreter.setInputPusher(new InputPusher());

		super.InitFromParameters();
	}
	
	@Override
	protected void InitIndividual(GAIndividual inIndividual) {
		PushFitnessPredictionIndividual i = (PushFitnessPredictionIndividual) inIndividual;
		
		int randomCodeSize = _RNG.nextInt(_maxRandomCodeSize) + 2;
		Program p = _interpreter.RandomCode(randomCodeSize);
		i.SetProgram(p);
		i.SetInterpreter(_interpreter);
		i.SetExecutionLimit(_executionLimit);
	}

	@Override
	protected void EvaluateTrainerFitnesses() {
		for(PushGPIndividual trainer : _trainerPopulation){
			if(!trainer.FitnessIsSet()){
				EvaluateSolutionIndividual(trainer);
			}	
		}
	}

	@Override
	protected GAIndividual ReproduceByMutation(int inIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected GAIndividual ReproduceByCrossover(int inIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
