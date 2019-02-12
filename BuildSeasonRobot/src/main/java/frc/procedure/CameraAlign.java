/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.procedure;

import frc.component.Drive;
import frc.sensor.Limelight;

/**
 * Add your docs here.
 */
public class CameraAlign {

    private Drive drive = Drive.getInstance();

    private double kP;
    private double kI;
    private double kD;

    private double previousError = 0;
    private double integralError = 0;

    public void run() {
        double error = Limelight.getInstance().getXAngle();
        integralError += error * 0.02f; //Error * seconds
        double derivativeError = (error - previousError) / .02f; //Change in error / time

        double speed = kP * error + kI * integralError + kD * derivativeError;

        //Strafe to the indicated position
        drive.driveCartesian(speed, 0, 0);

        previousError = error;

    }

    //Should be called at the start of a move using this
    //So that the derivative error and integral error
    //start correctly
    public void resetPID() {
        previousError = 0;
        integralError = 0;

    }

}
