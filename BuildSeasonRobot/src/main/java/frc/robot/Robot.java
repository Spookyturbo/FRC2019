/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot implements PIDOutput{
  //Xbox Control
  Joystick xBox = new Joystick(0);

  WPI_VictorSPX FL = new WPI_VictorSPX(1);
  WPI_VictorSPX BL = new WPI_VictorSPX(2);
  WPI_VictorSPX FR = new WPI_VictorSPX(3);
  WPI_VictorSPX BR = new WPI_VictorSPX(4);

  MecanumDrive drive = new MecanumDrive(FL, BL, FR, BR);

  AnalogInput sonar = new AnalogInput(0);
  PIDController sonarDistanceController = new PIDController(1, 0, 0, sonar, this);
  double speed; 


  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    double desiredVoltage = 30f*(25.4f/1024f);
    double desiredDistance = desiredVoltage / (5f/1024f);
    desiredDistance *= 5;
    desiredDistance /= 25.4;
    SmartDashboard.putNumber("DesiredOutput", desiredDistance);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    sonarDistanceController.setInputRange(0, 5);
    sonarDistanceController.setOutputRange(-0.5, 0.5);
    sonarDistanceController.setAbsoluteTolerance(5f*(25.4f/1024f));
    sonarDistanceController.setContinuous(false);
    sonarDistanceController.setSetpoint(30f*(25.4f/1024f));
    sonarDistanceController.enable();
    
    sonarDistanceController.setName("PID", "Sonar");
    LiveWindow.add(sonarDistanceController);
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
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  @Override
  public void teleopInit() {
    sonarDistanceController.enable();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    double coolData;
double storedData = sonar.getAverageVoltage(); 
coolData = (storedData/(5f/1024f)); 

System.out.print(storedData);
coolData*=5f;

coolData/=25.4; 
storedData = coolData; 
storedData *= (25.4/5f); 
storedData *= (5f/1024f);

System.out.println(" "+ storedData);
SmartDashboard.putNumber("sonar", coolData);
   
drive.driveCartesian(0, -speed, 0);


  }
    /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    sonarDistanceController.disable();
    drive.stopMotor();
  }

  @Override
  public void pidWrite(double output) {
    speed = output; 

  }
}
