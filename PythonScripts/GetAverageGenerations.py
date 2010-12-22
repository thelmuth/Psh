import os

outputDirectory = "zzzzzTESTING/tests"
outputFilePrefix = "schneau"
outputFileSuffix = ".txt"

path= "../" + outputDirectory + "/"
dirList = os.listdir(path)

print "Run terminations:\n"

i = 0
totalGen = 0
while (outputFilePrefix + str(i) + outputFileSuffix) in dirList:
    fileName = (outputFilePrefix + str(i) + outputFileSuffix)
    f = open(path + fileName)

    for line in f:
        if ("SUCCESS" in line) or ("FAILURE" in line):
            print line[0:-1]
            gen = int(line.split("generation ")[-1])
            totalGen += gen

    i += 1

print
print "Average generations = ", (totalGen / (i+1))
