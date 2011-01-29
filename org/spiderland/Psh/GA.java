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

package org.spiderland.Psh;

import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An abstract class for running genetic algorithms.
 */

public abstract class GA implements Serializable {
	private static final long serialVersionUID = 1L;

	protected HashMap<String, String> _parameters;
	public ArrayList<GATestCase> _testCases;

	protected Class<?> _individualClass;

	protected Random _RNG;
	
	protected GAIndividual _populations[][];
	protected int _currentPopulation;
	
	protected int _generationCount;
	protected int _maxGenerations;

	protected float _mutationPercent;
	protected float _crossoverPercent;

	protected int _tournamentSize;
	protected int _trivialGeographyRadius;

	protected String _survivalSelectionMode;
	protected float _survivalRatio;
	protected int _survivalPopulationSize;
	protected int _decimationTournamentSize;

	protected transient OutputStream _outputStream;
	boolean _alsoPrintToOutput;
	public boolean _verbose;

	protected Checkpoint _checkpoint;
	protected String _checkpointPrefix;
	protected String _outputfile;
	
	protected float _bestMeanFitness;
	protected ArrayList<Float> _bestErrors;
	protected double _populationMeanFitness;
	protected int _bestIndividual;


	/**
	 * Factor method for creating a GA object, with the GA class specified by
	 * the problem-class parameter.
	 */
	public static GA GAWithParameters(HashMap<String, String> inParams)
			throws Exception {
		
		Class<?> cls = Class.forName(inParams.get("problem-class"));
		
		Object gaObject = cls.newInstance();

		if (!(gaObject instanceof GA))
			throw (new Exception("problem-class must inherit from class GA"));

		GA ga = (GA) gaObject;

		ga.SetParams(inParams);
		ga.InitFromParameters();

		return ga;
	}

	public static GA GAWithCheckpoint(String checkpoint) throws Exception {
		File checkpointFile = new File(checkpoint);
		FileInputStream zin = new FileInputStream(checkpointFile);
		GZIPInputStream in = new GZIPInputStream(zin);
		ObjectInputStream oin = new ObjectInputStream(in);

		Checkpoint ckpt = (Checkpoint) oin.readObject();
		GA ga = ckpt.ga;
		ga._checkpoint = ckpt;
		ckpt.checkpointNumber++; // because it gets increased only after ckpt is
		// written

		oin.close();

		System.out.println(ckpt.report.toString());

		// Do we want to append to the file if it exists? Or just overwrite it?
		// Heu! Quae enim quaestio animas virorum vero pertemptit.
		// Wowzers! This is, indeed, a question that truly tests mens' souls.

		if (ga._outputfile != null)
			ga._outputStream = new FileOutputStream(new File(ga._outputfile));
		else
			ga._outputStream = System.out;

		return ga;
	}

	protected GA() {
		_RNG = new Random();
		_testCases = new ArrayList<GATestCase>();
		_bestMeanFitness = Float.MAX_VALUE;
		_outputStream = System.out;
	}

	/**
	 * Sets the parameters dictionary for this GA run.
	 */

	protected void SetParams(HashMap<String, String> inParams) {
		_parameters = inParams;
	}

	/**
	 * Utility function to fetch an non-optional string value from the parameter
	 * list.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 */

	public String GetParam(String inName) throws Exception {
		return GetParam(inName, false);
	}

	/**
	 * Utility function to fetch a string value from the parameter list,
	 * throwing an exception.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 * @param inOptional
	 *            whether the parameter is optional. If a parameter is not
	 *            optional, this method will throw an exception if the parameter
	 *            is not found.
	 * @return the string value for the parameter.
	 */

	public String GetParam(String inName, boolean inOptional)
			throws Exception {
		String value = _parameters.get(inName);

		if (value == null && !inOptional)
			throw new Exception("Could not locate required parameter \""
					+ inName + "\"");

		return value;
	}

	/**
	 * Utility function to fetch an non-optional float value from the parameter
	 * list.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 */

