package sophia;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.StoichiometryValueCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.interfaces.IContainerBuilder;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.writers.JSBMLWriter;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidationException;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.JSBMLValidator;
import pt.uminho.ceb.biosystems.mew.biocomponents.validation.io.jsbml.validators.JSBMLValidatorException;

public class modelConverter {
	
	public void convertBigg() throws JSBMLValidatorException, TransformerException, ParserConfigurationException, SAXException, IOException{
		
		try {
			String filePath1 = "C:\\Users\\Sophia Santos\\Dropbox\\DD-DeCaf\\Models\\Ec_iAF1260.xml"; //IMPORTANT: needs to be given by the user 

			JSBMLReader reader1 = new JSBMLReader(filePath1,"");
			
		
			
			Container c1 = new Container(reader1);
			
			InternalContainerBuilder builder = new InternalContainerBuilder();
			builder.cmpMap.put("default", new CompartmentCI("default", "default", null));
			
			//***METABOLITES		
			String m_id_1 = "EC";  //IMPORTANT: needs to be given by the user
			
			for (String cpdId : c1.getMetabolites().keySet()) {
				MetaboliteCI mci = c1.getMetabolite(cpdId);
				String id =  mci.getId();
				String newid =id.replace("M_", "M_"+m_id_1+"_");

				builder.spiMap.put(newid, new MetaboliteCI(id, mci.getId()));
				System.out.print(newid);
			}


////***REACTIONS	
//			
//			for (String rxnId : c1.getReactions().keySet()) {
//				ReactionCI rci = c1.getReaction(rxnId);
//				Map<String, StoichiometryValueCI> lhs = new HashMap<>();
//				Map<String, StoichiometryValueCI> rhs = new HashMap<>();
//				
//				Set<String> gene = rci.getGenesIDs();
//				String geneRule = rci.getGeneRuleString();
//				String ecNumber = rci.getEcNumber();	
//				
//				for (String l : rci.getReactants().keySet()) {
//					lhs.put(l, new StoichiometryValueCI(l, rci.getReactants().get(l).getStoichiometryValue(), "default"));
//				}
//				for (String r : rci.getProducts().keySet()) {
//					rhs.put( r, new StoichiometryValueCI( r, rci.getProducts().get(r).getStoichiometryValue(), "default"));
//				}
//				ReactionCI rciCopy = new ReactionCI(rci.getId(), rci.getName(), rci.isReversible(), lhs, rhs);
//				rciCopy.setGenesIDs(gene);
//				rciCopy.setGeneRule(geneRule);
//				rciCopy.setEc_number(ecNumber);
//				builder.rxnMap.put(rciCopy.getId(), rciCopy);
//			}

		
			Container newc = new Container(builder);
			JSBMLWriter writer = new JSBMLWriter("Newmodel_"+m_id_1, newc); 
			writer.writeToFile();
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
	
	