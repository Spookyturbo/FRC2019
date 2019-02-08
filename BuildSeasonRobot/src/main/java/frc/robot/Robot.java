package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Arm;
import frc.component.Drive;
import frc.component.Intake;
import frc.component.Jacks;
import frc.component.Wrist;
import frc.util.Component;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  ArrayList<Component> components = new ArrayList<>();
  //Xbox Control
  AHRS gyro;

  // Encoder leftEncoder = new Encoder(0, 1);
  // Encoder rightEncoder = new Encoder(2, 3);

  OI oi;

  Drive drive;
  Jacks jacks;
  Intake intake;
  Wrist wrist;
  Arm arm;

  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    oi = new OI();
    //Store in cleaner variables
    drive = Drive.getInstance();
    jacks = Jacks.getInstance();
    intake = Intake.getInstance();
    wrist = Wrist.getInstance();
    arm = Arm.getInstance();

    components.add(drive);
    components.add(jacks);
    components.add(intake);
    components.add(wrist);
    components.add(arm);

    drive.invertX(true);

    SmartDashboard.putData("Auto choices", m_chooser);

    // leftEncoder.setName("Encoders", "Left");
    // rightEncoder.setName("Encoders", "Right");

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
    updateAllComponents();
  }

  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during operator control.
   */
  
  @Override
  public void teleopPeriodic() {

    drive.driveCartesian(oi.getHorizontalDriveSpeed(), oi.getVerticalDriveSpeed(), oi.getRotationalDriveSpeed());

    //Arm control
    if(oi.armUpButton.get()) {
      arm.setSpeed(0.4f);
    }
    else if(oi.armDownButton.get()) {
      arm.setSpeed(-0.4f);
    }
    else {
      arm.setSpeed(0);
    }
    
    //Intake control
    if(oi.intakeOutButton.get()) {
      intake.setSpeed(1);
    }
    else if(oi.intakeInButton.get()) {
      intake.setSpeed(-1);
    }
    else {
      intake.setSpeed(0);
    }

    //Front jack control
    if(oi.frontJackUpButton.get()) {
      jacks.setFrontSpeed(0.4f);
    }
    else if(oi.frontJackDownButton.get()) {
      jacks.setFrontSpeed(-0.4f);
    }
    else {
      jacks.setFrontSpeed(0f);
    }

    //Rear jack control
    if(oi.rearJackUpButton.get()) {
      jacks.setRearSpeed(0.4f);
    }
    else if(oi.rearJackDownButton.get()) {
      jacks.setRearSpeed(-0.4f);
    }
    else {
      jacks.setRearSpeed(0);
    }

    //Jack wheel control
    if(oi.joystick.getRawButton(4)) {
      jacks.setWheelSpeed(1f);
    }
    else if(oi.joystick.getRawButton(6)) {
      jacks.setWheelSpeed(-1f);
    }
    else {
      jacks.setWheelSpeed(0);
    }

    //Positive is up
    wrist.setSpeed(oi.getWristSpeed());

    updateAllComponents();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {

  }

  public void updateAllComponents() {
    //run all of our components
    for(Component component : components) {
      component.execute();
    }
  }
}

