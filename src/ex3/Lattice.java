package ex3;

import java.util.*;

public class Lattice {
	private List<LatticeVar> set;

	public Lattice() {
		set = new ArrayList<LatticeVar>();
	}

	public void add(String varID, initKind kind) {
		LatticeVar var = new LatticeVar(varID, kind);
		set.add(var);
	}

	public void assign(String varID) {
		for (LatticeVar var : set) {
			if (varID.equals(var.VarID)) {
				var.isInit = initKind.tt;
				break;
			}
		}
	}

	public void join(Lattice l) {
		for (int i = 0; i < set.size(); i++) {
			set.get(i).isInit = LatticeVar.varJoin(set.get(i), l.set.get(i));
		}
	}

	public boolean isInit(String varID) {
		for (LatticeVar var : set) {
			if (varID.equals(var.VarID)) {
				if (var.isInit == initKind.tt)
					return true;
				else
					return false;
			}
		}
		return true;
	}

	public void copy(Lattice lat) {
		for (LatticeVar var : lat.set) {
			add(var.getVarID(), var.getKind());
		}
	}
}
