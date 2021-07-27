package sophia;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.CompartmentCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.GeneCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.MetaboliteCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.components.ReactionConstraintCI;
import pt.uminho.ceb.biosystems.mew.biocomponents.container.interfaces.IContainerBuilder;

public class InternalContainerBuilder implements IContainerBuilder {
	
	private static final long serialVersionUID = 1L;
	
	public Map<String, CompartmentCI> cmpMap = new HashMap<String, CompartmentCI>();
	public Map<String, MetaboliteCI> spiMap = new HashMap<String, MetaboliteCI>();
	public Map<String, ReactionCI> rxnMap = new HashMap<String, ReactionCI>();
	public Map<String, ReactionConstraintCI> ctMap = new HashMap<String, ReactionConstraintCI>();
	public String biomass;
	
	public String getBiomassId() {
		return biomass;
	}

	public Map<String, CompartmentCI> getCompartments() {
//		for (String k : cmpMap.keySet()) {
//			CompartmentCI cci = cmpMap.get(k);
//			System.out.println(k + " -> " + cci.getId() + " ");
//		}
		return cmpMap;
	}
	
	public Map<String, MetaboliteCI> getMetabolites() {
//		for (String k : spiMap.keySet()) {
//			MetaboliteCI cci = spiMap.get(k);
//			System.out.println(k + " -> " + cci.getId() + " ");
//		}
		return spiMap;
	}
	
	public Map<String, ReactionCI> getReactions() {
//		for (String k : rxnMap.keySet()) {
//			ReactionCI cci = rxnMap.get(k);
//			System.out.println(k + " -> " + cci.getId() + " ");
//			System.out.println(k + " -> " + cci.getProducts().values() + " ");
//		}
		return rxnMap;
	}

	public Map<String, ReactionConstraintCI> getDefaultEC() {
		return ctMap;
	}

	public String getExternalCompartmentId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, GeneCI> getGenes() {
		// TODO Auto-generated method stub
		return null;
	}



	public Map<String, Map<String, String>> getMetabolitesExtraInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getModelName() {
		return "model";
	}

	public String getNotes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOrganismName() {
		return "no_name";
	}



	public Map<String, Map<String, String>> getReactionsExtraInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
