/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.util;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * Add your docs here.
 */
public class Debug {

    public static final ShuffleboardTab arm = Shuffleboard.getTab("Arm");
    public static final ShuffleboardTab intake = Shuffleboard.getTab("Intake");
    public static final ShuffleboardTab wrist = Shuffleboard.getTab("Wrist");
    public static final ShuffleboardTab jacks = Shuffleboard.getTab("Jacks");
    public static final ShuffleboardTab drive = Shuffleboard.getTab("DriveTrain");

}
