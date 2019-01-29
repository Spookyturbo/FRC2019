/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.TimedRobot;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	private static final String PATH1 = "path1";
private static final int k_ticks_per_rev = 1024;
  private static final double k_wheel_diameter = 4.0 / 12.0;
  private static final double k_max_velocity = 10;

  private static final int k_left_channel = 0;
  private static final int k_right_channel = 1;

  private static final int k_left_encoder_port_a = 0;
  private static final int k_left_encoder_port_b = 1;
  private static final int k_right_encoder_port_a = 2;
  private static final int k_right_encoder_port_b = 3;

  private static final int k_gyro_port = 0;

  private static final String k_path_name = PATH1;
  AHRS gyro = new AHRS(SPI.Port.kMXP);

  
  private Notifier m_follower_notifier = new Notifier(this::followPath);
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  WPI_VictorSPX Fleftmotor = new WPI_VictorSPX(1);
   WPI_VictorSPX Brightmotor = new WPI_VictorSPX(1);
   WPI_VictorSPX Frightmotor = new WPI_VictorSPX(1);
   WPI_VictorSPX Bleftmotor = new WPI_VictorSPX(1);
   MecanumDrive drive = new MecanumDrive(Fleftmotor, Brightmotor, Frightmotor, Bleftmotor);
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    
    

    enc  = new Encoder(k_left_encoder_port_a, k_left_encoder_port_b);
    sampleEncoder = new Encoder(k_right_encoder_port_a, k_right_encoder_port_b);

      sampleEncoder.setMaxPeriod(.1);
  sampleEncoder.setMinRate(10);
  sampleEncoder.setDistancePerPulse(5);
  sampleEncoder.setReverseDirection(true);
  sampleEncoder.setSamplesToAverage(7);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  private void followPath() {
    if (m_left_follower.isFinished() || m_right_follower.isFinished()) {
      m_follower_notifier.stop();
    } else {
      double left_speed = m_left_follower.calculate(enc.get());
      double right_speed = m_right_follower.calculate(sampleEncoder.get());
      double heading = gyro.getAngle();
      double desired_heading = Pathfinder.r2d(m_left_follower.getHeading());
      double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);
      double turn =  0.8 * (-1.0/80.0) * heading_difference;
      Fleftmotor.set(left_speed + turn);
      Brightmotor.set(right_speed - turn);
      Bleftmotor.set(left_speed - turn);
      Frightmotor.set(right_speed + turn);
    }
  }



  @Override
  public void robotPeriodic() {



  }
  
  Encoder enc = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
  Encoder sampleEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
  EncoderFollower m_left_follower;
  EncoderFollower m_right_follower;
  

  @Override
  public void autonomousInit() {

    Trajectory left_trajectory = PathfinderFRC.getTrajectory(k_path_name + ".left");
    Trajectory right_trajectory = PathfinderFRC.getTrajectory(k_path_name + ".right");

    m_left_follower = new EncoderFollower(left_trajectory);
    m_right_follower = new EncoderFollower(right_trajectory);

    m_left_follower.configureEncoder(enc.get(), k_ticks_per_rev, k_wheel_diameter);
    // You must tune the PID values on the following line!
    m_left_follower.configurePIDVA(1.0, 0.0, 0.0, 1 / k_max_velocity, 0);

    m_right_follower.configureEncoder(sampleEncoder.get(), k_ticks_per_rev, k_wheel_diameter);
    // You must tune the PID values on the following line!
    m_right_follower.configurePIDVA(1.0, 0.0, 0.0, 1 / k_max_velocity, 0);
    
    m_follower_notifier = new Notifier(this::followPath);
    m_follower_notifier.startPeriodic(left_trajectory.get(0).dt);

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

    m_follower_notifier.stop();
    Bleftmotor.set(0);
    Frightmotor.set(0);
    Fleftmotor.set(0);
    Brightmotor.set(0);

    if  (sampleEncoder.get ()<20 ) {
       drive.driveCartesian(10, 0, 0);
    }
    int count = sampleEncoder.get();
    System.out.println ("count"+count);
  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
