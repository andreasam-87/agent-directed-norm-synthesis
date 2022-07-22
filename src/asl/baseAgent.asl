// Agent agent in project room_experiment

/* Initial beliefs and rules */
cancelled_plans(0).
failed_plans(0).

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

							.findall(Room,location(Room),Locations); 
							.length(Locations,Size);
							
							.print("I found ", Size, " rooms to explore");
							+toexplore(Size);
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
						//.nth(0,Plans,Item);
						
						//randomly select rather than always choosing the first one. 
						room_experiment.chooseRoom(Plans,C);
						.nth(C,Plans,Item);
						//.print("I choose to enter ",Room);
						
						
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
						
					 .
					 
+!retry_enterroom: true <- .print("I am about to try to enter the room again");
					.my_name(N);
					?room_entered(Rm);
					
					
					.findall(Room,toenter(Rm,Time),Plans); 
					.length(Plans,Size);
					
					// .print("I still need to enter ", Size, " rooms");
					
					if(Size\==0)
					{
						enter(N,Rm);
						
						-+current_action(enter(N,Rm));
					}
					else
					{
						!enter_room;
					}
					//-+room_entered(Rm); //entering same room so not necessary. 
					
		 //same action so probably not necessary. 
					//enter(N,Rm);
					
					 .
		
+perm(leave(_,_)) : roomCapacityExceeded <- ?role(P,R);
						.my_name(N);
						?room_entered(Rm);
						
						?current_action(A);
					.print("I am in Room ", Rm, " and my role is ",R," but it is full, I need to leave the room");
					?overseer(O);
					
					.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));	
					?failed_plans(C);
					-+failed_plans(C+1);
					+failedTo(enter,N,Rm,A);	

					
					!leave_now("Must leave now, leaving room","problem","noidle");	
						.




+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);
					
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
	
	
+!leave_now(Msg,St,Todo): true <- //.print("Decided to leave, leaving now");
					.print(Msg);
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm,St); 
					-+current_action(leave(N,Rm));
					if(Todo=="idle")
					{
						+perm(idle);
					}
					else
					{
						//.print("I will wait for word before acting again");
						//+waiting;
						!checkIdle;
					}		
					.

						
					
	
+!idle_now: true <- .print("Decided to remain in room for now");
					.						


					 
+!enterNext(Rm): true <- .print("I am about to try to enter another room");
					.my_name(N);
					
					-+room_entered(Rm); 
					enter(N,Rm);
		
					-+current_action(enter(N,Rm)); 
				
					 .					

+!checkIdle: true <- .print("Need to decide if to wait or try to explore another room");
					
					?failed_plans(FpC);
					?toexplore(TEx);
					
					if(FpC<TEx)
					{
						//+failedTo(enter,N,Rm,A);	
						
						
						//navigate the each room to be entered
						
						.findall(Rom,toenter(Rom,Time),Plans); 
						.length(Plans,Size);
						
						if(Size\==0)
						{
							for(.member(Room,Plans))
							{
								//navigate the rooms failed to explore
								
								.findall(Rm,failedTo(X,Y,Rm,A),F_Plans);
								.length(F_Plans,Size1);
								
								//navigate them
								for ( .range(I,0,(Size1-1)) )
								{        
							          .nth(I,F_Plans,Item);
							          if(Item==Room)
							          {	
							          	+found;
							          }    
							          
							    }
							    if (not found)
							    {
							    	-+next(Room);	
							    	
							    }
							    -found;
							}
							?next(R);
							!enterNext(R);
						}
						
						/* 
						//get all failed plans
						.findall(Rm,failedTo(X,Y,Rm,A),F_Plans);
						.length(F_Plans,Size1);
						//navigate them
						for ( .range(I,1,Size1) ) 
						{        
					          .nth(I,F_Plans,Item);
					          
					          .findall(Rom,toenter(Rom,Time),Plans); 
							  .length(Plans,Size);
					    }
						
							.findall(Rom,toenter(Rom,Time),Plans); 
								.length(Plans,Size);
								
								if(Size\==0)
								{
									for(.member(Room,Plans))
									{
										room_experiment.sameRoom(Rm,Room,Ret);
										if(Ret==0)
										{
											//remove the plan to enter this room
											-toenter(Room,_);
											
											?cancelled_plans(C);
								  			+cancelled_plans(C+1);
								  			+noEntry(Room);
								
										}
									}
								}
								* 
								*/
					
					}
					else
					{
						+waiting;
						.print("I will wait for word before acting again");
					}
					
				
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


