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
import java.util.HashMap;

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

		GA ga;
		
		// Fetch parameters important to PshGPRuns
		HashMap<String, String> tempParams = Params.ReadFromFile(new File(args[0]));
		boolean verbose = !"false".equals(tempParams.get("verbose")); //Set verbose
		tempParams.put("verbose", "false");
		GA tempGA = GA.GAWithParameters(tempParams);
		
		int runs = (int) tempGA.GetFloatParam("runs");
		
		String outputDirectory = tempGA.GetParam("output-directory");
		String outputFilePrefix = tempGA.GetParam("output-file-prefix");
		String outputFileSuffix = tempGA.GetParam("output-file-suffix");

		if(outputDirectory.charAt(outputDirectory.length() - 1) != '/'){
			outputDirectory += '/';
		}
		File parentDirectory = new File(outputDirectory);
		parentDirectory.mkdirs();
		

		System.out.println("\n\n+++++++++++++++ Beginning PushGPRuns +++++++++++++++\n");
		
		for(int i = 0; i < runs; i++){
			System.out.println("==========     Beginning run " + i
					+ "      ==========");
			if (!verbose) {
				System.out
						.println("========== Each \".\" is a generation ==========");
			}
			System.out.println();

			// Create output file string
			String outputFileString = outputDirectory + outputFilePrefix + i
					+ outputFileSuffix;

			// Setup GA
			HashMap<String, String> params = Params.ReadFromFile(new File(args[0]));
			params.put("output-file", outputFileString);
			ga = GA.GAWithParameters(params);

			// Run GA
			ga.Run();
		}
		
	}

}
