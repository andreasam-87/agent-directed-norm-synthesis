// Agent supervisor in project experiment5

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").
				 //experiment5.myPrint('hello world'). 

+stateOccurred(Ev,WhenEv) : true <- .print("Event: ",Ev, " occurred at state: ",WhenEv);
									. 

 
@request[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : not handling <- .print('Message received from ', Ag , ', will handle');
										.print('Action attempted ', ActAtmpt);
										+handling;
										+handle_request;
										//+to_handle(request(Ag,ActRes,ActAtmpt,Exp));
										//checkState(0);
										checkState(ActAtmpt);
										
										. 

+request(ActRes, ActAtmpt,Exp)[source(Ag)] : handling <- .print('Queued request for ',Ag);
										//experiment5.myPrint("Action attempted ", ActAtmpt);
										//+handling;
										+to_handle(request(Ag,ActRes,ActAtmpt,Exp));
										//checkState(ActAtmpt);
										
										. 


+deniedEntry[source(Ag)] : true <-  .print("Message received from ",Ag,", will handle").
									//experiment5.myPrint('Message received from ', Ag , ', will handle'). 
									//experiment5.myPrint(msg). 
									//checkState.
					//.send(Ag,tell,msg(M)).
  
 
