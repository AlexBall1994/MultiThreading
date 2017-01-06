package cmsc433.p2;

import java.util.ArrayList;
import java.util.List;

import cmsc433.p2.Machine.MachineType;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private static Machine fryer = new Machine(MachineType.fryer, FoodType.wings);
	private static	Machine fountain = new Machine(MachineType.fountain, FoodType.soda);
	private static	Machine oven = new Machine(MachineType.oven, FoodType.pizza);
	private static	Machine grillPress = new Machine(MachineType.grillPress, FoodType.sub);
	private static List<Customer> customerList = new ArrayList<Customer>();
	private static Object cookLock = new Object();
	/**
	 * You can feel free to modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public void logMachine(){
		fryer.log();
		fountain.log();
		oven.log();
		grillPress.log();
	}

	public void logMachineShutDown(){
		fryer.logShutDown();
		fountain.logShutDown();
		oven.logShutDown();
		grillPress.logShutDown();
	}

	public void placeOrder(Customer c){
		synchronized(cookLock){
			customerList.add(c);
			cookLock.notify();

		}
	}

	public Customer getOrder() throws InterruptedException{
		synchronized(cookLock){
			while(customerList.isEmpty())
				cookLock.wait();
			Customer c = customerList.get(0);
			customerList.remove(0);
			return c;
		}
	}

	public void process(Customer c) throws InterruptedException{
		Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, c.getOrder(), c.getOrderNumber()));
		List<Food> order = c.getOrder();
		Thread[] orderThreads = new Thread[order.size()];

		for(int i = 0; i < order.size(); i++){

			Simulation.logEvent(SimulationEvent.cookStartedFood(this, order.get(i), c.getOrderNumber()));
			if (order.get(i).equals(FoodType.pizza))
				orderThreads[i] = oven.makeFood();
			else if (order.get(i).equals(FoodType.sub))
				orderThreads[i] = grillPress.makeFood();
			else if (order.get(i).equals(FoodType.soda))
				orderThreads[i] = fountain.makeFood();
			else if (order.get(i).equals(FoodType.wings))
				orderThreads[i] = fryer.makeFood();
		}

		int i = 0;
		while(i < order.size()){

			if (!orderThreads[i].isAlive()){
				Simulation.logEvent(SimulationEvent.cookFinishedFood(this, order.get(i), c.getOrderNumber()));
				i++;
			}
		}
		Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, c.getOrderNumber()));
		c.orderReceived();
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {
		Simulation.logEvent(SimulationEvent.cookStarting(this));

		try {
			while(true) {

				Customer currentOrder = getOrder();
				process(currentOrder);
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}