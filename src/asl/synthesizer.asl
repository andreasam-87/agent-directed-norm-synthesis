// Agent supervisor in project experiment5

/* Initial beliefs and rules */
entered(0).
what(0).
name(bob).
name(alice).
name(jo).
name(jane).

updateCount(0).
count(0).
synCount(0).

accept(1).
refuse(0).
consensus(1).
min_vote(1).
received(0).

/* Initial goals */



!start.



/* Plans */

+!start : true <- .print("hello world.").
				 //experiment5.myPrint('hello world'). 

+stateOccurred(Ev,WhenEv) : true <- .print("Event: ",Ev, " occurred at state: ",WhenEv);
									. 


@request[atomic]
+request(restrictedAccess, ActAtmpt,allowedAccess(no_vip))[source(Ag)] : true <- 
													.print("This is a restricted access request");
											    	//.send(Ag,askOne,is_vip(me),Response);
											    	//.print("Response is ",Response);
											    //	if(Response==false)
											    //	{
											    	room_experiment.getRoom(ActAtmpt,Room);
											    	room_experiment.stripString(Room,Room2);
											    		.send(Ag,tell,noAccessVIProom(Room2));
											    		//-handling;
											    //	}
											    .abolish(request(restrictedAccess,ActAtmpt,allowedAccess(no_vip))[source(Ag)]);
																.
 
