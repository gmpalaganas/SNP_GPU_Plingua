package org.gcn.plinguacore.util.psystem.spiking.membrane;

import java.util.Iterator;
import java.util.List;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class AstrocyteFunction implements EvaluableFunction {

	public Integer numParams = 0;
	Calculable calc = null;
	
	@Override
	public void storeFunction(Object function, Integer numParams) throws Exception {
		String func = (String) function.toString().toLowerCase();
		try {
			this.calc = new ExpressionBuilder(func).build();
			if(numParams >= 0L)
				this.numParams = numParams;
			else
				throw new IllegalArgumentException("Num of params must be equal or greater to zero.");	
		} catch (UnknownFunctionException e) {
			throw new IllegalArgumentException("Invalid function used.");
		} catch (UnparsableExpressionException e) {
			throw new IllegalArgumentException("Unparsable Expression.");
		}

	}

	
	@Override
	public Object evaluateFunction(List<Object> params) throws Exception {

		Long lparam = 0L;
		Long result = 0L;
		
		if(params == null) // || params.size() != 1)
			throw new IllegalArgumentException("List of parameters can not be null");

		try
		{
		
		int i = 0;
		
		Iterator<Object> itparams = params.iterator();
		
		while(itparams.hasNext())
		{
			Object param = (Object) itparams.next();
			
			i++;
			
			if(!param.getClass().equals(Long.class))
				throw new IllegalArgumentException("The param type must be Long");
			else
			{
				lparam = (Long) param;
				
				if(lparam < 0L)
					throw new IllegalArgumentException("The param must be a natural number.");
						
				if(calc == null)
					throw new Exception("Function must be initialized before being evaluated.");
				else
				{
						
					String varName = "x" + i;
					calc.setVariable(varName,lparam);
						
				}
					
			}
				
		}
			
		Double semiresult = calc.calculate(); 
		result = semiresult.longValue();

		if (result < 0L)
			throw new IllegalArgumentException("The result must be a natural number.... " + "funcion: " + calc.getExpression() + " param: " + lparam + " result: " + result);
				
		return result;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

	}

	
	@Override
	public Integer getNumParams() {
		return numParams;
	}
}

