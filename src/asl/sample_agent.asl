// Agent sample_agent in project room_experiment

/* Initial beliefs and rules */

/* Initial goals */
location(room1).
location(room2).

!start.

/* Plans */

+!start : true <- .print("hello world. I am the basic agent");
					!chooseRoom;
					.
					
+!chooseRoom: true  <- //.print("Room chosen");
					.findall(Rom,location(Rom),Rooms); 
					room_experiment.chooseRoom(Rooms,C);
					.nth(C,Rooms,Item);
					.print("Room chosen - ",Item);
					
					.nth(0,Rooms,Item2);
					.print("Room in first position is - ",Item);
					.