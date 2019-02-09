/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.component;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.robot.RobotMap;
import frc.util.Component;

/**
 * Used for driving
 * Inlcudes both singular side tank drive as well
 * as mecanum
 */
public class Drive implements Component {
    private static Drive instance;

    private WPI_VictorSPX FL;
    private WPI_VictorSPX BL;
    private WPI_VictorSPX FR;
    private WPI_VictorSPX BR;

    private MecanumDrive mecanumDrive;
    //private DifferentialDrive tankDrive;

    private SpeedControllerGroup leftMotors;
    private SpeedControllerGroup rightMotors;

    private boolean mecanum = false;
    private boolean invertX = false;

    private double ySpeed, xSpeed, rotate;

    private Drive() {
        FL = new WPI_VictorSPX(RobotMap.Motors.FLDrive);
        BL = new WPI_VictorSPX(RobotMap.Motors.BLDrive);
        FR = new WPI_VictorSPX(RobotMap.Motors.FRDrive);
        BR = new WPI_VictorSPX(RobotMap.Motors.BRDrive);

        leftMotors = new SpeedControllerGroup(FL, BL);
        rightMotors = new SpeedControllerGroup(FR, BR);
        rightMotors.setInverted(true);

        mecanumDrive = new MecanumDrive(FL, BL, FR, BR);
        //tankDrive = new DifferentialDrive(leftMotors, rightMotors);
    }

    //Mecanum Drive
    public void driveCartesian(double ySpeed, double xSpeed, double rotate) {
        mecanum = true;
        this.ySpeed = ySpeed;
        this.xSpeed = (invertX) ? -xSpeed : xSpeed;
        this.rotate = rotate;
    }

    //Tank Drive
    public void tankDrive(double leftSpeed, double rightSpeed) {
        mecanum = false;
        xSpeed = leftSpeed;
        ySpeed = rightSpeed;
    }

    //Ran at the end of teleopPeriodic and autonomousPeriodic
    @Override
    public void execute() {
        if(mecanum) {
            mecanumDrive.driveCartesian(ySpeed, xSpeed, rotate);
            
        }
        else {
            //X = left Y = right
            //tankDrive.tankDrive(xSpeed, ySpeed);
        }
    }

    public void invertX(boolean b) {
        invertX = b;
    }

    //Debug the groups and individual motors
    public void debugMotors() {
        leftMotors.setName("DriveMotor", "Left Motors");
        rightMotors.setName("DriveMotor", "Right Motors");
        FL.setName("DriveMotor", "Front Left");
        BL.setName("DriveMotor", "Back Left");
        FR.setName("DriveMotor", "Front Right");
        BR.setName("DriveMotor", "Back Right");

        LiveWindow.add(leftMotors);
        LiveWindow.add(rightMotors);

        LiveWindow.add(FL);
        LiveWindow.add(BL);
        LiveWindow.add(FR);
        LiveWindow.add(BR);
    }

    //Debug the mecanum drive
    public void debugMecanum() {
        mecanumDrive.setName("Drive System", "Mecanum");
        LiveWindow.add(mecanumDrive);
    }

    //Debug the tank drive
    //public void debugTank() {
    //    tankDrive.setName("Drive System", "Tank");
    //    LiveWindow.add(tankDrive);
    //}

    //Runs all of the debug software
    public void debugAll() {
        debugMotors();
        debugMecanum();
        //debugTank();
    }

    public static Drive getInstance() {
        if(instance == null) {
            instance = new Drive();
        }
        return instance;
    }
}
