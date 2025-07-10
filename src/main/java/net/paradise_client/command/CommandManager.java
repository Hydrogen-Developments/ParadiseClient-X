package net.paradise_client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.command.impl.*;

import java.util.ArrayList;

/**
 * Manages and registers commands for the Paradise Client Fabric mod.
 */
public class CommandManager {

    /**
     * The command dispatcher used to register commands.
     */
    public final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
    /**
     * The command dispatcher prefix used to execute commands.
     */
    public final String prefix = ",";
    /**
     * A list of all registered commands.
     */
    private final ArrayList<Command> commands = new ArrayList<>();
    /**
     * The {@link MinecraftClient} instance.
     */
    private final MinecraftClient minecraftClient;


    /**
     * Constructs a new CommandManager instance and registers all commands.
     */
    public CommandManager(MinecraftClient minecraftClient) {
        this.minecraftClient = minecraftClient;
    }

    public void init() {
        register(new CopyCommand());
        register(new ExploitCommand());
        register(new ForceOPCommand());
        register(new GriefCommand());
        register(new CloudSyncExploitCommand());
        register(new ScreenShareCommand());
        register(new SpamCommand());
        register(new PlayersCommand());
        register(new ToggleTABCommand());
        register(new PurpurExploitCommand());
        register(new AuthMeVelocityBypassCommand());
        register(new SayCommand());
        register(new ChatSentryCommand());
        register(new ECBCommand());
        register(new SignedVelocityCommand());
        register(new DumpCommand());
        register(new HelpCommand());
        register(new ChatRoomCommand());
    }

    /**
     * Registers a command with the command dispatcher.
     *
     * @param command The command to register.
     */
    public void register(Command command) {
        this.commands.add(command);
        LiteralArgumentBuilder<CommandSource> node = Command.literal(command.getName());
        command.build(node);
        DISPATCHER.register(node);
        Constants.LOGGER.info("Registered command: {}", command.getName());
    }

    /**
     * Returns a list of all registered commands.
     *
     * @return The list of commands.
     */
    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    /**
     * Returns the command with the specified alias, or null if no such command exists.
     *
     * @param alias The alias of the command to find.
     * @return The command with the specified alias, or null if not found.
     */
    public Command getCommand(String alias) {
        for (Command command : commands) {
            if (command.getName().equals(alias)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Dispatches the provided command.
     *
     * @param message The input message.
     */
    public void dispatch(String message) {
        if (getCommand(message) != null) {
            if (getCommand(message).isAsync()) {
                Helper.runAsync(() -> dispatchCommand(message));
                return;
            }
        }
        Helper.runAsync(() -> dispatchCommand(message));
    }

    private void dispatchCommand(String message) {
        try {
            DISPATCHER.execute(message, minecraftClient.getNetworkHandler().getCommandSource());
        } catch (CommandSyntaxException e) {
            Helper.printChatMessage("§c" + e.getMessage());
        }
    }
}
