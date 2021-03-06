package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class Work4_1RuleAgent extends BasicMarioAIAgent implements Agent {
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;
	int falling = 0;// 上昇中-1、通常時0、落下中1
	int marioModeSave;

	public Work4_1RuleAgent() {
		super("Work4_1RuleAgent");
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
	}//isHoleの定義
	
	public boolean isWall(int c) {
		return isObstacle(marioEgoRow, c)
				&& isObstacle(marioEgoRow -1, c)
				&& isObstacle(marioEgoRow -2, c)
				&& isObstacle(marioEgoRow -3, c)
				&& isObstacle(marioEgoRow -4, c)
				&& isObstacle(marioEgoCol -5, c);
	}//isWallの定義

	public boolean[] getAction() {
		// fallingの設定
		if (isMarioOnGround) {
			falling = 0;
		} // 着地するたびリセット→0(通常時)
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

		// 基本動作(重要度最低→最初)
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_LEFT] = false;
		// 基本動作終了

		// ファイアーマリオ攻撃
		if (marioMode == 2) {
			action[Mario.KEY_SPEED] = getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + 1) > 25
					|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) > 25
					|| getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 1) > 25
					|| getEnemiesCellValue(marioEgoRow + 2, marioEgoCol + 7) > 25;

		} else if (marioModeSave == 2) {
			action[Mario.KEY_SPEED] = false;
		}
		// ファイアマリオ攻撃終わり

		if(!isMarioAbleToJump &&
				((getReceptiveFieldCellValue(marioEgoRow+2, marioEgoCol+1)==-24
				    && getReceptiveFieldCellValue(marioEgoRow +2, marioEgoCol+2 )==0)
				||(getReceptiveFieldCellValue(marioEgoRow+1, marioEgoCol) == -24
				    &&getReceptiveFieldCellValue(marioEgoRow+1, marioEgoCol+1)==0
				    &&getReceptiveFieldCellValue(marioEgoRow + 3, marioEgoCol+3) == -24)
				)){ 
			action[Mario.KEY_RIGHT] = false;
		}
		else if (!isMarioAbleToJump 
				&& getReceptiveFieldCellValue(marioEgoRow+1, marioEgoCol) == -24
				&&getReceptiveFieldCellValue(marioEgoRow+1, marioEgoCol+1)==0
				&&getReceptiveFieldCellValue(marioEgoRow+ 3, marioEgoCol+3) == -24){
			action[Mario.KEY_LEFT] = true;
		}//着地後ジャンプまで
		else if (getReceptiveFieldCellValue(marioEgoRow - 3, marioEgoCol + 3) == -24) {
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !(isMarioOnGround || getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == 0);
		}
		//ブロックにのぼる
		
		// 通常時
		if (falling <= 0) {
			if (isObstacle(marioEgoRow, marioEgoCol + 1)
					|| isObstacle(marioEgoRow -1, marioEgoCol +1)
					|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE
					|| getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE
					|| isHole(marioEgoCol + 1)) {
				action[Mario.KEY_JUMP ] = isMarioAbleToJump || !(isMarioOnGround || getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == -24);
				if (getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + 2) > 25
						|| getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + 1) > 25) {
					action[Mario.KEY_RIGHT] = false;
				} // 右上に敵がいる時のジャンプを遅らせる
			}
			// 真横に障害物、敵が右2マスにいるときジャンプ
			// 壁が高いときに障害物を使って乗り越える
			if (falling == -1 && (getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) > 25
					|| getEnemiesCellValue(marioEgoRow - 1, marioEgoCol + 1) > 25
					|| getEnemiesCellValue(marioEgoRow - 2, marioEgoCol + 2) > 25)) {
				action[Mario.KEY_RIGHT] = false;
				action[Mario.KEY_LEFT] = true;

			}
		}
		// 通常時終わり

		// 落下中
		else if (falling == 1) {
			if (!isHole(marioEgoCol) && isHole(marioEgoCol + 1)
					|| (isHole(marioEgoCol + 2) && !isHole(marioEgoCol+1))) {
				action[Mario.KEY_RIGHT] = false;
			}
			// 右が穴かつ下が陸地→右移動終了(重要)
			else if ((getEnemiesCellValue(marioEgoRow + 2, marioEgoCol + 3) > 25
					|| getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 2) > 25) && !isHole(marioEgoCol - 1) && !isHole(marioEgoCol)) {
				action[Mario.KEY_RIGHT] = false;
				action[Mario.KEY_LEFT] = true;
			}
		}
		// 落下中終わり

		marioModeSave = marioMode;
		return action;
	}

}

// 右側に高さ5以上の壁→左に行ってブロックを利用して飛び越える(無理)、それより前に上に登っておく。
// その壁を越えたあと、逆に上に行くと壁に出くわす
// 同じ座標に2人の敵→ファイア2発発射or落下時敵回避能力向上
// ジャンプして敵に突っ込むときの回避
//3マス上ブロック、4マス上敵なし→ジャンプ
//isMarioAbleToJumpはjumpボタンtrue時はfalse
//isMarioOnGroundはブロック上はfalse
//ジャンプ後ある程度長押ししないと小ジャンプ→!isMarioOnGroundが使われる
//jumpをfalseにしないとブロック上でジャンプしない