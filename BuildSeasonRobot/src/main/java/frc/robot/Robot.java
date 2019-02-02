package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Jacks;
import frc.util.Component;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import com.kauailabs.navx.frc.AHRS;

public class Robot extends TimedRobot {
  ArrayList<Component> components = new ArrayList<>();
  //Xbox Control
  XboxController xbox = new XboxController(OI.Driver.port);

  WPI_VictorSPX FL = new WPI_VictorSPX(RobotMap.Motors.FLDrive);
  WPI_VictorSPX BL = new WPI_VictorSPX(RobotMap.Motors.BLDrive);
  WPI_VictorSPX FR = new WPI_VictorSPX(RobotMap.Motors.FRDrive);
  WPI_VictorSPX BR = new WPI_VictorSPX(RobotMap.Motors.BRDrive);

  //WPI_VictorSPX intake = new WPI_VictorSPX(RobotMap.Motors.intake);

  MecanumDrive drive = new MecanumDrive(FL, BL, FR, BR);

  AHRS gyro;

  Encoder leftEncoder = new Encoder(0, 1);
  Encoder rightEncoder = new Encoder(2, 3);

  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    components.add(Jacks.getInstance());
    SmartDashboard.putData("Auto choices", m_chooser);

    leftEncoder.setName("Encoders", "Left");
    rightEncoder.setName("Encoders", "Right");

    LiveWindow.add(leftEncoder);
    LiveWindow.add(rightEncoder);
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
    for(Component component : components) {
      component.execute();
    }
  }

  @Override
  public void teleopInit() {
    //gyro.reset();
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
    
    // if(xbox.getBumper(Hand.kLeft)) {
    //   intake.set(1f);
    // }
    // else if(xbox.getBumper(Hand.kRight)) {
    //   intake.set(-1f);
    // }
    // else {
    //   intake.set(0);
    // }
    //run all of our components
    for(Component component : components) {
      component.execute();
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
