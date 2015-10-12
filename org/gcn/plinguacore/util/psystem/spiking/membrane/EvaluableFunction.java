package org.gcn.plinguacore.util.psystem.spiking.membrane;

import java.util.List;

public interface EvaluableFunction {
	
	public Integer numParams = 0;
	public void storeFunction(Object function, Integer numParams) throws Exception;
	public Object evaluateFunction(List<Object> params) throws Exception;
	public Integer getNumParams();

}
