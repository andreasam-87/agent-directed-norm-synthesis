package instal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class InstalAtom {
	public enum AtomType { 
		Holdsat,
		Occurred,
		Observed,
		Viol,
		Invalid
	}
	String institution;
	String atom_name;
	String[] arguments;
	InstalAtom e, d, v;
	AtomType type;
	public String toString() {
		return "InstalAtom: "+type+": "+atom_name;
	}
	public InstalAtom(JSONArray atom) {
		String type_part = atom.getString(0);
		JSONArray fluent_part;
		try { 
			fluent_part = atom.getJSONArray(1).getJSONArray(0);
		} catch (Exception e) {
			fluent_part = atom.getJSONArray(1);
		}
		String institution_part;
		try {
			institution_part = atom.getJSONArray(1).getString(1);
		} catch (Exception e) {
			institution_part = "null";
		}
		type = getTypeName(type_part);

		if (type == AtomType.Occurred) { 
			if (checkForViol(fluent_part)) {
				type = AtomType.Viol;
				fluent_part = fluent_part.getJSONArray(1);
			}
		}
		atom_name = getAtomName(fluent_part);
		if (atom_name.equals("_unrecognisedEvent") || atom_name.equals("_unempoweredEvent")) {
			type = AtomType.Invalid;
		} else if (atom_name.equals("obl")) {
			InstalAtom[] oblargs = getOblArguments(fluent_part.getJSONArray(1));
			e = oblargs[0];
			d = oblargs[1];
			v = oblargs[2];
		} else {
		arguments = getAtomArguments(fluent_part);
		}
		institution = institution_part;
		//System.out.println("Atom processed: \n atom name: "+atom_name+"\narguments: "+arguments.length+"\ninst: "+in_institution);
	}
	
	AtomType getTypeName(String typename) {
		if (typename.equals("holdsat")) {
			return AtomType.Holdsat;
		} else if (typename.equals("occurred")) {
			return AtomType.Occurred;
		} else if (typename.equals("observed")) {
			return AtomType.Observed;
		}
		return type; // deal with error
	}
	String getAtomName(JSONArray center_part) {
		String name;
		try { 
			name = center_part.getString(0);
		} catch (Exception e) {
			name = center_part.getJSONArray(0).getString(0);
		}
		return name;
			
	}
	InstalAtom[] getOblArguments(JSONArray center_part) {
		List<InstalAtom> lst = new ArrayList<InstalAtom>();
		for (int i = 0; i < center_part.length(); i++) {
			JSONArray arg = new JSONArray();
			arg.put(0,"occurred");
			arg.put(1,center_part.getJSONArray(i));
			lst.add(new InstalAtom(arg));
		}
		return lst.toArray(new InstalAtom[lst.size()]);
	}
	String[] getAtomArguments(JSONArray center_part) {
		List<String> args = new ArrayList<String>();
		JSONArray arguments;
		try {
			arguments = center_part.getJSONArray(1);
		} catch (Exception e) {
			return new String[0];
		}
		if (center_part.length() > 1) {
			for (int i = 0; i < center_part.getJSONArray(1).length(); i++) {
					String arg = center_part.getJSONArray(1).getString(i);
					args.add(arg);
			} 
		}
		return args.toArray(new String[args.size()]);
	}
	boolean checkForViol(JSONArray center_part) {
		if (getAtomName(center_part).equals("viol")) {
			return true;
		} 
		return false;

	}
	
	public String getInstitution() {
		return institution;
	}
	public String getAtomName() {
		return atom_name;
	}
	public AtomType getAtomType() {
		return type;
	}
	public String[] getArguments() {
		return arguments;
	}
	public InstalAtom[] getOblArguments() {
		return new InstalAtom[] { e, d, v };
	}
}