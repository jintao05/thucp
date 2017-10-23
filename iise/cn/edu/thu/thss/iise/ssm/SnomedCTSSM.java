/**
 *
 */
package cn.edu.thu.thss.iise.ssm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.openrdf.query.algebra.evaluation.function.string.UpperCase;

import cn.edu.thu.thss.iise.ssm.snomedctindex.LabelLuceneIndex;
import cn.edu.thu.thss.iise.ssm.snomedctindex.SimilarLabelQueryResult;
import cn.edu.thu.thss.iise.youdaodict.YoudaoDict;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.metrics.ic.utils.IcUtils;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;

/**
 * given two english term, first look them up in snomed ct terms, and then
 * compute their semantic similarity based on intrinsic informmation content
 *
 * @author Tao Jin
 *
 */
public class SnomedCTSSM {
	private String SNOMEDCT_DIR = "./datalib/SnomedCT_RF2Release_US1000124_20150901/Snapshot/Terminology";
	private String SNOMEDCT_CONCEPT = SNOMEDCT_DIR
			+ "/sct2_Concept_Snapshot_US1000124_20150901.txt";
	private String SNOMEDCT_RELATIONSHIPS = SNOMEDCT_DIR
			+ "/sct2_Relationship_Snapshot_US1000124_20150901.txt";
	private String SNOMEDCT_DESCRIPTION = SNOMEDCT_DIR
			+ "/sct2_Description_Snapshot-en_US1000124_20150901.txt";
	private URIFactory factory = URIFactoryMemory.getSingleton();
	private URI snomedctURI = factory.getURI("http://snomedct/");
	private G g = new GraphMemory(snomedctURI);
	private GDataConf conf = new GDataConf(GFormat.SNOMED_CT_RF2);
	// First we configure an intrincic IC
	private ICconf icConf = null;
	// Then we configure the pairwise measure to use, we here choose to use Lin
	// formula
	private SMconf smConf = null;
	// We define the engine used to compute the similarity
	private SM_Engine engine = null;

	private HashMap<String, String> term2cid = new HashMap<String, String>();
	private HashMap<String, String> cid2term = new HashMap<String, String>();
	private ArrayList<String> globalTermArray = new ArrayList<String>();

	// use lucene index for snomed ct concept
	private LabelLuceneIndex conceptIndex = new LabelLuceneIndex("snomedIndex");

	// description file column
	private final int c_id = 0;
	private final int c_effectiveTime = 1;
	private final int c_active = 2;
	private final int c_moduleId = 3;
	private final int c_conceptId = 4;
	private final int c_languageCode = 5;
	private final int c_typeId = 6;
	private final int c_term = 7;
	private final int c_caseSignificanceId = 8;

	private volatile static SnomedCTSSM snomedCTSSM;

	private void readDescription() throws Exception {
		System.out.println("loading snomed ct description ......");
		Pattern p_tab = Pattern.compile("\\t");
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		FileInputStream fstream;
        try {
            fstream = new FileInputStream(SNOMEDCT_DESCRIPTION);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            String[] data;
            boolean header = true;

            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                data = p_tab.split(line);
                //Date date = formatter.parse(data[effectiveTime]);
                String typeid = data[c_typeId];
                String cid = data[c_conceptId];
    			String term = data[c_term];
    			// for each cid, store the FSN
    			String termOld = cid2term.get(cid);
    			if (termOld == null) {
    				cid2term.put(cid, term);
    			} else {
    				if (typeid.equals("900000000000003001")) {
            			cid2term.put(cid, term);
                    }
    			}
                // for each term, store the cid with the most big IC
                String cidOld = term2cid.get(term);
                if (cidOld == null) {
                	term2cid.put(term, cid);
                } else {
                	URI uriOld = factory.getURI(snomedctURI.stringValue() + cidOld);
            		URI uriNew = factory.getURI(snomedctURI.stringValue() + cid);
            		Double icOld = engine.getIC_results(icConf).get(uriOld);
            		Double icNew = engine.getIC_results(icConf).get(uriNew);
            		if (icOld != null && icNew != null) {
            			if (icNew > icOld) {
            				term2cid.put(term, cid);
            			}
            		}
                }
            }
            globalTermArray.addAll(term2cid.keySet());
            if (!conceptIndex.open()) {
            	conceptIndex.create();
            	for (String str : globalTermArray) {
            		conceptIndex.addLabel(str);
            	}
            	conceptIndex.open();
            	System.out.println("snomed ct index has been build and opened");
            } else {
            	System.out.println("snomed ct index has been opened");
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		//System.out.println(term2cid.size());
		//System.out.println(cid2term.size());
	}

	public SnomedCTSSM() {
		System.out.println("loading snomed ct ......");
		conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE,
				SNOMEDCT_CONCEPT);
		conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE,
				SNOMEDCT_RELATIONSHIPS);
		try {
			// construct engine for semantic similarity measure
			GraphLoaderGeneric.populate(conf, g);
			icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_HARISPE_2012);
			smConf = new SMconf(
					SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);
			engine = new SM_Engine(g);
			engine.computeIC(icConf);

