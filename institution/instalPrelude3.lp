ifluent(0,0).
nifluent(0,0).
oblfluent(0,0).
initiated(0,0,0,0).
xinitiated(0,0,0,0,0).
terminated(0,0,0,0).
xterminated(0,0,0,0,0).
_typeNotDeclared :- 1 == 2.
bridge(1).

% TIME
%T
start(0).
instant(0..T) :- final(T).
next(T,T+1) :- instant(T).
%final(1).

% TODO: Remove the need for this in constraint generation
true.

% CREATION
% Event: _create_ (type: ex)
event(_create_) :- true.
evtype(_create_,X,ex) :- inst(X).
evinst(_create_,X) :- inst(X).
ifluent(pow(_create_),X) :- inst(X).
ifluent(perm(_create_),X) :- inst(X).
fluent(pow(_create_),X) :- inst(X).
fluent(perm(_create_),X) :- inst(X).
event(viol(_create_)) :- true.
evtype(viol(_create_),X,viol) :- inst(X).
evinst(viol(_create_),X) :- inst(X).
holdsat(live(X),X,I) :- start(I), inst(X).
holdsat(perm(_create_),X,I) :- start(I), inst(X).
holdsat(pow(_create_),X,I) :- start(I), inst(X).

% FLUENT RULES
fluentterminated(P, In, I) :- terminated(P, In, _, I), instant(I), inst(In).
fluentterminated(P, In, I) :- xterminated(InS, P, In, _, I), instant(I), inst(In), inst(InS).
fluentinitiated(P, In, E, I) :- initiated(P, In, E, I), instant(I), inst(In), event(E).
fluentinitiated(P, In, E, I) :- xinitiated(InSo, P, In, E, I), inst(InSo), inst(In), instant(I), event(E).
holdsat(P,In,J):- holdsat(P,In,I),not fluentterminated(P,In,I),
	next(I,J),ifluent(P, In),instant(I),instant(J), inst(In).
holdsat(P,In,J):- fluentinitiated(P,In,E,I),next(I,J),
	ifluent(P, In),instant(I),instant(J), inst(In), event(E).

holdsat(P,In,J):- holdsat(P,In,I),not fluentterminated(P,In,I),
	next(I,J),ifluent(P, In),instant(I),instant(J), bridge(In).
holdsat(P,In,J):- initiated(P,In,E,I),next(I,J),
	ifluent(P, In),instant(I),instant(J), bridge(In), event(E).

% EXTERNAL FLUENTS
% #external holdsat(F,I) : ifluent(F,I) : inst(I).
% need to make _create_ something the user can't write
% holdsat(F,I,J) :- holdsat(F,I), start(J).

% EVENTS OCCUR
% note use of _ in holdsat in next two rules; ought not to matter
occurred(E,In,I):- evtype(E,In,ex),observed(E,In,I),instant(I), inst(In), holdsat(pow(E),In,I).

occurred(unempoweredEvent(E), In, I) :- evtype(E,In,ex),observed(E,In,I),instant(I), inst(In), not holdsat(pow(E),In,I). 

occurred(_unrecognisedEvent(E),In,I) :- not evtype(E,In,ex), observed(E,In,I),
	instant(I), inst(In).

occurred(null,In,I) :- not evtype(E,In,ex), observed(E,In,I),
	instant(I), inst(In). % TODO: What's the point of the null anyway?


% for observation sequences
%%%#external extObserved(E,I) : event(E) : evtype(E,_,ex) : instant(I).

%recEvent(I) :- extObserved(E, I), event(E), instant(I), not final(I).
%extObserved(_unrecognisedEvent, I) :- not recEvent(I), _eventSet(I).

%EVENT SET
%%%#external _eventSet(I) : instant(I).

% VIOLATIONS FOR NON-PERMITTED EVENTS
% note use of _ in holdsat twice here; ought not to matter
occurred(viol(E),In,I):-
	occurred(E,In,I),
	not holdsat(perm(E),In,I),
	holdsat(live(In),In,I),evinst(E,In),
	event(E),instant(I),event(viol(E)),inst(In). 

%%  mode COMPOSITE is chosen:\n
1 {genObserved(E, J) : evtype(E, In, ex) : inst(In)} 1:- instant(J), not final(J), not extObserved(F, J), event(F).

:- observed(E,J),observed(F,J),instant(J),evtype(E,InX,ex), evtype(F,InY,ex), E!=F,inst(InX;InY).
obs(I):- observed(_,I),instant(I).
:- not obs(I), not final(I), instant(I), inst(In).
observed(E, I) :- genObserved(E, I), not final(I).
observed(E, I) :- extObserved(E, I), not final(I).
observed(E,In,I) :- observed(E,I), inst(In), instant(I).

:- _typeNotDeclared. %defends against partially grounded institutions.

_preludeLoaded.

#show observed/3.
#show occurred/3.
#show holdsat/3.
#show initiated/4.
#show terminated/4.

#hide .
% drop answer sets with _create_ events
:- observed(_create_,I).