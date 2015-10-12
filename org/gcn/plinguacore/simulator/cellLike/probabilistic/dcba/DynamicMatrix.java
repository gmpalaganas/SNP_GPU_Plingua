package org.gcn.plinguacore.simulator.cellLike.probabilistic.dcba;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.gcn.plinguacore.util.ExtendedLinkedHashSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.RandomNumbersGenerator;
import org.gcn.plinguacore.util.ShuffleIterator;
import org.gcn.plinguacore.util.Triple;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.cellLike.CellLikeConfiguration;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.rule.IConstantRule;


public class DynamicMatrix extends StaticMatrix {

	private List<MatrixColumn> filterColumns;
	private List<MatrixRow> filterRows;
	private List<Double> sumsByRow;
	private List<Long> multiplicities;
	private Map<MatrixKey,Long>dynamicMatrix;
	private Map<String,Integer>membranes;
	private MatrixKey matrixKeyAux;
	private long selectedRules;
	
	// new attributes for the current version of the algorithm
	private List<Pair<MatrixColumn,MatrixColumn>> inconsistencies;			// list with pairs of inconsistent blocks
	private Map<String,Pair<Byte,MatrixColumn>> inconsistencyDetector;		// map with the label associated to the last consistent block processed
	private boolean allZeroes = false;
	
	
	private ArrayList<MatrixColumn>auxArrayList;
	
	// LinkedList allows to add/remove element(s) at the beginning/end of the list
	//private LinkedList<MatrixColumn>auxLinkedList;
	
	
	/* Nº de aplicaciones por entorno y bloque */
	private Map<String,Map<MatrixColumn,Long>>applications;

	private String matrixToString[][]=null; // Estructura para imprimir por pantalla, si nunca
	//se llama al metodo toString, esta estructura permanece en null para no desperdiciar mem.
	
	public DynamicMatrix(Psystem ps) throws PlinguaCoreException{
		super(ps);
		//auxArrayList = new ArrayList<MatrixColumn>(getColumns().size());
		//auxLinkedList = new LinkedList<MatrixColumn>();
		filterRows = new LinkedList<MatrixRow>();
		filterColumns = new LinkedList<MatrixColumn>();
		applications = new HashMap<String,Map<MatrixColumn,Long>>();
		membranes = StaticMethods.getMembraneIdsByLabelAndEnvironment((CellLikeSkinMembrane)ps.getMembraneStructure());
		matrixKeyAux=new MatrixKey();
		sumsByRow = new ArrayList<Double>();
		dynamicMatrix=new HashMap<MatrixKey,Long>();
		multiplicities = new ArrayList<Long>();
		
		// initialization of the new attributes
		inconsistencies = new ArrayList<Pair<MatrixColumn,MatrixColumn>>();
		inconsistencyDetector = new HashMap<String,Pair<Byte,MatrixColumn>>();
		// TODO Auto-generated constructor stub
	}
	

	
	public List<MatrixRow> getFilterRows() {
		return filterRows;
	}
	
	
	public List<MatrixColumn> getFilterColumns() {
		return filterColumns;
	}
	
	
	/* step 0 */
	// Inicializa estructuras de datos, este paso se hace antes del bucle K
	public void initData(String environment)
	{
		selectedRules=0;
		
		// gets the applications for the given environment
		
		Map<MatrixColumn,Long>app = applications.get(environment);
		
		// if it does not exist, create an empty one
		
		if (app==null)
		{
			app = new HashMap<MatrixColumn,Long>();
			applications.put(environment,app);
		}
		
		// if it exists, clear it, rendering it empty again
		
		else
			app.clear();
		
		filterRows.clear();
		filterColumns.clear();

		
	}

	
	/* step		1 */
	/* filter	1 */
	
	public void filterColumns1(CellLikeConfiguration c1,String environment)
	{

		for (MatrixColumn c:getColumns())
			if(c.retainColumn((CellLikeSkinMembrane)c1.getMembraneStructure(), membranes, environment))
			{
					c.setMin(Long.MAX_VALUE);
					filterColumns.add(c);
			}
		
	}
	

	// generic second filter column: applies the second filter column over filterColumns array
	
