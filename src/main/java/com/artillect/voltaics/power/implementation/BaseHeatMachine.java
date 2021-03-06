package com.artillect.voltaics.power.implementation;

import com.artillect.voltaics.power.IEnergyConsumer;
import com.artillect.voltaics.power.IEnergyHolder;
import com.artillect.voltaics.power.IEnergyProducer;
import com.artillect.voltaics.power.IHeat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A basic Joule container that serves as a consumer, producer and holder. Custom
 * implementations do not need to use all three. The INBTSerializable interface is also
 * optional.
 */
public class BaseHeatMachine implements IHeat, IEnergyConsumer, IEnergyProducer, IEnergyHolder, INBTSerializable<NBTTagCompound> {
    
    /**
     * The amount of stored Joule power.
     */
    private long stored;
    
    /**
     * The maximum amount of Joule power that can be stored.
     */
    private long capacity;
    
    /**
     * The maximum amount of Joule power that can be accepted.
     */
    private long inputRate;
    
    /**
     * The maximum amount of Joule power that can be extracted
     */
    private long outputRate;
    
    private long temperature;
    
    private long meltingPoint;
    
    /**
     * Default constructor. Sets capacity to 5000 and transfer rate to 50. This constructor
     * will not set the amount of stored power. These values are arbitrary and should not be
     * taken as a base line for balancing.
     */
    public BaseHeatMachine() {
        
        this(5000, 50, 50, 70, 1200);
    }
    
    /**
     * Constructor for setting the basic values. Will not construct with any stored power.
     * 
     * @param capacity The maximum amount of Joule power that the container should hold.
     * @param input The maximum rate of power that can be accepted at a time.
     * @param output The maximum rate of power that can be extracted at a time.
     */
    public BaseHeatMachine(long capacity, long input, long output, long temperature, long meltingPoint) {
        
        this(0, capacity, input, output, temperature, meltingPoint);
    }
    
    /**
     * Constructor for setting all of the base values, including the stored power.
     * 
     * @param power The amount of stored power to initialize the container with.
     * @param capacity The maximum amount of Joule power that the container should hold.
     * @param input The maximum rate of power that can be accepted at a time.
     * @param output The maximum rate of power that can be extracted at a time.
     */
    public BaseHeatMachine(long power, long capacity, long input, long output, long temperature, long meltingPoint) {
        
        this.stored = power;
        this.capacity = capacity;
        this.inputRate = input;
        this.outputRate = output;
        this.temperature = temperature;
        this.meltingPoint = meltingPoint;
    }
    
    /**
     * Constructor for creating an instance directly from a compound tag. This expects that the
     * compound tag has some of the required data. @See {@link #deserializeNBT(NBTTagCompound)}
     * for precise info on what is expected. This constructor will only set the stored power if
     * it has been written on the compound tag.
     * 
     * @param dataTag The NBTCompoundTag to read the important data from.
     */
    public BaseHeatMachine(NBTTagCompound dataTag) {
        
        this.deserializeNBT(dataTag);
    }
    
    @Override
    public long getStoredPower () {
        
        return this.stored;
    }
    
    @Override
    public long givePower (long Joule, boolean simulated) {
        
        final long acceptedJoule = Math.min(this.getCapacity() - this.stored, Math.min(this.getInputRate(), Joule));
        
        if (!simulated)
            this.stored += acceptedJoule;
            
        return acceptedJoule;
    }
    
    @Override
    public long takePower (long Joule, boolean simulated) {
        
        final long removedPower = Math.min(this.stored, Math.min(this.getOutputRate(), Joule));
        
        if (!simulated)
            this.stored -= removedPower;
            
        return removedPower;
    }
    
    @Override
    public long getCapacity () {
        
        return this.capacity;
    }
    
    @Override
    public long getTemperature () {
    	return this.temperature;
    }
    
    @Override
    public long getMeltingPoint () {
    	return this.meltingPoint;
    }
    
    @Override
    public long giveHeat (long heat, boolean simulated) {
        final long acceptedHeat = 1;
        
        if (!simulated)
            this.temperature += acceptedHeat;
            
        return acceptedHeat;
    }
    
	@Override
	public long takeHeat(long heat, boolean simulated) {
		final long takenHeat = 1;
		
		if (!simulated)
			this.temperature -= takenHeat;
		return takenHeat;
	}
    
    @Override
    public NBTTagCompound serializeNBT () {
        
        final NBTTagCompound dataTag = new NBTTagCompound();
        dataTag.setLong("JoulePower", this.stored);
        dataTag.setLong("JouleCapacity", this.capacity);
        dataTag.setLong("JouleInput", this.inputRate);
        dataTag.setLong("JouleOutput", this.outputRate);
        dataTag.setLong("HeatTemperature", this.temperature);
        dataTag.setLong("HeatMeltingPoint", this.meltingPoint);
        
        return dataTag;
    }
    
    @Override
    public void deserializeNBT (NBTTagCompound nbt) {
        
        this.stored = nbt.getLong("JoulePower");
        
        if (nbt.hasKey("JouleCapacity"))
            this.capacity = nbt.getLong("JouleCapacity");
            
        if (nbt.hasKey("JouleInput"))
            this.inputRate = nbt.getLong("JouleInput");
            
        if (nbt.hasKey("JouleOutput"))
            this.outputRate = nbt.getLong("JouleOutput");
            
        if (this.stored > this.getCapacity())
            this.stored = this.getCapacity();
        
        if (nbt.hasKey("HeatTemperature"))
            this.temperature = nbt.getLong("HeatTemperature");
        
        if (nbt.hasKey("HeatMeltingPoint"))
            this.temperature = nbt.getLong("HeatMeltingPoint");
    }
    
    /**
     * Sets the capacity of the the container. If the existing stored power is more than the
     * new capacity, the stored power will be decreased to match the new capacity.
     * 
     * @param capacity The new capacity for the container.
     * @return The instance of the container being updated.
     */
    public BaseHeatMachine setCapacity (long capacity) {
        
        this.capacity = capacity;
        
        if (this.stored > capacity)
            this.stored = capacity;
            
        return this;
    }
    
    /**
     * Gets the maximum amount of Joule power that can be accepted by the container.
     * 
     * @return The amount of Joule power that can be accepted at any time.
     */
    public long getInputRate () {
        
        return this.inputRate;
    }
    
    /**
     * Sets the maximum amount of Joule power that can be accepted by the container.
     * 
     * @param rate The amount of Joule power to accept at a time.
     * @return The instance of the container being updated.
     */
    public BaseHeatMachine setInputRate (long rate) {
        
        this.inputRate = rate;
        return this;
    }
    
    /**
     * Gets the maximum amount of Joule power that can be pulled from the container.
     * 
     * @return The amount of Joule power that can be extracted at any time.
     */
    public long getOutputRate () {
        
        return this.outputRate;
    }
    
    /**
     * Sets the maximum amount of Joule power that can be pulled from the container.
     * 
     * @param rate The amount of Joule power that can be extracted.
     * @return The instance of the container being updated.
     */
    public BaseHeatMachine setOutputRate (long rate) {
        
        this.outputRate = rate;
        return this;
    }
    
    /**
     * Sets both the input and output rates of the container at the same time. Both rates will
     * be the same.
     * 
     * @param rate The input/output rate for the Joule container.
     * @return The instance of the container being updated.
     */
    public BaseHeatMachine setTransferRate (long rate) {
        
        this.setInputRate(rate);
        this.setOutputRate(rate);
        return this;
    }


}