package com.zonez.donutah;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class AuctionHouseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("ah")
                .executes(context -> open(""))
                .then(literal("regear").executes(context -> open("regear")))
                .then(argument("query", StringArgumentType.greedyString())
                    .executes(context -> open(StringArgumentType.getString(context, "query"))))
            )
        );
    }

    private static int open(String query) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new AuctionScreen(query)));
        return 1;
    }
}
