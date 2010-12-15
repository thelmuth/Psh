package org.spiderland.Psh.BitstringGA;

import java.util.ArrayList;

import org.spiderland.Psh.GA;
import org.spiderland.Psh.GAIndividual;

abstract public class BitstringGA extends GA {
	private static final long serialVersionUID = 1L;

	protected int _size;
	
	protected void InitFromParameters() throws Exception {
		_size = (int) GetFloatParam("bitstring-size");
		
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
			+ _populations[_currentPopulation][_bestIndividual]
			+ "\n";
		report += "Best fitness: " + _bestMeanFitness
				+ "\n\n";

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
		
		int index = _RNG.nextInt(a._bits.size());
		boolean flipped = !a._bits.get(index);
		a._bits.set(index, flipped);
		
		return a;
	}

}
