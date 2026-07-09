# Selas

Реалистичное освещение для Minecraft на **NeoForge 1.21.1**.

Начинается с реалистичной тьмы (убирается минимальный порог яркости, который в ванили
делает даже light level 0 «не совсем чёрным»), дальше — цветовая температура источников,
адаптация глаза и прочее.

## Сборка

- Требуется **JDK 21** (Minecraft 1.21.1 поставляется на Java 21).
- `./gradlew build` — собирает мод-jar в `build/libs/`.
- `./gradlew runClient` — запуск игры с модом для ручной проверки.

> Примечание: в `build.gradle` отключена рекомпиляция Minecraft (`disableRecompilation = true`),
> чтобы сборка не зависела от декомпилятора (vineflower) — для компиляции мода это не нужно.
> Если хочешь видеть исходники MC в IDE, уберИ эту опцию (понадобится больше RAM).

## Структура

- `com.selas.Selas` / `SelasClient` — точки входа (common / client).
- `com.selas.lighting.LightmapEngine` — ядро: пересчёт таблицы целевой яркости `LUMINANCE[16][16]`
  и применение к текстуре lightmap перед отправкой на GPU.
- `com.selas.lighting.config.Config` — конфиг (NeoForge `ModConfigSpec`).
- `com.selas.lighting.LightmapAccess` / `TextureAccess` — контракты между миксинами и ядром.
- `com.selas.mixin.*` — миксины в `LightTexture`, `DynamicTexture`, `GameRenderer` и эффекты
  измерений (`EndEffects` / `NetherEffects`).

## Конфиг (в игре: Mods → Selas → Config)

- По измерениям: Overworld / Nether / End / default / skyless.
- Яркость тумана Nether / End.
- «Только блочный свет» и «игнорировать фазу луны».

## Дорожная карта

- [x] Фаза 0 — каркас + подключение миксинов (NeoForge 1.21.1)
- [x] Фаза 1 — реалистичная тьма (MVP, играбельно)
- [ ] Фаза 2 — цветовая температура источников (тёплый торш, нейтральное солнце, холодная луна)
- [ ] Фаза 3 — адаптация глаза (корректно, без ущерба играбельности)
- [ ] Фаза 4 — туман / рассеивание
- [ ] Фаза 5 — совместимость (Iris / OptiFine / Distant Horizons), релиз

Лицензия: MIT.
