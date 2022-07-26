// Agent agent in project room_experiment

/* Initial beliefs and rules */
toenter(room1,3).
toenter(room2,2).
toexplore(2).

cancelled_plans(0).
failed_plans(0).

//is_vip(me).

/* Initial goals */

!initialise_logger_and_start.

//!start.

/* Plans */

					 

+!initialise_logger_and_start : true  <- //jason_logger.initialise; 
										!start.

+!start : true <-  .print("hello world.");
					//experiment5.myPrint('hello world');
					!check_act.

+!check_act: true <-  .print("you can enter");
					//		experiment5.myPrint('you can enter');
							!enter_room.
							//. 
								
							
+!enter_room: true <- .print("I tried to enter");
					//experiment5.myPrint('I tried to enter');
					.my_name(N);
					
					.findall([Room,Time],toenter(Room,Time),Plans); 
					.length(Plans,Size);
					
					 .print("I still need to enter ", Size, " rooms");
					//may need to rethink using a loop, just check the first item, or check any item using the question mark
					if(Size\==0)
					{
						//.nth(0,Plans,Item);
						
						//randomly select rather than always choosing the first one. 
						room_experiment.chooseRoom(Plans,C);
						.nth(C,Plans,Item);
						
						room_experiment.getItems(Item,2,Rm,Tm);
		
		  				-+room_entered(Rm);
						enter(N,Rm);
		
						-+current_action(enter(N,Rm));
		  					
							//.substring(Tm,P,6,7); //: true and R unifies with "20".
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
					
					
					if(Size\==0)
					{
						enter(N,Rm);
						
						-+current_action(enter(N,Rm));
					}
					else
					{
						!enter_room;
					}
					 .
		
+perm(leave(_,_)) : roomCapacityExceeded <- ?role(P,R);
						.my_name(N);
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
						
						?failed_plans(C);
						-+failed_plans(C+1);
						+failedTo(enter,N,Rm,A);
							
						+conflict;
						!leave_now;		
					}
					else
					{
						.print("I am not happy about this, but I am staying in the room");
						?toenter(Rm,Num);
						//skip_steps(N);
						
						for ( .range(I,1,Num) ) 
						{
							//.print("Repeating ",I);
					          explore; 
					    }
					    
					   // .abolish(toenter(Rm,N));
						.print("Finished exploring this room");
						//!leave_now;	
						
						?toexplore(Co);
						-+toexplore(Co-1);
						
						
						.abolish(toenter(Rm,Num));
						!leave_now;	
					}
						
					//	.print(request(roomCapacityExceededViol, A, noViol));
					
					//TO DO -- include the logic to have them decide to move 
						.


+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);
					
					?room_entered(Rm); //fix to work with in_room belief instead.
					
					.print("I am in ",Rm," and my role is ", R, "  I will exit when ready"); 
					
					?toenter(Rm,N);
					//skip_steps(N);
					
					for ( .range(I,1,N) ) 
					{
						//.print("Repeating ",I);
				          explore; 
				    }
					
					.print("Finished exploring this room");
					.abolish(toenter(Rm,N));
					
					?toexplore(C);
					-+toexplore(C-1);
					!leave_now;	
					

					.

/* +!leave_now: true <- .print("Decided to leave, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm); 
					-+current_action(leave(N,Rm));
					+perm(idle);
					.	*/
					
					
+!leave_now: not conflict <- .print("Finished, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm,"complete"); 
					-+current_action(leave(N,Rm));
					!idle_now;
					//+perm(idle);
					//+goal_completed;
					.	
					
	
	
+!leave_now: conflict <- .print("Must leave now, leaving room");

					?room_entered(Rm);
					.my_name(N);
					//+waiting;
					-conflict;
					leave(N,Rm,"problem"); 
					-+current_action(leave(N,Rm));
					
					!checkIdle;
					//+perm(idle);
					//.print("I will wait for word before acting again");
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
				
				!enter_room;
				.	
				
-!idle_now: true <- .print("Error likely with the delay function, continuing my plan");
					!enter_room;
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
					//	experiment5.myPrint('I left so I am chillin').



							
/* +restrictedAccess :  true <- ?role(Nm,R);
						?current_action(A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						
						?overseer(O);
						?room_entered(Rm);
						.send(O,tell,request(restrictedAccess, A, allowedAccess(Rm)));
						
							

								.							
+restrictedAccess(R): true <- .print("Unfortunately I cannot enter a VIP room since I am not a VIP");
						?room_entered(Rm);
						.print("Removing plans to enter VIP rooms since I am not a VIP");
						-toenter(Rm,T);
						
				
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
						
+noAccessVIProom(Rm):		true <- .print("Unfortunately I cannot enter a VIP room since I am not a VIP");
						//?room_entered(Rm);
						.print("Removing plans to enter VIP rooms since I am not a VIP");
						-toenter(Rm,T);	
						.			

+prob(enter) :  true <- ?role(Nm,R);
						//.print("I cannot enter, I am role -",R);
						?current_action(A);
						//experiment5.myPrint('I cannot enter, I am role - ',R, ' I was trying to ',A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
	
								.

+prob(wave) :  true <- .print("I cannot leave").
						//experiment5.myPrint('I cannot leave').


+bold(V): true <- +boldness(V). 


+overseer(O): true <- +overseer(O). 

//+revisionActive: true <- .print("The solution to my problem is currently active, I should retry my action").



+revisionActive[source(Ag)]: not acting <- .print("The solution to my problem is currently active,");
									+acting;
									-revisionActive;
									!retry_enterroom;
									. 

+revisionActive[source(Ag)]: acting <- .print("The solution to my problem is currently active but I am already exploring again so no need to do anything else");
									-revisionActive;
									. 

//+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").


+revisionFailed[source(Ag)]: not acting <- .print("Unfortunately, there is no solution to my problem.");
									+acting;
									
									 -revisionFailed;
									
									 !enter_room;
									 .
									 
+revisionFailed[source(Ag)]: acting <- .print("Unfortunately, there is no solution to my problem but I am already exploring again so no need to do anything else");
									 -revisionFailed;
									 .

+instRev[source(Ag)]: true <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
										.

/* +instRev[source(Ag)]: not conflict <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
										.

+instRev[source(Ag)]: conflict <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
									skip_steps(2);
									-conflict;
									!enter_room;
									. */
									
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
								
								.

