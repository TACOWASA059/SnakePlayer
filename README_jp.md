# Snake Player MOD
- Forge/Fabric 1.20.1

![image](https://github.com/user-attachments/assets/cc4da5a4-3b42-460c-a41b-5a5f1f0b3c25)

このModは、プレイヤーが「ヘビ化」することを可能にします。

ヘビの長さは経験値レベル（XpLevel）に応じて伸び、自動で移動し、体に触れた他のエンティティにダメージを与えることができます。
- プレイヤーは「ヘビモード」に変身可能。
- 経験値レベル + 1 に応じた長さのボディセグメントを持つ。
- ボディや動きの速度、ダメージ量などはコマンドで調整可能。
- プレイヤーのリスポーン位置を他プレイヤーと距離を取って分散させる設定も可能。

## 仕様（NBTデータ）
|NBTキー	|種類	|説明|
|-----|-----|-----|
|IsSnake	|Boolean	|ヘビモードかどうかを示すフラグ|
|SnakeHeadSize	|Float	|ヘビ時のプレイヤーの頭サイズ|
|SnakeBodySegmentSize	|Float	|ヘビ時のボディセグメントのサイズ|
|SnakeDamage	|Float	|セグメントが触れたときに与えるダメージ量|
|SnakeSpeed|	Float	|自動移動の速度（最大0.75）|

## コンフィグ
snakeplayer-common.toml に以下の項目が保存されます：

- 分散リスポーン設定（中心座標、範囲、最小距離）

- 初期設定（デフォルトの頭サイズ、ダメージ量、速度など）

- セグメントの経験値設定

## コマンド
### コンフィグ設定の変更 ```/snake config set```

|コマンドキー	|初期値	|範囲・型	|説明|
|------|------|------|------|
|spread|	false	|Boolean	|プレイヤーのリスポーン位置を分散させるかどうか|
|spread_pos|	0 0	|double, double	|分散リスポーンの中心座標（X, Z）|
|spread_Range	|50	|0.1 ～ 10000.0 (double)	|分散の範囲（正方形領域の一辺）|
|spread_minimum_distance|	5|	0.1 ～ 100.0 (double)|	他プレイヤーと最低限離す距離|
|segment_experience	|10	|0 以上の整数	|セグメントが落とす経験値量|
|default_is_snake	|true|	Boolean	|初期状態でヘビモードかどうか|
|default_head_size|	1	|0.001 ～ 100 (double)	|初期ヘビ頭サイズ|
|default_body_segment_size|1|	0.001 ～ 100 (double)	|初期ボディセグメントサイズ|
|default_damage	|1000|	0.0 ～ Float.MAX (double)|	初期ダメージ量|
|default_speed	|0.3|	0.0 ～ 1.5 (double)|	初期自動移動速度|
|spawn_block_view_distance	|8	|0 ～ 128 (int)	|プレイヤー視線方向にスポーン制限をかける最大距離|
|spawn_block_view_half_width	|2|0 ～ 64 (int)	|視線方向の左右にかけるスポーン制限幅（半分の長さ）|


### コンフィグ設定の確認 ```/snake config get <key>```

### プレイヤー設定の変更
```/snake <targets> <dataparameter_key> <value>```

対象プレイヤーに対して、ヘビ状態やパラメータ（サイズ・速度・ダメージなど）を直接設定できます。

- targets: 対象となるプレイヤー（例: @s, @a, @p, またはプレイヤー名）。

- dataparameter_key: 変更する属性のキー。以下から選択：

  - isSnake: true または false でヘビモードのON/OFFを切り替える。
  
  - headSize: ヘビの頭のサイズ（float, 範囲: 0.1 ～ 10.0）。
  
  - bodySegmentSize: 各ボディセグメントのサイズ（float, 範囲: 0.1 ～ 10.0）。
  
  - damage: セグメントが与えるダメージ（float, 範囲: 0.0 以上）。
  
  - speed: 自動移動の速度（float, 範囲: 0.0 ～ 1.5）。

![image](https://github.com/user-attachments/assets/811860e8-cffe-4b3f-b020-d5b2beb733ed)

