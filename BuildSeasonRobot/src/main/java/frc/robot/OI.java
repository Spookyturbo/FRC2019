package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.component.Arm;

public class OI {
    // Administrator, DriveTrials, Competition
    public static final String ADMIN_PROFILE = "Administrator";
    public static final String DRIVER_TRIALS_PROFILE = "DriveTrials";

    // Create and return the profile
    public static ControlProfile getProfile(String profile) {
        switch (profile) {
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

    // --------------------------------------DRIVERTRIALS--------------------------------------
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
            return -assistant.getRawAxis(3) / 0.6f;
            //return -assistant.getY(Hand.kRight) / 0.6;
        }

        @Override
        public double getArmSpeed() {
            Arm arm = Arm.getInstance();
            double armSpeed = 0;

            if (arm.isPIDEnabled()) {
                if(assistant.getAButton()) {
                    arm.setSetpoint(Arm.ARM_LOW);
                }
                else if(assistant.getXButton()) {
                    arm.setSetpoint(Arm.ARM_MIDDLE);
                }
                else if(assistant.getYButton()) {
                    arm.setSetpoint(Arm.ARM_HIGH);
                }
                else if(assistant.getBButton()) {
                    arm.setSetpoint(Arm.ARM_MIN);
                }

                // Speed is set in arm via PID
                armSpeed = arm.getSpeed();
                return armSpeed;
            }

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

    // --------------------------------------ADMIN--------------------------------------
    static class AdminProfile implements ControlProfile {

        public JoystickButton cameraTarget = new JoystickButton(driver, 7);

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
            wristSpeed += driver.getTriggerAxis(Hand.kRight) * -1f; // Add the down speed
            return wristSpeed;
        }

        @Override
        public double getArmSpeed() {
            Arm arm = Arm.getInstance();
            double armSpeed = 0;

            if (armUpButton.get()) {
                armSpeed = 0.4f;
            } else if (armDownButton.get()) {
                armSpeed = -0.4f;
            }

            if (arm.isPIDEnabled()) {
                double setPoint = Arm.getInstance().getSetpoint();
                if (driver.getBButtonPressed()) { // Up
                    if (setPoint < 0) {
                        arm.setSetpoint(0);
                        ;
                    } else if (setPoint < Arm.ARM_LOW) {
                        arm.setLow();
                    } else if (setPoint < Arm.ARM_MIDDLE) {
                        arm.setMiddle();
                    } else if (setPoint < Arm.ARM_HIGH) {
                        arm.setHigh();
                        ;
                    }
                } else if (driver.getAButtonPressed()) { // Down
                    if (setPoint > Arm.ARM_HIGH) {
                        arm.setHigh();
                    } else if (setPoint > Arm.ARM_MIDDLE) {
                        arm.setMiddle();
                    } else if (setPoint > Arm.ARM_LOW) {
                        arm.setLow();
                    } else if (setPoint > 0) {
                        arm.setSetpoint(0);
                    }
                } else if (driver.getStickButtonPressed(Hand.kRight)) {
                    arm.setSetpoint(arm.getSetpoint() - 6);
                }

                // Speed is set in arm via PID
                armSpeed = arm.getSpeed();
            }

            return armSpeed;
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