@request_b[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : not handling <- 
										   
										    +handling;
										    
										    .print("Request received from ",Ag," will handle");
										    
									/* 	    if(ActRes==restrictedAccess)
										    {
										    	.print("This is a restricted access request");
										    	.send(Ag,askOne,is_vip(me),Response);
										    	.print("Response is ",Response);
										    	if(Response==false)
										    	{
										    		.send(Ag,tell,restrictedAccess(vip_room));
										    		//-handling;
										    	}
										    }
										    */
										    ?entered(Ti);
											-+entered(Ti+1);
											//.print("Response is outside of block",Response);
											//if(Response\==false)
											//{
												+handlingCur(ActRes,ActAtmpt,Exp,Ag);
											    .send(coordinator,tell,informCoordinatorHandlingRevision(ActRes,ActAtmpt,Exp,Ag));
											    +to_inform(Ag,ActAtmpt);
											    .abolish(request(ActRes,ActAtmpt,Exp)[source(Ag)]);
											    
	
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
											//}
										    
										    
											
										.

							//			. 
							
+pause[source(Ag)]: true <- .print(Ag, " asked me to wait");
							//-pause[source(Ag)].
							.

@request_c[atomic]
+request(ActRes, ActAtmpt,Exp)[source(Ag)] : handling <- .print('Queued request for ',Ag);
					
										
									/* 	if(ActRest==restrictedAccess)
									    {
									    	.send(Ag,askOne,is_vip(me),Response);
									    	.print("Response is ",Response);
									    	if(Response==false)
									    	{
									    		.send(Ag,tell,restrictedAccess(vip_room));
									    	}
									    }
										.print("Response is outside of block ",Response);
										if(Response\==false)
										{*/
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
											
										//}	
										
	
										?entered(T);
										-+entered(T+1);	

										. 



//@eventOccurred[atomic]
+eventOccurred(When): true <- .print("Attempting to revise norms");
							
							?what(D);
							.print("What is happening - ", D);
							-+what(D+1);
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
                           revise(When,3,ActAtmpt,ActRes,Exp);
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
							updateState(ActAtmpt);
																				
						!handle;
							.

/* +revisionSuccess: true <- .print("A possble revision found, inform agents of change");
						
							-revisionSuccess;
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
							.send(coordinator,tell,getCoordinatorPermission(ActAtmpt,"roomsInst.lp"));
							
							.*/
		
+revisionSuccess: true <-  .print("The revision was successful, let me begin the discussion");
							-revisionSuccess;
							!discussRevision;
										.	
										
+revisionFile(File): true <- 	+fileFromRevision(File).
		
+!discussRevision: true <- 	.print("A possble revision found, inform agents of change");
//+revisionSuccess: revisionFile(File) <- .print("A possble revision found, inform agents of change");
						
							//-revisionSuccess;
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
							?fileFromRevision(File);
							.send(coordinator,tell,getCoordinatorPermission(ActAtmpt,File));
							
							//.send(coordinator,tell,getCoordinatorPermission(ActAtmpt,"roomsInst.lp"));
							.
							
+revisionLog(RFile): true <- .print("Log file received");
							+logForOracle(RFile).
													
+coor_permissiongranted[source(Agn)]: true <- .print("Received permission from ",Agn ," to begin discussion");
											
											?handlingCur(ActRes,ActAtmpt,Exp,Ag);

											?fileFromRevision(F);
											.all_names(Names);
											.my_name(Me);
											for (.member(N,Names))
											{
												//.substring("a","bbacc",0): false. When the third argument is 0, 
												//.substring works like a java startsWith method.
												
												if(.substring("syn",N,0) & not (Me==N))
												{
													.send(N,tell,seekInstChangeConsensus(ActAtmpt,F,ActRest,Exp));
				
													//.send(N,tell,seekInstChangeConsensus(ActAtmpt,"roomsInst.lp"));
				
													// .concat("request(",ActRes,",",ActAtmpt,",",Exp,")",R) 					
												}
													  				
											}
							
											
											
											. 

@seekInstChangeConsensus[atomic]
+seekInstChangeConsensus(Action,NewInst,Avoid,Prefer)[source(Ag)]: true <- .print(Ag, " is seeking consensus to change the institution"); 
																.print("Using sense to see if this is an acceptable change"); 
																+checkingFirst;
																//suspend;
																//+sense_time(Action,NewInst);
																+giveResponseTo(Ag);
																.print("about to try to sense the institution");
																room_experiment.getRoom(Action,R1);
																//.print("Which room --> ",R1); 
																 .concat("enter(sample,",R1,")",Act);
																 //.print("After Concat --> ",Msg); 
																 room_experiment.stripString(Act,Act1);
																 .print("Why am I stuck here ---> ",Act);
																sense(Act1,NewInst,Avoid,Prefer);
																
																. 

													
+revisionFailed: true <- .print("The revision task failed");
						-revisionFailed;
						!discussFailedRevision;
						.

+!discussFailedRevision: true <- 	.print("No possible revisions found");
						//.abolish(revisionFailed);
						//-revisionFailed;
						?handlingCur(ActRes,ActAtmpt,Exp,Ag);
						//.findall([W,ActAtmpt],to_inform(W,ActAtmpt),List1); 
						.findall(W,to_inform(W,ActAtmpt),List1); 
						
						.length(List1,Sizer)
						.print("I have to inform ", Sizer, " agents that there is no possible revisions");	
								

		  				
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
						
						//July 20, 2022 -is this where it is going wrong, can I possibly be doing this too early
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
  						
  						.abolish(handlingCur(_,_,_,_));
  						+handlingCur(I1,I2,I3,I4);
  						
  						
  						//-+handlingCur(I1,I2,I3,I4);
  						
  						.abolish(to_handle(I1,I2,I3,I4));
  						!handle_reqs(I1,I2,I3,I4);

  					}
  					.
  					
+!handle_reqs(H,I,J,K): true <- .print("handling next request");
						checkState(I);
						.

/* +sense_time: true <- !do_sense;
				
					-sense_time;
					.
						
+!do_sense: true <- .print("about to try to sense the institution");
					sense("enter(sample,room1)");
					.*/


+revisionAcceptable: true <- .print("I can accept the revision");
							 ?giveResponseTo(Ag);
							 .send(Ag,tell,instChangeConsensusGranted);
							 // .send(Ag,tell,instChangeConsensusRequest(Granted));
							//resume;
							 .
					
+revisionUnacceptable: true <- .print("I cannot accept the revision");
					?giveResponseTo(Ag);
					.send(Ag,tell,instChangeConsensusNotGranted);
					//.send(Ag,tell,instChangeConsensusRequest(NotGranted));
					.
					
+deniedEntry[source(Ag)] : true <-  .print("Message received from ",Ag,", will handle").



//need to count and ensure all the permission is received, or max required permission given	

