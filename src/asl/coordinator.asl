// Agent coordinator in project rooms

/* Initial beliefs and rules */

/* Initial goals */

!start.


/* Plans */

+!start : true <- .print("hello world. I am the coordinator").


/*Coordinating the revision implementation activity, may need to coordinate the actual revision too. */
@getCoordinatorPermission[atomic]
+getCoordinatorPermission(A,I)[source(Ag)]: not perm_requested <- .print("Received a coordinator permission request for revision consensus");
												+perm_requested;
												+active(Ag,A,I);
												.send(Ag,tell,coor_permissiongranted);
									
												//.count(getCoordinatorPermission(_,_)[source(_)],C); 
//												if(C>1)
//												{
//													.queue.create(Queue);
//												}
												.abolish(getCoordinatorPermission(A,I));
												.

@getCoordinatorPermission_a[atomic]
+getCoordinatorPermission(A,I)[source(Ag)]: perm_requested <- .print("Received a coordinator permission request for revision consensus");
										+queue(Ag,A,I);
										//.queue.add(Queue,Ag);
										.print("Added to queue");
										.abolish(getCoordinatorPermission(A,I));
										.


+informCoordinatorComplete[source(Ag)]: true <- .print(Ag," has received consensus and institution updated");
											
											.findall([A,Act,Inst],queue(A,Act,Inst),List); 
											
												//.length(Queue,L);
												.length(List,L);
												if(L>0)
												{
													.print("Checking next permission");
													//.queue.remove(Queue,Item);
													.nth(0,List,Item);
													//.print("Queue Item: ", Item);
													room_experiment.getItems(Item,3,Agn,Action,Inste);
													
													/*check if the item is already solved*/
													
													?revisionInProgress(Ag,A_R,A_A,E,Aa);
													.print('Found: ',Ag, ' ',A_R, ' ', A_A, ' ', E, ' ', Aa);
													
													?revisionInProgress(Agn,A_R1,A_A1,E1,Aa1);
													
													.print('Found1: ',Agn, ' ',A_R1, ' ', A_A1, ' ', E1, ' ', Aa1);
													room_experiment.checkSimilarRequest(A_R, A_A,E,A_R1,A_A1,E1,S);
													
													//May need to check if the solutions are similar too, rather than just the original problem
													if(S==1) //1 means they are similar
													{
														.print("Requests are not similar");
														.send(Agn,tell,coor_permissiongranted);
														
														+active(Agn,Action,Inste);
													}
													else
													{
														.print("Requests are similar");
														.send(Agn,tell,revisionSuccessful(active));
														
														//Moved these here since they only get removed if the requests are similar
														+completed(Agn,Action,Inste);
														
														+revisionCompleted(Agn,A_R1,A_A1,E1,Aa1);
														
														-revisionInProgress(Agn,A_R1,A_A1,E1,Aa1);
													}
	
													
												}
												else
												{
													-perm_requested;
													?revisionInProgress(Ag,A_R,A_A,E,Aa);
													//?active(Ag,Action2,Inste2);
													
//													-active(Ag,Action2,Inste2);
//													+revisionCompleted(Ag,A_R,A_A,E,Aa);
//													-revisionInProgress(Ag,A_R,A_A,E,Aa);
												}
												//adding new beliefs for completed activities
												?active(Ag,Action2,Inste2);
												-active(Ag,Action2,Inste2);
												+completed(Ag,Action2,Inste2);
												//+completed(Agn,Action,Inste);
												+revisionCompleted(Ag,A_R,A_A,E,Aa);
												//+revisionCompleted(Agn,A_R1,A_A1,E1,A1);
												
												//removing these beliefs that have been handled
												-queue(Agn,Action,Inste);
												-revisionInProgress(Ag,A_R,A_A,E,Aa);
												
												
												//removing this percept from the agent so I can receive another agent's percept
												.abolish(informCoordinatorComplete);
												.

+informCoordinatorHandlingRevision(ActRes,ActAtmpt,Exp,Ag)[source(Agn)]: true <- .print(Agn," has commenced handling a revision task");
																			  +revisionInProgress(Agn,ActRes,ActAtmpt,Exp,Ag);
																			  .abolish(informCoordinatorHandlingRevision(_,_,_,_));
																			  .
																			  
																			  
