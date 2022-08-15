// Agent agent in project room_experiment

/* Initial beliefs and rules */
toenter(vip_room).
cancelled_plans(0).
failed_plans(0).
entry_problems(0).

/* Initial goals */

!initialise_logger_and_start.

//!start.

/* Plans */

					 

+!initialise_logger_and_start : true  <- //jason_logger.initialise; 
										!start.

+!start : true <-  .print("hello world. I am a VIP agent");
					//skip_steps(2);  //NEED TO WAIT LONGER or check something else in the environment
				
					!check_vip_room.

//+!check_act: true <-  .print("you can enter");
					//		experiment5.myPrint('you can enter');
//							!enter_room.
							//. 

+!check_vip_room: true <-  .print("Checking for VIP rooms to enter");
							?toenter(What);
							checkVipRoom(What);
							//!enter_room.
							. 								

/* 
+vipRoomFound(R): true <- .print("Entering VIP room");
						!enter_vip_room(R);
						.*/


+vipRoomsFound: true <- .print("rooms found, time to explore");

							.findall(Rooms,viproom(Rooms),Locations); 
							.length(Locations,Size);
							
							.print("There are ", Size, " rooms to explore");
							
							
							if(Size\==0)
							{
								room_experiment.chooseRoom(Locations,C);
								.nth(C,Locations,Room);
								//.print("I choose to enter ",Room);
								
								 room_experiment.getRandomNum(1,2,Rnd);
						
								 +toenter(Room,Rnd);
								 +toexplore(1);
								// !enter_vip_room;
								 
								.print("I have rooms to explore but the place is crowded so I will wait a bit");

							}
							else
							{
								.print("There are no rooms to explore, leaving...");
							}

							//!check_act;
							.

+baton_handover: true <-  .print("People have left, let me explore");
					.abolish(baton_handover);
					!enter_vip_room;
					.
					
					
+!enter_vip_room: true <- .print("I am trying to enter the VIP now");
					
					.my_name(N);
					
					?toenter(Room,Time);
					
					-+room_entered(Room);
									
					enter(N,Room);
					
					-+current_action(enter(N,Room));

				 .
				 


+perm(leave(_,_)) :   not roomCapacityExceeded & not typeConflictInRoom <- ?role(P,R);
					
					?room_entered(Rm); //fix to work with in_room belief instead.
					
					
					?toenter(Rm,N);
					
					.print("I am in ",Rm," and my role is ", R, ",  I will leave after ", N, " timesteps"); 
					
					//skip_steps(N);
					for ( .range(I,1,N) ) 
					{
						.print("Repeating ",I);
				          explore; 
				    }
					
					//explore;
					?toexplore(C);
					-+toexplore(C-1);
					.abolish(toenter(Rm,N));
					.print("Finished exploring this room");
					!leave_now("Finished, leaving now","complete","idle");	

					.								

+perm(leave(_,_)) : typeConflictInRoom <- ?role(P,R);
						.my_name(N);
						?room_entered(Rm);
						
						?current_action(A);
					
					.print("I am in Room ", Rm, " and my role is ",R," but there is type conflict violation");
					.print("I will leave the room and re-enter when it is resolved")
					//.print("I am the first agent in the room, there is an earlybird violation");
					
					 ?overseer(O);
					//typeConflict
					.send(O,tell,request(roomTypeConflictViol, A, noViol(vip)));	
					?failed_plans(C);
					-+failed_plans(C+1);
					+failedTo(enter,N,Rm,A);	
			
					
					!leave_now("Must leave now, leaving room","problem","noidle");	
						.
							
+restrictedAccess(Reas) :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						
						.send(O,tell,request(restrictedAccess, A, allowedAccess(vip)));
						.	
							
+deniedEntry(Reas) :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						
						.send(O,tell,request(deniedEntry, A, allowedAccess(vip)));
						-deniedEntry(Reas)[source(percept)]; 
						.	
								



