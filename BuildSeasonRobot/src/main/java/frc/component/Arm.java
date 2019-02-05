/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.component;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import frc.util.Component;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

//Implement component so that this can be included in the main loop
public class Arm implements Component, PIDOutput{
    WPI_VictorSPX armMotor = new WPI_VictorSPX(RobotMap.Motors.armMotor);
    DigitalInput limitLower = new DigitalInput(RobotMap.armLimitLower);
    DigitalInput limitUpper = new DigitalInput(RobotMap.armLimitUpper);
    Encoder armEncoder = new Encoder(RobotMap.armEncoder1, RobotMap.armEncoder2);
    PIDController armPID = new PIDController(0, 0, 0, armEncoder, this);
    //Store a static instance and create it for the singleton pattern
    private static Arm instance = new Arm();

    double mSpeed;

    private Arm() {
        armPID.setInputRange(-1000000000000f, 10000000000f);
        armPID.setOutputRange(-0.5, 0.5);
        armPID.setAbsoluteTolerance(10);
        armPID.setContinuous(false);
        armPID.setSetpoint(0);
        //Just here to remove the public constructor
    }

/*
public void print(String word){
System.out.print(word);
}
*/
    public void setSpeed(double speed) {
        mSpeed = speed;
        if (limitLower.get() && mSpeed<0){
            mSpeed = 0;
        }
        if (limitUpper.get() && mSpeed>0){
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

	@Override
	public void pidWrite(double output) {
		
	}
}