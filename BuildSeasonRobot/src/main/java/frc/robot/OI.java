package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {

    XboxController xbox = new XboxController(0);
    Joystick joystick = new Joystick(1);

    JoystickButton armUpButton = new JoystickButton(xbox, 2), //xbox B
            armDownButton = new JoystickButton(xbox, 1), //xbox A

            intakeOutButton = new JoystickButton(xbox, 5), //xbox left bumber
            intakeInButton = new JoystickButton(xbox, 6), //xbox right bumper

            frontJackUpButton = new JoystickButton(xbox, 4), //xbox Y

            frontJackDownButton = new JoystickButton(xbox, 3), //xbox X
            rearJackUpButton = new JoystickButton(joystick, 3),
            rearJackDownButton = new JoystickButton(joystick, 5);
            




    public OI() {

    }

    //Strafing speed of the bot
    public double getHorizontalDriveSpeed() {
        return xbox.getX(Hand.kLeft);
    }

    //Forward/Backward speed of the bot
    public double getVerticalDriveSpeed() {
        return xbox.getY(Hand.kLeft);
    }

    //Rotational speed of the bot
    public double getRotationalDriveSpeed() {
        return xbox.getX(Hand.kRight);
    }

    //Speed for the wrist
    public double getWristSpeed() {
        //Vertical axix, forward is negative
        return joystick.getRawAxis(1);
    }
}