package forge.game.ability.effects;

import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.keyword.Keyword;
import forge.game.spellability.SpellAbility;

/**
 * Interface a Memonic Device with its Humanoid/Device-attuned user.
 *
 * The source may be the device (target the user) or the user (target
 * the device). A device can have one user at a time.
 *
 * A:AB$ InterfaceDevice | Cost$ 1 | ValidTgts$ Humanoid
 * A:AB$ InterfaceDevice | Cost$ 1 | ValidTgts$ Memonic Device
 */
public class InterfaceDeviceEffect extends SpellAbilityEffect {
    @Override
    public void resolve(final SpellAbility sa) {
        final Card source = sa.getHostCard();
        if (source == null || !source.isInPlay()) {
            return;
        }

        for (final Card target : getTargetCards(sa)) {
            if (target == null || !target.isInPlay()) {
                continue;
            }

            if (source.getType().hasOvertype("Memonic Device")) {
                if (target.getType().hasOvertype("Humanoid")
                        || target.hasKeyword(Keyword.DEVICE_ATTUNED)) {
                    source.setDeviceUser(target);
                }
            } else if (target.getType().hasOvertype("Memonic Device")
                    && (source.getType().hasOvertype("Humanoid")
                    || source.hasKeyword(Keyword.DEVICE_ATTUNED))) {
                target.setDeviceUser(source);
            }
        }
    }
}
