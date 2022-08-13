// Agent oracle in project rooms

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("I am the Oracle....").


+decideRevision(File,Change)[source(Ag)]: true <- .print(Ag," has asked me to decide on a revision");
												.print("The file to be checked ",File);
												.print("The revision log file ",Change);
												+giveFeedbackTo(Ag);
												verifyRevisionAcceptable(File,Change);
												//-decideRevision(File,Change)[source(Ag)];
												.


+revisionAcceptable(RevFile): true <- .print("Revision acceptable, can be implemented");
 													?giveFeedbackTo(Ag);
													.print("Must inform ------> ",Ag);
													.send(Ag,tell,oraclePermissionGranted(RevFile));
												    -giveFeedbackTo(Ag);
												 //   -revisionAcceptable(RevFile)[source(percept)];
													.
											

+revisionUnacceptable(RevFile): true <- .print("Revision not acceptable, cannot be implemented");
 													?giveFeedbackTo(Ag);
													.print("Must inform ------> ",Ag);
													.send(Ag,tell,oraclePermissionNotGranted(RevFile));
													 -giveFeedbackTo(Ag);
												  //  -revisionUnacceptable(RevFile)[source(percept)];
												
													.