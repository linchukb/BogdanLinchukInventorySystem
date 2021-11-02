package model;

/**
 * This is describes the InHouse type of part derived from part. Adds a machineId field as one of the data members.
 * */
public class InHouse extends Part{
    private int machineID;

    public InHouse(int id, String name, double price, int stock, int min, int max, int machineID) {
        super(id, name, price, stock, min, max);
        this.machineID = machineID;
    }

    /**
     * Add description. What does it do.
     * @param machineID the machine id to set
     * */
    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    /**
     * @return machineID the machine id
     * */
    public int getMachineID() {
        return machineID;
    }

}
