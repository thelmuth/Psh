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

import org.spiderland.Psh.*;

public class PushFitnessPredictionIndividual extends PredictionGAIndividual {
	private static final long serialVersionUID = 1L;

	public Program _program;
	protected Interpreter _interpreter; // The interpreter of the
										// PushFitnessPrediction GA

	protected static int _executionLimit;

	public PushFitnessPredictionIndividual() {
	}

	public PushFitnessPredictionIndividual(Program inProgram) {
		SetProgram(inProgram);
	}

	public PushFitnessPredictionIndividual(Program inProgram, Interpreter inI) {
		SetProgram(inProgram);
		_interpreter = inI;
	}

	@Override
	public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
		// TODO test this once everything else is up and running

		_interpreter.ClearStacks();

		// Set input
		Program inputProgram = pgpIndividual._program;
		_interpreter.codeStack().push(inputProgram);
		_interpreter.inputStack().push(inputProgram);

		//TODO remove
//		System.out.println("Before: " + _executionLimit);
//		System.out.println("Program: " + _program);
//		System.out.println(_interpreter + "\n");
		
		// Run prediction program
		_interpreter.Execute(_program, _executionLimit);
		
		

//		int steppps = _interpreter.Execute(_program, _executionLimit);

		//TODO remove
//		System.out.println("After " + steppps + " steps: ");
//		System.out.println(_interpreter + "\n\n\n");

		// Predict very large fitness if there is no result on the stack.
		if (_interpreter.floatStack().size() == 0) {
			return 1000000000;
		}
		
		// Get result
		float predictedFitness = _interpreter.floatStack().top();

		//TODO remove
		//System.out.println("woooo good result");
		//System.out.println(predictedFitness);
		//System.exit(0);
		
		return predictedFitness;
	}

	@Override
	public GAIndividual clone() {
		return new PushFitnessPredictionIndividual(_program, _interpreter);
	}

	public void SetProgram(Program inProgram) {
		if (inProgram != null)
			_program = new Program(inProgram);
	}

	public void SetInterpreter(Interpreter inI) {
		_interpreter = inI;
	}

	public static void SetExecutionLimit(int inExecutionLimit) {
		_executionLimit = inExecutionLimit;
	}

	public String toString() {
		return _program.toString();
	}

}
