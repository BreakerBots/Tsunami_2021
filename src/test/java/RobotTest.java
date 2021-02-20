import com.team5104.lib.setup.RobotState;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class RobotTest {

  @org.junit.Test
  public void start() throws IOException {
    Assert.assertEquals(RobotState.isSimulation(), true);

    // make sure every subsystem calls setDevices()
    File[] files = new File("src/main/java/com/team5104/frc2021/subsystems/").listFiles();

    for (File file : files) {
      Scanner scan = new Scanner(file);
      Assert.assertTrue(file.getName()+" does not contain the setDevices() method!", scan.findAll("setDevices").count() > 0);
    }

    // TODO
  }

}

