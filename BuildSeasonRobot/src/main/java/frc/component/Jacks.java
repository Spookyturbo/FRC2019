/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.component;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.RobotMap;
import frc.util.Component;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * Add your docs here.
 */
public class Jacks implements Component {

  WPI_VictorSPX frontJack = new WPI_VictorSPX(RobotMap.Motors.frontJack);
  WPI_VictorSPX rearJack = new WPI_VictorSPX(RobotMap.Motors.rearJack);

  DigitalInput frontUpperLimit = new DigitalInput(RobotMap.limitSwitches.frontjackUp);
  DigitalInput frontLowerLimit = new DigitalInput(RobotMap.limitSwitches.frontJackDown);

  DigitalInput rearUpperLimit = new DigitalInput(RobotMap.limitSwitches.rearJackUp);
  DigitalInput rearLowerLimit = new DigitalInput(RobotMap.limitSwitches.rearJackDown);

  // Store a static instance and create it for the singleton pattern
  private static Jacks instance;
  
  private boolean invertFrontMotor = false;
  private boolean invertRearMotor = false;

  private double frontSpeed;
  private double rearSpeed;

  public void setFrontSpeed(double speed) {
    if(speed < 0 && frontLowerLimit.get()) {
      speed = 0;
    }
    else if(speed > 0 && frontUpperLimit.get()) {
      speed = 0;
    }

    frontSpeed = speed;
  }

  public void setRearSpeed(double speed) {
    if(speed < 0 && rearLowerLimit.get()) {
      speed = 0;
    }
    else if(speed > 0 && rearUpperLimit.get()) {
      speed = 0;
    }

    rearSpeed = speed;
  }

  public void invertFrontJack(boolean state) {
    frontJack.setInverted(state);
  }

  public void invertRearJack(boolean state) {
    rearJack.setInverted(state);
  }

  private Jacks() {
    // Just here to remove the public constructor
  }

  @Override
  public void execute() {
    System.out.println("Front lower: " + frontLowerLimit.get() + " Front Upper: " + frontUpperLimit.get());
    frontJack.set(frontSpeed);
    rearJack.set(rearSpeed);

    // Code ran every loop
  }

  public static Jacks getInstance() {
    if(instance == null) {
      instance = new Jacks();
    }
    return instance;
  }

}
