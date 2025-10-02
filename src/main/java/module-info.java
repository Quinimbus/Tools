/// This module contains common tool classes used by Quinimbus and applications using Quinimbus.
module cloud.quinimbus.tools {
    exports cloud.quinimbus.tools.lang;
    exports cloud.quinimbus.tools.function;
    exports cloud.quinimbus.tools.stream;
    exports cloud.quinimbus.tools.throwing;

    requires throwing.interfaces;
    requires throwing.streams;
}
