/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.autonomous.Pathweaver;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  //Xbox Control
  Joystick xBox = new Joystick(0);

  WPI_VictorSPX FL = new WPI_VictorSPX(1);
  WPI_VictorSPX BL = new WPI_VictorSPX(2);
  WPI_VictorSPX FR = new WPI_VictorSPX(3);
  WPI_VictorSPX BR = new WPI_VictorSPX(4);

  SpeedControllerGroup left = new SpeedControllerGroup(FL, BL);
  SpeedControllerGroup right = new SpeedControllerGroup(FR, BR);

  MecanumDrive drive = new MecanumDrive(FL, BL, FR, BR);

  AHRS gyro = new AHRS(SPI.Port.kMXP);

  Encoder leftEncoder = new Encoder(0, 1);
  Encoder rightEncoder = new Encoder(2, 3);

  Pathweaver pathweaver = new Pathweaver(leftEncoder, rightEncoder, left, right, gyro);

  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  NeutralMode motorMode = NeutralMode.Brake;
  @Override
  public void robotInit() {
    FL.setNeutralMode(motorMode);
    BL.setNeutralMode(motorMode);
    FR.setNeutralMode(motorMode);
    BR.setNeutralMode(motorMode);

    leftEncoder.setDistancePerPulse((0.5f * Math.PI) / 257);
    rightEncoder.setDistancePerPulse((0.5f * Math.PI) / 171);

    gyro.reset();
    leftEncoder.reset();
    rightEncoder.reset();

    m_chooser.setDefaultOption("Forward", "Forward");
    m_chooser.addOption("ForwardLeft", "ForwardLeft");
    m_chooser.addOption("ForwardRight", "FowardRight");
    m_chooser.addOption("Tower", "Tower");
    SmartDashboard.putData("Auto choices", m_chooser);

    rightEncoder.setName("Encoders", "Right Encoder");
    leftEncoder.setName("Encoders", "Left Encoder");
    gyro.setName("Angle", "Gyro");

    LiveWindow.add(rightEncoder);
    LiveWindow.add(leftEncoder);
    LiveWindow.add(gyro);

    SmartDashboard.putNumber("LeftEncoder", leftEncoder.get());
    SmartDashboard.putNumber("RightEncoder", rightEncoder.get());
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    String path = m_chooser.getSelected();
    System.out.println(path);

    pathweaver.setPath(path);
    pathweaver.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("LeftEncoder", leftEncoder.get());
    SmartDashboard.putNumber("RightEncoder", rightEncoder.get());
  }

  @Override
  public void teleopInit() {
    leftEncoder.reset();
    rightEncoder.reset();
    pathweaver.stop();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("LeftEncoder", leftEncoder.get());
    SmartDashboard.putNumber("RightEncoder", rightEncoder.get());

    double y = -xBox.getRawAxis(1);
    double x = xBox.getRawAxis(0);
    double rotate = xBox.getRawAxis(4);

    drive.driveCartesian(x, y, rotate);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    pathweaver.stop();
    left.set(0);
    right.set(0);
  }
}
