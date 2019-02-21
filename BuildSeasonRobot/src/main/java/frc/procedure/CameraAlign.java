/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.procedure;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Drive;
import frc.sensor.Limelight;
import frc.sensor.PIDControl;

/**
 * Add your docs here.
 */
public class CameraAlign {

    private Limelight camera = Limelight.getInstance();
    private Drive drive = Drive.getInstance();

    PIDControl distanceController = new PIDControl(0.1);
    PIDControl strafingController = new PIDControl(0.1, 0.001);
    PIDControl rotationController = new PIDControl(0.009);

    public CameraAlign() {
        distanceController.setOutputRange(-0.5, 0.5);
        strafingController.setOutputRange(-0.5, 0.5);
        rotationController.setOutputRange(-0.5, 0.5);

        distanceController.setInputRange(-20.5, 20.5);
        strafingController.setInputRange(-27, 27);
        rotationController.setInputRange(-90, 0);

        distanceController.setTolerance(0.5);
        strafingController.setTolerance(0.5);
        rotationController.setTolerance(2);

        distanceController.setSetpoint(0);
        strafingController.setSetpoint(0);
        rotationController.setSetpoint(0);

        strafingController.initSmartDashboard("CameraDistance");
        LiveWindow.add(strafingController);
    }

    public void run() {
        strafingController.updateFromSmartDashboard("CameraDistance");
        double[] yCorners = camera.getYCorners();

        double leftCorner = yCorners[1];
        double rightCorner = yCorners[0];

        double strafingSpeed = -strafingController.calculate(camera.getXAngle());
        double distanceSpeed = -distanceController.calculate(camera.getYAngle());
        double rotationSpeed = rotationController.calculate(camera.getSkew());

        if(leftCorner < rightCorner) {
            rotationSpeed *= -1;   
        }
        
        //Strafe to the indicated position
        drive.driveCartesian(strafingSpeed, distanceSpeed, rotationSpeed);
    }

    //Should be called at the start of a move using this
    //So that the derivative error and integral error
    //start correctly
    public void resetPID() {
        distanceController.reset();
        strafingController.reset();
        rotationController.reset();
    }
}