+instChangeConsensusRequest(Granted)[source(Ag)]: true <- .print("Permission granted");
										?accept(N);
										-+accept(N+1);
										
										//?received(C);
										//-+received(C+1);
										.
										
+instChangeConsensusRequest(NotGranted)[source(Ag)]:true <- .print("Permission not granted");
										?refuse(N);
										-+refuse(N+1);
										//?received(C);
										//-+received(C+1);
	
										.

countVotes(C)  :- refuse(R) & accept(A) & C = R + A.	

enoughVotes  :- countVotes(CO) &  min_vote(MV) & CO >=MV.	

+consensusMet  : enoughVotes &  accept(A) & consensus(C) & A >= C <- !completeRevision.	

+consensusNotMet  : enoughVotes &  accept(A) & consensus(C) & A < C <- !completeRevision.	
       

//countVotes(C)
  //  :- refuse(R) & accept(A) & min_vote(MV) &
    //  C = R + A.										.
	
//+determineConsensus : accept(A) & received(R) & consensus(C) & min_vote(MV) &  A>=C  <- .count(introduction(participant,_),NP); //&
     //and accept(N2) and consensus(N3)
      // .count(propose(CNPId,_), NO) &
       //.count(refuse(CNPId), NR) &
  //     NP = NO + NR;
    //   .
	
	//-+accept(0);
	//				-+refuses(0);										
//

+completeRevision: consensusMet <- .print("TESTING::: Permission granted");
							
							/* .print("Permission granted");
							 * .broadcast(tell,instRev);
							.print("Removing relevant to_inform percepts");
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
							//.print("Action under review: ", ActAtmpt);
							.abolish(to_inform(_,ActAtmpt));	
							
							+updateEnv;
							.send(coordinator,tell,informCoordinatorComplete);*/
 							.


+completeRevision: consensusNotMet <- .print("TESTING::: Permission not granted");
 							.

+instChangeConsensusGranted: true <- .print("Permission granted");
							?fileFromRevision(F);
							?logForOracle(RF);
							
							.send(oracle,tell,decideRevision(F,RF));
							//decideRevision();
							
							/* .broadcast(tell,instRev);
							.print("Removing relevant to_inform percepts");
							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
							//.print("Action under review: ", ActAtmpt);
							.abolish(to_inform(_,ActAtmpt));	
							
							+updateEnv;
							.send(coordinator,tell,informCoordinatorComplete);*/
 							.


+instChangeConsensusNotGranted: true <- .print("Permission not granted");
 							//need to do something else so rework the workflow/pipeline
							//+updateEnv;
							!discussFailedRevision;
 							.		

+oraclePermissionGranted(NewInst): true <- .print("Finally, the institution can be revised");
											
											+updateInEnv(NewInst);
											.send(coordinator,tell,informCoordinatorComplete);
											
											.
											

+oraclePermissionNotGranted(NewInst): true <- .print("Nooo, the institution cannot be revised at this time");
											
											
										/* 	.broadcast(tell,instRev);
											
											.print("Removing relevant to_inform percepts");
											?handlingCur(ActRes,ActAtmpt,Exp,Ag);
											.abolish(to_inform(_,ActAtmpt));	
											
											+updateInEnv(NewInst);*/
											
											!discussFailedRevision;
											.send(coordinator,tell,informCoordinatorComplete);
											
											!handle;
											.
 
				
 
 +updateInEnv(NewInst): true <- .print("I'm about to update the institution");
 								//updateInst(NewInst);
 								
 								.broadcast(tell,instRev);
											
								.print("Informing relevant agents that a solution was found");
								?handlingCur(ActRes,ActAtmpt,Exp,Ag);
								
								.findall(W,to_inform(W,ActAtmpt),List); 
				  				
				  				.send(List,tell,revisionSucceeded);
								
								.print("Removing relevant to_inform percepts");
								.abolish(to_inform(_,ActAtmpt));	
 								
 								changeInst(NewInst,ActAtmpt);
 								!handle;
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
						//a
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
		
					-+accept(0);
					-+refuses(0);
					!handle;
					.

+bold(V): true <- +boldness(V).