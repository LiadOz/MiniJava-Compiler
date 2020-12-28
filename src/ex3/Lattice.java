package ex3;

import java.util.*;

public class Lattice {
    private final HashMap<String, initKind> varMap = new HashMap<>();

    public Lattice() {

    }

    public void add(String varID, initKind kind) {
        varMap.put(varID, kind);
    }

    public void assign(String varID) {
        this.add(varID, initKind.tt);
    }

    public void join(Lattice l) {
        for (var key : l.varMap.keySet()) {
            if (!this.varMap.containsKey(key)) {
                this.varMap.put(key, l.varMap.get(key));
                continue;
            }

            initKind value;
            var currentIsInit = this.isInit(key);
            var otherIsInit = l.isInit(key);

            if (currentIsInit != otherIsInit) {
                value = initKind.T;
            } else {
                value = currentIsInit ? initKind.tt : initKind.ff;
            }

            this.varMap.put(key, value);
        }
    }

    public boolean isInit(String varID) {
        return !varMap.containsKey(varID) || varMap.get(varID) == initKind.tt;
    }

    public void copy(Lattice lat) {
        this.varMap.putAll(lat.varMap);
    }
}
