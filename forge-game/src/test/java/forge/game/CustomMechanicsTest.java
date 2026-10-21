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
    public void inactiveSectionsGateFollowingAbilities() {
        final CardRules card = CardRules.fromScript(List.of(
                "Name:Test Device",
                "ManaCost:2",
                "Types:Artifact",
                "Overtypes:Memonic Device",
                "A:AB$ ActivateDevice | Cost$ 1 | SpellDescription$ Activate this device.",
                "Inactive:",
                "A:AB$ Draw | Cost$ T | NumCards$ 1 | SpellDescription$ Draw a card.",
                "T:Mode$ Phase | Phase$ Upkeep | Execute$ DrawOne | TriggerDescription$ Draw.",
                "SVar:DrawOne:DB$ Draw | NumCards$ 1",
                "Oracle:Test"
        ));
        final List<String> abilities = new java.util.ArrayList<>();
        card.getMainPart().getAbilities().forEach(abilities::add);
        assertEquals(abilities.size(), 2);
        assertFalse(abilities.get(0).contains("NeedsActiveDevice$ True"));
        assertTrue(abilities.get(1).contains("NeedsActiveDevice$ True"));

        final List<String> triggers = new java.util.ArrayList<>();
        card.getMainPart().getTriggers().forEach(triggers::add);
        assertTrue(triggers.get(0).contains("NeedsActiveDevice$ True"));
    }

    @Test
    public void inactiveKeywordIsNotGrantedUntilActivation() {
        final CardRules card = CardRules.fromScript(List.of(
                "Name:Test Device with Flying",
                "ManaCost:3",
                "Types:Artifact Creature Golem",
                "Overtypes:Memonic Device",
                "PT:2/2",
                "Inactive:",
                "K:Flying",
                "Oracle:Test"
        ));
        final List<String> keywords = new java.util.ArrayList<>();
        card.getMainPart().getKeywords().forEach(keywords::add);
        assertFalse(keywords.contains("Flying"));

        boolean gated = false;
        for (final java.util.Map.Entry<String, String> variable
                : card.getMainPart().getVariables()) {
            if (variable.getKey().startsWith("__InactiveKeyword")) {
                assertEquals(variable.getValue(), "Flying");
                gated = true;
            }
        }
        assertTrue(gated, "Inactive keyword must be retained for activation");
    }

    @Test
    public void multistrikeKeywordIsRecognized() {
        assertSame(Keyword.smartValueOf("Multistrike"), Keyword.MULTISTRIKE);
    }

    @Test
    public void deviceAttunedKeywordAndInterfaceScriptAreAvailable() {
        assertSame(Keyword.smartValueOf("Device-attuned"), Keyword.DEVICE_ATTUNED);
        final CardRules card = CardRules.fromScript(List.of(
                "Name:Device Technician",
                "ManaCost:1 U",
                "Types:Creature Human Wizard",
                "Overtypes:Humanoid",
                "PT:1/2",
                "K:Device-attuned",
                "A:AB$ InterfaceDevice | Cost$ 1 | ValidTgts$ Memonic Device"
                        + " | TargetMin$ 1 | TargetMax$ 1"
                        + " | SpellDescription$ Interface with a device.",
                "Oracle:Device-attuned"
        ));
        assertTrue(card.getType().hasOvertype("Humanoid"));
        assertFalse(card.getType().hasSubtype("Humanoid"));
        final List<String> keywords = new java.util.ArrayList<>();
        card.getMainPart().getKeywords().forEach(keywords::add);
        assertTrue(keywords.contains("Device-attuned"));
        final List<String> abilities = new java.util.ArrayList<>();
        card.getMainPart().getAbilities().forEach(abilities::add);
        assertTrue(abilities.get(0).contains("InterfaceDevice"));
    }

    @Test
    public void piercingKeywordIsRegistered() {
        assertSame(Keyword.smartValueOf("PIERCING"), Keyword.PIERCING);
    }
}
