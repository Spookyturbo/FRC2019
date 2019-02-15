/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.procedure;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class GyroTurning {

    public void run() {
        turnController.setP(SmartDashboard.getNumber("TurnP", turnController.getP()));
        turnController.setI(SmartDashboard.getNumber("TurnI", turnController.getI()));
        turnController.setD(SmartDashboard.getNumber("TurnD", turnController.getD()));

        System.out.println("Error: " + turnController.getError());

        double Angle = gyro.getAngle() % 360;
        SmartDashboard.putNumber("Angle", gyro.getAngle());
        double MotorSpeed = SmartDashboard.getNumber("MotorSpeed", drive.FL.get());
        System.out.println(MotorSpeed);
        SmartDashboard.putNumber("MotorSpeed", MotorSpeed);
        if (OI.ControlProfile.driver.getYButtonReleased()) {
            turnController.setSetpoint(0);
        }
        if (OI.ControlProfile.driver.getBButtonReleased()) {
            turnController.setSetpoint(90);
        }
        if (OI.ControlProfile.driver.getAButtonReleased()) {
            turnController.setSetpoint(180);
        }
        if (OI.ControlProfile.driver.getXButtonReleased()) {
            turnController.setSetpoint(-90);
        }
        System.out.println("SetpointL " + turnController.getSetpoint());
        System.out.println("turn rate: " + turnRate);
        drive.driveCartesian(0, 0, turnRate);
    }

    //Scale the angle input between -180 and 180
    public void setAngle(double angle) {
        turnController.setSetpoint(angle);
    }

}
