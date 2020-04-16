package instal;

import jason.asSyntax.*;
import java.util.*;
public interface Institutionable {
    // toGrounding() converts and Institutionable environment to an InstalGrounding
    public InstalGrounding toGrounding();
    public InstalGrounding toGrounding(InstalModel model);

    // perceptsFromInstitution() executes some queries on toGrounding() and returns a
    // Map from agent IDs to percept literals that that agent gets from
    // The institution.
    public Map<String, Literal[]> perceptsFromInstitution();

}