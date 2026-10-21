package forge.game;

import forge.game.card.Card;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;

/**
 * Resolves the effect of a Unique triggered ability.
 *
 * This must only be called when the triggered ability resolves, not directly
 * from a cast or zone-change event. Once triggered, it does not depend on the
 * originating card remaining in its original zone or retaining Unique.
 */
public final class UniqueRules {
    private UniqueRules() {
    }

    public static void exileOtherCopies(final Game game, final Card source, final SpellAbility cause) {
        if (game == null || source == null) {
            return;
        }

        // Printed card identity, not a temporary name changed by an effect.
        final String cardName = printedName(source);

        // getCardsInGame makes a snapshot across all players' zones, including
        // hidden zones, the command zone and the stack.
        for (final Card other : game.getCardsInGame()) {
            if (other.getId() == source.getId() || other.isInZone(ZoneType.Exile)) {
                continue;
            }
            if (cardName.equals(printedName(other))) {
                game.getAction().exile(other, cause, null);
            }
        }
    }

    private static String printedName(final Card card) {
        return card.getPaperCard() != null
                ? card.getPaperCard().getName()
                : card.getName();
    }
}