	public float GetFloatParam(String inName) throws Exception {
		return GetFloatParam(inName, false);
	}

	/**
	 * Utility function to fetch a float value from the parameter list, throwing
	 * an exception.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 * @param inOptional
	 *            whether the parameter is optional. If a parameter is not
	 *            optional, this method will throw an exception if the parameter
	 *            is not found.
	 * @return the float value for the parameter.
	 */

	public float GetFloatParam(String inName, boolean inOptional)
			throws Exception {
		String value = _parameters.get(inName);

		if (value == null && !inOptional)
			throw new Exception("Could not locate required parameter \""
					+ inName + "\"");

		if(value == null)
			return Float.NaN;
		
		return Float.parseFloat(value);
	}

	/**
	 * Sets up the GA object with the previously set parameters. This method is
	 * typically overridden to read in custom parameters associated with custom
	 * subclasses. Subclasses must always call the base class implementation
	 * first to ensure that all base parameters are setup.
	 */

	protected void InitFromParameters() throws Exception {
		// Default parameters to be used when optional parameters are not
		// given.
		int defaultTrivialGeographyRadius = 0;
		String defaultIndividualClass = "org.spiderland.Psh.PushGPIndividual";
		boolean defaultVerbose = true;
		String defaultSurvivalSelectionMode = "none";
		float defaultSurvivalRatio = 10;
		int defaultDecimationTournamentSize = 2;
		
		String individualClass = GetParam("individual-class", true);
		if(individualClass == null){
			individualClass = defaultIndividualClass;
		}
		_individualClass = Class.forName(individualClass);

		_survivalSelectionMode = GetParam("survival-selection-mode", true);
		if (_survivalSelectionMode == null) {
			_survivalSelectionMode = defaultSurvivalSelectionMode;
			_survivalPopulationSize = (int) GetFloatParam("population-size");
		} else {
			if (!_survivalSelectionMode.equals("none")
					&& !_survivalSelectionMode.equals("decimation")
					&& !_survivalSelectionMode.equals("truncation")){
				throw new Exception(
						"survival-selection-mode must be set to none,\n"
								+ "truncation, or decimation. Currently set to "
								+ _survivalSelectionMode + ".");
			}
			
			_survivalRatio = GetFloatParam("survival-ratio", true);
			if (Float.isNaN(_survivalRatio)) {
				_survivalRatio = defaultSurvivalRatio;
			}

			_survivalPopulationSize = (int) ((_survivalRatio * GetFloatParam("population-size")) / 100.0);

			_decimationTournamentSize = (int) GetFloatParam("survival-tournament-size", true);
			if (Float.isNaN(GetFloatParam("survival-tournament-size", true))) {
				_decimationTournamentSize = defaultDecimationTournamentSize;
			}
			
		}

		_mutationPercent = GetFloatParam("mutation-percent");
		_crossoverPercent = GetFloatParam("crossover-percent");
		_maxGenerations = (int) GetFloatParam("max-generations");
		_tournamentSize = (int) GetFloatParam("tournament-size");
		
		// trivial-geography-radius is an optional parameter
		if(Float.isNaN(GetFloatParam("trivial-geography-radius", true))){
			_trivialGeographyRadius = defaultTrivialGeographyRadius;
		}
		else{
			_trivialGeographyRadius = (int) GetFloatParam("trivial-geography-radius", true);
		}
		
		// verbose is an optional parameter
		String verboseString = GetParam("verbose", true);
		if(verboseString == null){
			_verbose = defaultVerbose;
		}
		else {
			if(verboseString.equals("true")){
				_verbose = true;
			} else if (verboseString.equals("false")) {
				_verbose = false;
			} else {
				_verbose = defaultVerbose;
			}
		}
		
		_checkpointPrefix = GetParam("checkpoint-prefix", true);
		_checkpoint = new Checkpoint(this);

		ResizeAndInitialize((int) GetFloatParam("population-size"));

		_outputfile = GetParam("output-file", true);
		if (_outputfile != null){
			File outputFile = new File(_outputfile);
			if (outputFile.exists()) {
				throw new Exception(
						"A file already exists at output-file location \""
								+ _outputfile + "\".");
			}
			
			_outputStream = new FileOutputStream(outputFile);
			_alsoPrintToOutput = true;
		}
		
		// Print the parameters
		PrintParameters();
		
	}

