package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GA;
import org.spiderland.Psh.GAIndividual;

abstract public class BitstringGA extends GA {
	private static final long serialVersionUID = 1L;

	protected int _size;
	
	protected String _GAMutationMode;
	protected float _GAMutationPercent;
	
	protected void InitFromParameters() throws Exception {
		_size = (int) GetFloatParam("bitstring-size");
		
		String defaultGAMutationMode = "one-bit";
		float defaultGAMutationPercent = 10.0f;
		_GAMutationMode = GetParam("ga-mutation-mode", true);
		if (_GAMutationMode == null) {
			_GAMutationMode = defaultGAMutationMode;
		}
		if (!_GAMutationMode.equals("one-bit") && !_GAMutationMode.equals("per-bit")){
			throw new Exception("ERROR: ga-mutation-mode must be set to one-bit or per-bit." + 
					"\nCurrently set to " + _GAMutationMode);
		}
		
		_GAMutationPercent = GetFloatParam("ga-mutation-percent", true);
		if (Float.isNaN(_GAMutationPercent)) {
			_GAMutationPercent = defaultGAMutationPercent;
		}
		
		super.InitFromParameters();
	}

	@Override
	protected void InitIndividual(GAIndividual inIndividual) {
		BitstringGAIndividual bgai = (BitstringGAIndividual) inIndividual;
		
		ArrayList<Boolean> bits = new ArrayList<Boolean>();
		for(int i = 0; i < _size; i++){
			bits.add(_RNG.nextBoolean());
		}
		bgai.setBitstring(bits);
	}

	@Override
	protected String Report() {
		String report = super.Report();

		report += "Best individual: "
				+ _populations[_currentPopulation][_bestIndividual] + "\n";
		report += "Best fitness: " + _bestMeanFitness + "\n\n";

		// Find worst individual that was allowed to reproduce
		int worstIndividual = 0;
		float worstFitness = _populations[_currentPopulation][0].GetFitness();
		for(int i = 1; i < _survivalPopulationSize; i++){
			if(_populations[_currentPopulation][i].GetFitness() > worstFitness){
				worstIndividual = i;
				worstFitness = _populations[_currentPopulation][i].GetFitness();
			}
		}
		report += "Worst reproducing individual: "
				+ _populations[_currentPopulation][worstIndividual] + "\n";
		report += "Worst reproducing individual's fitness: " + worstFitness + "\n\n";

		report += "Population mean fitness: " + _populationMeanFitness + "\n";
		report += ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n\n";
		
		return report;
	}

	@Override
	protected String FinalReport() {
		String report = super.FinalReport();
		
		if (Success()) {
			report += "Successful individual: "
					+ _populations[_currentPopulation][_bestIndividual] + "\n";
		} else {
			report += "Best failed individual: "
					+ _populations[_currentPopulation][_bestIndividual] + "\n";
		}
		report += "Fitness: " + _bestMeanFitness + "\n";
		
		return report;
	}
	
	/**
	 * One-point crossover.
	 */
	@Override
	protected GAIndividual ReproduceByCrossover(int inIndex) {
		BitstringGAIndividual a = (BitstringGAIndividual) ReproduceByClone(inIndex);
		BitstringGAIndividual b = (BitstringGAIndividual) TournamentSelect(
				_tournamentSize, inIndex);
		
		int size = a._bits.size();
		// index represents the index of the cut. The resulting child will be
		// a from 0 to index - 1 with b from index to size - 1 appended.
		int index = _RNG.nextInt(size - 1) + 1;
		
		for(int i = index; i < size; i++){
			a._bits.set(i, b._bits.get(i));
		}
		
		return a;
	}

	/**
	 * Single-point bit flip mutation.
	 */
	@Override
	protected GAIndividual ReproduceByMutation(int inIndex) {
		BitstringGAIndividual a = (BitstringGAIndividual) ReproduceByClone(inIndex);
		
		if (_GAMutationMode.equals("one-bit")) {
			int index = _RNG.nextInt(a._bits.size());
			boolean flipped = !a._bits.get(index);
			a._bits.set(index, flipped);
		}
		if(_GAMutationMode.equals("per-bit")){
			for(int i = 0; i < a._bits.size(); i++){
				if(_RNG.nextInt(100) <= _GAMutationPercent){
					a._bits.set(i, !a._bits.get(i));
				}
			}
		}
		
		return a;
	}

}
