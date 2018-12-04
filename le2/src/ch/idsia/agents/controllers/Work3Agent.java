package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class Work3Agent extends BasicMarioAIAgent implements Agent {
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;
	int falling = 0;// 上昇中-1、通常時0、落下中1
	int marioModeSave;

	public Work3Agent() {
		super("Work3Agent");
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
		// fallingの設定
		if (isMarioOnGround) {
			falling = 0;
		} //着地するたびリセット→0(通常時)
		else if (getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 3, marioEgoCol) == 0) {
			falling = -1;
		} // マリオの3マス下が穴→-1(上昇中)
		else if (falling == -1 && getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == 0
				&& getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol) == 0) {
			falling = 1;
		} // falling = -1 かつ マリオの2マス下が穴 → 1(落下中)
		// fallingの設定終わり

		//基本動作(重要度最低→最初)
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_LEFT] = false;
		//基本動作終了

		//ファイアーマリオ攻撃
		if (marioMode == 2) {
			action[Mario.KEY_SPEED] = getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + 1) > 3
					|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) > 3
					|| getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 1) > 3;

		} else if (marioModeSave == 2) {
			action[Mario.KEY_SPEED] = false;
		}
		//ファイアマリオ攻撃終わり

		// bブロック;通常時
		if (falling <= 0) {
			if (isObstacle(marioEgoRow, marioEgoCol + 1)
					|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE
					|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE
					|| isHole(marioEgoCol + 1)) {
				action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
			}
			// 真横に障害物、敵が右2マスにいるときジャンプ
			if (falling == -1 && (getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) > 3
					|| getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + 1) > 3
					|| getEnemiesCellValue(marioEgoRow - 2, marioEgoCol + 2) > 3)) {
				action[Mario.KEY_RIGHT] = false;
				action[Mario.KEY_LEFT] = true;
			}//マリオがジャンプして上昇中に、右側から右上に敵がいたときは左移動
		}
		// bブロック;通常時終わり

		// cブロック;落下中
		else if (falling == 1) {
			if (!isHole(marioEgoCol) && isHole(marioEgoCol + 1)) {
				action[Mario.KEY_RIGHT] = false;
			}
			// 右が穴かつ下が陸地→右移動終了(重要)
			else if ((getEnemiesCellValue(marioEgoRow + 2, marioEgoCol + 3) > 3
					|| getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 2) > 3) && !isHole(marioEgoCol - 1)) {
				action[Mario.KEY_RIGHT] = false;
				action[Mario.KEY_LEFT] = true;
			}//落下中に右下に敵がいて左側は穴ではない→左移動
		}
		// cブロック;落下中終わり

		marioModeSave = marioMode;
		return action;
	}

}

/*
 * work2agentでは敵の回避は敵が目前に来たときにジャンプすることのみ よって、空中にいてジャンプできないときは回避できない
 * 回避すべき状況
 * 右斜め上に敵がいるのに目前に障害物があるからとジャンプ
 * 落下中で右斜め下に敵 右斜め上から敵が降ってくる
 * 敵を踏みつける動作は消したくない
 * 解決策1
 * 済;a落下中に右2マス目の地面に敵→左(右1マス目なら放置で踏みつけ?)
 * 上昇中に右斜め上に敵→左
 * (地面から3マス以内の敵との衝突は回避できない、落下中に右上に敵がいると悪手)
 * 右1マス上3マスに敵→ジャンプしない(目前に敵がいるならジャンプしたい)
 * 左を押している間は左側の敵&穴に注意
 *
 * 敵が目前にきたらファイアを出す(マリオの状態に依存) 常にファイアを1球飛ばしておく(マリオの状態に依存)
 *
 * 結果 a右3下2,右2下1に敵→そのままだと衝突 右1下3に敵→踏む
 * 条件のバッティングがあったので順番を入れ替えると、ファイアが機能してすべて解決した。
 */