	private void PrintParameters() throws Exception {
		
		Print(" Parameters \n");
		Print(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n");
		
		ArrayList<String> sortedParameters = new ArrayList<String>();
		for(String param : _parameters.keySet()){
			sortedParameters.add(param);
		}
		
		Collections.sort(sortedParameters);
		
		for(String param : sortedParameters){
			Print(param + " = " + _parameters.get(param) + "\n");
		}

		Print(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n");
		Print("\n\n");
		
	}

	/**
	 * Sets the population size and resets the GA generation count, as well as
	 * initializing the population with random individuals.
	 * 
	 * @param inSize
	 *            the size of the new GA population.
	 */

	protected void ResizeAndInitialize(int inSize) throws Exception {
		_populations = new GAIndividual[2][inSize];
		_currentPopulation = 0;
		_generationCount = 0;
		
		Object iObject = _individualClass.newInstance();

		if (!(iObject instanceof GAIndividual))
			throw new Exception(
					"individual-class must inherit from class GAIndividual");

		GAIndividual individual = (GAIndividual) iObject;

		for (int i = 0; i < inSize; i++) {
			_populations[0][i] = individual.clone();
			InitIndividual(_populations[0][i]);
		}

	}

	/**
	 * Run the main GP run loop until the generation limit it met.
	 * 
	 * @return true, indicating the the execution of the GA is complete.
	 */

	public boolean Run() throws Exception {
		return Run(-1);
	}

	/**
	 * Run the main GP run loop until the generation limit it met, or until the
	 * provided number of generations has elapsed.
	 * 
	 * @param inGenerations
	 *            The maximum number of generations to run during this call.
	 *            This is distinct from the hard generation limit which
	 *            determines when the GA is actually complete.
	 * @return true if the the execution of the GA is complete.
	 */

	public boolean Run(int inGenerations) throws Exception {
		// inGenerations below must have !=, not >, since often inGenerations
		// is called at -1
		while (!Terminate() && inGenerations != 0) {
			BeginGeneration();
			
			Evaluate();
			SurvivalSelection();
			Reproduce();

			EndGeneration();
			
			Print(Report());
			
			Checkpoint();
			System.gc();
			
			_currentPopulation = (_currentPopulation == 0 ? 1 : 0);
			_generationCount++;
			inGenerations--;
		}
		
		if(Terminate()){
			// Since this value was changed after termination conditions were
			// set, revert back to previous state.
			_currentPopulation = (_currentPopulation == 0 ? 1 : 0);
		
			Print(FinalReport());
		}
		
		return (_generationCount < _maxGenerations);
	}

	/**
	 * Determine whether the GA should terminate. This method may be overridden
	 * by subclasses to customize GA behavior.
	 */

	public boolean Terminate() {
		return (_generationCount >= _maxGenerations || Success());
	}

	/**
	 * Determine whether the GA has succeeded. This method may be overridden by
	 * subclasses to customize GA behavior.
	 */

	protected boolean Success() {
		return _bestMeanFitness == 0.0;
	}

	/**
	 * Evaluates the current population and updates their fitness values. This
	 * method may be overridden by subclasses to customize GA behavior.
	 */
	protected void Evaluate() {
		double totalFitness = 0;
		_bestMeanFitness = Float.MAX_VALUE;

		for (int n = 0; n < _populations[_currentPopulation].length; n++) {
			GAIndividual i = _populations[_currentPopulation][n];
			
			EvaluateIndividual(i);
			
			totalFitness += i.GetFitness();

			if (i.GetFitness() < _bestMeanFitness) {
				_bestMeanFitness = i.GetFitness();
				_bestIndividual = n;
				_bestErrors = i.GetErrors();
			}
		}
		
		_populationMeanFitness = totalFitness / _populations[_currentPopulation].length;
	}

	/**
	 * Implements the survival selection mode given as a parameter. By default,
	 * no survival selection is used, meaning all individuals have a chance to
	 * reproduce.
	 * 
	 * If truncation survival selection is used, only the top "survival-ratio"
	 * percent of the population survive to reproduce. survival-ratio is a
	 * parameter set by the user.
	 * 
	 * If decimation survival selection is used, survival-ratio * population
	 * size individuals survive to reproduce, but these individuals are chosen
	 * in a different manner than in truncation survival. Here, a series of
	 * tournaments of size survival-tournament-size are conducted, where the
	 * participant with the least fitness is removed from the population (does
	 * not survive). These tournaments are conducted until the remaining
	 * population is survival-ratio percent of the total population.
	 * 
	 * Instead of removing individuals from the population who don't survive,
	 * the surviving population instead will be moved to the lowest
	 * _survivalPopulationSize indices in the population, which will be the only
	 * portion of the population chosen from for reproduction.
	 */
	protected void SurvivalSelection() throws Exception {

		if (_survivalSelectionMode.equals("none")) {
			return;
		} else if (_survivalSelectionMode.equals("truncation")) {
			TruncationSelection();
		} else if (_survivalSelectionMode.equals("decimation")) {
			DecimationSelection();
		} else {
			throw new Exception(
					"Unrecognized value for _survivalSelectionMode: "
							+ _survivalSelectionMode);
		}
	}

	/**
	 * Moves best _survivalPopulationSize individuals into beginning of the
	 * population array.
	 */
	protected void TruncationSelection() {
		
		for(int i = 0; i < _survivalPopulationSize; i++){
			int bestRemainingIndividual = GetBestIndividualAfterIndex(i);
			
			// Swap i and bestRemainingIndividual
			GAIndividual swaptemp = _populations[_currentPopulation][i];
			_populations[_currentPopulation][i] = _populations[_currentPopulation][bestRemainingIndividual];
			_populations[_currentPopulation][bestRemainingIndividual] = swaptemp;
			
			// Check if best individual has been moved
			if(_bestIndividual == bestRemainingIndividual){
				_bestIndividual = i;
			} else if(_bestIndividual == i){
				_bestIndividual = bestRemainingIndividual;
			}
		}
		
	}

	/**
	 * Returns best fitness (lowest error) individual after or at the parameter
	 * inIndex.
	 */
	private int GetBestIndividualAfterIndex(int inIndex) {
		int bestIndex = inIndex;
		float bestFitness = _populations[_currentPopulation][inIndex]
				.GetFitness();
		
		for (int j = inIndex; j < _populations[_currentPopulation].length; j++) {
			if (_populations[_currentPopulation][j].GetFitness() < bestFitness) {
				bestIndex = j;
				bestFitness = _populations[_currentPopulation][j].GetFitness();
			}
		}
		
		return bestIndex;
	}

	/**
	 * Moves a _survivalPopulationSize number of individuals into beginning of
	 * the population array, based on a loser tournament selection.
	 */
	protected void DecimationSelection() {
	
		// Loop from last index in population to the one just after
		// _survivalPopulationSize, using tournaments of size
		// _decimationTournamentSize. Each iteration, move the loser of the
		// tournament to the current index.
		for (int i = _populations[_currentPopulation].length - 1;
			 i >= _survivalPopulationSize; i--) {
			
			int tourneyLoser = GetTourneyLoser(i);
			
			// Swap i and tourneyLoser
			GAIndividual swaptemp = _populations[_currentPopulation][i];
			_populations[_currentPopulation][i] = _populations[_currentPopulation][tourneyLoser];
			_populations[_currentPopulation][tourneyLoser] = swaptemp;
			
			
			//TODO REMOVE - print the pop
//			System.out.println("Pop: ");
//			for(int qq = 0; qq < _populations[_currentPopulation].length; qq++){
//				System.out.println(_populations[_currentPopulation][qq].GetFitness());
//			}
//			
//			System.out.println("tourney loser = " + tourneyLoser + "\n\n");
//			System.exit(0);
			
			
			// Check if best individual has been moved
			// NB: Although unlikely, this could happen if many individuals
			// tie for the best fitness
			if(_bestIndividual == tourneyLoser){
				_bestIndividual = i;
			} else if(_bestIndividual == i){
				_bestIndividual = tourneyLoser;
			}			
		}

	}

	private int GetTourneyLoser(int inIndex) {
	
		int worstIndex = -1;
		float worstFitness = -1;
		
		for (int j = 0; j < _decimationTournamentSize; j++) {
			int selectedIndividual = -1;
			
			if(_trivialGeographyRadius > 0) {
				// Trivial geography
				//TODO implement and remove next line
				selectedIndividual = _RNG.nextInt(inIndex + 1);
				
			} else {
				selectedIndividual = _RNG.nextInt(inIndex + 1);
			}
			
			if (_populations[_currentPopulation][selectedIndividual]
					.GetFitness() > worstFitness) {
				worstIndex = selectedIndividual;
				worstFitness = _populations[_currentPopulation][selectedIndividual]
						.GetFitness();
			}
		
		}
		
		//TODO implement trivial geography as below
//		if (_trivialGeographyRadius > 0) {
//			int index = (_RNG.nextInt(_trivialGeographyRadius * 2) - _trivialGeographyRadius)
//					+ inIndex;
//			if (index < 0)
//				index += inPopsize;
//
//			return (index % inPopsize);
//		} else {
//			return _RNG.nextInt(inPopsize);
//		}
		
		return worstIndex;
	}

	/**
	 * Reproduces the current population into the next population slot. This
	 * method may be overridden by subclasses to customize GA behavior.
	 */
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

			_populations[nextPopulation][n] = next;
			
		}
	}