	/* step		2  */
	/* filter	2 */
	public void filterColumns2(CellLikeConfiguration c1,String environment)
	{		
		Iterator<MatrixColumn> itc = filterColumns.iterator();
	
		while(itc.hasNext())
		{
			
			MatrixColumn c = (MatrixColumn) itc.next();
			//System.out.println("Columna "+ (++count)+" de "+this.getColumns().size());
			matrixKeyAux.setColumn(c);
			
			boolean found = false;
			boolean empty = true;
			
			Iterator<MatrixRow> itr = this.getRows().iterator();
				
			while(itr.hasNext() && !found)
			{
				MatrixRow r = (MatrixRow) itr.next();
				
				matrixKeyAux.setRow(r);
				
				if (getStaticMatrix().containsKey(matrixKeyAux))
				{
					
					long value = getStaticMatrix().get(matrixKeyAux);
					
					Membrane m = StaticMethods.getMembrane( r.getLabel(), environment, (CellLikeSkinMembrane)c1.getMembraneStructure(), membranes);
					
					if(m != null)
					{
						empty=false;
						long mult = m.getMultiSet().count(r.getObject());
							
						if(mult < value)
						{
							itc.remove();
							found = true;
						}
					}
					
				}
			}
			if (empty) 
				itc.remove();
			
		}		
			
	}
	
	
	/* block mutual consistency check */ 
	
	/* step		3  */
	public void checkMutualConsistency(CellLikeConfiguration c1,String environment) throws PlinguaCoreException
	{
		
		inconsistencies.clear();
		inconsistencyDetector.clear();
		Pair<Byte,MatrixColumn> p;
		Pair<Byte,MatrixColumn> pAux;
		Pair<MatrixColumn,MatrixColumn> pInc;
		
		Iterator<MatrixColumn> it = filterColumns.iterator();
		
		while(it.hasNext())
		{
			MatrixColumn c = (MatrixColumn) it.next();
			
			if(c instanceof SkeletonRulesBlock)
			{
				SkeletonRulesBlock b = (SkeletonRulesBlock) c;
				String label = b.getMainLabel();
				Byte charge = b.getrCharge();
				
				pAux = inconsistencyDetector.get(label);
				
				if(pAux == null)
				{
					p = new Pair<Byte,MatrixColumn>(charge,b);
					inconsistencyDetector.put(label, p);
				}
				else
				{
					Byte charge2 = pAux.getFirst();
					
					if(charge.equals(charge2))
					{
						pAux.setSecond(b);
						inconsistencyDetector.put(label, pAux);
					}
					else
					{
						pInc = new Pair<MatrixColumn,MatrixColumn>(b,pAux.getSecond());
						inconsistencies.add(pInc);
					}
				}
				
			}
			
		}
		
		if(!inconsistencies.isEmpty())
		{
			throw new PlinguaCoreException("Inconsistent Blocks found for environment " + environment + " " + inconsistencies.toString());
		}
			
	}

	// generic row filter: applies row filter over filterRows array
	// modified version of the last implemented row filter, stepping over the associated column filter
	
	/* step		4 */
	/* filter	3 */
	public void initFilterRows(CellLikeConfiguration c1,String environment)
	{
		// filterRows.clear();
		// sumsByRow.clear();
		for (MatrixRow r:getRows())
		{
			
			if ( r.retainRow((CellLikeSkinMembrane)c1.getMembraneStructure(), membranes, environment))
				filterRows.add(r);
			
			/*
			else
			{
				matrixKeyAux.setRow(r);
				Iterator<MatrixColumn>it = filterColumns.iterator();
				while(it.hasNext())
				{
					MatrixColumn c = it.next();
					matrixKeyAux.setColumn(c);
					
					// erases the column from filterColumns in case of (row,column) exists in the matrix
					// this results in the erasure of all the columns c of the form (row,c) --> explain
					
					if (getStaticMatrix().containsKey(matrixKeyAux))
						it.remove();
				}
				
			}
			
			*/
		}
	}
	
	
	public void filterRows(CellLikeConfiguration c1,String environment)
	{
	
		Iterator<MatrixRow> it = filterRows.iterator();
		
		while(it.hasNext())
		{
			MatrixRow r = (MatrixRow) it.next();
			
			if ( ! r.retainRow((CellLikeSkinMembrane)c1.getMembraneStructure(), membranes, environment))
				it.remove();
				
		}
				
	}
	
