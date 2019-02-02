/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.component;

import frc.util.Component;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import frc.robot.RobotMap;

//Implement component so that this can be included in the main loop
public class Intake implements Component{
    WPI_VictorSPX intakeMotor = new WPI_VictorSPX(RobotMap.Motors.intake);
    double mSpeed;

    //Store a static instance and create it for the singleton pattern
public void setSpeed(double Speed){
    mSpeed = Speed;
} 


    private static Intake instance = new Intake();

    private Intake() {
        //Just here to remove the public constructor
    }

    @Override
    public void execute() {
        intakeMotor.set(mSpeed);
        //Code ran every loop
    }

    public static Intake getInstance() {
        return instance;
    }
}
