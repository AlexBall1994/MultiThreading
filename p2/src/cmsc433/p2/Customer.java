package cmsc433.p2;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the Ratsie's (only successful if the
 * Ratsie's has a free table), place its order, and then leave the
 * Ratsie's when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum; 
	private static int maxCapacity;
	private static int currentCapacity = 0;
	private static int runningCounter = 0;
	private static Object customerLock = new Object();
	private boolean orderReceived = false;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = runningCounter++;
	}

	public String toString() {
		return name;
	}

	public void setMaxCapacity(int i){
		this.maxCapacity = i;
	}
	
	public List<Food> getOrder(){
		return this.order;
	}
	
	public int getOrderNumber(){
		return this.orderNum;
	}
	
	public void orderReceived(){
		Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, this.orderNum));
		Simulation.logEvent(SimulationEvent.customerLeavingRatsies(this));
		orderReceived = true;
		currentCapacity--;
		synchronized(customerLock){
			customerLock.notifyAll();
			
		}
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the Ratsie's (only successful when the Ratsie's has a
	 * free table), place its order, and then leave the Ratsie's
	 * when the order is complete.
	 */
	public void run() {
		
		Simulation.logEvent(SimulationEvent.customerStarting(this));

		synchronized(customerLock){
			try {
				while(currentCapacity == maxCapacity)
					customerLock.wait();
				
				Cook c = new Cook(null);
				
				Simulation.logEvent(SimulationEvent.customerEnteredRatsies(this));
				currentCapacity++;
				Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
				c.placeOrder(this);
				
				customerLock.notify();
				
				while(!orderReceived)
					customerLock.wait();
				
			}
			catch (Exception e){

			}
		}		
	}
}