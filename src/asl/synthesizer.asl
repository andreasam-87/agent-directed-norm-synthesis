// Agent supervisor in project experiment5

/* Initial beliefs and rules */

/* Initial goals */
entered(0).
!start.


/* Plans */

+!start : true <- .print("hello world.").
				 //experiment5.myPrint('hello world'). 

+stateOccurred(Ev,WhenEv) : true <- .print("Event: ",Ev, " occurred at state: ",WhenEv);
									. 



 
//@request[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : true <- //.print('Message received from ', Ag , ', will handle');
											
											.count(request(_,_,_)[source(_)],C); 
											?entered(T);
											-+entered(T+1);	
											.print('T is ', T, 'C is ',C);
											if ((T+1)==C)
											{
												.print('Message received from ', C, ' agents, will handle');
												.findall([A,Ac,E],request(A,Ac,E),Reqs); 
												.print(Reqs);
												for (.member(Z,Reqs))
	  											{
	  												//.nth(0,Z,Xn);
	  												//.print(Xn);
	  												.print(Z);
	  											}
											}
											
											//.length(Reqs,Ct);
												
											//	.print('Message received from ', Ct, ' ',C, ' agents, will handle');		
  						//					.print(Reqs);
//  											
											//.abolish(request(_,_,_));
									
										//.print('Action attempted ', ActAtmpt);
									
										//!handle;
										.
// 
//@request[atomic]
//+request(ActRes, ActAtmpt,Exp)[source(Ag)] : not handling <- .print('Message received from ', Ag , ', will handle');
//										.print('Action attempted ', ActAtmpt);
//										+handling;
//										+handle_request;
//										!handle;
										//+to_handle(request(Ag,ActRes,ActAtmpt,Exp));
										//checkState(0);
									//	findall(r(A,Ac,E),request(Act,AcAtmp,Exp),Reqs); 
									//	.print(Reqs); 
										//checkState(ActAtmpt);
										//-request(ActRes, ActAtmpt,Exp)[source(Ag)];
									
										//wait(1000);
										//!handle;
							//			. 
//
//+request(ActRes, ActAtmpt,Exp)[source(Ag)] : handling <- .print('Queued request for ',Ag);
//										//experiment5.myPrint("Action attempted ", ActAtmpt);
//										//+handling;
//										+to_handle(request(Ag,ActRes,ActAtmpt,Exp));
//										//checkState(ActAtmpt);
//										
//										. 


 
  +!handle: true <- .print("Handling the others");
					.findall(r(A,Ac,E),request(Act,AcAtmp,Exp),Reqs);  					
  					.print(Reqs[0]);
  					.


+deniedEntry[source(Ag)] : true <-  .print("Message received from ",Ag,", will handle").
									//experiment5.myPrint('Message received from ', Ag , ', will handle'). 
									//experiment5.myPrint(msg). 
									//checkState.
					//.send(Ag,tell,msg(M)).
 
 
