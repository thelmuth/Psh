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

import java.io.Serializable;

public class InputPusher implements Serializable {
	private static final long serialVersionUID = 1L;

	public void pushInput(Interpreter inI, int n) {
		ObjectStack _stack = inI.inputStack();

		if (_stack.size() > n) {
			Object inObject = _stack.peek(n);

			if (inObject instanceof Integer) {
				intStack istack = inI.intStack();
				istack.push((Integer) inObject);
			} else if (inObject instanceof Number) {
				floatStack fstack = inI.floatStack();
				fstack.push(((Number) inObject).floatValue());
			} else if (inObject instanceof Boolean) {
				booleanStack bstack = inI.boolStack();
				bstack.push((Boolean) inObject);
			} else if( inObject instanceof Program){
				ObjectStack cstack = inI.codeStack();
				cstack.push((Program) inObject);
			} else {
				System.err.println("Error during input.index - object "
						+ inObject.getClass()
						+ " is not a legal object according to "
						+ this.getClass() + ".");
				System.exit(0);
			}
		}
	}

}
