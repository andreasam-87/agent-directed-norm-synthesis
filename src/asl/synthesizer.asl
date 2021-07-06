// Agent supervisor in project experiment5

/* Initial beliefs and rules */

/* Initial goals */
entered(0).
what(0).
name(bob).
name(alice).
name(jo).
name(jane).

updateCount(0).
count(0).
synCount(0).

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
										    .send(coordinator,tell,informCoordinatorHandlingRevision(ActRes,ActAtmpt,Exp,Ag));
										    +to_inform(Ag,ActAtmpt);
										    .abolish(request(ActRes,ActAtmpt,Exp)[source(Ag)]);
										    
//										    .perceive;
//										    
//										    room_experiment.checkRevisionActive(Bool);
//										     //.print("Bool is ", Bool);
//										    if(Bool==0)
//										    {
//										    	.print("I have to wait it seems");
//										    	 room_experiment.addRevisionItem(R,Ag);
//										    }
//										    else
//										    {
//										    	.concat("request(",ActRes,",",ActAtmpt,",",Exp,")",R)
//										    	.print("R is ", R);
//										    	 room_experiment.addRevisionItem(R,Ag);
////										    	  room_experiment.checkRevisionActive(B);
////										    	  .print("Bool is ", B);
//										    }
										   
										    
//										    if(pause)
//										    {
//										    	.print("I have to wait because of pause");
//										    }
//										    else
//										    {
//										    	.print("Tell other agents to wait");
//										    	.all_names(Names);
//										    	.my_name(Me);
//										    	for (.member(N,Names))
//									  			{
//									  				//.substring("a","bbacc",0): false. When the third argument is 0, 
//									  				//.substring works like a java startsWith method.
//									  				//
//									  				if(.substring("syn",N,0) & not (Me==N))
//									  				{
//									  					.send(N,tell,pause);
//									  					
////									  					if(not (my_name(Me)==N))
////									  					{
////									  						.send(N,tell,pause);
////									  					}
//									  					
//									  				}
//									  				
//									  			}
//										    	
//										    }
//										    
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
							
+pause[source(Ag)]: true <- .print(Ag, " asked me to wait");
							//-pause[source(Ag)].
							.

