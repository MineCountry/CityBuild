package net.quantrax.citybuild.backend.global;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.quantrax.citybuild.global.CityBuildPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CityBuildPlayerTest {

    private CityBuildPlayer cityBuildPlayer;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        PlayerMock player = server.addPlayer();

        cityBuildPlayer = CityBuildPlayer.create(player);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testAddCoins() {
        cityBuildPlayer.addCoins(100);
        assertEquals(100, cityBuildPlayer.coins());
    }

    @Test
    void testRemoveCoins() {
        cityBuildPlayer.setCoins(100);
        cityBuildPlayer.removeCoins(50);
        assertEquals(50, cityBuildPlayer.coins());
    }

    @Test
    void testSetCoins() {
        cityBuildPlayer.setCoins(100);
        assertEquals(100, cityBuildPlayer.coins());
    }

    @Test
    void testAddNegativeCoins() {
        assertThrows(IllegalStateException.class, () -> cityBuildPlayer.addCoins(-100));
    }

    @Test
    void testRemoveNegativeCoins() {
        assertThrows(IllegalStateException.class, () -> cityBuildPlayer.removeCoins(-100));
    }

    @Test
    void testRemoveMoreCoinsThanAvailable() {
        cityBuildPlayer.setCoins(50);
        assertThrows(IllegalStateException.class, () -> cityBuildPlayer.removeCoins(100));
    }
}