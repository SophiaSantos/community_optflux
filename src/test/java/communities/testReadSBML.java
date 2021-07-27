package sophia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.StoichiometryValueCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.readers.JSBMLReader;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.io.writers.JSBMLWriter;


public class testReadSBML {

	public HashMap<String, String> table = new HashMap<>();

	// PATTERNS	

	Pattern pattterToIntegrateMetaboliteBigg = Pattern.compile("M_(.*)_.");
	Pattern pattterToIntegrateMetaboliteSeed = Pattern.compile("cpd(.*)_.");
	Pattern pattterToIntegrateMetaboliteExtra = Pattern.compile("M_(.*)");
	Pattern pattterToIntegrateMetaboliteKegg = Pattern.compile("C(.*)");
	
	//Models in SBML format
	Map<String, Container> modelPaths(Set<String> pathId) throws Exception{
		System.out.println(pathId);
		Map<String, Container> models = new HashMap <String, Container>();
		
		for (String sbmlId : pathId) {
			JSBMLReader reader = new JSBMLReader(sbmlId,"");
			Container cId = new Container(reader);
			
			models.put(sbmlId, cId);
		}
	
		return models;
	}
	
	
	//***MATCH PATTERNS
	Map<String, String> integrationReplacingIds(Container c, Set<String> mets){
		Map<String,String> ret = new HashMap<String, String>();

		for (String cpdId : mets) {
			MetaboliteCI mci = c.getMetabolite(cpdId);
			String id =  mci.getId();
			String id1 = id.replace("__", "_");
			
			Matcher bigg = pattterToIntegrateMetaboliteBigg.matcher(id1);
			Matcher seed = pattterToIntegrateMetaboliteSeed.matcher(id1);
			Matcher extra = pattterToIntegrateMetaboliteExtra.matcher(id1);
			Matcher kegg = pattterToIntegrateMetaboliteKegg.matcher(id1);
			
			String integratedId = null;
			if(bigg.matches()){
				integratedId = bigg.group(1);
			}
			else if(seed.matches()){
				integratedId = seed.group(1);
			}
			else if(extra.matches()){
				integratedId = extra.group(1);
			}
			else if(kegg.matches()){
				integratedId = kegg.group(1);
			}
			else
				integratedId = cpdId;

			ret.put(integratedId, cpdId);
		}


		return ret;
	}

	Map<String,Map<String,String>> integartionMetabolites(Collection<Container> models) throws Exception{

		Map<String,Map<String,String>> ret = new HashMap<String, Map<String,String>>();
		Map<String, Map<String, String>> modelsTometaboliets = new HashMap<String, Map<String,String>>();
		
		for (Container m : models){
			Container c = m.clone();
			Map<String, String> integrationM = integrationReplacingIds(c, c.getMetaboliteToDrain().keySet());
			modelsTometaboliets.put(c.getModelName(), integrationM);	
		}
		
		Set<String> allIntegratedIds = new HashSet<String>();
		for(Map<String,String> map : modelsTometaboliets.values()){
			allIntegratedIds.addAll(map.keySet());
		}

		for(String integratedId :allIntegratedIds){
			for(String modelId : modelsTometaboliets.keySet()){
				String metaboliteInModel = modelsTometaboliets.get(modelId).get(integratedId);
				
				if(metaboliteInModel != null){
					Map<String,String> modelMap =ret.get(integratedId);
					if(modelMap == null){
						modelMap = new HashMap<String, String>();
						ret.put(integratedId, modelMap);
					}
					modelMap.put(modelId, metaboliteInModel);
				}
			}
		}

		return ret;
	}

	@Test
	public void testIntegration() throws Exception{

		String basePath = "C:\\Users\\Sophia Santos\\PycharmProjects\\DD-DeCaF\\Tests\\examples\\models\\";
		
		List<String> modelId = new ArrayList<String>();
		modelId.add("Lactococcus_lactis_subsp_lactis_Il1403_IL1403.xml");
		modelId.add("iMM904_framed.xml");
		//modelId.add("e_coli.xml");
		//modelId.add("Lactobacillus_plantarum_WCFS1.xml");
		
		
		Set<String> paths = new HashSet<String>();
		
		for (String m : modelId){
			paths.add(basePath+m);
		}
		
	}