	/**
	 * Prints out population report statistics. This method may be overridden by
	 * subclasses to customize GA behavior.
	 * @throws Exception 
	 */
	protected String Report(){
		String report = "";

		if (_verbose) {
			report += "\n";
			report += ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n";
			report += ";; -*- Report at generation " + _generationCount + "\n";
		} else {
			System.out.print(".");
			if ((_generationCount + 1) % 50 == 0)
				System.out.print("\n");
		}

		return report;
	}

	protected String FinalReport() {
		String report = "\n";

		report += "<<<<<<<<<<<<<>>>>>>>>>>>>>\n";

		if (Success()) {
			report += "SUCCESS";
		} else {
			report += "FAILURE";
		}
		report += " at generation " + (_generationCount - 1) + "\n";
		report += "<<<<<<<<<<<<<>>>>>>>>>>>>>\n";

		return report;
	}

	/**
	 * Logs output of the GA run to the appropriate location (which may be
	 * stdout, or a file).
	 */

	public void Print(String inStr) throws Exception {
		if (_outputStream != null) {
			_outputStream.write(inStr.getBytes());
			_outputStream.flush();
		}
		
		_checkpoint.report.append(inStr);
		
		if(_alsoPrintToOutput){
			System.out.write(inStr.getBytes());
			System.out.flush();
		}
	}

