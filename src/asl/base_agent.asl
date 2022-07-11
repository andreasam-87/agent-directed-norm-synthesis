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
					getLocations(0);
					.
					
+locations_found: true <- .print("rooms found, time to explore");

							.findall(Room,location(Room),Locations); 
							.length(Locations,Size);
							
							.print("I found ", Size, " rooms to explore");
							
							if(Size\==0)
							{
								
								for (.member(L,Locations))
				  				{
				  					 room_experiment.getRandomNum(1,2,Rnd);
						
									 +toenter(L,Rnd);
				  				}
								
							}

							!check_act;
							.

+!check_act: true <-  .print("you can enter");
					
							!enter_room;
							. 
								
							
+!enter_room: true <- .print("I am about to enter one room");
					.my_name(N);
					
					.findall([Room,Time],toenter(Room,Time),Plans); 
					.length(Plans,Size);
					
					 .print("I still need to enter ", Size, " rooms");
					
					if(Size\==0)
					{
						.nth(0,Plans,Item);
						room_experiment.getItems(Item,2,Rm,Tm);
		
		  				+room_entered(Rm);
						enter(N,Rm);
		
						-+current_action(enter(N,Rm));
		  				
		  				//.print("I will be in ", Rm, " for ",Tm, " timesteps.");
					}
					else
					{
						.print("I am finished, I can leave the building now");
					}
						

					 .
		
+perm(leave(_,_)) : roomCapacityExceeded <- ?role(P,R);
						?room_entered(Rm);
						
						?current_action(A);
					.print("I am in Room ", Rm, " and my role is ",R," but it is full, I need to leave the room");
					?overseer(O);
					
					.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));			

					!leave_now("Must Leave now, leaving room","problem");	
						.




+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);
					
					?room_entered(Rm); //fix to work with in_room belief instead.
					
					.print("I am in ",Rm," and my role is ", R, "  I will exit when ready"); 
					
					?toenter(Rm,N);
					skip_steps(N);
					.abolish(toenter(Rm,N));
					.print("Finished exploring this room");
					!leave_now("Finished, leaving now","complete");	

					.
	
	
+!leave_now(Msg,St): true <- //.print("Decided to leave, leaving now");
					.print(Msg);
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm,St); 
					-+current_action(leave(N,Rm));
					+perm(idle);
					.

						
					
	
+!idle_now: true <- .print("Decided to remain in room for now");
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
					
					
							
+restrictedAccess(Reas) :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						?room_entered(Rm);
						if(Reas==vip_room)
						{
							if(.ground(is_vip(M)))
							{
								?is_vip(V);
								.print("VIP value ", V);
								.send(O,tell,request(restrictedAccess, A, allowedAccess(vip)));
							}
							else
							{
								.send(O,tell,request(restrictedAccess, A, allowedAccess(no_vip)));
							}
							/* 
							if(.ground(is_vip(me)))
							{
								?is_vip(V);
								.print("VIP value ", V);
								.send(O,tell,request(restrictedAccess, A, allowedAccess(vip)));
							}
							else
							{
								.send(O,tell,request(restrictedAccess, A, allowedAccess(no_vip)));
							}*/
						}
						
						.
						
//+restrictedAccess(R): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
//				.	

+noAccessVIProom(Rm): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
//room_experiment.stripString(Room,Room2);
							.delete("\"",Rm,Room);
							.print("String is ",Room);
								//+noEntry(Rm);
								+noEntry(Room);
								.	
				
				
+prob(enter) :  true <- ?role(P,R);
						//.print("I cannot enter, I am role -",R);
						?current_action(A);
						//experiment5.myPrint('I cannot enter, I am role - ',R, ' I was trying to ',A);
						.print("I cannot enter, I am role - ",R, " I was trying to ",A);
						?overseer(O);
						.send(O,tell,request(prohibitedEntry, A, allowedEntry));	


								.

+prob(wave) :  true <- .print("I cannot leave").
						//experiment5.myPrint('I cannot leave').
						
+bold(V): true <- .print("My boldness is ",V);
+boldness(V). 

+overseer(O): true <- .print("My oversser is ",O);

+overseer(O). 

+revisionActive[source(Ag)]: true <- .print("The solution to my problem is currently active, I should retry my action").

+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").


+instRev[source(Ag)]: true <- .print("The institution has changed, message reveived from ", Ag);

								-instRev;
								.
					 