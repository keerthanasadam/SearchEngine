import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JTextArea;

import org.omg.Messaging.SyncScopeHelper;
public class Cosinesim {
	File[] listOfFiles;
	public static DB db = new DB();
Indexing idx=new Indexing();
searchengine1 se=new searchengine1();
JTextArea output=se.output;
HashMap<String, Double>cosscore=new HashMap<String, Double>();
List<String>topres=new ArrayList<String>();
List<String>moreres=new ArrayList<String>();
static HashMap<String, HashMap<String, Integer>> doctokens;
HashMap<String, HashMap<String, Integer>> doctokfreq;
	public void cossimcal(HashMap<String,Integer> qtok) throws IOException, SQLException{
		HashMap<String, Double> tfidfq = new HashMap<String,Double>();
		Double qlength=0.0;
		File folder = new File("C:/JavaPrograms/weblinks");
		listOfFiles = folder.listFiles();
		double doclength=listOfFiles.length;
		doctokens = se.doctokensnew;
		doctokfreq=se.doctokfreqnew;
		System.out.println("cosscore class begins");
		HashMap<String , Integer> qtokens=qtok;
	for(Map.Entry<String, Integer> meq: qtokens.entrySet())
	{
		if(!doctokens.containsKey(meq.getKey()))
			return;
				Double qweight=0.0;
				Double docweight=0.0;
				Double tweight=0.0;
				HashMap<String, Integer> docfreq=doctokens.get(meq.getKey());
				double docsize=docfreq.size();
				for(Map.Entry<String, Integer> mdf :docfreq.entrySet())
				{
					
					if(tfidfq.containsKey(mdf.getKey()))
					{
						tweight=tfidfq.get(mdf.getKey());	
						qweight=meq.getValue()*	Math.log(doclength / docsize) / Math.log(2);
						docweight=mdf.getValue()* Math.log(doclength / docsize) / Math.log(2);
						tweight=tweight+qweight*docweight;
						tfidfq.put(mdf.getKey(),tweight);	
					}
					else
					{
						qweight=meq.getValue()*	Math.log(doclength / docsize) / Math.log(2);
						docweight=mdf.getValue()* Math.log(doclength / docsize) / Math.log(2);
						tweight=qweight*docweight;
						//System.out.println("tweight:"+tweight);
						tfidfq.put(mdf.getKey(),tweight);		
					}
				}
				qlength=qlength+(qweight*qweight);
				qlength=Math.sqrt(qlength);
				System.out.println("qlength"+ qlength);
	}
	Double dlength=0.0;
	for(Map.Entry<String, Double> metfidf: tfidfq.entrySet())
	{
		Double docweight=0.0;
		HashMap<String, Integer> tokfreq=doctokfreq.get(metfidf.getKey());
		Iterator it=tokfreq.entrySet().iterator();
		while(it.hasNext())
		{
			dlength=dlength+docweight*docweight;
			Map.Entry<String, Integer> me=(Map.Entry<String, Integer>)it.next();
			HashMap<String, Integer> docfreq=doctokens.get(me.getKey());
			double docmapsize= docfreq.size();
			//System.out.println("docfreq for doclength:"+docmapsize);
			docweight=me.getValue()* Math.log(doclength/ docmapsize) / Math.log(2);
		}	
		dlength=Math.sqrt(dlength);
		//System.out.println("doclength:"+metfidf.getKey()+":"+dlength);
		cosscore.put(metfidf.getKey(), metfidf.getValue()/qlength*dlength);
	}
	Map<String, Double> sortedcosscore = sortByValue(cosscore);
	//System.out.println("cossim value:"+sortedcosscore);
	}
	public ArrayList<String> topresults() throws SQLException{
		Map<String, Double> sortedcosscore = sortByValue(cosscore);	
	Set<Entry<String, Double>> set = sortedcosscore.entrySet();
	Iterator<Entry<String, Double>> iterator = set.iterator();
	for(int i=0;i<5;i++)
	{
		Map.Entry me = (Map.Entry) iterator.next();
		
		String docname=(String) me.getKey();
		
		String sql1="select * from record where RecordID='"+docname+"'";
		ResultSet rs1 = db.runSql(sql1);
		
		while(rs1.next())
		{
		String url=rs1.getString("URL");
			topres.add(url);
		}
	}

	return (ArrayList<String>) topres;
	}
	//Retrieving all documents based on cosinesimilarity
	public ArrayList<String> moreresults() throws SQLException{
		Map<String, Double> sortedcosscore = sortByValue(cosscore);	
	Set<Entry<String, Double>> set = sortedcosscore.entrySet();
	Iterator<Entry<String, Double>> iterator = set.iterator();
	while(iterator.hasNext())
	{
		Map.Entry me = (Map.Entry) iterator.next();
		
		String docname=(String) me.getKey();
		
		String sql1="select * from record where RecordID='"+docname+"'";
		ResultSet rs1 = db.runSql(sql1);
		while(rs1.next())
		{
		String url=rs1.getString("URL");
			//System.out.println(url);
		
		moreres.add(url);
		}
	}
	
	return (ArrayList<String>) moreres;
	}
	public static void main(String args[])throws IOException
	{
		Cosinesim cosim=new Cosinesim();
		/*HashMap<String, Integer>test=new HashMap<>();
		test.put("blackboard", 1);
		try {
			cosim.cossimcal(test);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//cosim.cossimcal("blackboard");
		
	}
	public static Map<String, Double> sortByValue(Map<String, Double> unsortedMap) {
		Map<String, Double> sortedMap = new TreeMap<String, Double>(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
}

class ValueComparator implements Comparator<String> {

Map<String, Double> map;

public ValueComparator(Map<String, Double> map) {

this.map = map;

}

public int compare(String keyA, String keyB) {

int compare=map.get(keyB).compareTo(map.get(keyA));
 if (compare == 0) return 1;
else return compare;

}

}
