# Willow Forge custom MTG mechanics

This file describes rules implemented in the fork and distinguishes them from work that still needs combat-engine changes.

## Unique (implemented; in-game tests needed)

Use `Types:Unique Creature Scorpion Human`.

On **casting a Unique spell**, and separately when a Unique permanent **enters the battlefield from another zone**, a *triggered ability* goes on the stack. On resolution, all other cards with the same **printed card name** throughout the game are exiled. A countered triggered ability has no exile effect. The source card itself is never exiled by its own trigger. The same printed name owned by an opponent counts. Cards already exiled stay exiled.

Unique **does not grant commander eligibility**; a nonlegendary Unique commander needs explicit oracle text that grants it.

## Overtype (implemented; runtime tests needed)

```text
Types:Unique Creature Scorpion Human
Overtypes:Humanoid,Memonic Device
```

Overtypes are stored separately from supertypes, core card types and subtypes. Their labels appear in the type line; they have no native rules consequences. Effects can test them using the normal card-type selector, such as `ValidCards$ Humanoid`. They **do not** count as creature subtypes and do not interact with Changeling/creature-type selection. An overtype can include spaces and remains one name when declared in comma-separated `Overtypes:`.

## Piercing (damage assignment implemented; in-game tests needed)

```text
K:Piercing
```

During each combat damage step in which it assigns damage, this attacker assigns its **full combat damage** to the player/planeswalker/battle it attacks and **each creature currently blocking it**, even when blocked. Normal combat damage, prevention, replacement and damage consequences still apply. This is not trample and does not divide its damage between recipients.

## INACTIVE, Activate and Device-attuned (implemented; gameplay tests needed)

Devices begin inactive. `Inactive:` marks any following activated abilities, triggers, static abilities, replacement effects and keywords as disabled until the device is active. Activation abilities must be **above** the divider so they remain available.

```text
Name:Example Device
ManaCost:2
Types:Artifact
Overtypes:Memonic Device
K:Inactive
A:AB$ ActivateDevice | Cost$ 1 | SpellDescription$ Activate CARDNAME.
A:AB$ InterfaceDevice | Cost$ 1 | ValidTgts$ Humanoid | TargetMin$ 1 | TargetMax$ 1 | SpellDescription$ Interface with target Humanoid.
Inactive:
A:AB$ Draw | Cost$ T | NumCards$ 1 | SpellDescription$ Draw a card.
K:Flying
Oracle:Inactive — The following abilities do nothing until this device is activated.
```

`ActivateDevice` makes the source device active on resolution. With `ValidTgts$ Memonic Device`, it instead activates a targeted device. `Deactivate$ True` turns the device inactive again. Activation or deactivation updates its available abilities, triggers, keywords and static effects.

`InterfaceDevice` links a Memonic Device to an eligible user (a Humanoid or a creature with `K:Device-attuned`). The device becomes an attachment to that user. When a device with a user is activated, it taps that user. A Device-attuned user **untaps instead of tapping**. An already tapped unattuned user cannot operate the device again without untapping.

```text
Name:Masterful Device User
ManaCost:1 U
Types:Creature Human Wizard
Overtypes:Humanoid
PT:1/2
K:Device-attuned
A:AB$ InterfaceDevice | Cost$ 1 | ValidTgts$ Memonic Device | TargetMin$ 1 | TargetMax$ 1 | SpellDescription$ Interface with target Memonic Device.
Oracle:Device-attuned
```

Device-active state is stored on the card instance and does not survive its replacement after changing zones.

## Multistrike N (ordered combat-round implementation; gameplay tests needed)

```text
K:Multistrike:3
```

When the creature attacks, its controller can select **up to N different defending players, planeswalkers or battles**, including the initial defender, and choose their attack order. After the first attack's damage, the combat loops through fresh attacker, blocker, and first-strike/regular damage steps for each remaining defender. Each defending player independently declares blockers. Begin/end-of-combat triggers occur only once for the overall combat. This uses the native combat damage engine for each defender. If the attacking creature leaves the battlefield or ceases to be able to attack, its remaining queued attacks do not occur.

The implementation is present in `forge-game/src/main/java/forge/game/phase/PhaseHandler.java`, but still needs a live-game integration test covering multiple defenders, blocker timing, priority, replacement effects, and removal between attacks.

## Verification

The `Custom MTG mechanics tests` GitHub Actions workflow compiles and runs the Java 17 `forge-game` test suite. These are parser and rules-engine regression tests; they do **not** constitute proof that every gameplay edge case works. In-game tests are still needed for countering Unique, combat with multiple blocking creatures, timing of Multistrike subattacks, and activation/interfacing of devices.
