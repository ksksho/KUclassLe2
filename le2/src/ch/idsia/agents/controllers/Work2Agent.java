package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;

public class Work2Agent extends BasicMarioAIAgent implements Agent {
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;
	int falling = 0;// 上昇中-1、通常時0、落下中1

	public Work2Agent() {
		super("Work2Agent");
		reset();
	}

	public void reset() {
		action = new boolean[Environment.numberOfKeys];
	}

	public boolean isObstacle(int r, int c) {
		return getReceptiveFieldCellValue(r, c) == GeneralizerLevelScene.BRICK
				|| getReceptiveFieldCellValue(r, c) == GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
				|| getReceptiveFieldCellValue(r, c) == GeneralizerLevelScene.FLOWER_POT_OR_CANNON
				|| getReceptiveFieldCellValue(r, c) == GeneralizerLevelScene.LADDER;
	}
	// isObstacleの定義

	public boolean isHole(int c) {
		return getReceptiveFieldCellValue(marioEgoRow + 1, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 2, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 3, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 4, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 5, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 6, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 7, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 8, c) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 9, c) == 0;
	}

	public boolean[] getAction() {
		if (isObstacle(marioEgoRow, marioEgoCol + 1)
				|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE
				|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE
				|| isHole(marioEgoCol + 1)) {
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		// 真横に障害物、敵が右2マスにいるときジャンプ
				
		if (isMarioOnGround) {
			falling = 0;
		}
		if (getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 3, marioEgoCol) == 0) {
			falling = -1;
		} // 3マス下が穴→-1
		if (falling == -1 && getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol) == 0) {
			falling = 1;
		} // falling = -1 かつ 2マス下が穴 → 1
		if (falling == 1 && !isHole(marioEgoCol) && isHole(marioEgoCol + 1)) {
			action[Mario.KEY_RIGHT] = false;
		}
		// 落下中かつ右が穴かつ下が陸地
		else if (!isMarioOnGround || !isHole(marioEgoCol + 1)) {
			action[Mario.KEY_RIGHT] = true;
		} // 以下の場合以外;落下中かつ右が穴かつ下が陸地、地上かつ右が穴
		return action;
	}

}
