package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import com.kauailabs.navx.frc.AHRS;

public class Robot extends TimedRobot {
  //Xbox Control
  XboxController xbox = new XboxController(OI.Driver.port);

  WPI_VictorSPX FL = new WPI_VictorSPX(RobotMap.Motors.FLDrive);
  WPI_VictorSPX BL = new WPI_VictorSPX(RobotMap.Motors.BLDrive);
  WPI_VictorSPX FR = new WPI_VictorSPX(RobotMap.Motors.FRDrive);
  WPI_VictorSPX BR = new WPI_VictorSPX(RobotMap.Motors.BRDrive);

  MecanumDrive drive = new MecanumDrive(FL, BL, FR, BR);

  AHRS gyro;

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
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //If the gyro is not plugged in this can throw an error, make sure it doesn't crash the robot
    try {
      gyro = new AHRS(SPI.Port.kMXP);
    } catch (RuntimeException e) { 
      DriverStation.reportError("Error instantiating navX MXP:  " + e.getMessage(), true); 
    }
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
    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    double y = -xbox.getY(Hand.kLeft);
    double x = xbox.getX(Hand.kLeft);
    double rotate = xbox.getX(Hand.kRight);

    drive.driveCartesian(x, y, rotate);
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
