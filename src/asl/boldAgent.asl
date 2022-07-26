// Agent agent in project room_experiment

/* Initial beliefs and rules */

/* Initial goals */

!initialise_logger_and_start.

//!start.

/* Plans */

					 

+!initialise_logger_and_start : true  <- //jason_logger.initialise; 
										!start.

+!start : true <-  .print("hello world.");
					//experiment5.myPrint('hello world');
					!explore.
					
+!explore: true <- .print("find out which rooms are available");
					getLocations;
					.
					
+locations_found: true <- .print("rooms found, time to explore");

							.findall(Rooms,location(Rooms),Locations); 
							.length(Locations,Size);
							
							.print("There are ", Size, " rooms to explore");
							
							
							if(Size\==0)
							{
								room_experiment.chooseRoom(Locations,C);
								.nth(C,Locations,Room);
								.print("I choose to enter ",Room);
								
								 room_experiment.getRandomNum(1,2,Rnd);
						
								 +toenter(Room,Rnd);
								 !enter_room;
								
								/*for (.member(L,Locations))
				  				{
				  					 room_experiment.getRandomNum(1,2,Rnd);
						
									 +toenter(L,Rnd);
				  				}
								*/
							}
							else
							{
								.print("There are no rooms to explore, leaving...");
							}

							//!check_act;
							.

+!check_act: true <-  .print("you can enter");
					//		experiment5.myPrint('you can enter');
							!enter_room.
							//. 
								
							

					 
+!enter_room: true <- .print("I am about to enter a room");
					.my_name(N);
					
					?toenter(Room,Time);
						-+room_entered(Room);
						enter(N,Room);
		
						-+current_action(enter(N,Room));
						.
					
					/*if(.ground(toenter(R,T)))
					{
						?toenter(Room,Time);
						-+room_entered(Room);
						enter(N,Room);
		
						-+current_action(enter(N,Room));
						
					}
					else
					{
						.print("I am finished, I can leave the building now");
					}
					.*/
					/* 
					.findall([Room,Time],toenter(Room,Time),Plans); 
					.length(Plans,Size);
					
					 .print("I still need to enter ", Size, " rooms");
					
					if(Size\==0)
					{
						.nth(0,Plans,Item);
						room_experiment.getItems(Item,2,Rm,Tm);
		
		  				-+room_entered(Rm);
						enter(N,Rm);
		
						-+current_action(enter(N,Rm));
		  				
		  				//.print("I will be in ", Rm, " for ",Tm, " timesteps.");
					}
					else
					{
						.print("I am finished, I can leave the building now");
					}
						*/
					 
					
  
 +!retry_enterroom: true <- .print("I am about to try to enter the room again");
					.my_name(N);
					?room_entered(Rm);
					
					//-+room_entered(Rm); //entering same room so not necessary. 
					enter(N,Rm);
		
					-+current_action(enter(N,Rm)); //same action so probably not necessary. 
					//enter(N,Rm);
					
					 .
		
+perm(leave(_,_)) : roomCapacityExceeded <- ?role(P,R);
						?room_entered(Rm);

						?toenter(Rm,N);
						
						?current_action(A);
						.print("I am in Room ", Rm, " and my role is ",R," but it is full, I may need to leave the room");
						?boldness(B);
						if((B mod 2)==0)
						{
							?overseer(O);
						
							.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));	
							
							.print("I am not happy about this, complaining to synthesiser");
							+conflict;
							!leave_now;		
						}
						else
						{
							.print("I am not happy about this, but I will leave after ", N, " timesteps");
							//.print("I am in ",Rm," and my role is ", R, ",  I will leave after ", N, " timesteps"); 
						
							
							for ( .range(I,1,N) ) 
							{
								//.print("Repeating ",I);
						          explore; 
						    }
						    
						    .abolish(toenter(Rm,N));
							.print("Finished exploring this room");
							!leave_now;	
						}
							
						.

