import sys
import os

base_command = "java -jar /Users/andreasamartin/XHAIL/examples/xhail.jar -c /Users/andreasamartin/clasp -g /Users/andreasamartin/Gringo/gringo -b"

base_command_alt = "/Users/andreasamartin/Gringo/gringo"
pipe_to = "| /Users/andreasamartin/clasp "

if len(sys.argv)<2:
    print("No files provided...Exiting")
    exit(1)

#file_name = "file.txt"

supplementary_command = " ".join(sys.argv[1:])
"""
if ">" in sys.argv:
    index_of_bracket = sys.argv.index(">")
    supplementary_command = " ".join(sys.argv[1:index_of_bracket])
    file_name =  sys.argv[index_of_bracket + 1]
"""

stream = os.popen(f'{base_command} {supplementary_command}')
#stream = os.popen(f'{base_command_alt} {supplementary_command} {pipe_to}')


output = stream.read()
#output2 = ""

#line_parts =  output.split("\n")
#for line in line_parts:
#    if "[" in line and "m" in line:
#        i_of_m =  line.index("m")
#        output2 =  output2 +  line[i_of_m +  1 : ] + "\n"
#    else:
#        output2 =  output2 +  line.strip() + "\n"




#print(output, file=open("out_xxx", "w"))
#with open(file_name, 'w') as writer:
    #writer.write(output2)
 #   writer.write(output)

#print(output2)
print(output)