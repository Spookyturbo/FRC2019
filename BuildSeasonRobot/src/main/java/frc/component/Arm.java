/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.component;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import frc.robot.RobotMap;

import frc.util.Component;

//Implement component so that this can be included in the main loop
public class Arm implements Component{
    WPI_VictorSPX armMotor = new WPI_VictorSPX(RobotMap.Motors.armMotor);
    DigitalInput​ limitLower = new DigitalInput(RobotMap.armlimitLower);
    DigitalInput​ limitUpper = new DigitalInput(RobotMap.armlimitUpper);
    //Store a static instance and create it for the singleton pattern
    private static Arm instance = new Arm();

    double mSpeed;

    private Arm() {
        //Just here to remove the public constructor
    }

/*
public void print(String word){
System.out.print(word);
}
*/
    public void setSpeed(double speed) {
        mSpeed = speed;
        if (limitLower.get && mSpeed<0){
            mSpeed = 0;
        }
        if (limitUpper.get && mSpeed>0){
            mSpeed = 0;
        }
    }

    @Override
    public void execute() {

        armMotor.set(mSpeed);

        //Code ran every loop
    }

    public static Arm getInstance() {
        return instance;
    }
}