/* 
+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);

					?room_entered(Rm); //fix to work with in_room belief instead.
					
					.print("I am in ",Rm," and my role is ', R, '  I will exit when ready"); 
					.my_name(N);
					
					.random(Rd);
					//.print('random: ',Rd);
					
					if (Rd>0.5)
					{
					 	!leave_now;				
					}
					else
					{
						!idle_now;
					}								
											
					//leave(N,Rm); 
				//	 -+current_action(leave(N,RM));
					.
	*/
	
+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);
					
					?room_entered(Rm); 
					
					
					?toenter(Rm,N);
					
					.print("I am in ",Rm," and my role is ", R, ",  I will leave after ", N, " timesteps"); 
					
					//skip_steps(N);
					for ( .range(I,1,N) ) 
					{
						.print("Repeating ",I);
				          explore; 
				    }
					
					.abolish(toenter(Rm,N));
					.print("Finished exploring this room");
				//	!leave_now("Finished, leaving now","complete","idle");	
					!leave_now;	

					.
	
+!leave_now: not conflict <- .print("Finished, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm,"complete"); 
					-+current_action(leave(N,Rm));
					+goal_completed;
					.	
					
	
	
+!leave_now: conflict <- .print("Must leave now, leaving room");

//!leave_now("Must Leave now, leaving room","problem","noidle");
					?room_entered(Rm);
					.my_name(N);
					+waiting;
					-conflict;
					leave(N,Rm,"problem"); 
					-+current_action(leave(N,Rm));
					.print("I will wait for word before acting again");
					
					//+perm(idle);
					.	
					

+goal_completed: true <- .print("I have accomplished my goal");
					.	
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
				
				!enter_room;
				.	
				
-!idle_now: true <- .print("Error likely with the delay function, continuing my plan");
					!enter_room;
					.							
							
+perm(idle)	: true <- .print("I left so I am chillin");
						//.wait(2000);
						.random(Rnd);
					//.print('random: ',Rd);
						if (Rnd>0.5)
						{
						 	delay(2000);				
						}
						else
						{
							delay(3000);
						}
						//t=Rnd*1000;		
						
						//skip_steps(2);
						-perm(idle);
						!enter_room;
						.
					//	experiment5.myPrint('I left so I am chillin').
							
/* +restrictedAccess :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						?room_entered(Rm);
						.send(O,tell,request(restrictedAccess, A, allowedAccess(Rm)));
						
						.
						
+restrictedAccess(R): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
						.	*/


+restrictedAccess(Reas) :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						?room_entered(Rm);
						if(Reas==vip_room)
						{
							//?is_vip(Me);
							if(.ground(is_vip(me)))
							{
								.send(O,tell,request(restrictedAccess, A, allowedAccess(vip)));
							}
							else
							{
								.send(O,tell,request(restrictedAccess, A, allowedAccess(no_vip)));
							}
						}
						
						
						.
						
						
+noAccessVIProom(Rm): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
								+noEntry(Rm);
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

//+revisionActive: true <- .print("The solution to my problem is currently active, I should retry my action").

+revisionActive[source(Ag)]: true <- .print("The solution to my problem is currently active,");
									-revisionActive;
									// .abolish(revisionActive);
									!retry_enterroom;
									. 

//+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").


/*+instRev[source(Ag)]: not conflict <- .print("The institution has changed, message reveived from ", Ag);

								-instRev;
								.
+instRev[source(Ag)]: conflict <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
									skip_steps(2);
									-conflict;
									!enter_room;
									. */

+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.");
									//attempt to enter another room OR remove plan to enter this room. 
									
									/* //Temp action to remove plan to enter room  
									 	?room_entered(Rm);
									  ?cancelled_plans(C);
									  +cancelled_plans(C+1);
									  -toenter(Rm,_);
									  *
									  */
									  
									 -revisionFailed;
									// .abolish(revisionFailed);
									 
									 !enter_room;
									 .

+revisionSucceeded[source(Ag)]: true <- .print("My problem has a solution, message reveived from ", Ag);
								-revisionSucceeded;
								//may need to include a delay
								skip_steps(2);
								!retry_enterroom;
								.


+instRev[source(Ag)]: true <- .print("The institution has changed, message reveived from ", Ag);
								-instRev;
			
								.
