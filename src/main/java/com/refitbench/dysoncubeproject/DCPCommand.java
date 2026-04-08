package com.refitbench.dysoncubeproject;

import com.refitbench.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.refitbench.dysoncubeproject.world.DysonSphereStructure;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DCPCommand extends CommandBase {

    @Override
    public String getName() {
        return "dysoncubeproject";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dysoncubeproject <set|add> <beams|panels> <sphereId> <value>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4; // OP level
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException(getUsage(sender));
        }

        String operation = args[0]; // set or add
        String type = args[1];      // beams or panels
        String sphereId = args[2];
        int value = parseInt(args[3]);

        if (!operation.equals("set") && !operation.equals("add")) {
            throw new WrongUsageException("First argument must be 'set' or 'add'");
        }
        if (!type.equals("beams") && !type.equals("panels")) {
            throw new WrongUsageException("Second argument must be 'beams' or 'panels'");
        }

        World world = server.getWorld(0); // overworld
        DysonSphereProgressSavedData data = DysonSphereProgressSavedData.get(world);
        if (data == null) {
            sender.sendMessage(new TextComponentString("Failed to access Dyson sphere data"));
            return;
        }

        DysonSphereStructure sphere = data.getSpheres().computeIfAbsent(sphereId, s -> new DysonSphereStructure());

        if (type.equals("beams")) {
            int newVal;
            if (operation.equals("set")) {
                newVal = Math.max(0, Math.min(value, sphere.getMaxBeams()));
            } else {
                newVal = Math.max(0, Math.min(sphere.getBeams() + value, sphere.getMaxBeams()));
            }
            sphere.setBeams(newVal);
            data.markDirty();
            sender.sendMessage(new TextComponentString(
                    (operation.equals("set") ? "Set" : "Updated") + " beams for sphere '" + sphereId + "' to " + newVal));
        } else {
            int newVal;
            if (operation.equals("set")) {
                newVal = Math.max(0, Math.min(value, sphere.getMaxSolarPanels()));
            } else {
                newVal = Math.max(0, Math.min(sphere.getSolarPanels() + value, sphere.getMaxSolarPanels()));
            }
            sphere.setSolarPanels(newVal);
            data.markDirty();
            sender.sendMessage(new TextComponentString(
                    (operation.equals("set") ? "Set" : "Updated") + " solar panels for sphere '" + sphereId + "' to " + newVal));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "set", "add");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "beams", "panels");
        }
        if (args.length == 3) {
            World world = server.getWorld(0);
            DysonSphereProgressSavedData data = DysonSphereProgressSavedData.get(world);
            if (data != null) {
                return getListOfStringsMatchingLastWord(args, data.getSpheres().keySet());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("dcp");
    }
}
