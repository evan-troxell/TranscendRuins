package com.transcendruins.assets.layouts.placement;

import java.awt.Rectangle;
import java.util.List;

public interface PlacementArea {

    public int getWidth();

    public int getLength();

    public List<Rectangle> getMatches(String tag);
}
