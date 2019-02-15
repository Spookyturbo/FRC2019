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
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import frc.util.Component;

//Implement component so that this can be included in the main loop
public class Arm implements Component, PIDOutput {
    WPI_VictorSPX armMotor = new WPI_VictorSPX(RobotMap.Motors.armMotor);
    DigitalInput limitLower = new DigitalInput(RobotMap.limitSwitches.armDown);
    DigitalInput limitUpper = new DigitalInput(RobotMap.limitSwitches.armUp);
    // Store a static instance and create it for the singleton pattern
    private static Arm instance;
    Encoder armEncoder = new Encoder(RobotMap.Encoders.armA, RobotMap.Encoders.armB, false, EncodingType.k4X);
    PIDController armPID = new PIDController(0.03, 0.001, 0.1, armEncoder, this);

    public static final double ARM_HIGH = 63.75f;
    public static final double ARM_MIDDLE = 37f;
    public static final double ARM_LOW = 11f;
    // Store a static instance and create it for the singleton pattern

    double mSpeed;

    private Arm() {
        armEncoder.reset();

        armEncoder.setName("Encoder", "Arm");
        armPID.setName("Gyro", "ArmPID");

        SmartDashboard.putNumber("armP", armPID.getP());
        SmartDashboard.putNumber("armI", armPID.getI());
        SmartDashboard.putNumber("armD", armPID.getD());

        LiveWindow.add(armPID);
        LiveWindow.add(armEncoder);
        armMotor.setName("Encoder", "ArmMotor");
        LiveWindow.add(armMotor);

        armMotor.setInverted(true);
        armPID.setInputRange(0, 100);
        armPID.setOutputRange(-0.5, 0.5);
        armPID.setContinuous(false);
        armPID.setSetpoint(0);
        armPID.setAbsoluteTolerance(1.5);
        // Just here to remove the public constructor
    }

    /*
     * public void print(String word){ System.out.print(word); }
     */
    public void setSpeed(double speed) {
        //Cant set speed if PID is enabled
        if (!armPID.isEnabled()) {
            mSpeed = speed;
            if (limitLower.get() && mSpeed < 0) {
                mSpeed = 0;
            }
            if (limitUpper.get() && mSpeed > 0) {
                mSpeed = 0;
            }
        }
    }

    //Get the current set speed
    public double getSpeed() {
        return mSpeed;
    }

    @Override
    public void execute() {
        System.out.println("Speed of arm: " + mSpeed);
        System.out.println("Arm encoder: " + armEncoder.get());
        System.out.println("At setpoint: " + armPID.onTarget());
        if(armPID.onTarget()) {
            armPID.setI(0);
        }
        else {
            armPID.setI(0.001);
        }
        // Lower limit switch will be the 0 position for the encoder
        if (limitLower.get()) {
            armEncoder.reset();
        }

        armMotor.set(mSpeed);

        // Code ran every loop
    }

    public static Arm getInstance() {

        if (instance == null) {
            instance = new Arm();
        }
        return instance;
    }

    public void setHigh() {
        setSetpoint(ARM_HIGH);
    }

    public void setMiddle() {
        setSetpoint(ARM_MIDDLE);
    }

    public void setLow() {
        setSetpoint(ARM_LOW);
    }

    public void setDown() {
        setSetpoint(0);
    }

    @Override
    public void pidWrite(double output) {
        mSpeed = output;
    }

    public void enablePID() {
        armPID.setP(SmartDashboard.getNumber("armP", armPID.getP()));
        armPID.setI(SmartDashboard.getNumber("armI", armPID.getI()));
        armPID.setD(SmartDashboard.getNumber("armD", armPID.getD()));
        armPID.enable();
    }

    public void disablePID() {
        armPID.disable();
    }

    public void setSetpoint(double setpoint) {
        armPID.setSetpoint(setpoint);
    }

    public double getSetpoint() {
        return armPID.getSetpoint();
    }

    public boolean isPIDEnabled() {
        return armPID.isEnabled();
    }

    public void resetEncoder() {
        armEncoder.reset();
    }
}