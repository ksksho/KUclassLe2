package arrangeProgram;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;
import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;
import ch.idsia.scenarios.MainTask2;


/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */

public class Main2Arrange{
public static void main(String[] args)
{
    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);

    final Agent agent = new ForwardJumpingAgent();
    marioAIOptions.setAgent(agent);
    marioAIOptions.setEnemies("off");
    
    int seed = 99;
    marioAIOptions.setLevelRandSeed(seed);
    
    final BasicTask basicTask = new BasicTask(marioAIOptions);
    basicTask.setOptionsAndReset(marioAIOptions);
    basicTask.doEpisodes(1,true,1);
    System.exit(0);
}

}