package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

public class PushFitnessPredictionInterpreter extends Interpreter {
	private static final long serialVersionUID = 1L;
	
	PushFitnessPredictionInterpreter(){
		super();
	}
	
	/**
	 * Loads a Push program into the interpreter's exec stack. Overrides
	 * normal interpreter function to make it so that the program is not
	 * loaded onto the code stack.
	 * 
	 * @param inProgram
	 *            The program to load.
	 */
	@Override
	public void LoadProgram(Program inProgram) {
		_execStack.push(inProgram);
	}

}
