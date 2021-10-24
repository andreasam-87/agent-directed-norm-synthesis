#from instal.instalexceptions import InstalCompileError


class InstalCompiler(object):
    """
        InstalCompiler
        Compiles InstAL IR to ASP.
        Call compile_ial() - requires the IR.
        A significant chunk of this code is legacy and thus fragile.
    """

    def __init__(self):
        self.ial_ir = {}
        self.names = {}
        self.types = {}
        self.exevents = {}
        self.inevents = {}
        self.vievents = {}

        self.out = ""
        self.temp = ""
        self.rId = 1
        self.condLits = {}
        self.ruleDict = {}

    def print_message(self, msg):
        print(msg)
        
    def instal_print(self, to_append: str) -> None:
        #Legacy. InstAL print used to print to file: it now just adds to an out variable which is returned.
        self.out += to_append + "\n"

    def compile_ial(self, ial_ir: dict) -> str:
        """Called to compile ial_ir to ASP."""
        self.ial_ir = ial_ir
        self.names = self.ial_ir["names"]
        self.types = self.ial_ir["types"]
        self.exevents = self.ial_ir["exevents"]
        self.inevents = self.ial_ir["inevents"]
        self.vievents = self.ial_ir["vievents"]

        self.instal_print_all(self.ial_ir)

        #print(self.out,file=open('asp.txt','w')) 
        return self.out

    def instal_print_all(self, ial_ir: dict) -> None:
        self.instal_print("%\n% "
                          "-------------------------------"
                          "PART 1"
                          "-------------------------------"
                          "\n%")
        self.instal_print_standard_prelude()
        self.instal_print_constraints()
        self.instal_print_exevents(ial_ir["exevents"])
        self.instal_print_nullevent()
        self.instal_print_inevents(ial_ir["inevents"])
        self.instal_print_vievents(ial_ir["vievents"])
        self.instal_print_crevents({})
        self.instal_print_dievents({})
        self.instal_print_dissolve({})
        self.instal_print_inertial_fluents(ial_ir["fluents"])
        self.instal_print_noninertial_fluents(ial_ir["noninertial_fluents"])
        self.instal_print_violation_fluents([])
        self.instal_print_obligation_fluents(ial_ir["obligation_fluents"])
        self.instal_print("%\n% "
                          "-------------------------------"
                          "PART 2"
                          "-------------------------------"
                          "\n%")
        
        #Previous prints needed for basic instal
        self.instal_print_generates(ial_ir["generates"])
        self.instal_print_initiates(ial_ir["initiates"])
        self.instal_print_terminates(ial_ir["terminates"])
        self.instal_print_noninertials(ial_ir["whens"])
        
        #New prints with try del rules
        self.print_prep_generates(ial_ir["generates"])
        self.print_prep_initiates(ial_ir["initiates"])
        self.print_prep_terminates(ial_ir["terminates"])
        self.print_prep_noninertial(ial_ir["whens"])

        self.instal_print("%\n% "
                          "-------------------------------"
                          "PART 3"
                          "-------------------------------"
                          "\n%")
        self.instal_print_initially(ial_ir["initials"])

        #These grounding things caused errors so removed for now
        #self.instal_print_types()
        self.instal_print("%\n% End of file\n%")

        #Printing at the end to debug if needed
        #self.print_prep_noninertial(ial_ir["whens"])
        #self.print_prep_generates(ial_ir["generates"])
        #self.print_prep_initiates(ial_ir["initiates"])
        #self.print_prep_terminates(ial_ir["terminates"])

    def instal_print_standard_prelude(self):
        # Legacy. This used to dump the standard prelude at the top of every file.
        # It is now done in instal.models.InstalModel.
        self.instal_print("%\n% Standard prelude for {institution}\n%"
                          .format(**self.names))
        self.instal_print("% Standard prelude now dealt with in InstalModel.")

        # institution live fluents and the _preludeLoaded fluent
        self.instal_print("%\n% Rules for Institution {institution}\n%\n"
                          "  ifluent(live({institution}), {institution}).\n"
                          "  fluent(live({institution}), {institution}).\n"
                          "  inst({institution}).\n"
                          "  :- not _preludeLoaded. \n"
                          .format(**self.names))

    def instal_print_constraints(self):
        pass

    def isVar(self, t: str) -> bool:
        """
            Checks whether t is a type name by seeing if it has an uppercase first character.
        """
        return t[0] != t[0].lower()

    EXPR_SYMBOLS = ['==', '!=', '<', '>', '<=', '>=']

    def collectVars(self, t, d, compiler: 'InstalCompiler' = None):
        # A legacy function. Not removed because a lot of the type checking is tied to the functionality.
        if not t:
            return
        if t[0] == 'and':
            self.collectVars(t[1], d)
            self.collectVars(t[2], d)
        elif t[0] == 'not':
            self.collectVars(t[1], d)
        elif t[0] == 'obl':
            for x in t[1]:
                self.collectVars(x, d)
        elif t[0] in self.EXPR_SYMBOLS:
            pass
        else:
            if t[0] in ['perm', 'pow', 'viol']:
                t = t[1]
            op = t[0]
            args = t[1]
            for evd in [self.ial_ir["exevents"], self.ial_ir["inevents"], self.ial_ir["vievents"],
                        self.ial_ir["fluents"], self.ial_ir[
                        "noninertial_fluents"],
                        self.ial_ir["obligation_fluents"]]:
                if op in evd:
                    for (t1, t2) in zip(evd[op], args):
                        if t2 in d:
                            if t1 != d[t2]:
                              #  raise InstalCompileError(
                                 raise Exception(
                                    "% ERROR: {v} has type {t1} and type {t2} in {t}".format(v=t2, t1=t1, t2=d[t2],
                                                                                             t=t))
                        if self.isVar(t2):
                            d[t2] = t1
                    return
            #raise InstalCompileError(
            raise Exception("% WARNING: {t} not found in collectVars"
                                     .format(t=t))

    def instal_print_types(self) -> None:
        # Print types. Also adds a constraint that every type must be grounded.
        self.instal_print("%\n% "
                          "-------------------------------"
                          "GROUNDING"
                          "-------------------------------"
                          "\n%")
        for t in self.types:
            self.instal_print("% {x}".format(x=t))
            self.instal_print(
                "_typeNotDeclared :- not {x}(_).".format(x=t.lower()))
            self.instal_print("#program {x}(l).".format(x=t.lower()))
            self.instal_print("{x}(l).\n".format(x=t.lower()))

    def instal_print_exevents(self, exevents: dict) -> None:
        # print exevents
        self.instal_print("%\n% Exogenous events")
        for ev, args in exevents.items():
            self.instal_print(
                "% Event: {ev} (type: ex)\n"
                "  event({ev}{args}) :- {rhs}.\n"
                "  evtype({ev}{args},{inst},ex) :- {rhs}.\n"
                "  evinst({ev}{args},{inst}) :- {rhs}.\n"
                "  ifluent(pow({ev}{args}),{inst}) :- {rhs}.\n"
                "  ifluent(perm({ev}{args}),{inst}) :- {rhs}.\n"
                "  fluent(pow({ev}{args}),{inst}) :- {rhs}.\n"
                "  fluent(perm({ev}{args}),{inst}) :- {rhs}.\n"
                "  event(viol({ev}{args})) :- {rhs}.\n"
                "  evtype(viol({ev}{args}), {inst}, viol) :- {rhs}.\n"
                "  evinst(viol({ev}{args}),{inst}) :- {rhs}."
                .format(ev=ev,
                        args=self.args2string(args),
                        rhs=self.typecheck(args),
                        inst=self.names["institution"]))

    def instal_print_nullevent(self) -> None:
        # print nullevents
        self.instal_print("%\n% null event for unknown events")
        self.instal_print("% Event: null (type: ex)\n"
                          "  event(null).\n"
                          "  evtype(null,{inst},ex).\n"
                          "  evinst(null,{inst}).\n"
                          "  ifluent(pow(null),{inst}).\n"
                          "  ifluent(perm(null),{inst}).\n"
                          "  fluent(pow(null),{inst}).\n"
                          "  fluent(perm(null),{inst}).\n"
                          "  event(viol(null)).\n"
                          "  evtype(viol(null),{inst},viol).\n"
                          "  evinst(viol(null),{inst})."
                          .format(inst=self.names["institution"]))

    def instal_print_inevents(self, inevents: dict) -> None:
        # print inevents
        self.instal_print("% Institutional events")
        for ev, args in inevents.items():
            self.instal_print(
                "% Event: {ev} (type: in)\n"
                "  event({ev}{args}) :- {rhs}.\n"
                "  evtype({ev}{args},{inst},inst) :- {rhs}.\n"
                "  evinst({ev}{args},{inst}) :- {rhs}.\n"
                "  ifluent(perm({ev}{args}),{inst}) :- {rhs}.\n"
                "  fluent(perm({ev}{args}),{inst}) :- {rhs}.\n"
                "  event(viol({ev}{args})) :- {rhs}.\n"
                "  evtype(viol({ev}{args}),{inst},viol) :- {rhs}.\n"
                "  evinst(viol({ev}{args}),{inst}) :- {rhs}."
                .format(ev=ev,
                        args=self.args2string(args),
                        rhs=self.typecheck(args),
                        inst=self.names["institution"]))

    def instal_print_vievents(self, vievents: dict) -> None:
        # print vievents
        self.instal_print("%\n% Violation events\n%")
        for ev, args in vievents.items():
            self.instal_print(
                "% Event: {ev} (type: in)\n"
                "  event({ev}{args}) :- {rhs}.\n"
                "  evtype({ev}{args},{inst},viol) :- {rhs}.\n"
                "  evinst({ev}{args},{inst}) :- {rhs}."
                .format(ev=ev,
                        args=self.args2string(args),
                        rhs=self.typecheck(args),
                        inst=self.names["institution"]))

    def instal_print_crevents(self, crevents: dict) -> None:
        # print crevents
        self.instal_print("%\n% Creation events\n%")
        for ev in crevents:
            #raise InstalCompileError(
            raise Exception("NOT IMPLEMENTED: Creation events.")

    def instal_print_dievents(self, dievents: dict) -> None:
        # print dievents
        self.instal_print("%\n% Dissolution events\n%")
        for ev in dievents:
           # raise InstalCompileError(
            raise Exception("NOT IMPLEMENTED: Dissolution events.")

    def instal_print_inertial_fluents(self, fluents: dict) -> None:
        # inertial fluents
        self.instal_print("%\n% inertial fluents\n%")
        for inf, args in fluents.items():
            self.instal_print(
                "ifluent({name}{args},{inst}) :-\n"
                "  {preds}.\n"
                "fluent({name}{args},{inst}) :-\n"
                "  {preds}.\n"
                .format(name=inf,
                        args=self.args2string(args),
                        preds=self.typecheck(args),
                        inst=self.names["institution"]))

    def instal_print_noninertial_fluents(self, noninertial_fluents: dict) -> None:
        # noninertial fluents
        self.instal_print("%\n% noninertial fluents\n%")
        for nif, args in noninertial_fluents.items():
            self.instal_print(
                "nifluent({name}{args}, {inst}) :-\n"
                "  {preds}.\n"
                "fluent({name}{args}, {inst}) :-\n"
                "  {preds}.\n"
                .format(name=nif,
                        args=self.args2string(args),
                        preds=self.typecheck(args), inst=self.names["institution"]))

    def instal_print_violation_fluents(self, violation_fluents):
        # violation fluents
        self.instal_print("%\n% violation fluents (to be implemented)\n")
        for vif in violation_fluents:
            #raise InstalCompileError(
            raise Exception("NOT IMPLEMENTED: Violation fluents.")

    saved_enumerator = 0

    def typecheck(self, p, cont: bool=False):
        # Legacy. Type checking is dealt with elsewhere, but this is necessary functionality.
        if not p:
            return 'true'
        if not cont:
            self.saved_enumerator = 0
        i = self.saved_enumerator
        r = self.types[p[0]] + '(' + p[0] + str(i) + ')'
        for j, t in enumerate(p[1:]):
            r = r + ',' + self.types[t] + '(' + t + str(i + j + 1) + ')'
        self.saved_enumerator = i + len(p)
        return r

    def instal_print_obligation_fluents(self, obligation_fluents: list) -> None:
        # obligation fluents
        self.instal_print("%\n% obligation fluents\n%")
        for of in obligation_fluents:
            e = of[0][0] + self.args2string(of[0][1])
            d = of[1][0] + self.args2string(of[1][1], cont=True)
            v = of[2][0] + self.args2string(of[2][1], cont=True)
            te = self.typecheck(of[0][1])
            td = self.typecheck(of[1][1], cont=True)
            tv = self.typecheck(of[2][1], cont=True)

            # Future versions will allow for conditions/deadlines to be states as well as events.
            # Not implemented as present though.
            e_event = True
            e_fluent = False
            d_event = True
            d_fluent = False

            self.instal_print(
                "oblfluent(obl({e},{d},{v}), {inst}) :-".format(e=e, d=d, v=v, inst=self.names["institution"]))
            if e_event:
                self.instal_print("   event({e}),".format(e=e))
            if e_fluent:
                self.instal_print("   fluent({e},{inst}),".format(
                    e=e, inst=self.names["institution"]))
            if d_event:
                self.instal_print("   event({d}),".format(d=d))
            if d_fluent:
                self.instal_print("   fluent({d},{inst}),".format(
                    d=d, inst=self.names["institution"]))
            self.instal_print("   event({v}), {te},{td},{tv},inst({inst})."
                              .format(e=e, d=d, v=v, te=te, td=td, tv=tv, inst=self.names["institution"]))

            # The 2nd obligation rule
            self.instal_print("ifluent(obl({e},{d},{v}), {inst}) :-".format(e=e, d=d, v=v, inst=self.names[
                "institution"]))
            if e_event:
                self.instal_print("   event({e}),".format(e=e))
            if e_fluent:
                self.instal_print("   fluent({e},{inst}),".format(
                    e=e, inst=self.names["institution"]))
            if d_event:
                self.instal_print("   event({d}),".format(d=d))
            if d_fluent:
                self.instal_print("   fluent({d},{inst}),".format(
                    d=d, inst=self.names["institution"]))
            self.instal_print("   event({v}), {te},{td},{tv},inst({inst})."
                              .format(e=e, d=d, v=v, te=te, td=td, tv=tv, inst=self.names["institution"]))
            self.instal_print(
                "fluent(obl({e},{d},{v}), {inst}) :-".format(e=e, d=d, v=v, inst=self.names["institution"]))
            if e_event:
                self.instal_print("   event({e}),".format(e=e))
            if e_fluent:
                self.instal_print("   fluent({e},{inst}),".format(
                    e=e, inst=self.names["institution"]))
            if d_event:
                self.instal_print("   event({d}),".format(d=d))
            if d_fluent:
                self.instal_print("   fluent({d},{inst}),".format(
                    d=d, inst=self.names["institution"]))
            self.instal_print("   event({v}), {te},{td},{tv},inst({inst})."
                              .format(e=e, d=d, v=v, te=te, td=td, tv=tv, inst=self.names["institution"]))

            # The 3rd obligation rule
            self.instal_print(
                "terminated(obl({e},{d},{v}),{inst},{e},I) :-".format(e=e, d=d, v=v, inst=self.names["institution"]))
            if e_event:
                self.instal_print("   event({e}), occurred({e},{inst},I),".format(
                    e=e, inst=self.names["institution"]))
            if e_fluent:
                self.instal_print(
                    "   fluent({e},{inst}), holdsat({e},{inst},I),".format(e=e, inst=self.names["institution"])) # 20190606 JAP: add arg to holdsat
            if d_event:
                self.instal_print("   event({d}),".format(d=d))
            if d_fluent:
                self.instal_print("   fluent({d},{inst}),".format(
                    d=d, inst=self.names["institution"]))
            self.instal_print("   holdsat(obl({e},{d},{v}),{inst},I),\n" # 20190606 JAP: add arg to holdsat
                              "   event({v}), {te},{td},{tv},inst({inst})."
                              .format(e=e, d=d, v=v, te=te, td=td, tv=tv, inst=self.names["institution"]))

            # The fourth obligation rule
            self.instal_print(
                "terminated(obl({e},{d},{v}),{inst},{d},I) :-".format(e=e, d=d, v=v, inst=self.names["institution"]))
            if e_event:
                self.instal_print("   event({e}), ".format(e=e))
            if e_fluent:
                self.instal_print("   fluent({e},{inst}),".format(
                    e=e, inst=self.names["institution"]))
            if d_event:
                self.instal_print("   event({d}), occurred({d},{inst},I),".format(
                    d=d, inst=self.names["institution"]))
            if d_fluent:
                self.instal_print(
                    "   fluent({d},{inst}),  holdsat({d},{inst},I),".format(d=d, inst=self.names["institution"]))
            self.instal_print("   holdsat(obl({e},{d},{v}),{inst},I),\n" # 20190606 JAP: add arg to holdsat
                              "   event({v}), {te},{td},{tv},inst({inst})."
                              .format(e=e, d=d, v=v, te=te, td=td, tv=tv, inst=self.names["institution"]))

            # The fifth obligation rule
            self.instal_print(
                "occurred({v},{inst},I) :-".format(v=v, inst=self.names["institution"]))
            if e_event:
                self.instal_print("   event({e}), ".format(e=e))
            if e_fluent:
                self.instal_print(
                    "   fluent({e},{inst}), not holdsat({e},{inst},I),".format(e=e, inst=self.names["institution"])) # 20190606 JAP: add arg to holdsat
            if d_event:
                self.instal_print("   event({d}), occurred({d},{inst},I),".format(
                    d=d, inst=self.names["institution"]))
            if d_fluent:
                self.instal_print(
                    "   fluent({d},{inst}),  holdsat({d},{inst},I),".format(d=d, inst=self.names["institution"])) # 20190606 JAP: add arg to holdsat
            self.instal_print("   holdsat(obl({e},{d},{v}),{inst},I),\n" # 20190606 JAP: add arg to holdsat
                              "   event({v}), {te},{td},{tv},inst({inst})."
                              .format(e=e, d=d, v=v, te=te, td=td, tv=tv, inst=self.names["institution"]))


    def instal_print_generates(self, generates: list) -> None:
        # generates
        self.instal_print("%\n% generate rules\n%")
        for rl in generates:
            [inorexev, inev, cond, ti] = rl
            vars1 = {}
            self.collectVars(inorexev, vars1)
            self.collectVars(cond, vars1)
            if not ti:
                time = ""
            else:
                time = "+" + str(ti)
                #raise InstalCompileError(
                raise Exception(
                    "NOT IMPLEMENTED: Being able to generate events in n timesteps")
            for x in inev:
                vars2 = {}
                self.collectVars(x,vars2) # 20170316 JAP: restored
                self.instal_print(
                    "%\n"
                    "% Translation of {exev} generates {inev} if {condition} in {time}\n"
                    "occurred({inev},{inst},I{time}) :- occurred({exev},{inst},I),"
                    "not occurred(viol({exev}),{inst},I),\n"
                    .format(exev=self.extendedterm2string(inorexev),
                            inev=self.extendedterm2string(x),
                            inst=self.names["institution"],
                            condition=cond, time=time))
                self.printCondition(cond)
                for k in vars1:
                    self.instal_print(
                        "   {pred}({tvar}),"
                        .format(pred=self.types[vars1[k]], tvar=k))
                for k in vars2:
                    if k not in vars1:
                        self.instal_print(
                            "   {pred}({tvar}),"
                            .format(pred=self.types[vars2[k]], tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                    inst=self.names["institution"]))

    def instal_print_initiates(self, initiates: list) -> None:
        # initiates
        self.instal_print("%\n% initiate rules\n%")
        for rl in initiates:
            [inev, inits, cond] = rl
            vars1 = {}
            self.collectVars(inev, vars1)
            self.collectVars(cond, vars1)
            for x in inits:
                vars2 = {}
                self.collectVars(x,vars2) # 20170316 JAP: restored
                self.instal_print(
                    "%\n% Translation of {inev} initiates {inits} if {condition}"
                    .format(inev=self.extendedterm2string(inev), inits=x, condition=cond))
                self.instal_print("%\ninitiated({inf},{inst},{ev},I) :-\n" # 2019604 JAP: added ev
                                  "   occurred({ev},{inst},I),\n"
                                  "   not occurred(viol({ev}),{inst},I),\n"
                                  "   holdsat(live({inst}),{inst},I)," # 20190606 JAP: add arg to holdsat
                                  .format(inf=self.extendedterm2string(x),
                                          ev=self.extendedterm2string(inev),
                                          inst=self.names["institution"]))
                self.printCondition(cond)
                for k in vars1:
                    self.instal_print(
                        "   {pred}({tvar}),"
                        .format(pred=self.types[vars1[k]], tvar=k))
                for k in vars2:
                    if k not in vars1:
                        self.instal_print(
                            "   {pred}({tvar}),"
                            .format(pred=self.types[vars2[k]], tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                    inst=self.names["institution"]))

    def instal_print_terminates(self, terminates: list) -> None:
        # terminates
        self.instal_print("%\n% terminate rules\n%")
        for rl in terminates:
            [inev, terms, cond] = rl
            vars1 = {}
            self.collectVars(inev, vars1)
            self.collectVars(cond, vars1)
            for x in terms:
                vars2 = {}
                self.collectVars(x,vars2) # 20170316 JAP: restored
                self.instal_print(
                    "%\n% Translation of {inev} terminates {terms} if {condition}"
                    .format(inev=self.extendedterm2string(inev), terms=x, condition=cond))
                self.instal_print("%\nterminated({inf},{inst},{ev},I) :-\n" # 2019604 JAP: added ev
                                  "   occurred({ev},{inst},I),\n"
                                  "   not occurred(viol({ev}),{inst},I),\n"
                                  "   holdsat(live({inst}),{inst},I)," # 20190606 JAP: add arg to holdsat
                                  .format(inf=self.extendedterm2string(x),
                                          ev=self.extendedterm2string(inev),
                                          inst=self.names["institution"]))
                self.printCondition(cond)
                for k in vars1:
                    self.instal_print(
                        "   {pred}({tvar}),"
                        .format(pred=self.types[vars1[k]], tvar=k))
                for k in vars2:
                    if k not in vars1:
                        self.instal_print(
                            "   {pred}({tvar}),"
                            .format(pred=self.types[vars2[k]], tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                    inst=self.names["institution"]))

    def instal_print_noninertials(self, whens: list) -> None:
        # noninertials
        self.instal_print("%\n% noninertial rules\n%")
        for rl in whens:
            [nif, ante] = rl
            vars1 = {}
            
            #Modified by Andreasa 07/07/2021 - BEGINS
            #print("first condition"+ante[0])
            check,ar,trm = self.checkAggregates(ante)
            if check:
                #print("aggregate "+str(ar)+ " "+str(trm))
                self.temp = nif

                self.instal_print("%\n% Translation of {nif} when {ante} inclusive of new predicate definitions if necessary \n".format(nif=self.extendedterm2string(nif), ante=ante))

                self.printAggregates(ar,trm,institution=self.names["institution"])
                  
                self.collectVars(nif, vars1)

                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".
                                format(pred=self.types[vars1[k]], tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                inst=self.names["institution"]))
                
            #MODIFICATION ENDS
            #everything in the else was before as part of the algorithm before the condition and check
            else:
                self.collectVars(nif, vars1)
                self.instal_print("%\n% Translation of {nif} when {ante}\n"
                              "holdsat({nif},{inst},I) :-" # 20190606 JAP: add arg to holdsat
                              .format(nif=self.extendedterm2string(nif), ante=ante, inst=self.names["institution"]))
                self.printCondition(ante)
                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".
                                format(pred=self.types[vars1[k]], tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                inst=self.names["institution"]))


    def instal_print_initially(self, initials: list) -> None:
        # initially
        self.instal_print("%\n% initially\n%")
        if True:
            self.instal_print("% no creation event")
            self.instal_print("holdsat(live({inst}),{inst},I) :- start(I), inst({inst})." # 20190606 JAP: add arg to holdsat
                              .format(inst=self.names["institution"]))
            self.instal_print("holdsat(perm(null),{inst},I) :- start(I), inst({inst})." # 20190606 JAP: add arg to holdsat
                              .format(inst=self.names["institution"]))
            self.instal_print("holdsat(pow(null),{inst},I) :- start(I), inst({inst})." # 20190606 JAP: add arg to holdsat
                              .format(inst=self.names["institution"]))
            for inits in initials:
                [i, cond] = inits
                fvars = {}
                self.instal_print("% initially: {x}"
                                  .format(x=self.extendedterm2string(i)))
                if not (cond == []):
                    pass
                    #self.instal_print(
                    #    "% condition: {x}"
                    #    .format(x=self.extendedterm2string(cond)))
                self.instal_print("holdsat({inf},{inst},I) :- not holdsat(live({inst}),{inst})," # 20190606 JAP: add arg to holdsat
                                  .format(inst=self.names["institution"], inf=self.extendedterm2string(i)))
                self.collectVars(i, fvars)
                for k in fvars:
                    self.instal_print(
                        "   {pred}({tvar}),"
                        .format(pred=self.types[fvars[k]], tvar=k))
                if not (cond == []):
                    self.printCondition(cond)
                self.instal_print("   inst({inst}), start(I).".format(
                    inst=self.names["institution"]))
        else:
            pass

    def instal_print_dissolve(self, dievents: dict) -> None:
        # dissolve
        self.instal_print("%\n% dissolve events\n%")
        for d in dievents:
            #raise InstalCompileError(
            raise Exception("NOT IMPLEMENTED: Dissolution events.")

    def args2string(self, p, cont: bool=False):
        # Legacy.
        if not p:
            return ''
        if not cont:
            self.saved_enumerator = 0
        i = self.saved_enumerator
        r = '(' + p[0] + str(i)
        for j, x in enumerate(p[1:]):
            r = r + ',' + x + str(i + j + 1)
        r += ')'
        self.saved_enumerator = i + len(p)
        return r

    #New function added by Andreasa 23/06/2021 
    #modified extensively 07/06/2021 - Andreasa
    def printAggregates(self,c,term,institution: str=None):

        #self.instal_print(" {} = #count {{P: ".format(term) )
        #self.instal_print(" N = #count {P: ")
        #print('Term: ',term, ' ',len(term))
        if len(term)==1:
            t= term[0]
        else:
            for i in term:
                t+=i+','
            t=t[:end]
       
        ct=1
        l=len(c)
        str_event = ""

        str_aggr_print = "holdsat("+self.extendedterm2string(self.temp)+","+institution+",I) :- "+ t +" =  #count {holdsat("+self.temp[0]+str(ct)+"(vars),"+institution+",I) : types} "
       
        #self.instal_print(" {} = #count {{".format(t) )


        #str_new_fluent ="%%%"+str(c[0][0])+str(ct)+"(vars) :- "
        str_new_fluent ="holdsat("+self.temp[0]+str(ct)+"(vars),"+institution+",I) :- "
        
        str_fluent_def1 = "nifluent("+self.temp[0]+str(ct)+"(vars),"+institution+ ") :- "
        str_fluent_def2 = "fluent("+self.temp[0]+str(ct)+"(vars),"+institution +") :- "
        vars_head = {}
        self.collectVars(self.temp, vars_head)
        if l==1:
            self.instal_print("holdsat({nif},{inst},I) :- {var} = #count {{" 
                              .format(nif=self.extendedterm2string(self.temp),inst=institution,var=t))
             #self.instal_print(" {} = #count {{".format(t))
            
            self.instal_print(" holdsat({fluent},{inst},I) :" # 20190606 JAP: add arg to holdsat
                            .format(fluent=self.extendedterm2string(c[0]), inst=institution,count=ct))
            vars1 = {}
            vars1_temp = {}
            self.collectVars(c[0], vars1_temp)
 
            for key, value in vars1_temp.items():
                if key not in vars_head:
                    vars1[key]=value
            
            ct2=1
            l2=len(vars1)

            #if k not in vars_head:

            for k in vars1:
                if ct2==l2:
                    self.instal_print("   {pred}({tvar}) ".
                            format(pred=self.types[vars1[k]], tvar=k))
                else:
                    self.instal_print("   {pred}({tvar}) :".
                                format(pred=self.types[vars1[k]], tvar=k))
                ct2+=1
                    
            self.instal_print(" }, " ) #needed if calling collect vars after
            #self.instal_print(" }. " )
          
        else:
            #Need to produce a new rule to work with earlier clingo version, 
            #as only 1 predicate works well in aggregate

            # list(set(x).symmetric_difference(set(f))) for unique items in lists
            str_new_fluent_vars = set()
            str_new_types =""
            set_new_types = set() #ignores duplicates and if in head rule
            set_new_types1 = set() #ignores duplicates only
            for item in c:
                if(ct==l):
                    #str_event+=("event(E"+str(ct)+")")
                    #str_new_types+=("event(E"+str(ct)+"),")
                    #set_new_types1.add("event(E"+str(ct)+")")
                    #set_new_types.add("event(E"+str(ct)+")")
                    str_new_fluent+="holdsat("+self.extendedterm2string(item)+","+institution+",I),"
                    #str_new_fluent_vars.add("E"+str(ct))
                    vars1 = {}
                    self.collectVars(item, vars1)
                    #list(set(vars1).symmetric_difference(set(vars_head)))
                    ct2=1
                    l2=len(vars1)
                    for k in vars1:
                        if ct2==l2:
                            #self.instal_print("   {pred}({tvar}) ".
                            #        format(pred=self.types[vars1[k]], tvar=k))
                            #str_new_fluent_vars+=k
                            str_new_fluent_vars.add(k)
                            str_new_types+=(self.types[vars1[k]]+"("+k+")")
                            if k not in vars_head:
                                set_new_types.add(self.types[vars1[k]]+"("+k+")")
                            set_new_types1.add(self.types[vars1[k]]+"("+k+")")
                        else:
                           # self.instal_print("   {pred}({tvar}) :".
                            #        format(pred=self.types[vars1[k]], tvar=k))
                            #str_new_fluent_vars+=k+","
                            str_new_fluent_vars.add(k)
                            str_new_types+=(self.types[vars1[k]]+"("+k+"),")
                            if k not in vars_head:
                                set_new_types.add(self.types[vars1[k]]+"("+k+")")
                            set_new_types1.add(self.types[vars1[k]]+"("+k+")")
                        ct2+=1
                        
                   # self.instal_print(" }, " )
            
                else:
                    #self.instal_print(" holdsat({fluent},{inst},E{count},I) : " # 20190606 JAP: add arg to holdsat
                     #           .format(fluent=self.extendedterm2string(item), inst=institution,count=ct))
                    #str_event+=("event(E"+str(ct)+") : ")
                    #str_new_types+=("event(E"+str(ct)+"),")
                    #set_new_types.add("event(E"+str(ct)+")")
                    #set_new_types1.add("event(E"+str(ct)+")")
                    str_new_fluent+="holdsat("+self.extendedterm2string(item)+","+institution+",I), "
                    #str_new_fluent_vars.add("E"+str(ct))
                    vars1 = {}
                    self.collectVars(item, vars1)
                    #list(set(vars1).symmetric_difference(set(vars_head)))
                    for k in vars1:
                        #self.instal_print("   {pred}({tvar}) :".
                         #   format(pred=self.types[vars1[k]], tvar=k))
                            #str_new_fluent_vars+=k+","
                        str_new_fluent_vars.add(k)
                        str_new_types+=(self.types[vars1[k]]+"("+k+"),")
                        if k not in vars_head:
                            set_new_types.add(self.types[vars1[k]]+"("+k+")")
                        set_new_types1.add(self.types[vars1[k]]+"("+k+")")

                ct+=1

            str_vars = self.setToString(str_new_fluent_vars,",")
            
            temp_str_new_types = self.setToString(set_new_types1,",")
            str_new_fluent = str_new_fluent.replace("vars",str_vars)
            #str_new_fluent = str_new_fluent + str_new_types + "."
            str_new_fluent = str_new_fluent + temp_str_new_types + "."

            str_fluent_def1 = str_fluent_def1.replace("vars",str_vars)
            #str_fluent_def1 = str_fluent_def1 + str_new_types + "."
            str_fluent_def1 = str_fluent_def1 + temp_str_new_types + "."

            str_fluent_def2 = str_fluent_def2.replace("vars",str_vars)
            #str_fluent_def2 = str_fluent_def2 + str_new_types + "."
            str_fluent_def2 = str_fluent_def2 + temp_str_new_types + "."

            str_aggr_print = str_aggr_print.replace("vars",str_vars)


            types = self.setToString(set_new_types," : ")
            str_aggr_print = str_aggr_print.replace("types",types)
            
            
            #print the fluent definitions
            self.instal_print("\n {} \n ".format(str_fluent_def1) )
            self.instal_print("\n {} \n ".format(str_fluent_def2) )

            #print the new rule
            self.instal_print("\n {} \n ".format(str_new_fluent) )
            
            #print the new aggregagte
            self.instal_print("\n {}, ".format(str_aggr_print) )
            #self.instal_print(" {}, %%% ".format(str_new_types) )


    #New function added by Andreasa 07/07/2021
    def setToString(self,setToChange,char):
        retStr = ""
        c=1
        l=len(setToChange)   
        for s in setToChange: 
            if c==l:
                retStr+=s 
            else:
                retStr+=s+char 
            c+=1
        return retStr

    #New function added by Andreasa 23/06/2021
    def checkAggregates(self,c):
        i=0
        arr = []
        aggr = False
        term = ''
        #print('In checkAggregates function C- ',c)
        for k in c:
            #print('K is ',k)
            ch = k[0]
            #i+=1
            if aggr:
                arr.append(k)
            if(len(k)>2):
                #I cannot think of a more elegant way, maybe recursion
                for l in k:
                    chk = l[0]
                    if aggr:
                        arr.append(l)
                    if chk == 'aggrCount':
                        aggr = True
                        term = l[1]
            else:
                if ch == 'aggrCount':
                    aggr = True
                    term = k[1]
        # if aggr:
        #     print('List after aggr',arr)
        return aggr,arr,term
    
    
    def printCondition(self, c, institution: str=None):
        if not institution:
            institution = self.names["institution"]
        if not c:
            return
        if c[0] == 'and':
            self.printCondition(c[1],institution=institution)
            self.printCondition(c[2],institution=institution)
        elif c[0] == 'not':
            self.instal_print("   not")
            self.printCondition(c[1],institution=institution)
        elif c[0] == '==':
            self.instal_print("   {l}=={r},".format(l=c[1][0], r=c[1][1]))
        elif c[0] == '!=':
            self.instal_print("   {l}!={r},".format(l=c[1][0], r=c[1][1]))
        elif c[0] == '<':
            self.instal_print("   {l}<{r},".format(l=c[1][0], r=c[1][1]))
        elif c[0] == '>':
            self.instal_print("   {l}>{r},".format(l=c[1][0], r=c[1][1]))
        elif c[0] == '<=':
            self.instal_print("   {l}<={r},".format(l=c[1][0], r=c[1][1]))
        elif c[0] == '>=':
            self.instal_print("   {l}>={r},".format(l=c[1][0], r=c[1][1]))
        else:
            self.instal_print("   holdsat({fluent},{inst},I)," # 20190606 JAP: add arg to holdsat
                              .format(fluent=self.extendedterm2string(c), inst=institution))




    def term2string(self, p):
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
            r = self.term2string(p[1]) + ' ' + p[0] + \
                ' ' + self.term2string(p[2])
        else:
            r = '(' + args[0]
            for x in args[1:]:
                r = r + ',' + x
            r = p[0] + r + ')'
        return r

    def extendedterm2string(self, p):
        # Legacy.
        if p[0] in ['perm', 'viol', 'pow']:
            r = p[0] + '(' + self.term2string(p[1]) + ')'
        elif p[0] == 'obl':
            r = p[0] + '(' + self.term2string(p[1][0]) + ',' + self.term2string(p[1][1]) + ',' + self.term2string(
                p[1][2]) + ')'
        elif p[0] in ["tpow", "ipow", "gpow"]:
            r = p[0] + '(' + p[1][0] + ',' + \
                self.extendedterm2string(p[1][1]) + ',' + p[1][2] + ')'
        else:
            r = self.term2string(p)
        return r



