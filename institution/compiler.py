
from instalialcompiler import InstalCompiler
import sys
import json
import ast
#def compile_ial(self, ial_ir: dict) -> str

#def instal_print_all(self, ial_ir: dict) -> None
    
if len(sys.argv)>1:
    function_to_call = sys.argv[1]
    #print(function_to_call)
    parameters_as_string = sys.argv[2] 

    if function_to_call == "print_message":
        compiler = InstalCompiler()
        compiler.print_message(parameters_as_string)
    elif function_to_call == "compile_ial":
        compiler = InstalCompiler()
        
        #parameters_as_string = {'names': {'institution': 'rooms', 'bridge': None, 'source': None, 'sink': None}, 'types': {'Person': 'person', 'Role': 'role', 'Location': 'location', 'Number': 'number'}, 'exevents': {'enter': ['Person', 'Location'], 'leave': ['Person', 'Location']}, 'inevents': {'arrive': ['Person', 'Location'], 'exit': ['Person', 'Location'], 'barred': ['Person', 'Location'], 'deniedEntry': ['Person', 'Location'], 'deniedExit': ['Person', 'Location']}, 'vievents': {}, 'fluents': {'role': ['Person', 'Role'], 'in_room': ['Person', 'Location'], 'max': ['Location', 'Number'], 'min': ['Location', 'Number'], 'addone': ['Number', 'Number'], 'lessone': ['Number', 'Number'], 'smaller': ['Number', 'Number'], 'bigger': ['Number', 'Number'], 'aggrCount': ['Number']}, 'noninertial_fluents': {'potential_occupant_conflict': ['Location'], 'potential_role_conflict': ['Location'], 'room_conflict': ['Location'], 'permExit': ['Person', 'Location'], 'permEntry': ['Person', 'Location'], 'in_some_room': ['Person'], 'occupancy': ['Location', 'Number'], 'typeCount': ['Location', 'Role', 'Number']}, 'obligation_fluents': [], 'generates': [[['leave', ['P', 'L']], [['deniedExit', ['P', 'L']]], ['not', ['permExit', ['P', 'L']]], []], [['leave', ['P', 'L']], [['exit', ['P', 'L']]], ['permExit', ['P', 'L']], []], [['enter', ['P', 'L']], [['deniedEntry', ['P', 'L']]], ['not', ['permEntry', ['P', 'L']]], []], [['enter', ['P', 'L']], [['arrive', ['P', 'L']]], ['permEntry', ['P', 'L']], []]], 'initiates': [[['exit', ['P', 'L']], [['perm', ['enter', ['P', 'L']]], ['pow', ['enter', ['P', 'L']]], ['perm', ['arrive', ['P', 'L']]]], []], [['arrive', ['P', 'L']], [['perm', ['leave', ['P', 'L']]], ['pow', ['leave', ['P', 'L']]], ['perm', ['exit', ['P', 'L']]]], []], [['arrive', ['P', 'L']], [['in_room', ['P', 'L']]], []]], 'terminates': [[['exit', ['P', 'L']], [['in_room', ['P', 'L']]], []], [['exit', ['P', 'L']], [['perm', ['leave', ['P', 'L']]], ['pow', ['leave', ['P', 'L']]], ['perm', ['exit', ['P', 'L']]]], []], [['arrive', ['P', 'L']], [['perm', ['enter', ['P', 'L']]], ['pow', ['enter', ['P', 'L']]], ['perm', ['arrive', ['P', 'L']]]], []]], 'whens': [[['potential_role_conflict', ['L']], ['and', ['and', ['and', ['and', ['and', ['and', ['and', ['and', ['and', ['potential_occupant_conflict', ['L']], ['in_room', ['A1', 'L']]], ['in_room', ['A2', 'L']]], ['in_room', ['A3', 'L']]], ['role', ['A1', 'x']]], ['role', ['A2', 'y']]], ['role', ['A3', 'y']]], ['!=', ['A1', 'A2']]], ['!=', ['A2', 'A3']]], ['!=', ['A1', 'A3']]]], [['potential_occupant_conflict', ['L']], ['occupancy', ['L', '3']]], [['permExit', ['P', 'L']], ['in_room', ['P', 'L']]], [['typeCount', ['L', 'R', 'N']], ['and', ['and', ['aggrCount', ['N']], ['in_room', ['P', 'L']]], ['role', ['P', 'R']]]], [['occupancy', ['L', 'N']], ['and', ['aggrCount', ['N']], ['in_room', ['P', 'L']]]], [['in_some_room', ['P']], ['in_room', ['P', 'L']]], [['permEntry', ['P', 'L']], ['not', ['in_some_room', ['P']]]]], 'initials': [[['perm', ['enter', ['P', 'L']]], []], [['pow', ['enter', ['P', 'L']]], []], [['perm', ['arrive', ['P', 'L']]], []], [['perm', ['deniedEntry', ['P', 'L']]], []], [['perm', ['deniedExit', ['P', 'L']]], []]], 'cross_initiation_fluents': [], 'cross_termination_fluents': [], 'cross_generation_fluents': [], 'xgenerates': [], 'xinitiates': [], 'xterminates': []}

        f = open(parameters_as_string, "r")
        file_contents = f.read()

        #print("\n\n\n",file_contents, "\n\n\n")
       # x = compiler.compile_ial(parameters_as_string)
        #file_contents = file_contents.replace("'",'"')

        #print("\n\n\n ",file_contents, "\n\n\n")
       # x = compiler.compile_ial(json.loads(file_contents).replace("'",'"'))
        #json_data = ast.literal_eval(json.dumps(file_contents))

        
        #x = compiler.compile_ial(json.loads(file_contents))
       # x = compiler.compile_ial(json.loads(json_data))
        y = eval(file_contents)
        x = compiler.compile_ial(y)
        print("\n\n\n",x)