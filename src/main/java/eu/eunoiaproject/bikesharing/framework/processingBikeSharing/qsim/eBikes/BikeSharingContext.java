package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.router.util.LeastCostPathCalculator;

public class BikeSharingContext{
	public QSim getqSim(){
		return qSim;
	}

	public LeastCostPathCalculator getWalkPathCalculator(){
		return walkPathCalculator ;
	}

	static class Builder {
		private LeastCostPathCalculator standardBikePathCalculator;
		private LeastCostPathCalculator sharedBikePathCalculator;
		private QSim qSim;
		private LeastCostPathCalculator walkPathCalculator;

		Builder setStandardBikePathCalculator( LeastCostPathCalculator calc ) {
			standardBikePathCalculator = calc ;
			return this ;
		}
		Builder setSharedBikePathCalculator( LeastCostPathCalculator calc ) {
			sharedBikePathCalculator = calc ;
			return this ;
		}
		BikeSharingContext build() {
			return new BikeSharingContext( standardBikePathCalculator, sharedBikePathCalculator, qSim, walkPathCalculator ) ;
		}

		Builder setQSim( QSim qSim ){
			this.qSim = qSim;
			return this ;
		}

		Builder setWalkPathCalculator( LeastCostPathCalculator routeAlgo ){
			this.walkPathCalculator = routeAlgo;
			return this ;
		}
	}

	private LeastCostPathCalculator standardBikePathCalculator ;
	private LeastCostPathCalculator sharedBikePathCalculator ;
	private final QSim qSim;
	private final LeastCostPathCalculator walkPathCalculator;

	private BikeSharingContext( LeastCostPathCalculator standardBikePathCalculator, LeastCostPathCalculator sharedBikePathCalculator, QSim qSim,
					    LeastCostPathCalculator walkPathCalculator ){
		this.standardBikePathCalculator = standardBikePathCalculator;
		this.sharedBikePathCalculator = sharedBikePathCalculator;
		this.qSim = qSim;
		this.walkPathCalculator = walkPathCalculator;
	}

	public LeastCostPathCalculator getStandardBikePathCalculator(){
		return standardBikePathCalculator;
	}
	public LeastCostPathCalculator getSharedBikePathCalculator(){
		return sharedBikePathCalculator;
	}
}
