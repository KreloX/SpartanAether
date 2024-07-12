package krelox.spartanaether;

import krelox.spartantoolkit.SpartanMaterial;
import teamrazor.deepaether.datagen.tags.DATags;
import teamrazor.deepaether.init.DATiers;

import static krelox.spartanaether.SpartanAether.*;

class DeepAetherModule {
    static SpartanMaterial skyjade() {
        return material(DATiers.SKYJADE, DATags.Items.SKYJADE_REPAIRING, ETHEREAL);
    }

    static SpartanMaterial stratus() {
        return material(DATiers.STRATUS, DATags.Items.STRATUS_REPAIRING, UPDRAFT);
    }
}
