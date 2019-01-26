/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  WPI_VictorSPX Fleftmotor = new WPI_VictorSPX(1);
  WPI_VictorSPX Brightmotor = new WPI_VictorSPX(4);
  WPI_VictorSPX Frightmotor = new WPI_VictorSPX(3);
  WPI_VictorSPX Bleftmotor = new WPI_VictorSPX(2);
  MecanumDrive drive = new MecanumDrive(Fleftmotor, Brightmotor, Frightmotor, Bleftmotor);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    sampleEncoder.setMaxPeriod(.1);
    sampleEncoder.setMinRate(10);
    sampleEncoder.setDistancePerPulse(5);
    sampleEncoder.setReverseDirection(true);
    sampleEncoder.setSamplesToAverage(7);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    sampleEncoder.setName("ControlSystems", "Encoder");
    LiveWindow.add(sampleEncoder);
  }

  @Override
  public void robotPeriodic() {
  }

  Encoder sampleEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    double distance = sampleEncoder.getRaw();

    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:

      break;
    case kDefaultAuto:
    default:

      break;
    }
  }

  @Override
  public void teleopPeriodic() {
    System.out.println(sampleEncoder.getRaw());
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
