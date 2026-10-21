package forge.game.ability.effects;

import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.spellability.SpellAbility;

/**
 * Activate or deactivate a device. Abilities parsed below the Inactive:
 * divider are suppressed until the device has been activated.
 *
 * Example: A:AB$ ActivateDevice | Cost$ 1 | SpellDescription$ Activate CARDNAME.
 * Deactivate: DB$ ActivateDevice | Deactivate$ True
 */
public class ActivateDeviceEffect extends SpellAbilityEffect {
    @Override
    public void resolve(final SpellAbility sa) {
        if (sa.usesTargeting()) {
            // Other cards can interface with devices, such as a Mnemonic
            // Device that activates another target Mnemonic Device.
            for (final Card device : getTargetCards(sa)) {
                if (device != null && device.isInPlay()) {
                    device.setDeviceActive(!sa.hasParam("Deactivate"));
                }
            }
        } else {
            final Card device = sa.getHostCard();
            if (device != null && device.isInPlay()) {
                device.setDeviceActive(!sa.hasParam("Deactivate"));
            }
        }
    }
}
