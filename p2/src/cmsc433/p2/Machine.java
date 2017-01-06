package cmsc433.p2;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeS seconds to produce.
 */

public class Machine {

	// Types of machines used in Ratsie's.  Recall that enum types are
	// effectively "static" and "final", so each instance of Machine
	// will use the same MachineType.

	public enum MachineType { fountain, fryer, grillPress, oven };


	// Converts Machine instances into strings based on MachineType.

	public String toString() {
		switch (machineType) {
		case fountain: 		return "Fountain";
		case fryer:			return "Fryer";
		case grillPress:	return "Grill Presss";
		case oven:			return "Oven";
		default:			return "INVALID MACHINE";
		}
	}

	public final MachineType machineType;
	public final Food machineFoodType;
	private static int capacity;
	private int currentCapacity = 0;
	private Object foodLock = new Object();

	//YOUR CODE GOES HERE...


	/**
	 * The constructor takes at least the type of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(MachineType machineType, Food food, int capacityIn) {
		this.machineType = machineType;
		this.machineFoodType = food;
		capacity = capacityIn;

		//YOUR CODE GOES HERE...

	}

	public Machine(MachineType machineType, Food food){
		this.machineType = machineType;
		this.machineFoodType = food;
	}

	public void log(){
		Simulation.logEvent(SimulationEvent.machineStarting(this, this.machineFoodType, capacity));
	}

	public void logShutDown(){
		Simulation.logEvent(SimulationEvent.machineEnding(this));
	}


	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public Thread makeFood() throws InterruptedException {
		//YOUR CODE GOES HERE..
		Thread cookItem = new Thread(new CookAnItem(this.machineFoodType,  this));
		cookItem.start();

		return cookItem;
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		Food f;
		Machine m;


		public CookAnItem(Food f, Machine m){
			this.f = f;
			this.m = m;
		}

		public void run() {
			try {
				synchronized(foodLock){

					while(currentCapacity == capacity)
						foodLock.wait();
					currentCapacity++;
				}
					Simulation.logEvent(SimulationEvent.machineCookingFood(this.m, this.f));
					Thread.sleep(f.cookTimeS);
					currentCapacity--;
					synchronized(foodLock){
					foodLock.notify();
					}
					Simulation.logEvent(SimulationEvent.machineDoneFood(this.m, this.f));

				

			} catch(InterruptedException e) { }
		}
	}
}