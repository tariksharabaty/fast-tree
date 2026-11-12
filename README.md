<p align="center"><img src=".github/assets/icon.png" width="96" alt="🌲 Fast Tree"></p>

<h1 align="center">🌲 Fast Tree</h1>

<p align="center"><b>Chop the trees faster, faster, faster!</b></p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.1.2-0A7BBB?style=flat-square&labelColor=555555" alt="version 1.1.2">
  <img src="https://img.shields.io/badge/Minecraft-26.3-62B47A?style=flat-square&labelColor=555555" alt="Minecraft 26.3">
  <img src="https://img.shields.io/badge/loader-Fabric-DBD0B4?style=flat-square&labelColor=38342A" alt="loader Fabric">
  <img src="https://img.shields.io/badge/Java-25-E76F00?style=flat-square&labelColor=555555&logo=openjdk&logoColor=white" alt="Java 25">
  <img src="https://img.shields.io/badge/license-MIT-F1C40F?style=flat-square&labelColor=555555" alt="license MIT">
  <a href="https://github.com/tariksharabaty/o-lib"><img src="https://img.shields.io/badge/requires-O%20Lib-8957E5?style=flat-square&labelColor=555555" alt="requires O Lib"></a>
  <a href="https://modrinth.com/organization/oidioomstudio"><img src="https://img.shields.io/badge/Modrinth-Oidioom%20Studio-1BD96A?style=flat-square&labelColor=555555&logo=modrinth&logoColor=white" alt="Modrinth Oidioom Studio"></a>
  <a href="https://oidioom.com"><img src="https://img.shields.io/badge/by-Oidioom-0D1117?style=flat-square&labelColor=555555" alt="by Oidioom"></a>
</p>

<p align="center"><a href="#-english">🇬🇧 English</a> · <a href="#-türkçe">🇹🇷 Türkçe</a></p>

---

## 🇬🇧 English

> [!IMPORTANT]
> Requires **[O Lib](https://github.com/tariksharabaty/o-lib)** and **Fabric API**.

**Fast Tree** brings the classic "Tree Capitator" / "Chop Down" experience into modern Minecraft 26.3. Break the bottom log of any tree, and watch the entire trunk fall into your hands, accompanied by satisfying sound effects and rapid leaf decay.

---

## ✨ Key Features

- 🌲 **Instant Tree Felling:** Chop a single log to bring down the entire tree trunk instantly.
- 🍂 **Rapid Leaf Decay:** Leaves don't float indefinitely in the air! Fast Tree queues connected leaves to rapidly decay with sound and particle effects, dropping saplings, sticks, and apples smoothly.
- 🏡 **Intelligent House / Build Protection:** Ensures the scanned log structure actually has connected natural leaves before felling. You will never accidentally destroy your wooden cabin or house roof!
- 🛡️ **Tool Breakage Safeguard:** Never break your Netherite Axe by accident. Fast Tree halts chopping if your tool reaches critical durability.
- 🎒 **Auto-Collection (Teleport Drops):** Option to have all logs and saplings instantly teleported into your inventory.
- 🧎 **Flexible Sneak Modes:** Configure whether felling is always active, only active when sneaking, or disabled when sneaking.

---

## ⚙️ In-Game Configuration

Configure live in-game using **O Lib** and **ModMenu**:
- `enabled`: Global toggle for tree felling.
- `leafProtection`: Require leaves near the tree to prevent felling wooden structures.
- `breakLeaves`: Enable fast leaf decay.
- `maxLogs`: Maximum logs that can be harvested in a single chop (Default: 256).
- `durabilityMultiplier`: Axe durability consumed per log.
- `sneakMode`: Choose between `ALWAYS`, `SNEAK_ONLY`, or `SNEAK_DISABLE`.

---

## 📥 Installation

1. Install **Minecraft 26.3** with **Fabric Loader**.
2. Install **Fabric API**.
3. Install **O Lib** (required core library).
4. Drop `fast-tree-1.1.2-mc26.3.jar` into your `mods` folder.

---

## ⬇️ Download

- **Modrinth:** [Oidioom Studio](https://modrinth.com/organization/oidioomstudio)
- **GitHub Releases:** [`fast-tree-1.1.2-mc26.3.jar`](../../releases/latest)

## 🔨 Building from source

Requires **JDK 25**.

```bash
# 1) O Lib is a dependency — publish it to your local Maven first
git clone https://github.com/tariksharabaty/o-lib.git
(cd o-lib && ./gradlew publishToMavenLocal)

# 2) build this mod
./gradlew build
# output: build/libs/fast-tree-1.1.2-mc26.3.jar
```

## 📄 License

[MIT](LICENSE) © 2026 Oidioom / Tarek Sharabaty

---

## 🇹🇷 Türkçe

**Tek vuruş, bütün ağaç yerde. Akıllı yaprak koruması ve hızlı yaprak çürümesiyle.**

> [!IMPORTANT]
> **[O Lib](https://github.com/tariksharabaty/o-lib)** ve **Fabric API** gerektirir.

### ✨ Özellikler

- Tek kütüğü keserek bütün gövdeyi anında devirme
- Bağlı yaprakların hızlı çürümesi
- Ev ve yapı koruması: yalnızca gerçek ağaçları keser
- Alet kırılma koruması ve düşenleri envantere ışınlama
- Esnek eğilme modları ve otomatik fidan dikme

### 📥 Kurulum

1. **Minecraft 26.3** için **Fabric Loader** kurun.
2. **Fabric API** ve **O Lib** modlarını `mods` klasörüne ekleyin.
3. `fast-tree-1.1.2-mc26.3.jar` dosyasını `mods` klasörüne atın ve oyunu başlatın.

### ⬇️ İndir

- **Modrinth:** [Oidioom Studio](https://modrinth.com/organization/oidioomstudio)
- **GitHub Releases:** [`fast-tree-1.1.2-mc26.3.jar`](../../releases/latest)

### 📄 Lisans

[MIT](LICENSE) © 2026 Oidioom / Tarek Sharabaty

---

<p align="center"><a href="https://oidioom.com"><picture><source media="(prefers-color-scheme: dark)" srcset=".github/assets/oidioom-dark.svg"><img src=".github/assets/oidioom-light.svg" width="40" alt="Oidioom"></picture></a></p>
<p align="center"><b>by Oidioom — Tarek Sharabaty</b><br>
<a href="https://oidioom.com">oidioom.com</a> · <a href="https://github.com/tariksharabaty">GitHub</a> · <a href="https://modrinth.com/organization/oidioomstudio">Modrinth</a> · <a href="https://gear.oidioom.com">Gear</a> · <a href="https://www.instagram.com/oidioom">Instagram</a></p>
