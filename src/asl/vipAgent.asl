// Agent agent in project room_experiment

/* Initial beliefs and rules */
toenter(vip_room).
cancelled_plans(0).
failed_plans(0).

/* Initial goals */

!initialise_logger_and_start.

//!start.

/* Plans */

					 

+!initialise_logger_and_start : true  <- //jason_logger.initialise; 
										!start.

+!start : true <-  .print("hello world. I am a VIP agent");
					skip_steps(2);  //NEED TO WAIT LONGER or check something else in the environment
					//delay(2000);	
					.print("Finished skipping steps");
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
								.print("I choose to enter ",Room);
								
								 room_experiment.getRandomNum(1,2,Rnd);
						
								 +toenter(Room,Rnd);
								 +toexplore(1);
								 !enter_vip_room;
								 
								

							}
							else
							{
								.print("There are no rooms to explore, leaving...");
							}

							//!check_act;
							.

+!enter_vip_room: true <- .print("I am trying to enter the VIP now");
					
					.my_name(N);
					
					?toenter(Room,Time);
					
					-+room_entered(Room);
									
					enter(N,Room);
					
					-+current_action(enter(N,Room));

				 .
				 
/* 				 
+!enter_vip_room(Rm): true <- .print("I am trying to enter the VIP now");
					
					.my_name(N);
					
					room_experiment.getRandomNum(1,2,Rnd);
					
					+toenter(Rm,Rnd);
					+room_entered(Rm);
					+toexplore(1);
					
					enter(N,Rm);
					
					-+current_action(enter(N,Rm));
				 .
			 			
													
+!enter_room: true <- .print("I tried to enter");
					//experiment5.myPrint('I tried to enter');
					.my_name(N);
					
					.findall([Room,Time],toenter(Room,Time),Plans); 
					.length(Plans,Size);
					
					 .print("I still need to enter ", Size, " rooms");
					//may need to rethink using a loop, just check the first item, or check any item using the question mark
					if(Size\==0)
					{
						.nth(0,Plans,Item);
						room_experiment.getItems(Item,2,Rm,Tm);
		
		  				+room_entered(Rm);
						enter(N,Rm);
		
						-+current_action(enter(N,Rm));
		  					
							//.substring(Tm,P,6,7); //: true and R unifies with "20".
		  				.print("I will be in ", Rm, " for ",Tm, " timesteps.");
					}
					else
					{
						.print("I am finished, I can leave the building now");
					}
					
			 .*/
		/* 	 

+in_room(_,_) : true <- ?role(P,R);
						?room_entered(Rm);
						
						?current_action(A);
						.print("I am in Room ", Rm, " and my role is ",R," but it is full, I may need to leave the room");
					
						?boldness(B);
						.
						
						if((B mod 2)==0)
						{
							?overseer(O);
							.print("I am not happy about this, complaining to synthesiser");
							//.send(O,tell,request(roomCapacityExceededViol, A, noViol(holdsat(meeting))));	
							
							.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));	
							
							//.send(synthesizer,tell,request(roomCapacityExceededViol, A, noViol));	
							+conflict;
							!leave_now;		
						}
						else
						{
							.print("I am not happy about this, but I am staying in the room");
							?toenter(Rm,N);
							skip_steps(N);
							.abolish(toenter(Rm,N));
							!leave_now;	
						}
						 .
						*/
						
						
		
/* +restrictedAccess :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						.send(O,tell,request(restrictedAccess, A, allowedAccess(vip)));
						
						.*/

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
							
								
/* +perm(leave(_,_)) : roomCapacityExceeded <- ?role(P,R);
						?room_entered(Rm);
						
						?current_action(A);
					.print("I am in Room ", Rm, " and my role is ",R," but it is full, I may need to leave the room");
					
					?boldness(B);
					if((B mod 2)==0)
					{
						?overseer(O);
						.print("I am not happy about this, complaining to synthesiser");
						//.send(O,tell,request(roomCapacityExceededViol, A, noViol(holdsat(meeting))));	
						
						.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));	
						
						//.send(synthesizer,tell,request(roomCapacityExceededViol, A, noViol));	
						+conflict;
						!leave_now;		
					}
					else
					{
						.print("I am not happy about this, but I am staying in the room");
						?toenter(Rm,N);
						skip_steps(N);
						.abolish(toenter(Rm,N));
						!leave_now;	
					}
						
					//	.print(request(roomCapacityExceededViol, A, noViol));
					
					//TO DO -- include the logic to have them decide to move 
						.


+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);
					
					?room_entered(Rm); //fix to work with in_room belief instead.
					
					.print("I am in ",Rm," and my role is ", R, "  I will exit when ready"); 
					
					?toenter(Rm,N);
					skip_steps(N);
					.abolish(toenter(Rm,N));
					!leave_now;	
					
					. 
					* */
					

/* +!leave_now: true <- .print("Decided to leave, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm); 
					-+current_action(leave(N,Rm));
					+perm(idle);
					.	*/


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

+revisionActive: true <- .print("The solution to my problem is currently active, I should retry my action");
							skip_steps(2);
							!enter_vip_room;
							.

//+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").

+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.");
									//attempt to enter another room OR remove plan to enter this room. 
									
									//Temp action to remove plan to enter room  
									 ?room_entered(Rm);
									  ?cancelled_plans(C);
									  -+cancelled_plans(C+1);
									  -toenter(Rm,_);
									 
									  
									 -revisionFailed[source(Ag)];
									// .abolish(revisionFailed);
									 
									// !enter_room;
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
