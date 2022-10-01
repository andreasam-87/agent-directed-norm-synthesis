
#from instalialcompiler import InstalCompiler
#import sys
#import json
#import ast


import re
import sys, getopt

dict_file_path = ''
ial_file_path = ''
xhail_out_path = ''
output_file_path = ''

empty = False
count=0

#3 useful functions from instalialcompiler

def getConsoleParams():
    global ial_file_path
    global dict_file_path
    global xhail_out_path
    global output_file_path

    argv = sys.argv[1:]

    try:
        opts, args = getopt.getopt(argv,"i:d:x:o:",["ifile=","dfile=","dfile=","xfile=","ofile="])
    except getopt.GetoptError:
        print ('Error with getting the arguments')
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-i", "--ifile"):
            ial_file_path = arg
        elif opt in ("-d", "--dfile"):
            dict_file_path = arg
        elif opt in ("-x", "--xfile"):
            xhail_out_path = arg
        elif opt in ("-o", "--ofile"):
            output_file_path = arg

    #print("Files - ",dict_file_path,ial_file_path,xhail_out_path,output_file_path)


def term2string(p):
    # Legacy.
    args = p[1]
    r = ''
    if len(args) == 0:
        r = p[0]
    elif len(args) == 1:
        r = p[0] + '(' + args[0] + ')'
    elif p[0] in ['==', '!=', '<', '>', '<=', '>=']:
        r = p[1][0] + p[0] + p[1][1]
    elif p[0] == 'and':
        r = term2string(p[1]) + ' ' + p[0] + \
            ' ' + term2string(p[2])
    else:
        r = '(' + args[0]
        for x in args[1:]:
            r = r + ',' + x
        r = p[0] + r + ')'
    return r

def extendedterm2string(p):
    # Legacy.
    if p[0] in ['perm', 'viol', 'pow']:
        r = p[0] + '(' + term2string(p[1]) + ')'
    elif p[0] == 'obl':
        r = p[0] + '(' + term2string(p[1][0]) + ',' + term2string(p[1][1]) + ',' + term2string(
            p[1][2]) + ')'
    elif p[0] in ["tpow", "ipow", "gpow"]:
        r = p[0] + '(' + p[1][0] + ',' + \
            extendedterm2string(p[1][1]) + ',' + p[1][2] + ')'
    else:
        r = term2string(p)
    return r


def printCondition(c):
    returnStr = ""
    if not c:
        return returnStr
    if c[0] == 'and':
        returnStr += printCondition(c[1])
        returnStr += printCondition(c[2])
    elif c[0] == 'not':
        # need to store to string self.instal_print("   not")
        returnStr += " not "
        returnStr += printCondition(c[1])
    elif c[0] == '==':
        returnStr += c[1][0]+" == "+ c[1][1]
        #self.instal_print("   {l}=={r},".format(l=c[1][0], r=c[1][1]))
    elif c[0] == '!=':
        returnStr += c[1][0]+" != "+ c[1][1]
        #self.instal_print("   {l}!={r},".format(l=c[1][0], r=c[1][1]))
    elif c[0] == '<':
        returnStr += c[1][0]+" < "+ c[1][1]
        #self.instal_print("   {l}<{r},".format(l=c[1][0], r=c[1][1]))
    elif c[0] == '>':
        returnStr += c[1][0]+" > "+ c[1][1]
        #self.instal_print("   {l}>{r},".format(l=c[1][0], r=c[1][1]))
    elif c[0] == '<=':
        returnStr += c[1][0]+" == "+ c[1][1]
        #self.instal_print("   {l}<={r},".format(l=c[1][0], r=c[1][1]))
    elif c[0] == '>=':
        returnStr += c[1][0]+" == "+ c[1][1]
        #self.instal_print("   {l}>={r},".format(l=c[1][0], r=c[1][1]))
    elif type(c[0]) is list:
        returnStr += printCondition(c[0])
    else:
        #returnStr += " "+extendedterm2string(c) + ";"
        returnStr += extendedterm2string(c) + ";"
        #self.instal_print("   holdsat({fluent},{inst},I)," # 20190606 JAP: add arg to holdsat
           #                 .format(fluent=self.extendedterm2string(c), inst=institution))
    return returnStr

