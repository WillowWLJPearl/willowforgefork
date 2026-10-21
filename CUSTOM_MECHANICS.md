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

## INACTIVE and device activation (functional core; integration tests needed)

Devices begin *inactive* as new card instances. `Inactive:` in a card script marks the following **activated abilities, triggers, static abilities and replacement effects** as disabled until the device is active. The activation ability itself must appear **above** the divider so it remains usable:

```text
Name:Example Device
ManaCost:2
Types:Artifact
Overtypes:Memonic Device
K:Inactive
A:AB$ ActivateDevice | Cost$ 1 | SpellDescription$ Activate CARDNAME.
Inactive:
A:AB$ Draw | Cost$ T | NumCards$ 1 | SpellDescription$ Draw a card.
Oracle:Inactive — The following abilities do nothing until this device is activated.
```

The `ActivateDevice` API sets the device to its active state when the ability resolves; `Deactivate$ True` on the API changes it back to inactive. With `ValidTgts$ Memonic Device`, it activates the targeted device(s) instead of its host, so cards can interface with other devices. Example: `A:AB$ ActivateDevice | Cost$ 1 T | ValidTgts$ Memonic Device | TargetMin$ 1 | TargetMax$ 1 | SpellDescription$ Activate target Memonic Device.` Activating refreshes available abilities and triggers. **Keyword-granted abilities below the divider are not yet automatically gated.** Interaction with Device-attuned and attachment/interfacing rules still needs implementation.

## Multistrike N (syntax only, NOT gameplay-complete)

```text
K:Multistrike:2
```

The keyword and its reminder text are registered. The desired rule is to choose up to N different defending players/objects when declaring attackers, choose their order, and have separate attacks with each defender being able to block. If the attacking creature is destroyed in an earlier combat, it cannot continue to a later defender.

**These combat changes are NOT yet implemented.** Forge's current `Combat` data model assigns a single defender and blocking band to an attacker. Supporting separate sequential combats needs changes to attacker selection, turn/combat sequencing, per-defender blocking and damage, and UI/AI. Do not treat the registered keyword as functioning in a match.

## Verification

The Java 17 `Custom MTG mechanics tests` GitHub workflow compiles and runs the `forge-game` test suite. Gameplay testing for triggered-ability countering, entering from nonbattlefield zones, Piercing with multiple blockers, and device-activation timing is still needed.
