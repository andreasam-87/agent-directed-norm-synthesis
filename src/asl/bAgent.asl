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
					room_experiment.chooseRandomRoom(Rm);
					//experiment5.myPrint(Rm);
					//experiment5.myPrint(N);
					 //enter;
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
					?boldness(B);
					if((B mod 2)==0)
					{
						?overseer(O);
						//.send(O,tell,request(roomCapacityExceededViol, A, noViol(holdsat(meeting))));	
						.send(O,tell,request(capacityExceededViol, A, noViol(holdsat(meeting))));	
						
						.print("I am not happy about this, complaining to synthesiser");
						//.send(synthesizer,tell,request(roomCapacityExceededViol, A, noViol));	
						+conflict;
						!leave_now;		
					}
					else
					{
						.print("I am not happy about this, but I am staying in the room");
					}
						
					//	.print(request(roomCapacityExceededViol, A, noViol));
					
					//TO DO -- include the logic to have them decide to move 
						.


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
	
	
+!leave_now: not conflict <- .print("Decided to leave, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm); 
					-+current_action(leave(N,Rm));
					+perm(idle);
					.	
					
	
	
+!leave_now: conflict <- .print("Decided to leave, leaving now");
					?room_entered(Rm);
					.my_name(N);
					leave(N,Rm); 
					-+current_action(leave(N,Rm));
					//+perm(idle);
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

+revisionActive: true <- .print("The solution to my problem is currently active, I should retry my action").

+revisionFailed[source(Ag)]: true <- .print("Unfortunately, there is no solution to my problem.").


+instRev[source(Ag)]: not conflict <- .print("The institution has changed, message reveived from ", Ag);

								-instRev;
								.
+instRev[source(Ag)]: conflict <- .print("The institution has changed, message reveived from ", Ag);
									-instRev;
									skip_steps(2);
									-conflict;
									!enter_room;
									.
