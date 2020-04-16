// Agent agent in project room_experiment

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

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
					 
+perm(leave) : true <- ?role(R);
						?room_entered(Rm);
						.print('I am in ',Rm,' and my role is ', R, '  I will exit when ready'); 
						
						//experiment5.myPrint('I am in ',Rm,' and my role is ', R, '  I will exit when ready');
						.my_name(N);
						
						leave(N,Rm); 
							//leave.
						//	 -+current_action(leave(N,RM));
							.
							
+perm(idle)	: true <- .print("I left so I am chillin").
					//	experiment5.myPrint('I left so I am chillin').
							

+prob(enter) :  true <- ?role(R);
						//.print("I cannot enter, I am role -",R);
						?current_action(A);
						//experiment5.myPrint('I cannot enter, I am role - ',R, ' I was trying to ',A);
						.print('I cannot enter, I am role - ',R, ' I was trying to ',A);
						//.send(supervisor,tell,deniedEntry);
						
						//an appropriate send template
						.send(synthesizer,tell,request(prob(enter), A, perm(leave)));
						//request(ActRes,ActAtmpt,Exp)
						
						//.send(supervisor,tell,request(prob(enter),enter(R) --> A, perm(enter)))
						//1-unhappy with, 2- justification - what I was doing , 3- norm or expectation(maybe part of 3) 
						//3 cont'd - probably in_room expected
						
						.print("Awaiting directive");
						//experiment5.myPrint(' Awaiting directive').
								//setpermenter;
								//.print("I want to enter");
								//!check_act.
								.

+prob(wave) :  true <- .print("I cannot leave").
						//experiment5.myPrint('I cannot leave').
					 