def getSpecificCondition(c,cond):
    global count
    #print("entered ")
    returnStr = ""
    
    if not c:
        return returnStr
    if c[0] == 'and':
        returnStr += getSpecificCondition(c[1],cond)
        if len(returnStr)<1:
            returnStr += getSpecificCondition(c[2],cond)
    elif c[0] == 'not':
        returnStr += "not "
        returnStr += getSpecificCondition(c[1],cond)
    elif c[0] in ['==', '!=', '<', '>', '<=', '>=']:
        returnStr += c[1][0]+" "+c[0]+" "+c[1][1]
    elif type(c[0]) is list:
        returnStr = getSpecificCondition(c[0],cond)
    else:
        
        if count==cond:
            returnStr = extendedterm2string(c)
            print("I'm returning - ", returnStr)
            return returnStr
        count+=1
        #print("count ", count)
        #else:
        #    returnStr = getSpecificCondition(c[1],cond)
        
    return returnStr

def readFile(path):
    f = open(path, "r")
    #ial_file = f.read()
    count = 0
    ial_file_contents = list()
    while True:
        count += 1
    
        # Get next line from file
        line = f.readline()
    
        # if line is empty
        # end of file is reached
        if not line:
            break

        ial_file_contents.append(line.strip())
    
    f.close()
    return ial_file_contents

def printToConsole():
    for i in range(len(ialfile)):
        print("line ",(i+1), " - ", ialfile[i], "\n")

def printToFile():
    output = ""
    for i in range(len(ialfile)):
        output+=(ialfile[i]+ "\n" )
    #print(output, file=open("rooms_revised.ial", "w"))
    print(output, file=open(output_file_path, "w"))

def saveEmptyFile():
    print("", file=open(output_file_path, "w"))


def printBlankFile():
    print("", file=open(output_file_path, "w"))

def checkIfTyped(pred,term):
    pred = pred.strip()
    term = term.strip()
    
    what=''
    found = False
    print("pred I am checking - ",pred)
    print("term I am checking - ",term)
    if pred == "holdsat":
        flu = y["fluents"]
        nflu = y["noninertial_fluents"]
        if term in flu: 
            found = True
        elif term in nflu: 
            found = True
        else:
            print("it is not a fluent found", flu," ", nflu)
        what='F'
    elif pred == "occurred":
        i_ev = y["inevents"]
        e_ev = y["exevents"]
        if term in i_ev: 
            found = True
        elif term in e_ev: 
            found = True
        what='E'
    else:
        print("ERROR")
    
    return found, what

def getRuleFromDict(what2Revise):

    #what2Revise = rulesList[int(rule)-1]
    #what2ReviseWith = "add::"+toAdd

    head = extendedterm2string(what2Revise[0])

    list_new = []
    howMany=1
    if len(what2Revise)>2:
        for i in range(len(what2Revise)):
            if i==0:
                continue
            else:
                #print("i is ",i)
                #print("List", i, " is ",what2Revise[i])
                if len(what2Revise[i]):     
                    list_new.append(what2Revise[i])
                    howMany+=1

        for i in range(howMany-2):
            list_new.insert(0,'and')
    else:
        list_new=what2Revise[1]
    
    #print("new list:  ",list_new)
    body = printCondition(list_new)

    #body = printCondition(what2Revise[1])
    body = body[:-1]
    
    #body = body.replace(";",",")
    
    print("The head is: ",head)
    print("The body is: ",body)
    #print("The body is: ",printCondition(what2Revise[1]), " left - ", what2Revise[1])
    print("length  ",len(what2Revise[1]))
    print("length of what to revise ",len(what2Revise))


    term = ""
    head_term = head.split("(")[0]
    #if head_term in 
    if what2Revise in nons:
        term = " when "
    elif what2Revise in gens:
        term = " generates "
    elif what2Revise in inits:
        term = " initiates "
    elif what2Revise in terms:
        term = " terminates "
    else:
        print("ERROR!!!!") 

    #######
    #determining where to put the if in the condition
    count = body.count(';')
    if count>0 and what2Revise not in nons: 
        #if more than one term in the condition, an IF needs to be placed after the first term
        arrBody = body.split(";")
        tempBody = ""
        for i in range(len(arrBody)):
            if i==0:
                tempBody += arrBody[i]+ " if"
            elif i==(len(arrBody)-1):
                tempBody += " "+arrBody[i]
            else:
                tempBody += " "+arrBody[i]+ ", "
        body = tempBody
    else: 
        body = body.replace(";",", ")
    #######

    
    oldRule = head+term+body+";"
    return oldRule,head,term,body