+noAccessVIProom(Rm) : true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
								.findall(Rom,toenter(Rom,Time),Plans); 
								.length(Plans,Size);
								
								if(Size\==0)
								{
									for(.member(Room,Plans))
									{
										room_experiment.sameRoom(Rm,Room,Ret);
										if(Ret==0)
										{
											//remove the plan to enter this room
											-toenter(Room,_);
											
											?cancelled_plans(C);
								  			+cancelled_plans(C+1);
								  			+noEntry(Room);
								
										}
									}
								}
								.
/* 
+noAccessVIProom(Rm): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
//room_experiment.stripString(Room,Room2);
							//.delete("\"",Rm,Room);
							//.print("String is ",Room);
								//+noEntry(Rm);
							//	+noEntry(Room);
							
							.findall(Rom,toenter(Rom,Time),Plans); 
							.length(Plans,Size);
					
							 //.print("I still need to enter ", Size, " rooms");
							
							if(Size\==0)
							{
								for (.member(Room,Plans))
				  				{
				  					room_experiment.sameRoom(Rm,Room,Ret);
				  					
									if(Ret == 0)
									{
										/remove the plan to enter this room
										-toenter(Room,_);
										
										 ?cancelled_plans(C);
							  			+cancelled_plans(C+1);
							  			+noEntry(Room);
									}
				  				}
				  				
							}
				
				  				//-+room_entered(Rm);
							//	enter(N,Rm);
				
								//-+current_action(enter(N,Rm));
				  				  
									  
								.	
				
		*/		
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

+overseer(O): true <- .print("My overseer is ",O);

+overseer(O). 

+revisionActive[source(Ag)]: not acting <- .print("The solution to my problem is currently active,");
									+acting;
									-revisionActive;
									// .abolish(revisionActive);
									!retry_enterroom;
									. 

+revisionActive[source(Ag)]: acting <- .print("The solution to my problem is currently active but I am already exploring again so no need to do anything else");
									//+acting;
									-revisionActive;
									// .abolish(revisionActive);
									//!retry_enterroom;
									. 

/*+revisionActive[source(Ag)]: waiting <- .print("The solution to my problem is currently active, I should retry my action");
									-revisionActive;
									// .abolish(revisionActive);
									-waiting;
									!retry_enterroom;
									.
									
+revisionActive[source(Ag)]: not waiting <- .print("The solution to my problem is currently active,");
									-revisionActive;
									// .abolish(revisionActive);
									//!retry_enterroom;
									. */

+revisionFailed[source(Ag)]: not acting <- .print("Unfortunately, there is no solution to my problem.");
									//attempt to enter another room OR remove plan to enter this room. 
									+acting;
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
									 
+revisionFailed[source(Ag)]: acting <- .print("Unfortunately, there is no solution to my problem but I am already exploring again so no need to do anything else");
										  
									 -revisionFailed;
									 
									// !enter_room;
									 .

+revisionSucceeded[source(Ag)]: not acting <- .print("My problem has a solution, message reveived from ", Ag);
								-revisionSucceeded;
								+acting;
								skip_steps(2);
								
								?failed_plans(FpC);
								//?toexplore(TEx);
								
								if(FpC>1)
								{
									!enter_room;
								}
								else
								{
									!retry_enterroom;
								}
								
								
								.

+revisionSucceeded[source(Ag)]: acting <- .print("My problem has a solution, message reveived from ", Ag,"\nI am already exploring again so no need to do anything else");
							
								-revisionSucceeded;
								//skip_steps(2);
								
								//?failed_plans(FpC);
								//?toexplore(TEx);
								
								/*if(FpC>1)
								{
									!enter_room;
								}
								else
								{
									!retry_enterroom;
								}*/
								
								
								.


+instRev[source(Ag)]: true <- .print("The institution has changed, message reveived from ", Ag);
								-instRev;
			
								.

								
/* +instRev[source(Ag)]: waiting <- .print("The institution has changed, message reveived from ", Ag);
								-instRev;
								// .abolish(instRev);
								?overseer(O);
								if(O==Ag)
								{
									-waiting;
								
								    !retry_enterroom;
									
								}
								else{
									.print("There is a change but it may not have fixed my problem, I will keep wait");
									
								}
								
								.
								

+instRev[source(Ag)]: not waiting <- .print("The institution has changed, message reveived from ", Ag);
								-instRev;
			
								. */
					 