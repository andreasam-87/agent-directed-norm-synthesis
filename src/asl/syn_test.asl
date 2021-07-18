// Agent syn_test in project rooms

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.");
					!idle;
					

					
					.

+!idle: true <- .print("I am waiting a couple of seconds");
				.wait(6000);
				!messageCoordinator;
				.		
						
+!messageCoordinator: true <- .print('Messaging the coordinator');
								 .send(coordinator,tell,informCoordinatorHandlingRevision(prohibitedEntry,enter(agent00,"room1"),in_room,agent00));
													//.send(LP,tell,cfp(Id,Object)); 
					.concat("+!signalComplete",Event);
// the deadline of the CNP is now + 4 seconds, so
// the event +!contract(Id) is generated at that time 
					.at("now +6 seconds", Event);
								.

-!messageCoordinator: true <- .print('Error somewhere with future event trigger');	
.						

+!signalComplete: true <- .print('Signalling complete');
							.wait(6000);
							.send(coordinator,tell,getCoordinatorPermission(enter(agent00,"room1"),"roomsInst.lp"));
//							?handlingCur(ActRes,ActAtmpt,Exp,Ag);
						.
						

+coor_permissiongranted[source(Ag)]: true <- .print("Received permission from ",Ag ," to begin discussion");
											
											.wait(2000);
											.send(coordinator,tell,informCoordinatorComplete);
											.print('Done!!!!');
											. 	

@seekInstChangeConsensus[atomic]
+seekInstChangeConsensus(Action,NewInst)[source(Ag)]: true <- .print(Ag, " is seeking consensus to change the institution"); 
																
															//	.send(Ag,tell,instChangeConsensusGranted);
																. 				
						