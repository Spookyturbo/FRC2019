/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.component;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import frc.robot.RobotMap;
import frc.sensor.PIDControl;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import frc.util.Component;
import frc.util.Debug;

//Implement component so that this can be included in the main loop
public class Arm implements Component {
    private static Arm instance;

    // Control
    WPI_VictorSPX armMotor = new WPI_VictorSPX(RobotMap.Motors.armMotor);

    // Limit switches
    DigitalInput limitLower = new DigitalInput(RobotMap.limitSwitches.armDown);
    DigitalInput limitUpper = new DigitalInput(RobotMap.limitSwitches.armUp);

    // Feedback
    Encoder armEncoder = new Encoder(RobotMap.Encoders.armA, RobotMap.Encoders.armB, false, EncodingType.k4X);

    // Control Profiling
    PIDControl armPID = new PIDControl(0.02, 0.001, 0.001, 0);
    boolean closedLoop = false;
    double feedForward = 0.12f;

    // Encoder constants
    public static final double ARM_HIGH = 63.75f;
    public static final double ARM_MIDDLE = 37f;
    public static final double ARM_LOW = 11f;
    public static final double ARM_MIN = 0f;
    public static final double ARM_MAX = 70f;

    // Angle of arm at corresponding limit switches (Typical UNIT circle)
    public static final double ANGLE_MAX = 70f;
    public static final double ANGLE_MIN = -70f;

    //Motor speed
    double mSpeed;

    private Arm() {

        armEncoder.reset();
        armPID.initSmartDashboard("Arm");

        armMotor.setInverted(true);

        armPID.setInputRange(ARM_MIN, ARM_MAX);
        armPID.setOutputRange(-0.5, 0.5);         
        armPID.setSetpoint(ARM_MIN);
        armPID.setTolerance(1.5);
        // I can't contribute more then 0.2f speed in either direction
        //armPID.setMaxIContribution(0.2);
        // Total error won't start changing until less then 5 difference in error
        // between run loops
        armPID.setIKickInRate(5);
        // Just here to remove the public constructor
    }

    //Set the power of the motor. Scaled from -1 to 1 for -100% to 100% power
    public void setSpeed(double speed) {
        // Cant set speed if PID is enabled
        // This technically isn't necessary as PID speed is set in execute
        // But this gurantees calls to getSpeed() won't be incorrect based on when
        // called
        if (!closedLoop) {
            mSpeed = speed;
            if (limitLower.get() && mSpeed < 0) {
                mSpeed = 0;
            }
            if (limitUpper.get() && mSpeed > 0) {
                mSpeed = 0;
            }
        }
    }

    // Get the current set speed
    public double getSpeed() {
        return mSpeed;
    }

    // Ran at end of every control loop in Robot.java
    @Override
    public void execute() {
        //Lower limit switch will be the 0 position for the encoder
        if (limitLower.get()) {
            armEncoder.reset();
        }

        if(closedLoop) {
            //Get the degrees from the min that the arm is currently at and the adds the MIN
            double armAngle = getNormalizedEncoder() * (ANGLE_MAX - ANGLE_MIN) + ANGLE_MIN;
            //double currentFeedforward =  Math.copySign(feedForward, armPID.getError());
            //Use the PID loop using the current feedback device, as well as the feedforward term A*cos(theta)
            // if(Math.abs(mSpeed) > 0.2f) {
            //     armPID.acumulateError = false;
            // }
            // else {
            //     armPID.acumulateError = true;
            // }
            mSpeed = armPID.calculate(armEncoder.get(), feedForward);
        }

        armMotor.set(mSpeed);
    }

    //Set the arm to the high position for placing hatches on the rocket
    public void setHigh() {
        armPID.reset();
        setSetpoint(ARM_HIGH);
    }

    //Set the arm to the mid position for placing hatches on the rocket
    public void setMiddle() {
        armPID.reset();
        setSetpoint(ARM_MIDDLE);
    }

    //Set the arm to the low position for placing hatches on the rocket
    public void setLow() {
        armPID.reset();
        setSetpoint(ARM_LOW);
    }

    // Will move the arm down until it presses the limit switch it rezeroes
    public void setDown() {
        armPID.reset();
        // Still need to add moving down until zeroing and also that will need a timeout
        setSetpoint(ARM_MIN);
    }

    public void enablePID() {
        // SmartDashboard is handled in the new PIDControl class
        closedLoop = true;
        armPID.updateFromSmartDashboard("Arm");
        armPID.reset();
        SmartDashboard.putBoolean("PIDEnabled", true);
    }

    public void disablePID() {
        SmartDashboard.putBoolean("PIDEnabled", false);
        closedLoop = false;
    }

    // Sets the position in encoder units to go to with the arm
    public void setSetpoint(double setpoint) {
        armPID.setSetpoint(setpoint);
    }

    // Return the current setpoint
    public double getSetpoint() {
        return armPID.getSetpoint();
    }

    // Currently using PID or not
    public boolean isPIDEnabled() {
        return closedLoop;
    }

    //Returns the encoder scaled from 0-1 using the min and max height for it
    private double getNormalizedEncoder() {
        //Subtracts ARM_MIN to make this work even when the min is in the negatives
        return (armEncoder.get() - ARM_MIN) * 1 / (ARM_MAX - ARM_MIN);
    }

    // Zero the encoder
    public void resetEncoder() {
        armEncoder.reset();
    }

    public double getEncoder() {
        return armEncoder.get();
    }

    public void initDebug() {
        ShuffleboardTab tab = Debug.arm;

        armMotor.setName("Motors", "Arm");

        armEncoder.setName("Encoders", "Arm Height");

        limitLower.setName("Limit Switches", "Lower Limit");
        limitUpper.setName("Limit Switches", "Upper Limit");

        armPID.setName("PID", "ArmPID");

        tab.add(armMotor);
        tab.add(armEncoder);
        tab.add(limitLower)
            .withWidget(BuiltInWidgets.kBooleanBox);
        tab.add(limitUpper)
            .withWidget(BuiltInWidgets.kBooleanBox);
        tab.add(armPID)
            .withWidget(BuiltInWidgets.kPIDController);
    }

    // Handle the singleton instance
    public static Arm getInstance() {

        if (instance == null) {
            instance = new Arm();
        }
        return instance;
    }
}