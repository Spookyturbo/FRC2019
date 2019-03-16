/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.procedure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Drive;
import frc.sensor.Limelight;
import frc.sensor.PIDControl;

/**
 * Add your docs here.
 */
public class CameraAlign {

    private static CameraAlign instance;

    private Limelight camera = Limelight.getInstance();
    private Drive drive = Drive.getInstance();

    private int timeout;
    Timer timer = new Timer();

    PIDControl distanceController = new PIDControl(0.1);
    PIDControl strafingController = new PIDControl(0.1, 0.01);
    PIDControl rotationController = new PIDControl(0.05, 0.001);

    private CameraAlign() {
        distanceController.setOutputRange(-0.5, 0.5);
        strafingController.setOutputRange(-0.5, 0.5);
        rotationController.setOutputRange(-0.5, 0.5);

        distanceController.setInputRange(-20.5, 20.5);
        strafingController.setInputRange(-27, 27);
        rotationController.setInputRange(-90, 0);

        distanceController.setTolerance(0.5);
        strafingController.setTolerance(0.5);
        rotationController.setTolerance(1);

        distanceController.setSetpoint(0);
        strafingController.setSetpoint(0);
        rotationController.setSetpoint(-1.6);

        rotationController.setContinuous();

        strafingController.setMaxIContribution(0.3f);
        strafingController.setIKickInRate(2);

        rotationController.setIKickInRate(2);

        // strafingController.initSmartDashboard("CameraStrafe");
        // distanceController.initSmartDashboard("CameraDistance");
        // rotationController.initSmartDashboard("CameraRotation");
        // LiveWindow.add(strafingController);
    }

    public void run() {
        // strafingController.updateFromSmartDashboard("CameraStrafe");
        // distanceController.updateFromSmartDashboard("CameraDistance");
        // rotationController.updateFromSmartDashboard("CameraRotation");
        // double[] yCorners = camera.getYCorners();

        // double leftCorner = yCorners[1];
        // double rightCorner = yCorners[0];

        double strafingSpeed = -strafingController.calculate(camera.getXAngle());
        double distanceSpeed = -distanceController.calculate(camera.getYAngle());
        double rotationSpeed = rotationController.calculate(camera.getSkew());

        // if(leftCorner < rightCorner) {
        //     rotationSpeed *= -1;   
        // }
        
        //cant strafe and rotate at the same time unless less then this
        // if(Math.abs(rotationSpeed) > 0.08) {
        //     strafingSpeed = 0;
        // }

        //Strafe to the indicated position
        drive.driveCartesian(strafingSpeed, distanceSpeed, rotationSpeed);
    }

    //Runs the alignment for a set period of time until the timeout happens or is alligned
    public void align(int timeout) {
        this.timeout = timeout;
        timer.reset();
        timer.start();

        run();
    }

    //Returns if correctly aligned
    public boolean isAlligned() {
        return strafingController.onTarget() && distanceController.onTarget() && rotationController.onTarget();
    }

    public boolean isCompleted() {
        if(timer.hasPeriodPassed(timeout) || isAlligned()) {
            timer.stop();
            return true;
        }
        
        return false;
    }

    //Should be called at the start of a move using this
    //So that the derivative error and integral error
    //start correctly
    public void resetPID() {
        distanceController.reset();
        strafingController.reset();
        rotationController.reset();
    }

    public static CameraAlign getInstance() {
        if(instance == null) {
            instance = new CameraAlign();
        }

        return instance;
    }
}
