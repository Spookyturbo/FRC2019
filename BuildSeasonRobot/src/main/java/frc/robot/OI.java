package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.component.Arm;
import frc.component.Drive;
import frc.component.Intake;
import frc.component.Jacks;
import frc.component.Wrist;
import frc.procedure.CameraAlign;

public class OI {
    // Administrator, DriveTrials, Competition
    public static final String ADMIN_PROFILE = "Administrator";
    public static final String MAIN_DRIVER_PROFILE = "Feaven";

    // Create and return the profile
    public static DriverProfile getProfile(String profile) {
        switch (profile) {
        default:
        case MAIN_DRIVER_PROFILE:
            return new DriverProfile();
        case ADMIN_PROFILE:
            return new AdminProfile();
        }
    }

    static class DriverProfile {
        XboxController driver = new XboxController(0);
        XboxController assistant = new XboxController(1);

        JoystickButton intakeOutButton = new JoystickButton(assistant, 6); // right bumper
        JoystickButton intakeInButton = new JoystickButton(assistant, 5); // left bumper

        JoystickButton frontJackExtendButton = new JoystickButton(driver, 6); // Right bumper
        JoystickButton frontJackRetractButton = new JoystickButton(driver, 5); // Left bumper

        Drive drive = Drive.getInstance();
        Arm arm = Arm.getInstance();
        Jacks jacks = Jacks.getInstance();
        Wrist wrist = Wrist.getInstance();
        Intake intake = Intake.getInstance();

        CameraAlign cameraAlign = CameraAlign.getInstance();

        public double getHorizontalDriveSpeed() {
            // Deadzone (Controller has a bit of issues centering from the right) (Think
            // this is only the Bluetooth one)
            double controllerValue = driver.getX(Hand.kLeft);
            return (Math.abs(controllerValue) < 0.13f) ? 0 : controllerValue;
        }

        public double getVerticalDriveSpeed() {
            return driver.getY(Hand.kLeft);
        }

        public double getRotationalDriveSpeed() {
            return driver.getX(Hand.kRight);
        }

        public void drive() {
            // Drive based off controls
            drive.driveCartesian(getHorizontalDriveSpeed(), getVerticalDriveSpeed(), getRotationalDriveSpeed());
        }

        public void cameraDrive() {
            // Drive based off camera
            if (driver.getRawButtonPressed(7)) {
                cameraAlign.resetPID();
            } else if (driver.getRawButton(7)) {
                cameraAlign.run();
            }
        }

        public void controlArm() {
            arm.setSpeed(-assistant.getY(Hand.kLeft) * 0.65f);

            // Toggle the PIDControl on the arm
            if (assistant.getRawButtonPressed(8)) {
                // Prevent arm from drastically moving when first pressed
                arm.setSetpoint(arm.getEncoder());
                if (arm.isPIDEnabled()) {
                    arm.disablePID();
                } else {
                    arm.enablePID();
                }
            }
        }

        public void controlArmPID() {
            if (arm.isPIDEnabled()) {
                if (assistant.getAButton()) {
                    arm.setLow();
                } else if (assistant.getXButton()) {
                    arm.setMiddle();
                } else if (assistant.getYButton()) {
                    arm.setHigh();
                } else if (assistant.getBButton()) {
                    arm.setDown();
                }
            }
        }

        public void controlWrist() {
            wrist.setSpeed(-assistant.getY(Hand.kRight) * 0.6f);
        }

        public void controlFrontJacks() {
            if (frontJackExtendButton.get()) {
                jacks.setFrontSpeed(1f);
            } else if (frontJackRetractButton.get()) {
                jacks.setFrontSpeed(-1f);
            } else {
                jacks.setFrontSpeed(0);
            }
        }

        public void controlRearJacks() {
            double speed = assistant.getRawAxis(2) - assistant.getRawAxis(3);
            jacks.setRearSpeed(speed);
        }

        public void controlRearJackWheel() {
            int pov = assistant.getPOV();
            if (pov == 90 || pov == 45 || pov == 135) {
                jacks.setWheelSpeed(-1);
            } else if (pov == 270 || pov == 225 || pov == 315) {
                jacks.setWheelSpeed(1);
            } else {
                jacks.setWheelSpeed(0);
            }
        }

        public void controlIntake() {
            if (intakeOutButton.get()) { // out
                intake.setSpeed(0.5f);
            } else if (intakeInButton.get()) { // in
                intake.setSpeed(-0.5f);
            } else {
                intake.setSpeed(0);
            }
        }
    }

    // --------------------------------------ADMIN--------------------------------------
    static class AdminProfile extends DriverProfile {

        JoystickButton armUpButton = new JoystickButton(driver, 2); // Xbox B
        JoystickButton armDownButton = new JoystickButton(driver, 1); // xbox A

        JoystickButton frontJackRetractButton = new JoystickButton(driver, 4); // xbox Y
        JoystickButton frontJackExtendButton = new JoystickButton(driver, 3); // xbox X

        @Override
        public double getHorizontalDriveSpeed() {
            // Deadzone (Controller has a bit of issues centering from the right)
            double controllerValue = driver.getX(Hand.kLeft);
            return (Math.abs(controllerValue) < 0.13f) ? 0 : controllerValue;
        }

        @Override
        public void controlWrist() {
            double wristSpeed = driver.getTriggerAxis(Hand.kLeft) - driver.getTriggerAxis(Hand.kRight); // Add the
            wrist.setSpeed(wristSpeed);
        }

        @Override
        public void controlArm() {
            if (armUpButton.get()) {
                arm.setSpeed(0.4f);
            } else if (armDownButton.get()) {
                arm.setSpeed(-0.4f);
            }
        }

        @Override
        public void controlArmPID() {
            // PIDControl
            if (arm.isPIDEnabled()) {
                double setPoint = Arm.getInstance().getSetpoint();
                if (driver.getBButtonPressed()) { // Up
                    if (setPoint < 0) {
                        arm.setDown();
                    } else if (setPoint < Arm.ARM_LOW) {
                        arm.setLow();
                    } else if (setPoint < Arm.ARM_MIDDLE) {
                        arm.setMiddle();
                    } else if (setPoint < Arm.ARM_HIGH) {
                        arm.setHigh();
                    }
                } else if (driver.getAButtonPressed()) { // Down
                    if (setPoint > Arm.ARM_HIGH) {
                        arm.setHigh();
                    } else if (setPoint > Arm.ARM_MIDDLE) {
                        arm.setMiddle();
                    } else if (setPoint > Arm.ARM_LOW) {
                        arm.setLow();
                    } else if (setPoint > 0) {
                        arm.setDown();
                    }
                } else if (driver.getStickButtonPressed(Hand.kRight)) {
                    arm.setSetpoint(arm.getSetpoint() - 6);
                }
            }
        }

        @Override
        public void controlFrontJacks() {
            if(frontJackExtendButton.get()) {
                jacks.setFrontSpeed(1);
            }
            else if(frontJackRetractButton.get()) {
                jacks.setFrontSpeed(-1);
            }
            else {
                jacks.setFrontSpeed(0);
            }
        }

        @Override
        public void controlRearJacks() {
            int pov = driver.getPOV();
            if (pov == 0 || pov == 45 || pov == 315) {
                jacks.setRearSpeed(1);
            } else if (pov == 180 || pov == 225 || pov == 135) {
                jacks.setRearSpeed(-1);
            } else {
                jacks.setRearSpeed(0);
            }
        }
    }
}