	/**
	 * Preforms a tournament selection, return the best individual from a sample
	 * of the given size.
	 * 
	 * @param inSize
	 *            The number of individuals to consider in the tournament
	 *            selection.
	 */

	protected GAIndividual TournamentSelect(int inSize, int inIndex) {

		int best = TournamentSelectionIndex(inIndex, _survivalPopulationSize);
		float bestFitness = _populations[_currentPopulation][best].GetFitness();

		for (int n = 0; n < inSize - 1; n++) {
			int candidate = TournamentSelectionIndex(inIndex, _survivalPopulationSize);
			float candidateFitness = _populations[_currentPopulation][candidate]
					.GetFitness();

			if (candidateFitness < bestFitness) {
				best = candidate;
				bestFitness = candidateFitness;
			}
		}

		return _populations[_currentPopulation][best];
	}

	/**
	 * Produces an index for a tournament selection candidate.
	 * 
	 * @param inIndex
	 *            The index which is to be replaced by the current reproduction
	 *            event (used only if trivial-geography is enabled).
	 * @param inPopsize
	 *            The size of the population.
	 * @return the index for the tournament selection.
	 */

	protected int TournamentSelectionIndex(int inIndex, int inPopsize) {
		if (_trivialGeographyRadius > 0) {
			int index = (_RNG.nextInt(_trivialGeographyRadius * 2) - _trivialGeographyRadius)
					+ inIndex;
			if (index < 0)
				index += inPopsize;

			return (index % inPopsize);
		} else {
			return _RNG.nextInt(inPopsize);
		}
	}

