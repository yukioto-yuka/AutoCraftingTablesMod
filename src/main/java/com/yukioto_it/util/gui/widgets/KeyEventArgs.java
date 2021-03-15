package com.yukioto_it.util.gui.widgets;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KeyEventArgs {
    public int keyCode;
    public char keyChar;
    public long time;

    public boolean passEventToParent = false;

    protected List<Integer> pressingKeys;

    public KeyEventArgs() {
        this.pressingKeys = new ArrayList<>();
    }

    public KeyEventArgs(int keyCode, char keyChar, long time, List<Integer> pressingKeys) {
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.time = time;
        this.pressingKeys = pressingKeys;
    }

    public String KeyName() {
        return Keyboard.getKeyName(keyCode);
    }

    public boolean isKeyDown(int keyCode) {
        return this.pressingKeys.contains(keyCode);
    }

    public boolean Alt() {
        return isKeyDown(Keyboard.KEY_LMENU) || isKeyDown(Keyboard.KEY_RMENU);
    }

    public boolean Control() {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            return isKeyDown(Keyboard.KEY_LMETA) || isKeyDown(Keyboard.KEY_RMETA);
        }
        else {
            return isKeyDown(Keyboard.KEY_LCONTROL) || isKeyDown(Keyboard.KEY_RCONTROL);
        }
    }

    public boolean Shift() {
        return isKeyDown(Keyboard.KEY_LSHIFT) || isKeyDown(Keyboard.KEY_RSHIFT);
    }
}