#####  functions added from Tingting's s1_preprocess.py - July 12-13, 2021 %%%%
### Updated Jul 14-18,2021

    # ------------------------------------------------
    #  parser part until here
    #
    #  The following are functions that are used in pre-processing phase
    # ------------------------------------------------

    #-------------------
    # noninertials
    #-------------------

    # pre-processing for noninertial rules
    #sys.stdout = prep
    #def instal_print_noninertials(self, whens: list) -> None:

    def print_prep_noninertial(self, whens: list) -> None:

        self.instal_print("%")
        self.instal_print("% ---------------------------------------------------------------- ")
        self.instal_print("%  Pre-processing: noninertial rules")
        self.instal_print("% ---------------------------------------------------------------- ")

        self.instal_print("%")
        self.instal_print("% noninertial rules")
        #for rl in noninertials:
        for rl in whens:
            [nif,ante] = rl
            vars1 = {}

            check,ar,trm = self.checkAggregates(ante)
            #uodated to print the aggregates normally but every other non-interial with the try del axioms
            if check:
                self.temp = nif

                self.instal_print("%\n% Translation of {nif} when {ante} inclusive of new predicate definitions if necessary \n"
                "% Rule ID: {rId} \n"
                .format(nif=self.extendedterm2string(nif), ante=ante,rId = self.rId))

                self.printAggregates(ar,trm,institution=self.names["institution"])
                  
                self.collectVars(nif, vars1)

                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".
                                format(pred=self.types[vars1[k]], tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                    inst=self.names["institution"]))
                
            else:
                self.collectVars(nif,vars1)
                self.instal_print("%\n"
                    "% Translation of {nif} when {ante}\n"
                    "% Rule ID: {rId} \n"
                    "holdsat({nif},{inst},I) :-"
                    .format(nif=self.term2string(nif),ante=ante,inst=self.names["institution"],
                            rId = self.rId))
                bId = 1
                self.bId=1
                #bId = 0
                #shouldn't bId be increased at some point
                self.prepPrintCondition(self.rId,bId,ante)
                head = "holdsat("+self.term2string(nif)+","+self.names["institution"]+",I)"
                
                #self.prepPrintExtension(self.rId, head)
                self.prepPrintException(self.rId, head)
                
                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars1[k]],tvar=k))
                    #bId+=1
                self.instal_print("   instant(I).\n")
                self.bId=1
                self.prepPrintTryUseForCondition(self.rId, self.bId, ante)

            #moved this increment out of the condition 18/08
            self.rId += 1

    #------------
    # generates
    #------------
    def print_prep_generates(self, generates: list) -> None:
        self.instal_print("%")
        self.instal_print("% ---------------------------------------------------------------- ")
        self.instal_print("%  Pre-processing: generates rules")
        self.instal_print("% ---------------------------------------------------------------- ")

        for rl in generates:
            self.condLits = {}
            [inorexev,inev,cond, ti] = rl

            vars1 = {}
            self.collectVars(inorexev,vars1)
            self.collectVars(cond,vars1)

            if not ti:
                time = ""
            else:
                time = "+" + str(ti)
                #raise InstalCompileError(
                raise Exception(
                    "NOT IMPLEMENTED: Being able to generate events in n timesteps")
            
            for x in inev:
                vars2 = {}
                bId = 3
                self.bId = 3

                self.collectVars(x,vars2)
                #self.instal_print("%\n"
                #    "% Translation of {exev} generates {inev} if {condition}\n"
                #    "% Rule ID: {rId} \n"
                #    "occurred({inev},{inst},I) :- try({rId}, 1, occurred({exev},{inst},I)),\n"
                #    "   try({rId}, 2, holdsat(pow({inev}),{inst},I)),"
                #    .format(exev=self.extendedterm2string(inorexev),
                #            inev=self.extendedterm2string(x),
                #            inst=self.names["institution"],
                #            condition=cond,
                #            rId = self.rId))

                self.instal_print("%\n"
                    "% Translation of {exev} generates {inev} if {condition}\n"
                    "% Rule ID: {rId} \n"
                    "occurred({inev},{inst},I) :- try({rId}, 1, occurred({exev},{inst},I)),\n"
                    "   try({rId}, 2, not occurred(viol({exev}),{inst},I)),"
                    .format(exev=self.extendedterm2string(inorexev),
                            inev=self.extendedterm2string(x),
                            inst=self.names["institution"],
                            condition=cond,
                            rId = self.rId))

                # try-use for all conditions
                #self.instal_print("Condition Var: \n"+str(cond)+"\n\n")
                self.prepPrintCondition(self.rId,bId,cond)

                # extension for head part
                head = "occurred("+self.extendedterm2string(x)+","+self.names["institution"]+",I)"
                
                #self.prepPrintExtension(self.rId, head)
                self.prepPrintException(self.rId, head)
                
                # add the head to modehOCC, in g-rules, only occurred(inst-events) can be the head part
                modeName = 'hOCC_' + str(self.rId)
                modeh = [modeName, x]
                #modehOCC.append(modeh)

                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars1[k]],tvar=k))
                for k in vars2:
                    # should check for consistent usage of k in vars1 and vars2
                    if k not in vars1:
                        self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars2[k]],tvar=k))
                self.instal_print("   inst({inst}), instant(I).".format(
                            inst=self.names["institution"]))

                # print try-use pairs for fixed conditions entered in generates
                lit = "occurred("+self.extendedterm2string(inorexev)+","+self.names["institution"]+",I)"
                self.prepPrintTryDel(self.rId, 1, lit, inorexev)
               
                lit = "not occurred(viol("+self.extendedterm2string(inorexev)+"),"+self.names["institution"]+",I)"
                
                self.prepPrintTryDel(self.rId, 2, lit, inorexev)
                #self.rId =1

                self.bId=3

                # print try-use pairs for all conditions
                self.prepPrintTryUseForCondition(self.rId, self.bId, cond)

                self.rId +=1

    #--------------
    # initiates
    #--------------

    def print_prep_initiates(self, initiates: list) -> None:
       
        self.instal_print("%")
        self.instal_print("% ---------------------------------------------------------------- ")
        self.instal_print("%  Pre-processing: initiates rules")
        self.instal_print("% ---------------------------------------------------------------- ")

        self.instal_print("%")

        for rl in initiates:
            [inev,inits,cond] = rl

            vars1 = {}
            self.collectVars(inev,vars1)
            self.collectVars(cond,vars1)

            for x in inits:
                vars2 = {}
                self.collectVars(x,vars2)
                self.instal_print("%\n"
                    "% Translation of {inev} initiates {inits} if {condition}"
                    .format(inev=self.term2string(inev),inits=x,condition=cond))
                self.instal_print("% Rule ID: {rId} \n"
                    "initiated({inf},{inst},{ev},I) :-\n"
                    "   try({rId}, 1, occurred({ev},{inst},I)),\n"
                    "   try({rId}, 2, not occurred(viol({ev}),{inst},I)),\n"
                    "   try({rId}, 3, holdsat(live({inst}),{inst},I)),"
                    .format(inf=self.extendedterm2string(x),
                            ev=self.term2string(inev),
                            rId=self.rId,
                            inst=self.names["institution"]))

                bId = 4
                self.bId = 4
                # try-use for all conditions
                self.prepPrintCondition(self.rId,bId,cond)
                
                # extension for the head
                head = "initiated("+self.extendedterm2string(x)+","+self.names["institution"]+",I)"
                
                #self.prepPrintExtension(self.rId, head)
                self.prepPrintException(self.rId, head)

                # add the head to modehINIT,
                modeName = 'hINIT_' + str(self.rId)
                modeh = [modeName, x]
                #modehINIT.append(modeh)


                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars1[k]],tvar=k))
                for k in vars2:
                    # should check for consistent usage of k in vars1 and vars2
                    if k not in vars1:
                        self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars2[k]],tvar=k))
                self.instal_print("   inst({inst}), instant(I).\n".format(
                            inst=self.names["institution"]))

                # print try-use statments
                lit = "occurred("+self.term2string(inev)+","+self.names["institution"]+",I)"
                self.prepPrintTryDel(self.rId, 1, lit, inev)
                
                lit = "not occurred(viol("+self.term2string(inev)+"),"+self.names["institution"]+",I)"
                self.prepPrintTryDel(self.rId, 2, lit, inev)

                lit = "holdsat(live("+self.names["institution"]+"),"+self.names["institution"]+",I)"
               
                #self.prepPrintTryDel(self.rId, 3, lit, x)
                self.prepPrintTryDel(self.rId, 3, lit, {})


                self.bId=4

                self.prepPrintTryUseForCondition(self.rId, self.bId, cond)


                self.rId +=1


    #--------------
    # terminates
    #--------------

    def print_prep_terminates(self, terminates: list) -> None:
       
        self.instal_print("%")
        self.instal_print("% ---------------------------------------------------------------- ")
        self.instal_print("%  Pre-processing: terminates rules")
        self.instal_print("% ---------------------------------------------------------------- ")

        self.instal_print("%")

        for rl in terminates:
            [inev,terms,cond] = rl

            vars1 = {}
            self.collectVars(inev,vars1)
            self.collectVars(cond,vars1)

            for x in terms:
                vars2 = {}
                self.collectVars(x,vars2)
                self.instal_print("%\n"
                    "% Translation of {inev} terminates {inits} if {condition}"
                    .format(inev=self.term2string(inev),inits=x,condition=cond))
                self.instal_print("% Rule ID: {rId} \n"
                    "terminated({inf},{inst},{ev},I) :-\n"
                    "   try({rId}, 1, occurred({ev},{inst},I)),\n"
                    "   try({rId}, 2, not occurred(viol({ev}),{inst},I)),\n"
                    "   try({rId}, 3, holdsat(live({inst}),{inst},I)),"
                    .format(inf=self.extendedterm2string(x),
                            ev=self.term2string(inev),
                            rId=self.rId,
                            inst=self.names["institution"]))

                bId = 4
                self.bId = 4
                # try-use for all conditions
                self.prepPrintCondition(self.rId,bId,cond)
                
                # extension for the head
                head = "terminated("+self.extendedterm2string(x)+","+self.names["institution"]+",I)"
                
                #self.prepPrintExtension(self.rId, head)
                self.prepPrintException(self.rId, head)

                # add the head to modehINIT,
                modeName = 'hTERM_' + str(self.rId)
                modeh = [modeName, x]
                #modehINIT.append(modeh)


                for k in vars1:
                    self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars1[k]],tvar=k))
                for k in vars2:
                    # should check for consistent usage of k in vars1 and vars2
                    if k not in vars1:
                        self.instal_print("   {pred}({tvar}),".format(pred=self.types[vars2[k]],tvar=k))
                self.instal_print("   inst({inst}), instant(I).\n".format(
                            inst=self.names["institution"]))

                # print try-use statments
                lit = "occurred("+self.term2string(inev)+","+self.names["institution"]+",I)"
                self.prepPrintTryDel(self.rId, 1, lit, inev)
                
                lit = "not occurred(viol("+self.term2string(inev)+"),"+self.names["institution"]+",I)"
                self.prepPrintTryDel(self.rId, 2, lit, inev)

                lit = "holdsat(live("+self.names["institution"]+"),"+self.names["institution"]+",I)"
               
                #self.prepPrintTryDel(self.rId, 3, lit, x)
                self.prepPrintTryDel(self.rId, 3, lit, {})
                
                self.bId=4

                self.prepPrintTryUseForCondition(self.rId, self.bId, cond)


                self.rId +=1

    def prepPrintCondition(self,rId, bId, c):
        #global condLits
        #bId+=1
        if c==[]: return
        if c[0]=='and':
            #bId+=1
            self.prepPrintCondition(rId,bId,c[1])
            #bId+=1
            #self.prepPrintCondition(rId,bId,c[2])
            self.prepPrintCondition(rId,bId+1,c[2])
        elif c[0]=='not':
            self.prepPrintCondition(rId,bId,c[1])
        else:
            self.instal_print("   try({rId}, {bId}, holdsat({fluent},{inst},I)),".format(
                fluent=self.term2string(c),rId = rId, bId = self.bId,inst = self.names["institution"]))
            lit = "holdsat("+ self.term2string(c)+","+self.names["institution"]+",I)"
            vars1 = {}
            self.collectVars(c,vars1)
            self.condLits[lit] = vars1
            self.bId+=1


    def prepPrintExtension(self,rId, h):
        #how do we decide whether to use not before the extension? or not :)
        self.instal_print("   not extension({rId},{head}),".format(rId = rId, head = h))
        
        #self.instal_print("   extension({rId},{head},(e)),".format(rId = rId, head = h))  
        #ting ting's version ^^^ 
        #what is the (e), I have to figure it out

    def prepPrintException(self,rId, h):
        self.instal_print("   not exception({rId},{head}),".format(rId = rId, head = h))



    def prepPrintTryUseForCondition(self,rId, bId, c):
        if c==[]: return
        if c[0]=='and':
            self.prepPrintTryUseForCondition(rId,bId,c[1])
            self.prepPrintTryUseForCondition(rId,bId+1,c[2])
        elif c[0]=='not':
            self.prepPrintTryUseForCondition(rId,bId,c[1])
        else:
            lit = "holdsat("+ self.term2string(c)+","+self.names["institution"]+",I)"
            #inst = self.names["institution"]
            #self.prepPrintTryUse(rId, bId, lit, c)
            self.prepPrintTryDel(rId, self.bId, lit, c)
            self.bId+=1

    def prepPrintTryUse(self,rId, bId, lit, ev):
        vars1 = {}
        self.collectVars(ev,vars1)

        self.instal_print("try({rId}, {bId}, {lit}) :- use({rId}, {bId}), {lit}.".format(rId=rId, bId=bId, lit=lit))
        domainStr = ""
        for k in vars1:
            domainStr += self.types[vars1[k]]+'('+k+'), '
        
        domainStr +=  "instant(I)."
        self.instal_print("try({rId}, {bId}, {lit}) :- not use({rId}, {bId}),{domainStr}".format(rId=rId, bId=bId, lit=lit, domainStr=domainStr))
        # gather rev(del)
        d = "rev("+self.names["institution"]+", "+str(rId)+", r((hDel, c("+str(rId)+","+str(bId)+"),l)),1)"
        #ruleDelStr.append(d)
        # gather rev(del) to dict
        #if self.ruleDict.has_key(rId): self.ruleDict[rId].append(d) has_key() depreciated in python 3
        if rId in self.ruleDict: self.ruleDict[rId].append(d)
        else: self.ruleDict[rId] = [d]

        # print complementary literals
        self.instal_print ("use({rId},{bId}) :- not del({rId},{bId}),rId({rId}), index({bId}).".format(rId = rId, bId = bId))
        self.instal_print ("del({rId},{bId}) :- rev({inst}, {rId}, r((hDel, c({rId},{bId}),l)),1). ".format(rId = rId, bId = bId, inst = self.names["institution"]))
        self.instal_print ("rId({rId}). index({bId}).".format(rId = rId, bId = bId))

    def prepPrintTryDel(self,rId, bId, lit, ev):
        vars1 = {}
        self.collectVars(ev,vars1)
        
        #self.instal_print("\n")

        domainStr = ""
        for k in vars1:
            domainStr += self.types[vars1[k]]+'('+k+'), '
        
        #domainStr += "inst(rooms), instant(I)"
        domainStr += "instant(I)"
        #domainStr = domainStr [:-2]

        self.instal_print("try({rId}, {bId}, {lit}) :- not del({rId}, {bId}), {lit}, {dom}.\n".format(rId=rId, bId=bId, lit=lit, dom = domainStr))

        self.instal_print("try({rId}, {bId}, {lit}) :- del({rId}, {bId}), {dom}.\n".format(rId=rId, bId=bId, lit=lit, dom = domainStr))


