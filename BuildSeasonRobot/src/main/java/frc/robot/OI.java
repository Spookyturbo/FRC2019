package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {
    // Administrator, DriveTrials, Competition
    private final String adminProfile = "Administrator";
    private final String driverTrialsProfile = "DriveTrials";
    String profileName = driverTrialsProfile;

    XboxController xbox = new XboxController(0);
    XboxController assistant = new XboxController(1);
    Joystick joystick = new Joystick(1);

    JoystickButton armUpButton, armDownButton, intakeOutButton, intakeInButton, frontJackRetractButton,
            frontJackExtendButton, rearJackUpButton, rearJackDownButton, wheelJackOutButton, wheelJackInButton;

    public OI() {
        switch (profileName) {
        case adminProfile:
            armUpButton = new JoystickButton(xbox, 2);
            armDownButton = new JoystickButton(xbox, 1); // xbox A

            intakeOutButton = new JoystickButton(xbox, 5); // xbox left bumber
            intakeInButton = new JoystickButton(xbox, 6); // xbox right bumper

            frontJackRetractButton = new JoystickButton(xbox, 4); // xbox Y
            frontJackExtendButton = new JoystickButton(xbox, 3); // xbox X

            rearJackUpButton = new JoystickButton(joystick, 3);
            rearJackDownButton = new JoystickButton(joystick, 5);

            wheelJackOutButton = new JoystickButton(joystick, 4);
            wheelJackInButton = new JoystickButton(joystick, 6);
            break;
        case driverTrialsProfile:
            intakeOutButton = new JoystickButton(assistant, 6); // right bumper
            intakeInButton = new JoystickButton(assistant, 5); // left bumper

            frontJackExtendButton = new JoystickButton(xbox, 6); // right bumper
            frontJackRetractButton = new JoystickButton(xbox, 5);
            break;
        }
    }

    // Strafing speed of the bot
    public double getHorizontalDriveSpeed() {
        // Deadzone (Controller has a bit of issues centering from the right)
        double controllerValue = xbox.getX(Hand.kLeft);
        return (Math.abs(controllerValue) < 0.13f) ? 0 : controllerValue;
    }

    // Forward/Backward speed of the bot
    public double getVerticalDriveSpeed() {
        return xbox.getY(Hand.kLeft);
    }

    // Rotational speed of the bot
    public double getRotationalDriveSpeed() {
        return xbox.getX(Hand.kRight);
    }

    // Speed for the wrist
    public double getWristSpeed() {
        // Vertical axix, forward is negative
        switch (profileName) {
        case adminProfile:
            if (xbox.getPOV() == 0) {
                System.out.println("Wrist Up");
                return 0.6f;
            } else if (xbox.getPOV() == 180) {
                System.out.println("Wrist Down");
                return -0.6f;
            }
            break;
        case driverTrialsProfile:
            return -assistant.getY(Hand.kRight) / 0.6;
        }

        return 0;
    }

    public double getArmSpeed() {
        switch (profileName) {
        case adminProfile:
            if (armUpButton.get()) {
                return 0.4f;
            } else if (armDownButton.get()) {
                return -0.4f;
            }
            break;
        case driverTrialsProfile:
            return -assistant.getY(Hand.kLeft) * 0.65f;
        }

        return 0;
    }

    public double getFrontJackSpeed() {
        switch (profileName) {
        case adminProfile:
        case driverTrialsProfile:
            if (frontJackExtendButton.get()) {
                return 1;
            } else if (frontJackRetractButton.get()) {
                return -1f;
            }
            break;
        }

        return 0;
    }

    public double getRearJackSpeed() {
        switch (profileName) {
        case driverTrialsProfile:
            int pov = assistant.getPOV();
            if (pov == 0 || pov == 45 || pov == 315) {
                return 1;
            } else if (pov == 180 || pov == 225 || pov == 135) {
                return -1;
            }
        }

        return 0;
    }

    public double getJackWheelSpeed() {
        switch (profileName) {
        case adminProfile:
        case driverTrialsProfile:
            int pov = assistant.getPOV();
            if (pov == 90 || pov == 45 || pov == 135) {
                return -1;
            } else if (pov == 270 || pov == 225 || pov == 315) {
                return 1;
            }
            break;
        }
        return 0;
    }

    public double getIntakeSpeed() {
        switch (profileName) {
        case adminProfile:
        case driverTrialsProfile:
            if (intakeOutButton.get()) {
                return 1;
            } else if (intakeInButton.get()) {
                return -1;
            }
        }

        return 0;
    }
}