def getInstalPredicate(toAdd):
     #grab the predicate based on if it has variables pred(a,b) or not
    start = toAdd.find("(")+len("(")
    if toAdd.count("(")>1:
        end = toAdd.find(")")
        add = toAdd[start:end+1]
    else:
        end = toAdd.find(",")
        add = toAdd[start:end]
    tempAdd = add

    #print("this is what I will add - ", tempAdd)

    #this means we have some variables in the predicate
    if "(" in add: 
        start = add.find("(")+len("(")
        end = add.find(")")
        vars = add[start:end]
        print("Vars 1 ",vars)
        #if more than one variable exists
        if "," in vars:
            varsList = vars.split(",")
            for varsI in varsList: #loop the varibales
                print("V ",varsI)
                for i in range(len(types)): #loop the types for each variable
                    if varsI in types[i]:
                        print("found ",types[i])
                        typeDef = types[i]
                        break
                add = add.replace(varsI,typeDef[0].upper())
                """start = typeDef.find("(")#+len("(")
                addType = typeDef[:start]
                addType = addType.replace(addType[0],addType[0].upper())
                print("Type to add ",addType)
                addType = tempAdd.replace(varsI,addType)
                tempAdd = addType
                print("Type to add ",addType) """
        else: # this means we only have 1 variable
            
            #replace with the appropriate variable letter

            #print("V ",vars)
            #get the one item
            for i in range(len(types)):
                #print("List ",i," - ",types[i])
                if vars in types[i]:
                    print("found ",types[i])
                    typeDef = types[i]
                    break
            add = add.replace(vars,typeDef[0].upper())
            """addType = typeDef[:start]
            addType = addType.replace(addType[0],addType[0].upper())
            print("Type to add ",addType)
            addType = tempAdd.replace(vars,addType)
            print("Type to add ",addType)"""

    return add

###### CODE STARTS HERE ######

#####  Extract the dictionary   ######
what2Revise = ""
what2ReviseWith = ""
revisedRule =""
oldRule =""

getConsoleParams()

#path = "/Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt"
#f = open(path, "r")
f = open(dict_file_path, "r")
dict_contents = f.read()

#print("\n\n\n",file_contents)

y = eval(dict_contents)
#need to edit this and print it out again using instalcompiler
#gen, in, term, non

#print("\n\n\n",y,"\n",type(y))

gens = y["generates"]
#print("\n\n", gens," len - ", len(gens)) #should be 4, is 4


#these are stored based on the event that initiates them, 
#one event initates 3 separate fluents hence the discrepancy, 
# have to think how to fix for initiates and terminates

inits = y["initiates"]
#print("\n\n", inits," len - ", len(inits)) #should be 7, only 3

terms = y["terminates"]
#print("\n\n",terms, " len - ", len(terms)) #should be 7, only 3


nons = y["whens"]
#print("\n\n",nons, " type - ", type(nons), " len - ", len(nons)) #should be 8, is 8

#concatenate the lists together
rulesList = gens + inits + terms + nons
#print("rulesList is ",len(rulesList)," items long")

#for k in nons:
#    if k is "capacityExceededViol":
#        "print found"
#print("\n\n",y["capacityExceededViol"])


#####   Read IAL file into a list     ######
#ialfile = readFile("/Users/andreasamartin/Documents/InstalExamples/rooms/rooms_testaug11.ial")
ialfile = readFile(ial_file_path)


#####   Extract the results     ######
#path = "out"
#path = "out1"
#path = "out2"
path = xhail_out_path
f = open(path, "r")
file_contents = f.read()

#print("\n\n\n",file_contents)

#y = eval(file_contents)

#print("\n\n\n",y)


#s = 'asdf=5;iwantthis123jasd'
start = 'hypothesis:'
end = 'uncovered:'



#Determine the existance of hypotheses in the file produced and removes duplicates
if start in file_contents: 
    start = file_contents.find("hypothesis:") + len("hypothesis:")
    end = file_contents.find("uncovered:")
    substring = file_contents[start:end]
    print("the learnt hypothesis: \n",substring.strip())

    #the case when no meaningful answers found we should print an empty file too
    #the word hypothesis would still be found in the file
    if(len(substring.strip())<2):
        printBlankFile()
        print("Revision task failed, no hypothesis found so we output an empty file \n")
        exit()

    start = 'hypothesis:'
    end = 'uncovered:' 

    #found this loop solution online 
    #https://stackoverflow.com/questions/18865058/extract-values-between-two-strings-in-a-text-file-using-python
    
    hypotheses = set()
    inFile = open(path)
    keepCurrentSet = False
    temp = ''
    for line in inFile:
        if end in line:
            keepCurrentSet = False
            hypotheses.add(temp)
            temp = ''

        if keepCurrentSet:
            temp = temp+line
            #x_contents.append(line)
        if start in line:
            keepCurrentSet = True
    inFile.close()
    print("saved contents \n",hypotheses, "\n")
    print(len(hypotheses), " hypotheses found")