+!leave_now(Msg,St,Todo): true <- //.print("Decided to leave, leaving now");
					.print(Msg);
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm,St); 
					-+current_action(leave(N,Rm));
					if(Todo=="idle")
					{
						//+perm(idle);
						!idle_now;
					}
					else
					{
						.print("I will wait for word before acting again");
						+waiting;
						//!checkIdle;
					}		
					.	
//+!leave_now: not conflict <- .print("Decided to leave, leaving now");
//					?room_entered(Rm);
//					.my_name(N);
//					leave(N,Rm); 
//					-+current_action(leave(N,Rm));
//					+perm(idle);
//					.	
//					
//	
//	
//+!leave_now: conflict <- .print("Decided to leave, leaving now");
//					?room_entered(Rm);
//					.my_name(N);
//					leave(N,Rm); 
//					-+current_action(leave(N,Rm));
//					//+perm(idle);
//					.	
					
	
					
+!idle_now: true <- .print("I left so I am chillin");
				.random(Rnd);
				if (Rnd>0.5)
				{
				 	delay(2000);				
				}
				else
				{
					delay(3000);
				}
				+goal_completed;
				//!enter_room;
				.	
				
-!idle_now: true <- .print("Error likely with the delay function, continuing my plan");
					+goal_completed;
					//!enter_room;
					.				
							

							

+prob(enter) :  true <- ?role(Nm,R);
						//.print("I cannot enter, I am role -",R);
						?current_action(A);
						//experiment5.myPrint('I cannot enter, I am role - ',R, ' I was trying to ',A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						//.send(supervisor,tell,deniedEntry);
						
						//an appropriate send template
					//	.send(synthesizer,tell,request(prob(enter), A, perm(leave)));
						
						
						//request(ActRes,ActAtmpt,Exp)
						//.send(supervisor,tell,request(prob(enter),enter(R) --> A, perm(enter)))
						//1-unhappy with, 2- justification - what I was doing , 3- norm or expectation(maybe part of 3) 
						//3 cont'd - probably in_room expected
						
				//		.print("Awaiting directive");
				
						//experiment5.myPrint(' Awaiting directive').
								//setpermenter;
								//.print("I want to enter");
								//!check_act.
								.

+prob(wave) :  true <- .print("I cannot leave").
						//experiment5.myPrint('I cannot leave').


+bold(V): true <- +boldness(V). 


+overseer(O): true <- +overseer(O). 

+goal_completed: true <- .print("I have accomplished my goal");
						.

+revisionActive: true <- .print("The solution to my problem is currently active, I should retry my action");
							skip_steps(2);
							!enter_vip_room;
							.

//+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").

+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.");
									//attempt to enter another room OR remove plan to enter this room. 
									.print("I really want to see the exhibit so I will retry my action.");
									
									//Temp action to remove plan to enter room  
									// ?room_entered(Rm);
									 ?entry_problems(C);
									  -+entry_problems(C+1);
									  //-toenter(Rm,_);
									 
									  
									 -revisionFailed[source(Ag)];
									// .abolish(revisionFailed);
									 
									// !enter_room;
									if(C<=2)
									{
										!enter_vip_room;
									}
									else
									{
										?cancelled_plans(CP);
										-+cancelled_plans(CP+1);
										.print("I have tried entering twice with no luck and no solution to my problem, I am leaving now");
									}
									
									 .

+revisionSucceeded[source(Ag)]: true <- .print("My problem has a solution, message reveived from ", Ag);
								-revisionSucceeded;
								//may need to include a delay
								skip_steps(2);
								!enter_vip_room;
								.


+instRev[source(Ag)]: true <- .print("The institution has changed, message reveived from ", Ag);
								-instRev;
			
								.

/* 
+instRev[source(Ag)]: not waiting <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
										.

+instRev[source(Ag)]: waiting <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
									skip_steps(2);
									-waiting;
									!enter_vip_room;
									.*/

