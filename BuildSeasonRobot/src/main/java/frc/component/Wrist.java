/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.component;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.RobotMap;
import frc.util.Component;
import frc.util.Debug;

//Implement component so that this can be included in the main loop
public class Wrist implements Component {

    // Store a static instance and create it for the singleton pattern
    private static Wrist instance;
    
    WPI_VictorSPX motor = new WPI_VictorSPX(RobotMap.Motors.wrist);

    DigitalInput lowerLimitSwitch = new DigitalInput(RobotMap.limitSwitches.wristDown);
    DigitalInput upperLimitSwitch = new DigitalInput(RobotMap.limitSwitches.wristUp);

    double mSpeed;

    private Wrist() {
        // Just here to remove the public constructor
    }

    public void setSpeed(double speed) {
        mSpeed = speed;

        if (upperLimitSwitch.get()) {
            mSpeed = Math.min(mSpeed, 0);
        } else if (lowerLimitSwitch.get()) {
            mSpeed = Math.max(mSpeed, 0);
        }
    }

    @Override
    public void execute() {
        // Code ran every loop
        motor.set(mSpeed);

    }

    public void initDebug() {
        ShuffleboardTab tab = Debug.wrist;

        motor.setName("Motors", "Wrist");

        upperLimitSwitch.setName("Limit Switches", "Upper Limit Wrist");
        lowerLimitSwitch.setName("Limit Switches", "Lower Limit Wrist");

        tab.add(motor);

        tab.add(upperLimitSwitch);
            //.withWidget(BuiltInWidgets.kBooleanBox);
        tab.add(lowerLimitSwitch);

        Debug.limitSwitches.add(upperLimitSwitch);
        Debug.limitSwitches.add(lowerLimitSwitch);

        Debug.motors.add(motor);
    }

    public static Wrist getInstance() {
        if(instance == null) {
            instance = new Wrist();
        }
        return instance;
    }
}
