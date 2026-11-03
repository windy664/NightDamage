<div align="center">

<img src="assets/banner.svg" alt="NightDamage" width="100%">

# NightDamage

**黑暗伤害** —— 身处光照不足的位置持续受到伤害，黑暗本身成为威胁；恐怖包/生存挑战服利器。

![Platform](https://img.shields.io/badge/Platform-Spigot%20%2F%20Paper-62bda8?style=flat-square&logo=minecraft&logoColor=white)
![Minecraft](https://img.shields.io/badge/MC-1.20.x-8a9a5b?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-f89820?style=flat-square&logo=openjdk&logoColor=white)

[功能](#-功能) · [配置](#️-配置)

</div>

## ✨ 功能

- **光照阈值检测**：按 `check-frequency` 周期检查玩家所在位置的 `LightLevel`，低于 `light-threshold` 即进入黑暗威胁
- **持续伤害**：黑暗中按 `damage` 扣血；`damage-till-death` 开启后直接致死（"黑暗袭击了你"）
- **分阶段提示**：轻度黑暗与重度黑暗使用不同文案（`message` / `message2`）
- **世界范围**：`enabled-worlds` 列表控制生效世界（主世界/下界/末地/空岛世界等）
- **管理命令**：重载配置与临时切换 debug

## ⚙️ 配置

```yaml
enabled: true
enabled-worlds:
  - world
  - world_the_end
  - world_nether
light-threshold: 1         # 光照低于此值受伤害
check-frequency: 20        # 检查周期（tick）
damage: 2.0
damage-till-death: true
message: '§o§d黑暗袭击了你'
message2: '§o§d你感到一丝黑暗，心跟碎了一样'
```

## 🔨 构建

```bash
./gradlew build
```
