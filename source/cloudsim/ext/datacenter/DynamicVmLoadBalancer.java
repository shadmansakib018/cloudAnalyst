package cloudsim.ext.datacenter;

import java.util.Iterator;
import java.util.Map;

import cloudsim.VirtualMachine;
import cloudsim.VirtualMachineList;
import cloudsim.ext.Constants;
import cloudsim.ext.InternetCloudlet;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.event.CloudSimEvents;
import java.util.ArrayList;
import java.util.List;
/**
 * This class implements {@link VmLoadBalancer} as a Throttled load balancer. Each VM is
 * allocated only one task at a time and can be allocated another task only when the current
 * task has completed. The ThrottledVmLoadBalancer does not implement any task queueing 
 * functionality, but returns a valid VM id in the <code>getNextAvailableVm</code>
 * only if available. The calling {@link DatacenterController} should implement the task 
 * queueing locally.
 * 
 * The ThrottledVmLoadBalancer implements CloudSimEventListener to get notified of the VM's
 * being allocated and freed up by the {@link DatacenterController}
 * 
 * @author Bhathiya Wickremasinghe
 *
 */
public class DynamicVmLoadBalancer extends VmLoadBalancer implements CloudSimEventListener {
	
	private Map<Integer, VirtualMachineState> vmStatesList;
	private DatacenterController dcbLocal;
	boolean once = true;
	private List<CustomVirtualMachine> customList;
	
	/** 
	 * Constructor
	 * 
	 * @param dcb The {@link DatacenterController} using the load balancer.
	 */
	public DynamicVmLoadBalancer(DatacenterController dcb){
		this.vmStatesList = dcb.getVmStatesList();
		this.dcbLocal=dcb;
		dcb.addCloudSimEventListener(this);
		this.customList = new ArrayList<CustomVirtualMachine>();
	} 
 
	/**
	 * @return VM id of the first available VM from the vmStatesList in the calling
	 * 			{@link DatacenterController}
	 */
	@Override
	public int getNextAvailableVm(VirtualMachineList vmlist, InternetCloudlet cl){
		int vmId = 0;
		double maxMem = Double.NEGATIVE_INFINITY;
		System.out.println("data size: "+cl.getDataSize());
		
		if(once) {
			
			for(int i = 0; i < vmlist.size(); i++) {
			VirtualMachine vm = (VirtualMachine)vmlist.get(i);

//			System.out.println("*********VM# "+ i + " cpus: "+vm.getCpus() + " bandwidth: " + vm.getBw() +" memory: "+
//			vm.getMemory() +" size: " + vm.getSize());
			CustomVirtualMachine cVM = new CustomVirtualMachine(i,vm.getMemory(),vm.getSize(),vm.getBw());
			customList.add(cVM);
			int score = vm.getMemory() + (int)vm.getBw() + (int)vm.getSize();
//			System.out.println("FIRST RUN *********VM# "+ i + "Current Score: " + score);
			if(score >= maxMem) {
				vmId = i;
				maxMem = vm.getMemory() + (int)vm.getBw() + (int)vm.getSize();
			}
			
			
			}
			once = false;
			CustomVirtualMachine CVm = customList.get(vmId);
			boolean gotenoughresources = CVm.allocateResources(CVm.getMemory()/4,(int)cl.getDataSize(), (int)CVm.getBandwidth()/4);
			if(gotenoughresources) {
			allocatedVm(vmId);
			return vmId;
			}else {
				allocatedVm(-1);
				return -1;
			}
		}
		
		for(int i = 0; i < customList.size(); i++) {
			CustomVirtualMachine CVm = customList.get(i);
			int score = CVm.getMemory() + (int)CVm.getBandwidth() + (int)CVm.getSize();
			System.out.println("*********VM# "+ i + "Current Score: " + score);
			if(score >= maxMem) {
				vmId = i;
				maxMem = score;
//				System.out.println("!!!MAX FOUND!! Score:" + score);
			}	
		}
		System.out.println("!!!MAX FOUND!! Score:" + maxMem + " on VmId: " + vmId);
		CustomVirtualMachine CVm = customList.get(vmId);
		boolean gotenoughresources = CVm.allocateResources(CVm.getMemory()/4,(int)cl.getDataSize(), (int)CVm.getBandwidth()/4);
		if(gotenoughresources) {
			System.out.println("chosen vm: "+ vmId);
		allocatedVm(vmId);
		return vmId;
		}else {
			allocatedVm(-1);
			return -1;
		}
		
	}
	
	public void cloudSimEventFired(CloudSimEvent e) {
		if (e.getId() == CloudSimEvents.EVENT_CLOUDLET_ALLOCATED_TO_VM){
			int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
			vmStatesList.put(vmId, VirtualMachineState.BUSY);
			
			
			
		}
		else if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET){
			int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
			double DataSize = (double) e.getParameter("DATA_SIZE");
			
			CustomVirtualMachine CVm = customList.get(vmId);
			CVm.releaseResources(CVm.getMemory()*1/3,(int)DataSize, (int)CVm.getBandwidth()*1/3);
//			System.out.println("resources released: "+ DataSize + " for vmID: " + vmId);
			vmStatesList.put(vmId, VirtualMachineState.AVAILABLE);
			
			
		}
	}
	

}