/*
 * Copyright (c) 2023, Michael Mirwaldt. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/4.0/">
 * <img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-nd/4.0/88x31.png" />
 * </a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/4.0/">
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License</a>.
 */

package net.mirwaldt.basic.records.design.patterns;

public class SealedTypes_02_Classes {
    sealed class SealedSuperClass {

    }

    final class FinalSealedSubClass extends SealedSuperClass {

    }

    non-sealed class NotSealedAnymoreSubClass extends SealedSuperClass {

    }

    sealed class SealedAgainSubClass extends NotSealedAnymoreSubClass {

    }

    // This final class is only declared to avoid a compiler error with the declaration of the interface SealedAgainSubClass
    final class FinalSealedAgainSubClass extends SealedAgainSubClass {

    }

    sealed class SealedSealedSuperClass extends SealedSuperClass {

    }

    // This final class is only declared to avoid a compiler error with the declaration of the interface SealedSealedSuperClass
    final class SealedSealedSubClass extends SealedSealedSuperClass {

    }
}
