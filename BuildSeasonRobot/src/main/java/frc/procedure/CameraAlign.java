/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.procedure;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Drive;
import frc.sensor.Limelight;

/**
 * Add your docs here.
 */
public class CameraAlign {

    private Limelight camera = Limelight.getInstance();
    private Drive drive = Drive.getInstance();

    private double kP = 0.3f;
    private double kI;
    private double kD;

    private double kPDistance = 0.1f;
    private double kPRotation = 0.01f;

    private double previousError = 0;
    private double integralError = 0;

    public CameraAlign() {
        SmartDashboard.putNumber("DistanceP", kPDistance);
        SmartDashboard.putNumber("HeadingP", kP);
        SmartDashboard.putNumber("RotationP", kPRotation);
    }

    public void run() {
        double[] yCorners = camera.getYCorners();

        double leftCorner = yCorners[1];
        double rightCorner = yCorners[0];
        kP = SmartDashboard.getNumber("HeadingP", kP);
        kPDistance = SmartDashboard.getNumber("DistanceP", kPDistance);
        kPRotation = SmartDashboard.getNumber("RotationP", kPRotation);
        double rotationError = camera.getSkew();
        double headingError = camera.getXAngle();
        double distanceError = camera.getYAngle();
        integralError += headingError * 0.02f; //Error * seconds
        double derivativeError = (headingError - previousError) / .02f; //Change in error / time

        double ySpeed = kP * headingError + kI * integralError + kD * derivativeError;
        ySpeed = clamp(ySpeed, -1, 1);

        double xSpeed = clamp(kPDistance * distanceError, -1, 1);
        double rotationSpeed = clamp(kPRotation * rotationError, -1, 1);
        if(leftCorner > rightCorner) {
            rotationSpeed *= -1;   
        }
        //Strafe to the indicated position
        drive.driveCartesian(ySpeed * 0.5f, xSpeed * 0.5f, rotationSpeed * 0.5f);

        previousError = headingError;

    }

    //Should be called at the start of a move using this
    //So that the derivative error and integral error
    //start correctly
    public void resetPID() {
        previousError = 0;
        integralError = 0;

    }

    private double clamp(double n, double min, double max) {
        if(n > max)
            return max;
        else if(n < min)
            return min;

        return n;
    }

}
