package com.transcendruins.assets.interfaces.map;

import javax.swing.ImageIcon;

import com.transcendruins.assets.scripts.TRScript;
import com.transcendruins.resources.styles.Style.IconSize;

public final record LocationRender(TRScript name, TRScript description, ImageIcon icon, ImageIcon pin, IconSize pinSize,
                double x, double y, double height) {

}
