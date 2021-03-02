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



 
@request[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : true <- //.print('Message received from ', Ag , ', will handle');
										//	.wait(1000);
											//delay(1000)
										    .print("Request received");
											.count(request(_,_,_)[source(_)],C); 
											?entered(T);
											-+entered(T+1);	
											-+countRequests(C);
											//.print('T is ', T, 'C is ',C);
											if ((T+1)==C)
											{
												if (C==1)
												{
													+checkone;
													.print("Only 1 request to handle");
													.print('Action attempted ', ActAtmpt);
													checkState(ActAtmpt);
													-request(ActRes,ActAtmpt,Exp);
												}
												else
												{
													.print('Message received from ', C, ' agents, will handle');
													.findall([A,Ac,E],request(A,Ac,E),Reqs); 
													.abolish(request(_,_,_));
													-+entered(0);
													.print(Reqs);
													room_experiment.checkSameReqs(Reqs,Ret,Num);
													.print("Returned ",Ret," ",Num);
													if(Num==0)
													{
														room_experiment.getItems(Ret,I1,I2,I3);
														.print('Action attempted ', I2);
														checkState(I2);
													}
													else
													{
														.print("List of items: ",Ret);
														for (.member(Z,Ret))
		  												{
		  													room_experiment.getItems(Z,I1,I2,I3);
		  													.print('Action attempted ', I2);
		  												}
		  												
		  											 }	
												}
												
												//}
//												
//												for (.member(Z,Reqs))
//	  											{
//	  												//.nth(0,Z,Xn);
//	  												//.print(Xn);
//	  												//.print("Loop item: ",Z);
//	  												//.print(Z);
//	  												room_experiment.getItems(Z,I1,I2,I3);
//	  												
//	  												//.print('Action attempted ', I2);
//	  												checkState(I2);
//	  											//	delay;
//	  											//	!fix;
//	  												//check and see if the items are the same, 
//	  												//if they are, only one action required
//	  												
//	  											}
	  											
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

@eventOccurred[atomic]
+eventOccurred(When): true <- .print("Attempting to revise norms");
                           revise(When,3);
                           -eventOccurred(When);
                           .


+revisionFailed: true <- .print("No possible revisions found");
						-revisionFailed;
						.


+!fix: true <- revise(0).
 
  +!handle: true <- .print("Handling the others");
					.findall(r(A,Ac,E),request(Act,AcAtmp,Exp),Reqs);  					
  					.print(Reqs[0]);
  					.


+deniedEntry[source(Ag)] : true <-  .print("Message received from ",Ag,", will handle").
									//experiment5.myPrint('Message received from ', Ag , ', will handle'). 
									//experiment5.myPrint(msg). 
									//checkState.
					//.send(Ag,tell,msg(M)).
 
 
