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

import java.util.ArrayList;

public class DiseaseHpoAssociation {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException{		 
		
		int b_number = 0;
		String line;
		String[] split;
		String OMIM_ID = null, HPO_ID = null, Orphanet_ID = null, temp = null;

		BufferedReader br_phenotype_Manual = new BufferedReader(new FileReader(args[0]));

		HashMap<String, String> OMIM_HPO_Manual_Map = new HashMap<String, String>();
		HashMap<String, String> Orphanet_Manual = new HashMap<String, String>();
		
		while((line = br_phenotype_Manual.readLine()) != null)
		{
			split = line.split("\t");

			if(split[0].split(":")[0].equals("OMIM"))
			{
				OMIM_ID = split[0].replace("OMIM:", "").replace(" ", "");
				HPO_ID = split[3].replace("HP:", "").replace(" ", "");

				if(OMIM_HPO_Manual_Map.get(OMIM_ID + "\t" + HPO_ID) == null)
				{
					OMIM_HPO_Manual_Map.put(OMIM_ID + "\t" + HPO_ID, "Manual");
				}
			}
			else if(split[0].split(":")[0].equals("ORPHA"))
			{
				Orphanet_ID = split[0].replace("ORPHA:", "");
				HPO_ID = split[3].replace("HP:", "");
	
				if(Orphanet_Manual.get(Orphanet_ID + "\t" + HPO_ID) == null)
				{
					Orphanet_Manual.put(Orphanet_ID + "\t" + HPO_ID, "Manual");
				}
			}
		}
		br_phenotype_Manual.close();

		
		BufferedWriter bw_OMIM_HP_Association = new BufferedWriter(new FileWriter("./OMIM_HP_Association.ttl"));
		
		bw_OMIM_HP_Association.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_OMIM_HP_Association.newLine();
		bw_OMIM_HP_Association.write("PREFIX foaf: <http://xmlns.com/foaf/0.1>");bw_OMIM_HP_Association.newLine();
		bw_OMIM_HP_Association.write("PREFIX mim: <http://identifiers.org/mim/>");bw_OMIM_HP_Association.newLine();
		bw_OMIM_HP_Association.write("PREFIX oa: <http://www.w3.org/ns/oa#>");bw_OMIM_HP_Association.newLine();
		bw_OMIM_HP_Association.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_OMIM_HP_Association.newLine();
		bw_OMIM_HP_Association.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_OMIM_HP_Association.newLine();	
		bw_OMIM_HP_Association.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_OMIM_HP_Association.newLine();
		
		for(String key : OMIM_HPO_Manual_Map.keySet())
		{
			OMIM_ID = key.split("\t")[0];
			HPO_ID = key.split("\t")[1];
			
			bw_OMIM_HP_Association.write("<https://pubcasefinder.dbcls.jp/phenotype_context/disease:OMIM:" + OMIM_ID + "/phenotype:HP:" + HPO_ID + ">");bw_OMIM_HP_Association.newLine();
			bw_OMIM_HP_Association.write("    a oa:Annotation ;");bw_OMIM_HP_Association.newLine();
			bw_OMIM_HP_Association.write("    oa:hasTarget mim:" + OMIM_ID + " ;");bw_OMIM_HP_Association.newLine();
			bw_OMIM_HP_Association.write("    oa:hasBody obo:HP_" + HPO_ID + " ;");bw_OMIM_HP_Association.newLine();			
			bw_OMIM_HP_Association.write("    dcterms:source _:b" + ++b_number + " ;");bw_OMIM_HP_Association.newLine();
			bw_OMIM_HP_Association.write("    obo:ECO_9000001 obo:ECO_0000218 .");bw_OMIM_HP_Association.newLine();			
			bw_OMIM_HP_Association.write("_:b" + b_number);bw_OMIM_HP_Association.newLine();
			bw_OMIM_HP_Association.write("    dcterms:creator \"Human Phenotype Ontology Consortium\" ;");bw_OMIM_HP_Association.newLine();
			bw_OMIM_HP_Association.write("    foaf:page <http://compbio.charite.de/jenkins/job/hpo.annotations.current/lastSuccessfulBuild/artifact/current/phenotype.hpoa> .");bw_OMIM_HP_Association.newLine();
		}
		bw_OMIM_HP_Association.close();
		
		
		// orphanet
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(args[1]);
		
		HashMap<String, String> Orphanet_frequency = new HashMap<String, String>();
		
		Element root = document.getDocumentElement();
		NodeList DisorderList = root.getElementsByTagName("HPODisorderSetStatusList");		
	    Element DisorderList_el = null;
    	NodeList Disorder = null;
    	NodeList OrphaNumber = null;
		Element OrphaNumber_el = null;
    	Element Name_el = null;

