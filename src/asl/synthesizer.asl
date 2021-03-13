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
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : not handling <- 
										   
										    +handling;
										    
										    .print("Request received from ",Ag," will handle");
										    
										    ?entered(Ti);
											-+entered(Ti+1);
										    
										    +handlingCur(ActRes,ActAtmpt,Exp,Ag);
										    +to_inform(Ag,ActAtmpt);
										    .abolish(request(ActRes,ActAtmpt,Exp)[source(Ag)]);
										    
										    .print('Action attempted ', ActAtmpt);
											checkState(ActAtmpt);
										    
										    .wait(1000);
											.count(request(_,_,_)[source(_)],C); 
											
											if (C==1)
											{
//												+checkone;
												.print("Only 1 request to handle and currently handling");
											//	-request(ActRes,ActAtmpt,Exp);
											//	.abolish(request(ActRes,ActAtmpt,Exp)[source(Ag)]);
											}
											else
											{
											
												.print("Checking out other requests");
											}
											
										.

							//			. 

@request_b[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : handling <- .print('Queued request for ',Ag);
					
										?handlingCur(A1,Act1,E1,Ag1);
										room_experiment.checkSimilarRequest(ActRes, ActAtmpt,Exp,A1,Act1,E1,R1);
										
										.print("R1 is ", R1);
										if(R1==1)
										{
											
										
											.perceive;
											.findall([X,Y,Z,Q],to_handle(X,Y,Z,Q),Lists2);  
											.length(Lists2,Sze);
											.count(to_handle(_,_,_,_),C); 
											.print("Size: ",Sze, ", Count: ", C);	
											
											if(Sze>0)
											{
												.print("Checking if this request is different from the queued request");
												
												//currently only checks similarity with the first request in the list
												//TODO: CHECK THROUGH THE LIST OF REQUESTS
												
												for (.member(Z,Lists2))
									  			{
									  				room_experiment.getItems(Z,3,I1,I2,I3);
	//												room_experiment.stripString(I1,S1);
	//												room_experiment.stripString(I2,S2);
	//												room_experiment.stripString(I3,S3);
	//												.print("String S2 - ", S2);
													room_experiment.checkSimilarRequest(ActRes,ActAtmpt,Exp,I1,I2,I3,R2);
									  				
													if(R2==1)
													{
														.print("Adding this request to the official queue because it is different to what is there");
							  							+to_handle(ActRes,ActAtmpt,Exp,Ag);
							  							+to_inform(Ag,ActAtmpt);
													}
													else
													{
														.print("No need to add the request to the official queue, a similar request is already in the queue");
														+to_inform(Ag,I2);
													}
										
									  			}
												
												
												//.nth(0,Lists2,First);
												


											}				
						  					else
						  					{
						  						.print("Adding a request to the official queue");
						  						+to_handle(ActRes,ActAtmpt,Exp,Ag);
						  						+to_inform(Ag,ActAtmpt);
						  					}
											
											
										}
										else
										{
											.print("Need to inform this agent ", Ag);
											+to_inform(Ag,Act1);
										}	
										//-request(ActRes,ActAtmpt,Exp);
										
										.abolish(request(ActRes,ActAtmpt,Exp)[source(Ag)]);
										.print("Removed request from list");
										
										
//										.count(request(_,_,_)[source(_)],C); 
										?entered(T);
										-+entered(T+1);	
//										if ((T+1)==C)
//										{
//											-handling;
//											-+entered(0);	
//										}

										. 

//+request(ActRes, ActAtmpt,Exp)[source(Ag)] : handling <- .print('Queued request for ',Ag);
//										//experiment5.myPrint("Action attempted ", ActAtmpt);
//										//+handling;
//									//	+to_handle(request(Ag,ActRes,ActAtmpt,Exp));
//										//checkState(ActAtmpt);
//											.count(request(_,_,_)[source(_)],C); 
//											?entered(T);
//											.print("T is ",T);
//											-+entered(T+1);	
//											-+countRequests(C);
//											if ((T+1)==C)
//											{
////													if (C==1)
////													{
////														+checkone;
////														.print("Only 1 request to handle");
////														.print('Action attempted ', ActAtmpt);
////														checkState(ActAtmpt);
////														-request(ActRes,ActAtmpt,Exp);
////													}
////													else
////													{
//													.print('Message received from ', C, ' agents, will handle');
//													.findall([A,Ac,E,Agn],request(A,Ac,E)[source(Agn)],Reqs); 
//													//.findall([A,Ac,E],request(A,Ac,E,Ag),Reqs); 
//												//	.abolish(request(_,_,_));
//										//			-+entered(0);
//													.print(Reqs);
//													.abolish(request(_,_,_));
////													for (.member(Z,Reqs))
////	  												{
////	  													room_experiment.getItems(Z,I1,I2,I3,I4);
////	  													.print("Deleting requests from ", I4);
////	  													.abolish(request(_,_,_)[source(I4)]);
////	  												//	-request(I1,I2,I3)[source(I4)];
////	  													//.print('Action attempted ', I2);
////	  												}
//	  												-handling;
//													-+entered(0);
//													room_experiment.checkSameReqs(Reqs,Ret,Num);
////													.print("Returned ",Ret," ",Num);
//													if(Num==0)
//													{
//														room_experiment.getItems(Ret,I1,I2,I3,I4);
//														.print('Action attempted ', I2);
//														checkState(I2);
//													}
//													else
//													{
////														.print("List of items: ",Ret);
//														for (.member(Z,Ret))
//		  												{
//		  													room_experiment.getItems(Z,I1,I2,I3,I4);
//		  													.print('Action attempted ', I2);
//		  												}
//		  												
//		  											 }	
////													}
//													
////		  											
//												}
////												.drop_intention(request(X,Y,Z));
////												.print("TRYING DROPPING THINGS");
//										. 



/*
 * Old working request percept handler
 * +request(ActRes, ActAtmpt,Exp)[source(Ag)] : true <- //.print('Message received from ', Ag , ', will handle');
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
											
											+handling;
											//.length(Reqs,Ct);
												
											//	.print('Message received from ', Ct, ' ',C, ' agents, will handle');		
  						//					.print(Reqs);
//  											
											//.abolish(request(_,_,_));
									
										//.print('Action attempted ', ActAtmpt);
									
										//!handle;
										.
// 
 * 
 */
@eventOccurred[atomic]
+eventOccurred(When): true <- .print("Attempting to revise norms");
                           revise(When,3);
                          // -eventOccurred(When);
                           .abolish(eventOccurred(When));
                           .


+revisionFailed: true <- .print("No possible revisions found");
						//-revisionFailed;
						//.print("Removing revision failed percept");
						.abolish(revisionFailed);
						
						?handlingCur(ActRes,ActAtmpt,Exp,Ag);
						
					//	.findall([W,A],to_inform(W,A),List1); 
						
						.findall([W,A],to_inform(W,ActAtmpt),List1); 
						
						.length(List1,Sizer)
						.print("I have to inform ", Sizer, " agents");	
								
						
						room_experiment.getRoom(ActAtmpt,R1);	
						-+room1(R1);		
						for (.member(Z,List1))
		  				{
		  					room_experiment.getItems(Z,2,Agn,Act);
		  					
		  					room_experiment.getRoom(Act,R2);	
		  					-+room2(R2);
		  					.print("R1 = ",R1, " R2 = ", R2);	
		  					?room1(Roo1);
		  					?room2(Roo2);
		  					if(Roo1==Roo2)
		  					{
		  						.print("Inform ",Agn," no possible revisions");
		  						
		  					}
		  					
//		  					if(R1==R2)
//		  					{
//		  						.print("Inform ",Agn," no possible revisions");
//		  						
//		  					}
		  					
		  				}
		  				.abolish(to_inform(_,ActAtmpt));							
						!handle;
						.


+!fix: true <- revise(0).
 
 +!handle: true <- .print("Handling the others");
 					.perceive;
					.findall([A5,Ac5,E5,Aa],to_handle(A5,Ac5,E5,Aa),Reqs);  
					.length(Reqs,Size);
					.print("Number of to_handle beliefs found ", Size);
					.count(to_handle(_,_,_,_),C); 
					.print("Count: ", C);
					if(Size==0)
					{
						.print("No events to handle");
						
						-handling;
						-+entered(0);	
					}				
  					else
  					{
  						.print("Number of requests to handle ",Size);
  						//.print("First: ",Reqs[0]);
  						.print("Need to update current handling belief ");
  					//	.abolish(handlingCur(_,_,_,_));
  						// -+handlingCur(ActRes,ActAtmpt,Exp,Ag);
  						
  						for (.member(Z,Reqs))
		  				{
		  					room_experiment.getItems(Z,4,I1,I2,I3,I4);
		  					
//							room_experiment.stripString(I1,S1);
//							room_experiment.stripString(I2,S2);
//							room_experiment.stripString(I3,S3);
//							room_experiment.stripString(I4,S4);
							-+handlingCur(I1,I2,I3,I4);
		  					//.print("Action attempted in next  ", I2);
		  					//-to_handle(I1,I2,I3);
		  					//.abolish(to_handle(I1,I2,I3,I4));	
		  					
		  				//	.abolish(to_handle(S1,S2,S3,S4));	//TROUBLE WITH STRINGS AGAIN
		  					.abolish(to_handle(_,_,_,_));	
		  					
		  					!handle_reqs(I1,I2,I3,I4);
		  				}
  					}
  					.


@handle_reqs[atomic]
+!handle_reqs(H,I,J,K): true <- .print("handling remaining requests");
						checkState(I);
						.


+deniedEntry[source(Ag)] : true <-  .print("Message received from ",Ag,", will handle").
									//experiment5.myPrint('Message received from ', Ag , ', will handle'). 
									//experiment5.myPrint(msg). 
									//checkState.
					//.send(Ag,tell,msg(M)).
 
 
