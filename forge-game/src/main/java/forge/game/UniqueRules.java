
package forge.game;

import forge.card.CardType;
import forge.game.card.Card;
import forge.game.zone.ZoneType;

public final class UniqueRules {

    private UniqueRules() {
    }

    public static void exileOtherCopies(
            final Game game, final Card source) {

        if (source == null ||
                !source.getType().hasSupertype(
                        CardType.Supertype.Unique)) {
            return;
        }

        for (final Card other : game.getCardsInGame()) {

            // Never exile the copy that activated Unique.
            if (other.getId() == source.getId()) {
                continue;
            }

            // Only affect copies with the same name.
            if (!other.getName().equals(source.getName())) {
                continue;
            }

            // Already exiled cards can stay there.
            if (other.isInZone(ZoneType.Exile)) {
                continue;
            }

            game.getAction().exile(other, null, null);
        }
    }
}
