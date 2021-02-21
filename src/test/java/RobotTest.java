import com.team5104.lib.setup.RobotState;
import org.junit.Assert;

import java.io.IOException;

public class RobotTest {

  @org.junit.Test
  public void start() throws IOException {
    Assert.assertEquals(RobotState.isSimulation(), true);

    // TODO
  }

}

