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
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Encoder;
import frc.util.Component;

//Implement component so that this can be included in the main loop
public class Arm implements Component, PIDOutput{
    WPI_VictorSPX armMotor = new WPI_VictorSPX(RobotMap.Motors.armMotor);
    DigitalInput limitLower = new DigitalInput(RobotMap.limitSwitches.armDown);
    DigitalInput limitUpper = new DigitalInput(RobotMap.limitSwitches.armUp);
    //Store a static instance and create it for the singleton pattern
    private static Arm instance;
    Encoder armEncoder = new Encoder(RobotMap.armEncoder1, RobotMap.armEncoder2);
    PIDController armPID = new PIDController(0, 0, 0, armEncoder, this);
    //Store a static instance and create it for the singleton pattern

    double mSpeed;

    private Arm() {
        armMotor.setInverted(true);
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

        if(instance == null) {
            instance = new Arm();
        }
        return instance;
    }


	@Override
	public void pidWrite(double output) {
		
	}

}