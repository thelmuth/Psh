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
		
		
		//TODO remove
		try {
			i.SetProgram(new Program("(5.0)"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		PushFitnessPredictionIndividual i = (PushFitnessPredictionIndividual) ReproduceByClone(inIndex);

		int totalsize = i._program.programsize();
		int which = NodeSelection(i);

		int oldsize = i._program.SubtreeSize(which);
		int newsize = 0;

		// No size fair mutation in the Prediction GA for now...
		// if (_useFairMutation) {
		// int range = (int) Math.max(1, _fairMutationRange * oldsize);
		// newsize = Math.max(1, oldsize + _RNG.nextInt(2 * range) - range);
		// } else {
		newsize = _RNG.nextInt(_maxRandomCodeSize);
		// }

		Object newtree;

		if (newsize == 1)
			newtree = _interpreter.RandomAtom();
		else
			newtree = _interpreter.RandomCode(newsize);

		if (newsize + totalsize - oldsize <= _maxPointsInProgram)
			i._program.ReplaceSubtree(which, newtree);

		return i;
	}
	
	@Override
	protected GAIndividual ReproduceByCrossover(int inIndex) {
		PushFitnessPredictionIndividual a = (PushFitnessPredictionIndividual) ReproduceByClone(inIndex);
		PushFitnessPredictionIndividual b = (PushFitnessPredictionIndividual) TournamentSelect(
				_tournamentSize, inIndex);

		if (a._program.programsize() <= 0) {
			return b;
		}
		if (b._program.programsize() <= 0) {
			return a;
		}
		
		int aindex = NodeSelection(a);
		int bindex = NodeSelection(b);
		
		if (a._program.programsize() + b._program.SubtreeSize(bindex)
				- a._program.SubtreeSize(aindex) <= _maxPointsInProgram)
			a._program.ReplaceSubtree(aindex, b._program.Subtree(bindex));

		return a;
	}

	private int NodeSelection(PushFitnessPredictionIndividual inInd){
		int totalSize = inInd._program.programsize();
		int selectedNode = 0;
		
		if(totalSize <= 1){
			selectedNode = 0;
		}
		else {
			selectedNode = _RNG.nextInt(totalSize);
		}
		
		return selectedNode;
	}
	
}
