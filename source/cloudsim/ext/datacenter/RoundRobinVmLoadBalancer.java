package cloudsim.ext.datacenter;

import java.util.Map;

import cloudsim.VirtualMachine;
import cloudsim.VirtualMachineList;
import cloudsim.ext.InternetCloudlet;

/**
 * This class implements {@link VmLoadBalancer} with a Round Robin policy.
 * 
 * @author Bhathiya Wickremasinghe
 *
 */
public class RoundRobinVmLoadBalancer extends VmLoadBalancer {
	
	private Map<Integer, VirtualMachineState> vmStatesList;
	private int currVm = -1;
	boolean once = true;

	public RoundRobinVmLoadBalancer(Map<Integer, VirtualMachineState> vmStatesList){
		super();
		
		this.vmStatesList = vmStatesList;
	}

	/* (non-Javadoc)
	 * @see cloudsim.ext.VMLoadBalancer#getVM()
	 */
	public int getNextAvailableVm(VirtualMachineList vmlist, InternetCloudlet cl){
		if(once) {
			for(int i = 0; i < vmStatesList.size(); i++) {
			VirtualMachine vm = (VirtualMachine)vmlist.get(i);

			System.out.println("VM# "+ i + " cpus: "+vm.getCpus() + " bandwidth: " + vm.getBw() +" memory: "+ vm.getMemory() +" size: " + vm.getSize());

		}
			once = false;
		}
		currVm++;
		
		if (currVm >= vmStatesList.size()){
			currVm = 0;
		}
		
		allocatedVm(currVm);
		
		return currVm;
		
	}
}
