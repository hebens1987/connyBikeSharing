package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.bsQsim;

import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.router.util.LeastCostPathCalculator;

import eu.eunoiaproject.bikesharing.framework.scenarioBsAndBike.IKK_ObjectAttributesSingleton;

public class BikeSharingContext{
	public QSim getqSim(){
		return qSim;
	}

	public LeastCostPathCalculator getWalkPathCalculator(){
		return walkPathCalculator ;
	}

	public IKK_ObjectAttributesSingleton getInstance(){
		return instance;
	}

	static class Builder {
		private LeastCostPathCalculator standardBikePathCalculator;
		private LeastCostPathCalculator sharedBikePathCalculator;
		private QSim qSim;
		private LeastCostPathCalculator walkPathCalculator;
		private IKK_ObjectAttributesSingleton instance;

		Builder setStandardBikePathCalculator( LeastCostPathCalculator calc ) {
			standardBikePathCalculator = calc ;
			return this ;
		}
		Builder setSharedBikePathCalculator( LeastCostPathCalculator calc ) {
			sharedBikePathCalculator = calc ;
			return this ;
		}
		BikeSharingContext build() {
			return new BikeSharingContext( standardBikePathCalculator, sharedBikePathCalculator, qSim, walkPathCalculator, instance ) ;
		}

		Builder setQSim( QSim qSim ){
			this.qSim = qSim;
			return this ;
		}

		Builder setWalkPathCalculator( LeastCostPathCalculator routeAlgo ){
			this.walkPathCalculator = routeAlgo;
			return this ;
		}

		Builder setObjectAttributesSingleton( IKK_ObjectAttributesSingleton instance ){
			this.instance = instance;
			return this ;
		}
	}

	private LeastCostPathCalculator standardBikePathCalculator ;
	private LeastCostPathCalculator sharedBikePathCalculator ;
	private final QSim qSim;
	private final LeastCostPathCalculator walkPathCalculator;
	private final IKK_ObjectAttributesSingleton instance;

	private BikeSharingContext( LeastCostPathCalculator standardBikePathCalculator, LeastCostPathCalculator sharedBikePathCalculator, QSim qSim,
					    LeastCostPathCalculator walkPathCalculator, IKK_ObjectAttributesSingleton instance ){
		this.standardBikePathCalculator = standardBikePathCalculator;
		this.sharedBikePathCalculator = sharedBikePathCalculator;
		this.qSim = qSim;
		this.walkPathCalculator = walkPathCalculator;
		this.instance = instance;
	}

	public LeastCostPathCalculator getStandardBikePathCalculator(){
		return standardBikePathCalculator;
	}
	public LeastCostPathCalculator getSharedBikePathCalculator(){
		return sharedBikePathCalculator;
	}
}
