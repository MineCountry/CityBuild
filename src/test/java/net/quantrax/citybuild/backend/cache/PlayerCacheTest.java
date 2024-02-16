package net.quantrax.citybuild.backend.cache;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.quantrax.citybuild.global.CityBuildPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCacheTest {

    private PlayerMock player;
    private PlayerCache cache;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();

        player = server.addPlayer();
        cache = PlayerCache.getInstance();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testTrack() {
        cache.track(player);
        assertNotNull(cache.get(player));
    }

    @Test
    void testUntrack() {
        cache.track(player);
        cache.untrack(player);

        CityBuildPlayer cityBuildPlayer = cache.get(player);
        assertNull(cityBuildPlayer);
    }

    @Test
    void testGet() {
        cache.track(player);
        CityBuildPlayer cityBuildPlayer = cache.get(player);

        assertNotNull(cityBuildPlayer);
        assertEquals(player.getUniqueId(), cityBuildPlayer.uuid());
    }
}