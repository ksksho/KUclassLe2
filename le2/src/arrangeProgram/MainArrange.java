package arrangeProgram;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 * Arranged by Sho Kinoshita
 */

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

public final class MainArrange {
	public static void main(String[] args)
	{
	    final MarioAIOptions marioAIOptions = new MarioAIOptions(args);
	    
	    int seed = 1;
	    marioAIOptions.setLevelRandSeed(seed);
	    int d = 100;
	    marioAIOptions.setLevelDifficulty(d);
	    marioAIOptions.setEnemies("gwgkwrkwsw");
	    final BasicTask basicTask = new BasicTask(marioAIOptions);
	    basicTask.setOptionsAndReset(marioAIOptions);
	    basicTask.doEpisodes(1,true,1);
	    System.exit(0);
	}

	}