	/* step 5 */
	public void normalizeRowsAndCalculateMinimums(CellLikeConfiguration c1,String environment)
	{
		double sum;
		double multiplicity;
		MatrixKey mklocal = null;
		
		// initialize new attributes in use
		
		int totalBlocks = filterColumns.size();
		int numZeroBlocks = 0;
		setAllZeroes(false);
		
		// end initialization
		
		dynamicMatrix.clear();
		multiplicities.clear();
		
		// since filterRows method is now called outside the accuracy loop, this array is now initialized here, so no 'duplicities' are added.
		
		sumsByRow.clear();

		Iterator<MatrixRow> itr = filterRows.iterator();
		//for (MatrixRow r:filterRows)
		while(itr.hasNext())
		{
			
			MatrixRow r = (MatrixRow) itr.next();
			
			sum=0;
						
			ListIterator<MatrixColumn>it = filterColumns.listIterator();
			while (it.hasNext())
			{
				MatrixColumn c = it.next();

				mklocal = new MatrixKey(r,c);
				Long value = getStaticMatrix().get(mklocal);
				if (value!=null)
					sum+= 1/value.doubleValue();
			}
			
			// remember: if sum == 0 then the current row is never involved in the application of any block
			if (sum > 0)
			{
				Membrane m = StaticMethods.getMembrane(r.getLabel(), environment, (CellLikeSkinMembrane)c1.getMembraneStructure(), membranes);
				if (m==null)
					multiplicity=0;
				else
					multiplicity=m.getMultiSet().count(r.getObject());
				multiplicities.add((long)multiplicity);
				
				sumsByRow.add(sum);

				while (it.hasPrevious())
				{
					MatrixColumn c=it.previous();
					mklocal = new MatrixKey(r,c);
	
					Long value = getStaticMatrix().get(mklocal);
					if (value!=null)
					{
						long normalizedValue =(long)Math.floor(multiplicity*(1/value.doubleValue())*(1/value.doubleValue())/sum);
						dynamicMatrix.put(mklocal, normalizedValue);
						if (normalizedValue<c.getMin())
						{
							c.setMin(normalizedValue);
						
							// only when the minimum is updated we have to check if is equal to zero, plus in this case is not going to be updated again (0 < 0 == false)
							// so we can safely increment numZeroBlocks here.
							
							if(normalizedValue == 0L)
								numZeroBlocks++;
						}
					}
					
				}	
			}
			else
			{
				itr.remove();
			}
		
		}

		// we set the value of num zero blocks attribute
		
		setAllZeroes((numZeroBlocks == totalBlocks));
		
	}
	
	/* step 5 */
	public void removeLeftHandRuleObjects(CellLikeConfiguration c1,String environment)
	{
	
		
		Map<MatrixColumn,Long>app = applications.get(environment);
		auxArrayList = new ArrayList<MatrixColumn>(); //.clear();
		for (MatrixColumn c:filterColumns)
		{			
			selectedRules+=c.getMin();
			
			app.put(c, app.containsKey(c)?app.get(c)+c.getMin():c.getMin());
		
			if (c.removeLeftHandRuleObjects((CellLikeSkinMembrane)c1.getMembraneStructure(),membranes,environment,c.getMin()))
				auxArrayList.add(c);
			
			// this step is also performing a column filter 
		
		}
		
		filterColumns=auxArrayList;
	
	
	}
	

	/* method for updating multiplicities at the beginning of the maximality step */
	
	public void updateMultiplicities(CellLikeConfiguration c1,String environment)
	{
		double multiplicity;
		
		multiplicities.clear();
		

		for (MatrixRow r:filterRows)
		{
		
			Membrane m = StaticMethods.getMembrane(r.getLabel(), environment, (CellLikeSkinMembrane)c1.getMembraneStructure(), membranes);
			if (m==null)
				multiplicity=0;
			else
				multiplicity=m.getMultiSet().count(r.getObject());
			multiplicities.add((long)multiplicity);
			
		}

	}
	
	/* step 6 */
	public void maximality(CellLikeConfiguration c1,String environment)
	{
		
		updateMultiplicities(c1,environment);
		
		Map<MatrixColumn,Long>app = applications.get(environment);
		
		
		ShuffleIterator<MatrixColumn> it = new ShuffleIterator<MatrixColumn>(filterColumns);
		while (it.hasNext())
		{
		
			MatrixColumn c = it.next();
			c.setMin(0);
			long applications = c.countApplications((CellLikeSkinMembrane)c1.getMembraneStructure(),membranes,environment);
			if (applications>0)
			{
				c.removeLeftHandRuleObjects((CellLikeSkinMembrane)c1.getMembraneStructure(),membranes,environment,applications);
				c.setMin(applications);
				selectedRules+=applications;
				
				app.put(c, app.containsKey(c)?app.get(c)+applications:applications);
					
			}
			
		
		}
		
		
	}
	
	
	public long getSelectedRules() {
	
		return selectedRules;
	}



	/**
	 * @param allZeroes the allZeroes to set
	 */
	public void setAllZeroes(boolean allZeroes) {
		this.allZeroes = allZeroes;
	}



	/**
	 * @return the allZeroes
	 */
	public boolean getAllZeroes() {
		return allZeroes;
	}



