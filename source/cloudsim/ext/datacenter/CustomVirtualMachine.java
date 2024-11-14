package cloudsim.ext.datacenter;

public class CustomVirtualMachine {
	private int vmId;
    private int memory; // in MB
    private long size; // in GB
    private long bandwidth; // in Mbps

    public CustomVirtualMachine(int vmId, int memory, long size, long bandwidth) {
        this.vmId = vmId;
        this.memory = memory;
        this.size = size;
        this.bandwidth = bandwidth;
    }

    // Allocate resources
    public boolean allocateResources(int memoryNeeded, int sizeNeeded, int bandwidthNeeded) {
//    	System.out.println("Memory Needed: "+ memoryNeeded + " size needed: " + sizeNeeded + " bandwidth Needed: "+ bandwidthNeeded);
//       	System.out.println("Memory avail: "+ memory + " size avail: " + size + " bandwidth avail: "+ bandwidth);

        if (memoryNeeded <= memory && sizeNeeded <= size && bandwidthNeeded <= bandwidth) {
            memory -= memoryNeeded;
            size -= sizeNeeded;
            bandwidth -= bandwidthNeeded;
//            System.out.println("After Allocation vmID "+ vmId +" ===> memory: "+ memory + " size: " + size + " bandwidth: " + bandwidth);
            return true; // Allocation successful
        }
        return false; // Not enough resources
    }

    // Free resources
    public void releaseResources(int memoryFreed, int sizeFreed, int bandwidthFreed) {
        memory += memoryFreed;
        size += sizeFreed;
        bandwidth += bandwidthFreed;
//        System.out.println("After Release vmID "+ vmId +" ===> memory: "+ memory + " size: " + size + " bandwidth: " + bandwidth);
    }

    // Getters for VM characteristics (optional)
    public int getVmId() { return vmId; }
    public int getMemory() { return memory; }
    public long getSize() { return size; }
    public long getBandwidth() { return bandwidth; }

}
