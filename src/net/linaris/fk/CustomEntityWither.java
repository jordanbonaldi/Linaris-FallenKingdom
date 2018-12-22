package net.linaris.fk;

import net.minecraft.server.v1_7_R4.EntityWither;
import net.minecraft.server.v1_7_R4.World;

public class CustomEntityWither extends EntityWither {

    public CustomEntityWither(World world) {
        super(world);
    }

    @Override
    protected void aD() {
        super.aD();
    }

    @Override
    protected void bn() {}

    @Override
    public void g(double d1, double d2, double d3) {}

    @Override
    public int ca() {
        return 0;
    }
}
