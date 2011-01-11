import math
import os
import sys

# Set these before running:
outputDirectory = "../../GAExperiments/decimation5NoXover/PerBitMut/ssmTRUNCATION/pop500gen501runs1000"
outputFilePrefix = "run"
outputFileSuffix = ".txt"

population_size = 500
max_generations = 501
z = 0.99

# Don't have to change anything below!
really_huge_number = 999999999999999999999999

# M = population size
# G = maximum generations in a run
# z = desired probability of finding a solution
def computational_effort(success_generations, runs, M, G, z):
    min_effort = really_huge_number
    for i in range(G):
        effort = number_individuals_evaluated(success_generations,
                                              runs, M, i, z)
        
        # Uncomment following line to print effort per generations
        print "effort(" +  str(i) + ") = " + str(effort)
        
        min_effort = min(min_effort, effort)

    return min_effort

def number_individuals_evaluated(success_generations, runs, M, i, z):
    return (M * (i + 1) *
            number_of_required_independent_runs(success_generations,
                                                runs, i, z))
        
def number_of_required_independent_runs(success_generations, runs, i, z):
    cumulative_probability = cumulative_probability_of_success(success_generations,
                                                               runs, i)
    if cumulative_probability == 0 or cumulative_probability == 1:
        return really_huge_number
    return int(math.ceil(math.log(1.0 - z) / math.log(1.0 - cumulative_probability))) #real one
    #return math.log(1.0 - z) / math.log(1.0 - cumulative_probability) #no ceil

def cumulative_probability_of_success(success_generations, runs, i):
    total_prob = 0.0
    for j in range(i + 1):
        total_prob += probability_of_success(success_generations, runs, j)
    return total_prob

def probability_of_success(success_generations, runs, i):
    return float(success_generations.count(i)) / float(runs)


#print computational_effort([10, 11, 12, 13, 14, 20, 30], 10, 1000, 51, 0.99)

# Main area
if outputDirectory[-1] != '/':
    outputDirectory += '/'
dirList = os.listdir(outputDirectory)

print "Computing Computational Effort..."

i = 0
runs = 0
success_generations = []
while (outputFilePrefix + str(i) + outputFileSuffix) in dirList:
    sys.stdout.write('.')
    if i % 50 == 49:
        print
    
    runs = i + 1 # After this loop ends, runs should be correct
    fileName = (outputFilePrefix + str(i) + outputFileSuffix)
    f = open(outputDirectory + fileName)

    for line in f:
        if "SUCCESS" in line:
            gen = int(line.split("generation ")[-1])
            success_generations.append(gen)
            
        if "FAILURE" in line:
            break

    i += 1


print
print "Success generation counts:"
if len(success_generations) == 0:
    print "    No Successful Generations"
else:
    for i in range(min(success_generations), max(success_generations) + 1):
        print "    Runs succeeding in gen", i, "=", success_generations.count(i)

print
computational_effort = computational_effort(success_generations, runs, population_size, max_generations, z)
print
print "Computational Effort =", computational_effort


print "Number of successful runs =", len(success_generations)