else:
    printBlankFile()
    print("Revision task failed, no hypothesis found so we output an empty file \n")
    exit()

#result = re.search('%s(.*)%s' % (start, end), file_contents)
#result = re.search('hypothesis(.*)uncovered', file_contents)

#result = re.search('hyp(.*):', file_contents)
#print(result.group(1))

#s = 's1Texts2'
#result = re.search('s1(.*)s2', s)
#print(result.group(1))
revLog = ""


for num,subStr in enumerate(hypotheses):
    print("the value of num is", num)
    #print(id, val
#for x in range(len(hypotheses)):


#start = file_contents.find("hypothesis:") + len("hypothesis:")
#end = file_contents.find("uncovered:")
#substring = file_contents[start:end]

#if len(substring)<1:
#    print("No hypothesis found so we output an empty file")
#    saveEmptyFile()
#    exit(1)

#print("the learnt hypothesis: \n",substring.strip())

    #add a not in the instAL IAL output because learning not exception 
    toNegate = True

    subStr = substring

    #try different changes
    #subStr = "del(10,1)."
    #subStr ="exception(22,holdsat(potential_role_conflict(V1),rooms,V2)):-holdsat(meeting(V1),rooms,V2),instant(V2),location(V1)."
    #subStr ="exception(22,holdsat(potential_role_conflict,rooms,V2)):-holdsat(meeting,rooms,V2),instant(V2)."
    #subStr ="exception(22,holdsat(potential_role_conflict,rooms,V2)):-holdsat(meeting(V1,V3),rooms,V2),instant(V2),location(V1),person(V3)."
    #subStr ="exception(22,holdsat(potential_role_conflict,rooms,V2)):-holdsat(meeting(V1,V2,V3),rooms,V2),instant(V2),location(V1),person(V3)."
    #subStr ="exception(22,holdsat(potential_role_conflict,rooms,V2)):-not holdsat(meeting(V1,V3),rooms,V2),instant(V2),location(V1),person(V3)."


    revisions = subStr.count(".")
    #need to check for more than one rule learnt

    print("number of rules = ", revisions)
    if revisions>1: 
        #probably need to use findall, the regular expressions are not my friend
        subStr = subStr.split(".")
    c=0
    for x in range(revisions):
        if isinstance(subStr,list):
            sub=subStr[x].strip()
        else:
            sub=subStr.strip()
        print("List item ",sub)

        #need to check for del or exception 
        if sub.startswith("del"):
            
            start = sub.find("(")+len("(")
            end = sub.find(",")
            rule = sub[start:end]
            print("the rule number is ",rule)
            
            start = sub.find(",")+len(",")
            end = sub.find(")")
            pred_num = sub[start:end]
            print("the predicate number is ",pred_num)

            print("Item found: ",rulesList[int(rule)-1])

            #Keep track what revision is done
            ###
            tempClause = sub.replace("del","deleteClause")
            tempClause = tempClause.replace(".","")
            revLog = revLog + tempClause + "|"

            ###

            what2Revise = rulesList[int(rule)-1]
            what2ReviseWith = "del::"+str(pred_num)
            
            #print("Length what to revise: ",len(what2Revise))
            head = extendedterm2string(what2Revise[0])
            body=""
            #print("remaining list:  ",what2Revise[1:])
            #list_new = what2Revise[1]+what2Revise[2]

            #### Structure of json array is different on some of the rules
            #### Therefore we need to do this to ensure we capture the entire condition at all times
            list_new = []
            howMany=1
            if len(what2Revise)>2:
                for i in range(len(what2Revise)):
                    if i==0:
                        continue
                    else:
                        #print("i is ",i)
                        #print(what2Revise[i])
                        if len(what2Revise[i]): 
                            list_new.append(what2Revise[i])
                            howMany+=1

                for i in range(howMany-2):
                    list_new.insert(0,'and')
            #list_new = [what2Revise[1],what2Revise[2]]

            #l.extend([v] * n)
            #
            else:
                list_new=what2Revise[1]

            #list_new.insert(0,'and')
            #print("new list:  ",list_new)
            body = printCondition(list_new)
            #body += printCondition(what2Revise[2])
            body = body[:-1]
            #print("The head is: ",head)
            #print("The body is: ",body)
            #print("The body is: ",printCondition(what2Revise[1]), " left - ", what2Revise[1])
            
            #print("length  ",len(what2Revise[1]))

            

            term = ""
            #head_term = head.split("(")[0]
            pred_num = int(pred_num)
            #if head_term in 
            if what2Revise in nons:
                term = " when "
                #first item in condition will be 1
            
            elif what2Revise in gens:
                term = " generates "
                #first item in condition will be 3
                #so pred_num - 3 ???
                #1 would be occurred, 2 would be viol occurred
                #removing condition 1 or 2 would invalidate the rule so we delete it
                if pred_num < 3: 
                    deleteRule = True
                else:
                    #however we do not want the first item, that is the head
                    pred_num -= 2 
            elif what2Revise in inits:
                term = " initiates "
                #first item in condition will be 4
                #1 would be occurred, 2 would be viol occurred, 3 would be holdsat inst
                #removing condition 1,2 or 3 would invalidate the rule so we delete it
                if pred_num < 4: 
                    deleteRule = True
                else:
                    #however we do not want the first item, that is the head
                    pred_num -= 3 
            elif what2Revise in terms:
                term = " terminates "
                #first item in condition will be 4
                #1 would be occurred, 2 would be viol occurred, 3 would be holdsat inst
                #removing condition 1,2 or 3 would invalidate the rule so we delete it
                if pred_num < 4: 
                    deleteRule = True
                else:
                    #however we do not want the first item, that is the head
                    pred_num -= 3 

                ####### IMPORTANT NOTES ########
                ### maybe I can conclude that the triggering event would not be change
                #so maybe it is likely that a condition will be changed

                #### determining when to put the if in the output 
                ########################
            else:
                print("ERROR!!!!") 
            
            #find the item to delete from the condition  - need to figure out how to find the nth predicate from the body
            print("pred num: ",pred_num)
            #condition = getSpecificCondition((what2Revise[1]+what2Revise[2]),pred_num)
            condition = getSpecificCondition(list_new,pred_num)
            
            #oldRule = head+term+body+";"
            #revisedRule = oldRule #replacing the previous condition with empty space
            #print("Old rule: ",oldRule, "\nThe new rule is: ",revisedRule)
            print("Condition: ",condition, "\nBody: ",body)

            #determining where to put the if in the condition
            count = body.count(';')
            if count>0 and what2Revise not in nons: 
                #if more than one term in the condition, an IF needs to be placed after the first term
                arrBody = body.split(";")
                tempBody = ""
                for i in range(len(arrBody)):
                    if i==0:
                        tempBody += arrBody[i]+ " if"
                    elif i==(len(arrBody)-1):
                        tempBody += " "+arrBody[i]
                    else:
                        tempBody += " "+arrBody[i]+ ","
                body = tempBody
            else: 
                body = body.replace(";",", ")

            oldRule = head+term+body+";"
            if not condition.isspace():
                revisedRule = oldRule.replace(condition,"") #replacing the previous condition with empty space
            else: 
                revisedRule = oldRule

            #oldRule = head+term+body+" if permExit(P,L);"
            #revisedRule = oldRule.replace("permExit(P,L)","") #replacing the previous condition with empty space
            if "if" in revisedRule:
                result = re.search('if(.*);', revisedRule).group(1)
                #print("Result is", result)
                if result.isspace():
                    revisedRule = revisedRule.split('if')[0] + ";"
                if ' , ' in result:
                    #cheat for replacing this for now
                    revisedRule = revisedRule.replace(" , "," ")

            #Looking for extra commas to the end not needed
            #result = re.search(',(.*);', revisedRule).group(1)
            i = revisedRule.rfind(",")
            result = revisedRule[i+1:-1]
            #print("R ",result)    
            if result.isspace():
                #print("Space")
                i = revisedRule.rfind(",")
                revisedRule = revisedRule[:i] +revisedRule[-1]


            print("Old rule: ",oldRule, "\nThe new rule is: ",revisedRule)
            
            revisedRule = revisedRule+ "   %%% DELETION %%%"
            count=0
            #print("Condition 2 is ", getSpecificCondition(what2Revise[1],2))

        elif sub.startswith("exception"):
            #----EXCEPTION -----starts with exception rather than del
            print("this has an exception >>>>>>>")
            start = sub.find("(")+len("(")
            end = sub.find(",")
            rule = sub[start:end]
            print("the rule number is ",rule)

            start = sub.find(",")+len(",")
            end = sub.find(":")
            head = sub[start:end-1]
            print("the rule head is ",head)

            

            #Look for an EMPTY RIGHT HAND EXCEPTION #####
            if ":-" in sub: 

                #Keep track what revision is done
                ###
                tempClause = "addException("+rule+")"
                revLog = revLog + tempClause + "|"

                ###

                start = sub.find(":-")+len(":-")
                pred = sub[start:]
                print("The full right hand side is ",pred)


                #####     BEGIN   ######
                #Will need to more cleverly extract the right hand side 
                # Will need to work out how to separate the different predicates
                # How to correct the variable if any V1 -> P 
                # maybe there will be a person(V1) grounding available
                # This has been sorted as below

                #result = re.findall(r'([a-z][a-zA-Z0-9_]*\([A-Z_][a-zA-Z0-9_]*\)\s*[,.]\s*)+$', pred)
                result = re.findall(r'[,]\w+\(\w+\)', pred)
                #result = re.findall(r'[^\(]\w+\(\w+\)', pred)
                #"([a-z][a-zA-Z0-9_]*\([A-Z_][a-zA-Z0-9_]*\)\s*[,.]\s*)+$"
                l=len(result)
                types = []
                if l>0:
                    for i in range(l):
                        a_str = result[i].replace(',','')
                        a_str = a_str.replace("'","")
                        types.append(a_str)
                        #types.append(result[i].replace(',',''))
                        
                type1 = pred.find(result[0]) # do this because we know there will be at least one, the time instance
                toAdd = pred[:type1]

                #Begin cheat for missing types needed in script
                if 'V1' not in types:
                    types.append('person(V1)')
                else:
                    print("V1 is in the types")
                
                if 'V2' not in types:
                    print("V2 is not in the types")
                    types.append('location(V2)')
                else:
                    print("V2 is in the types")
                    
                #End cheat for missing types needed in script

                print("Types - ",types, " ", type1, " ", toAdd)
                #print("Regex result",type(result), " - ", result)

                
                ##### End of deriving the different predicates from the learnt rule
                ##### separating the types form the condition to be used
                #####      END       #######


                ####### May need this section to deal with multiple atoms in revision #######
                ##### BEGIN MULTIPLE ATOMS REVISION #####

                tempArr = toAdd.split("holdsat")
                print("---holdsat split ----",tempArr)

                count = toAdd.count("),")
                if count>1:
                    print("more than one atom to add ")

                    #--- need to work out a better way to grab all the atoms---
                    #z= [m.start() for m in re.finditer(r"\),",toAdd)][1]
                    #print(z)
                    #sub1 = toAdd[0:z+1] #+len("),")
                    #sub2 = toAdd[z+len("),"):len(toAdd)]
                    #print("two atoms are: \n1) - ",sub1, "\n2) -",sub2)

                    # ---- regular expression to get the atoms from the string ----  wasn't easy
                    result = re.findall('(?:not )?holdsat+\(+\w+(?:\(+\w+\))?,\w+,\w+\)|(?:not )?occurred+\(+\w+(?:\(\w+,?\w+\))?,\w+,\w+\)', toAdd)
                    print("number of atoms: ",len(result), "\n atoms -",result)
                    
                    ########
                    #Why am I not doing anything else if multiple atoms learnt??
                    #Shouldn't I be looping this process???
                    ########

                ##### END MULTIPLE ATOMS REVISION #####


                #if the learnt predicate has a not then it nullifies the not
                #therefore there is no need to put a not in the output anymore
                if "not " in toAdd: 
                    toAdd = toAdd.replace("not ", "") #remove the NOT so we can parse the predicate as normal
                    toNegate = False

                #start = sub.find(":-")+len(":-")
                #end = sub.rfind(",")
                #rfind and lfind can also be used instead of find
                #toAdd = sub[start:end]
                print("the predicate to add is ",toAdd)



                #grab the predicate based on if it has variables pred(a,b) or not
                start = toAdd.find("(")+len("(")
                if toAdd.count("(")>1:
                    #start = toAdd.find("(")+len("(")
                    end = toAdd.find(")")
                    add = toAdd[start:end+1]
                else:
                    #start = toAdd.find("(")+len("(")
                    end = toAdd.find(",")
                    add = toAdd[start:end]
                tempAdd = add


                #this means we have some variables in the predicate
                if "(" in add: 
                    start = add.find("(")+len("(")
                    end = add.find(")")
                    vars = add[start:end]
                    print("Vars 1 ",vars)
                    #if more than one variable exists
                    if "," in vars:
                        varsList = vars.split(",")
                        for varsI in varsList: #loop the varibales
                            print("V ",varsI)
                            for i in range(len(types)): #loop the types for each variable
                                if varsI in types[i]:
                                    print("found ",types[i])
                                    typeDef = types[i]
                                    break
                            add = add.replace(varsI,typeDef[0].upper())
                            start = typeDef.find("(")#+len("(")
                            addType = typeDef[:start]
                            #addType[0] = addType[0].upper()
                            addType = addType.replace(addType[0],addType[0].upper())
                            print("Type to add ",addType)
                            addType = tempAdd.replace(varsI,addType)
                            tempAdd = addType
                            print("Type to add ",addType)
                    else: # this means we only have 1 variable
                        
                        #replace with the appropriate variable letter

                        #print("V ",vars)
                        #get the one item
                        start = add.find("(")+len("(")
                        for i in range(len(types)):
                            #print("List ",i," - ",types[i])
                            if vars in types[i]:
                                print("found ",types[i])
                                typeDef = types[i]
                                break
                        add = add.replace(vars,typeDef[0].upper())
                        addType = typeDef[:start]
                        #addType[0] = addType[0].upper()
                        addType = addType.replace(addType[0],addType[0].upper())
                        print("Type to add ",addType)
                        addType = tempAdd.replace(vars,addType)
                        print("Type to add ",addType)

                ######
                print("Item found: ",rulesList[int(rule)-1])
                """""
                what2Revise = rulesList[int(rule)-1]
                #what2ReviseWith = "add::"+toAdd

                head = extendedterm2string(what2Revise[0])

                list_new = []
                howMany=1
                if len(what2Revise)>2:
                    for i in range(len(what2Revise)):
                        if i==0:
                            continue
                        else:
                            #print("i is ",i)
                            #print("List", i, " is ",what2Revise[i])
                            if len(what2Revise[i]):     
                                list_new.append(what2Revise[i])
                                howMany+=1

                    for i in range(howMany-2):
                        list_new.insert(0,'and')
                else:
                    list_new=what2Revise[1]
                
                #print("new list:  ",list_new)
                body = printCondition(list_new)

                #body = printCondition(what2Revise[1])
                body = body[:-1]
                
                #body = body.replace(";",",")
                
                print("The head is: ",head)
                print("The body is: ",body)
                #print("The body is: ",printCondition(what2Revise[1]), " left - ", what2Revise[1])
                print("length  ",len(what2Revise[1]))
                print("length of what to revise ",len(what2Revise))


                term = ""
                head_term = head.split("(")[0]
                #if head_term in 
                if what2Revise in nons:
                    term = " when "
                elif what2Revise in gens:
                    term = " generates "
                elif what2Revise in inits:
                    term = " initiates "
                elif what2Revise in terms:
                    term = " terminates "
                else:
                    print("ERROR!!!!") 

                #######
            #determining where to put the if in the condition
                count = body.count(';')
                if count>0 and what2Revise not in nons: 
                    #if more than one term in the condition, an IF needs to be placed after the first term
                    arrBody = body.split(";")
                    tempBody = ""
                    for i in range(len(arrBody)):
                        if i==0:
                            tempBody += arrBody[i]+ " if"
                        elif i==(len(arrBody)-1):
                            tempBody += " "+arrBody[i]
                        else:
                            tempBody += " "+arrBody[i]+ ", "
                    body = tempBody
                else: 
                    body = body.replace(";",", ")
                #######

                
                #oldRule = head+term+body+";"
                """
                oldRule,head,term,body=getRuleFromDict(rulesList[int(rule)-1])
                
                #revisedRule = head+ term+body+", not "+add+";"

                ### if it contains a not, then we do not need a NOT in the clause
                ### if it doesn't then we need a NOT because we need the negated exception based on how the rules are learnt. 
                if not toNegate:
                    revisedRule = head+ term+body+", "+add+";" + "    %%% ADDITION %%%"
                else:
                    revisedRule = head+ term+body+", not "+add+";" + "   %%% ADDITION %%%"
                
                #if (add.strip()).startswith("not"):
                #    revisedRule = head+ term+body+", "+add+";"
                #else:
                #    revisedRule = head+ term+body+", not "+add+";"



                print("Old rule: ",oldRule, "\nThe new rule is: ",revisedRule)
                
                #count=0
                #print("Condition 2 is ", getSpecificCondition(what2Revise[1],2))
                #print("hello ",toAdd[:start-1], " ", add)
                
                #if the learnt predicate is not a part of the current specification we need to add it
                start = toAdd.find("(")+len("(")
                if '(' in add:
                    tempstr = add.split('(')[0]
                    isTyped,what = checkIfTyped(toAdd[:start-1],tempstr)
                else:
                    isTyped,what = checkIfTyped(toAdd[:start-1],add)
                
                if isTyped:
                    print("Type is defined")
                else:
                    print("Type is not defined")
                    if what == 'F':
                        for i in range(len(ialfile)):
                            if "fluent" in ialfile[i]:
                                break
                        if "(" in add: 
                            ialfile.insert(i,"fluent "+addType+";")
                        else:
                            ialfile.insert(i,"fluent "+add+";")
                    elif what == 'E':
                        for i in range(len(ialfile)):
                            if "inst" in ialfile[i]:
                                break
                        if "(" in add:
                            ialfile.insert(i,"inst event "+addType+";")
                        else:
                            ialfile.insert(i,"inst event "+add+";")
            else:
                #No :- in the hypothesis

                ###### EMPTY RIGHT HAND SIDE March 21, 2022########
                # There is a case where the exception has no right hand side
                # so no predicates as an exception which means delete the whole rule. 
                # for now we are not deleting the whole rule
                # SO we will print an empty file for now
            
                print("The right hand side is empty, we delete the rule from the file")
                #saveEmptyFile()
                
                #Keep track what revision is done
                ###

                tempClause = "deleteRule("+rule+")"
                revLog = revLog + tempClause + "|"

                ###
                
                oldRule,head,term,body=getRuleFromDict(rulesList[int(rule)-1])
                print("Rule to delete is: ",rulesList[int(rule)-1], "\nEnglish version: ",oldRule)
                #printBlankFile()
                revisedRule = "%%% DELETED RULE %%%"
                empty=True
                #exit(1)
        else:
            #this is when there is a new rule found to be added so no del or exception
            #Aug 8 or 9, 2022
            if ":-" in sub: 
                start = sub.find(":-")
                head = sub[:start]
                print("The full left hand side is ",head)

                start = sub.find(":-")+len(":-")
                pred = sub[start:]
                print("The full right hand side is ",pred)

                result = re.findall(r'[,]\w+\(\w+\)', pred)
                l=len(result)
                types = []
                if l>0:
                    for i in range(l):
                        a_str = result[i].replace(',','')
                        a_str = a_str.replace("'","")
                        types.append(a_str)
                        
            

                type1 = pred.find(result[0]) # do this because we know there will be at least one, the time instance
                toAdd = pred[:type1]



                print("Types - ",types, " ", type1, " ", toAdd)
                
                #Aug10,2022 - need this cheat to update the types for a new rules
                #will only work on this use case because we know there is this problem and we know the solution
        
                #Begin cheat for missing types needed in script
                if 'V1' not in types:
                    types.append('person(V1)')
                
                if 'V2' not in types:
                    types.append('location(V2)')
                #End cheat for missing types needed in script

                newH = getInstalPredicate(toAdd)
                print("The new head - ",newH)
                newB = getInstalPredicate(head)
                print("The new predicate - ",newB)
                
                #arrive(P,L) initiates in_room(P,L) if revise;

                newRule = newH + " initiates " + newB + " if revise; %%%%NEW RULE ADDED%%%%"
                ####### 
                # Working on this for a simple rule learning where it expects only one predicate learnt
                # will improve it going forward
                # 
                ########
                ialfile.append("\n")
                ialfile.append(newRule)
                tempClause = "newRuleAdded"
                revLog = revLog + tempClause + "|"

                print("need to add ",newRule, "to the ial file")
                #printBlankFile()
                oldRule = ""
                revisedRule = ""
                #printToFile()
                #exit()
            else:
                #No :- in the the new rule so we 

                ###### EMPTY RIGHT HAND SIDE Aug 10, 2022########
                # There is a case where the new rule has no right hand side
                # we do not accept this  
                # SO we will print an empty file for now
            
                print("This new rule has no body so we print an empty file")

                printBlankFile()
                
                empty=True
                exit(1)

            
        c+=1
        #if not empty: 
        for i in range(len(ialfile)):
            #print("line ",i, " - ", ialfile[i])
            if ialfile[i] == oldRule:
                print("rule found")
                ialfile[i] = revisedRule


            

    print("# of rules is ",c)
    #print(ialfile)

    #print("new IAL file\n", printToFile())
    #print("new IAL file\n")
    #printToConsole()
    
    #if not empty: 
    
    #Print to a file rather than output
    
    printToFile()

    #print revision log to a file for the oracle to easily know the revisions made to the file
    tempPath = output_file_path.split(".")
    tempPath2 = tempPath[0]+"revLog"
    print(revLog[:-1], file=open(tempPath2, "w"))
    revLog = ""       

    #if multiple hypotheses, getting a new file name
    index = output_file_path.find(".")
    name1 = output_file_path[0:index]
    ext1= output_file_path[index:len(output_file_path)]
    i=num+1
    output_file_path = name1+"_"+str(i)+ext1
    print("new file name is ", name1+"_"+str(i)+ext1)





