import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DiseaseGene {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException{

		int count = 0, count1 = 0, count2 = 0;
		String OMIM_ID = null, Orphanet_ID = null, temp = null;
		
		// Gene All
		HashMap<String, String> NCBI_Gene_Symbol_NCBI_ID_Map = new HashMap<String, String>();
		
		BufferedReader br_Gene_All = new BufferedReader(new FileReader(args[0]));
		
		String line;
		String[] split;
		
		String NCBI_ID = null, Gene_Symbol = null;
		
		br_Gene_All.readLine();

		while((line = br_Gene_All.readLine()) != null)
		{
			try {
				split = line.split("\t");
			
				Gene_Symbol = split[1];
				NCBI_ID = split[2];

				if(NCBI_Gene_Symbol_NCBI_ID_Map.get(Gene_Symbol) == null)
				{
					++count;
					NCBI_Gene_Symbol_NCBI_ID_Map.put(Gene_Symbol, NCBI_ID);
				}						
			}
			catch (Exception e) {
				continue;
			}
		}
		br_Gene_All.close();
		//System.out.println("NCBI_Gene_Symbol_NCBI_ID All Count : " + count);

		count = 0;
		
		HashMap<String, String> Orphanet_NCBIGene_Map = new HashMap<String, String>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(args[1]);
		
		// root
		Element root = document.getDocumentElement();
		
		NodeList DisorderList = root.getElementsByTagName("DisorderList");		
	    Element DisorderList_el = null;
	    
	    	NodeList Disorder = null;
	    	Element Disorder_el = null;
	    	
		    	NodeList OrphaNumber = null;
		    	Element OrphaNumber_el = null;
		    	
		    	NodeList Symbol = null;
		    	Element Symbol_el = null;

	    for(int i = 0; i < DisorderList.getLength(); i++)
	    {
	    	DisorderList_el = (Element) DisorderList.item(i);
	    	
	    	Disorder = DisorderList_el.getElementsByTagName("Disorder");
	      
	    	for(int j = 0; j < Disorder.getLength(); j++)
	    	{
	    		Disorder_el = (Element) Disorder.item(j);
	    		
	    		OrphaNumber = Disorder_el.getElementsByTagName("OrphaCode");
	    		OrphaNumber_el = (Element) OrphaNumber.item(0);

	    		Symbol = Disorder_el.getElementsByTagName("Symbol");
	    		
	    		for(int k = 0; k < Symbol.getLength(); k++)
	    		{
	    			Symbol_el = (Element) Symbol.item(k);
	    			if(NCBI_Gene_Symbol_NCBI_ID_Map.get(Symbol_el.getTextContent()) != null)
					{
	    				if(Orphanet_NCBIGene_Map.get(OrphaNumber_el.getTextContent() + "\t" + NCBI_Gene_Symbol_NCBI_ID_Map.get(Symbol_el.getTextContent())) == null)
						{
							++count;
							Orphanet_NCBIGene_Map.put(OrphaNumber_el.getTextContent() + "\t" + NCBI_Gene_Symbol_NCBI_ID_Map.get(Symbol_el.getTextContent()), "Orphanet");
						}
					}
	    		}
	    	}
	    }
	    //System.out.println("Orphanet NCBI Count : " + count);
	    
	    count = 0;
	    BufferedReader br_GenCC_ncbigene_orpha = new BufferedReader(new FileReader(args[2]));
	    
	    while((line = br_GenCC_ncbigene_orpha.readLine()) != null)
		{
	    	split = line.split("\t");
			
	    	NCBI_ID = split[0];
			Orphanet_ID = split[1];
			if(Orphanet_NCBIGene_Map.get(Orphanet_ID + "\t" + NCBI_ID) == null)
			{
				++count;
				Orphanet_NCBIGene_Map.put(Orphanet_ID + "\t" + NCBI_ID, "GenCC");
			}
			else
			{
				temp = Orphanet_NCBIGene_Map.get(Orphanet_ID + "\t" + NCBI_ID);
				temp = temp + "\tGenCC";
				Orphanet_NCBIGene_Map.put(Orphanet_ID + "\t" + NCBI_ID, temp);
			}
		}
	    br_GenCC_ncbigene_orpha.close();
	    //System.out.println("GenCC_ncbigene_orpha Count : " + count);
	    
		BufferedWriter bw_DiseaseGene = new BufferedWriter(new FileWriter("./Orphanet_Gene_Association.ttl"));
		
		bw_DiseaseGene.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_DiseaseGene.newLine();
		bw_DiseaseGene.write("PREFIX ncbigene: <http://identifiers.org/ncbigene/>");bw_DiseaseGene.newLine();
		bw_DiseaseGene.write("PREFIX ordo: <http://www.orpha.net/ORDO/>");bw_DiseaseGene.newLine();
		bw_DiseaseGene.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_DiseaseGene.newLine();
		bw_DiseaseGene.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_DiseaseGene.newLine();
		bw_DiseaseGene.write("PREFIX sio: <http://semanticscience.org/resource/>");bw_DiseaseGene.newLine();
		
		for(String key : Orphanet_NCBIGene_Map.keySet())
		{
			Orphanet_ID = key.split("\t")[0];
			NCBI_ID = key.split("\t")[1];
			
			temp = Orphanet_NCBIGene_Map.get(key);
			
			bw_DiseaseGene.write("<https://pubcasefinder.dbcls.jp/gene_context/disease:ORDO:" + Orphanet_ID + "/gene:ENT:" + NCBI_ID + ">");bw_DiseaseGene.newLine();
			bw_DiseaseGene.write("    a sio:SIO_000983 ;");bw_DiseaseGene.newLine();
			bw_DiseaseGene.write("    sio:SIO_000628 ordo:Orphanet_" + Orphanet_ID + ", ncbigene:" + NCBI_ID + " ;");bw_DiseaseGene.newLine();
			bw_DiseaseGene.write("    dcterms:source ");
			for(int i = 0; i < temp.split("\t").length; i++)
			{
				split = temp.split("\t");

				if(temp.split("\t").length - 1 == i)
				{
					if(split[i].equals("Orphanet"))
						bw_DiseaseGene.write("<http://www.orphadata.org/data/xml/en_product6.xml> .");
					else
						bw_DiseaseGene.write("<https://search.thegencc.org/download/action/submissions-export-csv> .");
					bw_DiseaseGene.newLine();
				}
				else
				{
					if(split[i].equals("Orphanet"))
						bw_DiseaseGene.write("<http://www.orphadata.org/data/xml/en_product6.xml>, ");
					else
						bw_DiseaseGene.write("<https://search.thegencc.org/download/action/submissions-export-csv>, ");
				}				
			}
		}
		bw_DiseaseGene.close();
	}
}
