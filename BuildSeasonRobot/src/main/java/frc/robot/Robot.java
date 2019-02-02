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

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionThread;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

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
  UsbCamera camera;
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
    camera.setExposureManual(20);
    SmartDashboard.putNumber("Exposure", 20);
    visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
        if (!pipeline.filterContoursOutput().isEmpty()) {
            Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
            synchronized (imgLock) {
                centerX = r.x + (r.width / 2);
            }
        }
    });
    visionThread.start();


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
	
	private static final int IMG_WIDTH = 320;
	private static final int IMG_HEIGHT = 240;
	
	private VisionThread visionThread;
	private double centerX = 0.0;

	
  private final Object imgLock = new Object();




@Override
public void autonomousPeriodic() {
    double centerX;
    synchronized (imgLock) {
        centerX = this.centerX; 
        
    }
    System.out.println("The X position is " + centerX);
    double turn = centerX - (IMG_WIDTH / 2);
} 

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }




  @Override
  public void teleopInit() {
    
  }

  /**
   * This function is called periodically during operator control.
   */
  
  @Override
  public void teleopPeriodic() {

    double rotate = 0;
    if (centerX < 110) {
        rotate = -0.2;
    }
    else if (centerX > 210) {
         rotate = 0.2;
    }
    else {
        rotate = 0;
    }
    drive.driveCartesian(0, 0, rotate);
    System.out.println("The value of the center is" + centerX);
  }
    



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
      int Exposure = (int) SmartDashboard.getNumber("Exposure", 20);
      camera.setExposureManual(Exposure);
  }
}