@request_b[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : handling <- .print('Queued request for ',Ag);
					
										?handlingCur(A1,Act1,E1,Ag1);
										room_experiment.checkSimilarRequest(ActRes, ActAtmpt,Exp,A1,Act1,E1,R1);
										
										//.print("R1 is ", R1);
										if(R1==1)
										{
											
										
											//.perceive;
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
	
														room_experiment.checkSimilarRequest(ActRes,ActAtmpt,Exp,I1,I2,I3,R2);
									  				
													if(R2==1)
													{
														.print("Adding this request to the official queue because it is different to what is there");
							  							+to_handle(ActRes,ActAtmpt,Exp,Ag);
							  							+to_inform(Ag,ActAtmpt);
							  							+different;
													}
													else
													{
														.print("No need to add the request to the official queue, a similar request is already in the queue");
    												//	room_experiment.stripString(I2,S2);
														+to_inform(Ag,I2);
														+similar;
													}
													
													//rethink this to work with the list as it adds something to based on all the to handle stuff there 
													//if similar to anythin in the list then add a new request, otherwise add a to_inform
									  			}
												if(similar & different)
												{
													.abolish(to_handle(ActRes,ActAtmpt,Exp,Ag));
							  						.abolish(to_inform(Ag,ActAtmpt));
							  						-similar;
							  						-different;
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

										.abolish(request(ActRes,ActAtmpt,Exp)[source(Ag)]);
										.print("Removed request from list");
	
										?entered(T);
										-+entered(T+1);	

										. 



//@eventOccurred[atomic]
+eventOccurred(When): true <- .print("Attempting to revise norms");
							
							?what(D);
							.print("What is happening - ", D);
							-+what(D+1);
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
                           revise(When,3,ActAtmpt,ActRes);
                           -eventOccurred(When);
                         // .abolish(eventOccurred(When)); //something strange happens when I abolish
                         //the previous event returns and then this part runs twice again, how and why? 
                           
                           .



+revisionSuccessful(T): true <- .print("Revision successful ");
							?count(C);
							-+count(C+1);
							if(T==active)
							{
								.print("A solution exists for your problem but it is currently active so no change required");
								?handlingCur(ActRes,ActAtmpt,Exp,Ag);
								
								.print("Action under review: ", ActAtmpt);
								//.findall([W,ActAtmpt],to_inform(W,ActAtmpt),List1); 
								.findall(W,to_inform(W,ActAtmpt),List1); 
						
								.length(List1,Sizer)
								.print("I have to inform ", Sizer, " agents that the revision solution is currently active");	
//								
//						
//								room_experiment.getRoom(ActAtmpt,R1);	
//		
//								for (.member(Zn,List1))
//				  				{
//				  					room_experiment.getItems(Zn,2,Agn,Act);
//		
//									.send(Agn,tell,revisionActive);
//				  					.print("Inform ",Agn,", revision solution currently active");
//		
//				  				}
//				  				.abolish(to_inform(_,ActAtmpt));	
//				  				
				  				.send(List1,tell,revisionActive);
							}
							else
							{
								 .print("T is ",T);
							}
							.abolish(revisionSuccessful(_));
							//-revisionSuccessful(_);
						
																				
						!handle;
							.

+revisionSuccess: true <- .print("A possble revision found, inform agents of change");
						//	.abolish(revisionSuccess);
							-revisionSuccess;
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
							.send(coordinator,tell,getCoordinatorPermission(ActAtmpt,"roomsInst.lp"));
//							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
//
//	
//							.all_names(Names);
//							.my_name(Me);
//							for (.member(N,Names))
//							{
//								//.substring("a","bbacc",0): false. When the third argument is 0, 
//								//.substring works like a java startsWith method.
//								
//								if(.substring("syn",N,0) & not (Me==N))
//								{
//									.send(N,tell,seekInstChangeConsensus(ActAtmpt,"roomsInst.lp"));
//
//									// .concat("request(",ActRes,",",ActAtmpt,",",Exp,")",R) 					
//								}
//									  				
//							}
							
							
						//	+updateEnv;
							
							.
							
+coor_permissiongranted[source(Ag)]: true <- .print("Received permission from ",Ag ," to begin discussion");
											
											//?handlingCur(ActRes,ActAtmpt,Exp,Ag);

	
											.all_names(Names);
											.my_name(Me);
											for (.member(N,Names))
											{
												//.substring("a","bbacc",0): false. When the third argument is 0, 
												//.substring works like a java startsWith method.
												
												if(.substring("syn",N,0) & not (Me==N))
												{
													.send(N,tell,seekInstChangeConsensus(ActAtmpt,"roomsInst.lp"));
				
													// .concat("request(",ActRes,",",ActAtmpt,",",Exp,")",R) 					
												}
													  				
											}
							
											
											
											. 

@seekInstChangeConsensus[atomic]
+seekInstChangeConsensus(Action,NewInst)[source(Ag)]: true <- .print(Ag, " is seeking consensus to change the institution"); 
																.print("Using sense to see if this is an acceptable change"); 
																+checkingFirst;
																//suspend;
																//+sense_time(Action,NewInst);
																+giveResponseTo(Ag);
																.print("about to try to sense the institution");
																sense("enter(sample,room1)",NewInst);
																
																. 

													
+revisionFailed: true <- .print("No possible revisions found");
						//.abolish(revisionFailed);
						-revisionFailed;
						?handlingCur(ActRes,ActAtmpt,Exp,Ag);
						//.findall([W,ActAtmpt],to_inform(W,ActAtmpt),List1); 
						.findall(W,to_inform(W,ActAtmpt),List1); 
						
						.length(List1,Sizer)
						.print("I have to inform ", Sizer, " agents that there is no possible revisions");	
								
//						
//						room_experiment.getRoom(ActAtmpt,R1);	
//		
//						for (.member(Zn,List1))
//		  				{
//		  					room_experiment.getItems(Zn,2,Agn,Act);
//							.send(Agn,tell,revisionFailed);
//		  					.print("Inform ",Agn," no possible revisions");
//
//		  				}
		  				
		  				.send(List1,tell,revisionFailed);
		  				.abolish(to_inform(_,ActAtmpt));							
						!handle;
						.


+!fix: true <- revise(0).
 
 @handle[atomic]
 +!handle: true <- .print("Handling the others");
 					//.perceive;
 					//.drop_event(revisionSuccessful(_));
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
						.abolish(handlingCur(_,_,_,_));
						
						//trying something
						//+sense_time;
						//!do_sense;

					}				
  					else
  					{

  						.print("Preparing to handle the first item in the list");
  						.nth(0,Reqs,Z);
  						room_experiment.getItems(Z,4,I1,I2,I3,I4);
  						-+handlingCur(I1,I2,I3,I4);
  						.abolish(to_handle(I1,I2,I3,I4));
  						!handle_reqs(I1,I2,I3,I4);

  					}
  					.
  					
+!handle_reqs(H,I,J,K): true <- .print("handling next request");
						checkState(I);
						.

+sense_time: true <- !do_sense;
				
					-sense_time;
					.
						
+!do_sense: true <- .print("about to try to sense the institution");
					sense("enter(sample,room1)");
					.

+revisionAcceptable: true <- .print("I can accept the revision");
							 ?giveResponseTo(Ag);
							 .send(Ag,tell,instChangeConsensusGranted);
							//resume;
							 .
					
+revisionUnacceptable: true <- .print("I cannot accept the revision");
					?giveResponseTo(Ag);
					.send(Ag,tell,instChangeConsensusNotGranted);
					.
					
+deniedEntry[source(Ag)] : true <-  .print("Message received from ",Ag,", will handle").


+instChangeConsensusGranted: true <- .print("Permission granted");
							
							.broadcast(tell,instRev);
							.print("Removing relevant to_inform percepts");
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
							//.print("Action under review: ", ActAtmpt);
							.abolish(to_inform(_,ActAtmpt));	
							
							+updateEnv;
							.send(coordinator,tell,informCoordinatorComplete);
 							.
 
 
+instChangeConsensusNotGranted: true <- .print("Permission not granted");
 							//need to do something else so rework the workflow/pipeline
							//+updateEnv;
 							.						
 
+updateEnv: true <- .print("Updating the institution");
				
					
					changeInst("roomsInst.lp");
					-updateEnv;
					?updateCount(C);
					if(C==0)
					{
						
						
						
						.findall(Nm,name(Nm),Names); 
						.length(Names,Sz);
						 room_experiment.getRandomNum(0,Sz-1,Rnd);
						
						.nth(Rnd,Names,N);
						
						//?name(N);
						addAgent(N);
						
						//can I pass a list as a parameter - YES
					//	addAgent([alice,bob,jane]);
					
						.print("Adding one agent");	
						.create_agent(N, "agent.asl");
						
//						.create_agent(alice, "agent.asl");
//						.create_agent(bob, "agent.asl");
//						.create_agent(jane, "agent.asl");
//						
						.abolish(name(N));
						-+updateCount(C+1);
					}
		
					!handle;
					.

+bold(V): true <- +boldness(V).