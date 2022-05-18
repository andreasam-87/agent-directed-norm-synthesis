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
					!check_act.

+!check_act: true <-  .print("you can enter");
					//		experiment5.myPrint('you can enter');
							!enter_room.
							//. 
								
							
+!enter_room: true <- .print("I tried to enter");
					//experiment5.myPrint('I tried to enter');
					.my_name(N);
			
					 room_experiment.getRandomNum(1,2,Rnd);
		
					//attempting to strip the extra ""
					//		 .concat(room,Rnd,Rom);
					//	 room_experiment.stripString(Rom,Rm);
					 
					  .concat(room,Rnd,Rm);
					 +room_entered(Rm);
					 enter(N,Rm);
					//cheers;
					//check;
					//experiment5.myPrint(N);
					 -+current_action(enter(N,Rm));
					// +perm(leave);
					 .
		
+perm(leave(_,_)) : roomCapacityExceeded <- ?role(P,R);
						?room_entered(Rm);
					//	.print('I am in ',Rm,' and my role is ', R, '  I will exit when ready'); 
						
						?current_action(A);
					.print("I am in Room ", Rm, " and my role is ",R," but it is full, I may need to leave the room");
					?overseer(O);
					//.send(synthesizer,tell,request(roomCapacityExceededViol, A, noViol));	
					//.send(O,tell,request(roomCapacityExceededViol, A, noViol(holdsat(meeting))));
					.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));			
					//	.print(request(roomCapacityExceededViol, A, noViol));
					
					//TO DO -- include the logic to have them decide to move 
					//They should leavw, no
					//!leave_now;	
						.


+perm(leave(_,_)) :   not roomCapacityExceeded <- ?role(P,R);

					?room_entered(Rm); //fix to work with in_room belief instead.
					
					.print("I am in ",Rm," and my role is ", R, "  I will exit when ready"); 
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
	
	
+!leave_now: true <- .print("Decided to leave, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm); 
					-+current_action(leave(N,Rm));
					+perm(idle);
					.	
					
	
+!idle_now: true <- .print("Decided to remain in room for now");
					.						

//maybe an error with trying to do two actions, will check with Fahid on how the environment works or something					 
//+perm(leave(_,_)) : true <- ?role(R);
//						?room_entered(Rm);
//						.print('I am in ',Rm,' and my role is ', R, '  I will exit when ready'); 
////						
////						//experiment5.myPrint('I am in ',Rm,' and my role is ', R, '  I will exit when ready');
//						.my_name(N);
//						
//						leave(N,Rm); 
//							//leave.
//							 -+current_action(leave(N,RM));
//							.
//	
//+roomCapacityExceeded : true <- 
//					?room_entered(Rm);
//					?current_action(A);
//					.print('Room ', Rm,' is full, I may need to leave the toom');
//				//	.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
//					.send(synthesizer,tell,request(prob(enter), A, perm(leave)));
//					
//								.
		
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
						
//+restrictedAccess(R): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
//				.	

+noAccessVIProom(Rm): true <- .print("Unfortunately I cannot enter a VIP room as I am not a VIP");
								+noEntry(Rm);
								.	
				
				
+prob(enter) :  true <- ?role(P,R);
						//.print("I cannot enter, I am role -",R);
						?current_action(A);
						//experiment5.myPrint('I cannot enter, I am role - ',R, ' I was trying to ',A);
						.print("I cannot enter, I am role - ",R, " I was trying to ",A);
						?overseer(O);
						.send(O,tell,request(prohibitedEntry, A, allowedEntry));	
						//.send(synthesizer,tell,request(prohibitedEntry, A, allowedEntry));	
						
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

+revisionActive[source(Ag)]: true <- .print("The solution to my problem is currently active, I should retry my action").

+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").


+instRev[source(Ag)]: true <- .print("The institution has changed, message reveived from ", Ag);

								-instRev;
								.
					 