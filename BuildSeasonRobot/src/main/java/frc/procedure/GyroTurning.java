/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.procedure;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Drive;
import frc.robot.OI;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.sensor.PIDControl;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;

import com.kauailabs.navx.frc.AHRS;


/**
 * Add your docs here.
 */
public class GyroTurning {
    AHRS gyro;
    PIDControl turnController;
    double turnRate;
    Drive drive = Drive.getInstance();

    private static GyroTurning instance;
    
    public GyroTurning() {
        init();
    }
    
	public void init() {
        turnController = new PIDControl(0.03, 0, 0.05);

        turnController.setInputRange(-180.0f, 180.0f);
        turnController.setOutputRange(-0.5, 0.5);
        turnController.setTolerance(2);
        turnController.setContinuous();
        turnController.setSetpoint(0);

        try {
            gyro = new AHRS(SPI.Port.kMXP);
            gyro.setName("Gyro", "Angle");
            gyro.reset();
        } catch(Exception e) {System.out.println("Gyro could not initialize!");}
    }

    public double run() {
        double motorSpeed = turnController.calculate(gyro.getAngle());

        drive.driveCartesian(drive.getHorizontalSpeed(), drive.getVerticalSpeed(), motorSpeed);
        return turnRate;
    }

    public double getSpeed() {
        return turnController.calculate(gyro.getAngle());
    }

    //Scale the angle input between -180 and 180
    public void setAngle(double angle) {
        turnController.reset();
        turnController.setSetpoint(angle);
    }

    public boolean atTarget() {
        return turnController.onTarget();
    }

    public void resetGyro() {
        gyro.reset();
    }

    public void resetPID() {
        turnController.reset();
    }

    public GyroTurning getInstance() {
        if(instance == null) {
            instance = new GyroTurning();
        }

        return instance;
    }
}
