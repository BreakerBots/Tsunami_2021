# Tsunami 2021
A repository for storing all the code for the Tsunami.

This readme explains how to set up and program Tsunami, 
along with a brief walk through this repository.

## Setting up Locally
1) Download the [WPILib Suite](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html)
2) Download [Github Desktop](https://desktop.github.com/)
3) Open Github Desktop and clone this repo (BreakerBots/Tsunami_2021)
4) Download [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/#section=windows)
5) Open IntelliJ and choose `File > Open` then navigate to the folder where
you downloaded this repo (should be /Documents/Github/Tsunami_2021) and press `OK`. 
I recommend clicking on the settings icon in the top left and selecting
`Flatten Packages`.

## Deploying Code
1) Open up a command prompt in the repo
    - Go back to Github desktop select `Repository > Open in Command Prompt`
2) Type `gradlew deploy` and press `enter`

## Launching Webapp
1) Open up a command prompt in the repo
2) Run `webapp` for the desktop version

Note: If you don't want to use the desktop version navigate to `http://localhost:5804` in your web browser.

## Simulating Autonomous
1) Open up a command prompt in the repo
2) Type `gradlew simulateJava` and press `enter`
3) Open the webapp (see above)
4) Select the `Plotter` tab
5) Click on the switch in the header (next to the robot name) to enable the robot
8) To finish close the command line that launched when you ran `simulateJava`

Note: If you want to change the selected auto path navigate to `frc.team5104.RobotSim`
   in IntelliJ and change the path inside the line: `AutoManager.setTargetPath(new ExamplePath());`

## Characterizing
1) Install [Python 3.7](https://www.python.org/downloads/release/python-379/)
2) Open up a command prompt in the repo
3) Run `pip install frc-characterization`
4) Open `Robot.java` and comment out `AutoManager.setTargetPath(...);`
5) Under that add the line (or uncomment) `AutoManager.characterize(SUBSYSTEM.class)` and replace `SUBSYSTEM` with the name of the subsystem you want to characterize.
6) Go back to the command line inside the repo
7) Run `characterize` and connect to the robot
8) Follow the instructions on the logger window
9) Click `Save File`
10) Close the window
11) In the new window, choose the file: `.charactize/charactization-data.json`
12) Double check your units and settings, then save ALL variables (you dont have to put all of them in code, but at least put them in google drive).

## Organization
The robot code is separated into multiple different sections or "packages" located inside
`src/main/java`. This explains what is located inside each package.
- `frc.team5104`
    - `Constants.java`: all constant values for the robot, put in one file for ease of access
    - `Controls.java`: all of the controls for the robot
    - `Ports.java`: all of the ports (CanIDs, RoboRIO ports, etc) for the robot
    - `Robot.java`: the start of the code, edit the attached Subsystems, selected TeleopControllers,
    selected Autonomous Path, and what Subsystems to debug.
    - `RobotSim.java`: the start of the code for simulation, otherwise the same as `Robot.java`
    - `Superstructure.java`: the master statemachine for the robot
- `frc.team5104.auto`: contains all necessary non-robot specific files for autonomous to function
  - `AutoAction.java`: a framework for all actions inside `frc.team5104.auto.actions`
  - `AutoManager.java`: manages the running of an autonomous path
  - `AutoPath.java`: a framework for all paths inside `frc.team5104.auto.paths`
  - `Odometry.java`: keeps of the position of the robot relative to the field (in x, y, angle)
  - `Position.java`: a class that helps store a field-relative position in feet
- `frc.team5104.auto.actions`: contains all actions that can be run in autonomous. 
  Actions are pieces of paths such as running a trajectory or entering intake mode.
- `frc.team5104.auto.paths`: contains all paths that can be run in autonomous
  Paths are a series of actions that work together to form an autonomous routine.
- `frc.team5104.subsystems`: contains all subsystems on the robot. Subsystems are smaller sections of the robot
  that usually consistent of a few sensors, motors, and/or pneumatic piston.
- `frc.team5104.teleop`: contains all the teleop controllers for the robot. Teleop controllers are controllers
  that run during teleop code, they translate user input (from Controls.java) into method calls to Subsystems directly
  are through the Superstructure first.
- `frc.team5104.util`: contains all utility files for the robot. Utility files are files that can help reduce
  duplicate code and ease in development of many of other things. Below are some noteable examples but there
  are far more inside the package.
  - `console.java`: use this class as an alternative for `System.out.println`. It handles file logging, time tracking,
  print locations, and makes logging significantly easier. This method works similar to `console.log()` in javascript.
  - `BreakerMath.java`: has some useful math functions, feel free to add-on to this file
  - `LatchedBoolean.java`: detects when a boolean changes from `true to false`, `false to true`, or both.
  - `MovingAverage.java`: averages values while input feeds in. This can be useful to de-noise sensor input.
  - `Plotter.java`: plots points on the BreakerBoard plotter (see `Webapp.java`)
  - `Tuner.java`: adds inputs or outputs to the BreakerBoard tuner (see `Webapp.java`)
  - `Units.java`: has many useful unit conversions, feel free to add-on to this file
  - `Webapp.java`: hosts the BreakerBoard Webapp. The webapp consists of two sections: Tuner and Plotter. The tuner
    is a realtime graph that display outputs from the code in graphical form and/or text form. The tuner also allows
    for inputs to be taken in a string form (can be casted to numbers) that allows for extremely fast realtime PID tuning.
    The plotter uses the Desmos API to plot points on a graph. This allows for trajectory graphing and real-time trajectory
    tracking.
  - `XboxController.java`: allows for separation of controls objects (see `Controls.java`) and processes complex 
    user input from an xbox controller including double clicking, holding buttons, and complex axis adjustment with
    `BezierCurve.java` and `Deadband.java`.
- `frc.team5104.util.managers`: contains all managers for the robot and corresponding files
  - `Subsystem.java`: a framework for all subsystem classes located inside `frc.team5104.subsystems`
  - `SubsystemManager.java`: a manager that handles all function calls from `Robot.java` to all the attached subsystems
  - `TeleopController.java`: a framework for teleop controllers located inside `frc.team5104.teleop`
  - `TeleopControllerManager.java`: a manager that handles all function calls from `Robot.java` to all the selected teleop controllers
- `frc.team5104.util.setup`: contains backend files for the robot code
    - `Main.java`: the actual start of the code. Simply starts `RobotController.java`
    - `RobotController.java`: extends `RobotBase.java` and handles all calls to `Robot.java`
    or `RobotSim.java` and updated values inside `RobotState.java`
    - `RobotState.java`: stores different values about the robots current state (enabled, mode, etc.)