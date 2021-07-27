package sophia;

import pt.uminho.ceb.biosystems.mew.biocomponents.container.Container;
import pt.uminho.ceb.biosystems.mew.core.model.converters.ContainerConverter;
import pt.uminho.ceb.biosystems.mew.core.model.steadystatemodel.ISteadyStateModel;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationProperties;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SimulationSteadyStateControlCenter;
import pt.uminho.ceb.biosystems.mew.core.simulation.components.SteadyStateSimulationResult;
import pt.uminho.ceb.biosystems.mew.solvers.SolverType;

public class EasySim {
	public static SteadyStateSimulationResult sim(Container ct, String rxn) {
		try {
			ISteadyStateModel ssm = ContainerConverter.convert(ct);
			SimulationSteadyStateControlCenter ssscs = 
					new SimulationSteadyStateControlCenter(null, null, ssm, SimulationProperties.FBA);
			ssscs.setSolver(SolverType.CPLEX3);
			ssscs.setMaximization(true);
			ssscs.setFBAObjSingleFlux(rxn, 1.0);
			SteadyStateSimulationResult sssr = ssscs.simulate();
			return sssr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}