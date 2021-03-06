package org.usfirst.frc.team4456.robot;

import org.usfirst.frc.team4456.robot.util.Util;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;

//TODO callibrate

/**
 * TOTES
 * Class for the winch on the hooks that pick up totes.
 * CONTROLS:
 * LB RB raise, lower
 * LT RT nudge
 * @author oom2013
 */
public class Aims
{
	/*
	 * Controls:
	 * LBumper, RBumper, LTrigger, RTrigger
	 */
	private CANTalon talon;
	private boolean leftBumperPress, rightBumperPress;
	private int currentTargetIndex = 0;
	
	/** 
	 * Constructor makes the motor for the winch and sets it up for use
	 * @param id
	 * @author oom2013
	 */
	public Aims(int id)
	{
		talon = new CANTalon(id);
		talon.changeControlMode(CANTalon.TalonControlMode.Position);
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		talon.setPID(.7, 0.000001, 0);
		talon.setPosition(0);
		talon.set(talon.get());  // don't move when started...
		//talon1.enableControl();
	}
	
	/**
	 * Gets current target index
	 * @return currentTargetIndex
	 * @author oom2013
	 */
	public int getCurrentTargetIndex()
	{
		return currentTargetIndex;
	}
	
	public void setIndex(int index)
	{
		talon.set(Constants.CANNON_POSITIONS[index]);
	}
	/**
	 * Takes inputs from the XBoxController and performs actions based on them
	 * @param controller
	 * @author oom2013
	 */
	public void cycle(XBoxController controller, Robot robot)
	{
		// Left bumper raises cannon by one level
		boolean rawLeftBumperState = controller.getLBumper();
		if(rawLeftBumperState && !leftBumperPress)
		{
			leftBumperPress = true;
			this.raiseCannon();
		}
		else if(!rawLeftBumperState && leftBumperPress)
		{
			leftBumperPress = false;
		}
		else
		{
			/*
			 * This will trigger if the bumperBPress == true and rawLeftBumperState == true
			 * Or if they're both false
			 */
		}
		
		// Right bumper lowers cannon by one level
		boolean rawRightBumperState = controller.getRBumper();
		if(rawRightBumperState && !rightBumperPress)
		{
			rightBumperPress = true;
			this.lowerCannon();
		}
		else if(!rawRightBumperState && rightBumperPress)
		{
			rightBumperPress = false;
		}
		else
		{
			/*
			 * This will trigger if the bumperBPress == true and rawLeftBumperState == true
			 * Or if they're both false
			 */
		}
		
		
		//NUDGE
		// Left and right triggers move the cannon down and up
		double talonSetValue = talon.getSetpoint() + (Constants.MAX_CANNON_NUDGE * controller.getAxisTriggers());
		if (robot.limitModeEnabled)
		{
			//will set limits if limitModeEnabled
			talonSetValue = Util.max(talonSetValue, Constants.CANNON_POSITIONS[0] - 400);
			talonSetValue = Util.min(talonSetValue, 1000);
		}
		talon.set(talonSetValue);
		
		//System.out.println("fwd:" + forwardNudge + " rev:" + reverseNudge);
	}
	
	/** 
	 * Returns what the current Cannon position is at
	 * 
	 * @author oom2013
	 */
	public double getCannonPosition()
	{
		return talon.get();
	}
	
	/**
	 *  Raises cannon to closest default cannon position above it 
	 *  unless the current position is above a certain threshold. 
	 *  If so, it goes to the next highest position.
	 *  @author oom2013
	 */
	private void lowerCannon()
	{
		int closestIndex = findClosestPosition();
		int targetIndex;
		if(closestIndex >= Constants.CANNON_POSITIONS.length-1)
		{
			targetIndex = Constants.CANNON_POSITIONS.length - 1;
		}
		else
		{
			targetIndex = closestIndex + 1;
		}
		talon.set(Constants.CANNON_POSITIONS[targetIndex]);
		this.currentTargetIndex = targetIndex;
	}
	
	/*
	/** 
	 * Raises cannon to the maximum position
	 * @author oom2013
	private void raiseHooksMax()
	{
		talon.set(Constants.CANNON_POSITIONS[Constants.CANNON_POSITIONS.length-1]);
		this.currentTargetIndex = Constants.CANNON_POSITIONS.length-1;
	}
	*/
	
	/**
	 *  Lowers cannon to closest default cannon position below it 
	 *  unless the current position is above a certain threshold. 
	 *  If so, it goes to the next lowest position.
	 *  @author oom2013
	 */
	private void raiseCannon()
	{
		int closestIndex = findClosestPosition();
		int targetIndex;
		if(closestIndex <= 0)
		{
			targetIndex = 0;
		}
		else
		{
			targetIndex = closestIndex-1;
		}
		talon.set(Constants.CANNON_POSITIONS[targetIndex]);
		this.currentTargetIndex = targetIndex;
	}
	
	/*
	/** 
	 * Lowers cannon to the minimum position
	 * @author oom2013
	private void lowerHooksMin()
	{
		talon.set(Constants.CANNON_POSITIONS[0]);
		this.currentTargetIndex = 0;
	}
	*/
	
	/** 
	 * @return Closest default position to the current position
	 * @author oom2013
	 */
	private int findClosestPosition()
	{
		double currentPos = talon.get();
		double closestDistance = 0;
		int closestIndex = 0;
		double highestPos = Constants.CANNON_POSITIONS[Constants.CANNON_POSITIONS.length-1];
		double lowestPos = Constants.CANNON_POSITIONS[0];
		if(currentPos > highestPos)
		{
			return Constants.CANNON_POSITIONS.length-1;
		}
		else if(currentPos < lowestPos)
		{
			return 0;
		}
		else
		{
			for(int i = 0; i < Constants.CANNON_POSITIONS.length; i++)
			{
				double distance = Math.abs(currentPos - Constants.CANNON_POSITIONS[i]);
				if(distance < closestDistance || i == 0)
				{
					closestDistance = distance;
					closestIndex = i;
				}
			}
		}
		return closestIndex;
	}
}