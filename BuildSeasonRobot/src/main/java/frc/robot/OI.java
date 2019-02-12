package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {
    // Administrator, DriveTrials, Competition
    public static final String ADMIN_PROFILE = "Administrator";
    public static final String DRIVER_TRIALS_PROFILE = "DriveTrials";

    //Create and return the profile
    public static ControlProfile getProfile(String profile) {
        switch(profile) {
            default:
                return new DriverTrialsProfile();
            case ADMIN_PROFILE:
                return new AdminProfile();
            case DRIVER_TRIALS_PROFILE:
                return new DriverTrialsProfile();
        }
    }

    interface ControlProfile {
        XboxController driver = new XboxController(0);
        XboxController assistant = new XboxController(1);

        double getHorizontalDriveSpeed();

        double getVerticalDriveSpeed();

        double getRotationalDriveSpeed();

        double getWristSpeed();

        double getArmSpeed();

        double getFrontJackSpeed();

        double getRearJackSpeed();

        double getJackWheelSpeed();

        double getIntakeSpeed();
    }

    //--------------------------------------DRIVERTRIALS--------------------------------------
    static class DriverTrialsProfile implements ControlProfile {

        JoystickButton intakeOutButton = new JoystickButton(assistant, 6); // right bumper
        JoystickButton intakeInButton = new JoystickButton(assistant, 5); // left bumper

        JoystickButton frontJackExtendButton = new JoystickButton(driver, 6); // Right bumper
        JoystickButton frontJackRetractButton = new JoystickButton(driver, 5); // Left bumper

        @Override
        public double getHorizontalDriveSpeed() {
            // Deadzone (Controller has a bit of issues centering from the right)
            double controllerValue = driver.getX(Hand.kLeft);
            return (Math.abs(controllerValue) < 0.13f) ? 0 : controllerValue;
        }

        @Override
        public double getVerticalDriveSpeed() {
            return driver.getY(Hand.kLeft);
        }

        @Override
        public double getRotationalDriveSpeed() {
            return driver.getX(Hand.kRight);
        }

        @Override
        public double getWristSpeed() {
            return -assistant.getY(Hand.kRight) / 0.6;
        }

        @Override
        public double getArmSpeed() {
            return -assistant.getY(Hand.kLeft) * 0.65f;
        }

        @Override
        public double getFrontJackSpeed() {
            if (frontJackExtendButton.get()) {
                return 1;
            } else if (frontJackRetractButton.get()) {
                return -1f;
            }

            return 0;
        }

        @Override
        public double getRearJackSpeed() {
            int pov = assistant.getPOV();
            if (pov == 0 || pov == 45 || pov == 315) {
                return 1;
            } else if (pov == 180 || pov == 225 || pov == 135) {
                return -1;
            }

            return 0;
        }

        @Override
        public double getJackWheelSpeed() {
            int pov = assistant.getPOV();
            if (pov == 90 || pov == 45 || pov == 135) {
                return -1;
            } else if (pov == 270 || pov == 225 || pov == 315) {
                return 1;
            }

            return 0;
        }

        @Override
        public double getIntakeSpeed() {
            if (intakeOutButton.get()) {
                return 1;
            } else if (intakeInButton.get()) {
                return -1;
            }

            return 0;
        }

    }

    //--------------------------------------ADMIN--------------------------------------
    static class AdminProfile implements ControlProfile {

        JoystickButton armUpButton = new JoystickButton(driver, 2);
        JoystickButton armDownButton = new JoystickButton(driver, 1); // xbox A

        JoystickButton intakeOutButton = new JoystickButton(driver, 5); // xbox left bumber
        JoystickButton intakeInButton = new JoystickButton(driver, 6); // xbox right bumper

        JoystickButton frontJackRetractButton = new JoystickButton(driver, 4); // xbox Y
        JoystickButton frontJackExtendButton = new JoystickButton(driver, 3); // xbox X

        JoystickButton rearJackUpButton = new JoystickButton(driver, 3);
        JoystickButton rearJackDownButton = new JoystickButton(driver, 5);

        JoystickButton wheelJackOutButton = new JoystickButton(driver, 4);
        JoystickButton wheelJackInButton = new JoystickButton(driver, 6);

        @Override
        public double getHorizontalDriveSpeed() {
            // Deadzone (Controller has a bit of issues centering from the right)
            double controllerValue = driver.getX(Hand.kLeft);
            return (Math.abs(controllerValue) < 0.13f) ? 0 : controllerValue;
        }

        @Override
        public double getVerticalDriveSpeed() {
            return driver.getY(Hand.kLeft);
        }

        @Override
        public double getRotationalDriveSpeed() {
            return driver.getX(Hand.kRight);
        }

        @Override
        public double getWristSpeed() {
            double wristSpeed = driver.getTriggerAxis(Hand.kLeft) * 1f; // Add the up speed
            wristSpeed += driver.getTriggerAxis(Hand.kRight) * -1f; //Add the down speed
            return wristSpeed;
        }

        @Override
        public double getArmSpeed() {
            if (armUpButton.get()) {
                return 0.4f;
            } else if (armDownButton.get()) {
                return -0.4f;
            }

            return 0;
        }

        @Override
        public double getFrontJackSpeed() {
            if (frontJackExtendButton.get()) {
                return 1;
            } else if (frontJackRetractButton.get()) {
                return -1f;
            }

            return 0;
        }

        @Override
        public double getRearJackSpeed() {
            int pov = driver.getPOV();
            if (pov == 0 || pov == 45 || pov == 315) {
                return 1;
            } else if (pov == 180 || pov == 225 || pov == 135) {
                return -1;
            }

            return 0;
        }

        @Override
        public double getJackWheelSpeed() {
            int pov = driver.getPOV();
            if (pov == 90 || pov == 45 || pov == 135) {
                return -1;
            } else if (pov == 270 || pov == 225 || pov == 315) {
                return 1;
            }

            return 0;
        }

        @Override
        public double getIntakeSpeed() {
            if (intakeOutButton.get()) {
                return 1;
            } else if (intakeInButton.get()) {
                return -1;
            }

            return 0;
        }

    }
}