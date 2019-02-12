package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.component.Arm;
import frc.component.Drive;
import frc.component.Intake;
import frc.component.Jacks;
import frc.component.Wrist;
import frc.procedure.CameraAlign;
import frc.util.Component;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot implements PIDOutput {
    ArrayList<Component> components = new ArrayList<>();
    // Xbox Control
    AHRS gyro;

    CameraAlign cameraAlign = new CameraAlign();

    Encoder armEncoder = new Encoder(8, 9, false, EncodingType.k4X);
    Encoder leftEncoder = new Encoder(12, 13, false, EncodingType.k4X);
    Encoder rightEncoder = new Encoder(10, 11, false, EncodingType.k4X);
    // Encoder rightEncoder = new Encoder(2, 3);

    OI.ControlProfile controlProfile;

    Drive drive;
    Jacks jacks;
    Intake intake;
    Wrist wrist;
    Arm arm;

    private String m_driverSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();

    PIDController turnController;
    double turnRate;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        CameraServer.getInstance().startAutomaticCapture();

        // Init here, should be overwritten in telopinit
        controlProfile = OI.getProfile(OI.ADMIN_PROFILE);

        // Store in cleaner variables
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

        //Encoder test
        armEncoder.setName("Encoder", "Arm");
        leftEncoder.setName("Encoder", "Left");
        rightEncoder.setName("Encoder", "Right");

        LiveWindow.add(armEncoder);
        LiveWindow.add(leftEncoder);
        LiveWindow.add(rightEncoder);

        //Put drive profiles on smartDashboard
        m_chooser.setDefaultOption("DriveTrials", OI.DRIVER_TRIALS_PROFILE);
        m_chooser.addOption("Admin", OI.ADMIN_PROFILE);

        SmartDashboard.putData("Driver Mode", m_chooser);

        turnController.setInputRange(-180.0f, 180.0f);
        turnController.setOutputRange(-0.5, 0.5);
        turnController.setAbsoluteTolerance(2);
        turnController.setContinuous(true);
        turnController.setSetpoint(0);
        turnController.enable();
        gyro.reset();

        // If the gyro is not plugged in this can throw an error, make sure it doesn't
        // crash the robot
        try {
            gyro = new AHRS(SPI.Port.kMXP);
            gyro.setName("Gyro", "Angle");
            LiveWindow.add(gyro);
            turnController = new PIDController(0.03, 0, 0.05, gyro, this);
        } catch (RuntimeException e) {
            DriverStation.reportError("Error instantiating navX MXP:  " + e.getMessage(), true);
        }
    }

    /**
     * This function is called every robot packet, no matter the mode. Use this for
     * items like diagnostics that you want ran during disabled, autonomous,
     * teleoperated and test.
     *
     * <p>
     * This runs after the mode specific periodic functions, but before LiveWindow
     * and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
    }

    @Override
    public void autonomousInit() {
        m_driverSelected = m_chooser.getSelected();
        // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
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
        //Only swap profile if the profile was changed since last teleop
        if(m_driverSelected != m_chooser.getSelected()) {
            m_driverSelected = m_chooser.getSelected();
            controlProfile = OI.getProfile(m_driverSelected);
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    //leftEncoder = 8,9 
    @Override
    public void teleopPeriodic() {
        System.out.println(armEncoder.get() + " " + leftEncoder.get() + " " + rightEncoder.get());

        drive.driveCartesian(controlProfile.getHorizontalDriveSpeed(), controlProfile.getVerticalDriveSpeed(),
                controlProfile.getRotationalDriveSpeed());

        // Arm Control
        arm.setSpeed(controlProfile.getArmSpeed());
        // Intake control
        intake.setSpeed(controlProfile.getIntakeSpeed());
        // Jack Control
        jacks.setFrontSpeed(controlProfile.getFrontJackSpeed());
        jacks.setRearSpeed(controlProfile.getRearJackSpeed());
        jacks.setWheelSpeed(controlProfile.getJackWheelSpeed());
        // Wrist control
        wrist.setSpeed(controlProfile.getWristSpeed());

        updateAllComponents();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {

    }

    public void updateAllComponents() {
        // run all of our components
        for (Component component : components) {
            component.execute();
        }
    }
    public void Run() {
      double Angle = gyro.getAngle() % 360;
      SmartDashboard.putNumber("Angle", gyro.getAngle());
      double MotorSpeed = SmartDashboard.getNumber("MotorSpeed", drive.FL.get());
      System.out.println(MotorSpeed);
      SmartDashboard.putNumber("MotorSpeed", MotorSpeed);
    }
    
    public void pidWrite(double output) {
      turnRate = output;
    }
}
