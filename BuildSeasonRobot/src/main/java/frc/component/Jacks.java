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
public class Jacks implements Component{

    WPI_VictorSPX J1 = new WPI_VictorSPX(RobotMap.Motors.J1Drive);
    WPI_VictorSPX J2 = new WPI_VictorSPX(RobotMap.Motors.J2Drive);

    DigitalInput  UL = new DigitalInput(RobotMap.upperLimitSwitch);
    DigitalInput  LL = new DigitalInput(RobotMap.lowerLimitSwitch);

    //Store a static instance and create it for the singleton pattern
    private static Jacks instance = new Jacks();
    private double mSpeed;

    public void setSpeed(double speed) {
         mSpeed = speed;
    }
    
    private Jacks() {
        //Just here to remove the public constructor
    }

    @Override
    public void execute() {
      if (mSpeed < 0 && LL.get())  {
        mSpeed = 0;
  
      }
      if (mSpeed > 0 && UL.get())  {
        mSpeed = 0;
  
      }
      J1.set(mSpeed);
      J2.set(mSpeed);
      
      
        //Code ran every loop
    }
    public static Jacks getInstance() {
      return instance;
  }

}