			// read description file to set up map betweeen cid and term
			// after engine is build and IC is computed, since engine will be used
			readDescription();
			System.out.println("snomed ct loaded");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SnomedCTSSM getSnomedCTSSM() {
		if (snomedCTSSM == null) {
			synchronized (SnomedCTSSM.class) {
				if (snomedCTSSM == null) {
					snomedCTSSM = new SnomedCTSSM();
				}
			}
		}
		return snomedCTSSM;
	}

	public String getMostSimilarConcept(String phrase, float simThreshold) {
		TreeSet<SimilarLabelQueryResult> qResults = conceptIndex.getSimilarLabels(phrase, simThreshold);
		if (qResults.size() == 0) {
			return "";
		}
		SimilarLabelQueryResult qResult = qResults.last();
		String result = qResult.getLabel();
		if (result == null) {
			result = "";
		}
		return result;
	}

	public String getConceptId(String concept) {
		return term2cid.get(concept);
	}

	private URI getMICA(URI uri1, URI uri2) {
		try {
			return IcUtils.searchMICA(uri1, uri2, engine.getAncestorsInc(uri1), engine.getAncestorsInc(uri2), engine.getIC_results(icConf));
		} catch (SLIB_Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getCidOfMICA(String cid1, String cid2) {
		URI URI1 = factory.getURI(snomedctURI.stringValue() + cid1);
		URI URI2 = factory.getURI(snomedctURI.stringValue() + cid2);
		URI uriMica = getMICA(URI1, URI2);
		return uriMica.stringValue().substring(snomedctURI.stringValue().length());
	}

	public String getTermOfMICA(String term1, String term2) {
		String cid1 = term2cid.get(term1);
		String cid2 = term2cid.get(term2);
		if (cid1 == null || cid2 == null) {
			return null;
		}
		String cidMica = getCidOfMICA(cid1, cid2);
		return cid2term.get(cidMica);
	}

	public double getSimilarity(String term1, String term2) {
		String cid1 = term2cid.get(term1);
		String cid2 = term2cid.get(term2);
//		System.out.println(cid1 + "   /    " +cid2);
		if (cid1 == null || cid2 == null) {
			return 0;
		}
		URI URI1 = factory.getURI(snomedctURI.stringValue() + cid1);
		URI URI2 = factory.getURI(snomedctURI.stringValue() + cid2);
		double sim = 0;
		try {
			if(engine.getClasses().contains(URI1) && engine.getClasses().contains(URI2) )
				sim = engine.compare(smConf, URI1, URI2);
		} catch (SLIB_Ex_Critic e) {
			e.printStackTrace();
		}
		return sim;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws SLIB_Exception {
		// We compute the similarity between the concepts
		// associated to Heart and Myocardium, i.e. 80891009 and 74281007
		// respectively
		SnomedCTSSM ssm = new SnomedCTSSM();
		YoudaoDict translator = new YoudaoDict();
		String term1 = "��Ⱥ��ƽ";
		String term2 = "������ƽ";

		String term3 = translator.chinese2english(term1);
		String term4 = translator.chinese2english(term2);
		term3 = seqClean(term3);
		term4 = seqClean(term4);
		System.out.println(term1+"=>"+term3);//pantoprazole
		System.out.println(term2+"=>"+term4);//lansoprazole
//		System.out.println(term3);//pantoprazole
//		System.out.println(term4);//lansoprazole
		System.out.println("Similarity 	: " + ssm.getSimilarity(term3, term4));

		term1 = "ͷ������";
		term2 = "ͷ������";
		term3 = translator.chinese2english(term1);
		term4 = translator.chinese2english(term2);
		term3 = seqClean(term3);
		term4 = seqClean(term4);
		System.out.println(term1+"=>"+term3);//pantoprazole
		System.out.println(term2+"=>"+term4);//lansoprazole
		System.out.println("Similarity 	: " + ssm.getSimilarity(term3, term4));
//		System.out.println("Similarity 	: " + ssm.getSimilarity(term5, term6));
//		System.out.println("the MICA of Heart/Myocardium: " + ssm.getTermOfMICA(term1, term2));
	}

	public static String seqClean(String str){
		return upperCase(delParentheses(delbracket(str))).replaceAll("[^a-z^A-Z^0-9^\\s]", "").trim();
	}

	public static String delbracket(String str) {
		int index1,index2;
		index1 = str.indexOf('[');
		index2 = str.indexOf(']');
		if (index1 == -1 || index2 == -1) {
			return str;
		}
		else return str.substring(0,index1)+str.substring(index2+2);

	}
	public static String delParentheses(String str) {
		int index1,index2;
		index1 = str.indexOf('(');
		index2 = str.indexOf(')');
		if (index1 == -1 || index2 == -1) {
			return str;
		}
		else return str.substring(0,index1)+str.substring(index2+2);

	}

	public static String upperCase(String str) {
	    return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

}