	@Test
	public void testBuilder() {

		try {
			String basePath = "C:\\Users\\Sophia Santos\\PycharmProjects\\DD-DeCaF\\Tests\\examples\\models\\";
			
			List<String> modelId = new ArrayList<String>();
			//modelId.add("Lactococcus_lactis_subsp_lactis_Il1403_IL1403.xml");
			modelId.add("iMM904.xml");
			modelId.add("e_coli.xml");
			//modelId.add("Lactobacillus_plantarum_WCFS1.xml");
			
			Set<String> paths = new HashSet<String>();
			
			for (String m : modelId){
				paths.add(basePath+m);
			}

			//***MODEL VARIABLES
			
			int upper = 1000; //upper bound value
			int lower = -1000; //lower bound value
			int upper_d = 10; //upper bound value
			int lower_d = -10; //lower bound value
			int zero = 0; //bound equal to 0
			
			String cc = "cc"; //community compartment
			
			String medium ="medium"; //external metabolites that are shared
			
			Map <String, String> extCompartment= new HashMap <String, String>();
			
			Map <String, String> biomassEq= new HashMap <String, String>();
			
			
			//***EXTERNAL METABOLITE INTEGRATION
			
			Map <String, Container> modelInfo = modelPaths (paths);
			
			Collection <Container> cont = modelInfo.values();
			
			Map<String,Map<String,String>> map = integartionMetabolites(cont);


			//***GENERAL COMPARTMENT			

			InternalContainerBuilder builder = new InternalContainerBuilder(); //Creation of the main compartment (container)
			builder.cmpMap.put(cc, new CompartmentCI(cc, cc, null)); // Internal general compartment named default
			MetaboliteCI M_BM_comm = new MetaboliteCI ("M_Growth_comm","Biomass community"); // New metabolite Biomass
			builder.spiMap.put(M_BM_comm.getId(), M_BM_comm); // Add biomass new metabolite do the metabolite Map
			
			Map<String,StoichiometryValueCI> reactants_BM = new HashMap<>();
			Map<String,StoichiometryValueCI> products_BM = new HashMap<>();
			
			HashMap <String, Float> biomassFactors = new HashMap <String, Float>();
			
			
			int len = modelInfo.size(); //Number of models
			
			double factor = 1d / len ;
			
			HashMap <Container, String> cNameMap = new HashMap <Container, String>();
			
			
			//***ORGANISMS COMPARTMENT TREATMENT
			for (Container c: modelInfo.values()){
				
				//System.out.println("Dead ends of "+ c.getModelName() + " model: " + c.identifyDeadEnds(true));
				
				int nModels = modelId.size();
				
				for (int i=0; i<=nModels-1;i++){ 
					if (c.getModelName() == null){
						String cName = modelId.get(i);
						cNameMap.put(c, cName);
					}
					else {
						String cName = c.getModelName();
						cNameMap.put(c, cName);
					}
				
				}
				
				//System.out.println(c.getBiomassId());
				
				for(String comp : c.getCompartments().keySet()){
					
					CompartmentCI cci = c.getCompartment(comp);
					String c_id =  (cci.getId()+"_"+ cNameMap.get(c)).replace("__", "_");
					builder.cmpMap.put(c_id, new CompartmentCI(c_id,c_id,null));
					extCompartment.put(cNameMap.get(c), c.getExternalCompartmentId());
					biomassEq.put(cNameMap.get(c), c.getBiomassId());
				}
				
				biomassFactors.put(cNameMap.get(c), (float) factor);
				
				
//			 	Metabolites	
				
				for (String cptId : c.getMetabolites().keySet()) { 
							
						MetaboliteCI mci = c.getMetabolite(cptId);
						String id = (mci.getId().replace("__", "_") + "_"+cNameMap.get(c)).replace("__", "_");
						String formula = mci.getFormula();
							
						MetaboliteCI m = new MetaboliteCI (id,id);
						m.setId(id);
						m.setFormula(formula);
						builder.spiMap.put(id,m);
						}
		
				
				
//			 	Reactions	
				
				for (String rxnId : c.getReactionsNotDrains().keySet()) {
					
					ReactionCI rci = c.getReaction(rxnId);
					String id = rci.getId().replace("__", "_");

					Map<String, StoichiometryValueCI> lhs = new HashMap<String, StoichiometryValueCI>();
					Map<String, StoichiometryValueCI> rhs = new HashMap<String, StoichiometryValueCI>();

					Set<String> gene = rci.getGenesIDs();
					String geneRule = rci.getGeneRuleString();
					String ecNumber = rci.getEcNumber();
					String pathway = rci.getSubsystem();

					for (String l : rci.getReactants().keySet()) {
						
						Set<String> comp = c.getMetaboliteCompartments(l);
						String comp_1 = comp.toString();
						String comp_11 = comp_1.substring(1, comp_1.length()-1).replace("__", "_");
						
						lhs.put((l+ "_"+cNameMap.get(c)).replace("__", "_") , new StoichiometryValueCI((l + "_"+cNameMap.get(c)).replace("__", "_") , rci.getReactants().get(l).getStoichiometryValue(), (comp_11+"_"+cNameMap.get(c)).replace("__", "_")));
					}
					for (String r : rci.getProducts().keySet()) {
						
						Set<String> comp = c.getMetaboliteCompartments(r);
						String comp_1 = comp.toString();
						String comp_11 = comp_1.substring(1, comp_1.length()-1);
						
						rhs.put((r + "_"+cNameMap.get(c)).replace("__", "_") , new StoichiometryValueCI((r + "_"+cNameMap.get(c)).replace("__", "_") , rci.getProducts().get(r).getStoichiometryValue(), (comp_11+"_"+cNameMap.get(c)).replace("__", "_")));
					}
					ReactionCI rciCopy = new ReactionCI((id+"_"+cNameMap.get(c)).replace("__", "_"), rci.getName(), rci.isReversible(), lhs, rhs);
					rciCopy.setGenesIDs(gene);
					rciCopy.setGeneRule(geneRule);
					rciCopy.setEc_number(ecNumber);
					rciCopy.setSubsystem(pathway);
					builder.rxnMap.put(rciCopy.getId(), rciCopy);
					
				}
				
//			 	Biomass Reaction
				
				CompartmentCI mainComp = builder.cmpMap.get(cc);
				
				MetaboliteCI M_BM = new MetaboliteCI ("M_Growth_"+cNameMap.get(c),"Biomass model_"+cNameMap.get(c));
				builder.spiMap.put(M_BM.getId(), M_BM);
				
				for (String metB: c.getReaction(c.getBiomassId()).getProducts().keySet()){
					double stoic = c.getMetaboliteStoichiometry(c.getBiomassId(), metB);
					
					Map<String, Set<String>> metToComp = new HashMap<String, Set<String>>();
					metToComp.put(metB, c.getMetaboliteCompartments(metB));
					
					if (stoic==1.0){
						M_BM.setId(metB);
					}
					
					else{
						builder.rxnMap.get(c.getBiomassId() + "_"+cNameMap.get(c)).getProducts().put(M_BM.getId(), new StoichiometryValueCI(M_BM.getId(), 1.0, mainComp.getId()));
					}

				}
				
				reactants_BM.put("M_Growth_"+cNameMap.get(c), new StoichiometryValueCI(M_BM.getId(), (double) biomassFactors.get(cNameMap.get(c)), mainComp.getId()));
				products_BM.put("M_Growth_comm", new StoichiometryValueCI(M_BM_comm.getId(), 1.0, mainComp.getId()));
				
				ReactionCI R_BM_Comm = new ReactionCI ("R_Community_Growth", "Community Biomass", false, reactants_BM, products_BM);

				builder.rxnMap.put(R_BM_Comm.getId(), R_BM_Comm);
				
				builder.biomass="R_Community_Growth";
			
			}
			
			Container newc = new Container(builder);
			

			//***DRAINS TREATMENT		

			
			
			CompartmentCI mainComp = builder.cmpMap.get(cc);
			Map<String, String> metsComp = new HashMap<String, String>();
			Map<String, String> metsCompShared = new HashMap<String, String>();
			Map<String, String> metsNew = new HashMap<String, String>();
			

			//	Comparison of Drains of model 1 with model 2	

			for(String metIntId :map.keySet()){

				Collection<String> metInt = map.get(metIntId).values();
				
				Object[] metShared = metInt.toArray();
				
				Collection<String> mName = cNameMap.values();
				
				Object[] mods = mName.toArray();
				
				
				
				// Biomass Drains
				
//				for (Container c : modelInfo.values()){
//					String BM_metabolite = newc.getMetabolite("M_Growth_"+cNameMap.get(c)).getId();
//					newc.constructDrain(BM_metabolite, mainComp.getId(), zero, upper);
//				}
				
				
				if(metInt.size()==1){
				
					for (int i = 0; i < mods.length; i++){
						
						
						if(newc.getMetabolite((metShared[0] + "_" + mods[i]).replaceAll("__", "_")) != null){
							String metD = ((String) metShared[0]+ "_" + mods[i]).replaceAll("__", "_");
							MetaboliteCI newmD = new MetaboliteCI(metD, metD);
							builder.spiMap.put(newmD.getId(), newmD);
							
							for (Container c : modelInfo.values()){
								if(c.getMetaboliteToDrain().get((String) metShared[0]) != null && mods[i] == c.getModelName()){
									//System.out.println("Metabolite: " + newmD.getId() + " Compartment: " + c.getExternalCompartmentId() + "_" + c.getModelName());
									metsComp.put(newmD.getId(), c.getExternalCompartmentId() + "_" + c.getModelName());
								}
							
							} 	
							
						}
						
					}
					
				}

				if(metInt.size()>=2){
					
					String newMS = (metShared[0]+"_"+medium).replaceAll("__", "_");
					MetaboliteCI newm = new MetaboliteCI(newMS, newMS);
					builder.spiMap.put(newm.getId(), newm);
					newc.constructDrain(newm.getId(), mainComp.getId(), lower, upper);
					
					for (int i = 0; i < mods.length; i++){
						
						String metS = (metShared[0] + "_" + mods[i]).replaceAll("__", "_");
						
						for (Container c : modelInfo.values()){
							metsCompShared.put(metS, c.getExternalCompartmentId() + "_" + mods[i]);
							metsNew.put(metS, newMS);
						}
						
					}
					
				}

			}
			
			//System.out.println("Mets External: "+  metsComp.keySet());
			
			for(String metDrain: metsComp.keySet()){
				newc.constructDrain(metDrain, metsComp.get(metDrain), lower, upper);
			}
			
			for (String metNew: metsCompShared.keySet()){
				
				if (metsNew.get(metNew) != null){
					
					Map<String,StoichiometryValueCI> reactantsMedium = new HashMap<>();
					Map<String,StoichiometryValueCI> productsMedium = new HashMap<>();
					
					reactantsMedium.put(metNew, new StoichiometryValueCI(metNew, 1.0, metsCompShared.get(metNew)));
					productsMedium.put(metsNew.get(metNew), new StoichiometryValueCI(metsNew.get(metNew), 1.0, cc));
					
					ReactionCI newR = new ReactionCI ("R_"+ metNew + "_medium", "R_"+ metNew + "_medium", true, reactantsMedium, productsMedium);
					//System.out.println("Reactants: "+  reactantsMedium + "Products: "+  productsMedium);
					//System.out.println("Reaction: "+  newR.getId());
					newc.addReaction(newR);
				}
				

			}

			newc.setBiomassId("R_Community_Growth");
			newc.constructDrain("M_Growth_comm", cc, zero, upper);
			
			
//			double v = EasySim.sim(newc, "R_BM_comm").getOFvalue();
//			System.out.println(v);

			JSBMLWriter writer = new JSBMLWriter(basePath +"Eco_Sce_mine.xml", newc); 
			writer.writeToFile();
			
			System.out.println("Dead ends: " + newc.identifyDeadEnds(true));
			
			System.out.println("Unique: " + newc.identifyIfHasUniqueMetaboliteIds());

			System.out.println("Drains: "+ newc.getDrains());
			System.out.println("Metabolites: "+ newc.getMetabolites().keySet());
			System.out.println("Reactions: "+ newc.getReactions().keySet());

			System.out.println("Biomass: "+newc.getBiomassId());
			
			System.out.println("Total metabolites: " + newc.getMetabolites().size());
			System.out.println("Total reactions: " + newc.getReactions().size());


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
