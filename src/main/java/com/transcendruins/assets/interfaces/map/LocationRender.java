package com.transcendruins.assets.interfaces.map;

import javax.swing.ImageIcon;

import com.transcendruins.resources.styles.Style.TextureSize;

public final record LocationRender(String name, String description, ImageIcon icon, ImageIcon pin, TextureSize pinSize,
                double x, double y, double height) {

}
