package ex3;

public class LatticeVar {
	String VarID;
	initKind isInit;

	public LatticeVar(String varID, initKind isInit) {
		this.VarID = varID;
		this.isInit = isInit;
	}

	public static initKind varJoin(LatticeVar var1, LatticeVar var2) {
		if (var1.isInit == var2.isInit)
			return var1.isInit;
		return initKind.T;
	}
	
	public String getVarID() {
		return this.VarID;
	}
	
	public initKind getKind() {
		return this.isInit;
	}
}
