package eu.eunoiaproject.bikesharing.framework.processingBikeSharing.qsim.eBikes;

import eu.eunoiaproject.bikesharing.framework.processingBikeSharing.IKK_ObjectAttributesSingleton;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.router.TripRouter;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.pt.router.TransitRouterImpl;

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

	public TripRouter getTripRouter(){
		return tripRouter;
	}
	@Deprecated // should try to use trip router instead.  kai, apr'19
	public TransitRouterImpl getTransitRouter(){
		return transitRouter;
	}

	static class Builder {
		private LeastCostPathCalculator standardBikePathCalculator;
		private LeastCostPathCalculator sharedBikePathCalculator;
		private QSim qSim;
		private LeastCostPathCalculator walkPathCalculator;
		private IKK_ObjectAttributesSingleton instance;
		private TripRouter tripRouter;
		private TransitRouterImpl transitRouter;

		Builder setStandardBikePathCalculator( LeastCostPathCalculator calc ) {
			standardBikePathCalculator = calc ;
			return this ;
		}
		Builder setSharedBikePathCalculator( LeastCostPathCalculator calc ) {
			sharedBikePathCalculator = calc ;
			return this ;
		}
		BikeSharingContext build() {
			return new BikeSharingContext( standardBikePathCalculator, sharedBikePathCalculator, qSim, walkPathCalculator, instance, tripRouter, transitRouter ) ;
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

		void setTripRouter( TripRouter tripRouter ){
			this.tripRouter = tripRouter;
		}

		public void setTransitRouter( TransitRouterImpl transitRouter ){
			this.transitRouter = transitRouter;
		}
	}

	private LeastCostPathCalculator standardBikePathCalculator ;
	private LeastCostPathCalculator sharedBikePathCalculator ;
	private final QSim qSim;
	private final LeastCostPathCalculator walkPathCalculator;
	private final IKK_ObjectAttributesSingleton instance;
	private final TripRouter tripRouter;
	private final TransitRouterImpl transitRouter;

	private BikeSharingContext( LeastCostPathCalculator standardBikePathCalculator, LeastCostPathCalculator sharedBikePathCalculator, QSim qSim,
					    LeastCostPathCalculator walkPathCalculator, IKK_ObjectAttributesSingleton instance, TripRouter tripRouter,
					    TransitRouterImpl transitRouter ){
		this.standardBikePathCalculator = standardBikePathCalculator;
		this.sharedBikePathCalculator = sharedBikePathCalculator;
		this.qSim = qSim;
		this.walkPathCalculator = walkPathCalculator;
		this.instance = instance;
		this.tripRouter = tripRouter;
		this.transitRouter = transitRouter;
	}

	public LeastCostPathCalculator getStandardBikePathCalculator(){
		return standardBikePathCalculator;
	}
	public LeastCostPathCalculator getSharedBikePathCalculator(){
		return sharedBikePathCalculator;
	}
}
