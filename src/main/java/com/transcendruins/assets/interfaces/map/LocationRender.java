package com.transcendruins.assets.interfaces.map;

import javax.swing.ImageIcon;

import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.Style.TextureSize;

public final record LocationRender(TRScript name, TRScript description, ImageIcon icon, ImageIcon pin,
        TextureSize pinSize, double x, double y, double height) {

}