	/**
	 * Clones an individual selected through tournament selection.
	 * 
	 * @return the cloned individual.
	 */

	protected GAIndividual ReproduceByClone(int inIndex) {
		return TournamentSelect(_tournamentSize, inIndex).clone();
	}
	
	/**
	 * Computes the absolute-average-of-errors fitness from an error vector.
	 * 
	 * @return the average error value for the vector.
	 */
	protected float AbsoluteAverageOfErrors(ArrayList<Float> inArray) {
		float total = 0.0f;

		for (int n = 0; n < inArray.size(); n++)
			total += Math.abs(inArray.get(n));

		if(Float.isInfinite(total))
			return Float.MAX_VALUE;

		return (total / inArray.size());
	}

	/**
	 * Retrieves GAIndividual at index i from the current population.
	 * @param i
	 * @return GAIndividual at index i
	 */
	public GAIndividual GetIndividualFromPopulation(int i){
		return _populations[_currentPopulation][i];
	}
	
	/**
	 * Retrieves best individual from the current population.
	 * @return best GAIndividual in population
	 */
	public GAIndividual GetBestIndividual(){
		if(Terminate()){
			return _populations[_currentPopulation][_bestIndividual];
		}
		int oldpop = (_currentPopulation == 0 ? 1 : 0);
		return _populations[oldpop][_bestIndividual];
	}
	
	/**
	 * @return population size
	 */
	public int GetPopulationSize(){
		return _populations[_currentPopulation].length;
	}
	
	/**
	 * 
	 * @return generation count
	 */
	public int GetGenerationCount(){
		return _generationCount;
	}
	
	public int GetMaxGenerations(){
		return _maxGenerations;
	}
	
	/**
	 * Called at the beginning of each generation. This method may be overridden
	 * by subclasses to customize GA behavior.
	 * @throws Exception 
	 */
	protected void BeginGeneration() throws Exception {
	}

	/**
	 * Called at the end of each generation. This method may be overridden by
	 * subclasses to customize GA behavior.
	 */
	protected void EndGeneration() {
	};

	abstract protected void InitIndividual(GAIndividual inIndividual);

	/**
	 * Determines and sets a GAIndividual's fitness and errors.
	 * @param inIndividual
	 */
	abstract protected void EvaluateIndividual(GAIndividual inIndividual);

	abstract public float EvaluateTestCase(GAIndividual inIndividual,
			Object inInput, Object inOutput);

	abstract protected GAIndividual ReproduceByCrossover(int inIndex);

	abstract protected GAIndividual ReproduceByMutation(int inIndex);

	protected void Checkpoint() throws Exception {
		if (_checkpointPrefix == null)
			return;

		File file = new File(_checkpointPrefix + _checkpoint.checkpointNumber
				+ ".gz");
		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(file)));

		out.writeObject(_checkpoint);
		out.flush();
		out.close();
		System.out.println("Wrote checkpoint file " + file.getAbsolutePath());
		_checkpoint.checkpointNumber++;
	}

}
