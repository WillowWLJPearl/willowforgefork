package forge.game;

import forge.card.CardRules;
import forge.card.CardType;
import forge.game.keyword.Keyword;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class CustomMechanicsTest {
    @Test
    public void overtypesAreIndependentOfCreatureSubtypes() {
        final CardRules card = CardRules.fromScript(List.of(
                "Name:Test Scorpion",
                "ManaCost:2 B",
                "Types:Unique Creature Scorpion Human",
                "Overtypes:Humanoid,Memonic Device",
                "PT:1/3",
                "Oracle:Test"
        ));
        final CardType types = card.getType();
        assertTrue(types.hasOvertype("Humanoid"));
        assertTrue(types.hasOvertype("Memonic Device"));
        assertTrue(types.hasStringType("Humanoid"));
        assertFalse(types.hasSubtype("Humanoid"));
        assertFalse(types.hasCreatureType("Humanoid"));
        assertTrue(types.hasSupertype(CardType.Supertype.Unique));
        assertTrue(types.toString().contains("Memonic Device"));
    }

    @Test
    public void overtypesSurviveCopiesAndCombination() {
        CardType original = new CardType(false);
        original.add("Creature");
        original.addOvertype("Humanoid");
        CardType copy = new CardType(original);
        CardType combined = CardType.combine(original, new CardType(false));
        assertTrue(copy.hasOvertype("Humanoid"));
        assertTrue(combined.hasOvertype("Humanoid"));
        copy.removeOvertype("Humanoid");
        assertFalse(copy.hasOvertype("Humanoid"));
        assertTrue(original.hasOvertype("Humanoid"));
    }

    @Test
    public void piercingKeywordIsRegistered() {
        assertSame(Keyword.smartValueOf("PIERCING"), Keyword.PIERCING);
    }
}
