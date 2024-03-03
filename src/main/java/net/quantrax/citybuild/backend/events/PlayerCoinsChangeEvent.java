package net.quantrax.citybuild.backend.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quantrax.citybuild.global.CityBuildPlayer;

@Getter
@RequiredArgsConstructor
public class PlayerCoinsChangeEvent extends CityBuildEvent {

    private final CityBuildPlayer player;
    private final int before;
    private final int after;
    private final ChangeType changeType;

    public enum ChangeType {
        ADD, REMOVE, SET
    }
}