	/* step 7 */
	public void executeRules(CellLikeConfiguration c1,String environment,DCBAProbabilisticSimulator sim)
	{
		Map<MatrixColumn,Long>app = applications.get(environment);
		for (Entry<MatrixColumn,Long>entry:app.entrySet())
		{
			MatrixColumn c = entry.getKey();
			long N = entry.getValue();
			
			double d = 1.0;
			for (IRightHandRule rhr:c.getRightHandRules())
			{
				
				long n;
				if (N==0)
					n=0;
				else
				{
					double p = rhr.getProbability(environment).doubleValue();
					
					if (p==0)
						n=0;
					else
					if (p==1)
						n=N;
					else
					{
						p=p/d;
						double q = 1-p;
						d = d*q;
						if (d<0) d=0;
						if (p>=1)
							n=N;
						else
							n = RandomNumbersGenerator.getInstance().nextLongBi(N,p);
					}
				}
				if (n>0)
				{
				
					rhr.execute((CellLikeSkinMembrane)c1.getMembraneStructure(), membranes, c.getMainLabel(), environment,n);
					long resto=N-n;
					if (resto<0) resto=0;
					N=resto;
					
					if (sim.isShowRules())
					{
						Long rid = rhr.getRuleId(environment);
						if (rid==null)
							rid=rhr.getRuleId("");
						String str = "#"+rid+" "+c.matrixColumnToString()+rhr.toString(environment);
						ChangeableMembrane m= (ChangeableMembrane)StaticMethods.getMembrane(c.getMainLabel(), environment, (CellLikeSkinMembrane)c1.getMembraneStructure(), membranes);
						sim.selectRule(str, m, n);
					}
					
				}
					
				
			
			
			}
		}
		
	}
	private String getSeparador(int columnas,int size)
	{
		String str="+";
		for (int i=0;i<columnas;i++)
		{
			for (int j=0;j<size;j++)
				str+="-";
			str+="+";
		}
		return str;
		
	}
	
	@Override
	public String toString() {
		
		if (matrixToString==null)
				matrixToString = new String[getRows().size()+2][getColumns().size()+2];	
	
				
		int maxColumnSize=0;
	
		// TODO Auto-generated method stub
		for (int i =0;i<filterColumns.size();i++)
		{
			String text=filterColumns.get(i).matrixColumnToString();
			matrixToString[0][i+1]=text;
			if (text.length()>maxColumnSize)
				maxColumnSize=text.length();
			text=String.valueOf(filterColumns.get(i).getMin());
			matrixToString[getFilterRows().size()+1][i+1]=text;
			if (text.length()>maxColumnSize)
				maxColumnSize=text.length();
		}
		for (int i=0;i<filterRows.size();i++)
		{
			String text =filterRows.get(i).toString();
			text+=" * "+multiplicities.get(i);
		
			matrixToString[i+1][0]=text;
			if (text.length()>maxColumnSize)
				maxColumnSize=text.length();
			text = sumsByRow.get(i).toString();
			matrixToString[i+1][filterColumns.size()+1]=text;
			if (text.length()>maxColumnSize)
				maxColumnSize=text.length();
		}
		matrixToString[filterRows.size()+1][0]="";
		matrixToString[filterRows.size()+1][filterColumns.size()+1]="";
		matrixToString[0][filterColumns.size()+1]="";
		for (int i =0;i<filterRows.size();i++)
		{
			for (int j=0;j<filterColumns.size();j++)
			{
				matrixKeyAux.setRow(filterRows.get(i));
				matrixKeyAux.setColumn(filterColumns.get(j));
				
				Long data=getStaticMatrix().get(matrixKeyAux);
				// we change the line below to accommodate to the change 1/k --> k 
				// String text = data==null?"-":data.toString();
				
				String text = null;
				
				if(data == null)
					text = "-";
				else
					text = "" + (1 / data.doubleValue());
				
				data=dynamicMatrix.get(matrixKeyAux);
				
				text += data==null?"":" | "+data.toString();
				matrixToString[i+1][j+1]=text;
				if (text.length()>maxColumnSize)
					maxColumnSize=text.length();
			}
			
		}
		String separador =getSeparador(getFilterColumns().size()+2,maxColumnSize);
		String str =separador +"\n";
		for (int i=0;i<getFilterRows().size()+2;i++)
		{
			for (int j=0;j<getFilterColumns().size()+2;j++)
			{
				String text = matrixToString[i][j];
				if (text==null)
					text="";
				int blancos = maxColumnSize-text.length();
				int lb = blancos/2;
				int rb = blancos-lb;
				for (int k=0;k<lb;k++)
					text = " "+text;
				for (int k=0;k<rb;k++)
					text = text + " ";
				if (j==0)
					text = "|"+text;
				text = text+"|";
				str+=text;
			}
			str+="\n"+separador+"\n";
		
			
		}
		
		//String str = "Columns: ";
		//str+=columns.toString()+"\n";
		//str+="Rows: "+rows.toString()+"\n";
		//str+="Cells: "+staticMatrix.toString();
		return str;
	}
	
	
	
	
	
}