    	for(int i = 0; i < DisorderList.getLength(); i++)
	    {
	    	DisorderList_el = (Element) DisorderList.item(i);
	    	Disorder = DisorderList_el.getElementsByTagName("Disorder");

	    	for(int j = 0; j < Disorder.getLength(); j++)
	    	{
	    		Element Disorder_el = (Element) Disorder.item(j);
	    		OrphaNumber = Disorder_el.getElementsByTagName("OrphaCode");
	    		OrphaNumber_el = (Element) OrphaNumber.item(0);

	    		NodeList HPOId = Disorder_el.getElementsByTagName("HPOId");
	    		NodeList HPOFrequency = Disorder_el.getElementsByTagName("HPOFrequency");
	    		
	    		for(int k = 0; k < HPOId.getLength(); k++)
	    		{
	    			Element HPOId_el = (Element) HPOId.item(k);
	    			Element HPOFrequency_el = (Element) HPOFrequency.item(k);
	    			
	    			NodeList Name = HPOFrequency_el.getElementsByTagName("Name");	    		
		    		Element subItem = (Element) Name.item(0);
	                NodeList subElement1 = subItem.getChildNodes();
	                
	                if(Orphanet_frequency.get(OrphaNumber_el.getTextContent() + "\t" + HPOId_el.getTextContent().replace("HP:", "")) == null)
					{
						Orphanet_frequency.put(OrphaNumber_el.getTextContent() + "\t" + HPOId_el.getTextContent().replace("HP:", ""), subElement1.item(0).getNodeValue());
					}
	    		}
	    	}
	    }	
		
		BufferedWriter bw_Orphanet_HP_Association = new BufferedWriter(new FileWriter("./Orphanet_HP_Association.ttl"));
		
		bw_Orphanet_HP_Association.write("PREFIX dcterms: <http://purl.org/dc/terms/>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX foaf: <http://xmlns.com/foaf/0.1>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX hoom: <http://www.semanticweb.org/ontology/HOOM#>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX oa: <http://www.w3.org/ns/oa#>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX obo: <http://purl.obolibrary.org/obo/>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX ordo: <http://www.orpha.net/ORDO/>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");bw_Orphanet_HP_Association.newLine();
		
		for(String key : Orphanet_Manual.keySet())
		{
			Orphanet_ID = key.split("\t")[0];
			HPO_ID = key.split("\t")[1];
			
			bw_Orphanet_HP_Association.write("<https://pubcasefinder.dbcls.jp/phenotype_context/disease:ORDO:" + Orphanet_ID + "/phenotype:HP:" + HPO_ID + ">");bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("    a oa:Annotation ;");bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("    oa:hasTarget ordo:Orphanet_" + Orphanet_ID + " ;");bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("    oa:hasBody obo:HP_" + HPO_ID + " ;");bw_Orphanet_HP_Association.newLine();						

			if(Orphanet_frequency.get(key) != null)
			{
				temp = Orphanet_frequency.get(key);
				
				switch(temp) {
				case "Obligate (100%)":			bw_Orphanet_HP_Association.write("    hoom:with_frequency obo:HP_0040280 ;"); break;
				case "Very frequent (99-80%)":	bw_Orphanet_HP_Association.write("    hoom:with_frequency obo:HP_0040281 ;"); break;
				case "Frequent (79-30%)":		bw_Orphanet_HP_Association.write("    hoom:with_frequency obo:HP_0040282 ;"); break;
				case "Occasional (29-5%)":		bw_Orphanet_HP_Association.write("    hoom:with_frequency obo:HP_0040283 ;"); break;
				case "Very rare (<4-1%)":		bw_Orphanet_HP_Association.write("    hoom:with_frequency obo:HP_0040284 ;"); break;
				case "Excluded (0%)":			bw_Orphanet_HP_Association.write("    hoom:with_frequency obo:HP_0040285 ;"); break;
				}
				bw_Orphanet_HP_Association.newLine();
			}			
			bw_Orphanet_HP_Association.write("    dcterms:source _:b" + ++b_number + " ;");bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("    obo:ECO_9000001 obo:ECO_0000218 .");bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("_:b" + b_number);bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("    dcterms:creator \"Orphanet\" ;");bw_Orphanet_HP_Association.newLine();
			bw_Orphanet_HP_Association.write("    foaf:page <http://compbio.charite.de/jenkins/job/hpo.annotations.current/lastSuccessfulBuild/artifact/current/phenotype.hpoa> .");bw_Orphanet_HP_Association.newLine();
		}
		bw_Orphanet_HP_Association.write("obo:HP_0040280");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("    rdfs:label \"Obligate (100%)\"@en .");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("obo:HP_0040281");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("    rdfs:label \"Very frequent (99-80%)\"@en .");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("obo:HP_0040282");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("    rdfs:label \"Frequent (79-30%)\"@en .");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("obo:HP_0040283");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("    rdfs:label \"Occasional (29-5%)\"@en .");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("obo:HP_0040284");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("    rdfs:label \"Very rare (<4-1%)\"@en .");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("obo:HP_0040285");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.write("    rdfs:label \"Excluded (0%)\"@en .");bw_Orphanet_HP_Association.newLine();
		bw_Orphanet_HP_Association.close();
	}
}
