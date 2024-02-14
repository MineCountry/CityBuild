package net.quantrax.citybuild.utils;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Preconditions {

    @FormatMethod
    public static void state(boolean expression, @NotNull @FormatString String template, @Nullable Object... args) {
        if (expression) return;
        throw new IllegalStateException(String.format(template, args));
    }
}
