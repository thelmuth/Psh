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

import java.io.File;

import org.spiderland.Psh.*;

public class PshGPRuns {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("Usage: PshGPRuns paramfile");
			System.exit(0);
		}

		GA ga = null;
		int runs = 1;
		boolean verbose = true;
		
		for(int i = 0; i < runs; i++){
			ga = GA.GAWithParameters(Params.ReadFromFile(new File(args[0])));
			
			if (i == 0) {
				runs = (int) ga.GetFloatParam("runs");
				verbose = ga._verbose;
				System.out.println("\n\n++++++++++ Beginning PushGPRuns for " + runs
						+ " runs ++++++++++\n\n");
				if(runs < 1)
					break;
			}
			
			System.out.println("==========     Beginning run " + i
					+ "      ==========");
			if (!verbose) {
				System.out
						.println("========== Each \".\" is a generation ==========");
			}

			ga.Run();
		}
		
	}

}
