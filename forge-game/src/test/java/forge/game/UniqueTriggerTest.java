package forge.game;

import forge.card.CardRules;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class UniqueTriggerTest {
    private static CardRules createCard(final String type) {
        return CardRules.fromScript(List.of(
                "Name:Test Scorpion",
                "ManaCost:2 B",
                "Types:" + type,
                "PT:1/3",
                "Oracle:Test card"
        ));
    }

    private static List<String> triggers(final CardRules rules) {
        final List<String> result = new ArrayList<>();
        rules.getMainPart().getTriggers().forEach(result::add);
        return result;
    }

    @Test
    public void uniqueAddsTwoCounterableTriggeredAbilities() {
        final CardRules rules = createCard("Unique Creature Scorpion");
        final List<String> triggers = triggers(rules);

        assertEquals(triggers.size(), 2);
        assertTrue(triggers.stream().anyMatch(t -> t.contains("Mode$ SpellCast")
                && t.contains("TriggerZones$ Stack")
                && t.contains("Execute$ UniqueRuleExile")));
        assertTrue(triggers.stream().anyMatch(t -> t.contains("Mode$ ChangesZone")
                && t.contains("Destination$ Battlefield")
                && t.contains("ExcludedOrigins$ Battlefield")
                && t.contains("RequireOrigin$ True")
                && t.contains("Execute$ UniqueRuleExile")));

        boolean hasExileAbility = false;
        for (final Map.Entry<String, String> variable : rules.getMainPart().getVariables()) {
            if (variable.getKey().equals("UniqueRuleExile")) {
                assertTrue(variable.getValue().contains("DB$ UniqueExile"));
                hasExileAbility = true;
            }
        }
        assertTrue(hasExileAbility);
    }

    @Test
    public void ordinaryCardsDoNotGetUniqueTriggers() {
        final CardRules rules = createCard("Creature Scorpion");
        final List<String> triggers = triggers(rules);

        assertFalse(triggers.stream().anyMatch(t -> t.contains("UniqueRuleExile")));
    }
}
