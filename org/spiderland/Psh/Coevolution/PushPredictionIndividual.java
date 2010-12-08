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

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

public class PushPredictionIndividual extends PredictionGAIndividual{
	private static final long serialVersionUID = 1L;
	
	protected Program _program;

	protected PushGP _solutionGA;
	
	public PushPredictionIndividual() {
		_solutionGA = null;
	}
	
	public PushPredictionIndividual(Program inProgram, PushGP inSolutionGA) {
		_program = inProgram;
		_solutionGA = inSolutionGA;
	}
	
	@Override
	public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
		//TODO implement _program being run to predict fitness



		
		return -2999;
	}

	@Override
	public GAIndividual clone() {
		return new PushPredictionIndividual(_program, _solutionGA);
	}
	
	void SetProgram(Program inProgram) {
		if (inProgram != null)
			_program = new Program(inProgram);
	}

	public String toString() {
		return _program.toString();
	}
	
}
