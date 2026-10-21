package forge.game.ability.effects;

import forge.game.UniqueRules;
import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.spellability.SpellAbility;

/**
 * Exiles other instances of the card when a Unique trigger resolves.
 * The effect uses Forge's normal triggered-ability stack; it does not
 * happen if the triggered ability is countered.
 */
public class UniqueExileEffect extends SpellAbilityEffect {
    @Override
    public void resolve(final SpellAbility sa) {
        final Card source = sa.getHostCard();
        UniqueRules.exileOtherCopies(source.getGame(), source, sa);
    }
}
